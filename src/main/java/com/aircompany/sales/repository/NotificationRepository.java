package com.aircompany.sales.repository;

import com.aircompany.hr.model.Customer;
import com.aircompany.hr.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications for a user, ordered by creation date (newest first)
     */
    List<Notification> findByUserOrderByCreatedAtDesc(Customer user);
    
    /**
     * Find unread notifications for a user, ordered by creation date (newest first)
     */
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(Customer user);
    
    /**
     * Count unread notifications for a user
     */
    long countByUserAndIsReadFalse(Customer user);
    
    /**
     * Mark all notifications as read for a user
     */
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    int markAllAsReadForUser(Customer user);
    
    /**
     * Delete all notifications for a user
     */
    @Modifying
    @Transactional
    void deleteByUser(Customer user);
}