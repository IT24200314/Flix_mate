package com.flixmate.flixmate.api.service;

import com.flixmate.flixmate.api.entity.Notification;
import com.flixmate.flixmate.api.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    /**
     * Create a new notification
     */
    public Notification createNotification(String type, String title, String message) {
        Notification notification = new Notification(type, title, message);
        return notificationRepository.save(notification);
    }
    
    /**
     * Get all notifications with pagination
     */
    public Page<Notification> getAllNotifications(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return notificationRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
    
    /**
     * Get unread notifications count
     */
    public Long getUnreadCount() {
        return notificationRepository.countUnreadNotifications();
    }
    
    /**
     * Mark notification as read
     */
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
    
    /**
     * Create movie added notification
     */
    public void notifyMovieAdded(String movieTitle) {
        createNotification(
            "MOVIE_ADDED",
            "New Movie Added",
            "A new movie '" + movieTitle + "' has been added to the system."
        );
    }
    
    /**
     * Create showtime added notification
     */
    public void notifyShowtimeAdded(String movieTitle, String showtime) {
        createNotification(
            "SHOWTIME_ADDED",
            "New Showtime Added",
            "A new showtime for '" + movieTitle + "' has been scheduled for " + showtime + "."
        );
    }
    
    /**
     * Create staff member added notification
     */
    public void notifyStaffAdded(String staffName) {
        createNotification(
            "STAFF_ADDED",
            "New Staff Member Added",
            "A new staff member '" + staffName + "' has been added to the system."
        );
    }
    
    /**
     * Create staff schedule added notification
     */
    public void notifyStaffScheduleAdded(String staffName, String schedule) {
        createNotification(
            "STAFF_SCHEDULE_ADDED",
            "New Staff Schedule Added",
            "A new schedule has been assigned to staff member '" + staffName + "' for " + schedule + "."
        );
    }
    
    /**
     * Create admin added notification
     */
    public void notifyAdminAdded(String adminEmail) {
        createNotification(
            "ADMIN_ADDED",
            "New Admin Added",
            "A new admin user '" + adminEmail + "' has been added to the system."
        );
    }
}
