package com.placementsystem.controller;

import com.placementsystem.dto.response.ApiResponse;
import com.placementsystem.dto.response.ApplicationResponse;
import com.placementsystem.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllApplications() {
        List<ApplicationResponse> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved", applications));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getApplicationsByJob(@PathVariable Long jobId) {
        List<ApplicationResponse> applications = applicationService.getApplicationsByJobDrive(jobId);
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved", applications));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getApplicationsByStudent(@PathVariable Long studentId) {
        List<ApplicationResponse> applications = applicationService.getApplicationsByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved", applications));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getApplicationsByStatus(@PathVariable String status) {
        List<ApplicationResponse> applications = applicationService.getApplicationsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved", applications));
    }

    @GetMapping("/count/{jobId}")
    public ResponseEntity<ApiResponse> countApplicationsByJob(@PathVariable Long jobId) {
        Long count = applicationService.countApplicationsByJobDrive(jobId);
        return ResponseEntity.ok(ApiResponse.success("Application count retrieved", count));
    }
}