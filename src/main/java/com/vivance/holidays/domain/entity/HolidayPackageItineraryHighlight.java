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
@Table(name = "holidays_package_itinerary_highlights")
public class HolidayPackageItineraryHighlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "itinerary_day_id", nullable = false)
    private HolidayPackageItineraryDay itineraryDay;

    @Column(nullable = false, length = 200)
    private String highlight;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public String getHighlight() {
        return highlight;
    }
}
