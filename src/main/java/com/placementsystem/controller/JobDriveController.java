package com.placementsystem.controller;

import com.placementsystem.dto.request.JobDriveRequest;
import com.placementsystem.dto.response.ApiResponse;
import com.placementsystem.dto.response.JobDriveResponse;
import com.placementsystem.service.JobDriveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobDriveController {

    private final JobDriveService jobDriveService;

    // Admin endpoints
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createJobDrive(@Valid @RequestBody JobDriveRequest request) {
        JobDriveResponse response = jobDriveService.createJobDrive(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job drive created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateJobDrive(@PathVariable Long id, @Valid @RequestBody JobDriveRequest request) {
        JobDriveResponse response = jobDriveService.updateJobDrive(id, request);
        return ResponseEntity.ok(ApiResponse.success("Job drive updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteJobDrive(@PathVariable Long id) {
        jobDriveService.deleteJobDrive(id);
        return ResponseEntity.ok(ApiResponse.success("Job drive deleted", null));
    }

    // Public endpoints
    @GetMapping
    public ResponseEntity<ApiResponse> getAllJobDrives() {
        List<JobDriveResponse> jobs = jobDriveService.getAllJobDrives();
        return ResponseEntity.ok(ApiResponse.success("Job drives retrieved", jobs));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getActiveJobDrives() {
        List<JobDriveResponse> jobs = jobDriveService.getActiveJobDrives();
        return ResponseEntity.ok(ApiResponse.success("Active job drives retrieved", jobs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getJobDriveById(@PathVariable Long id) {
        JobDriveResponse response = jobDriveService.getJobDriveById(id);
        return ResponseEntity.ok(ApiResponse.success("Job drive retrieved", response));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse> getJobDrivesByCompany(@PathVariable Long companyId) {
        List<JobDriveResponse> jobs = jobDriveService.getJobDrivesByCompany(companyId);
        return ResponseEntity.ok(ApiResponse.success("Job drives retrieved", jobs));
    }

    @GetMapping("/location")
    public ResponseEntity<ApiResponse> getJobDrivesByLocation(@RequestParam String location) {
        List<JobDriveResponse> jobs = jobDriveService.getJobDrivesByLocation(location);
        return ResponseEntity.ok(ApiResponse.success("Job drives retrieved", jobs));
    }
}