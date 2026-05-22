package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Hero carousel slide")
public record HeroSlideDto(
        String title,
        String subtitle,
        String imageUrl,
        @Schema(description = "CSS object-fit", allowableValues = {"cover", "contain"})
        String objectFit,
        String objectPosition,
        Integer rotateMs
) {}
