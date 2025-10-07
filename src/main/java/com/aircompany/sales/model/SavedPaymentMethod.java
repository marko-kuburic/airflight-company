package com.aircompany.sales.model;

import com.aircompany.hr.model.Customer;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_payment_methods")
public class SavedPaymentMethod {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    
    @Column(name = "encrypted_card_number", nullable = false)
    private String encryptedCardNumber;
    
    @Column(name = "masked_card_number", nullable = false)
    private String maskedCardNumber;
    
    @Column(name = "cardholder_name", nullable = false, length = 100)
    private String cardholderName;
    
    @Column(name = "expiry_date", nullable = false, length = 5)
    private String expiryDate;
    
    @Column(name = "card_type", nullable = false, length = 20)
    private String cardType;
    
    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public SavedPaymentMethod() {}
    
    public SavedPaymentMethod(Customer customer, String encryptedCardNumber, String maskedCardNumber,
                            String cardholderName, String expiryDate, String cardType, boolean isDefault) {
        this.customer = customer;
        this.encryptedCardNumber = encryptedCardNumber;
        this.maskedCardNumber = maskedCardNumber;
        this.cardholderName = cardholderName;
        this.expiryDate = expiryDate;
        this.cardType = cardType;
        this.isDefault = isDefault;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public String getEncryptedCardNumber() {
        return encryptedCardNumber;
    }
    
    public void setEncryptedCardNumber(String encryptedCardNumber) {
        this.encryptedCardNumber = encryptedCardNumber;
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
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}