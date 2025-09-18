package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.ShowTime;
import com.flixmate.flixmate.api.repository.ShowTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@Service
public class ScheduleChangeService {

    @Autowired
    private ShowTimeRepository showTimeRepository;

    @Autowired
    private EmailService emailService;

    public void updateShowTime(Integer showtimeId, String newStartTime) {
        ShowTime showtime = showTimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        LocalDateTime updatedStart;
        try {
            updatedStart = LocalDateTime.parse(newStartTime);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid start time format. Expected ISO-8601 string.", ex);
        }

        showtime.setStartTime(updatedStart);
        showTimeRepository.save(showtime);

        // Mock user email (replace with actual user retrieval logic)
        String userEmail = "user@example.com"; // Fetch from bookings or users
        emailService.sendScheduleChangeNotification(userEmail, showtime.getMovie().getTitle(), updatedStart.toString());
    }
}


