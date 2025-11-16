package com.carrefour.domain.policy;

import com.carrefour.domain.model.TimeSlot;

import java.time.LocalDateTime;

public class DeliveryPolicyImpl implements DeliveryPolicy {

    @Override
    public boolean isAllowed(TimeSlot slot) {
        LocalDateTime slotDateTime = LocalDateTime.of(slot.getDate(), slot.getStartTime());
        return LocalDateTime.now().plusHours(24).isBefore(slotDateTime);
    }
}

