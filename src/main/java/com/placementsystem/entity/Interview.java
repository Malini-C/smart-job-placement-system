package com.placementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "interviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Application is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    @NotNull(message = "Interview date is required")
    @Future(message = "Interview date must be in the future")
    @Column(name = "interview_date", nullable = false)
    private LocalDate interviewDate;

    @NotNull(message = "Interview time is required")
    @Column(name = "interview_time", nullable = false)
    private LocalTime interviewTime;

    @NotBlank(message = "Interview mode is required")
    @Column(nullable = false)
    private String mode; // ONLINE, OFFLINE, HYBRID

    private String location; // For offline interviews

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}