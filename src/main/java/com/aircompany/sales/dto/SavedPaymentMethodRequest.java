package com.aircompany.sales.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SavedPaymentMethodRequest {
    
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{4} \\d{4} \\d{4} \\d{4}", message = "Card number must be in format XXXX XXXX XXXX XXXX")
    private String cardNumber;
    
    @NotBlank(message = "Cardholder name is required")
    @Size(min = 2, max = 100, message = "Cardholder name must be between 2 and 100 characters")
    private String cardholderName;
    
    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "\\d{2}/\\d{2}", message = "Expiry date must be in format MM/YY")
    private String expiryDate;
    
    @NotBlank(message = "Card type is required")
    private String cardType; // VISA, MASTERCARD, AMEX, etc.
    
    private boolean isDefault = false;
    
    // Constructors
    public SavedPaymentMethodRequest() {}
    
    // Getters and Setters
    public String getCardNumber() {
        return cardNumber;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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
    
    // Utility method to get masked card number
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}