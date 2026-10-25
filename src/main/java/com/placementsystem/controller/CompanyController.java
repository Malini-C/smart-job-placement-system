package com.placementsystem.controller;

import com.placementsystem.dto.request.CompanyRequest;
import com.placementsystem.dto.response.ApiResponse;
import com.placementsystem.dto.response.CompanyResponse;
import com.placementsystem.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    // Admin endpoints
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createCompany(@Valid @RequestBody CompanyRequest request) {
        CompanyResponse response = companyService.createCompany(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Company created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyRequest request) {
        CompanyResponse response = companyService.updateCompany(id, request);
        return ResponseEntity.ok(ApiResponse.success("Company updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(ApiResponse.success("Company deleted", null));
    }

    // Public endpoints
    @GetMapping
    public ResponseEntity<ApiResponse> getAllCompanies() {
        List<CompanyResponse> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(ApiResponse.success("Companies retrieved", companies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCompanyById(@PathVariable Long id) {
        CompanyResponse response = companyService.getCompanyById(id);
        return ResponseEntity.ok(ApiResponse.success("Company retrieved", response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchCompanies(@RequestParam String name) {
        List<CompanyResponse> companies = companyService.searchCompanies(name);
        return ResponseEntity.ok(ApiResponse.success("Companies found", companies));
    }
}