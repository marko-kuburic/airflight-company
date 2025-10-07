package com.aircompany.sales.dto;

import com.aircompany.sales.model.Reservation;
import java.time.LocalDateTime;

public class ReservationResponse {
    private Long id;
    private String reservationNumber;
    private String status;
    private String specialRequests;
    private LocalDateTime createdAt;
    private Long offerId;
    private Long customerId;
    // Removed tickets field to avoid lazy loading issues

    public ReservationResponse() {}

    public ReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.reservationNumber = reservation.getReservationNumber();
        this.status = reservation.getStatus() != null ? reservation.getStatus().toString() : null;
        this.specialRequests = reservation.getSpecialRequests();
        this.createdAt = reservation.getCreatedAt();
        // Don't access lazy-loaded fields - they will be null but that's OK for now
        this.offerId = null;
        this.customerId = null;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
}
