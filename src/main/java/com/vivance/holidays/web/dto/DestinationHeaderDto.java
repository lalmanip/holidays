package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Destination listing page header")
public record DestinationHeaderDto(
        Long id,
        String slug,
        String name,
        String description,
        String heroImageUrl,
        BigDecimal startingPrice,
        @Schema(description = "Whether the destination is published (is_active)")
        boolean active
) {}
