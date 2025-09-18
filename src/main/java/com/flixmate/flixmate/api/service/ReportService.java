package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.Booking;
import com.flixmate.flixmate.api.entity.Report;
import com.flixmate.flixmate.api.entity.User;
import com.flixmate.flixmate.api.repository.BookingRepository;
import com.flixmate.flixmate.api.repository.ReportRepository;
import com.flixmate.flixmate.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    public Report generateRevenueReport(String email) {
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Booking> bookings = bookingRepository.findAll();
        Double totalRevenue = bookings.stream()
                .filter(b -> "PAID".equals(b.getStatus()))
                .mapToDouble(Booking::getTotalAmount)
                .sum();
        String data = "{\"totalRevenue\": " + totalRevenue + "}";
        Report report = new Report("REVENUE", data, LocalDateTime.now(), admin);
        return reportRepository.save(report);
    }

    public Report generatePopularityReport(String email) {
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Mock popularity (e.g., count bookings per movie)
        String data = "{\"popularMovie\": \"Inception\", \"bookingCount\": 5}";
        Report report = new Report("POPULARITY", data, LocalDateTime.now(), admin);
        return reportRepository.save(report);
    }

    public Report generateTicketSalesReport(String email) {
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Booking> bookings = bookingRepository.findAll();
        Long totalTickets = bookings.stream()
                .filter(b -> "PAID".equals(b.getStatus()))
                .mapToLong(Booking::getTotalSeats)
                .sum();
        String data = "{\"totalTickets\": " + totalTickets + "}";
        Report report = new Report("TICKET_SALES", data, LocalDateTime.now(), admin);
        return reportRepository.save(report);
    }

    public List<Report> getAllReports(String email) {
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return reportRepository.findByUser(admin);
    }

    public Report getReportById(Integer id) {
        return reportRepository.findById(id).orElse(null);
    }

    public Report createReport(String email, String type) {
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        switch (type.toUpperCase()) {
            case "REVENUE":
                return generateRevenueReport(email);
            case "POPULARITY":
                return generatePopularityReport(email);
            case "TICKET_SALES":
                return generateTicketSalesReport(email);
            default:
                throw new RuntimeException("Invalid report type: " + type);
        }
    }

    public boolean deleteReport(Integer id) {
        if (reportRepository.existsById(id)) {
            reportRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
