package com.vivance.holidays.web.controller;

import com.vivance.holidays.service.HolidayDestinationService;
import com.vivance.holidays.service.HolidayHeroService;
import com.vivance.holidays.service.HolidayPackageService;
import com.vivance.holidays.web.dto.DestinationHeaderDto;
import com.vivance.holidays.web.dto.DestinationPackagesResponseDto;
import com.vivance.holidays.web.dto.HeroResponseDto;
import com.vivance.holidays.web.dto.PackageCategoryDto;
import com.vivance.holidays.web.dto.TourPackageDetailDto;
import com.vivance.holidays.web.dto.TrendingDestinationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Holidays", description = "Vivance Travel holiday packages API")
@RestController
@RequestMapping("/api/v1/holidays")
@Validated
public class HolidayApiController {

    private final HolidayHeroService heroService;
    private final HolidayDestinationService destinationService;
    private final HolidayPackageService packageService;

    public HolidayApiController(
            HolidayHeroService heroService,
            HolidayDestinationService destinationService,
            HolidayPackageService packageService) {
        this.heroService = heroService;
        this.destinationService = destinationService;
        this.packageService = packageService;
    }

    @Operation(summary = "Hero banner", description = "Slides and ticker for HolidayHeroBanner")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = HeroResponseDto.class)))
    @GetMapping(value = "/hero", produces = MediaType.APPLICATION_JSON_VALUE)
    public HeroResponseDto getHero() {
        return heroService.getHero();
    }

    @Operation(
            summary = "Trending destinations",
            description = "International or India trending tiles (active destinations only)")
    @GetMapping(value = "/destinations/trending", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TrendingDestinationDto> getTrendingDestinations(
            @Parameter(description = "international or india", required = true)
            @RequestParam
            @NotBlank
            @Pattern(regexp = "^(?i)(international|india)$", message = "region must be 'international' or 'india'")
            String region) {
        return destinationService.getTrendingDestinations(region.toLowerCase());
    }

    @Operation(summary = "Destination header", description = "Listing page hero for an active destination slug")
    @ApiResponse(responseCode = "404", description = "Destination not found or inactive")
    @GetMapping(value = "/destinations/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DestinationHeaderDto getDestination(
            @Parameter(description = "URL slug, e.g. mauritius-tour-packages")
            @PathVariable String slug) {
        return destinationService.getDestinationBySlug(slug);
    }

    @Operation(summary = "Package categories", description = "Active category icons for listing page")
    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PackageCategoryDto> getCategories() {
        return destinationService.getActiveCategories();
    }

    @Operation(summary = "Destination packages by category", description = "Active package cards for a destination and category")
    @ApiResponse(responseCode = "404", description = "Destination or category not found")
    @GetMapping(value = "/destinations/{slug}/packages", produces = MediaType.APPLICATION_JSON_VALUE)
    public DestinationPackagesResponseDto getDestinationPackages(
            @PathVariable String slug,
            @Parameter(description = "Category code, e.g. best-seller", required = true)
            @RequestParam @NotBlank String categoryCode) {
        return destinationService.getDestinationPackages(slug, categoryCode);
    }

    @Operation(summary = "Package detail", description = "Full detail for InternationalPackageDetailPage")
    @ApiResponse(responseCode = "404", description = "Package not found or inactive")
    @GetMapping(value = "/packages/{pkgId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TourPackageDetailDto getPackageDetail(
            @Parameter(description = "Business package id, e.g. PKG-MRU-CLASSIC-001")
            @PathVariable String pkgId) {
        return packageService.getPackageDetail(pkgId);
    }
}
