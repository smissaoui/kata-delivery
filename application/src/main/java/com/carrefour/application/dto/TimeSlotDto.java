package com.carrefour.application.dto;

import com.carrefour.domain.model.DeliveryMode;

import java.time.LocalDate;
import java.time.LocalTime;

public record TimeSlotDto(Long id,
                          DeliveryMode mode,
                          LocalDate date,
                          LocalTime startTime,
                          LocalTime endTime,
                          int capacity) {
}
