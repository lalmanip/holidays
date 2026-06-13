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
@Table(name = "holidays_package_detail_sections")
public class HolidayPackageDetailSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private HolidayTourPackage tourPackage;

    @Column(name = "section_type", nullable = false, length = 32)
    private String sectionType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    public String getSectionType() {
        return sectionType;
    }

    public String getContent() {
        return content;
    }

    public void setTourPackage(HolidayTourPackage tourPackage) {
        this.tourPackage = tourPackage;
    }

    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
