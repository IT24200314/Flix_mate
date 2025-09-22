package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.*;
import com.flixmate.flixmate.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Profile("!default")
public class DataInitializationService implements CommandLineRunner {

    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private CinemaHallRepository cinemaHallRepository;
    
    @Autowired
    private SeatRepository seatRepository;
    
    @Autowired
    private ShowTimeRepository showTimeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserStatusRepository userStatusRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeData();
    }

    private void initializeData() {
        // Check if data already exists
        if (movieRepository.count() > 0 && showTimeRepository.count() > 0) {
            return; // Data already exists
        }

        System.out.println("Initializing FlixMate data...");

        // Create user statuses
        createUserStatuses();
        
        // Create users
        createUsers();
        
        // Create movies
        createMovies();
        
        // Create cinema halls
        createCinemaHalls();
        
        // Create seats
        createSeats();
        
        // Create show times
        createShowTimes();

        System.out.println("Data initialization completed!");
    }

    private void createUserStatuses() {
        if (userStatusRepository.count() == 0) {
            UserStatus user = new UserStatus("USER", "USER");
            UserStatus admin = new UserStatus("ADMIN", "ADMIN");
            
            userStatusRepository.saveAll(List.of(user, admin));
            System.out.println("User statuses created");
        }
    }

    private void createUsers() {
        System.out.println("=== DATAINITIALIZATIONSERVICE: Skipping user creation (handled by DataLoader) ===");
        // User creation is handled by DataLoader to avoid conflicts
        // This method is kept for potential future use
    }

    private void createMovies() {
        // Movies will be created by administrators through the application interface
        System.out.println("Movie creation skipped - will be handled by admin interface");
    }

    private void createCinemaHalls() {
        // Cinema halls will be created by administrators through the application interface
        System.out.println("Cinema hall creation skipped - will be handled by admin interface");
    }

    private void createSeats() {
        // Seats will be automatically created when cinema halls are added through the admin interface
        System.out.println("Seat creation skipped - will be handled automatically when halls are created");
    }

    private void createShowTimes() {
        if (showTimeRepository.count() == 0) {
            List<Movie> movies = movieRepository.findAll();
            List<CinemaHall> halls = cinemaHallRepository.findAll();
            
            if (!movies.isEmpty() && !halls.isEmpty()) {
                // Create show times for the next few days
                LocalDateTime now = LocalDateTime.now();
                
                // Show time 1: Movie 1 in Hall 1
                ShowTime showTime1 = new ShowTime();
                showTime1.setMovie(movies.get(0));
                showTime1.setCinemaHall(halls.get(0));
                showTime1.setStartTime(now.plusHours(2));
                showTime1.setEndTime(now.plusHours(4));
                showTime1.setPrice(10.0);
                showTimeRepository.save(showTime1);

                // Show time 2: Movie 1 in Hall 1 (evening)
                ShowTime showTime2 = new ShowTime();
                showTime2.setMovie(movies.get(0));
                showTime2.setCinemaHall(halls.get(0));
                showTime2.setStartTime(now.plusHours(6));
                showTime2.setEndTime(now.plusHours(8));
                showTime2.setPrice(12.0);
                showTimeRepository.save(showTime2);

                // Show time 3: Movie 2 in Hall 2
                if (movies.size() > 1) {
                    ShowTime showTime3 = new ShowTime();
                    showTime3.setMovie(movies.get(1));
                    showTime3.setCinemaHall(halls.get(1));
                    showTime3.setStartTime(now.plusHours(3));
                    showTime3.setEndTime(now.plusHours(5));
                    showTime3.setPrice(15.0);
                    showTimeRepository.save(showTime3);
                }

                // Show time 4: Movie 3 in Hall 3
                if (movies.size() > 2 && halls.size() > 2) {
                    ShowTime showTime4 = new ShowTime();
                    showTime4.setMovie(movies.get(2));
                    showTime4.setCinemaHall(halls.get(2));
                    showTime4.setStartTime(now.plusHours(7));
                    showTime4.setEndTime(now.plusHours(9));
                    showTime4.setPrice(18.0);
                    showTimeRepository.save(showTime4);
                }

                System.out.println("Show times created");
            }
        }
    }
}
