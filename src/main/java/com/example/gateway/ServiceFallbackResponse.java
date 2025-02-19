package com.example.gateway;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFallbackResponse {
    private String message;
    private Integer errorCode; // Optional error code
}