package com.vivance.holidays.service;

import com.vivance.holidays.domain.DestinationRegion;
import com.vivance.holidays.domain.entity.HolidayDestination;
import com.vivance.holidays.domain.entity.HolidayPackageCategory;
import com.vivance.holidays.domain.entity.HolidayTourPackage;
import com.vivance.holidays.domain.repository.HolidayDestinationRepository;
import com.vivance.holidays.domain.repository.HolidayPackageCategoryRepository;
import com.vivance.holidays.domain.repository.HolidayTourPackageRepository;
import com.vivance.holidays.web.dto.DestinationHeaderDto;
import com.vivance.holidays.web.dto.DestinationPackagesResponseDto;
import com.vivance.holidays.web.dto.PackageCategoryDto;
import com.vivance.holidays.web.dto.TrendingDestinationDto;
import com.vivance.holidays.web.exception.ResourceNotFoundException;
import com.vivance.holidays.web.mapper.HolidayDtoMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class HolidayDestinationService {

    private final HolidayDestinationRepository destinationRepository;
    private final HolidayPackageCategoryRepository categoryRepository;
    private final HolidayTourPackageRepository packageRepository;
    private final HolidayDtoMapper mapper;

    public HolidayDestinationService(
            HolidayDestinationRepository destinationRepository,
            HolidayPackageCategoryRepository categoryRepository,
            HolidayTourPackageRepository packageRepository,
            HolidayDtoMapper mapper) {
        this.destinationRepository = destinationRepository;
        this.categoryRepository = categoryRepository;
        this.packageRepository = packageRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<TrendingDestinationDto> getTrendingDestinations(String region) {
        return mapTrendingDestinations(region, true);
    }

    @Transactional(readOnly = true)
    public List<TrendingDestinationDto> getTrendingDestinationsForAdmin(String region) {
        return mapTrendingDestinations(region, false);
    }

    private List<TrendingDestinationDto> mapTrendingDestinations(String region, boolean activeOnly) {
        DestinationRegion destinationRegion = DestinationRegion.fromDbValue(region);
        List<HolidayDestination> destinations = activeOnly
                ? destinationRepository.findByRegionAndActiveTrueOrderBySortOrderAsc(
                        destinationRegion.getDbValue())
                : destinationRepository.findByRegionOrderBySortOrderAsc(destinationRegion.getDbValue());
        return destinations.stream().map(mapper::toTrendingDestination).toList();
    }

    @Transactional(readOnly = true)
    public DestinationHeaderDto getDestinationBySlug(String slug) {
        return mapDestinationBySlug(slug, true);
    }

    @Transactional(readOnly = true)
    public DestinationHeaderDto getDestinationBySlugForAdmin(String slug) {
        return mapDestinationBySlug(slug, false);
    }

    private DestinationHeaderDto mapDestinationBySlug(String slug, boolean activeOnly) {
        HolidayDestination destination = (activeOnly
                        ? destinationRepository.findBySlugAndActiveTrue(slug)
                        : destinationRepository.findBySlug(slug))
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: " + slug));
        return mapper.toDestinationHeader(destination);
    }

    @Transactional(readOnly = true)
    public List<PackageCategoryDto> getActiveCategories() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAsc().stream()
                .map(mapper::toCategory)
                .toList();
    }

    @Transactional(readOnly = true)
    public DestinationPackagesResponseDto getDestinationPackages(String slug, String categoryCode) {
        return mapDestinationPackages(slug, categoryCode, true);
    }

    @Transactional(readOnly = true)
    public DestinationPackagesResponseDto getDestinationPackagesForAdmin(String slug, String categoryCode) {
        return mapDestinationPackages(slug, categoryCode, false);
    }

    private DestinationPackagesResponseDto mapDestinationPackages(
            String slug, String categoryCode, boolean activeOnly) {
        if (!StringUtils.hasText(categoryCode)) {
            throw new IllegalArgumentException("Query parameter 'categoryCode' is required");
        }

        HolidayDestination destination = (activeOnly
                        ? destinationRepository.findBySlugAndActiveTrue(slug)
                        : destinationRepository.findBySlug(slug))
                .orElseThrow(() -> new ResourceNotFoundException("Destination not found: " + slug));

        HolidayPackageCategory category = (activeOnly
                        ? categoryRepository.findByCodeAndActiveTrue(categoryCode)
                        : categoryRepository.findByCode(categoryCode))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryCode));

        List<HolidayTourPackage> packages = activeOnly
                ? packageRepository.findByDestinationIdAndCategoryIdAndActiveTrueOrderBySortOrderAsc(
                        destination.getId(), category.getId())
                : packageRepository.findByDestinationIdAndCategoryIdOrderBySortOrderAsc(
                        destination.getId(), category.getId());

        return new DestinationPackagesResponseDto(
                mapper.toDestinationHeader(destination),
                mapper.toCategory(category),
                packages.stream().map(mapper::toPackageCard).toList());
    }
}
