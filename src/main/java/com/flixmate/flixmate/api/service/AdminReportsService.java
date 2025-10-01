package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.repository.*;
import com.flixmate.flixmate.api.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
            Map<String, Object> report = new HashMap<>();
            
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            // Query successful payments from database for accurate revenue calculation
            List<Payment> successfulPayments = paymentRepository.findAll().stream()
                .filter(payment -> {
                    LocalDate paymentDate = payment.getPaymentDate().toLocalDate();
                    return !paymentDate.isBefore(start) && !paymentDate.isAfter(end) 
                           && "SUCCESS".equals(payment.getStatus());
                })
                .collect(Collectors.toList());
            
            // Calculate real statistics from actual payments
            double totalRevenue = successfulPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
            
            int totalBookings = successfulPayments.size();
            double averageTicketPrice = totalBookings > 0 ? totalRevenue / totalBookings : 0.0;
            
            // Generate time series data based on period
            List<String> labels = new ArrayList<>();
            List<Double> revenueData = new ArrayList<>();
            
            if ("daily".equals(period)) {
                for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                    labels.add(date.format(DateTimeFormatter.ofPattern("MMM dd")));
                    final LocalDate currentDate = date;
                    double dailyRevenue = successfulPayments.stream()
                        .filter(payment -> payment.getPaymentDate().toLocalDate().equals(currentDate))
                        .mapToDouble(Payment::getAmount)
                        .sum();
                    revenueData.add(dailyRevenue);
                }
            } else if ("weekly".equals(period)) {
                for (LocalDate date = start; !date.isAfter(end); date = date.plusWeeks(1)) {
                    labels.add("Week of " + date.format(DateTimeFormatter.ofPattern("MMM dd")));
                    final LocalDate currentDate = date;
                    final LocalDate weekEnd = currentDate.plusDays(6);
                    double weeklyRevenue = successfulPayments.stream()
                        .filter(payment -> {
                            LocalDate paymentDate = payment.getPaymentDate().toLocalDate();
                            return !paymentDate.isBefore(currentDate) && !paymentDate.isAfter(weekEnd);
                        })
                        .mapToDouble(Payment::getAmount)
                        .sum();
                    revenueData.add(weeklyRevenue);
                }
            } else { // monthly
                for (LocalDate date = start; !date.isAfter(end); date = date.plusMonths(1)) {
                    labels.add(date.format(DateTimeFormatter.ofPattern("MMM yyyy")));
                    final LocalDate currentDate = date;
                    final LocalDate monthEnd = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
                    double monthlyRevenue = successfulPayments.stream()
                        .filter(payment -> {
                            LocalDate paymentDate = payment.getPaymentDate().toLocalDate();
                            return !paymentDate.isBefore(currentDate) && !paymentDate.isAfter(monthEnd);
                        })
                        .mapToDouble(Payment::getAmount)
                        .sum();
                    revenueData.add(monthlyRevenue);
                }
            }
            
            // Calculate growth rate (compare with previous period)
            double growthRate = 0.0;
            if (revenueData.size() >= 2) {
                double currentPeriod = revenueData.get(revenueData.size() - 1);
                double previousPeriod = revenueData.get(revenueData.size() - 2);
                if (previousPeriod > 0) {
                    growthRate = ((currentPeriod - previousPeriod) / previousPeriod) * 100;
                }
            }
            
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
            // Query real movie data from database
            List<Movie> allMovies = movieRepository.findAll();
            List<Map<String, Object>> movies = new ArrayList<>();
            
            for (Movie movie : allMovies) {
                // Get successful payments for this movie
                List<Payment> moviePayments = paymentRepository.findAll().stream()
                    .filter(payment -> payment.getBooking() != null && 
                           payment.getBooking().getMovie() != null &&
                           payment.getBooking().getMovie().getMovieId().equals(movie.getMovieId()) &&
                           "SUCCESS".equals(payment.getStatus()))
                    .collect(Collectors.toList());
                
                // Calculate statistics from actual payments
                int bookings = moviePayments.size();
                double revenue = moviePayments.stream()
                    .mapToDouble(Payment::getAmount)
                    .sum();
                
                // Calculate average rating from reviews
                double rating = 0.0;
                // Note: You would need to implement review repository to get actual ratings
                // For now, using a default rating
                rating = 4.0; // Default rating
                
                Map<String, Object> movieData = new HashMap<>();
                movieData.put("title", movie.getTitle());
                movieData.put("bookings", bookings);
                movieData.put("revenue", Math.round(revenue * 100.0) / 100.0);
                movieData.put("rating", rating);
                movieData.put("releaseYear", movie.getReleaseYear() != null ? 
                    movie.getReleaseYear() : 2020);
                
                movies.add(movieData);
            }
            
            // Sort based on criteria
            if ("revenue".equals(sortBy)) {
                movies.sort((a, b) -> Double.compare((Double) b.get("revenue"), (Double) a.get("revenue")));
            } else if ("bookings".equals(sortBy)) {
                movies.sort((a, b) -> Integer.compare((Integer) b.get("bookings"), (Integer) a.get("bookings")));
            } else { // rating
                movies.sort((a, b) -> Double.compare((Double) b.get("rating"), (Double) a.get("rating")));
            }
            
            // Take top N movies
            List<Map<String, Object>> topMovies = movies.stream()
                .limit(topN)
                .collect(Collectors.toList());
            
            List<String> labels = topMovies.stream()
                .map(movie -> (String) movie.get("title"))
                .collect(Collectors.toList());
            
            List<Object> values = topMovies.stream()
                .map(movie -> {
                    if ("revenue".equals(sortBy)) {
                        return movie.get("revenue");
                    } else if ("bookings".equals(sortBy)) {
                        return movie.get("bookings");
                    } else {
                        return movie.get("rating");
                    }
                })
                .collect(Collectors.toList());
            
            Map<String, Object> report = new HashMap<>();
            report.put("topN", topN);
            report.put("sortBy", sortBy);
            report.put("period", period);
            report.put("labels", labels);
            report.put("values", values);
            report.put("movies", topMovies);
            
            return report;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate popular movies report: " + e.getMessage());
        }
    }

    public Map<String, Object> generatePaymentReport(String status, String startDate, String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            // Query real payment data from database
            List<Payment> allPayments = paymentRepository.findAll();
            
            // Filter payments by date range
            List<Payment> payments = allPayments.stream()
                .filter(payment -> {
                    LocalDate paymentDate = payment.getPaymentDate().toLocalDate();
                    return !paymentDate.isBefore(start) && !paymentDate.isAfter(end);
                })
                .collect(Collectors.toList());
            
            // Filter by status if not "all"
            if (!"all".equals(status)) {
                payments = payments.stream()
                    .filter(payment -> status.equalsIgnoreCase(payment.getStatus()))
                    .collect(Collectors.toList());
            }
            
            // Calculate real statistics
            int totalPayments = payments.size();
            int successCount = (int) payments.stream()
                .filter(payment -> "SUCCESS".equals(payment.getStatus()))
                .count();
            int failedCount = (int) payments.stream()
                .filter(payment -> "FAILED".equals(payment.getStatus()))
                .count();
            int pendingCount = (int) payments.stream()
                .filter(payment -> "PENDING".equals(payment.getStatus()))
                .count();
            
            double successRate = totalPayments > 0 ? (double) successCount / totalPayments * 100 : 0.0;
            
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
            List<String> labels = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            
            LocalDate start = LocalDate.parse(startDate);
            
            if ("time".equals(trendType)) {
                // Query real booking data for time trends
                List<Booking> bookings = bookingRepository.findAll().stream()
                    .filter(booking -> "PAID".equals(booking.getStatus()))
                    .collect(Collectors.toList());
                
                if ("daily".equals(period)) {
                    for (int i = 0; i < 7; i++) {
                        LocalDate date = start.plusDays(i);
                        final LocalDate currentDate = date;
                        labels.add(currentDate.format(DateTimeFormatter.ofPattern("MMM dd")));
                        int dailyBookings = (int) bookings.stream()
                            .filter(booking -> booking.getBookingDate().toLocalDate().equals(currentDate))
                            .count();
                        values.add(dailyBookings);
                    }
                } else if ("weekly".equals(period)) {
                    for (int i = 0; i < 4; i++) {
                        LocalDate weekStart = start.plusWeeks(i);
                        final LocalDate finalWeekStart = weekStart;
                        final LocalDate weekEnd = finalWeekStart.plusDays(6);
                        labels.add("Week " + (i + 1));
                        int weeklyBookings = (int) bookings.stream()
                            .filter(booking -> {
                                LocalDate bookingDate = booking.getBookingDate().toLocalDate();
                                return !bookingDate.isBefore(finalWeekStart) && !bookingDate.isAfter(weekEnd);
                            })
                            .count();
                        values.add(weeklyBookings);
                    }
                } else { // monthly
                    for (int i = 0; i < 6; i++) {
                        LocalDate monthStart = start.plusMonths(i);
                        final LocalDate finalMonthStart = monthStart;
                        final LocalDate monthEnd = finalMonthStart.withDayOfMonth(finalMonthStart.lengthOfMonth());
                        labels.add(finalMonthStart.format(DateTimeFormatter.ofPattern("MMM yyyy")));
                        int monthlyBookings = (int) bookings.stream()
                            .filter(booking -> {
                                LocalDate bookingDate = booking.getBookingDate().toLocalDate();
                                return !bookingDate.isBefore(finalMonthStart) && !bookingDate.isAfter(monthEnd);
                            })
                            .count();
                        values.add(monthlyBookings);
                    }
                }
            } else if ("movie".equals(trendType)) {
                // Query real movie booking data
                List<Movie> movies = movieRepository.findAll();
                for (Movie movie : movies) {
                    labels.add(movie.getTitle());
                    int movieBookings = (int) bookingRepository.findAll().stream()
                        .filter(booking -> booking.getMovie() != null && 
                               booking.getMovie().getMovieId().equals(movie.getMovieId()) &&
                               "PAID".equals(booking.getStatus()))
                        .count();
                    values.add(movieBookings);
                }
            } else { // hall
                // Query real hall booking data
                // Note: You would need to implement CinemaHallRepository to get hall data
                // For now, using a placeholder
                labels.add("Hall A");
                labels.add("Hall B");
                labels.add("Hall C");
                labels.add("Hall D");
                labels.add("Hall E");
                
                // Placeholder values - in real implementation, query actual hall data
                values.add(50);
                values.add(45);
                values.add(60);
                values.add(40);
                values.add(55);
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
