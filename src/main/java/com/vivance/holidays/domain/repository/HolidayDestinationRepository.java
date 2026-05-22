package com.vivance.holidays.domain.repository;

import com.vivance.holidays.domain.entity.HolidayDestination;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayDestinationRepository extends JpaRepository<HolidayDestination, Long> {

    boolean existsBySlug(String slug);

    Optional<HolidayDestination> findBySlug(String slug);

    Optional<HolidayDestination> findBySlugAndActiveTrue(String slug);

    List<HolidayDestination> findByRegionAndActiveTrueOrderBySortOrderAsc(String region);
}
