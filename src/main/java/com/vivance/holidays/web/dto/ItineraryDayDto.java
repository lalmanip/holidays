package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Single itinerary day")
public record ItineraryDayDto(
        int day,
        String title,
        String description,
        List<String> highlights,
        String meals,
        String accommodation
) {}
