package com.carrefour.domain.model;

import com.carrefour.domain.exception.DomainException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    private Long Id;
    private String customerId;
    private Long timeSlotId;
    private LocalDateTime createdAt;

    public Reservation(String customerId, Long timeSlotId) {
        if (customerId == null || customerId.isBlank()) {
            throw new DomainException("CustomerId is required");
        }
        if (timeSlotId == null) {
            throw new DomainException("TimeSlotId is required");
        }

        this.customerId = customerId;
        this.timeSlotId = timeSlotId;
        this.createdAt = LocalDateTime.now();
    }
}
