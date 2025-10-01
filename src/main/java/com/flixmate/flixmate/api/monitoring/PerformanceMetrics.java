package com.flixmate.flixmate.api.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom performance metrics for FlixMate API
 * Tracks application-specific metrics beyond standard Spring Boot metrics
 */
@Component
public class PerformanceMetrics {
    
    private final MeterRegistry meterRegistry;
    private final AtomicLong activeBookings = new AtomicLong(0);
    private final AtomicLong totalRevenue = new AtomicLong(0);
    private final AtomicLong databaseConnections = new AtomicLong(0);
    
    // Counters for business metrics
    private final Counter bookingCounter;
    private final Counter paymentCounter;
    private final Counter userRegistrationCounter;
    private final Counter movieViewCounter;
    private final Counter errorCounter;
    
    // Timers for performance tracking
    private final Timer bookingProcessTimer;
    private final Timer paymentProcessTimer;
    private final Timer databaseQueryTimer;
    private final Timer authenticationTimer;
    
    @Autowired
    public PerformanceMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // Initialize counters
        this.bookingCounter = Counter.builder("flixmate.bookings.total")
                .description("Total number of bookings created")
                .register(meterRegistry);
                
        this.paymentCounter = Counter.builder("flixmate.payments.total")
                .description("Total number of payments processed")
                .register(meterRegistry);
                
        this.userRegistrationCounter = Counter.builder("flixmate.users.registrations")
                .description("Total number of user registrations")
                .register(meterRegistry);
                
        this.movieViewCounter = Counter.builder("flixmate.movies.views")
                .description("Total number of movie page views")
                .register(meterRegistry);
                
        this.errorCounter = Counter.builder("flixmate.errors.total")
                .description("Total number of application errors")
                .register(meterRegistry);
        
        // Initialize timers
        this.bookingProcessTimer = Timer.builder("flixmate.booking.process.time")
                .description("Time taken to process booking requests")
                .register(meterRegistry);
                
        this.paymentProcessTimer = Timer.builder("flixmate.payment.process.time")
                .description("Time taken to process payment requests")
                .register(meterRegistry);
                
        this.databaseQueryTimer = Timer.builder("flixmate.database.query.time")
                .description("Time taken for database queries")
                .register(meterRegistry);
                
        this.authenticationTimer = Timer.builder("flixmate.auth.process.time")
                .description("Time taken for authentication processes")
                .register(meterRegistry);
        
        // Register gauges for real-time metrics
        Gauge.builder("flixmate.bookings.active", this, metrics -> metrics.activeBookings.get())
                .description("Number of active bookings")
                .register(meterRegistry);
                
        Gauge.builder("flixmate.revenue.total", this, metrics -> metrics.totalRevenue.get())
                .description("Total revenue generated")
                .register(meterRegistry);
                
        Gauge.builder("flixmate.database.connections", this, metrics -> metrics.databaseConnections.get())
                .description("Number of active database connections")
                .register(meterRegistry);
    }
    
    // Business metric methods
    public void incrementBookings() {
        bookingCounter.increment();
    }
    
    public void incrementPayments() {
        paymentCounter.increment();
    }
    
    public void incrementUserRegistrations() {
        userRegistrationCounter.increment();
    }
    
    public void incrementMovieViews() {
        movieViewCounter.increment();
    }
    
    public void incrementErrors() {
        errorCounter.increment();
    }
    
    // Timer methods
    public Timer.Sample startBookingTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void recordBookingTime(Timer.Sample sample) {
        sample.stop(bookingProcessTimer);
    }
    
    public Timer.Sample startPaymentTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void recordPaymentTime(Timer.Sample sample) {
        sample.stop(paymentProcessTimer);
    }
    
    public Timer.Sample startDatabaseQueryTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void recordDatabaseQueryTime(Timer.Sample sample) {
        sample.stop(databaseQueryTimer);
    }
    
    public Timer.Sample startAuthenticationTimer() {
        return Timer.start(meterRegistry);
    }
    
    public void recordAuthenticationTime(Timer.Sample sample) {
        sample.stop(authenticationTimer);
    }
    
    // Gauge update methods
    public void setActiveBookings(long count) {
        activeBookings.set(count);
    }
    
    public void addRevenue(double amount) {
        totalRevenue.addAndGet((long) (amount * 100)); // Store as cents
    }
    
    public void setDatabaseConnections(long count) {
        databaseConnections.set(count);
    }
    
    // Utility methods for monitoring
    public long getActiveBookings() {
        return activeBookings.get();
    }
    
    public double getTotalRevenue() {
        return totalRevenue.get() / 100.0; // Convert from cents
    }
    
    public long getDatabaseConnections() {
        return databaseConnections.get();
    }
    
    /**
     * Record sample timing data for demonstration
     */
    public void recordSampleTimings() {
        // Record some sample booking times
        bookingProcessTimer.record(120, java.util.concurrent.TimeUnit.MILLISECONDS);
        bookingProcessTimer.record(180, java.util.concurrent.TimeUnit.MILLISECONDS);
        bookingProcessTimer.record(95, java.util.concurrent.TimeUnit.MILLISECONDS);
        
        // Record some sample payment times
        paymentProcessTimer.record(250, java.util.concurrent.TimeUnit.MILLISECONDS);
        paymentProcessTimer.record(320, java.util.concurrent.TimeUnit.MILLISECONDS);
        paymentProcessTimer.record(190, java.util.concurrent.TimeUnit.MILLISECONDS);
        
        // Record some sample database query times
        databaseQueryTimer.record(45, java.util.concurrent.TimeUnit.MILLISECONDS);
        databaseQueryTimer.record(80, java.util.concurrent.TimeUnit.MILLISECONDS);
        databaseQueryTimer.record(35, java.util.concurrent.TimeUnit.MILLISECONDS);
        
        // Record some sample authentication times
        authenticationTimer.record(90, java.util.concurrent.TimeUnit.MILLISECONDS);
        authenticationTimer.record(120, java.util.concurrent.TimeUnit.MILLISECONDS);
        authenticationTimer.record(75, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
}
