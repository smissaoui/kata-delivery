package com.carrefour.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "com.carrefour")
@EnableCaching
public class DeliveryKataApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryKataApplication.class, args);
    }
}
