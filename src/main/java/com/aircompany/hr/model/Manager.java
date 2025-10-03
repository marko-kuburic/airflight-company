package com.aircompany.hr.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("MANAGER")
public class Manager extends User {
    
    @Column(name = "department")
    private String department;
    
    @Column(name = "management_level")
    private String managementLevel;
    
    @Column(name = "team_size")
    private Integer teamSize;
    
    // Constructors
    public Manager() {
        super();
    }
    
    public Manager(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
    }
    
    // Getters and Setters
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getManagementLevel() {
        return managementLevel;
    }
    
    public void setManagementLevel(String managementLevel) {
        this.managementLevel = managementLevel;
    }
    
    public Integer getTeamSize() {
        return teamSize;
    }
    
    public void setTeamSize(Integer teamSize) {
        this.teamSize = teamSize;
    }
    
    @Override
    public String getUserType() {
        return "MANAGER";
    }
}
