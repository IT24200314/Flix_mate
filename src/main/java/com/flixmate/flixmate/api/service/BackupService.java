package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.repository.BookingRepository;
import com.flixmate.flixmate.api.repository.SeatRepository;
import com.flixmate.flixmate.api.repository.ShowTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class BackupService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowTimeRepository showTimeRepository;

    public void createBackup() {
        String timestamp = LocalDateTime.now().toString().replace(":", "-");
        String filename = "backup_" + timestamp + ".txt";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("=== FlixMate Data Backup ===\n");
            writer.write("Backup created at: " + LocalDateTime.now() + "\n\n");
            
            writer.write("=== Bookings ===\n");
            bookingRepository.findAll().forEach(booking -> {
                try {
                    writer.write(booking.toString() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            
            writer.write("\n=== Seats ===\n");
            seatRepository.findAll().forEach(seat -> {
                try {
                    writer.write(seat.toString() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            
            writer.write("\n=== Show Times ===\n");
            showTimeRepository.findAll().forEach(showTime -> {
                try {
                    writer.write(showTime.toString() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            
            writer.write("\n=== End of Backup ===\n");
        } catch (IOException e) {
            throw new RuntimeException("Backup failed", e);
        }
    }
}
