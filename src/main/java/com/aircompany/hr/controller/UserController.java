package com.aircompany.hr.controller;

import com.aircompany.sales.dto.*;
import com.aircompany.hr.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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