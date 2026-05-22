package com.vivance.holidays.web.controller;

import com.vivance.holidays.service.HolidayPackageCreateService;
import com.vivance.holidays.web.dto.create.CreateHolidayPackageRequest;
import com.vivance.holidays.web.dto.create.CreateHolidayPackageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Holidays Admin", description = "Backoffice APIs to create holiday packages")
@RestController
@RequestMapping("/api/v1/holidays/admin")
public class HolidayAdminController {

    private final HolidayPackageCreateService createService;

    public HolidayAdminController(HolidayPackageCreateService createService) {
        this.createService = createService;
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
}
