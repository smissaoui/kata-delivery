package com.carrefour.application.exception;

public class TimeSlotNotFoundException extends RuntimeException{
    public TimeSlotNotFoundException(String message) {
        super(message);
    }

}
