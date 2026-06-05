package com.vivance.holidays.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vivance.holidays.media")
public class HolidayMediaProperties {

    /** Root directory for uploaded images (created if missing). */
    private String storageDir = "./data/holiday-media";

    /** Max upload size in bytes (default 5 MB). */
    private long maxFileSizeBytes = 5L * 1024 * 1024;

    /**
     * Public origin used in returned URLs, e.g. http://localhost:8095 or https://next.vivancetravels.com.
     * No trailing slash.
     */
    private String publicBaseUrl = "http://localhost:8095";

    public String getStorageDir() {
        return storageDir;
    }

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
    }

    public long getMaxFileSizeBytes() {
        return maxFileSizeBytes;
    }

    public void setMaxFileSizeBytes(long maxFileSizeBytes) {
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    public String getPublicBaseUrl() {
        return publicBaseUrl;
    }

    public void setPublicBaseUrl(String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl;
    }
}
