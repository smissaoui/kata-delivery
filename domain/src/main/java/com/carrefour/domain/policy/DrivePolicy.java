package com.carrefour.domain.policy;

import com.carrefour.domain.model.TimeSlot;

import java.time.LocalTime;

public class DrivePolicy implements DeliveryPolicy {

    private static final LocalTime START = LocalTime.of(9, 0);
    private static final LocalTime END = LocalTime.of(18, 0);

    @Override
    public boolean isAllowed(TimeSlot slot) {
        return !slot.getStartTime().isBefore(START)
                && !slot.getEndTime().isAfter(END);
    }
}
