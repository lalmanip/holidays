package com.vivance.holidays.web.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "New destination (or omit when using existingDestinationSlug)")
public record CreateDestinationRequest(
        @NotBlank @Size(max = 120) String slug,
        @NotBlank @Size(max = 120) String name,
        @NotBlank
        @Pattern(regexp = "^(?i)(international|india)$", message = "region must be international or india")
        String region,
        String description,
        @Size(max = 500) String heroImageUrl,
        @NotNull @DecimalMin("0") BigDecimal startingPrice,
        @Schema(defaultValue = "true") Boolean active,
        @Schema(defaultValue = "0") Integer sortOrder
) {
    public boolean activeOrDefault() {
        return active == null || active;
    }

    public int sortOrderOrDefault() {
        return sortOrder != null ? sortOrder : 0;
    }

    public String regionNormalized() {
        return region.toLowerCase();
    }
}
