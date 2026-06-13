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
@Table(name = "holidays_package_inclusions")
public class HolidayPackageInclusion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private HolidayTourPackage tourPackage;

    @Column(nullable = false, length = 64)
    private String label;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public String getLabel() {
        return label;
    }

    public void setTourPackage(HolidayTourPackage tourPackage) {
        this.tourPackage = tourPackage;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
