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
import java.util.Map;

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

    public Report generateDailySalesReport(String email, String date) {
        try {
            User admin = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            LocalDateTime startDate = LocalDateTime.parse(date + "T00:00:00");
            LocalDateTime endDate = startDate.plusDays(1);
            
            List<Booking> dailyBookings = bookingRepository.findByBookingDateBetween(startDate, endDate);
            Double dailyRevenue = dailyBookings.stream()
                    .filter(b -> "CONFIRMED".equals(b.getStatus()))
                    .mapToDouble(Booking::getTotalAmount)
                    .sum();
            
            String data = String.format("{\"date\": \"%s\", \"totalRevenue\": %f, \"totalBookings\": %d}", 
                    date, dailyRevenue, dailyBookings.size());
            
            Report report = new Report("DAILY_SALES", data, LocalDateTime.now(), admin);
            return reportRepository.save(report);
        } catch (Exception e) {
            System.err.println("Error generating daily sales report: " + e.getMessage());
            throw new RuntimeException("Failed to generate daily sales report: " + e.getMessage());
        }
    }

    public Report generateMonthlyReport(String email, int year, int month) {
        try {
            User admin = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1);
            
            List<Booking> monthlyBookings = bookingRepository.findByBookingDateBetween(startDate, endDate);
            Double monthlyRevenue = monthlyBookings.stream()
                    .filter(b -> "CONFIRMED".equals(b.getStatus()))
                    .mapToDouble(Booking::getTotalAmount)
                    .sum();
            
            String data = String.format("{\"year\": %d, \"month\": %d, \"totalRevenue\": %f, \"totalBookings\": %d}", 
                    year, month, monthlyRevenue, monthlyBookings.size());
            
            Report report = new Report("MONTHLY_SALES", data, LocalDateTime.now(), admin);
            return reportRepository.save(report);
        } catch (Exception e) {
            System.err.println("Error generating monthly report: " + e.getMessage());
            throw new RuntimeException("Failed to generate monthly report: " + e.getMessage());
        }
    }

    public Report generateMoviePerformanceReport(String email) {
        try {
            User admin = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Booking> allBookings = bookingRepository.findAll();
            Map<String, Double> movieStats = allBookings.stream()
                    .filter(b -> "CONFIRMED".equals(b.getStatus()))
                    .collect(java.util.stream.Collectors.groupingBy(
                            b -> b.getShowtime().getMovie().getTitle(),
                            java.util.stream.Collectors.summingDouble(Booking::getTotalAmount)
                    ));
            
            String data = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(movieStats);
            Report report = new Report("MOVIE_PERFORMANCE", data, LocalDateTime.now(), admin);
            return reportRepository.save(report);
        } catch (Exception e) {
            System.err.println("Error generating movie performance report: " + e.getMessage());
            throw new RuntimeException("Failed to generate movie performance report: " + e.getMessage());
        }
    }

    public Report generateUserActivityReport(String email) {
        try {
            User admin = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Booking> allBookings = bookingRepository.findAll();
            long totalUsers = allBookings.stream()
                    .map(Booking::getUser)
                    .distinct()
                    .count();
            
            double avgBookingsPerUser = totalUsers > 0 ? (double) allBookings.size() / totalUsers : 0;
            
            String data = String.format("{\"totalUsers\": %d, \"totalBookings\": %d, \"avgBookingsPerUser\": %f}", 
                    totalUsers, allBookings.size(), avgBookingsPerUser);
            
            Report report = new Report("USER_ACTIVITY", data, LocalDateTime.now(), admin);
            return reportRepository.save(report);
        } catch (Exception e) {
            System.err.println("Error generating user activity report: " + e.getMessage());
            throw new RuntimeException("Failed to generate user activity report: " + e.getMessage());
        }
    }
}
