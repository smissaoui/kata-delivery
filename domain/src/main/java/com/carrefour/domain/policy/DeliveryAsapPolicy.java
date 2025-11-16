package com.carrefour.domain.policy;

import com.carrefour.domain.model.TimeSlot;

import java.time.LocalDateTime;

public class DeliveryAsapPolicy implements DeliveryPolicy {

    @Override
    public boolean isAllowed(TimeSlot slot) {
        LocalDateTime slotStart = LocalDateTime.of(slot.getDate(), slot.getStartTime());
        return slotStart.isBefore(LocalDateTime.now().plusMinutes(120));
    }
}
