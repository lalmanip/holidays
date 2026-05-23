package com.vivance.holidays.service;

import com.vivance.holidays.domain.entity.EntityFactory;
import com.vivance.holidays.domain.entity.HolidayDestination;
import com.vivance.holidays.domain.entity.HolidayTourPackage;
import com.vivance.holidays.domain.repository.HolidayDestinationRepository;
import com.vivance.holidays.web.dto.create.CreateDestinationRequest;
import com.vivance.holidays.web.dto.create.CreateHolidayPackageRequest;
import com.vivance.holidays.web.exception.ConflictException;
import com.vivance.holidays.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
class HolidayPackageDestinationResolver {

    private final HolidayDestinationRepository destinationRepository;

    HolidayPackageDestinationResolver(HolidayDestinationRepository destinationRepository) {
        this.destinationRepository = destinationRepository;
    }

    void validateDestinationInput(CreateHolidayPackageRequest request) {
        boolean hasExistingSlug = StringUtils.hasText(request.existingDestinationSlug());
        if (hasExistingSlug && request.destination() != null) {
            String pathSlug = request.existingDestinationSlug().trim();
            String bodySlug = request.destination().slug().trim();
            if (!pathSlug.equals(bodySlug)) {
                throw new IllegalArgumentException(
                        "existingDestinationSlug must match destination.slug when both are provided");
            }
        }
        if (!hasExistingSlug && request.destination() == null) {
            throw new IllegalArgumentException(
                    "Either existingDestinationSlug or destination is required");
        }
    }

    HolidayDestination resolveForCreate(CreateHolidayPackageRequest request) {
        if (StringUtils.hasText(request.existingDestinationSlug())) {
            return destinationRepository
                    .findBySlug(request.existingDestinationSlug().trim())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Destination not found: " + request.existingDestinationSlug()));
        }

        return findOrCreateDestination(request.destination());
    }

    /**
     * Creates a destination when the slug is new; otherwise reuses the existing row so multiple
     * packages can be added under the same destination from the same create payload shape.
     */
    private HolidayDestination findOrCreateDestination(CreateDestinationRequest destReq) {
        return destinationRepository
                .findBySlug(destReq.slug())
                .map(existing -> updateLinkedDestination(existing, destReq))
                .orElseGet(() -> destinationRepository.save(EntityFactory.newDestination(
                        destReq.slug(),
                        destReq.name(),
                        destReq.regionNormalized(),
                        destReq.description(),
                        destReq.heroImageUrl(),
                        destReq.startingPrice(),
                        destReq.activeOrDefault(),
                        destReq.sortOrderOrDefault())));
    }

  /** Updates linked destination, switches destination, or leaves unchanged when both omitted. */
    HolidayDestination resolveForUpdate(
            CreateHolidayPackageRequest request, HolidayTourPackage existingPackage) {
        if (StringUtils.hasText(request.existingDestinationSlug())) {
            return destinationRepository
                    .findBySlug(request.existingDestinationSlug().trim())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Destination not found: " + request.existingDestinationSlug()));
        }
        if (request.destination() != null) {
            // Resolve by slug so we update/relink the canonical destination row (same as create)
            return findOrCreateDestination(request.destination());
        }
        return existingPackage.getDestination();
    }

    private HolidayDestination updateLinkedDestination(
            HolidayDestination destination, CreateDestinationRequest destReq) {
        if (!destination.getSlug().equals(destReq.slug())
                && destinationRepository.existsBySlug(destReq.slug())) {
            throw new ConflictException("Destination slug already exists: " + destReq.slug());
        }
        destination.setSlug(destReq.slug());
        destination.setName(destReq.name());
        destination.setRegion(destReq.regionNormalized());
        destination.setDescription(destReq.description());
        destination.setHeroImageUrl(destReq.heroImageUrl());
        destination.setStartingPrice(destReq.startingPrice());
        destination.setActive(destReq.activeOrDefault());
        destination.setSortOrder(destReq.sortOrderOrDefault());
        return destinationRepository.save(destination);
    }
}
