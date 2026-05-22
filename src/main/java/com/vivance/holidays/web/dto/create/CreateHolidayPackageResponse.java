package com.vivance.holidays.web.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Created holiday package summary")
public record CreateHolidayPackageResponse(
        String message,
        Long destinationId,
        String destinationSlug,
        String destinationName,
        Long packageInternalId,
        String pkgId,
        String packageSlug,
        String categoryCode,
        String detailUrl,
        String listingUrl,
        CreatedCounts counts,
        Instant createdAt
) {
    @Schema(description = "Number of child rows inserted")
    public record CreatedCounts(
            int inclusions,
            int itineraryDays,
            int itineraryHighlights,
            int detailSections,
            int hotels,
            int terms,
            int pricingConfig
    ) {}
}
