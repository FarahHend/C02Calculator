package com.Hend.FootprintService.service;

import com.Hend.FootprintService.entity.CompanyResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CompanyClientService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${application.config.company-url}")
    private String companyServiceUrl;


    @PostConstruct
    public void init() {
        System.out.println("Company Service URL: " + companyServiceUrl);
    }

    public String getCompanyNameById(String companyId) {
        String url = companyServiceUrl + "/" + companyId;
        CompanyResponse response = restTemplate.getForObject(url, CompanyResponse.class);
        if (response != null) {
            return response.getCompanyName();
        }
        throw new IllegalArgumentException("Company not found with ID: " + companyId);
    }
}


