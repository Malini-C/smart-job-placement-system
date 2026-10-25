package com.placementsystem.service.impl;

import com.placementsystem.dto.request.JobDriveRequest;
import com.placementsystem.dto.response.JobDriveResponse;
import com.placementsystem.entity.Company;
import com.placementsystem.entity.JobDrive;
import com.placementsystem.exception.ResourceNotFoundException;
import com.placementsystem.repository.ApplicationRepository;
import com.placementsystem.repository.CompanyRepository;
import com.placementsystem.repository.JobDriveRepository;
import com.placementsystem.service.JobDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobDriveServiceImpl implements JobDriveService {

    private final JobDriveRepository jobDriveRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional
    public JobDriveResponse createJobDrive(JobDriveRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        JobDrive jobDrive = new JobDrive();
        jobDrive.setCompany(company);
        jobDrive.setJobRole(request.getJobRole());
        jobDrive.setPackageAmount(request.getPackageAmount());
        jobDrive.setRequiredCgpa(request.getRequiredCgpa());
        jobDrive.setRequiredSkills(request.getRequiredSkills());
        jobDrive.setDeadline(request.getDeadline());
        jobDrive.setLocation(request.getLocation());
        return convertToResponse(jobDriveRepository.save(jobDrive));
    }

    @Override
    @Transactional
    public JobDriveResponse updateJobDrive(Long id, JobDriveRequest request) {
        JobDrive jobDrive = jobDriveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job drive not found"));
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        jobDrive.setCompany(company);
        jobDrive.setJobRole(request.getJobRole());
        jobDrive.setPackageAmount(request.getPackageAmount());
        jobDrive.setRequiredCgpa(request.getRequiredCgpa());
        jobDrive.setRequiredSkills(request.getRequiredSkills());
        jobDrive.setDeadline(request.getDeadline());
        jobDrive.setLocation(request.getLocation());
        return convertToResponse(jobDriveRepository.save(jobDrive));
    }

    @Override
    @Transactional
    public void deleteJobDrive(Long id) {
        if (!jobDriveRepository.existsById(id)) {
            throw new ResourceNotFoundException("Job drive not found");
        }
        jobDriveRepository.deleteById(id);
    }

    // ✅ ADD @Transactional(readOnly = true)
    @Override
    @Transactional(readOnly = true)
    public JobDriveResponse getJobDriveById(Long id) {
        JobDrive jobDrive = jobDriveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job drive not found"));
        return convertToResponse(jobDrive);
    }

    // ✅ ADD @Transactional(readOnly = true)
    @Override
    @Transactional(readOnly = true)
    public List<JobDriveResponse> getAllJobDrives() {
        return jobDriveRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDriveResponse> getJobDrivesByCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return jobDriveRepository.findByCompany(company).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ✅ ADD @Transactional(readOnly = true) — CRITICAL for /jobs/active
    @Override
    @Transactional(readOnly = true)
    public List<JobDriveResponse> getActiveJobDrives() {
        return jobDriveRepository.findByDeadlineAfter(LocalDate.now()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobDriveResponse> getJobDrivesByLocation(String location) {
        return jobDriveRepository.findByLocation(location).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private JobDriveResponse convertToResponse(JobDrive jobDrive) {
        JobDriveResponse response = new JobDriveResponse();
        response.setId(jobDrive.getId());
        // ✅ This line accesses LAZY relationship — needs transaction!
        response.setCompanyId(jobDrive.getCompany().getId());
        response.setCompanyName(jobDrive.getCompany().getCompanyName());
        response.setJobRole(jobDrive.getJobRole());
        response.setPackageAmount(jobDrive.getPackageAmount());
        response.setRequiredCgpa(jobDrive.getRequiredCgpa());
        response.setRequiredSkills(jobDrive.getRequiredSkills());
        response.setDeadline(jobDrive.getDeadline());
        response.setLocation(jobDrive.getLocation());
        response.setCreatedAt(jobDrive.getCreatedAt());
        response.setUpdatedAt(jobDrive.getUpdatedAt());
        Long applicationCount = applicationRepository.countByJobDrive(jobDrive);
        response.setApplicationCount(applicationCount);
        return response;
    }
}