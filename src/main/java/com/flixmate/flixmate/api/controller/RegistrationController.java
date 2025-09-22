package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.User;
import com.flixmate.flixmate.api.entity.UserStatus;
import com.flixmate.flixmate.api.repository.UserRepository;
import com.flixmate.flixmate.api.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserStatusRepository userStatusRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/api/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            System.out.println("=== REGISTRATION CONTROLLER: register ===");
            System.out.println("Request data: " + request);
            
            String email = request.get("email");
            String password = request.get("password");
            String userName = request.get("userName");
            String phone = request.get("phone");
            
            System.out.println("Email: " + email);
            System.out.println("UserName: " + userName);
            
            // Validate required fields
            if (email == null || password == null || userName == null) {
                System.out.println("Missing required fields");
                return ResponseEntity.badRequest().body("Email, password, and username are required");
            }

            // Check if user already exists
            if (userRepository.findByEmail(email).isPresent()) {
                System.out.println("User already exists: " + email);
                return ResponseEntity.badRequest().body("User with this email already exists");
            }

            // Get or create user status
            UserStatus userStatus = userStatusRepository.findByStatusName("user")
                .orElse(new UserStatus("user", "ROLE_USER"));
            
            if (userStatus.getStatusId() == null) {
                userStatus = userStatusRepository.save(userStatus);
                System.out.println("Created user status");
            }

            // Create new user
            User user = new User();
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setUserName(userName);
            user.setPhone(phone != null ? phone : "");
            user.setStatus(userStatus);
            user.setRegistrationDate(LocalDateTime.now());

            User savedUser = userRepository.save(user);
            System.out.println("User registered successfully with ID: " + savedUser.getUserId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("userId", savedUser.getUserId());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== REGISTRATION CONTROLLER ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Registration failed: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
