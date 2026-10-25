package com.placementsystem.repository;

import com.placementsystem.entity.Application;
import com.placementsystem.entity.JobDrive;
import com.placementsystem.entity.Student;
import com.placementsystem.util.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Find applications by student
    List<Application> findByStudent(Student student);

    // Find applications by job drive
    List<Application> findByJobDrive(JobDrive jobDrive);

    // Find applications by status
    List<Application> findByStatus(ApplicationStatus status);

    // Find applications by student and status
    List<Application> findByStudentAndStatus(Student student, ApplicationStatus status);

    // Check if student already applied for a job
    boolean existsByStudentAndJobDrive(Student student, JobDrive jobDrive);

    // Find application by student and job drive
    Optional<Application> findByStudentAndJobDrive(Student student, JobDrive jobDrive);

    // Count applications by job drive
    Long countByJobDrive(JobDrive jobDrive);

    // Count applications by status for a job drive
    Long countByJobDriveAndStatus(JobDrive jobDrive, ApplicationStatus status);

    // Find applications with interview scheduled
    @Query("SELECT a FROM Application a WHERE a.status = 'INTERVIEW_SCHEDULED'")
    List<Application> findApplicationsWithInterviewScheduled();

    // Find recent applications for a student
    @Query("SELECT a FROM Application a WHERE a.student = :student ORDER BY a.appliedDate DESC")
    List<Application> findRecentApplicationsByStudent(@Param("student") Student student);
}