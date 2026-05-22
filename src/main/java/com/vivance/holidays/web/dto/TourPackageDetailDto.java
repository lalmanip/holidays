package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Full tour package detail (aligns with vivance-ui TourPackageDetail + pricing)")
public record TourPackageDetailDto(
        String pkgId,
        String slug,
        String destinationSlug,
        String destinationName,
        String title,
        String image,
        BigDecimal price,
        int days,
        int nights,
        BigDecimal rating,
        @Schema(description = "Review count (frontend field: comments)")
        int comments,
        String badge,
        List<String> inclusions,
        List<ItineraryDayDto> itinerary,
        PackageDetailContentDto details,
        List<String> terms,
        PackagePricingDto pricing
) {}
