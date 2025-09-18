package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.*;
import com.flixmate.flixmate.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TheatreManagementService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private StaffScheduleRepository staffScheduleRepository;

    @Autowired
    private CinemaHallRepository cinemaHallRepository;

    @Autowired
    private MovieRepository movieRepository;

    public List<Seat> getAvailableSeatsByHall(Integer hallId) {
        return seatRepository.findByCinemaHall_HallIdAndStatus(hallId, "AVAILABLE");
    }

    public ShowTime addShowTime(Integer hallId, String startTime, String endTime, Double price, Integer movieId) {
        CinemaHall hall = cinemaHallRepository.findById(hallId)
                .orElseThrow(() -> new RuntimeException("Cinema hall not found"));
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        LocalDateTime parsedStart = LocalDateTime.parse(startTime);
        LocalDateTime parsedEnd = LocalDateTime.parse(endTime);

        ShowTime showtime = new ShowTime(parsedStart, parsedEnd, price, hall, movie);
        return showTimeRepository.save(showtime);
    }
    public StaffSchedule addStaffSchedule(String staffName, LocalDateTime startTime, LocalDateTime endTime, Integer hallId) {
        StaffSchedule schedule = new StaffSchedule(staffName, startTime, endTime, hallId);
        return staffScheduleRepository.save(schedule);
    }

    public List<StaffSchedule> getStaffSchedulesByHall(Integer hallId) {
        return staffScheduleRepository.findByHallId(hallId);
    }

    public List<ShowTime> getShowtimesByMovie(Integer movieId) {
        System.out.println("=== THEATRE SERVICE: getShowtimesByMovie ===");
        System.out.println("Movie ID: " + movieId);
        
        try {
            System.out.println("Querying database for showtimes with movie ID: " + movieId);
            List<ShowTime> showtimes = showTimeRepository.findByMovie_MovieId(movieId);
            System.out.println("Database returned " + showtimes.size() + " showtimes");
            
            // Log each showtime from database
            for (ShowTime st : showtimes) {
                System.out.println("DB Showtime - ID: " + st.getShowtimeId() + 
                                 ", Start: " + st.getStartTime() + 
                                 ", End: " + st.getEndTime() + 
                                 ", Price: " + st.getPrice() +
                                 ", Movie: " + (st.getMovie() != null ? st.getMovie().getTitle() : "NULL") +
                                 ", Hall: " + (st.getCinemaHall() != null ? st.getCinemaHall().getHallId() : "NULL"));
            }
            
            return showtimes;
        } catch (Exception e) {
            System.err.println("=== THEATRE SERVICE ERROR: getShowtimesByMovie ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Movie ID: " + movieId);
            System.err.println("Stack Trace:");
            e.printStackTrace();
            System.err.println("=== END THEATRE SERVICE ERROR ===");
            throw e;
        }
    }

    public List<ShowTime> getAllShowtimes() {
        return showTimeRepository.findAll();
    }
}


