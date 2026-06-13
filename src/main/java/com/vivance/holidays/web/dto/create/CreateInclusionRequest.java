package com.vivance.holidays.web.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateInclusionRequest(
        @NotBlank @Size(max = 64) String label,
        @NotNull Integer sortOrder
) {}
