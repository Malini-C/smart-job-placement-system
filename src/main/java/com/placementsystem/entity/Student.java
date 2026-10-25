package com.placementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @NotBlank(message = "Department is required")
    @Column(nullable = false)
    private String department;

    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "0.0", message = "CGPA must be between 0.0 and 10.0")
    @DecimalMax(value = "10.0", message = "CGPA must be between 0.0 and 10.0")
    @Column(nullable = false)
    private Double cgpa;

    @NotNull(message = "Graduation year is required")
    @Min(value = 2020, message = "Graduation year must be 2020 or later")
    @Max(value = 2030, message = "Graduation year must be 2030 or earlier")
    @Column(name = "graduation_year", nullable = false)
    private Integer graduationYear;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Application> applications = new ArrayList<>();

    // Helper method to add application
    public void addApplication(Application application) {
        applications.add(application);
        application.setStudent(this);
    }

    // Helper method to remove application
    public void removeApplication(Application application) {
        applications.remove(application);
        application.setStudent(null);
    }
}