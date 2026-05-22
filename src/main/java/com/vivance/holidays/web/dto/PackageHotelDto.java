package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Hotel row on package details tab")
public record PackageHotelDto(
        String name,
        String nights,
        String mealPlan
) {}
