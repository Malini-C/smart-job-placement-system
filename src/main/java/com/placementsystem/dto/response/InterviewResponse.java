package com.placementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResponse {
    private Long id;
    private Long applicationId;
    private String studentName;
    private String jobRole;
    private String companyName;
    private LocalDate interviewDate;
    private LocalTime interviewTime;
    private String mode;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}