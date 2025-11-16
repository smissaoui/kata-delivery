package com.carrefour.domain.policy;

import com.carrefour.domain.model.TimeSlot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DeliveryTodayPolicy implements DeliveryPolicy {

    private static final LocalTime LIMIT = LocalTime.of(15, 0);

    @Override
    public boolean isAllowed(TimeSlot slot) {
        LocalDate today = LocalDate.now();

        boolean sameDay = slot.getDate().isEqual(today);

        boolean beforeLimit =
                LocalDateTime.of(slot.getDate(), slot.getStartTime())
                        .isBefore(LocalDateTime.of(today, LIMIT));

        return sameDay && beforeLimit;
    }
}