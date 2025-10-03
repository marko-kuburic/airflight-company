package com.aircompany.hr.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    
    // Constructors
    public Admin() {
        super();
    }
    
    public Admin(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    @Override
    public String getUserType() {
        return "ADMIN";
    }
}
