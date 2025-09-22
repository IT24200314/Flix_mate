package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminReportsService {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private MovieRepository movieRepository;
    
    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> generateSalesReport(String period, String startDate, String endDate) {
        try {
            // Mock data for demonstration - in real implementation, query actual data
            Map<String, Object> report = new HashMap<>();
            
            // Generate sample data based on period
            List<String> labels = new ArrayList<>();
            List<Double> revenueData = new ArrayList<>();
            
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            if ("daily".equals(period)) {
                for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                    labels.add(date.format(DateTimeFormatter.ofPattern("MMM dd")));
                    revenueData.add(Math.random() * 1000 + 500); // Random revenue between 500-1500
                }
            } else if ("weekly".equals(period)) {
                for (LocalDate date = start; !date.isAfter(end); date = date.plusWeeks(1)) {
                    labels.add("Week of " + date.format(DateTimeFormatter.ofPattern("MMM dd")));
                    revenueData.add(Math.random() * 5000 + 2000); // Random revenue between 2000-7000
                }
            } else { // monthly
                for (LocalDate date = start; !date.isAfter(end); date = date.plusMonths(1)) {
                    labels.add(date.format(DateTimeFormatter.ofPattern("MMM yyyy")));
                    revenueData.add(Math.random() * 20000 + 10000); // Random revenue between 10000-30000
                }
            }
            
            double totalRevenue = revenueData.stream().mapToDouble(Double::doubleValue).sum();
            int totalBookings = (int) (Math.random() * 100 + 50); // Random bookings between 50-150
            double averageTicketPrice = totalRevenue / totalBookings;
            double growthRate = Math.random() * 20 - 10; // Random growth rate between -10% to +10%
            
            report.put("totalRevenue", Math.round(totalRevenue * 100.0) / 100.0);
            report.put("totalBookings", totalBookings);
            report.put("averageTicketPrice", Math.round(averageTicketPrice * 100.0) / 100.0);
            report.put("growthRate", Math.round(growthRate * 100.0) / 100.0);
            report.put("labels", labels);
            report.put("revenueData", revenueData);
            
            return report;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate sales report: " + e.getMessage());
        }
    }

    public Map<String, Object> generatePopularMoviesReport(Integer topN, String sortBy, String period) {
        try {
            // Mock data for demonstration
            List<String> movieTitles = Arrays.asList(
                "The Matrix", "Inception", "Avatar", "Titanic", "Avengers: Endgame",
                "The Dark Knight", "Pulp Fiction", "Forrest Gump", "The Lion King", "Spirited Away"
            );
            
            List<String> labels = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            
            for (int i = 0; i < Math.min(topN, movieTitles.size()); i++) {
                labels.add(movieTitles.get(i));
                
                if ("revenue".equals(sortBy)) {
                    values.add(Math.round((Math.random() * 50000 + 10000) * 100.0) / 100.0);
                } else if ("bookings".equals(sortBy)) {
                    values.add((int) (Math.random() * 500 + 100));
                } else { // rating
                    values.add(Math.round((Math.random() * 2 + 3) * 10.0) / 10.0);
                }
            }
            
            Map<String, Object> report = new HashMap<>();
            report.put("topN", topN);
            report.put("sortBy", sortBy);
            report.put("period", period);
            report.put("labels", labels);
            report.put("values", values);
            
            return report;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate popular movies report: " + e.getMessage());
        }
    }

    public Map<String, Object> generatePaymentReport(String status, String startDate, String endDate) {
        try {
            // Mock data for demonstration
            int totalPayments = (int) (Math.random() * 200 + 100);
            int successCount = (int) (totalPayments * 0.85); // 85% success rate
            int failedCount = (int) (totalPayments * 0.10); // 10% failed
            int pendingCount = totalPayments - successCount - failedCount; // 5% pending
            
            double successRate = (double) successCount / totalPayments * 100;
            
            Map<String, Object> report = new HashMap<>();
            report.put("totalPayments", totalPayments);
            report.put("successCount", successCount);
            report.put("failedCount", failedCount);
            report.put("pendingCount", pendingCount);
            report.put("successRate", Math.round(successRate * 100.0) / 100.0);
            
            return report;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate payment report: " + e.getMessage());
        }
    }

    public Map<String, Object> generateTrendsReport(String trendType, String period, String startDate) {
        try {
            // Mock data for demonstration
            List<String> labels = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            
            LocalDate start = LocalDate.parse(startDate);
            
            if ("time".equals(trendType)) {
                if ("daily".equals(period)) {
                    for (int i = 0; i < 7; i++) {
                        labels.add(start.plusDays(i).format(DateTimeFormatter.ofPattern("MMM dd")));
                        values.add((int) (Math.random() * 50 + 20));
                    }
                } else if ("weekly".equals(period)) {
                    for (int i = 0; i < 4; i++) {
                        labels.add("Week " + (i + 1));
                        values.add((int) (Math.random() * 200 + 100));
                    }
                } else { // monthly
                    for (int i = 0; i < 6; i++) {
                        labels.add(start.plusMonths(i).format(DateTimeFormatter.ofPattern("MMM yyyy")));
                        values.add((int) (Math.random() * 800 + 400));
                    }
                }
            } else if ("movie".equals(trendType)) {
                List<String> movies = Arrays.asList("The Matrix", "Inception", "Avatar", "Titanic", "Avengers");
                for (String movie : movies) {
                    labels.add(movie);
                    values.add((int) (Math.random() * 100 + 50));
                }
            } else { // hall
                List<String> halls = Arrays.asList("Hall A", "Hall B", "Hall C", "Hall D", "Hall E");
                for (String hall : halls) {
                    labels.add(hall);
                    values.add((int) (Math.random() * 80 + 30));
                }
            }
            
            Map<String, Object> report = new HashMap<>();
            report.put("trendType", trendType);
            report.put("period", period);
            report.put("labels", labels);
            report.put("values", values);
            
            return report;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate trends report: " + e.getMessage());
        }
    }
}
