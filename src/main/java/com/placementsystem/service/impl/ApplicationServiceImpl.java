package com.placementsystem.service.impl;

import com.placementsystem.dto.request.ApplicationStatusRequest;
import com.placementsystem.dto.response.ApplicationResponse;
import com.placementsystem.entity.Application;
import com.placementsystem.entity.Student;
import com.placementsystem.exception.BadRequestException;
import com.placementsystem.exception.ResourceNotFoundException;
import com.placementsystem.repository.ApplicationRepository;
import com.placementsystem.repository.JobDriveRepository;
import com.placementsystem.repository.StudentRepository;
import com.placementsystem.service.ApplicationService;
import com.placementsystem.util.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final JobDriveRepository jobDriveRepository;

    // ✅ ADD @Transactional(readOnly = true) — CRITICAL for /admin/applications
    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByJobDrive(Long jobDriveId) {
        return applicationRepository.findByJobDrive(
                        jobDriveRepository.findById(jobDriveId)
                                .orElseThrow(() -> new ResourceNotFoundException("Job drive not found"))
                ).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        return applicationRepository.findByStudent(student).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByStatus(String status) {
        try {
            ApplicationStatus appStatus = ApplicationStatus.valueOf(status);
            return applicationRepository.findByStatus(appStatus).stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status);
        }
    }

    @Override
    @Transactional
    public ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusRequest request) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        try {
            ApplicationStatus newStatus = ApplicationStatus.valueOf(request.getStatus());
            application.setStatus(newStatus);
            return convertToResponse(applicationRepository.save(application));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + request.getStatus());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(Long applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        return convertToResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countApplicationsByJobDrive(Long jobDriveId) {
        return applicationRepository.countByJobDrive(
                jobDriveRepository.findById(jobDriveId)
                        .orElseThrow(() -> new ResourceNotFoundException("Job drive not found"))
        );
    }

    private ApplicationResponse convertToResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        // ✅ These access LAZY relationships — must be inside transaction
        response.setStudentId(application.getStudent().getId());
        response.setStudentName(application.getStudent().getUser().getName());
        response.setJobDriveId(application.getJobDrive().getId());
        response.setJobRole(application.getJobDrive().getJobRole());
        response.setCompanyName(application.getJobDrive().getCompany().getCompanyName());
        response.setStatus(application.getStatus().name());
        response.setAppliedDate(application.getAppliedDate());
        response.setCreatedAt(application.getCreatedAt());
        response.setUpdatedAt(application.getUpdatedAt());
        return response;
    }
}