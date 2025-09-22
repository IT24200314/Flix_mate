import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class debug_datetime {
    public static void main(String[] args) {
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
        
        // Test the exact error case
        try {
            LocalDateTime.parse(testData);
            System.out.println("Default parsing works");
        } catch (DateTimeParseException e) {
            System.out.println("Default parsing failed: " + e.getMessage());
        }
    }
}
