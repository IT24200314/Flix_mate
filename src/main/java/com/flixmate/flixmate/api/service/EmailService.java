package com.flixmate.flixmate.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;


    public void sendBookingConfirmation(String to, String bookingId, String movieTitle, String showTime) {
        sendBookingConfirmation(to, bookingId, movieTitle, showTime, null, null, null);
    }
    
    public void sendBookingConfirmation(String to, String bookingId, String movieTitle, String showTime, 
                                     String seatNumbers, Double totalAmount, String cinemaName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject("🎬 FlixMate Booking Confirmation - " + movieTitle);
            
            // Load HTML template
            String htmlContent = loadEmailTemplate();
            
            // Replace placeholders with actual values
            htmlContent = htmlContent.replace("{bookingId}", bookingId);
            htmlContent = htmlContent.replace("{movieTitle}", movieTitle);
            htmlContent = htmlContent.replace("{showTime}", formatShowTime(showTime));
            htmlContent = htmlContent.replace("{seatNumbers}", seatNumbers != null ? seatNumbers : "To be confirmed at cinema");
            htmlContent = htmlContent.replace("{totalAmount}", totalAmount != null ? String.format("$%.2f", totalAmount) : "To be confirmed at cinema");
            htmlContent = htmlContent.replace("{cinemaName}", cinemaName != null ? cinemaName : "FlixMate Cinema");
            htmlContent = htmlContent.replace("{websiteUrl}", "http://localhost:8080");
            
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            log.info("HTML booking confirmation email sent successfully to: {}", to);
            
        } catch (MessagingException | IOException ex) {
            log.error("Failed to send HTML booking confirmation email: {}", ex.getMessage());
            // Fallback to simple text email
            sendSimpleBookingConfirmation(to, bookingId, movieTitle, showTime);
        }
    }
    
    private void sendSimpleBookingConfirmation(String to, String bookingId, String movieTitle, String showTime) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("FlixMate Booking Confirmation");
        message.setText("Your booking (ID: " + bookingId + ") for " + movieTitle + " at " + showTime + " has been confirmed. Enjoy your movie!");
        try {
            mailSender.send(message);
            log.info("Simple booking confirmation email sent to: {}", to);
        } catch (MailException ex) {
            log.warn("Failed to send simple booking confirmation email: {}", ex.getMessage());
        }
    }
    
    private String loadEmailTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/booking-confirmation-email.html");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
    
    private String formatShowTime(String showTime) {
        try {
            // Parse the showTime string and format it nicely
            LocalDateTime dateTime = LocalDateTime.parse(showTime);
            return dateTime.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a"));
        } catch (Exception e) {
            return showTime; // Return original if parsing fails
        }
    }

    public void sendScheduleChangeNotification(String to, String movieTitle, String newShowTime) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("FlixMate Schedule Change");
        message.setText("The schedule for " + movieTitle + " has changed to " + newShowTime + ". Please check your booking.");
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.warn("Failed to send schedule change email: {}", ex.getMessage());
        }
    }
}


