package com.flixmate.flixmate.api.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

public class DateTimeConverterTest {

    @Test
    public void testDateTimeParsing() {
        String testData = "2025-09-17 18:00";
        
        // Test different formats
        String[] patterns = {
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSSSSSS"
        };
        
        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                LocalDateTime result = LocalDateTime.parse(testData, formatter);
                System.out.println("Pattern '" + pattern + "' works: " + result);
            } catch (DateTimeParseException e) {
                System.out.println("Pattern '" + pattern + "' failed: " + e.getMessage());
            }
        }
        
        // Test the converter
        LocalDateTimeStringAttributeConverter converter = new LocalDateTimeStringAttributeConverter();
        try {
            LocalDateTime result = converter.convertToEntityAttribute(testData);
            System.out.println("Converter works: " + result);
        } catch (Exception e) {
            System.out.println("Converter failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
