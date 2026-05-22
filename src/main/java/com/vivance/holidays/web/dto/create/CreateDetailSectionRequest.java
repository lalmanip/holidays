package com.vivance.holidays.web.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateDetailSectionRequest(
        @NotBlank
        @Pattern(
                regexp = "^(?i)(highlights|inclusions|exclusions|flights_note|visa_note)$",
                message = "sectionType must be highlights, inclusions, exclusions, flights_note, or visa_note")
        String sectionType,
        @NotBlank String content,
        @NotNull Integer sortOrder
) {
    @Schema(hidden = true)
    public String sectionTypeNormalized() {
        return sectionType.toLowerCase();
    }
}
