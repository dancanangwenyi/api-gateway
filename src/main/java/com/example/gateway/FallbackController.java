package com.example.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FallbackController {

    // Common method to handle fallbacks
    private ResponseEntity<ServiceFallbackResponse> fallback(String serviceName) {
        String message = serviceName + " is currently unavailable. Please try again later.";
        log.warn(message);
        ServiceFallbackResponse response = new ServiceFallbackResponse(message, 503);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/bookServiceFallBack", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<ServiceFallbackResponse> bookServiceFallBack() {
        return fallback("Book Service");
    }

    @RequestMapping(value = "/securityServiceFallBack", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<ServiceFallbackResponse> securityServiceFallBack() {
        return fallback("Security Service");
    }
}
