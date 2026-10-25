package com.placementsystem.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileRequest {

    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.0", message = "CGPA must be between 0.0 and 10.0")
    @DecimalMax(value = "10.0", message = "CGPA must be between 0.0 and 10.0")
    private Double cgpa;

    @NotNull(message = "Graduation year is required")
    @Min(value = 2020, message = "Graduation year must be 2020 or later")
    @Max(value = 2030, message = "Graduation year must be 2030 or earlier")
    private Integer graduationYear;

    private String skills;
}