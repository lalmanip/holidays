package com.vivance.holidays.service;

import com.vivance.holidays.config.HolidaysProperties;
import com.vivance.holidays.domain.entity.EntityFactory;
import com.vivance.holidays.domain.entity.HolidayDestination;
import com.vivance.holidays.domain.entity.HolidayPackageCategory;
import com.vivance.holidays.domain.entity.HolidayPackageItineraryDay;
import com.vivance.holidays.domain.entity.HolidayTourPackage;
import com.vivance.holidays.domain.repository.HolidayDestinationRepository;
import com.vivance.holidays.domain.repository.HolidayPackageCategoryRepository;
import com.vivance.holidays.domain.repository.HolidayPackageDetailSectionRepository;
import com.vivance.holidays.domain.repository.HolidayPackageHotelRepository;
import com.vivance.holidays.domain.repository.HolidayPackageInclusionRepository;
import com.vivance.holidays.domain.repository.HolidayPackageItineraryDayRepository;
import com.vivance.holidays.domain.repository.HolidayPackageItineraryHighlightRepository;
import com.vivance.holidays.domain.repository.HolidayPackagePricingConfigRepository;
import com.vivance.holidays.domain.repository.HolidayPackageTermRepository;
import com.vivance.holidays.domain.repository.HolidayTourPackageRepository;
import com.vivance.holidays.web.dto.create.CreateDetailSectionRequest;
import com.vivance.holidays.web.dto.create.CreateDestinationRequest;
import com.vivance.holidays.web.dto.create.CreateHolidayPackageRequest;
import com.vivance.holidays.web.dto.create.CreateHolidayPackageResponse;
import com.vivance.holidays.web.dto.create.CreateHotelRequest;
import com.vivance.holidays.web.dto.create.CreateInclusionRequest;
import com.vivance.holidays.web.dto.create.CreateItineraryDayRequest;
import com.vivance.holidays.web.dto.create.CreateItineraryHighlightRequest;
import com.vivance.holidays.web.dto.create.CreateTermRequest;
import com.vivance.holidays.web.dto.create.CreateTourPackageRequest;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class HolidayPackageChildPersistence {

    private final HolidayPackageInclusionRepository inclusionRepository;
    private final HolidayPackageItineraryDayRepository itineraryDayRepository;
    private final HolidayPackageItineraryHighlightRepository itineraryHighlightRepository;
    private final HolidayPackageDetailSectionRepository detailSectionRepository;
    private final HolidayPackageHotelRepository hotelRepository;
    private final HolidayPackageTermRepository termRepository;
    private final HolidayPackagePricingConfigRepository pricingConfigRepository;
    private final EntityManager entityManager;

    HolidayPackageChildPersistence(
            HolidayPackageInclusionRepository inclusionRepository,
            HolidayPackageItineraryDayRepository itineraryDayRepository,
            HolidayPackageItineraryHighlightRepository itineraryHighlightRepository,
            HolidayPackageDetailSectionRepository detailSectionRepository,
            HolidayPackageHotelRepository hotelRepository,
            HolidayPackageTermRepository termRepository,
            HolidayPackagePricingConfigRepository pricingConfigRepository,
            EntityManager entityManager) {
        this.inclusionRepository = inclusionRepository;
        this.itineraryDayRepository = itineraryDayRepository;
        this.itineraryHighlightRepository = itineraryHighlightRepository;
        this.detailSectionRepository = detailSectionRepository;
        this.hotelRepository = hotelRepository;
        this.termRepository = termRepository;
        this.pricingConfigRepository = pricingConfigRepository;
        this.entityManager = entityManager;
    }

    void deleteAllChildren(HolidayTourPackage tourPackage) {
        Long packageId = tourPackage.getId();
        inclusionRepository.deleteByTourPackage_Id(packageId);
        itineraryDayRepository.deleteByTourPackage_Id(packageId);
        detailSectionRepository.deleteByTourPackage_Id(packageId);
        hotelRepository.deleteByTourPackage_Id(packageId);
        termRepository.deleteByTourPackage_Id(packageId);
        pricingConfigRepository.deleteByTourPackage_Id(packageId);
        // Ensure deletes hit the DB before re-inserting (avoids uk_holidays_itinerary_package_day violations)
        entityManager.flush();
    }

    CreateHolidayPackageResponse.CreatedCounts saveAllChildren(
            HolidayTourPackage tourPackage, CreateTourPackageRequest pkgReq) {
        int inclusionCount = saveInclusions(tourPackage, pkgReq.inclusions());
        int[] itineraryCounts = saveItinerary(tourPackage, pkgReq.itinerary());
        int detailSectionCount = saveDetailSections(tourPackage, pkgReq.detailSections());
        int hotelCount = saveHotels(tourPackage, pkgReq.hotels());
        int termCount = saveTerms(tourPackage, pkgReq.terms());
        int pricingCount = savePricing(tourPackage, pkgReq);
        return new CreateHolidayPackageResponse.CreatedCounts(
                inclusionCount,
                itineraryCounts[0],
                itineraryCounts[1],
                detailSectionCount,
                hotelCount,
                termCount,
                pricingCount);
    }

    private int saveInclusions(HolidayTourPackage tourPackage, List<CreateInclusionRequest> inclusions) {
        if (inclusions == null) {
            return 0;
        }
        for (CreateInclusionRequest item : inclusions) {
            inclusionRepository.save(EntityFactory.newInclusion(tourPackage, item.label(), item.sortOrder()));
        }
        return inclusions.size();
    }

    private int[] saveItinerary(HolidayTourPackage tourPackage, List<CreateItineraryDayRequest> days) {
        if (days == null) {
            return new int[] {0, 0};
        }
        int highlightCount = 0;
        for (CreateItineraryDayRequest dayReq : days) {
            HolidayPackageItineraryDay day = EntityFactory.newItineraryDay(
                    tourPackage,
                    dayReq.dayNumber(),
                    dayReq.title(),
                    dayReq.description(),
                    dayReq.meals(),
                    dayReq.accommodation(),
                    dayReq.sortOrder());
            day = itineraryDayRepository.save(day);
            if (dayReq.highlights() != null) {
                for (CreateItineraryHighlightRequest highlightReq : dayReq.highlights()) {
                    itineraryHighlightRepository.save(
                            EntityFactory.newItineraryHighlight(
                                    day, highlightReq.highlight(), highlightReq.sortOrder()));
                    highlightCount++;
                }
            }
        }
        return new int[] {days.size(), highlightCount};
    }

    private int saveDetailSections(
            HolidayTourPackage tourPackage, List<CreateDetailSectionRequest> sections) {
        if (sections == null) {
            return 0;
        }
        for (CreateDetailSectionRequest section : sections) {
            detailSectionRepository.save(EntityFactory.newDetailSection(
                    tourPackage,
                    section.sectionTypeNormalized(),
                    section.content(),
                    section.sortOrder()));
        }
        return sections.size();
    }

    private int saveHotels(HolidayTourPackage tourPackage, List<CreateHotelRequest> hotels) {
        if (hotels == null) {
            return 0;
        }
        for (CreateHotelRequest hotel : hotels) {
            hotelRepository.save(EntityFactory.newHotel(
                    tourPackage,
                    hotel.name(),
                    hotel.nights(),
                    hotel.mealPlan(),
                    hotel.tourType(),
                    hotel.sortOrder()));
        }
        return hotels.size();
    }

    private int saveTerms(HolidayTourPackage tourPackage, List<CreateTermRequest> terms) {
        if (terms == null) {
            return 0;
        }
        for (CreateTermRequest term : terms) {
            termRepository.save(EntityFactory.newTerm(tourPackage, term.termText(), term.sortOrder()));
        }
        return terms.size();
    }

    private int savePricing(HolidayTourPackage tourPackage, CreateTourPackageRequest pkgReq) {
        if (pkgReq.pricing() == null) {
            return 0;
        }
        var pricing = pkgReq.pricing();
        pricingConfigRepository.save(EntityFactory.newPricingConfig(
                tourPackage,
                pricing.basePrice(),
                pricing.currency().toUpperCase(),
                pricing.allowsFlightsOrDefault(),
                EntityFactory.joinTourTypes(pricing.tourTypes())));
        return 1;
    }
}

@Component
class HolidayPackageUrlBuilder {

    private final HolidaysProperties holidaysProperties;

    HolidayPackageUrlBuilder(HolidaysProperties holidaysProperties) {
        this.holidaysProperties = holidaysProperties;
    }

    String buildListingUrl(String destinationSlug, String region) {
        if (!"international".equalsIgnoreCase(region)) {
            return null;
        }
        return holidaysProperties.getInternationalListingPrefix() + destinationSlug;
    }

    String buildDetailUrl(String destinationSlug, String packageSlug, String pkgId) {
        return holidaysProperties.getInternationalListingPrefix()
                + destinationSlug
                + "/"
                + packageSlug
                + "?pkgId="
                + pkgId;
    }
}
