package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.service.AdminReportsService;
import com.flixmate.flixmate.api.service.PdfReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportsController {

    @Autowired
    private AdminReportsService adminReportsService;
    
    @Autowired
    private PdfReportService pdfReportService;

    @PostMapping("/sales")
    public ResponseEntity<?> generateSalesReport(@RequestBody Map<String, Object> request) {
        try {
            String period = (String) request.get("period");
            String startDate = (String) request.get("startDate");
            String endDate = (String) request.get("endDate");
            
            Map<String, Object> report = adminReportsService.generateSalesReport(period, startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to generate sales report",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/popular-movies")
    public ResponseEntity<?> generatePopularMoviesReport(@RequestBody Map<String, Object> request) {
        try {
            Integer topN = (Integer) request.get("topN");
            String sortBy = (String) request.get("sortBy");
            String period = (String) request.get("period");
            
            Map<String, Object> report = adminReportsService.generatePopularMoviesReport(topN, sortBy, period);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to generate popular movies report",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/payments")
    public ResponseEntity<?> generatePaymentReport(@RequestBody Map<String, Object> request) {
        try {
            String status = (String) request.get("status");
            String startDate = (String) request.get("startDate");
            String endDate = (String) request.get("endDate");
            
            Map<String, Object> report = adminReportsService.generatePaymentReport(status, startDate, endDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to generate payment report",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/trends")
    public ResponseEntity<?> generateTrendsReport(@RequestBody Map<String, Object> request) {
        try {
            String trendType = (String) request.get("trendType");
            String period = (String) request.get("period");
            String startDate = (String) request.get("startDate");
            
            Map<String, Object> report = adminReportsService.generateTrendsReport(trendType, period, startDate);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to generate trends report",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/sales/export")
    public ResponseEntity<?> exportSalesReport(@RequestParam String format) {
        try {
            Map<String, Object> reportData = adminReportsService.generateSalesReport("monthly", "2024-01-01", "2024-12-31");
            
            if ("pdf".equals(format)) {
                // Generate PDF content
                byte[] pdfBytes = pdfReportService.generateSalesReportPdf(reportData);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "sales-report.pdf");
                headers.setContentLength(pdfBytes.length);
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
            } else if ("excel".equals(format) || "csv".equals(format)) {
                // Generate CSV content
                String csvContent = generateSalesCSV(reportData);
                byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "sales-report.csv");
                headers.setContentLength(csvBytes.length);
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvBytes);
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Unsupported format",
                    "message", "Only PDF and CSV formats are supported"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to export sales report",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/popular-movies/export")
    public ResponseEntity<?> exportPopularMoviesReport(@RequestParam String format) {
        try {
            Map<String, Object> reportData = adminReportsService.generatePopularMoviesReport(10, "bookings", "all");
            
            if ("pdf".equals(format)) {
                // Generate PDF content
                byte[] pdfBytes = pdfReportService.generatePopularMoviesReportPdf(reportData);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "popular-movies-report.pdf");
                headers.setContentLength(pdfBytes.length);
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
            } else if ("excel".equals(format) || "csv".equals(format)) {
                // Generate CSV content
                String csvContent = generatePopularMoviesCSV(reportData);
                byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "popular-movies-report.csv");
                headers.setContentLength(csvBytes.length);
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvBytes);
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Unsupported format",
                    "message", "Only PDF and CSV formats are supported"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to export popular movies report",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/payments/export")
    public ResponseEntity<?> exportPaymentReport(@RequestParam String format) {
        try {
            Map<String, Object> reportData = adminReportsService.generatePaymentReport("all", "2024-01-01", "2024-12-31");
            
            if ("pdf".equals(format)) {
                // Generate PDF content
                byte[] pdfBytes = pdfReportService.generatePaymentReportPdf(reportData);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "payment-report.pdf");
                headers.setContentLength(pdfBytes.length);
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
            } else if ("excel".equals(format) || "csv".equals(format)) {
                // Generate CSV content
                String csvContent = generatePaymentCSV(reportData);
                byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "payment-report.csv");
                headers.setContentLength(csvBytes.length);
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvBytes);
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Unsupported format",
                    "message", "Only PDF and CSV formats are supported"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to export payment report",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/trends/export")
    public ResponseEntity<?> exportTrendsReport(@RequestParam String format) {
        try {
            Map<String, Object> reportData = adminReportsService.generateTrendsReport("time", "monthly", "2024-01-01");
            
            if ("pdf".equals(format)) {
                // Generate PDF content
                byte[] pdfBytes = pdfReportService.generateTrendsReportPdf(reportData);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                headers.setContentDispositionFormData("attachment", "trends-report.pdf");
                headers.setContentLength(pdfBytes.length);
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
            } else if ("excel".equals(format) || "csv".equals(format)) {
                // Generate CSV content
                String csvContent = generateTrendsCSV(reportData);
                byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "trends-report.csv");
                headers.setContentLength(csvBytes.length);
                
                return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvBytes);
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Unsupported format",
                    "message", "Only PDF and CSV formats are supported"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to export trends report",
                "message", e.getMessage()
            ));
        }
    }

    // CSV Generation Methods
    private String generateSalesCSV(Map<String, Object> reportData) {
        StringBuilder csv = new StringBuilder();
        csv.append("Sales Report\n");
        csv.append("Total Revenue,Total Bookings,Average Ticket Price,Growth Rate\n");
        csv.append(reportData.get("totalRevenue")).append(",");
        csv.append(reportData.get("totalBookings")).append(",");
        csv.append(reportData.get("averageTicketPrice")).append(",");
        csv.append(reportData.get("growthRate")).append("\n\n");
        
        // Add time series data
        csv.append("Date,Revenue\n");
        @SuppressWarnings("unchecked")
        java.util.List<String> labels = (java.util.List<String>) reportData.get("labels");
        @SuppressWarnings("unchecked")
        java.util.List<Double> revenueData = (java.util.List<Double>) reportData.get("revenueData");
        
        if (labels != null && revenueData != null) {
            for (int i = 0; i < labels.size() && i < revenueData.size(); i++) {
                csv.append(labels.get(i)).append(",").append(revenueData.get(i)).append("\n");
            }
        }
        
        return csv.toString();
    }

    private String generatePopularMoviesCSV(Map<String, Object> reportData) {
        StringBuilder csv = new StringBuilder();
        csv.append("Popular Movies Report\n");
        csv.append("Rank,Movie Title,Bookings,Revenue,Rating,Release Year\n");
        
        @SuppressWarnings("unchecked")
        java.util.List<Map<String, Object>> movies = (java.util.List<Map<String, Object>>) reportData.get("movies");
        
        if (movies != null) {
            for (int i = 0; i < movies.size(); i++) {
                Map<String, Object> movie = movies.get(i);
                csv.append(i + 1).append(",");
                csv.append("\"").append(movie.get("title")).append("\",");
                csv.append(movie.get("bookings")).append(",");
                csv.append(movie.get("revenue")).append(",");
                csv.append(movie.get("rating")).append(",");
                csv.append(movie.get("releaseYear")).append("\n");
            }
        }
        
        return csv.toString();
    }

    private String generatePaymentCSV(Map<String, Object> reportData) {
        StringBuilder csv = new StringBuilder();
        csv.append("Payment Report\n");
        csv.append("Total Payments,Successful,Failed,Pending,Success Rate\n");
        csv.append(reportData.get("totalPayments")).append(",");
        csv.append(reportData.get("successCount")).append(",");
        csv.append(reportData.get("failedCount")).append(",");
        csv.append(reportData.get("pendingCount")).append(",");
        csv.append(reportData.get("successRate")).append("\n");
        
        return csv.toString();
    }

    private String generateTrendsCSV(Map<String, Object> reportData) {
        StringBuilder csv = new StringBuilder();
        csv.append("Booking Trends Report\n");
        csv.append("Period,Bookings\n");
        
        @SuppressWarnings("unchecked")
        java.util.List<String> labels = (java.util.List<String>) reportData.get("labels");
        @SuppressWarnings("unchecked")
        java.util.List<Double> values = (java.util.List<Double>) reportData.get("values");
        
        if (labels != null && values != null) {
            for (int i = 0; i < labels.size() && i < values.size(); i++) {
                csv.append(labels.get(i)).append(",").append(values.get(i)).append("\n");
            }
        }
        
        return csv.toString();
    }
}
