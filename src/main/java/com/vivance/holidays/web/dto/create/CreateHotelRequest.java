package com.vivance.holidays.web.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateHotelRequest(
        @NotBlank @Size(max = 200) String name,
        @Size(max = 64) String nights,
        @Size(max = 120) String mealPlan,
        @Size(max = 32) String tourType,
        @NotNull Integer sortOrder
) {}
