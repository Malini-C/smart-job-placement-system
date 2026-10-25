package com.placementsystem.entity;
import com.placementsystem.util.ApplicationStatus;  // ← IMPORT FROM UTIL
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "job_drive_id"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "Job drive is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_drive_id", nullable = false)
    private JobDrive jobDrive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @CreatedDate
    @Column(name = "applied_date", updatable = false)
    private LocalDateTime appliedDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Interview interview;

    // Enum for application status
//    public enum ApplicationStatus {
//        APPLIED,
//        SHORTLISTED,
//        INTERVIEW_SCHEDULED,
//        SELECTED,
//        REJECTED
//    }
}