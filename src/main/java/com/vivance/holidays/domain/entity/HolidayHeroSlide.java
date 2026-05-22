package com.vivance.holidays.domain.entity;

import com.vivance.holidays.domain.TinyIntBoolean;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "holidays_hero_slides")
public class HolidayHeroSlide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 300)
    private String subtitle;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "accent_class", length = 64)
    private String accentClass;

    @Column(name = "object_fit", length = 16)
    private String objectFit;

    @Column(name = "object_position", length = 32)
    private String objectPosition;

    private BigDecimal zoom;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @TinyIntBoolean
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "rotate_ms")
    private Integer rotateMs;

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getObjectFit() {
        return objectFit;
    }

    public String getObjectPosition() {
        return objectPosition;
    }

    public Integer getRotateMs() {
        return rotateMs;
    }
}
