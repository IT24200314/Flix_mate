package com.flixmate.flixmate.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecureController {

    @GetMapping("/api/secure")
    public String secureEndpoint() {
        return "Welcome to the secure area!";
    }

    @GetMapping("/public/health")
    public String healthCheck() {
        return "API is running!";
    }
}
