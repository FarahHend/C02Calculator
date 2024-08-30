package com.Hend.CompanyService.service;

import com.Hend.CompanyService.entity.Company;
import com.Hend.CompanyService.entity.UserResponse;
import com.Hend.CompanyService.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private UserServiceClient userServiceClient;

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Optional<UserResponse> getUserById(String userId) {
        return userServiceClient.getUserById(userId);
    }
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Optional<Company> getCompanyById(String companyId) {
        return companyRepository.findById(companyId);
    }

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company updateCompany(Company company) {
        return companyRepository.save(company);
    }

    public void deleteCompany(String companyId) {
        companyRepository.deleteById(companyId);
    }

    public List<Company> getCompaniesByUserId(String userId) {
        return companyRepository.findByUserId(userId);
    }

    public Optional<String> getCompanyNameById(String companyId) {
        return companyRepository.findById(companyId)
                .map(Company::getName);
    }

}

