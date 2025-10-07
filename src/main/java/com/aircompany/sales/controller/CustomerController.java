package com.aircompany.sales.controller;

import com.aircompany.hr.model.Customer;
import com.aircompany.hr.model.Notification;
import com.aircompany.sales.dto.*;
import com.aircompany.sales.service.CustomerService;
import com.aircompany.sales.service.NotificationService;
import com.aircompany.sales.model.Loyalty;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.aircompany.sales.model.Reservation;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Get customer profile
     */
    @GetMapping("/profile/{customerId}")
    public ResponseEntity<?> getCustomerProfile(@PathVariable Long customerId) {
        try {
            logger.info("Getting profile for customer ID: {}", customerId);
            Optional<UserProfileResponse> profileOptional = customerService.getCustomerProfile(customerId);
            if (profileOptional.isPresent()) {
                UserProfileResponse profile = profileOptional.get();
                return ResponseEntity.ok(profile);
            } else {
                logger.warn("No profile found for customer ID: {}", customerId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting customer profile for ID {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Customer not found"));
        }
    }
    
    /**
     * Update customer profile
     */
    @PutMapping("/profile/{customerId}")
    public ResponseEntity<?> updateCustomerProfile(@PathVariable Long customerId, 
                                             @Valid @RequestBody UpdateUserProfileRequest updateRequest) {
        try {
            logger.info("Updating profile for customer ID: {}", customerId);
            logger.info("Update request data: firstName={}, lastName={}, email={}, phone={}, dateOfBirth={}, preferredLanguage={}", 
                updateRequest.getFirstName(), updateRequest.getLastName(), updateRequest.getEmail(), 
                updateRequest.getPhone(), updateRequest.getDateOfBirth(), updateRequest.getPreferredLanguage());
            
            UserProfileResponse updatedProfile = customerService.updateCustomerProfile(customerId, updateRequest);
            
            logger.info("Profile updated successfully. Response: ID={}, Email={}, DateOfBirth={}, PreferredLanguage={}", 
                updatedProfile.getId(), updatedProfile.getEmail(), updatedProfile.getDateOfBirth(), updatedProfile.getPreferredLanguage());
            
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            logger.error("Error updating customer profile for ID {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to update profile: " + e.getMessage()));
        }
    }
    
    /**
     * Get customer's saved payment methods
     */
    @GetMapping("/{customerId}/payment-methods")
    public ResponseEntity<?> getCustomerPaymentMethods(@PathVariable Long customerId) {
        try {
            List<SavedPaymentMethodResponse> paymentMethods = customerService.getCustomerPaymentMethods(customerId);
            return ResponseEntity.ok(paymentMethods);
        } catch (Exception e) {
            logger.error("Error getting payment methods for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get payment methods"));
        }
    }
    
    /**
     * Save a new payment method
     */
    @PostMapping("/{customerId}/payment-methods")
    public ResponseEntity<?> savePaymentMethod(@PathVariable Long customerId,
                                              @Valid @RequestBody SavedPaymentMethodRequest request) {
        try {
            SavedPaymentMethodResponse savedMethod = customerService.savePaymentMethod(customerId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMethod);
        } catch (Exception e) {
            logger.error("Error saving payment method for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to save payment method: " + e.getMessage()));
        }
    }
    
    /**
     * Delete a saved payment method
     */
    @DeleteMapping("/{customerId}/payment-methods/{paymentMethodId}")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable Long customerId,
                                                @PathVariable Long paymentMethodId) {
        try {
            customerService.deletePaymentMethod(customerId, paymentMethodId);
            return ResponseEntity.ok(Map.of("message", "Payment method deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting payment method {} for customer {}: {}", paymentMethodId, customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to delete payment method: " + e.getMessage()));
        }
    }
    
    /**
     * Get customer's loyalty information
     */
    @GetMapping("/{customerId}/loyalty")
    public ResponseEntity<?> getCustomerLoyalty(@PathVariable Long customerId) {
        try {
            logger.info("*** Getting loyalty info for customer: {}", customerId);
            Optional<LoyaltyResponse> loyaltyDataOpt = customerService.getCustomerLoyaltyData(customerId);
            if (loyaltyDataOpt.isPresent()) {
                LoyaltyResponse response = loyaltyDataOpt.get();
                logger.info("*** Returning loyalty data: points={}, tier={}", response.getPoints(), response.getTier());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("*** No loyalty data found for customer: {}", customerId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting loyalty info for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Loyalty information not found"));
        }
    }
    
    /**
     * Award points to customer (for testing/admin purposes)
     */
    @PostMapping("/{customerId}/loyalty/award-points")
    public ResponseEntity<?> awardPoints(@PathVariable Long customerId, @RequestBody Map<String, Object> request) {
        try {
            logger.info("*** Award points request received for customer {}: {}", customerId, request);
            
            Integer points = (Integer) request.get("points");
            String reason = (String) request.get("reason");
            
            logger.info("*** Parsed points: {}, reason: {}", points, reason);
            
            if (points == null || points <= 0) {
                logger.warn("*** Invalid points value: {}", points);
                return ResponseEntity.badRequest().body(createErrorResponse("Valid points amount required"));
            }
            
            // Award points manually
            customerService.awardManualPoints(customerId, points.intValue(), reason != null ? reason : "Manual points award");
            
            logger.info("Awarded {} points to customer {} for: {}", points, customerId, reason);
            return ResponseEntity.ok(Map.of("message", "Points awarded successfully", "points", points));
        } catch (Exception e) {
            logger.error("Error awarding points to customer {}: {}", customerId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to award points: " + e.getMessage()));
        }
    }
    
    /**
     * Get customer's reservation history
     */
    @GetMapping("/{customerId}/reservations")
    public ResponseEntity<?> getCustomerReservations(@PathVariable Long customerId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        try {
            List<Reservation> reservations = customerService.getCustomerReservations(customerId);
            return ResponseEntity.ok(Map.of("reservations", reservations, "totalElements", reservations.size()));
        } catch (Exception e) {
            logger.error("Error getting reservations for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get reservations"));
        }
    }
    
    /**
     * Get notifications for a customer
     */
    @GetMapping("/{customerId}/notifications")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getCustomerNotifications(@PathVariable Long customerId) {
        try {
            List<Notification> notifications = notificationService.getCustomerNotifications(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("notifications", notifications);
            response.put("unreadCount", notificationService.getUnreadCount(customerId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching notifications for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to fetch notifications"));
        }
    }
    
    /**
     * Mark a notification as read
     */
    @PutMapping("/{customerId}/notifications/{notificationId}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long customerId, @PathVariable Long notificationId) {
        try {
            boolean success = notificationService.markAsRead(notificationId, customerId);
            
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Notification marked as read");
                response.put("unreadCount", notificationService.getUnreadCount(customerId));
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("Failed to mark notification as read"));
            }
        } catch (Exception e) {
            logger.error("Error marking notification {} as read for customer {}: {}", notificationId, customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to mark notification as read"));
        }
    }
    
    /**
     * Mark all notifications as read for a customer
     */
    @PutMapping("/{customerId}/notifications/read-all")
    public ResponseEntity<?> markAllNotificationsAsRead(@PathVariable Long customerId) {
        try {
            int count = notificationService.markAllAsRead(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", String.format("Marked %d notifications as read", count));
            response.put("unreadCount", 0);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error marking all notifications as read for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to mark all notifications as read"));
        }
    }
    
    /**
     * Clear all notifications for a customer (for testing purposes)
     */
    @DeleteMapping("/{customerId}/notifications/clear")
    public ResponseEntity<?> clearCustomerNotifications(@PathVariable Long customerId) {
        try {
            notificationService.clearNotificationsForCustomer(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "All notifications cleared successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error clearing notifications for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to clear notifications"));
        }
    }
    
    /**
     * Initialize test notifications (GET endpoint for easy testing)
     */
    @GetMapping("/{customerId}/notifications/init")
    public ResponseEntity<?> initTestNotifications(@PathVariable Long customerId) {
        try {
            notificationService.initializeTestNotifications(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test notifications created");
            response.put("unreadCount", notificationService.getUnreadCount(customerId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating test notifications for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create test notifications"));
        }
    }
    
    /**
     * Create various types of notifications via GET for easy testing
     */
    @GetMapping("/{customerId}/notifications/demo/{type}")
    public ResponseEntity<?> createDemoNotification(@PathVariable Long customerId, @PathVariable String type) {
        try {
            switch (type.toLowerCase()) {
                case "flight":
                    notificationService.notifyFlightUpdate(customerId, "FL-456", "Departure delayed by 30 minutes");
                    break;
                case "maintenance":
                    notificationService.notifyMaintenanceAlert(customerId, "System maintenance scheduled for tonight 2-4 AM");
                    break;
                case "security":
                    notificationService.notifySecurityNotice(customerId, "New security measures in effect at all terminals");
                    break;
                case "schedule":
                    notificationService.notifyScheduleChange(customerId, "FL-789", "Departure time moved from 14:30 to 15:00");
                    break;
                case "announcement":
                    notificationService.notifySystemAnnouncement(customerId, "Welcome to our improved notification system!");
                    break;
                case "offer":
                    notificationService.notifyOfferExpiring(customerId, "BEG → LHR", 15);
                    break;
                case "ticket":
                    notificationService.notifyTicketIssued(customerId, "TCK-" + System.currentTimeMillis());
                    break;
                default:
                    return ResponseEntity.badRequest().body(createErrorResponse("Unknown notification type. Use: flight, maintenance, security, schedule, announcement, offer, ticket"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", type.substring(0,1).toUpperCase() + type.substring(1) + " notification created");
            response.put("unreadCount", notificationService.getUnreadCount(customerId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating {} notification for customer {}: {}", type, customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create " + type + " notification"));
        }
    }
    
    /**
     * Simulate flight update notification
     */
    @PostMapping("/{customerId}/notifications/flight-update")
    public ResponseEntity<?> createFlightUpdateNotification(@PathVariable Long customerId, @RequestBody Map<String, String> request) {
        try {
            String flightNumber = request.get("flightNumber");
            String updateMessage = request.get("updateMessage");
            
            if (flightNumber == null || updateMessage == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Flight number and update message required"));
            }
            
            notificationService.notifyFlightUpdate(customerId, flightNumber, updateMessage);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Flight update notification created");
            response.put("unreadCount", notificationService.getUnreadCount(customerId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating flight update notification for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create flight update notification"));
        }
    }
    
    /**
     * Simulate schedule change notification
     */
    @PostMapping("/{customerId}/notifications/schedule-change")
    public ResponseEntity<?> createScheduleChangeNotification(@PathVariable Long customerId, @RequestBody Map<String, String> request) {
        try {
            String flightNumber = request.get("flightNumber");
            String changeDetails = request.get("changeDetails");
            
            if (flightNumber == null || changeDetails == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Flight number and change details required"));
            }
            
            notificationService.notifyScheduleChange(customerId, flightNumber, changeDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Schedule change notification created");
            response.put("unreadCount", notificationService.getUnreadCount(customerId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating schedule change notification for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create schedule change notification"));
        }
    }
    
    /**
     * Simulate maintenance alert notification
     */
    @PostMapping("/{customerId}/notifications/maintenance-alert")
    public ResponseEntity<?> createMaintenanceAlertNotification(@PathVariable Long customerId, @RequestBody Map<String, String> request) {
        try {
            String details = request.get("details");
            
            if (details == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Maintenance details required"));
            }
            
            notificationService.notifyMaintenanceAlert(customerId, details);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Maintenance alert notification created");
            response.put("unreadCount", notificationService.getUnreadCount(customerId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating maintenance alert notification for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create maintenance alert notification"));
        }
    }
    
    /**
     * Simulate security notice notification
     */
    @PostMapping("/{customerId}/notifications/security-notice")
    public ResponseEntity<?> createSecurityNoticeNotification(@PathVariable Long customerId, @RequestBody Map<String, String> request) {
        try {
            String details = request.get("details");
            
            if (details == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Security notice details required"));
            }
            
            notificationService.notifySecurityNotice(customerId, details);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Security notice notification created");
            response.put("unreadCount", notificationService.getUnreadCount(customerId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating security notice notification for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create security notice notification"));
        }
    }
    
    /**
     * Simulate system announcement notification
     */
    @PostMapping("/{customerId}/notifications/system-announcement")
    public ResponseEntity<?> createSystemAnnouncementNotification(@PathVariable Long customerId, @RequestBody Map<String, String> request) {
        try {
            String announcement = request.get("announcement");
            
            if (announcement == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Announcement text required"));
            }
            
            notificationService.notifySystemAnnouncement(customerId, announcement);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "System announcement notification created");
            response.put("unreadCount", notificationService.getUnreadCount(customerId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating system announcement notification for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create system announcement notification"));
        }
    }
    
    /**
     * Simulate offer expiring notification
     */
    @PostMapping("/{customerId}/notifications/offer-expiring")
    public ResponseEntity<?> createOfferExpiringNotification(@PathVariable Long customerId, @RequestBody Map<String, Object> request) {
        try {
            String searchDetails = (String) request.get("searchDetails");
            Integer minutesRemaining = (Integer) request.get("minutesRemaining");
            
            if (searchDetails == null || minutesRemaining == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Search details and minutes remaining required"));
            }
            
            notificationService.notifyOfferExpiring(customerId, searchDetails, minutesRemaining);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Offer expiring notification created");
            response.put("unreadCount", notificationService.getUnreadCount(customerId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating offer expiring notification for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create offer expiring notification"));
        }
    }
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}