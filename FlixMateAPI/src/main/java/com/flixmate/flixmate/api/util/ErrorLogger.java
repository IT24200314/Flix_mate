package com.flixmate.flixmate.api.util;

import org.springframework.stereotype.Component;

@Component
public class ErrorLogger {
    
    public static void logApiCall(String apiName, String details) {
        System.out.println("=== " + apiName + " API CALL ===");
        System.out.println("Details: " + details);
        System.out.println("Timestamp: " + java.time.LocalDateTime.now());
    }
    
    public static void logApiSuccess(String apiName, String details) {
        System.out.println("=== " + apiName + " API SUCCESS ===");
        System.out.println("Details: " + details);
        System.out.println("Timestamp: " + java.time.LocalDateTime.now());
    }
    
    public static void logApiError(String apiName, Exception e, String context) {
        System.err.println("=== " + apiName + " API ERROR ===");
        System.err.println("Context: " + context);
        System.err.println("Error Type: " + e.getClass().getSimpleName());
        System.err.println("Error Message: " + e.getMessage());
        System.err.println("Timestamp: " + java.time.LocalDateTime.now());
        System.err.println("Stack Trace:");
        e.printStackTrace();
        System.err.println("=== END " + apiName + " API ERROR ===");
    }
    
    public static void logDatabaseQuery(String query, Object... params) {
        System.out.println("=== DATABASE QUERY ===");
        System.out.println("Query: " + query);
        System.out.println("Parameters: " + java.util.Arrays.toString(params));
        System.out.println("Timestamp: " + java.time.LocalDateTime.now());
    }
    
    public static void logDatabaseResult(String query, int resultCount) {
        System.out.println("=== DATABASE RESULT ===");
        System.out.println("Query: " + query);
        System.out.println("Result Count: " + resultCount);
        System.out.println("Timestamp: " + java.time.LocalDateTime.now());
    }
}
