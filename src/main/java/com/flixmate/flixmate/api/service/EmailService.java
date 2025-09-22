package com.flixmate.flixmate.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;


    public void sendBookingConfirmation(String to, String bookingId, String movieTitle, String showTime) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("FlixMate Booking Confirmation");
        message.setText("Your booking (ID: " + bookingId + ") for " + movieTitle + " at " + showTime + " has been confirmed. Enjoy your movie!");
        try {
            mailSender.send(message);
        } catch (MailException ex) {
            log.warn("Failed to send booking confirmation email: {}", ex.getMessage());
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


