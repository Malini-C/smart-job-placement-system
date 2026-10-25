package com.placementsystem.service;

import com.placementsystem.dto.request.InterviewRequest;
import com.placementsystem.dto.response.InterviewResponse;

import java.util.List;

public interface InterviewService {
    InterviewResponse scheduleInterview(InterviewRequest request);
    InterviewResponse updateInterview(Long id, InterviewRequest request);
    void cancelInterview(Long id);
    InterviewResponse getInterviewById(Long id);
    List<InterviewResponse> getInterviewsByStudent(Long studentId);
    List<InterviewResponse> getInterviewsByDate(String date);
}