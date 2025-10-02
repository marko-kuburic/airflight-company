package com.aircompany.hr.model;

import jakarta.persistence.*;
import com.aircompany.sales.model.Loyalty;

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "date_of_birth")
    private String dateOfBirth;
    
    @Column(name = "preferred_language")
    private String preferredLanguage;
    
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Loyalty loyalty;
    
    // Constructors
    public Customer() {
        super();
    }
    
    public Customer(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    // Getters and Setters
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
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
    
    public Loyalty getLoyalty() {
        return loyalty;
    }
    
    public void setLoyalty(Loyalty loyalty) {
        this.loyalty = loyalty;
    }
    
    @Override
    public String getUserType() {
        return "CUSTOMER";
    }
}
