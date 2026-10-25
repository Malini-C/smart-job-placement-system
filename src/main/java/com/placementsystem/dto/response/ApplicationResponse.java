package com.placementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long jobDriveId;
    private String jobRole;
    private String companyName;
    private String status;
    private LocalDateTime appliedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}