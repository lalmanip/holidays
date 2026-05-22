package com.vivance.holidays.domain.repository;

import com.vivance.holidays.domain.entity.HolidayHeroSlide;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayHeroSlideRepository extends JpaRepository<HolidayHeroSlide, Long> {

    List<HolidayHeroSlide> findByActiveTrueOrderBySortOrderAsc();
}
