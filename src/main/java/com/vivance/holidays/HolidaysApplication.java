package com.vivance.holidays;

import com.vivance.holidays.config.HolidaysProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(HolidaysProperties.class)
public class HolidaysApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolidaysApplication.class, args);
    }
}
