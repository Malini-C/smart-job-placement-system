package com.placementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db")
    public Map<String, Object> testDatabase() {
        Map<String, Object> response = new HashMap<>();
        try {
            String result = jdbcTemplate.queryForObject(
                    "SELECT 'Database Connected Successfully!' as message",
                    String.class
            );
            response.put("status", "success");
            response.put("message", result);
            response.put("database", "MySQL Connected!");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Database connection failed: " + e.getMessage());
        }
        return response;
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Smart Job Placement System is running!");
        response.put("status", "success");
        return response;
    }
}