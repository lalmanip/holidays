package com.vivance.holidays.domain.repository;

import com.vivance.holidays.domain.entity.HolidayHeroTickerItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayHeroTickerItemRepository extends JpaRepository<HolidayHeroTickerItem, Long> {

    List<HolidayHeroTickerItem> findByActiveTrueOrderBySortOrderAsc();
}
