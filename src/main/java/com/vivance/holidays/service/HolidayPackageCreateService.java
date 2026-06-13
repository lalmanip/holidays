package com.vivance.holidays.service;

import com.vivance.holidays.domain.entity.EntityFactory;
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

@Service
public class HolidayPackageCreateService {

    private final HolidayTourPackageRepository tourPackageRepository;
    private final HolidayPackageCategoryRepository categoryRepository;
    private final HolidayPackageDestinationResolver destinationResolver;
    private final HolidayPackageChildPersistence childPersistence;
    private final HolidayPackageUrlBuilder urlBuilder;

    public HolidayPackageCreateService(
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
    public CreateHolidayPackageResponse createPackage(CreateHolidayPackageRequest request) {
        destinationResolver.validateDestinationInput(request);

        HolidayDestination destination = destinationResolver.resolveForCreate(request);
        CreateTourPackageRequest pkgReq = request.tourPackage();

        if (tourPackageRepository.existsByPkgId(pkgReq.pkgId())) {
            throw new ConflictException("Package pkgId already exists: " + pkgReq.pkgId());
        }
        if (tourPackageRepository.existsByDestinationIdAndSlug(destination.getId(), pkgReq.slug())) {
            throw new ConflictException(
                    "Package slug already exists for destination: " + destination.getSlug() + "/" + pkgReq.slug());
        }

        HolidayPackageCategory category = loadCategory(pkgReq.categoryCode());

        HolidayTourPackage tourPackage = EntityFactory.newTourPackage(
                pkgReq.pkgId(),
                pkgReq.slug(),
                destination,
                category,
                pkgReq.title(),
                pkgReq.imageUrlOrDefault(),
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

        var counts = childPersistence.saveAllChildren(tourPackage, pkgReq);
        return buildResponse("Holiday package created successfully", destination, tourPackage, category, pkgReq, counts);
    }

    private HolidayPackageCategory loadCategory(String categoryCode) {
        return categoryRepository
                .findByCodeAndActiveTrue(categoryCode)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryCode));
    }

    private CreateHolidayPackageResponse buildResponse(
            String message,
            HolidayDestination destination,
            HolidayTourPackage tourPackage,
            HolidayPackageCategory category,
            CreateTourPackageRequest pkgReq,
            CreateHolidayPackageResponse.CreatedCounts counts) {
        String listingUrl = urlBuilder.buildListingUrl(destination.getSlug(), destination.getRegion());
        String detailUrl = pkgReq.hasDetailPage()
                ? urlBuilder.buildDetailUrl(destination.getSlug(), tourPackage.getSlug(), tourPackage.getPkgId())
                : null;

        return new CreateHolidayPackageResponse(
                message,
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
}
