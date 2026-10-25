package com.placementsystem.repository;

import com.placementsystem.entity.Student;
import com.placementsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find student by user
    Optional<Student> findByUser(User user);

    // Find students by CGPA range
    List<Student> findByCgpaBetween(Double minCgpa, Double maxCgpa);

    // Find students by department
    List<Student> findByDepartment(String department);

    // Find students eligible for a job (CGPA >= required)
    @Query("SELECT s FROM Student s WHERE s.cgpa >= :requiredCgpa")
    List<Student> findEligibleStudents(@Param("requiredCgpa") Double requiredCgpa);

    // Count students by department
    Long countByDepartment(String department);
}