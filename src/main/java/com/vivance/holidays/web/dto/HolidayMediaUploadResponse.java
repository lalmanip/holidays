package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Uploaded holiday image metadata")
public record HolidayMediaUploadResponse(
        @Schema(description = "Relative path under storage root, e.g. destinations/hero_123.jpg")
                String storedPath,
        @Schema(
                        description =
                                "Public URL for vivance_ui and package JSON (heroImageUrl / imageUrl)")
                String url) {}
