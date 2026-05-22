package com.vivance.holidays.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final HolidaysProperties holidaysProperties;

    public WebConfig(HolidaysProperties holidaysProperties) {
        this.holidaysProperties = holidaysProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = holidaysProperties.getCorsAllowedOrigins().split(",");
        registry.addMapping("/api/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
