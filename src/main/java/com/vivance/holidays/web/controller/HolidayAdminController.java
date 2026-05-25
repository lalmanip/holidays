package com.vivance.holidays.web.controller;

import com.vivance.holidays.service.HolidayDestinationService;
import com.vivance.holidays.service.HolidayPackageCreateService;
import com.vivance.holidays.service.HolidayPackageUpdateService;
import com.vivance.holidays.web.dto.DestinationHeaderDto;
import com.vivance.holidays.web.dto.DestinationPackagesResponseDto;
import com.vivance.holidays.web.dto.TrendingDestinationDto;
import com.vivance.holidays.web.dto.create.CreateHolidayPackageRequest;
import com.vivance.holidays.web.dto.create.CreateHolidayPackageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Holidays Admin", description = "Backoffice APIs to create and update holiday packages")
@RestController
@RequestMapping("/api/v1/holidays/admin")
@Validated
public class HolidayAdminController {

    private final HolidayPackageCreateService createService;
    private final HolidayPackageUpdateService updateService;
    private final HolidayDestinationService destinationService;

    public HolidayAdminController(
            HolidayPackageCreateService createService,
            HolidayPackageUpdateService updateService,
            HolidayDestinationService destinationService) {
        this.createService = createService;
        this.updateService = updateService;
        this.destinationService = destinationService;
    }

    @Operation(
            summary = "Trending destinations (admin)",
            description = "International or India trending tiles including active and inactive destinations")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TrendingDestinationDto.class)))
    @GetMapping(value = "/destinations/trending", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TrendingDestinationDto> getTrendingDestinations(
            @Parameter(description = "international or india", required = true)
            @RequestParam
            @NotBlank
            @Pattern(regexp = "^(?i)(international|india)$", message = "region must be 'international' or 'india'")
            String region) {
        return destinationService.getTrendingDestinationsForAdmin(region.toLowerCase());
    }

    @Operation(
            summary = "Destination header (admin)",
            description = "Listing page hero for a destination slug, including inactive destinations")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = DestinationHeaderDto.class)))
    @ApiResponse(responseCode = "404", description = "Destination not found")
    @GetMapping(value = "/destinations/{slug}", produces = MediaType.APPLICATION_JSON_VALUE)
    public DestinationHeaderDto getDestination(
            @Parameter(description = "URL slug, e.g. new-jersey-tour-packages")
            @PathVariable
            String slug) {
        return destinationService.getDestinationBySlugForAdmin(slug);
    }

    @Operation(
            summary = "Destination packages by category (admin)",
            description = "Package cards for a destination and category, including active and inactive packages")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = DestinationPackagesResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Destination or category not found")
    @GetMapping(value = "/destinations/{slug}/packages", produces = MediaType.APPLICATION_JSON_VALUE)
    public DestinationPackagesResponseDto getDestinationPackages(
            @Parameter(description = "URL slug, e.g. new-jersey-tour-packages")
            @PathVariable
            String slug,
            @Parameter(description = "Category code, e.g. best-seller", required = true)
            @RequestParam
            @NotBlank
            String categoryCode) {
        return destinationService.getDestinationPackagesForAdmin(slug, categoryCode);
    }

    @Operation(
            summary = "Create holiday package",
            description =
                    "Inserts destination (optional), tour package, inclusions, itinerary, detail sections, hotels, terms, and pricing in one transaction. Primary keys are auto-generated.")
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = CreateHolidayPackageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Category or existing destination not found")
    @ApiResponse(responseCode = "409", description = "Duplicate slug or pkgId")
    @PostMapping(value = "/packages", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CreateHolidayPackageResponse createHolidayPackage(
            @Valid @RequestBody CreateHolidayPackageRequest request) {
        return createService.createPackage(request);
    }

    @Operation(
            summary = "Update holiday package",
            description =
                    "Replaces tour package fields and all child rows for the given pkgId. "
                            + "Uses the same JSON body as create. Path pkgId must match tourPackage.pkgId. "
                            + "Omit destination blocks to keep the current destination; send destination to update it in place, "
                            + "or existingDestinationSlug to re-link.")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = CreateHolidayPackageResponse.class)))
    @ApiResponse(responseCode = "400", description = "Validation error or pkgId mismatch")
    @ApiResponse(responseCode = "404", description = "Package, category, or destination not found")
    @ApiResponse(responseCode = "409", description = "Duplicate destination or package slug")
    @PutMapping(value = "/packages/{pkgId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateHolidayPackageResponse updateHolidayPackage(
            @Parameter(description = "Business package id, e.g. PKG-GAYA-CLASSIC-001")
            @PathVariable
            String pkgId,
            @Valid @RequestBody CreateHolidayPackageRequest request) {
        return updateService.updatePackage(pkgId, request);
    }
}
