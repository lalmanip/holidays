package com.vivance.holidays.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "holidays_seasons")
public class HolidaySeason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 16)
    private String code;

    @Column(nullable = false, length = 32)
    private String label;

    @Column(columnDefinition = "TEXT")
    private String headline;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "background_url", length = 500)
    private String backgroundUrl;

    @OneToMany(mappedBy = "season")
    @OrderBy("sortOrder ASC")
    private List<HolidaySeasonPackage> packages = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getHeadline() {
        return headline;
    }

    public String getDescription() {
        return description;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public List<HolidaySeasonPackage> getPackages() {
        return packages;
    }
}
