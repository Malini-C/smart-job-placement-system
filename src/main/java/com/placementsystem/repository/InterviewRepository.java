package com.placementsystem.repository;

import com.placementsystem.entity.Application;
import com.placementsystem.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    // Find interview by application
    Optional<Interview> findByApplication(Application application);

    // Find interviews on a specific date
    List<Interview> findByInterviewDate(LocalDate date);

    // Find interviews between dates
    List<Interview> findByInterviewDateBetween(LocalDate startDate, LocalDate endDate);

    // Find interviews by mode
    List<Interview> findByMode(String mode);

    // Find interviews for a student
    @Query("SELECT i FROM Interview i WHERE i.application.student.id = :studentId")
    List<Interview> findInterviewsByStudentId(@Param("studentId") Long studentId);
}