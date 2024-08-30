package com.Hend.CompanyService.repository;

import com.Hend.CompanyService.entity.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CompanyRepository extends MongoRepository<Company, String> {

    List<Company> findByUserId(String userId);
}