package com.aircompany.hr.model;

public enum CrewPosition {
    PILOT("Pilot"),
    CABIN_CREW("Cabin Crew"),
    GROUND_CREW("Ground Crew");
    
    private final String displayName;
    
    CrewPosition(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}