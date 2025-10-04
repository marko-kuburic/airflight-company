package com.aircompany.sales.controller;

import com.aircompany.hr.model.Customer;
import com.aircompany.sales.dto.*;
import com.aircompany.sales.service.UserService;
import com.aircompany.sales.model.Loyalty;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                logger.info("Profile found: ID={}, Email={}, FirstName={}", profile.getId(), profile.getEmail(), profile.getFirstName());
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
            UserProfileResponse updatedProfile = userService.updateUserProfile(userId, updateRequest);
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
            // TODO: Implement loyalty functionality  
            return ResponseEntity.ok(Map.of("tier", "BRONZE", "points", 0));
        } catch (Exception e) {
            logger.error("Error getting loyalty info for user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse("Loyalty information not found"));
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
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}