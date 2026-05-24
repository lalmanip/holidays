package com.vivance.holidays.web.dto.create;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Tour package and all child rows")
public record CreateTourPackageRequest(
        @NotBlank @Size(max = 32) String pkgId,
        @NotBlank @Size(max = 160) String slug,
        @NotBlank @Size(max = 64) String categoryCode,
        @NotBlank @Size(max = 200) String title,
        @Size(max = 500) String imageUrl,
        @NotNull @DecimalMin("0") BigDecimal price,
        @Positive int days,
        @Positive int nights,
        @NotNull @DecimalMin("0") BigDecimal rating,
        @NotNull Integer reviewCount,
        @Size(max = 64) String badge,
        @NotNull Boolean hasDetailPage,
        @NotNull Integer sortOrder,
        Boolean active,
        @Valid List<CreateInclusionRequest> inclusions,
        @Valid List<CreateItineraryDayRequest> itinerary,
        @Valid List<CreateDetailSectionRequest> detailSections,
        @Valid List<CreateHotelRequest> hotels,
        @Valid List<CreateTermRequest> terms,
        @Valid CreatePricingRequest pricing
) {
    public boolean activeOrDefault() {
        return active == null || active;
    }

    /** Empty string when omitted (DB column is NOT NULL). */
    public String imageUrlOrDefault() {
        if (imageUrl == null || imageUrl.isBlank()) {
            return "";
        }
        return imageUrl.trim();
    }
}
