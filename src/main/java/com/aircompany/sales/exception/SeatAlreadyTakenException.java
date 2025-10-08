package com.aircompany.sales.exception;

/**
 * Exception thrown when attempting to book a seat that is already taken
 */
public class SeatAlreadyTakenException extends RuntimeException {
    
    private final String seatNumber;
    
    public SeatAlreadyTakenException(String seatNumber) {
        super("Seat " + seatNumber + " is already taken");
        this.seatNumber = seatNumber;
    }
    
    public String getSeatNumber() {
        return seatNumber;
    }
}
