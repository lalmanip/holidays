package com.vivance.holidays.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "holidays_package_hotels")
public class HolidayPackageHotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private HolidayTourPackage tourPackage;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "nights_label", length = 64)
    private String nightsLabel;

    @Column(name = "meal_plan", length = 120)
    private String mealPlan;

    @Column(name = "tour_type", length = 32)
    private String tourType;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public String getName() {
        return name;
    }

    public String getNightsLabel() {
        return nightsLabel;
    }

    public String getMealPlan() {
        return mealPlan;
    }

    public void setTourPackage(HolidayTourPackage tourPackage) {
        this.tourPackage = tourPackage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNightsLabel(String nightsLabel) {
        this.nightsLabel = nightsLabel;
    }

    public void setMealPlan(String mealPlan) {
        this.mealPlan = mealPlan;
    }

    public void setTourType(String tourType) {
        this.tourType = tourType;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
