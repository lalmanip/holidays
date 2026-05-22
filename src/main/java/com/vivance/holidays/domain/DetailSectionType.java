package com.vivance.holidays.domain;

public enum DetailSectionType {
    HIGHLIGHTS("highlights"),
    INCLUSIONS("inclusions"),
    EXCLUSIONS("exclusions"),
    FLIGHTS_NOTE("flights_note"),
    VISA_NOTE("visa_note");

    private final String dbValue;

    DetailSectionType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static DetailSectionType fromDbValue(String value) {
        for (DetailSectionType type : values()) {
            if (type.dbValue.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown section type: " + value);
    }
}
