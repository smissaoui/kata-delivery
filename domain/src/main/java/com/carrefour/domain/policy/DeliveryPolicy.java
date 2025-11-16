package com.carrefour.domain.policy;

import com.carrefour.domain.model.TimeSlot;

public interface DeliveryPolicy {
    boolean isAllowed(TimeSlot slot);
}
