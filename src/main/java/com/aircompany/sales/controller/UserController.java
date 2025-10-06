package com.aircompany.sales.controller;

import com.aircompany.hr.model.Customer;
import com.aircompany.hr.model.Notification;
import com.aircompany.sales.dto.*;
import com.aircompany.sales.service.UserService;
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
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * User login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for email: {}", loginRequest.getEmail());
            
            Optional<UserProfileResponse> userOptional = userService.authenticateUser(loginRequest);
            if (userOptional.isPresent()) {
                UserProfileResponse user = userOptional.get();
                
                // Create response with user data
                Map<String, Object> response = new HashMap<>();
                response.put("user", user);
                response.put("message", "Login successful");
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Invalid email or password"));
            }
        } catch (Exception e) {
            logger.error("Login failed for email {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(createErrorResponse("Invalid email or password"));
        }
    }
    
    /**
     * User registration
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Registration attempt for email: {}", registerRequest.getEmail());
            
            UserProfileResponse user = userService.registerUser(registerRequest);
            
            // Create response with user data
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("message", "Registration successful");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Registration failed for email {}: {}", registerRequest.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Registration failed: " + e.getMessage()));
        }
    }
    
    /**
     * Get user profile
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId) {
        try {
            logger.info("Getting profile for user ID: {}", userId);
            Optional<UserProfileResponse> profileOptional = userService.getUserProfile(userId);
            if (profileOptional.isPresent()) {
                UserProfileResponse profile = profileOptional.get();
                return ResponseEntity.ok(profile);
            } else {
                logger.warn("No profile found for user ID: {}", userId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting user profile for ID {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("User not found"));
        }
    }
    
    /**
     * Update user profile
     */
    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateUserProfile(@PathVariable Long userId, 
                                             @Valid @RequestBody UpdateUserProfileRequest updateRequest) {
        try {
            logger.info("Updating profile for user ID: {}", userId);
            logger.info("Update request data: firstName={}, lastName={}, email={}, phone={}, dateOfBirth={}, preferredLanguage={}", 
                updateRequest.getFirstName(), updateRequest.getLastName(), updateRequest.getEmail(), 
                updateRequest.getPhone(), updateRequest.getDateOfBirth(), updateRequest.getPreferredLanguage());
            
            UserProfileResponse updatedProfile = userService.updateUserProfile(userId, updateRequest);
            
            logger.info("Profile updated successfully. Response: ID={}, Email={}, DateOfBirth={}, PreferredLanguage={}", 
                updatedProfile.getId(), updatedProfile.getEmail(), updatedProfile.getDateOfBirth(), updatedProfile.getPreferredLanguage());
            
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            logger.error("Error updating user profile for ID {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to update profile: " + e.getMessage()));
        }
    }
    
    /**
     * Get user's saved payment methods
     */
    @GetMapping("/{userId}/payment-methods")
    public ResponseEntity<?> getUserPaymentMethods(@PathVariable Long userId) {
        try {
            List<SavedPaymentMethodResponse> paymentMethods = userService.getUserPaymentMethods(userId);
            return ResponseEntity.ok(paymentMethods);
        } catch (Exception e) {
            logger.error("Error getting payment methods for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get payment methods"));
        }
    }
    
    /**
     * Save a new payment method
     */
    @PostMapping("/{userId}/payment-methods")
    public ResponseEntity<?> savePaymentMethod(@PathVariable Long userId,
                                              @Valid @RequestBody SavedPaymentMethodRequest request) {
        try {
            SavedPaymentMethodResponse savedMethod = userService.savePaymentMethod(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedMethod);
        } catch (Exception e) {
            logger.error("Error saving payment method for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to save payment method: " + e.getMessage()));
        }
    }
    
    /**
     * Delete a saved payment method
     */
    @DeleteMapping("/{userId}/payment-methods/{paymentMethodId}")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable Long userId,
                                                @PathVariable Long paymentMethodId) {
        try {
            userService.deletePaymentMethod(userId, paymentMethodId);
            return ResponseEntity.ok(Map.of("message", "Payment method deleted successfully"));
        } catch (Exception e) {
            logger.error("Error deleting payment method {} for user {}: {}", paymentMethodId, userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to delete payment method: " + e.getMessage()));
        }
    }
    
    /**
     * Get user's loyalty information
     */
    @GetMapping("/{userId}/loyalty")
    public ResponseEntity<?> getUserLoyalty(@PathVariable Long userId) {
        try {
            logger.info("*** Getting loyalty info for user: {}", userId);
            Optional<LoyaltyResponse> loyaltyDataOpt = userService.getUserLoyaltyData(userId);
            if (loyaltyDataOpt.isPresent()) {
                LoyaltyResponse response = loyaltyDataOpt.get();
                logger.info("*** Returning loyalty data: points={}, tier={}", response.getPoints(), response.getTier());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("*** No loyalty data found for user: {}", userId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error getting loyalty info for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Loyalty information not found"));
        }
    }
    
    /**
     * Award points to user (for testing/admin purposes)
     */
    @PostMapping("/{userId}/loyalty/award-points")
    public ResponseEntity<?> awardPoints(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        try {
            logger.info("*** Award points request received for user {}: {}", userId, request);
            
            Integer points = (Integer) request.get("points");
            String reason = (String) request.get("reason");
            
            logger.info("*** Parsed points: {}, reason: {}", points, reason);
            
            if (points == null || points <= 0) {
                logger.warn("*** Invalid points value: {}", points);
                return ResponseEntity.badRequest().body(createErrorResponse("Valid points amount required"));
            }
            
            // Award points manually
            userService.awardManualPoints(userId, points.intValue(), reason != null ? reason : "Manual points award");
            
            logger.info("Awarded {} points to user {} for: {}", points, userId, reason);
            return ResponseEntity.ok(Map.of("message", "Points awarded successfully", "points", points));
        } catch (Exception e) {
            logger.error("Error awarding points to user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to award points: " + e.getMessage()));
        }
    }
    
    /**
     * Get user's reservation history
     */
    @GetMapping("/{userId}/reservations")
    public ResponseEntity<?> getUserReservations(@PathVariable Long userId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        try {
            List<Reservation> reservations = userService.getUserReservations(userId);
            return ResponseEntity.ok(Map.of("reservations", reservations, "totalElements", reservations.size()));
        } catch (Exception e) {
            logger.error("Error getting reservations for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to get reservations"));
        }
    }
    
    /**
     * Change user password
     */
    @PostMapping("/{userId}/change-password")
    public ResponseEntity<?> changePassword(@PathVariable Long userId,
                                          @RequestBody Map<String, String> passwordData) {
        try {
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");
            
            // TODO: Implement password change functionality
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password change functionality coming soon");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error changing password for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to change password: " + e.getMessage()));
        }
    }
    
    /**
     * Logout user (invalidate token)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            // TODO: Implement logout functionality
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged out successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Logout failed"));
        }
    }
    
    /**
     * Get notifications for a user
     */
    @GetMapping("/{userId}/notifications")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getUserNotifications(@PathVariable Long userId) {
        try {
            List<Notification> notifications = notificationService.getUserNotifications(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("notifications", notifications);
            response.put("unreadCount", notificationService.getUnreadCount(userId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching notifications for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to fetch notifications"));
        }
    }
    
    /**
     * Mark a notification as read
     */
    @PutMapping("/{userId}/notifications/{notificationId}/read")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable Long userId, @PathVariable Long notificationId) {
        try {
            boolean success = notificationService.markAsRead(notificationId, userId);
            
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Notification marked as read");
                response.put("unreadCount", notificationService.getUnreadCount(userId));
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(createErrorResponse("Failed to mark notification as read"));
            }
        } catch (Exception e) {
            logger.error("Error marking notification {} as read for user {}: {}", notificationId, userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to mark notification as read"));
        }
    }
    
    /**
     * Mark all notifications as read for a user
     */
    @PutMapping("/{userId}/notifications/read-all")
    public ResponseEntity<?> markAllNotificationsAsRead(@PathVariable Long userId) {
        try {
            int count = notificationService.markAllAsRead(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", String.format("Marked %d notifications as read", count));
            response.put("unreadCount", 0);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error marking all notifications as read for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to mark all notifications as read"));
        }
    }
    
    /**
     * Clear all notifications for a user (for testing purposes)
     */
    @DeleteMapping("/{userId}/notifications/clear")
    public ResponseEntity<?> clearUserNotifications(@PathVariable Long userId) {
        try {
            notificationService.clearNotificationsForUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "All notifications cleared successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error clearing notifications for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to clear notifications"));
        }
    }
    
    /**
     * Initialize test notifications (GET endpoint for easy testing)
     */
    @GetMapping("/{userId}/notifications/init")
    public ResponseEntity<?> initTestNotifications(@PathVariable Long userId) {
        try {
            notificationService.initializeTestNotifications(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Test notifications created");
            response.put("unreadCount", notificationService.getUnreadCount(userId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating test notifications for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create test notifications"));
        }
    }
    
    /**
     * Create various types of notifications via GET for easy testing
     */
    @GetMapping("/{userId}/notifications/demo/{type}")
    public ResponseEntity<?> createDemoNotification(@PathVariable Long userId, @PathVariable String type) {
        try {
            switch (type.toLowerCase()) {
                case "flight":
                    notificationService.notifyFlightUpdate(userId, "FL-456", "Departure delayed by 30 minutes");
                    break;
                case "maintenance":
                    notificationService.notifyMaintenanceAlert(userId, "System maintenance scheduled for tonight 2-4 AM");
                    break;
                case "security":
                    notificationService.notifySecurityNotice(userId, "New security measures in effect at all terminals");
                    break;
                case "schedule":
                    notificationService.notifyScheduleChange(userId, "FL-789", "Departure time moved from 14:30 to 15:00");
                    break;
                case "announcement":
                    notificationService.notifySystemAnnouncement(userId, "Welcome to our improved notification system!");
                    break;
                case "offer":
                    notificationService.notifyOfferExpiring(userId, "BEG → LHR", 15);
                    break;
                case "ticket":
                    notificationService.notifyTicketIssued(userId, "TCK-" + System.currentTimeMillis());
                    break;
                default:
                    return ResponseEntity.badRequest().body(createErrorResponse("Unknown notification type. Use: flight, maintenance, security, schedule, announcement, offer, ticket"));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", type.substring(0,1).toUpperCase() + type.substring(1) + " notification created");
            response.put("unreadCount", notificationService.getUnreadCount(userId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating {} notification for user {}: {}", type, userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create " + type + " notification"));
        }
    }
    
    /**
     * Simulate flight update notification
     */
    @PostMapping("/{userId}/notifications/flight-update")
    public ResponseEntity<?> createFlightUpdateNotification(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String flightNumber = request.get("flightNumber");
            String updateMessage = request.get("updateMessage");
            
            if (flightNumber == null || updateMessage == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Flight number and update message required"));
            }
            
            notificationService.notifyFlightUpdate(userId, flightNumber, updateMessage);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Flight update notification created");
            response.put("unreadCount", notificationService.getUnreadCount(userId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating flight update notification for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create flight update notification"));
        }
    }
    
    /**
     * Simulate schedule change notification
     */
    @PostMapping("/{userId}/notifications/schedule-change")
    public ResponseEntity<?> createScheduleChangeNotification(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String flightNumber = request.get("flightNumber");
            String changeDetails = request.get("changeDetails");
            
            if (flightNumber == null || changeDetails == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Flight number and change details required"));
            }
            
            notificationService.notifyScheduleChange(userId, flightNumber, changeDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Schedule change notification created");
            response.put("unreadCount", notificationService.getUnreadCount(userId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating schedule change notification for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create schedule change notification"));
        }
    }
    
    /**
     * Simulate maintenance alert notification
     */
    @PostMapping("/{userId}/notifications/maintenance-alert")
    public ResponseEntity<?> createMaintenanceAlertNotification(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String details = request.get("details");
            
            if (details == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Maintenance details required"));
            }
            
            notificationService.notifyMaintenanceAlert(userId, details);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Maintenance alert notification created");
            response.put("unreadCount", notificationService.getUnreadCount(userId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating maintenance alert notification for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create maintenance alert notification"));
        }
    }
    
    /**
     * Simulate security notice notification
     */
    @PostMapping("/{userId}/notifications/security-notice")
    public ResponseEntity<?> createSecurityNoticeNotification(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String details = request.get("details");
            
            if (details == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Security notice details required"));
            }
            
            notificationService.notifySecurityNotice(userId, details);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Security notice notification created");
            response.put("unreadCount", notificationService.getUnreadCount(userId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating security notice notification for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create security notice notification"));
        }
    }
    
    /**
     * Simulate system announcement notification
     */
    @PostMapping("/{userId}/notifications/system-announcement")
    public ResponseEntity<?> createSystemAnnouncementNotification(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String announcement = request.get("announcement");
            
            if (announcement == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Announcement text required"));
            }
            
            notificationService.notifySystemAnnouncement(userId, announcement);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "System announcement notification created");
            response.put("unreadCount", notificationService.getUnreadCount(userId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating system announcement notification for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create system announcement notification"));
        }
    }
    
    /**
     * Simulate offer expiring notification
     */
    @PostMapping("/{userId}/notifications/offer-expiring")
    public ResponseEntity<?> createOfferExpiringNotification(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        try {
            String searchDetails = (String) request.get("searchDetails");
            Integer minutesRemaining = (Integer) request.get("minutesRemaining");
            
            if (searchDetails == null || minutesRemaining == null) {
                return ResponseEntity.badRequest().body(createErrorResponse("Search details and minutes remaining required"));
            }
            
            notificationService.notifyOfferExpiring(userId, searchDetails, minutesRemaining);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Offer expiring notification created");
            response.put("unreadCount", notificationService.getUnreadCount(userId));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating offer expiring notification for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Failed to create offer expiring notification"));
        }
    }
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}