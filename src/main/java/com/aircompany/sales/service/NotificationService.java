package com.aircompany.sales.service;

import com.aircompany.hr.model.Customer;
import com.aircompany.hr.model.Notification;
import com.aircompany.sales.repository.NotificationRepository;
import com.aircompany.sales.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    /**
     * Create and save a notification for a user
     */
    public Notification createNotification(Long userId, String message, Notification.NotificationType type) {
        Optional<Customer> customerOpt = customerRepository.findById(userId);
        if (customerOpt.isEmpty()) {
            logger.warn("Cannot create notification - user not found: {}", userId);
            return null;
        }
        
        Customer customer = customerOpt.get();
        Notification notification = new Notification(message, type, customer);
        
        notification = notificationRepository.save(notification);
        logger.info("Created notification for user {}: {}", userId, message);
        
        return notification;
    }
    
    /**
     * Get all notifications for a user
     */
    public List<Notification> getUserNotifications(Long userId) {
        Optional<Customer> customerOpt = customerRepository.findById(userId);
        if (customerOpt.isEmpty()) {
            return List.of();
        }
        
        return notificationRepository.findByUserOrderByCreatedAtDesc(customerOpt.get());
    }
    
    /**
     * Get unread notifications for a user
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        Optional<Customer> customerOpt = customerRepository.findById(userId);
        if (customerOpt.isEmpty()) {
            return List.of();
        }
        
        return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(customerOpt.get());
    }
    
    /**
     * Get unread notification count for a user
     */
    public long getUnreadCount(Long userId) {
        Optional<Customer> customerOpt = customerRepository.findById(userId);
        if (customerOpt.isEmpty()) {
            return 0;
        }
        
        return notificationRepository.countByUserAndIsReadFalse(customerOpt.get());
    }
    
    /**
     * Mark a notification as read
     */
    public boolean markAsRead(Long notificationId, Long userId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            return false;
        }
        
        Notification notification = notificationOpt.get();
        
        // Verify the notification belongs to the user
        if (!notification.getUser().getId().equals(userId)) {
            logger.warn("User {} attempted to mark notification {} as read, but it belongs to another user", 
                       userId, notificationId);
            return false;
        }
        
        notification.setIsRead(true);
        notificationRepository.save(notification);
        logger.info("Marked notification {} as read for user {}", notificationId, userId);
        
        return true;
    }
    
    /**
     * Mark all notifications as read for a user
     */
    public int markAllAsRead(Long userId) {
        Optional<Customer> customerOpt = customerRepository.findById(userId);
        if (customerOpt.isEmpty()) {
            return 0;
        }
        
        int count = notificationRepository.markAllAsReadForUser(customerOpt.get());
        logger.info("Marked {} notifications as read for user {}", count, userId);
        
        return count;
    }
    
    // Business logic methods to create specific types of notifications
    
    /**
     * Notify user about ticket issued
     */
    public void notifyTicketIssued(Long userId, String ticketNumber) {
        String message = String.format("Ticket %s confirmed and issued. Check My Tickets.", ticketNumber);
        createNotification(userId, message, Notification.NotificationType.GENERAL);
    }
    
    /**
     * Notify user about points credited
     */
    public void notifyPointsCredited(Long userId, int points, String flightNumber) {
        String message = String.format("+%,d points for flight %s (completed).", points, flightNumber);
        createNotification(userId, message, Notification.NotificationType.GENERAL);
    }
    
    /**
     * Notify user about tier upgrade
     */
    public void notifyTierUpgrade(Long userId, String newTier) {
        String message = String.format("Congratulations! You reached %s tier.", newTier);
        createNotification(userId, message, Notification.NotificationType.GENERAL);
    }
    
    /**
     * Notify user about flight updates
     */
    public void notifyFlightUpdate(Long userId, String flightNumber, String updateMessage) {
        String message = String.format("Flight %s: %s", flightNumber, updateMessage);
        createNotification(userId, message, Notification.NotificationType.FLIGHT_UPDATE);
    }
    
    /**
     * Notify user about offer expiration
     */
    public void notifyOfferExpiring(Long userId, String searchDetails, int minutesRemaining) {
        String message = String.format("Search offer %s will expire in %d minutes.", searchDetails, minutesRemaining);
        createNotification(userId, message, Notification.NotificationType.GENERAL);
    }
    
    /**
     * Notify about maintenance alerts
     */
    public void notifyMaintenanceAlert(Long userId, String details) {
        String message = String.format("Maintenance Alert: %s", details);
        createNotification(userId, message, Notification.NotificationType.MAINTENANCE_ALERT);
    }
    
    /**
     * Notify about security notices
     */
    public void notifySecurityNotice(Long userId, String details) {
        String message = String.format("Security Notice: %s", details);
        createNotification(userId, message, Notification.NotificationType.SECURITY_NOTICE);
    }
    
    /**
     * Notify about schedule changes
     */
    public void notifyScheduleChange(Long userId, String flightNumber, String changeDetails) {
        String message = String.format("Schedule Change for flight %s: %s", flightNumber, changeDetails);
        createNotification(userId, message, Notification.NotificationType.SCHEDULE_CHANGE);
    }
    
    /**
     * Send system announcements to users
     */
    public void notifySystemAnnouncement(Long userId, String announcement) {
        createNotification(userId, announcement, Notification.NotificationType.SYSTEM_ANNOUNCEMENT);
    }
    
    /**
     * Initialize some test notifications for demonstration
     */
    @Transactional
    public void initializeTestNotifications() {
        try {
            // Check if notifications already exist for user 1
            long existingCount = getUnreadCount(1L);
            if (existingCount > 0) {
                logger.info("Test notifications already exist, skipping initialization");
                return;
            }
            
            // Create test notifications
            notifyTicketIssued(1L, "TCK-10218");
            notifyPointsCredited(1L, 1250, "FD-815");
            createNotification(1L, "Search offer BEG → CDG will expire in 10 minutes.", Notification.NotificationType.GENERAL);
            notifyTierUpgrade(1L, "GOLD");
            
            logger.info("Test notifications initialized successfully");
        } catch (Exception e) {
            logger.warn("Could not initialize test notifications: {}", e.getMessage());
        }
    }
    
    /**
     * Initialize some test notifications for development
     */
    public void initializeTestNotifications(Long userId) {
        notifyPointsCredited(userId, 1250, "FD-815");
        createNotification(userId, "Ticket TCK-10218 confirmed and issued. Check My Tickets.", Notification.NotificationType.GENERAL);
        notifyOfferExpiring(userId, "BEG → CDG", 10);
        notifyTierUpgrade(userId, "GOLD");
    }
    
    /**
     * Clear all notifications for a user
     */
    @Transactional
    public void clearNotificationsForUser(Long userId) {
        try {
            Customer customer = customerRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
            
            notificationRepository.deleteByUser(customer);
            logger.info("Cleared all notifications for user {}", userId);
        } catch (Exception e) {
            logger.error("Error clearing notifications for user {}: {}", userId, e.getMessage());
            throw e;
        }
    }
    
    // Customer-specific method aliases for better naming consistency
    public List<Notification> getCustomerNotifications(Long customerId) {
        return getUserNotifications(customerId);
    }
    
    public void clearNotificationsForCustomer(Long customerId) {
        clearNotificationsForUser(customerId);
    }
}