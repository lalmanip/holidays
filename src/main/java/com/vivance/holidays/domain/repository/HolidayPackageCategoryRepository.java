package com.vivance.holidays.domain.repository;

import com.vivance.holidays.domain.entity.HolidayPackageCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayPackageCategoryRepository extends JpaRepository<HolidayPackageCategory, Long> {

    List<HolidayPackageCategory> findByActiveTrueOrderBySortOrderAsc();

    Optional<HolidayPackageCategory> findByCodeAndActiveTrue(String code);

    Optional<HolidayPackageCategory> findByCode(String code);
}
