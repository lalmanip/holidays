package com.vivance.holidays.service;

import com.vivance.holidays.domain.entity.HolidayDepartureCity;
import com.vivance.holidays.domain.entity.HolidayTourPackage;
import com.vivance.holidays.domain.repository.HolidayDepartureCityRepository;
import com.vivance.holidays.domain.repository.HolidayTourPackageRepository;
import com.vivance.holidays.web.dto.TourPackageDetailDto;
import com.vivance.holidays.web.exception.ResourceNotFoundException;
import com.vivance.holidays.web.mapper.HolidayDtoMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HolidayPackageService {

    private final HolidayTourPackageRepository packageRepository;
    private final HolidayDepartureCityRepository departureCityRepository;
    private final HolidayDtoMapper mapper;

    public HolidayPackageService(
            HolidayTourPackageRepository packageRepository,
            HolidayDepartureCityRepository departureCityRepository,
            HolidayDtoMapper mapper) {
        this.packageRepository = packageRepository;
        this.departureCityRepository = departureCityRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public TourPackageDetailDto getPackageDetail(String pkgId) {
        HolidayTourPackage pkg = packageRepository.findActiveDetailByPkgId(pkgId)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found: " + pkgId));

        initializeDetailCollections(pkg);

        List<HolidayDepartureCity> departureCities =
                departureCityRepository.findByActiveTrueOrderByIdAsc();

        return mapper.toPackageDetail(pkg, departureCities);
    }

    /** Touch lazy collections inside the transaction (batched SELECT IN queries). */
    private void initializeDetailCollections(HolidayTourPackage pkg) {
        pkg.getInclusions().size();
        pkg.getItineraryDays().forEach(day -> day.getHighlights().size());
        pkg.getDetailSections().size();
        pkg.getHotels().size();
        pkg.getTerms().size();
    }
}
