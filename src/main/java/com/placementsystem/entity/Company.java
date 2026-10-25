package com.placementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 200, message = "Company name must be between 2 and 200 characters")
    @Column(name = "company_name", nullable = false)
    private String companyName;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    // ✅ No @Email — plain String for URL
    @Column(name = "website")
    private String website;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JobDrive> jobDrives = new ArrayList<>();

    public void addJobDrive(JobDrive jobDrive) {
        jobDrives.add(jobDrive);
        jobDrive.setCompany(this);
    }

    public void removeJobDrive(JobDrive jobDrive) {
        jobDrives.remove(jobDrive);
        jobDrive.setCompany(null);
    }
}