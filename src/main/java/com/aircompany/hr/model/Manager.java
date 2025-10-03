package com.aircompany.hr.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("MANAGER")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Manager extends User {
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "management_level")
    private String managementLevel;
    
    @Column(name = "team_size")
    private Integer teamSize;
    
    public Manager(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    @Override
    public String getUserType() {
        return "MANAGER";
    }
}
