package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Report;
import com.flixmate.flixmate.api.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Report> generateRevenueReport(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reportService.generateRevenueReport(userDetails.getUsername()));
    }

    @GetMapping("/popularity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Report> generatePopularityReport(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reportService.generatePopularityReport(userDetails.getUsername()));
    }

    @GetMapping("/ticket-sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Report> generateTicketSalesReport(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reportService.generateTicketSalesReport(userDetails.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Report>> getAllReports(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reportService.getAllReports(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Report> getReportById(@PathVariable Integer id) {
        Report report = reportService.getReportById(id);
        if (report != null) {
            return ResponseEntity.ok(report);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createReport(@AuthenticationPrincipal UserDetails userDetails, @RequestParam String type) {
        try {
            Report report = reportService.createReport(userDetails.getUsername(), type);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create report: " + e.getMessage());
        }
    }

    @PostMapping("/daily-sales")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateDailySalesReport(@AuthenticationPrincipal UserDetails userDetails, 
                                                    @RequestParam String date) {
        try {
            Report report = reportService.generateDailySalesReport(userDetails.getUsername(), date);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to generate daily sales report: " + e.getMessage());
        }
    }

    @PostMapping("/monthly")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateMonthlyReport(@AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestParam int year,
                                                 @RequestParam int month) {
        try {
            Report report = reportService.generateMonthlyReport(userDetails.getUsername(), year, month);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to generate monthly report: " + e.getMessage());
        }
    }

    @PostMapping("/movie-performance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateMoviePerformanceReport(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Report report = reportService.generateMoviePerformanceReport(userDetails.getUsername());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to generate movie performance report: " + e.getMessage());
        }
    }

    @PostMapping("/user-activity")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generateUserActivityReport(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            Report report = reportService.generateUserActivityReport(userDetails.getUsername());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to generate user activity report: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteReport(@PathVariable Integer id) {
        boolean deleted = reportService.deleteReport(id);
        if (deleted) {
            return ResponseEntity.ok("Report deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
