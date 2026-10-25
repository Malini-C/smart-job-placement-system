package com.placementsystem.service;

import com.placementsystem.dto.request.JobDriveRequest;
import com.placementsystem.dto.response.JobDriveResponse;

import java.util.List;

public interface JobDriveService {
    JobDriveResponse createJobDrive(JobDriveRequest request);
    JobDriveResponse updateJobDrive(Long id, JobDriveRequest request);
    void deleteJobDrive(Long id);
    JobDriveResponse getJobDriveById(Long id);
    List<JobDriveResponse> getAllJobDrives();
    List<JobDriveResponse> getJobDrivesByCompany(Long companyId);
    List<JobDriveResponse> getActiveJobDrives();
    List<JobDriveResponse> getJobDrivesByLocation(String location);
}