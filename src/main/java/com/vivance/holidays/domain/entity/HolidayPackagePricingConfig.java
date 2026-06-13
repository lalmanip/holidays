package com.vivance.holidays.domain.entity;

import com.vivance.holidays.domain.CharColumn;
import com.vivance.holidays.domain.TinyIntBoolean;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "holidays_package_pricing_config")
public class HolidayPackagePricingConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private HolidayTourPackage tourPackage;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @CharColumn
    @Column(nullable = false, length = 3, columnDefinition = "CHAR(3)")
    private String currency;

    @TinyIntBoolean
    @Column(name = "allows_flights", nullable = false)
    private boolean allowsFlights;

    @Column(name = "tour_types", length = 120)
    private String tourTypes;

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public String getCurrency() {
        return currency;
    }

    public boolean isAllowsFlights() {
        return allowsFlights;
    }

    public String getTourTypes() {
        return tourTypes;
    }

    public void setTourPackage(HolidayTourPackage tourPackage) {
        this.tourPackage = tourPackage;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAllowsFlights(boolean allowsFlights) {
        this.allowsFlights = allowsFlights;
    }

    public void setTourTypes(String tourTypes) {
        this.tourTypes = tourTypes;
    }
}
