package com.carrefour.domain.policy;

import com.carrefour.domain.model.DeliveryMode;

import static com.carrefour.domain.model.DeliveryMode.*;

public class DeliveryPolicyFactory {

    public DeliveryPolicy getPolicy(DeliveryMode mode) {
        return switch (mode) {
            case DRIVE -> new DrivePolicy();
            case DELIVERY -> new DeliveryPolicyImpl();
            case DELIVERY_TODAY -> new DeliveryTodayPolicy();
            case DELIVERY_ASAP -> new DeliveryAsapPolicy();
        };
    }
}
