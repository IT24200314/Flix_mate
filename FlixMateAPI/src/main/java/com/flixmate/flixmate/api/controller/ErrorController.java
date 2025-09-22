package com.flixmate.flixmate.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/errors")
@CrossOrigin(origins = "*")
public class ErrorController {

    @PostMapping
    public ResponseEntity<?> logError(@RequestBody Map<String, Object> errorData) {
        try {
            System.err.println("=== CLIENT ERROR RECEIVED ===");
            System.err.println("Type: " + errorData.get("type"));
            System.err.println("Message: " + errorData.get("message"));
            System.err.println("URL: " + errorData.get("url"));
            System.err.println("Timestamp: " + errorData.get("timestamp"));
            System.err.println("User Agent: " + errorData.get("userAgent"));
            
            // Log the error to console
            Exception exception = new Exception("Client Error: " + errorData.get("message"));
            exception.printStackTrace();
            
            // You could also store errors in database for analysis
            // errorRepository.save(createErrorEntity(errorData));
            
            return ResponseEntity.ok("Error logged successfully");
        } catch (Exception e) {
            System.err.println("Failed to log client error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Failed to log error: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> getErrorHealth() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "healthy");
            health.put("timestamp", LocalDateTime.now());
            health.put("message", "Error logging system is operational");
            
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error health check failed: " + e.getMessage());
        }
    }
}
