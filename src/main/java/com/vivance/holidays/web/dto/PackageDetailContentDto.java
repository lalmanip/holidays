package com.vivance.holidays.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Package Details tab content")
public record PackageDetailContentDto(
        List<String> highlights,
        List<String> inclusions,
        List<String> exclusions,
        List<PackageHotelDto> hotels,
        String flightsNote,
        String visaNote
) {}
