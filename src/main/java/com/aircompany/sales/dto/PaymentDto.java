package com.aircompany.sales.dto;

import com.aircompany.sales.model.Payment.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO for processing payment
 */
public class PaymentDto {
    
    @NotNull(message = "Reservation ID is required")
    private Long reservationId;
    
    @NotNull(message = "Total amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal totalAmount;
    
    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
    
    // For loyalty points payment
    @Min(value = 0, message = "Loyalty points cannot be negative")
    private Integer loyaltyPointsToUse = 0;
    
    // Cash amount (calculated automatically)
    private BigDecimal cashAmount;
    
    // Payment details (for card payments)
    private String cardNumber;
    private String cardHolderName;
    private String expiryMonth;
    private String expiryYear;
    private String cvv;
    
    // For bank transfer
    private String bankAccount;
    
    // Constructors
    public PaymentDto() {}
    
    public PaymentDto(Long reservationId, BigDecimal totalAmount, PaymentMethod paymentMethod) {
        this.reservationId = reservationId;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.cashAmount = totalAmount;
    }
    
    // Getters and Setters
    public Long getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public Integer getLoyaltyPointsToUse() {
        return loyaltyPointsToUse;
    }
    
    public void setLoyaltyPointsToUse(Integer loyaltyPointsToUse) {
        this.loyaltyPointsToUse = loyaltyPointsToUse;
    }
    
    public BigDecimal getCashAmount() {
        return cashAmount;
    }
    
    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    public String getCardHolderName() {
        return cardHolderName;
    }
    
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }
    
    public String getExpiryMonth() {
        return expiryMonth;
    }
    
    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }
    
    public String getExpiryYear() {
        return expiryYear;
    }
    
    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }
    
    public String getCvv() {
        return cvv;
    }
    
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
    
    public String getBankAccount() {
        return bankAccount;
    }
    
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }
}