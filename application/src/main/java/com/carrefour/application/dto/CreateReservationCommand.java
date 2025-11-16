package com.carrefour.application.dto;

public record CreateReservationCommand(String customerId,
                                       Long timeSlotId) {
}
