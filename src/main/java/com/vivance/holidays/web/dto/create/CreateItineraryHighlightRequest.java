package com.vivance.holidays.web.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateItineraryHighlightRequest(
        @NotBlank @Size(max = 200) String highlight,
        @NotNull Integer sortOrder
) {}
