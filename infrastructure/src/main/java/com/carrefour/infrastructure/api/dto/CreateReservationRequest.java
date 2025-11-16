package com.carrefour.infrastructure.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateReservationRequest(@NotBlank(message = "customerId must not be blank") String customerId,
                                       @NotNull(message = "timeSlotId must not be null")
                                       @Positive(message = "timeSlotId must be a positive number")Long timeSlotId) {
}
