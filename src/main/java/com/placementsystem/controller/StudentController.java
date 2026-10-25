package com.placementsystem.controller;

import com.placementsystem.dto.request.StudentProfileRequest;
import com.placementsystem.dto.response.ApiResponse;
import com.placementsystem.dto.response.ApplicationResponse;
import com.placementsystem.dto.response.JobDriveResponse;
import com.placementsystem.dto.response.StudentResponse;
import com.placementsystem.security.SecurityUtils;
import com.placementsystem.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final SecurityUtils securityUtils;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile() {
        Long userId = securityUtils.getCurrentUserId();
        StudentResponse response = studentService.getStudentProfile(userId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", response));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(@Valid @RequestBody StudentProfileRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        StudentResponse response = studentService.updateStudentProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", response));
    }

    @GetMapping("/jobs/eligible")
    public ResponseEntity<ApiResponse> getEligibleJobs() {
        Long userId = securityUtils.getCurrentUserId();
        List<JobDriveResponse> jobs = studentService.getEligibleJobDrives(userId);
        return ResponseEntity.ok(ApiResponse.success("Eligible jobs retrieved", jobs));
    }

    @PostMapping("/jobs/{jobId}/apply")
    public ResponseEntity<ApiResponse> applyForJob(@PathVariable Long jobId) {
        Long userId = securityUtils.getCurrentUserId();
        studentService.applyForJob(userId, jobId);
        return ResponseEntity.ok(ApiResponse.success("Application submitted successfully", null));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse> getMyApplications() {
        Long userId = securityUtils.getCurrentUserId();
        List<ApplicationResponse> applications = studentService.getMyApplications(userId);
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved", applications));
    }

    @GetMapping("/applications/{applicationId}/status")
    public ResponseEntity<ApiResponse> getApplicationStatus(@PathVariable Long applicationId) {
        Long userId = securityUtils.getCurrentUserId();
        ApplicationResponse response = studentService.getApplicationStatus(userId, applicationId);
        return ResponseEntity.ok(ApiResponse.success("Application status retrieved", response));
    }
}