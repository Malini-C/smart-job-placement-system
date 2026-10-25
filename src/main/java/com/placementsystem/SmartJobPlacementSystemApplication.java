package com.placementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync        // Enable async operations
public class SmartJobPlacementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartJobPlacementSystemApplication.class, args);
        System.out.println("🚀 Smart Job Placement System Started Successfully!");
        System.out.println("📝 Visit: http://localhost:8080");
        System.out.println("🔧 API Base URL: http://localhost:8080/api");
    }
}