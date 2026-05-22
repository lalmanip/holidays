package com.vivance.holidays.web.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateItineraryDayRequest(
        @Positive int dayNumber,
        @NotBlank @Size(max = 200) String title,
        String description,
        @Size(max = 120) String meals,
        @Size(max = 200) String accommodation,
        @NotNull Integer sortOrder,
        @Valid
        @Schema(description = "Highlights for this day (linked after day is saved)")
        List<CreateItineraryHighlightRequest> highlights
) {}
