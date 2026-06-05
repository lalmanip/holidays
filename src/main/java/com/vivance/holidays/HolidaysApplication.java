package com.vivance.holidays;

import com.vivance.holidays.config.HolidayMediaProperties;
import com.vivance.holidays.config.HolidaysProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({HolidaysProperties.class, HolidayMediaProperties.class})
public class HolidaysApplication {

    public static void main(String[] args) {
        SpringApplication.run(HolidaysApplication.class, args);
         System.out.println("====== Holidays Application Started =======");
    }
}
