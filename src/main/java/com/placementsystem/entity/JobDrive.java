package com.placementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_drives")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class JobDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotBlank(message = "Job role is required")
    @Column(name = "job_role", nullable = false)
    private String jobRole;

    @NotNull(message = "Package amount is required")
    @Positive(message = "Package amount must be positive")
    @Column(name = "package_amount", nullable = false)
    private Double packageAmount;

    @NotNull(message = "Required CGPA is required")
    @DecimalMin(value = "0.0", message = "Required CGPA must be between 0.0 and 10.0")
    @DecimalMax(value = "10.0", message = "Required CGPA must be between 0.0 and 10.0")
    @Column(name = "required_cgpa", nullable = false)
    private Double requiredCgpa;

    @Column(name = "required_skills", columnDefinition = "TEXT")
    private String requiredSkills;

    // ⚠️ CRITICAL: @Future must NOT be on the entity!
    // Remove @Future if present — it fails on load
    @Column(nullable = false)
    private LocalDate deadline;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "jobDrive", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Application> applications = new ArrayList<>();

    public void addApplication(Application application) {
        applications.add(application);
        application.setJobDrive(this);
    }

    public void removeApplication(Application application) {
        applications.remove(application);
        application.setJobDrive(null);
    }
}