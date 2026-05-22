package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Package category tab (listing page icon row)")
public record PackageCategoryDto(
        String code,
        String label,
        String iconKey
) {}
