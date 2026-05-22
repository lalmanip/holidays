package com.vivance.holidays.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * Logs incoming API request/response details to the console for {@code /api/v1/holidays/**}.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class ApiLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiLoggingFilter.class);
    private static final String API_PREFIX = "/api/v1/holidays";
    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization", "cookie", "set-cookie", "x-api-key", "proxy-authorization");

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri == null || !uri.startsWith(API_PREFIX);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startedAt = System.currentTimeMillis();
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long durationMs = System.currentTimeMillis() - startedAt;
            logRequest(wrappedRequest);
            logResponse(wrappedRequest, wrappedResponse, durationMs);
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        StringBuilder sb = new StringBuilder(512);
        sb.append("\n========== Holidays API REQUEST ==========\n");
        sb.append("Method     : ").append(request.getMethod()).append('\n');
        sb.append("URI        : ").append(request.getRequestURI()).append('\n');
        if (request.getQueryString() != null) {
            sb.append("Query      : ").append(request.getQueryString()).append('\n');
        }
        sb.append("Params     : ").append(formatParams(request)).append('\n');
        sb.append("Headers    : ").append(formatHeaders(request)).append('\n');
        sb.append("Body       : ").append(readRequestBody(request)).append('\n');
        sb.append("==========================================");
        log.info(sb.toString());
    }

    private void logResponse(
            ContentCachingRequestWrapper request,
            ContentCachingResponseWrapper response,
            long durationMs) {
        StringBuilder sb = new StringBuilder(512);
        sb.append("\n========== Holidays API RESPONSE ==========\n");
        sb.append("Method     : ").append(request.getMethod()).append('\n');
        sb.append("URI        : ").append(request.getRequestURI()).append('\n');
        sb.append("Status     : ").append(response.getStatus()).append('\n');
        sb.append("Duration   : ").append(durationMs).append(" ms\n");
        sb.append("Headers    : ").append(formatResponseHeaders(response)).append('\n');
        sb.append("Body       : ").append(readResponseBody(response)).append('\n');
        sb.append("===========================================");
        log.info(sb.toString());
    }

    private Map<String, String> formatParams(HttpServletRequest request) {
        Map<String, String> params = new LinkedHashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values == null || values.length == 0) {
                params.put(key, "");
            } else if (values.length == 1) {
                params.put(key, values[0]);
            } else {
                params.put(key, String.join(",", values));
            }
        });
        return params.isEmpty() ? Map.of() : params;
    }

    private Map<String, String> formatHeaders(HttpServletRequest request) {
        Map<String, String> headers = new LinkedHashMap<>();
        Enumeration<String> names = request.getHeaderNames();
        while (names != null && names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(name, maskIfSensitive(name, request.getHeader(name)));
        }
        return headers;
    }

    private Map<String, String> formatResponseHeaders(ContentCachingResponseWrapper response) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (String name : response.getHeaderNames()) {
            headers.put(name, maskIfSensitive(name, response.getHeader(name)));
        }
        return headers;
    }

    private String maskIfSensitive(String headerName, String value) {
        if (headerName == null) {
            return value;
        }
        if (SENSITIVE_HEADERS.contains(headerName.toLowerCase())) {
            return "***";
        }
        return value;
    }

    private String readRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return "(empty)";
        }
        if (!isLoggableContentType(request.getContentType())) {
            return "(binary or omitted, content-type=" + request.getContentType() + ")";
        }
        return new String(content, resolveCharset(request.getCharacterEncoding()));
    }

    private String readResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length == 0) {
            return "(empty)";
        }
        if (!isLoggableContentType(response.getContentType())) {
            return "(binary or omitted, content-type=" + response.getContentType() + ")";
        }
        return new String(content, resolveCharset(response.getCharacterEncoding()));
    }

    private boolean isLoggableContentType(String contentType) {
        if (contentType == null) {
            return true;
        }
        String lower = contentType.toLowerCase();
        return lower.contains(MediaType.APPLICATION_JSON_VALUE)
                || lower.contains(MediaType.APPLICATION_XML_VALUE)
                || lower.contains(MediaType.TEXT_PLAIN_VALUE)
                || lower.contains(MediaType.TEXT_HTML_VALUE)
                || lower.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                || lower.startsWith("text/");
    }

    private Charset resolveCharset(String encoding) {
        if (encoding == null) {
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(encoding);
        } catch (Exception ex) {
            return StandardCharsets.UTF_8;
        }
    }
}
