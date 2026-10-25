package com.placementsystem.service;

import com.placementsystem.dto.request.ApplicationStatusRequest;
import com.placementsystem.dto.response.ApplicationResponse;

import java.util.List;

public interface ApplicationService {
    List<ApplicationResponse> getAllApplications();
    List<ApplicationResponse> getApplicationsByJobDrive(Long jobDriveId);
    List<ApplicationResponse> getApplicationsByStudent(Long studentId);
    List<ApplicationResponse> getApplicationsByStatus(String status);
    ApplicationResponse updateApplicationStatus(Long applicationId, ApplicationStatusRequest request);
    ApplicationResponse getApplicationById(Long applicationId);
    Long countApplicationsByJobDrive(Long jobDriveId);
}