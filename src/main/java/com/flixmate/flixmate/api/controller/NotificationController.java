package com.flixmate.flixmate.api.controller;

import com.flixmate.flixmate.api.entity.Notification;
import com.flixmate.flixmate.api.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Get all notifications with pagination
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            Page<Notification> notifications = notificationService.getAllNotifications(page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", notifications.getContent());
            response.put("totalPages", notifications.getTotalPages());
            response.put("totalElements", notifications.getTotalElements());
            response.put("currentPage", page);
            response.put("size", size);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to fetch notifications",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Get unread notifications count
     */
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUnreadCount() {
        try {
            Long count = notificationService.getUnreadCount();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to get notification count",
                "message", e.getMessage()
            ));
        }
    }
    
    /**
     * Mark notification as read
     */
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> markAsRead(@PathVariable Long notificationId) {
        try {
            Notification notification = notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(Map.of(
                "message", "Notification marked as read",
                "notification", notification
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Failed to mark notification as read",
                "message", e.getMessage()
            ));
        }
    }
}
