package com.vivance.holidays.service;

import com.vivance.holidays.domain.repository.HolidayHeroSlideRepository;
import com.vivance.holidays.domain.repository.HolidayHeroTickerItemRepository;
import com.vivance.holidays.web.dto.HeroResponseDto;
import com.vivance.holidays.web.dto.HeroSlideDto;
import com.vivance.holidays.web.mapper.HolidayDtoMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HolidayHeroService {

    private final HolidayHeroSlideRepository slideRepository;
    private final HolidayHeroTickerItemRepository tickerRepository;
    private final HolidayDtoMapper mapper;

    public HolidayHeroService(
            HolidayHeroSlideRepository slideRepository,
            HolidayHeroTickerItemRepository tickerRepository,
            HolidayDtoMapper mapper) {
        this.slideRepository = slideRepository;
        this.tickerRepository = tickerRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public HeroResponseDto getHero() {
        List<HeroSlideDto> slides = slideRepository.findByActiveTrueOrderBySortOrderAsc().stream()
                .map(mapper::toHeroSlide)
                .toList();
        List<String> tickerItems = tickerRepository.findByActiveTrueOrderBySortOrderAsc().stream()
                .map(item -> item.getText())
                .toList();
        return new HeroResponseDto(slides, tickerItems);
    }
}
