package com.placementsystem.repository;

import com.placementsystem.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    // Find companies by name (contains)
    List<Company> findByCompanyNameContaining(String name);

    // Find companies by location
    List<Company> findByLocation(String location);

    // Count total companies
    @Query("SELECT COUNT(c) FROM Company c")
    Long countCompanies();
}