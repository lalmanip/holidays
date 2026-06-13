package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Trending destination tile")
public record TrendingDestinationDto(
        Long id,
        String slug,
        String name,
        String imageUrl,
        @Schema(description = "Starting price in INR")
        java.math.BigDecimal startingPrice,
        @Schema(description = "Listing page path; null for India tiles without routes yet")
        String listingPath,
        @Schema(description = "Whether the destination is published (is_active)")
        boolean active
) {}
