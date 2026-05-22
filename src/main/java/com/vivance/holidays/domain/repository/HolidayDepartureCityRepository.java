package com.vivance.holidays.domain.repository;

import com.vivance.holidays.domain.entity.HolidayDepartureCity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayDepartureCityRepository extends JpaRepository<HolidayDepartureCity, Long> {

    List<HolidayDepartureCity> findByActiveTrueOrderByIdAsc();
}
