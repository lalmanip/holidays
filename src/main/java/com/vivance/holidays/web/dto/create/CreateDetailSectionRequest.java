package com.vivance.holidays.web.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDetailSectionRequest(
        @NotBlank @Size(max = 32) String sectionType,
        @NotBlank String content,
        @NotNull Integer sortOrder
) {
    @Schema(hidden = true)
    public String sectionTypeNormalized() {
        return sectionType.trim();
    }
}
