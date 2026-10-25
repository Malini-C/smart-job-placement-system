package com.placementsystem.service;

import com.placementsystem.dto.request.StudentProfileRequest;
import com.placementsystem.dto.response.ApplicationResponse;
import com.placementsystem.dto.response.JobDriveResponse;
import com.placementsystem.dto.response.StudentResponse;

import java.util.List;

public interface StudentService {
    StudentResponse getStudentProfile(Long userId);
    StudentResponse updateStudentProfile(Long userId, StudentProfileRequest request);
    List<JobDriveResponse> getEligibleJobDrives(Long userId);
    void applyForJob(Long userId, Long jobDriveId);
    List<ApplicationResponse> getMyApplications(Long userId);
    ApplicationResponse getApplicationStatus(Long userId, Long applicationId);
}