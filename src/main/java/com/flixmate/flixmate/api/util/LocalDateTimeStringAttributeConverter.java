package com.flixmate.flixmate.api.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Converter(autoApply = false)
public class LocalDateTimeStringAttributeConverter implements AttributeConverter<LocalDateTime, String> {

    private static final DateTimeFormatter SQL_SERVER_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSS");
    private static final DateTimeFormatter STANDARD_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter SIMPLE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        // Trim whitespace
        String trimmed = dbData.trim();
        
        try {
            // Try simple format first (yyyy-MM-dd HH:mm)
            return LocalDateTime.parse(trimmed, SIMPLE_FORMAT);
        } catch (DateTimeParseException e1) {
            try {
                // Try with seconds added
                String withSeconds = trimmed + ":00";
                return LocalDateTime.parse(withSeconds, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (DateTimeParseException e2) {
                try {
                    // Try SQL Server format (with microseconds)
                    return LocalDateTime.parse(trimmed, SQL_SERVER_FORMAT);
                } catch (DateTimeParseException e3) {
                    try {
                        // Try standard ISO format
                        return LocalDateTime.parse(trimmed, STANDARD_FORMAT);
                    } catch (DateTimeParseException e4) {
                        try {
                            // Try parsing without microseconds
                            String cleaned = trimmed.replaceAll("\\.\\d+$", "");
                            return LocalDateTime.parse(cleaned, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        } catch (DateTimeParseException e5) {
                            // Fallback to default parsing
                            return LocalDateTime.parse(trimmed);
                        }
                    }
                }
            }
        }
    }
}
