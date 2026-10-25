package com.placementsystem.service;

import com.placementsystem.dto.request.CompanyRequest;
import com.placementsystem.dto.response.CompanyResponse;

import java.util.List;

public interface CompanyService {
    CompanyResponse createCompany(CompanyRequest request);
    CompanyResponse updateCompany(Long id, CompanyRequest request);
    void deleteCompany(Long id);
    CompanyResponse getCompanyById(Long id);
    List<CompanyResponse> getAllCompanies();
    List<CompanyResponse> searchCompanies(String name);
}