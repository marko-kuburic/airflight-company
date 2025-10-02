package com.aircompany.sales.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.aircompany.flight.model.Route;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sale_stats")
@EntityListeners(AuditingEntityListener.class)
public class SaleStats {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "total_revenue", nullable = false)
    private BigDecimal totalRevenue;
    
    @NotNull
    @Column(name = "total_tickets_sold", nullable = false)
    private Integer totalTicketsSold;
    
    @NotNull
    @Column(name = "total_flights", nullable = false)
    private Integer totalFlights;
    
    @Column(name = "average_ticket_price")
    private BigDecimal averageTicketPrice;
    
    @Column(name = "occupancy_rate")
    private BigDecimal occupancyRate;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;
    
    @OneToMany(mappedBy = "saleStats", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CabinClass> cabinClasses = new ArrayList<>();
    
    // Constructors
    public SaleStats() {}
    
    public SaleStats(BigDecimal totalRevenue, Integer totalTicketsSold, Integer totalFlights) {
        this.totalRevenue = totalRevenue;
        this.totalTicketsSold = totalTicketsSold;
        this.totalFlights = totalFlights;
        this.calculateDerivedStats();
    }
    
    // Helper method to calculate derived statistics
    private void calculateDerivedStats() {
        if (totalTicketsSold != null && totalTicketsSold > 0) {
            this.averageTicketPrice = totalRevenue.divide(BigDecimal.valueOf(totalTicketsSold), 2, BigDecimal.ROUND_HALF_UP);
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
        calculateDerivedStats();
    }
    
    public Integer getTotalTicketsSold() {
        return totalTicketsSold;
    }
    
    public void setTotalTicketsSold(Integer totalTicketsSold) {
        this.totalTicketsSold = totalTicketsSold;
        calculateDerivedStats();
    }
    
    public Integer getTotalFlights() {
        return totalFlights;
    }
    
    public void setTotalFlights(Integer totalFlights) {
        this.totalFlights = totalFlights;
    }
    
    public BigDecimal getAverageTicketPrice() {
        return averageTicketPrice;
    }
    
    public void setAverageTicketPrice(BigDecimal averageTicketPrice) {
        this.averageTicketPrice = averageTicketPrice;
    }
    
    public BigDecimal getOccupancyRate() {
        return occupancyRate;
    }
    
    public void setOccupancyRate(BigDecimal occupancyRate) {
        this.occupancyRate = occupancyRate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }
    
    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
    
    public Route getRoute() {
        return route;
    }
    
    public void setRoute(Route route) {
        this.route = route;
    }
    
    public List<CabinClass> getCabinClasses() {
        return cabinClasses;
    }
    
    public void setCabinClasses(List<CabinClass> cabinClasses) {
        this.cabinClasses = cabinClasses;
    }
}
