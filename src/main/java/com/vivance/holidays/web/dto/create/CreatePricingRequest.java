package com.vivance.holidays.web.dto.create;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

public record CreatePricingRequest(
        @NotNull @DecimalMin("0") BigDecimal basePrice,
        @NotBlank @Size(min = 3, max = 3) String currency,
        Boolean allowsFlights,
        List<@NotBlank String> tourTypes
) {
    public boolean allowsFlightsOrDefault() {
        return allowsFlights == null || allowsFlights;
    }
}
