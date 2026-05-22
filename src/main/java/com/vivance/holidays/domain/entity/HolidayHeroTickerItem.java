package com.vivance.holidays.domain.entity;

import com.vivance.holidays.domain.TinyIntBoolean;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "holidays_hero_ticker_items")
public class HolidayHeroTickerItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String text;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @TinyIntBoolean
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public String getText() {
        return text;
    }
}
