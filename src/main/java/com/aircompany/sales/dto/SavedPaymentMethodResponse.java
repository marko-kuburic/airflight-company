package com.aircompany.sales.dto;

import java.time.LocalDateTime;

public class SavedPaymentMethodResponse {
    
    private Long id;
    private String maskedCardNumber;
    private String cardholderName;
    private String expiryDate;
    private String cardType;
    private boolean isDefault;
    private LocalDateTime createdAt;
    
    // Constructors
    public SavedPaymentMethodResponse() {}
    
    public SavedPaymentMethodResponse(Long id, String maskedCardNumber, String cardholderName, 
                                    String expiryDate, String cardType, boolean isDefault, LocalDateTime createdAt) {
        this.id = id;
        this.maskedCardNumber = maskedCardNumber;
        this.cardholderName = cardholderName;
        this.expiryDate = expiryDate;
        this.cardType = cardType;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }
    
    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }
    
    public String getCardholderName() {
        return cardholderName;
    }
    
    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }
    
    public String getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public String getCardType() {
        return cardType;
    }
    
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}