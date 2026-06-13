package com.vivance.holidays.domain.entity;

import jakarta.persistence.Column;
import org.hibernate.annotations.BatchSize;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "holidays_package_itinerary_days")
public class HolidayPackageItineraryDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private HolidayTourPackage tourPackage;

    @Column(name = "day_number", nullable = false)
    private int dayNumber;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 120)
    private String meals;

    @Column(length = 200)
    private String accommodation;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @OneToMany(mappedBy = "itineraryDay")
    @OrderBy("sortOrder ASC")
    @BatchSize(size = 32)
    private List<HolidayPackageItineraryHighlight> highlights = new ArrayList<>();

    public int getDayNumber() {
        return dayNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getMeals() {
        return meals;
    }

    public String getAccommodation() {
        return accommodation;
    }

    public List<HolidayPackageItineraryHighlight> getHighlights() {
        return highlights;
    }

    public Long getId() {
        return id;
    }

    public void setTourPackage(HolidayTourPackage tourPackage) {
        this.tourPackage = tourPackage;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMeals(String meals) {
        this.meals = meals;
    }

    public void setAccommodation(String accommodation) {
        this.accommodation = accommodation;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
