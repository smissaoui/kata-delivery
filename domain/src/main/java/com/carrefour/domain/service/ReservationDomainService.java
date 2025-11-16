package com.carrefour.domain.service;

import com.carrefour.domain.exception.DomainException;
import com.carrefour.domain.model.DeliveryMode;
import com.carrefour.domain.model.TimeSlot;
import com.carrefour.domain.policy.DeliveryPolicy;
import com.carrefour.domain.policy.DeliveryPolicyFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
public class ReservationDomainService {

    private final DeliveryPolicyFactory policyFactory;

    public void ensureUserCanReserve(boolean alreadyReserved) {
        if (alreadyReserved) {
            throw new DomainException("User already reserved this time slot");
        }
    }

    public void ensureCapacityAvailable(TimeSlot slot) {
        if (!slot.hasAvailableCapacity()) {
            throw new DomainException("No more capacity");
        }
    }

    public void reserve(TimeSlot slot) {
        slot.reserveOne();
    }

    public void release(TimeSlot slot) {
        slot.releaseOne();
    }

    public DeliveryPolicy getPolicyForMode(DeliveryMode mode) {
        return policyFactory.getPolicy(mode);
    }

    public void ensureSlotAllowed(DeliveryPolicy policy, TimeSlot slot) {
        if (!policy.isAllowed(slot)) {
            throw new DomainException("Timeslot violates business rule for delivery mode");
        }
    }
}
