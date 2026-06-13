package com.vivance.holidays.domain.repository;

import com.vivance.holidays.domain.entity.HolidayTourPackage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HolidayTourPackageRepository extends JpaRepository<HolidayTourPackage, Long> {

    boolean existsByPkgId(String pkgId);

    boolean existsByDestinationIdAndSlug(Long destinationId, String slug);

    Optional<HolidayTourPackage> findByPkgId(String pkgId);

    boolean existsByDestinationIdAndSlugAndIdNot(Long destinationId, String slug, Long id);

    @EntityGraph(attributePaths = {"destination", "category"})
    Optional<HolidayTourPackage> findWithDestinationByPkgId(String pkgId);

    @EntityGraph(attributePaths = {"destination", "category", "inclusions"})
    List<HolidayTourPackage> findByDestinationIdAndCategoryIdAndActiveTrueOrderBySortOrderAsc(
            Long destinationId, Long categoryId);

    @EntityGraph(attributePaths = {"destination", "category", "inclusions"})
    List<HolidayTourPackage> findByDestinationIdAndCategoryIdOrderBySortOrderAsc(
            Long destinationId, Long categoryId);

    /**
     * Fetches only to-one associations. Collections are loaded via @BatchSize when accessed
     * (avoids MultipleBagFetchException from joining multiple List bags in one query).
     */
    @EntityGraph(attributePaths = {"destination", "category", "pricingConfig"})
    @Query("SELECT p FROM HolidayTourPackage p WHERE p.pkgId = :pkgId AND p.active = true")
    Optional<HolidayTourPackage> findActiveDetailByPkgId(@Param("pkgId") String pkgId);
}
