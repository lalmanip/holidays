package com.vivance.holidays.domain.entity;

import java.math.BigDecimal;
import java.util.List;

/** Builds new entity graphs for package creation (IDs assigned on persist). */
public final class EntityFactory {

    private EntityFactory() {}

    public static HolidayDestination newDestination(
            String slug,
            String name,
            String region,
            String description,
            String heroImageUrl,
            BigDecimal startingPrice,
            boolean active,
            int sortOrder) {
        HolidayDestination entity = new HolidayDestination();
        entity.setSlug(slug);
        entity.setName(name);
        entity.setRegion(region);
        entity.setDescription(description);
        entity.setHeroImageUrl(heroImageUrl);
        entity.setStartingPrice(startingPrice);
        entity.setActive(active);
        entity.setSortOrder(sortOrder);
        return entity;
    }

    public static HolidayTourPackage newTourPackage(
            String pkgId,
            String slug,
            HolidayDestination destination,
            HolidayPackageCategory category,
            String title,
            String imageUrl,
            BigDecimal price,
            int days,
            int nights,
            BigDecimal rating,
            int reviewCount,
            String badge,
            boolean hasDetailPage,
            int sortOrder,
            boolean active) {
        HolidayTourPackage entity = new HolidayTourPackage();
        entity.setPkgId(pkgId);
        entity.setSlug(slug);
        entity.setDestination(destination);
        entity.setCategory(category);
        entity.setTitle(title);
        entity.setImageUrl(imageUrl);
        entity.setPrice(price);
        entity.setDays(days);
        entity.setNights(nights);
        entity.setRating(rating);
        entity.setReviewCount(reviewCount);
        entity.setBadge(badge);
        entity.setHasDetailPage(hasDetailPage);
        entity.setSortOrder(sortOrder);
        entity.setActive(active);
        return entity;
    }

    public static HolidayPackageInclusion newInclusion(
            HolidayTourPackage tourPackage, String label, int sortOrder) {
        HolidayPackageInclusion entity = new HolidayPackageInclusion();
        entity.setTourPackage(tourPackage);
        entity.setLabel(label);
        entity.setSortOrder(sortOrder);
        return entity;
    }

    public static HolidayPackageItineraryDay newItineraryDay(
            HolidayTourPackage tourPackage,
            int dayNumber,
            String title,
            String description,
            String meals,
            String accommodation,
            int sortOrder) {
        HolidayPackageItineraryDay entity = new HolidayPackageItineraryDay();
        entity.setTourPackage(tourPackage);
        entity.setDayNumber(dayNumber);
        entity.setTitle(title);
        entity.setDescription(description);
        entity.setMeals(meals);
        entity.setAccommodation(accommodation);
        entity.setSortOrder(sortOrder);
        return entity;
    }

    public static HolidayPackageItineraryHighlight newItineraryHighlight(
            HolidayPackageItineraryDay day, String highlight, int sortOrder) {
        HolidayPackageItineraryHighlight entity = new HolidayPackageItineraryHighlight();
        entity.setItineraryDay(day);
        entity.setHighlight(highlight);
        entity.setSortOrder(sortOrder);
        return entity;
    }

    public static HolidayPackageDetailSection newDetailSection(
            HolidayTourPackage tourPackage, String sectionType, String content, int sortOrder) {
        HolidayPackageDetailSection entity = new HolidayPackageDetailSection();
        entity.setTourPackage(tourPackage);
        entity.setSectionType(sectionType);
        entity.setContent(content);
        entity.setSortOrder(sortOrder);
        return entity;
    }

    public static HolidayPackageHotel newHotel(
            HolidayTourPackage tourPackage,
            String name,
            String nightsLabel,
            String mealPlan,
            String tourType,
            int sortOrder) {
        HolidayPackageHotel entity = new HolidayPackageHotel();
        entity.setTourPackage(tourPackage);
        entity.setName(name);
        entity.setNightsLabel(nightsLabel);
        entity.setMealPlan(mealPlan);
        entity.setTourType(tourType);
        entity.setSortOrder(sortOrder);
        return entity;
    }

    public static HolidayPackageTerm newTerm(HolidayTourPackage tourPackage, String termText, int sortOrder) {
        HolidayPackageTerm entity = new HolidayPackageTerm();
        entity.setTourPackage(tourPackage);
        entity.setTermText(termText);
        entity.setSortOrder(sortOrder);
        return entity;
    }

    public static HolidayPackagePricingConfig newPricingConfig(
            HolidayTourPackage tourPackage,
            BigDecimal basePrice,
            String currency,
            boolean allowsFlights,
            String tourTypes) {
        HolidayPackagePricingConfig entity = new HolidayPackagePricingConfig();
        entity.setTourPackage(tourPackage);
        entity.setBasePrice(basePrice);
        entity.setCurrency(currency);
        entity.setAllowsFlights(allowsFlights);
        entity.setTourTypes(tourTypes);
        return entity;
    }

    public static String joinTourTypes(List<String> tourTypes) {
        if (tourTypes == null || tourTypes.isEmpty()) {
            return null;
        }
        return String.join(",", tourTypes);
    }
}
