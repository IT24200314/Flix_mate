package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.User;
import com.flixmate.flixmate.api.entity.UserStatus;
import com.flixmate.flixmate.api.repository.UserRepository;
import com.flixmate.flixmate.api.repository.UserStatusRepository;
import com.flixmate.flixmate.api.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserStatusRepository userStatusRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            System.out.println("=== AUTH CONTROLLER: login ===");
            String email = loginRequest.get("email");
            String password = loginRequest.get("password");
            
            System.out.println("Email: " + email);
            
            if (email == null || password == null) {
                return ResponseEntity.badRequest().body("Email and password are required");
            }

            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                System.out.println("User not found: " + email);
                return ResponseEntity.badRequest().body("Invalid credentials");
            }

            User user = userOptional.get();
            System.out.println("Found user: " + user.getEmail());
            System.out.println("Stored password hash: " + user.getPasswordHash());
            System.out.println("Provided password: " + password);
            
            boolean passwordMatches = passwordEncoder.matches(password, user.getPasswordHash());
            System.out.println("Password matches: " + passwordMatches);
            
            if (!passwordMatches) {
                System.out.println("Invalid password for user: " + email);
                return ResponseEntity.badRequest().body("Invalid credentials");
            }

            System.out.println("Login successful for user: " + email);
            System.out.println("User role: " + (user.getStatus() != null ? user.getStatus().getStatusName() : "UNKNOWN"));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", Map.of(
                "email", user.getEmail(),
                "userName", user.getUserName(),
                "phone", user.getPhone() != null ? user.getPhone() : "",
                "statusName", user.getStatus() != null ? user.getStatus().getStatusName() : "USER",
                "role", user.getStatus() != null && user.getStatus().getStatusName().equals("ADMIN") ? "ADMIN" : "USER"
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== AUTH CONTROLLER ERROR: login ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin() {
        try {
            System.out.println("=== CREATING ADMIN USER ===");
            
            // Check if admin already exists
            Optional<User> existingAdmin = userRepository.findByEmail("admin@example.com");
            if (existingAdmin.isPresent()) {
                return ResponseEntity.ok("Admin user already exists: " + existingAdmin.get().getEmail());
            }
            
            // Find or create ADMIN status
            UserStatus adminStatus = userStatusRepository.findByStatusName("ADMIN").orElse(null);
            if (adminStatus == null) {
                adminStatus = new UserStatus("ADMIN", "ROLE_ADMIN");
                userStatusRepository.save(adminStatus);
                System.out.println("Created ADMIN status");
            }
            
            // Create admin user
            User admin = new User();
            admin.setUserName("Admin User");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("password123"));
            admin.setPhone("1234567890");
            admin.setStatus(adminStatus);
            admin.setRegistrationDate(LocalDateTime.now());
            
            User savedAdmin = userRepository.save(admin);
            System.out.println("Admin user created with ID: " + savedAdmin.getUserId());
            System.out.println("Admin email: " + savedAdmin.getEmail());
            System.out.println("Admin password hash: " + savedAdmin.getPasswordHash());
            
            return ResponseEntity.ok("Admin user created successfully: " + savedAdmin.getEmail());
        } catch (Exception e) {
            System.err.println("Error creating admin: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/test-admin")
    public ResponseEntity<?> testAdmin() {
        try {
            System.out.println("=== TESTING ADMIN USER ===");
            
            // Check total user count
            long totalUsers = userRepository.count();
            System.out.println("Total users in database: " + totalUsers);
            
            // List all users
            List<User> allUsers = userRepository.findAll();
            System.out.println("All users in database:");
            for (User user : allUsers) {
                System.out.println("- User: " + user.getEmail() + " (ID: " + user.getUserId() + ")");
            }
            
            // Check user statuses
            List<UserStatus> allStatuses = userStatusRepository.findAll();
            System.out.println("All user statuses:");
            for (UserStatus status : allStatuses) {
                System.out.println("- Status: " + status.getStatusName() + " (ID: " + status.getStatusId() + ")");
            }
            
            Optional<User> adminUser = userRepository.findByEmail("admin@example.com");
            if (adminUser.isPresent()) {
                User user = adminUser.get();
                System.out.println("Admin user found: " + user.getEmail());
                System.out.println("Password hash: " + user.getPasswordHash());
                System.out.println("Status: " + (user.getStatus() != null ? user.getStatus().getStatusName() : "NULL"));
                
                Map<String, Object> response = new HashMap<>();
                response.put("email", user.getEmail());
                response.put("userName", user.getUserName());
                response.put("statusName", user.getStatus() != null ? user.getStatus().getStatusName() : "NULL");
                response.put("passwordHash", user.getPasswordHash());
                response.put("totalUsers", totalUsers);
                response.put("allUsers", allUsers.stream().map(u -> u.getEmail()).collect(java.util.stream.Collectors.toList()));
                return ResponseEntity.ok(response);
            } else {
                System.out.println("Admin user not found!");
                Map<String, Object> response = new HashMap<>();
                response.put("error", "Admin user not found");
                response.put("totalUsers", totalUsers);
                response.put("allUsers", allUsers.stream().map(u -> u.getEmail()).collect(java.util.stream.Collectors.toList()));
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            System.err.println("Error testing admin: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            System.out.println("=== AUTH CONTROLLER: getProfile ===");
            
            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                return ResponseEntity.badRequest().body("Authorization header required");
            }

            String credentials = authHeader.substring(6);
            String decodedCredentials = new String(java.util.Base64.getDecoder().decode(credentials));
            String[] parts = decodedCredentials.split(":", 2);
            
            if (parts.length != 2) {
                return ResponseEntity.badRequest().body("Invalid authorization format");
            }

            String email = parts[0];
            String password = parts[1];
            
            System.out.println("Profile request for email: " + email);

            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isEmpty()) {
                System.out.println("User not found: " + email);
                return ResponseEntity.badRequest().body("User not found");
            }

            User user = userOptional.get();
            if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                System.out.println("Invalid password for user: " + email);
                return ResponseEntity.badRequest().body("Invalid credentials");
            }

            System.out.println("Profile retrieved for user: " + email);

            Map<String, Object> profile = new HashMap<>();
            profile.put("email", user.getEmail());
            profile.put("userName", user.getUserName());
            profile.put("phone", user.getPhone() != null ? user.getPhone() : "");
            profile.put("statusName", user.getStatus() != null ? user.getStatus().getStatusName() : "USER");

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            System.err.println("=== AUTH CONTROLLER ERROR: getProfile ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to get profile: " + e.getMessage());
        }
    }
}
