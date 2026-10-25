package com.placementsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDriveRequest {

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotBlank(message = "Job role is required")
    private String jobRole;

    @NotNull(message = "Package amount is required")
    @Positive(message = "Package amount must be positive")
    private Double packageAmount;

    @NotNull(message = "Required CGPA is required")
    @DecimalMin(value = "0.0", message = "Required CGPA must be between 0.0 and 10.0")
    @DecimalMax(value = "10.0", message = "Required CGPA must be between 0.0 and 10.0")
    private Double requiredCgpa;

    private String requiredSkills;

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private LocalDate deadline;

    @NotBlank(message = "Location is required")
    private String location;
}