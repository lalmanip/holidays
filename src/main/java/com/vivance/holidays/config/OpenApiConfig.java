package com.vivance.holidays.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI holidaysOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vivance Holidays API")
                        .description("REST API for Vivance Travel holiday packages (MySQL holidays_* tables)")
                        .version("1.0.0")
                        .contact(new Contact().name("Vivance Travel").url("https://vivance.com")));
    }
}
