package com.vivance.holidays.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vivance.holidays")
public class HolidaysProperties {

    private String corsAllowedOrigins = "http://localhost:3000";
    private String internationalListingPrefix = "/international-tour-packages/";

    public String getCorsAllowedOrigins() {
        return corsAllowedOrigins;
    }

    public void setCorsAllowedOrigins(String corsAllowedOrigins) {
        this.corsAllowedOrigins = corsAllowedOrigins;
    }

    public String getInternationalListingPrefix() {
        return internationalListingPrefix;
    }

    public void setInternationalListingPrefix(String internationalListingPrefix) {
        this.internationalListingPrefix = internationalListingPrefix;
    }
}
