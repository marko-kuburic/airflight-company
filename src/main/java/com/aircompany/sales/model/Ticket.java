package com.aircompany.sales.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.aircompany.sales.model.Reservation;
import com.aircompany.sales.model.Passenger;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tickets")
@EntityListeners(AuditingEntityListener.class)
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TicketStatus status;
    
    @Column(name = "seat_number")
    private String seatNumber; // npr. "12A", "15F"
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id")
    private Passenger passenger;
    
    // Constructors
    public Ticket() {}
    
    public Ticket(BigDecimal price, Reservation reservation, Passenger passenger) {
        this.price = price;
        this.reservation = reservation;
        this.passenger = passenger;
        this.status = TicketStatus.CONFIRMED;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public TicketStatus getStatus() {
        return status;
    }
    
    public void setStatus(TicketStatus status) {
        this.status = status;
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
    
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
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
    
    public Reservation getReservation() {
        return reservation;
    }
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    public Passenger getPassenger() {
        return passenger;
    }
    
    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }
    
    // Helper method to get fare through reservation
    // Since an offer can now have multiple fares, we need to specify which fare we want
    // For now, we'll return the first fare (could be enhanced to select by cabin class)
    public Fare getFare() {
        if (reservation != null && reservation.getOffer() != null) {
            List<Fare> fares = reservation.getOffer().getFares();
            return fares != null && !fares.isEmpty() ? fares.get(0) : null;
        }
        return null;
    }
    
    // Nested Enum
    public enum TicketStatus {
        CREATED,    // Креирана - резервација направљена, плаћање није завршено
        CONFIRMED,  // Потврђена/плаћена - плаћање успешно
        CANCELLED,  // Отказана - поништена
        USED,       // Искоришћена - употребљена након завршеног лета
        REFUNDED,   // Рефундирана - новац враћен
        EXPIRED     // Истекла - истекао рок
    }
}
