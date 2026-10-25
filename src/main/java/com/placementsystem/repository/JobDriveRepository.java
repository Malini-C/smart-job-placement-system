package com.placementsystem.repository;

import com.placementsystem.entity.Company;
import com.placementsystem.entity.JobDrive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobDriveRepository extends JpaRepository<JobDrive, Long> {

    // Find job drives by company
    List<JobDrive> findByCompany(Company company);

    // Find active job drives (deadline not passed)
    List<JobDrive> findByDeadlineAfter(LocalDate date);

    // Find job drives by minimum CGPA requirement
    List<JobDrive> findByRequiredCgpaLessThanEqual(Double cgpa);

    // Find job drives by role containing keyword
    List<JobDrive> findByJobRoleContaining(String role);

    // Find job drives by location
    List<JobDrive> findByLocation(String location);

    // Find job drives with deadline before given date (expired)
    List<JobDrive> findByDeadlineBefore(LocalDate date);

    // Find job drives where student is eligible (CGPA >= required)
    @Query("SELECT j FROM JobDrive j WHERE j.requiredCgpa <= :cgpa AND j.deadline > :currentDate")
    List<JobDrive> findEligibleJobDrives(@Param("cgpa") Double cgpa, @Param("currentDate") LocalDate currentDate);
}