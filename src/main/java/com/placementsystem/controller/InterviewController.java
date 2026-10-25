package com.placementsystem.controller;

import com.placementsystem.dto.request.InterviewRequest;
import com.placementsystem.dto.response.ApiResponse;
import com.placementsystem.dto.response.InterviewResponse;
import com.placementsystem.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> scheduleInterview(@Valid @RequestBody InterviewRequest request) {
        InterviewResponse response = interviewService.scheduleInterview(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Interview scheduled", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateInterview(@PathVariable Long id, @Valid @RequestBody InterviewRequest request) {
        InterviewResponse response = interviewService.updateInterview(id, request);
        return ResponseEntity.ok(ApiResponse.success("Interview updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> cancelInterview(@PathVariable Long id) {
        interviewService.cancelInterview(id);
        return ResponseEntity.ok(ApiResponse.success("Interview cancelled", null));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse> getInterviewsByStudent(@PathVariable Long studentId) {
        List<InterviewResponse> interviews = interviewService.getInterviewsByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success("Interviews retrieved", interviews));
    }

    @GetMapping("/date/{date}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getInterviewsByDate(@PathVariable String date) {
        List<InterviewResponse> interviews = interviewService.getInterviewsByDate(date);
        return ResponseEntity.ok(ApiResponse.success("Interviews retrieved", interviews));
    }
}