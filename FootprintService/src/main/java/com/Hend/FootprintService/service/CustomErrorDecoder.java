package com.Hend.FootprintService.service;

import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400:
                return new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad request error");
            case 404:
                return new HttpClientErrorException(HttpStatus.NOT_FOUND, "Not found error");
            case 500:
                return new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error");
            default:
                return new Exception("Generic error: " + response.reason());
        }
    }

}
