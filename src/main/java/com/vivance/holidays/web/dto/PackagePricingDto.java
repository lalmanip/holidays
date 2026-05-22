package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Calculate Price tab configuration")
public record PackagePricingDto(
        BigDecimal basePrice,
        String currency,
        boolean allowsFlights,
        List<String> tourTypes,
        List<DepartureCityDto> departureCities
) {}
