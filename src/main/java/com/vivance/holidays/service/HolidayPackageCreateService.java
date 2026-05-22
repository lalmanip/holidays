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
import com.vivance.holidays.web.exception.ConflictException;
import com.vivance.holidays.web.exception.ResourceNotFoundException;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class HolidayPackageCreateService {

    private final HolidayDestinationRepository destinationRepository;
    private final HolidayPackageCategoryRepository categoryRepository;
    private final HolidayTourPackageRepository tourPackageRepository;
    private final HolidayPackageInclusionRepository inclusionRepository;
    private final HolidayPackageItineraryDayRepository itineraryDayRepository;
    private final HolidayPackageItineraryHighlightRepository itineraryHighlightRepository;
    private final HolidayPackageDetailSectionRepository detailSectionRepository;
    private final HolidayPackageHotelRepository hotelRepository;
    private final HolidayPackageTermRepository termRepository;
    private final HolidayPackagePricingConfigRepository pricingConfigRepository;
    private final HolidaysProperties holidaysProperties;

    public HolidayPackageCreateService(
            HolidayDestinationRepository destinationRepository,
            HolidayPackageCategoryRepository categoryRepository,
            HolidayTourPackageRepository tourPackageRepository,
            HolidayPackageInclusionRepository inclusionRepository,
            HolidayPackageItineraryDayRepository itineraryDayRepository,
            HolidayPackageItineraryHighlightRepository itineraryHighlightRepository,
            HolidayPackageDetailSectionRepository detailSectionRepository,
            HolidayPackageHotelRepository hotelRepository,
            HolidayPackageTermRepository termRepository,
            HolidayPackagePricingConfigRepository pricingConfigRepository,
            HolidaysProperties holidaysProperties) {
        this.destinationRepository = destinationRepository;
        this.categoryRepository = categoryRepository;
        this.tourPackageRepository = tourPackageRepository;
        this.inclusionRepository = inclusionRepository;
        this.itineraryDayRepository = itineraryDayRepository;
        this.itineraryHighlightRepository = itineraryHighlightRepository;
        this.detailSectionRepository = detailSectionRepository;
        this.hotelRepository = hotelRepository;
        this.termRepository = termRepository;
        this.pricingConfigRepository = pricingConfigRepository;
        this.holidaysProperties = holidaysProperties;
    }

    @Transactional
    public CreateHolidayPackageResponse createPackage(CreateHolidayPackageRequest request) {
        validateDestinationInput(request);

        HolidayDestination destination = resolveDestination(request);
        CreateTourPackageRequest pkgReq = request.tourPackage();

        if (tourPackageRepository.existsByPkgId(pkgReq.pkgId())) {
            throw new ConflictException("Package pkgId already exists: " + pkgReq.pkgId());
        }
        if (tourPackageRepository.existsByDestinationIdAndSlug(destination.getId(), pkgReq.slug())) {
            throw new ConflictException(
                    "Package slug already exists for destination: " + destination.getSlug() + "/" + pkgReq.slug());
        }

        HolidayPackageCategory category = categoryRepository
                .findByCodeAndActiveTrue(pkgReq.categoryCode())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + pkgReq.categoryCode()));

        HolidayTourPackage tourPackage = EntityFactory.newTourPackage(
                pkgReq.pkgId(),
                pkgReq.slug(),
                destination,
                category,
                pkgReq.title(),
                pkgReq.imageUrl(),
                pkgReq.price(),
                pkgReq.days(),
                pkgReq.nights(),
                pkgReq.rating(),
                pkgReq.reviewCount(),
                pkgReq.badge(),
                pkgReq.hasDetailPage(),
                pkgReq.sortOrder(),
                pkgReq.activeOrDefault());
        tourPackage = tourPackageRepository.save(tourPackage);

        int inclusionCount = saveInclusions(tourPackage, pkgReq.inclusions());
        int[] itineraryCounts = saveItinerary(tourPackage, pkgReq.itinerary());
        int detailSectionCount = saveDetailSections(tourPackage, pkgReq.detailSections());
        int hotelCount = saveHotels(tourPackage, pkgReq.hotels());
        int termCount = saveTerms(tourPackage, pkgReq.terms());
        int pricingCount = savePricing(tourPackage, pkgReq);

        String listingUrl = buildListingUrl(destination.getSlug(), destination.getRegion());
        String detailUrl = pkgReq.hasDetailPage()
                ? buildDetailUrl(destination.getSlug(), tourPackage.getSlug(), tourPackage.getPkgId())
                : null;

        return new CreateHolidayPackageResponse(
                "Holiday package created successfully",
                destination.getId(),
                destination.getSlug(),
                destination.getName(),
                tourPackage.getId(),
                tourPackage.getPkgId(),
                tourPackage.getSlug(),
                category.getCode(),
                detailUrl,
                listingUrl,
                new CreateHolidayPackageResponse.CreatedCounts(
                        inclusionCount,
                        itineraryCounts[0],
                        itineraryCounts[1],
                        detailSectionCount,
                        hotelCount,
                        termCount,
                        pricingCount),
                Instant.now());
    }

    private void validateDestinationInput(CreateHolidayPackageRequest request) {
        boolean hasExisting = StringUtils.hasText(request.existingDestinationSlug());
        if (hasExisting && request.destination() != null) {
            throw new IllegalArgumentException(
                    "Provide either existingDestinationSlug or destination, not both");
        }
        if (!hasExisting && request.destination() == null) {
            throw new IllegalArgumentException(
                    "Either existingDestinationSlug or destination is required");
        }
    }

    private HolidayDestination resolveDestination(CreateHolidayPackageRequest request) {
        if (StringUtils.hasText(request.existingDestinationSlug())) {
            return destinationRepository
                    .findBySlug(request.existingDestinationSlug().trim())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Destination not found: " + request.existingDestinationSlug()));
        }

        CreateDestinationRequest destReq = request.destination();
        if (destinationRepository.existsBySlug(destReq.slug())) {
            throw new ConflictException("Destination slug already exists: " + destReq.slug());
        }

        HolidayDestination destination = EntityFactory.newDestination(
                destReq.slug(),
                destReq.name(),
                destReq.regionNormalized(),
                destReq.description(),
                destReq.heroImageUrl(),
                destReq.startingPrice(),
                destReq.activeOrDefault(),
                destReq.sortOrderOrDefault());
        return destinationRepository.save(destination);
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

    private String buildListingUrl(String destinationSlug, String region) {
        if (!"international".equalsIgnoreCase(region)) {
            return null;
        }
        return holidaysProperties.getInternationalListingPrefix() + destinationSlug;
    }

    private String buildDetailUrl(String destinationSlug, String packageSlug, String pkgId) {
        return holidaysProperties.getInternationalListingPrefix()
                + destinationSlug
                + "/"
                + packageSlug
                + "?pkgId="
                + pkgId;
    }
}
