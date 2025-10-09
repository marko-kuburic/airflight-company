package com.aircompany.flight.util;

public class DistanceCalculator {
    
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double AVERAGE_SPEED_KMH = 800.0; // Average cruising speed for commercial aircraft

    /**
     * Calculate distance between two points using Haversine formula
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double rLat1 = Math.toRadians(lat1);
        double rLat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(rLat1) * Math.cos(rLat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculate flight duration in minutes based on distance
     * Assumes average cruising speed of 800 km/h
     * Adds 30 minutes for takeoff/landing
     */
    public static int calculateDurationMinutes(double distanceKm) {
        // Calculate base flight time
        double flightTimeHours = distanceKm / AVERAGE_SPEED_KMH;
        
        // Add 30 minutes for takeoff and landing
        double totalTimeHours = flightTimeHours + 0.5;
        
        return (int) Math.ceil(totalTimeHours * 60);
    }
}

