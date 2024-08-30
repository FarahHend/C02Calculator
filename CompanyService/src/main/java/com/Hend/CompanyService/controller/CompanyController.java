package com.Hend.CompanyService.controller;

import com.Hend.CompanyService.entity.Company;
import com.Hend.CompanyService.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    //@CrossOrigin(origins = "http://localhost:4200")
    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies() {
        List<Company> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }
    @GetMapping("/{companyId}")
    public ResponseEntity<Company> getCompanyById(@PathVariable String companyId) {
        Optional<Company> company = companyService.getCompanyById(companyId);
        return company.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{companyId}/name")
    public ResponseEntity<String> getCompanyNameById(@PathVariable String companyId) {
        Optional<String> companyName = companyService.getCompanyNameById(companyId);
        return companyName.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Company>> getCompaniesByUserId(@PathVariable String userId) {
        List<Company> companies = companyService.getCompaniesByUserId(userId);
        return ResponseEntity.ok(companies);
    }

    @PostMapping
    public ResponseEntity<Company> createCompany(@RequestBody Company company) {
        Company createdCompany = companyService.createCompany(company);
        return ResponseEntity.ok(createdCompany);
    }

    @PostMapping("/{userId}")
    public ResponseEntity<Company> createCompany(@PathVariable String userId, @RequestBody Company company) {
        company.setUserId(userId);
        Company createdCompany = companyService.createCompany(company);
        return ResponseEntity.ok(createdCompany);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<Company> updateCompany(@PathVariable String companyId, @RequestBody Company company) {
        Optional<Company> existingCompany = companyService.getCompanyById(companyId);
        if (existingCompany.isPresent()) {
            company.setCompanyId(companyId);
            Company updatedCompany = companyService.updateCompany(company);
            return ResponseEntity.ok(updatedCompany);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> deleteCompany(@PathVariable String companyId) {
        companyService.deleteCompany(companyId);
        return ResponseEntity.noContent().build();
    }
}
