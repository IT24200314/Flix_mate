package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.service.AdminReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportsController {

    @Autowired
    private AdminReportsService adminReportsService;

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
            // For now, return a simple response
            // In a real implementation, you would generate actual PDF/Excel files
            return ResponseEntity.ok(Map.of(
                "message", "Sales report export functionality will be implemented",
                "format", format
            ));
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
            return ResponseEntity.ok(Map.of(
                "message", "Popular movies report export functionality will be implemented",
                "format", format
            ));
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
            return ResponseEntity.ok(Map.of(
                "message", "Payment report export functionality will be implemented",
                "format", format
            ));
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
            return ResponseEntity.ok(Map.of(
                "message", "Trends report export functionality will be implemented",
                "format", format
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to export trends report",
                "message", e.getMessage()
            ));
        }
    }
}
