package com.flixmate.flixmate.api.repository;

import com.flixmate.flixmate.api.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications ordered by creation date (newest first)
     */
    Page<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Count unread notifications
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.read = false")
    Long countUnreadNotifications();
    
    /**
     * Find unread notifications
     */
    Page<Notification> findByReadFalseOrderByCreatedAtDesc(Pageable pageable);
}
