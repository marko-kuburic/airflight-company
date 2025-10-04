package com.aircompany.sales.dto;

import com.aircompany.hr.model.Customer;
import com.aircompany.sales.model.Loyalty;

import java.time.LocalDateTime;

public class UserProfileResponse {
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String dateOfBirth;
    private String preferredLanguage;
    private LocalDateTime createdAt;
    
    // Loyalty information
    private LoyaltyInfo loyalty;
    
    // Constructors
    public UserProfileResponse() {}
    
    public UserProfileResponse(Customer customer, Loyalty loyalty) {
        this.id = customer.getId();
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.email = customer.getEmail();
        this.phone = customer.getPhone();
        this.dateOfBirth = customer.getDateOfBirth();
        this.preferredLanguage = customer.getPreferredLanguage();
        // this.address = customer.getAddress();
        this.createdAt = customer.getCreatedAt();
        
        if (loyalty != null) {
            this.loyalty = new LoyaltyInfo(loyalty);
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LoyaltyInfo getLoyalty() {
        return loyalty;
    }
    
    public void setLoyalty(LoyaltyInfo loyalty) {
        this.loyalty = loyalty;
    }
    
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getPreferredLanguage() {
        return preferredLanguage;
    }
    
    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
    
    // Nested class for loyalty information
    public static class LoyaltyInfo {
        private Long id;
        private Integer points;
        private String tier;
        
        public LoyaltyInfo() {}
        
        public LoyaltyInfo(Loyalty loyalty) {
            this.id = loyalty.getId();
            this.points = loyalty.getPoints();
            this.tier = loyalty.getTier().toString();
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getPoints() { return points; }
        public void setPoints(Integer points) { this.points = points; }
        public String getTier() { return tier; }
        public void setTier(String tier) { this.tier = tier; }
    }
}