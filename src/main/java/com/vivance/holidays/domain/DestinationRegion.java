package com.vivance.holidays.domain;

public enum DestinationRegion {
    INTERNATIONAL("international"),
    INDIA("india");

    private final String dbValue;

    DestinationRegion(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static DestinationRegion fromDbValue(String value) {
        for (DestinationRegion region : values()) {
            if (region.dbValue.equalsIgnoreCase(value)) {
                return region;
            }
        }
        throw new IllegalArgumentException("Unknown region: " + value);
    }
}
