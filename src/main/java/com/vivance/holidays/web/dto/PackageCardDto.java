package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Tour package listing card")
public record PackageCardDto(
        String pkgId,
        String slug,
        String title,
        String imageUrl,
        BigDecimal price,
        int days,
        int nights,
        BigDecimal rating,
        int reviewCount,
        String badge,
        List<String> inclusions,
        boolean hasDetailPage,
        @Schema(description = "Detail page URL when hasDetailPage is true")
        String detailUrl,
        @Schema(description = "Whether the package is published (is_active)")
        boolean active
) {}
