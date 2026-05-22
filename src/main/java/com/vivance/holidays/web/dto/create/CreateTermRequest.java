package com.vivance.holidays.web.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTermRequest(
        @NotBlank String termText,
        @NotNull Integer sortOrder
) {}
