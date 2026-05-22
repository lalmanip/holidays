package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Hero banner content for Holidays pages")
public record HeroResponseDto(
        List<HeroSlideDto> slides,
        List<String> tickerItems
) {}
