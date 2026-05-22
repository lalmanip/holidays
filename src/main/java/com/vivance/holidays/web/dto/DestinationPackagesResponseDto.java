package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Packages for a destination and category")
public record DestinationPackagesResponseDto(
        DestinationHeaderDto destination,
        PackageCategoryDto category,
        List<PackageCardDto> packages
) {}
