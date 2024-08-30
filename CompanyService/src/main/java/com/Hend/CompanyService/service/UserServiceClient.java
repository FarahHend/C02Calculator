package com.Hend.CompanyService.service;

import com.Hend.CompanyService.entity.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "UserService")
public interface UserServiceClient {
    @GetMapping("/{userId}")
    Optional<UserResponse> getUserById(@PathVariable("userId") String userId);
}

//,
//        url = "${application.config.footprint-url}