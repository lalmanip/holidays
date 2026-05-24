package com.vivance.holidays.service;

import com.vivance.holidays.domain.entity.HolidayDestination;
import com.vivance.holidays.domain.entity.HolidayPackageCategory;
import com.vivance.holidays.domain.entity.HolidayTourPackage;
import com.vivance.holidays.domain.repository.HolidayPackageCategoryRepository;
import com.vivance.holidays.domain.repository.HolidayTourPackageRepository;
import com.vivance.holidays.web.dto.create.CreateHolidayPackageRequest;
import com.vivance.holidays.web.dto.create.CreateHolidayPackageResponse;
import com.vivance.holidays.web.dto.create.CreateTourPackageRequest;
import com.vivance.holidays.web.exception.ConflictException;
import com.vivance.holidays.web.exception.ResourceNotFoundException;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class HolidayPackageUpdateService {

    private final HolidayTourPackageRepository tourPackageRepository;
    private final HolidayPackageCategoryRepository categoryRepository;
    private final HolidayPackageDestinationResolver destinationResolver;
    private final HolidayPackageChildPersistence childPersistence;
    private final HolidayPackageUrlBuilder urlBuilder;

    public HolidayPackageUpdateService(
            HolidayTourPackageRepository tourPackageRepository,
            HolidayPackageCategoryRepository categoryRepository,
            HolidayPackageDestinationResolver destinationResolver,
            HolidayPackageChildPersistence childPersistence,
            HolidayPackageUrlBuilder urlBuilder) {
        this.tourPackageRepository = tourPackageRepository;
        this.categoryRepository = categoryRepository;
        this.destinationResolver = destinationResolver;
        this.childPersistence = childPersistence;
        this.urlBuilder = urlBuilder;
    }

    @Transactional
    public CreateHolidayPackageResponse updatePackage(String pkgId, CreateHolidayPackageRequest request) {
        validateUpdateDestinationInput(request);

        HolidayTourPackage tourPackage = tourPackageRepository
                .findWithDestinationByPkgId(pkgId)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found: " + pkgId));

        CreateTourPackageRequest pkgReq = request.tourPackage();
        if (!pkgId.equals(pkgReq.pkgId())) {
            throw new IllegalArgumentException(
                    "Path pkgId must match tourPackage.pkgId in the request body");
        }

        HolidayDestination destination = destinationResolver.resolveForUpdate(request, tourPackage);
        HolidayPackageCategory category = categoryRepository
                .findByCodeAndActiveTrue(pkgReq.categoryCode())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + pkgReq.categoryCode()));

        if (tourPackageRepository.existsByDestinationIdAndSlugAndIdNot(
                destination.getId(), pkgReq.slug(), tourPackage.getId())) {
            throw new ConflictException(
                    "Package slug already exists for destination: " + destination.getSlug() + "/" + pkgReq.slug());
        }

        tourPackage.setSlug(pkgReq.slug());
        tourPackage.setDestination(destination);
        tourPackage.setCategory(category);
        tourPackage.setTitle(pkgReq.title());
        tourPackage.setImageUrl(pkgReq.imageUrlOrDefault());
        tourPackage.setPrice(pkgReq.price());
        tourPackage.setDays(pkgReq.days());
        tourPackage.setNights(pkgReq.nights());
        tourPackage.setRating(pkgReq.rating());
        tourPackage.setReviewCount(pkgReq.reviewCount());
        tourPackage.setBadge(pkgReq.badge());
        tourPackage.setHasDetailPage(pkgReq.hasDetailPage());
        tourPackage.setSortOrder(pkgReq.sortOrder());
        tourPackage.setActive(pkgReq.activeOrDefault());
        tourPackage = tourPackageRepository.save(tourPackage);

        childPersistence.deleteAllChildren(tourPackage);
        var counts = childPersistence.saveAllChildren(tourPackage, pkgReq);

        String listingUrl = urlBuilder.buildListingUrl(destination.getSlug(), destination.getRegion());
        String detailUrl = pkgReq.hasDetailPage()
                ? urlBuilder.buildDetailUrl(destination.getSlug(), tourPackage.getSlug(), tourPackage.getPkgId())
                : null;

        return new CreateHolidayPackageResponse(
                "Holiday package updated successfully",
                destination.getId(),
                destination.getSlug(),
                destination.getName(),
                tourPackage.getId(),
                tourPackage.getPkgId(),
                tourPackage.getSlug(),
                category.getCode(),
                detailUrl,
                listingUrl,
                counts,
                Instant.now());
    }

    private void validateUpdateDestinationInput(CreateHolidayPackageRequest request) {
        if (StringUtils.hasText(request.existingDestinationSlug()) && request.destination() != null) {
            throw new IllegalArgumentException(
                    "Provide either existingDestinationSlug or destination, not both");
        }
    }
}
