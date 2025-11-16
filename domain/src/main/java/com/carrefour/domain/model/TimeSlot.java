package com.carrefour.domain.model;

import com.carrefour.domain.exception.DomainException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class TimeSlot {

    private Long id;
    private DeliveryMode mode;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;

    public TimeSlot(Long id,
                    DeliveryMode mode,
                    LocalDate date,
                    LocalTime startTime,
                    LocalTime endTime,
                    int capacity) {

        if (capacity < 0) {
            throw new DomainException("Capacity cannot be negative");
        }

        this.id = id;
        this.mode = mode;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
    }

    public boolean hasAvailableCapacity() {
        return capacity > 0;
    }

    public void reserveOne() {
        if (capacity <= 0) {
            throw new DomainException("No remaining capacity for this timeslot");
        }
        capacity--;
    }

    /**
     * Releases one unit of capacity when a reservation is cancelled.
     */
    public void releaseOne() {
        capacity++;
    }
}
