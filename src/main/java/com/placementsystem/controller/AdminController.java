package com.placementsystem.controller;

import com.placementsystem.dto.request.ApplicationStatusRequest;
import com.placementsystem.dto.response.ApiResponse;
import com.placementsystem.dto.response.ApplicationResponse;
import com.placementsystem.dto.response.StudentResponse;
import com.placementsystem.service.ApplicationService;
import com.placementsystem.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final StudentService studentService;
    private final ApplicationService applicationService;

    @GetMapping("/students")
    public ResponseEntity<ApiResponse> getAllStudents() {
        // You'll need to add a method to get all students
        return ResponseEntity.ok(ApiResponse.success("Students retrieved", null));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse> getAllApplications() {
        List<ApplicationResponse> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved", applications));
    }

    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<ApiResponse> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody ApplicationStatusRequest request) {
        ApplicationResponse response = applicationService.updateApplicationStatus(applicationId, request);
        return ResponseEntity.ok(ApiResponse.success("Application status updated", response));
    }
}