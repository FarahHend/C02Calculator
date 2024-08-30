package com.Hend.FootprintService.service;

import com.Hend.FootprintService.entity.CompanyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;


@FeignClient(name = "CompanyService", url = "${application.config.company-url}", configuration = FeignConfig.class)
public interface CompanyServiceClient {
    @GetMapping("/{companyId}")
    CompanyResponse getCompanyById(@PathVariable("companyId") String companyId);

    @GetMapping("/{companyId}/name")
    ResponseEntity<String> getCompanyNameById(@PathVariable("companyId") String companyId);
}


