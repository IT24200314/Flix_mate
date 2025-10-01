package com.flixmate.flixmate.api.monitoring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Scheduled tasks for performance monitoring
 * Automatically updates metrics and performs health checks
 */
@Component
public class PerformanceScheduler {
    
    @Autowired
    private PerformanceMetrics performanceMetrics;
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * Update active bookings count every 30 seconds
     */
    @Scheduled(fixedRate = 30000)
    public void updateActiveBookings() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "SELECT COUNT(*) FROM bookings WHERE status = 'CONFIRMED' AND booking_date > DATEADD(day, -1, GETDATE())")) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                performanceMetrics.setActiveBookings(rs.getLong(1));
            }
        } catch (SQLException e) {
            // Log error but don't throw exception to avoid breaking the scheduler
            System.err.println("Error updating active bookings: " + e.getMessage());
        }
    }
    
    /**
     * Update total revenue every minute
     */
    @Scheduled(fixedRate = 60000)
    public void updateTotalRevenue() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "SELECT ISNULL(SUM(amount), 0) FROM payments WHERE status = 'COMPLETED'")) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double totalRevenue = rs.getDouble(1);
                performanceMetrics.addRevenue(totalRevenue);
            }
        } catch (SQLException e) {
            System.err.println("Error updating total revenue: " + e.getMessage());
        }
    }
    
    /**
     * Update database connection count every 15 seconds
     */
    @Scheduled(fixedRate = 15000)
    public void updateDatabaseConnections() {
        try (Connection connection = dataSource.getConnection()) {
            // Test the connection and set to 1 if valid, 0 if not
            if (connection.isValid(5)) {
                performanceMetrics.setDatabaseConnections(1);
            } else {
                performanceMetrics.setDatabaseConnections(0);
            }
        } catch (SQLException e) {
            System.err.println("Error updating database connections: " + e.getMessage());
            performanceMetrics.setDatabaseConnections(0);
        }
    }
    
    /**
     * Perform system health check every 2 minutes
     */
    @Scheduled(fixedRate = 120000)
    public void performHealthCheck() {
        try {
            // Record some sample timing data for demonstration
            performanceMetrics.recordSampleTimings();
            
            // Check memory usage
            long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long maxMemory = Runtime.getRuntime().maxMemory();
            double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
            
            if (memoryUsagePercent > 90) {
                System.err.println("WARNING: Memory usage is critically high: " + 
                    String.format("%.1f", memoryUsagePercent) + "%");
            }
            
            // Check database connectivity
            try (Connection connection = dataSource.getConnection()) {
                if (!connection.isValid(5)) {
                    System.err.println("WARNING: Database connection is not valid");
                }
            }
            
            // Log performance metrics
            System.out.println("Performance Check - Active Bookings: " + performanceMetrics.getActiveBookings() + 
                ", Revenue: $" + String.format("%.2f", performanceMetrics.getTotalRevenue()) + 
                ", DB Connections: " + performanceMetrics.getDatabaseConnections());
                
        } catch (Exception e) {
            System.err.println("Error during health check: " + e.getMessage());
        }
    }
    
    /**
     * Clean up old metrics data every hour
     */
    @Scheduled(fixedRate = 3600000)
    public void cleanupOldMetrics() {
        try {
            // This could be extended to clean up old log files, temporary data, etc.
            System.out.println("Performing metrics cleanup at: " + System.currentTimeMillis());
        } catch (Exception e) {
            System.err.println("Error during metrics cleanup: " + e.getMessage());
        }
    }
}
