package com.placementsystem.service.impl;

import com.placementsystem.dto.request.InterviewRequest;
import com.placementsystem.dto.response.InterviewResponse;
import com.placementsystem.entity.Application;
import com.placementsystem.entity.Interview;
import com.placementsystem.exception.BadRequestException;
import com.placementsystem.exception.ResourceNotFoundException;
import com.placementsystem.repository.ApplicationRepository;
import com.placementsystem.repository.InterviewRepository;
import com.placementsystem.service.InterviewService;
import com.placementsystem.util.ApplicationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;

    // ═══════════════════════════════════════════════════════════
    // SCHEDULE
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional
    public InterviewResponse scheduleInterview(InterviewRequest request) {
        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (application.getStatus() != ApplicationStatus.SHORTLISTED) {
            throw new BadRequestException(
                    "Interview can only be scheduled for SHORTLISTED applications. Current status: "
                            + application.getStatus());
        }

        if (interviewRepository.findByApplication(application).isPresent()) {
            throw new BadRequestException("Interview already scheduled for this application");
        }

        Interview interview = new Interview();
        interview.setApplication(application);
        interview.setInterviewDate(request.getInterviewDate());
        interview.setInterviewTime(request.getInterviewTime());
        interview.setMode(request.getMode());
        interview.setLocation(request.getLocation());

        application.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        applicationRepository.save(application);

        Interview saved = interviewRepository.save(interview);
        return convertToResponse(saved);
    }

    // ═══════════════════════════════════════════════════════════
    // UPDATE
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional
    public InterviewResponse updateInterview(Long id, InterviewRequest request) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        interview.setInterviewDate(request.getInterviewDate());
        interview.setInterviewTime(request.getInterviewTime());
        interview.setMode(request.getMode());
        interview.setLocation(request.getLocation());

        return convertToResponse(interviewRepository.save(interview));
    }

    // ═══════════════════════════════════════════════════════════
    // CANCEL
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional
    public void cancelInterview(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        Application application = interview.getApplication();
        application.setStatus(ApplicationStatus.SHORTLISTED);
        applicationRepository.save(application);

        interviewRepository.deleteById(id);
    }

    // ═══════════════════════════════════════════════════════════
    // GET BY ID ✅ @Transactional
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional(readOnly = true)
    public InterviewResponse getInterviewById(Long id) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));
        return convertToResponse(interview);
    }

    // ═══════════════════════════════════════════════════════════
    // GET BY STUDENT ✅ @Transactional
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional(readOnly = true)
    public List<InterviewResponse> getInterviewsByStudent(Long studentId) {
        return interviewRepository.findInterviewsByStudentId(studentId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════
    // GET BY DATE ✅ @Transactional
    // ═══════════════════════════════════════════════════════════
    @Override
    @Transactional(readOnly = true)
    public List<InterviewResponse> getInterviewsByDate(String date) {
        LocalDate interviewDate = LocalDate.parse(date);
        return interviewRepository.findByInterviewDate(interviewDate).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════
    // CONVERTER
    // ═══════════════════════════════════════════════════════════
    private InterviewResponse convertToResponse(Interview interview) {
        InterviewResponse response = new InterviewResponse();
        response.setId(interview.getId());
        response.setApplicationId(interview.getApplication().getId());
        response.setStudentName(interview.getApplication().getStudent().getUser().getName());
        response.setJobRole(interview.getApplication().getJobDrive().getJobRole());
        response.setCompanyName(interview.getApplication().getJobDrive().getCompany().getCompanyName());
        response.setInterviewDate(interview.getInterviewDate());
        response.setInterviewTime(interview.getInterviewTime());
        response.setMode(interview.getMode());
        response.setLocation(interview.getLocation());
        response.setCreatedAt(interview.getCreatedAt());
        response.setUpdatedAt(interview.getUpdatedAt());
        return response;
    }
}