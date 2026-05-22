package com.vivance.holidays.domain.entity;

import com.vivance.holidays.domain.TinyIntBoolean;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "holidays_tour_packages")
@BatchSize(size = 32)
public class HolidayTourPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pkg_id", nullable = false, length = 32)
    private String pkgId;

    @Column(nullable = false, length = 160)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_id", nullable = false)
    private HolidayDestination destination;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private HolidayPackageCategory category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int days;

    @Column(nullable = false)
    private int nights;

    @Column(nullable = false, precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(length = 64)
    private String badge;

    @TinyIntBoolean
    @Column(name = "has_detail_page", nullable = false)
    private boolean hasDetailPage;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @TinyIntBoolean
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "tourPackage")
    @OrderBy("sortOrder ASC")
    @BatchSize(size = 32)
    private List<HolidayPackageInclusion> inclusions = new ArrayList<>();

    @OneToMany(mappedBy = "tourPackage")
    @OrderBy("sortOrder ASC, dayNumber ASC")
    @BatchSize(size = 32)
    private List<HolidayPackageItineraryDay> itineraryDays = new ArrayList<>();

    @OneToMany(mappedBy = "tourPackage")
    @OrderBy("sortOrder ASC")
    @BatchSize(size = 32)
    private List<HolidayPackageDetailSection> detailSections = new ArrayList<>();

    @OneToMany(mappedBy = "tourPackage")
    @OrderBy("sortOrder ASC")
    @BatchSize(size = 32)
    private List<HolidayPackageHotel> hotels = new ArrayList<>();

    @OneToMany(mappedBy = "tourPackage")
    @OrderBy("sortOrder ASC")
    @BatchSize(size = 32)
    private List<HolidayPackageTerm> terms = new ArrayList<>();

    @OneToOne(mappedBy = "tourPackage", fetch = FetchType.LAZY)
    private HolidayPackagePricingConfig pricingConfig;

    public Long getId() {
        return id;
    }

    public String getPkgId() {
        return pkgId;
    }

    public String getSlug() {
        return slug;
    }

    public HolidayDestination getDestination() {
        return destination;
    }

    public HolidayPackageCategory getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getDays() {
        return days;
    }

    public int getNights() {
        return nights;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public String getBadge() {
        return badge;
    }

    public boolean isHasDetailPage() {
        return hasDetailPage;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isActive() {
        return active;
    }

    public List<HolidayPackageInclusion> getInclusions() {
        return inclusions;
    }

    public List<HolidayPackageItineraryDay> getItineraryDays() {
        return itineraryDays;
    }

    public List<HolidayPackageDetailSection> getDetailSections() {
        return detailSections;
    }

    public List<HolidayPackageHotel> getHotels() {
        return hotels;
    }

    public List<HolidayPackageTerm> getTerms() {
        return terms;
    }

    public HolidayPackagePricingConfig getPricingConfig() {
        return pricingConfig;
    }

    public void setPkgId(String pkgId) {
        this.pkgId = pkgId;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setDestination(HolidayDestination destination) {
        this.destination = destination;
    }

    public void setCategory(HolidayPackageCategory category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void setNights(int nights) {
        this.nights = nights;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public void setHasDetailPage(boolean hasDetailPage) {
        this.hasDetailPage = hasDetailPage;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
