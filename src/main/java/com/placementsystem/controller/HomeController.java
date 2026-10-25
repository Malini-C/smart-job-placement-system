package com.placementsystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Smart Job Placement System");
        response.put("status", "running");
        response.put("version", "1.0.0");
        response.put("message", "Welcome to Smart Job Placement System API");
        response.put("endpoints", Map.of(
                "auth", "/api/auth",
                "students", "/api/students",
                "admin", "/api/admin",
                "companies", "/api/companies",
                "jobs", "/api/jobs",
                "applications", "/api/applications",
                "interviews", "/api/interviews"
        ));
        return response;
    }
}