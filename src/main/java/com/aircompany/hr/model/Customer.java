package com.aircompany.hr.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("CUSTOMER")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Customer extends User {
    
    @Column(name = "phone")
    private String phone;
    
    @Column(name = "date_of_birth")
    private String dateOfBirth;
    
    @Column(name = "preferred_language")
    private String preferredLanguage;
    
    // TODO: Add loyalty relationship when sales module is ready
    // @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Loyalty loyalty;
    
    public Customer(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    @Override
    public String getUserType() {
        return "CUSTOMER";
    }
}
