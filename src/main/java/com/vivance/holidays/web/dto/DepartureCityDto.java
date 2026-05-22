package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Departure city for Calculate Price")
public record DepartureCityDto(
        String code,
        String name
) {}
