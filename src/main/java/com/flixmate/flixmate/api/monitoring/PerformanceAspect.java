package com.flixmate.flixmate.api.monitoring;

import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AOP aspect for automatic performance monitoring
 * Intercepts method calls to track execution times and performance metrics
 */
@Aspect
@Component
public class PerformanceAspect {
    
    @Autowired
    private PerformanceMetrics performanceMetrics;
    
    /**
     * Monitor booking-related operations
     */
    @Around("execution(* com.flixmate.flixmate.api.service.BookingService.*(..))")
    public Object monitorBookingOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = performanceMetrics.startBookingTimer();
        try {
            Object result = joinPoint.proceed();
            performanceMetrics.incrementBookings();
            return result;
        } catch (Exception e) {
            performanceMetrics.incrementErrors();
            throw e;
        } finally {
            performanceMetrics.recordBookingTime(sample);
        }
    }
    
    /**
     * Monitor payment-related operations
     */
    @Around("execution(* com.flixmate.flixmate.api.service.PaymentService.*(..))")
    public Object monitorPaymentOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = performanceMetrics.startPaymentTimer();
        try {
            Object result = joinPoint.proceed();
            performanceMetrics.incrementPayments();
            return result;
        } catch (Exception e) {
            performanceMetrics.incrementErrors();
            throw e;
        } finally {
            performanceMetrics.recordPaymentTime(sample);
        }
    }
    
    /**
     * Monitor database operations
     */
    @Around("execution(* com.flixmate.flixmate.api.repository.*.*(..))")
    public Object monitorDatabaseOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = performanceMetrics.startDatabaseQueryTimer();
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            performanceMetrics.incrementErrors();
            throw e;
        } finally {
            performanceMetrics.recordDatabaseQueryTime(sample);
        }
    }
    
    /**
     * Monitor authentication operations
     */
    @Around("execution(* com.flixmate.flixmate.api.service.UserService.authenticate*(..)) || " +
            "execution(* com.flixmate.flixmate.api.util.JwtUtil.*(..))")
    public Object monitorAuthenticationOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        Timer.Sample sample = performanceMetrics.startAuthenticationTimer();
        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            performanceMetrics.incrementErrors();
            throw e;
        } finally {
            performanceMetrics.recordAuthenticationTime(sample);
        }
    }
    
    /**
     * Monitor movie view operations
     */
    @Around("execution(* com.flixmate.flixmate.api.controller.MovieController.*(..)) || " +
            "execution(* com.flixmate.flixmate.api.controller.PublicMovieController.*(..))")
    public Object monitorMovieOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object result = joinPoint.proceed();
            performanceMetrics.incrementMovieViews();
            return result;
        } catch (Exception e) {
            performanceMetrics.incrementErrors();
            throw e;
        }
    }
    
    /**
     * Monitor user registration operations
     */
    @Around("execution(* com.flixmate.flixmate.api.service.UserService.register*(..))")
    public Object monitorUserRegistrationOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object result = joinPoint.proceed();
            performanceMetrics.incrementUserRegistrations();
            return result;
        } catch (Exception e) {
            performanceMetrics.incrementErrors();
            throw e;
        }
    }
}
