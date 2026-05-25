package com.vivance.holidays.web.mapper;

import com.vivance.holidays.config.HolidaysProperties;
import com.vivance.holidays.domain.DestinationRegion;
import com.vivance.holidays.domain.entity.HolidayDepartureCity;
import com.vivance.holidays.domain.entity.HolidayDestination;
import com.vivance.holidays.domain.entity.HolidayHeroSlide;
import com.vivance.holidays.domain.entity.HolidayPackageCategory;
import com.vivance.holidays.domain.entity.HolidayPackageDetailSection;
import com.vivance.holidays.domain.entity.HolidayPackageHotel;
import com.vivance.holidays.domain.entity.HolidayPackageItineraryDay;
import com.vivance.holidays.domain.entity.HolidayPackageItineraryHighlight;
import com.vivance.holidays.domain.entity.HolidayPackageInclusion;
import com.vivance.holidays.domain.entity.HolidayPackagePricingConfig;
import com.vivance.holidays.domain.entity.HolidayPackageTerm;
import com.vivance.holidays.domain.entity.HolidayTourPackage;
import com.vivance.holidays.web.dto.DepartureCityDto;
import com.vivance.holidays.web.dto.DestinationHeaderDto;
import com.vivance.holidays.web.dto.HeroSlideDto;
import com.vivance.holidays.web.dto.ItineraryDayDto;
import com.vivance.holidays.web.dto.PackageCardDto;
import com.vivance.holidays.web.dto.PackageCategoryDto;
import com.vivance.holidays.web.dto.PackageDetailContentDto;
import com.vivance.holidays.web.dto.PackageHotelDto;
import com.vivance.holidays.web.dto.PackagePricingDto;
import com.vivance.holidays.web.dto.TourPackageDetailDto;
import com.vivance.holidays.web.dto.TrendingDestinationDto;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class HolidayDtoMapper {

    private final HolidaysProperties holidaysProperties;

    public HolidayDtoMapper(HolidaysProperties holidaysProperties) {
        this.holidaysProperties = holidaysProperties;
    }

    public HeroSlideDto toHeroSlide(HolidayHeroSlide slide) {
        String objectFit = normalizeObjectFit(slide.getObjectFit());
        return new HeroSlideDto(
                slide.getTitle(),
                slide.getSubtitle(),
                slide.getImageUrl(),
                objectFit,
                slide.getObjectPosition(),
                slide.getRotateMs() != null ? slide.getRotateMs() : 5000
        );
    }

    public TrendingDestinationDto toTrendingDestination(HolidayDestination destination) {
        String listingPath = null;
        if (DestinationRegion.INTERNATIONAL.getDbValue().equals(destination.getRegion())) {
            listingPath = holidaysProperties.getInternationalListingPrefix() + destination.getSlug();
        }
        return new TrendingDestinationDto(
                destination.getId(),
                destination.getSlug(),
                destination.getName(),
                destination.getHeroImageUrl(),
                destination.getStartingPrice(),
                listingPath,
                destination.isActive()
        );
    }

    public DestinationHeaderDto toDestinationHeader(HolidayDestination destination) {
        return new DestinationHeaderDto(
                destination.getId(),
                destination.getSlug(),
                destination.getName(),
                destination.getDescription(),
                destination.getHeroImageUrl(),
                destination.getStartingPrice(),
                destination.isActive()
        );
    }

    public PackageCategoryDto toCategory(HolidayPackageCategory category) {
        return new PackageCategoryDto(category.getCode(), category.getLabel(), category.getIconKey());
    }

    public PackageCardDto toPackageCard(HolidayTourPackage pkg) {
        List<String> inclusions = pkg.getInclusions().stream()
                .map(HolidayPackageInclusion::getLabel)
                .collect(Collectors.toList());
        String detailUrl = null;
        if (pkg.isHasDetailPage()) {
            detailUrl = buildDetailUrl(pkg);
        }
        return new PackageCardDto(
                pkg.getPkgId(),
                pkg.getSlug(),
                pkg.getTitle(),
                pkg.getImageUrl(),
                pkg.getPrice(),
                pkg.getDays(),
                pkg.getNights(),
                pkg.getRating(),
                pkg.getReviewCount(),
                pkg.getBadge(),
                inclusions,
                pkg.isHasDetailPage(),
                detailUrl,
                pkg.isActive()
        );
    }

    public TourPackageDetailDto toPackageDetail(
            HolidayTourPackage pkg,
            List<HolidayDepartureCity> departureCities) {
        HolidayDestination destination = pkg.getDestination();
        List<String> cardInclusions = pkg.getInclusions().stream()
                .map(HolidayPackageInclusion::getLabel)
                .collect(Collectors.toList());

        List<ItineraryDayDto> itinerary = pkg.getItineraryDays().stream()
                .map(this::toItineraryDay)
                .collect(Collectors.toList());

        PackageDetailContentDto details = buildDetailContent(pkg);
        List<String> terms = pkg.getTerms().stream()
                .map(HolidayPackageTerm::getTermText)
                .collect(Collectors.toList());

        PackagePricingDto pricing = buildPricing(pkg, departureCities);

        return new TourPackageDetailDto(
                pkg.getPkgId(),
                pkg.getSlug(),
                destination.getSlug(),
                destination.getName(),
                pkg.getTitle(),
                pkg.getImageUrl(),
                pkg.getPrice(),
                pkg.getDays(),
                pkg.getNights(),
                pkg.getRating(),
                pkg.getReviewCount(),
                pkg.getBadge(),
                cardInclusions,
                itinerary,
                details,
                terms,
                pricing
        );
    }

    private ItineraryDayDto toItineraryDay(HolidayPackageItineraryDay day) {
        List<String> highlights = day.getHighlights().stream()
                .map(HolidayPackageItineraryHighlight::getHighlight)
                .collect(Collectors.toList());
        return new ItineraryDayDto(
                day.getDayNumber(),
                day.getTitle(),
                day.getDescription() != null ? day.getDescription() : "",
                highlights,
                day.getMeals() != null ? day.getMeals() : "",
                day.getAccommodation() != null ? day.getAccommodation() : ""
        );
    }

    private PackageDetailContentDto buildDetailContent(HolidayTourPackage pkg) {
        List<String> highlights = new ArrayList<>();
        List<String> inclusions = new ArrayList<>();
        List<String> exclusions = new ArrayList<>();
        String flightsNote = "";
        String visaNote = "";

        for (HolidayPackageDetailSection section : pkg.getDetailSections()) {
            String type = section.getSectionType() != null
                    ? section.getSectionType().trim().toLowerCase()
                    : "";
            switch (type) {
                case "highlights" -> highlights.add(section.getContent());
                case "inclusions" -> inclusions.add(section.getContent());
                case "exclusions" -> exclusions.add(section.getContent());
                case "flights_note" -> flightsNote = section.getContent();
                case "visa_note" -> visaNote = section.getContent();
                default -> { }
            }
        }

        List<PackageHotelDto> hotels = pkg.getHotels().stream()
                .map(this::toHotel)
                .collect(Collectors.toList());

        return new PackageDetailContentDto(
                highlights,
                inclusions,
                exclusions,
                hotels,
                flightsNote,
                visaNote
        );
    }

    private PackageHotelDto toHotel(HolidayPackageHotel hotel) {
        return new PackageHotelDto(
                hotel.getName(),
                hotel.getNightsLabel() != null ? hotel.getNightsLabel() : "",
                hotel.getMealPlan() != null ? hotel.getMealPlan() : ""
        );
    }

    private PackagePricingDto buildPricing(
            HolidayTourPackage pkg,
            List<HolidayDepartureCity> departureCities) {
        HolidayPackagePricingConfig config = pkg.getPricingConfig();
        BigDecimal basePrice = config != null ? config.getBasePrice() : pkg.getPrice();
        String currency = config != null ? config.getCurrency() : "INR";
        boolean allowsFlights = config == null || config.isAllowsFlights();
        List<String> tourTypes = config != null && StringUtils.hasText(config.getTourTypes())
                ? Arrays.stream(config.getTourTypes().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList())
                : Collections.emptyList();

        List<DepartureCityDto> cities = departureCities.stream()
                .map(c -> new DepartureCityDto(c.getCode(), c.getName()))
                .collect(Collectors.toList());

        return new PackagePricingDto(basePrice, currency, allowsFlights, tourTypes, cities);
    }

    private String buildDetailUrl(HolidayTourPackage pkg) {
        String destinationSlug = pkg.getDestination().getSlug();
        return holidaysProperties.getInternationalListingPrefix()
                + destinationSlug + "/"
                + pkg.getSlug()
                + "?pkgId="
                + pkg.getPkgId();
    }

    private String normalizeObjectFit(String objectFit) {
        if ("contain".equalsIgnoreCase(objectFit)) {
            return "contain";
        }
        return "cover";
    }
}
