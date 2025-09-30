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
        if (userStatusRepository.count() == 0) {
            UserStatus userStatus = new UserStatus("Active", "ROLE_USER");
            UserStatus adminStatus = new UserStatus("Admin", "ROLE_ADMIN");
            UserStatus suspendedStatus = new UserStatus("Suspended", "ROLE_USER");
            UserStatus inactiveStatus = new UserStatus("Inactive", "ROLE_USER");

            userStatusRepository.save(userStatus);
            userStatusRepository.save(adminStatus);
            userStatusRepository.save(suspendedStatus);
            userStatusRepository.save(inactiveStatus);
            userStatusRepository.flush(); // Ensure UserStatus entities are persisted
        }

        // Create admin and sample user accounts
        System.out.println("=== DATALOADER: Checking user count ===");
        long userCount = userRepository.count();
        System.out.println("User count: " + userCount);
        
        // Check if admin user exists specifically
        Optional<User> existingAdmin = userRepository.findByEmail("admin@example.com");
        if (existingAdmin.isEmpty()) {
            System.out.println("Admin user not found, creating...");
            
            // Get the existing UserStatus entities (they should already be saved)
            UserStatus adminStatus = userStatusRepository.findByStatusName("Admin")
                .orElseThrow(() -> new RuntimeException("Admin status not found"));
            UserStatus userStatus = userStatusRepository.findByStatusName("Active")
                .orElseThrow(() -> new RuntimeException("User status not found"));

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

        // Seed initial movies if none exist
        if (movieRepository.count() == 0) {
            movieRepository.save(new Movie("Inception", "A mind-bending heist movie", 2010, "Sci-Fi", 148));
            movieRepository.save(new Movie("The Matrix", "A sci-fi classic", 1999, "Sci-Fi", 136));
            movieRepository.save(new Movie("Dune", "A sci-fi epic", 2021, "Sci-Fi", 155));
            movieRepository.save(new Movie("Interstellar", "A space exploration adventure", 2014, "Sci-Fi", 169));
            movieRepository.save(new Movie("Blade Runner 2049", "A neo-noir sci-fi thriller", 2017, "Sci-Fi", 164));
        }

        // Seed cinema halls, seats, and showtimes if absent
        if (cinemaHallRepository.count() == 0) {
            CinemaHall hall1 = new CinemaHall("Hall 1", "Main Cinema Complex", 50);
            CinemaHall hall2 = new CinemaHall("Hall 2", "Main Cinema Complex", 30);
            cinemaHallRepository.save(hall1);
            cinemaHallRepository.save(hall2);

            List<Movie> movies = movieRepository.findAll();
            if (movies.size() >= 2) {
                Movie inception = movies.get(0);
                Movie matrix = movies.get(1);

                showTimeRepository.save(new ShowTime(
                    LocalDateTime.of(2025, 9, 18, 18, 0),
                    LocalDateTime.of(2025, 9, 18, 20, 30),
                    12.50,
                    hall1,
                    inception
                ));
                showTimeRepository.save(new ShowTime(
                    LocalDateTime.of(2025, 9, 18, 21, 0),
                    LocalDateTime.of(2025, 9, 18, 23, 30),
                    12.50,
                    hall1,
                    matrix
                ));
                showTimeRepository.save(new ShowTime(
                    LocalDateTime.of(2025, 9, 19, 14, 0),
                    LocalDateTime.of(2025, 9, 19, 16, 30),
                    10.00,
                    hall2,
                    inception
                ));
            }

            for (int i = 1; i <= 50; i++) {
                String row = i <= 10 ? "A" : i <= 20 ? "B" : i <= 30 ? "C" : i <= 40 ? "D" : "E";
                int seatNumber = i <= 10 ? i : i <= 20 ? i - 10 : i <= 30 ? i - 20 : i <= 40 ? i - 30 : i - 40;
                seatRepository.save(new Seat(row, seatNumber, "AVAILABLE", hall1));
            }

            for (int i = 1; i <= 30; i++) {
                String row = i <= 10 ? "A" : i <= 20 ? "B" : "C";
                int seatNumber = i <= 10 ? i : i <= 20 ? i - 10 : i - 20;
                seatRepository.save(new Seat(row, seatNumber, "AVAILABLE", hall2));
            }
        }

        // Ensure at least some showtimes exist when halls already seeded
        if (showTimeRepository.count() == 0) {
            List<CinemaHall> halls = cinemaHallRepository.findAll();
            List<Movie> movies = movieRepository.findAll();

            if (!halls.isEmpty() && !movies.isEmpty()) {
                CinemaHall primaryHall = halls.get(0);
                CinemaHall secondaryHall = halls.size() > 1 ? halls.get(1) : primaryHall;
                Movie primaryMovie = movies.get(0);
                Movie secondaryMovie = movies.size() > 1 ? movies.get(1) : primaryMovie;

                LocalDateTime baseTime = LocalDateTime.of(2025, 9, 18, 18, 0);

                ShowTime showtime1 = new ShowTime(
                    baseTime,
                    baseTime.plusHours(2),
                    12.50,
                    primaryHall,
                    primaryMovie
                );
                ShowTime showtime2 = new ShowTime(
                    baseTime.plusHours(3),
                    baseTime.plusHours(5),
                    14.00,
                    primaryHall,
                    secondaryMovie
                );
                ShowTime showtime3 = new ShowTime(
                    baseTime.plusDays(1),
                    baseTime.plusDays(1).plusHours(2),
                    10.00,
                    secondaryHall,
                    primaryMovie
                );

                showTimeRepository.saveAll(List.of(showtime1, showtime2, showtime3));
            }
        }

        // Seed staff schedules if none exist
        if (staffScheduleRepository.count() == 0) {
            staffScheduleRepository.save(new StaffSchedule(
                "John Doe",
                LocalDateTime.of(2025, 9, 18, 18, 0),
                LocalDateTime.of(2025, 9, 18, 22, 0),
                1 // Hall A
            ));
            staffScheduleRepository.save(new StaffSchedule(
                "Jane Smith",
                LocalDateTime.of(2025, 9, 18, 19, 0),
                LocalDateTime.of(2025, 9, 18, 23, 0),
                1 // Hall A
            ));
            staffScheduleRepository.save(new StaffSchedule(
                "Mike Johnson",
                LocalDateTime.of(2025, 9, 19, 14, 0),
                LocalDateTime.of(2025, 9, 19, 18, 0),
                2 // Hall B
            ));
        }
    }
}
