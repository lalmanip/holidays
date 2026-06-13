package com.vivance.holidays.service;

import com.vivance.holidays.config.HolidayMediaProperties;
import com.vivance.holidays.web.dto.HolidayMediaUploadResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class HolidayMediaService {

    private static final String MEDIA_API_PREFIX = "/api/v1/holidays/media/";
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final Set<String> ALLOWED_KINDS = Set.of("destination-hero", "package");

    private final HolidayMediaProperties properties;

    public HolidayMediaService(HolidayMediaProperties properties) {
        this.properties = properties;
    }

    public HolidayMediaUploadResponse upload(String kind, MultipartFile file) throws IOException {
        if (!StringUtils.hasText(kind) || !ALLOWED_KINDS.contains(kind.trim().toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("kind must be destination-hero or package");
        }
        validateFile(file);

        String folder =
                "destination-hero".equals(kind.trim().toLowerCase(Locale.ROOT))
                        ? "destinations"
                        : "packages";
        String ext = resolveExtension(file);
        String storedFileName =
                ("destination-hero".equals(kind.trim().toLowerCase(Locale.ROOT)) ? "hero_" : "pkg_")
                        + System.currentTimeMillis()
                        + "_"
                        + UUID.randomUUID().toString().substring(0, 8)
                        + "."
                        + ext;
        String relativePath = folder + "/" + storedFileName;

        Path root = storageRoot();
        Path targetDir = root.resolve(folder).normalize();
        if (!targetDir.startsWith(root)) {
            throw new IllegalArgumentException("Invalid storage path");
        }
        Files.createDirectories(targetDir);

        Path targetFile = targetDir.resolve(storedFileName).normalize();
        if (!targetFile.startsWith(targetDir)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);

        String url = buildPublicUrl(relativePath);
        return new HolidayMediaUploadResponse(relativePath, url);
    }

    public Resource resolveMediaResource(String relativePath) throws IOException {
        if (!StringUtils.hasText(relativePath) || relativePath.contains("..")) {
            throw new IllegalArgumentException("Invalid media path");
        }
        Path root = storageRoot();
        Path file = root.resolve(relativePath).normalize();
        if (!file.startsWith(root) || !Files.isRegularFile(file)) {
            throw new IllegalArgumentException("Media file not found");
        }
        return new FileSystemResource(file);
    }

    public MediaType mediaTypeForFileName(String fileName) {
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        }
        if (lower.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }
        if (lower.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        }
        return MediaType.IMAGE_JPEG;
    }

    public String buildPublicUrl(String relativePath) {
        String base = properties.getPublicBaseUrl().replaceAll("/+$", "");
        String path = relativePath.replace("\\", "/").replaceAll("^/+", "");
        return base + MEDIA_API_PREFIX + path;
    }

    private Path storageRoot() throws IOException {
        Path root = Paths.get(properties.getStorageDir()).toAbsolutePath().normalize();
        Files.createDirectories(root);
        return root;
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (file.getSize() > properties.getMaxFileSizeBytes()) {
            throw new IllegalArgumentException(
                    "File exceeds maximum size of "
                            + (properties.getMaxFileSizeBytes() / (1024 * 1024))
                            + " MB");
        }
        resolveExtension(file);
    }

    private static String resolveExtension(MultipartFile file) {
        String original = file.getOriginalFilename();
        String ext = "";
        if (StringUtils.hasText(original) && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        }
        if (!StringUtils.hasText(ext)) {
            String contentType =
                    file.getContentType() != null ? file.getContentType().toLowerCase(Locale.ROOT) : "";
            ext =
                    switch (contentType) {
                        case "image/png" -> "png";
                        case "image/gif" -> "gif";
                        case "image/webp" -> "webp";
                        default -> "jpg";
                    };
        }
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("File type not allowed. Use JPG, PNG, WEBP, or GIF");
        }
        return ext;
    }
}
