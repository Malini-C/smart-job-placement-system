package com.placementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDriveResponse {
    private Long id;
    private Long companyId;
    private String companyName;
    private String jobRole;
    private Double packageAmount;
    private Double requiredCgpa;
    private String requiredSkills;
    private LocalDate deadline;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long applicationCount;
}