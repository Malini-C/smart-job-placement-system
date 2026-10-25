package com.placementsystem.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewRequest {

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotNull(message = "Interview date is required")
    @Future(message = "Interview date must be in the future")
    private LocalDate interviewDate;

    @NotNull(message = "Interview time is required")
    private LocalTime interviewTime;

    @NotBlank(message = "Mode is required")
    private String mode; // ONLINE, OFFLINE, HYBRID

    private String location;
}