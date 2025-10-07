package com.aircompany.sales.dto;

import com.aircompany.sales.model.Loyalty;

import java.util.List;
import java.util.Map;

public class LoyaltyResponse {
    
    private Integer points;
    private String tier;
    private List<EarningHistoryItem> earningHistory;
    private Map<String, TierThreshold> tierThresholds;
    
    public LoyaltyResponse() {}
    
    public LoyaltyResponse(Loyalty loyalty, List<EarningHistoryItem> earningHistory) {
        this.points = loyalty.getPoints();
        this.tier = loyalty.getTier().name();
        this.earningHistory = earningHistory;
        
        // Define tier thresholds
        this.tierThresholds = Map.of(
            "BRONZE", new TierThreshold(0, 10000),
            "SILVER", new TierThreshold(10000, 25000),
            "GOLD", new TierThreshold(25000, 50000),
            "PLATINUM", new TierThreshold(50000, 100000),
            "DIAMOND", new TierThreshold(100000, Integer.MAX_VALUE)
        );
    }
    
    // Getters and Setters
    public Integer getPoints() {
        return points;
    }
    
    public void setPoints(Integer points) {
        this.points = points;
    }
    
    public String getTier() {
        return tier;
    }
    
    public void setTier(String tier) {
        this.tier = tier;
    }
    
    public List<EarningHistoryItem> getEarningHistory() {
        return earningHistory;
    }
    
    public void setEarningHistory(List<EarningHistoryItem> earningHistory) {
        this.earningHistory = earningHistory;
    }
    
    public Map<String, TierThreshold> getTierThresholds() {
        return tierThresholds;
    }
    
    public void setTierThresholds(Map<String, TierThreshold> tierThresholds) {
        this.tierThresholds = tierThresholds;
    }
    
    // Inner classes
    public static class EarningHistoryItem {
        private String date;
        private String flight;
        private String status;
        private Integer points;
        
        public EarningHistoryItem() {}
        
        public EarningHistoryItem(String date, String flight, String status, Integer points) {
            this.date = date;
            this.flight = flight;
            this.status = status;
            this.points = points;
        }
        
        // Getters and Setters
        public String getDate() {
            return date;
        }
        
        public void setDate(String date) {
            this.date = date;
        }
        
        public String getFlight() {
            return flight;
        }
        
        public void setFlight(String flight) {
            this.flight = flight;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public Integer getPoints() {
            return points;
        }
        
        public void setPoints(Integer points) {
            this.points = points;
        }
    }
    
    public static class TierThreshold {
        private Integer min;
        private Integer max;
        
        public TierThreshold() {}
        
        public TierThreshold(Integer min, Integer max) {
            this.min = min;
            this.max = max;
        }
        
        // Getters and Setters
        public Integer getMin() {
            return min;
        }
        
        public void setMin(Integer min) {
            this.min = min;
        }
        
        public Integer getMax() {
            return max;
        }
        
        public void setMax(Integer max) {
            this.max = max;
        }
    }
}