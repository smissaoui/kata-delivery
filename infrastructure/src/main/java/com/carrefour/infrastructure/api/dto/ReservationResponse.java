package com.carrefour.infrastructure.api.dto;

import java.time.LocalDateTime;

public record ReservationResponse(Long id,
                                  String customerId,
                                  Long timeSlotId,
                                  LocalDateTime createdAt) {
}
