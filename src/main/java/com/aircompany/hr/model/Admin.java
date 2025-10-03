package com.aircompany.hr.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {
    
    public Admin(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    @Override
    public String getUserType() {
        return "ADMIN";
    }
}
