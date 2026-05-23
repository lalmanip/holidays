package com.vivance.holidays.web.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Create destination + tour package with all related rows in one transaction")
public record CreateHolidayPackageRequest(
        @Schema(
                description =
                        "Optional. Links package to an existing destination by slug. "
                                + "If omitted, destination.slug is used: existing slug reuses that destination, new slug creates one.",
                example = "mauritius-tour-packages")
        @Size(max = 120)
        String existingDestinationSlug,
        @Valid CreateDestinationRequest destination,
        @NotNull @Valid CreateTourPackageRequest tourPackage
) {}
