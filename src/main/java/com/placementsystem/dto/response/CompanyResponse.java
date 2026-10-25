package com.placementsystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {
    private Long id;
    private String companyName;
    private String location;
    private String website;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}