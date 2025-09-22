package com.flixmate.flixmate.api.config;

import com.flixmate.flixmate.api.entity.*;
import com.flixmate.flixmate.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaHallRepository cinemaHallRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private StaffScheduleRepository staffScheduleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Create user statuses if they don't exist
        System.out.println("=== DATALOADER: Checking user statuses ===");
        long statusCount = userStatusRepository.count();
        System.out.println("User status count: " + statusCount);
        
        // Always ensure user statuses exist
        UserStatus adminStatus = userStatusRepository.findByStatusName("admin")
            .orElseGet(() -> {
                System.out.println("Creating admin status...");
                UserStatus status = new UserStatus("admin", "ROLE_ADMIN");
                return userStatusRepository.save(status);
            });
            
        UserStatus userStatus = userStatusRepository.findByStatusName("user")
            .orElseGet(() -> {
                System.out.println("Creating user status...");
                UserStatus status = new UserStatus("user", "ROLE_USER");
                return userStatusRepository.save(status);
            });

        // Create admin and sample user accounts
        System.out.println("=== DATALOADER: Checking user count ===");
        long userCount = userRepository.count();
        System.out.println("User count: " + userCount);
        
        // Check if admin user exists specifically
        Optional<User> existingAdmin = userRepository.findByEmail("admin@example.com");
        if (existingAdmin.isEmpty()) {
            System.out.println("Admin user not found, creating...");

            User adminUser = new User(
                "admin@example.com",
                passwordEncoder.encode("password123"),
                "Admin",
                adminStatus
            );

            User regularUser = new User(
                "user@example.com",
                passwordEncoder.encode("userpass123"),
                "User",
                userStatus
            );

            userRepository.save(adminUser);
            userRepository.save(regularUser);
            userRepository.flush(); // Ensure User entities are persisted
            System.out.println("=== DATALOADER: Admin user saved ===");
            System.out.println("Admin email: " + adminUser.getEmail());
            System.out.println("Admin password hash: " + adminUser.getPasswordHash());
        } else {
            System.out.println("Admin user already exists, skipping creation");
        }

        // Movies will be added by administrators through the application interface

        // Cinema halls, seats, and showtimes will be created by administrators through the application interface



        // Staff schedules will be created by administrators through the application interface
    }
}
