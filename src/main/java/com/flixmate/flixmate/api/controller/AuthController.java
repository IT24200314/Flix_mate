package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.User;
import com.flixmate.flixmate.api.entity.UserStatus;
import com.flixmate.flixmate.api.repository.UserRepository;
import com.flixmate.flixmate.api.repository.UserStatusRepository;
import com.flixmate.flixmate.api.service.CustomUserDetailsService;
import com.flixmate.flixmate.api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;

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

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );

            // Get user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<User> userOptional = userRepository.findByEmail(email);
            
            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            User user = userOptional.get();
            System.out.println("Login successful for user: " + email);
            System.out.println("User role: " + (user.getStatus() != null ? user.getStatus().getStatusName() : "UNKNOWN"));

            // Generate JWT token
            Map<String, Object> claims = new HashMap<>();
            String statusName = user.getStatus() != null ? user.getStatus().getStatusName() : "user";
            boolean isAdmin = statusName.equalsIgnoreCase("admin");
            claims.put("role", isAdmin ? "ADMIN" : "USER");
            claims.put("userId", user.getUserId());
            claims.put("userName", user.getUserName());
            
            String token = jwtUtil.generateToken(userDetails, claims);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", jwtUtil.extractExpiration(token).getTime());
            response.put("user", Map.of(
                "email", user.getEmail(),
                "userName", user.getUserName(),
                "phone", user.getPhone() != null ? user.getPhone() : "",
                "statusName", statusName,
                "role", isAdmin ? "admin" : "user",
                "userId", user.getUserId()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("=== AUTH CONTROLLER ERROR: login ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registerRequest) {
        try {
            System.out.println("=== AUTH CONTROLLER: register ===");
            String email = registerRequest.get("email");
            String password = registerRequest.get("password");
            String userName = registerRequest.get("userName");
            String phone = registerRequest.get("phone");
            
            System.out.println("Registration request for email: " + email);
            
            if (email == null || password == null || userName == null) {
                return ResponseEntity.badRequest().body("Email, password, and username are required");
            }

            // Check if user already exists
            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.badRequest().body("User with this email already exists");
            }

            // Get or create user status
            UserStatus userStatus = userStatusRepository.findByStatusName("user")
                .orElse(new UserStatus("user", "ROLE_USER"));
            
            if (userStatus.getStatusId() == null) {
                userStatus = userStatusRepository.save(userStatus);
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

            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            System.err.println("=== AUTH CONTROLLER ERROR: register ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
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
            
            // Find or create admin status
            UserStatus adminStatus = userStatusRepository.findByStatusName("admin").orElse(null);
            if (adminStatus == null) {
                adminStatus = new UserStatus("admin", "ROLE_ADMIN");
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
        return ResponseEntity.ok("Admin endpoint is accessible");
    }
    
    @GetMapping("/list-users")
    public ResponseEntity<?> listUsers() {
        try {
            List<User> users = userRepository.findAll();
            List<Map<String, Object>> userList = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userId", user.getUserId());
                    userMap.put("email", user.getEmail());
                    userMap.put("userName", user.getUserName());
                    userMap.put("phone", user.getPhone() != null ? user.getPhone() : "");
                    userMap.put("status", user.getStatus() != null ? user.getStatus().getStatusName() : "unknown");
                    userMap.put("registrationDate", user.getRegistrationDate() != null ? user.getRegistrationDate().toString() : "unknown");
                    return userMap;
                })
                .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalUsers", users.size());
            response.put("users", userList);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error listing users: " + e.getMessage());
        }
    }
    
    @GetMapping("/user-role")
    public ResponseEntity<?> getUserRole(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        
        return ResponseEntity.ok(Map.of(
            "email", userDetails.getUsername(),
            "authorities", userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .collect(java.util.stream.Collectors.toList())
        ));
    }
    
    @PostMapping("/make-admin")
    public ResponseEntity<?> makeUserAdmin(@RequestParam String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found: " + email);
            }
            
            User user = userOpt.get();
            UserStatus adminStatus = userStatusRepository.findByStatusName("admin")
                .orElse(new UserStatus("admin", "ROLE_ADMIN"));
            
            user.setStatus(adminStatus);
            userRepository.save(user);
            
            return ResponseEntity.ok("User " + email + " is now an admin");
        } catch (Exception e) {
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
