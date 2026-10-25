package com.placementsystem.service.impl;

import com.placementsystem.dto.request.CompanyRequest;
import com.placementsystem.dto.response.CompanyResponse;
import com.placementsystem.entity.Company;
import com.placementsystem.exception.BadRequestException;
import com.placementsystem.exception.ResourceNotFoundException;
import com.placementsystem.repository.CompanyRepository;
import com.placementsystem.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public CompanyResponse createCompany(CompanyRequest request) {
        Company company = new Company();
        company.setCompanyName(request.getCompanyName());
        company.setLocation(request.getLocation());
        company.setWebsite(request.getWebsite());
        company.setDescription(request.getDescription());

        Company savedCompany = companyRepository.save(company);
        return convertToResponse(savedCompany);
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(Long id, CompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        company.setCompanyName(request.getCompanyName());
        company.setLocation(request.getLocation());
        company.setWebsite(request.getWebsite());
        company.setDescription(request.getDescription());

        Company updatedCompany = companyRepository.save(company);
        return convertToResponse(updatedCompany);
    }

    @Override
    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company not found");
        }
        companyRepository.deleteById(id);
    }

    @Override
    public CompanyResponse getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return convertToResponse(company);
    }

    @Override
    public List<CompanyResponse> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompanyResponse> searchCompanies(String name) {
        return companyRepository.findByCompanyNameContaining(name).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private CompanyResponse convertToResponse(Company company) {
        CompanyResponse response = new CompanyResponse();
        response.setId(company.getId());
        response.setCompanyName(company.getCompanyName());
        response.setLocation(company.getLocation());
        response.setWebsite(company.getWebsite());
        response.setDescription(company.getDescription());
        response.setCreatedAt(company.getCreatedAt());
        response.setUpdatedAt(company.getUpdatedAt());
        return response;
    }
}