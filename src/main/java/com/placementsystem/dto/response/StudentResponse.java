package com.placementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private Long id;
    private String name;
    private String email;
    private String department;
    private Double cgpa;
    private Integer graduationYear;
    private String skills;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}