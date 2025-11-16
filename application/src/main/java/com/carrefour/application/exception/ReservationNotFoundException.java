package com.carrefour.application.exception;

public class ReservationNotFoundException extends RuntimeException{

    public ReservationNotFoundException(String message) {
        super(message);
    }
}
