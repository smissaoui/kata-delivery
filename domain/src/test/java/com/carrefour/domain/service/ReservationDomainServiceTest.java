package com.carrefour.domain.service;

import com.carrefour.domain.exception.DomainException;
import com.carrefour.domain.model.DeliveryMode;
import com.carrefour.domain.model.TimeSlot;
import com.carrefour.domain.policy.DeliveryPolicy;
import com.carrefour.domain.policy.DeliveryPolicyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationDomainServiceTest {

    @Mock
    private DeliveryPolicyFactory policyFactory;

    @Mock
    private DeliveryPolicy policy;

    @InjectMocks
    private ReservationDomainService service;

    private TimeSlot slot;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        slot = new TimeSlot(
                1L,
                DeliveryMode.DRIVE,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                3
        );
    }

    @Test
    void ensureUserCanReserve_shouldThrow_whenAlreadyReservedIsTrue() {
        assertThatThrownBy(() -> service.ensureUserCanReserve(true))
                .isInstanceOf(DomainException.class)
                .hasMessage("User already reserved this time slot");
    }

    @Test
    void ensureUserCanReserve_shouldPass_whenAlreadyReservedIsFalse() {
        service.ensureUserCanReserve(false);
    }

    @Test
    void ensureCapacityAvailable_shouldThrow_whenCapacityZero() {
        slot.setCapacity(0);

        assertThatThrownBy(() -> service.ensureCapacityAvailable(slot))
                .isInstanceOf(DomainException.class)
                .hasMessage("No more capacity");
    }

    @Test
    void ensureCapacityAvailable_shouldPass_whenCapacityAvailable() {
        slot.setCapacity(5);
        service.ensureCapacityAvailable(slot);
    }

    @Test
    void reserve_shouldDecreaseCapacity() {
        int initialCapacity = slot.getCapacity();

        service.reserve(slot);

        assertThat(slot.getCapacity()).isEqualTo(initialCapacity - 1);
    }

    @Test
    void reserve_shouldThrow_whenSlotCannotReserve() {
        slot.setCapacity(0);

        assertThatThrownBy(() -> service.reserve(slot))
                .isInstanceOf(DomainException.class)
                .hasMessage("No remaining capacity for this timeslot");
    }

    @Test
    void release_shouldIncreaseCapacity() {
        int initialCapacity = slot.getCapacity();

        service.release(slot);

        assertThat(slot.getCapacity()).isEqualTo(initialCapacity + 1);
    }

    @Test
    void getPolicyForMode_shouldReturnPolicyFromFactory() {
        when(policyFactory.getPolicy(DeliveryMode.DRIVE)).thenReturn(policy);

        DeliveryPolicy result = service.getPolicyForMode(DeliveryMode.DRIVE);

        assertThat(result).isEqualTo(policy);
        verify(policyFactory).getPolicy(DeliveryMode.DRIVE);
    }

    @Test
    void ensureSlotAllowed_shouldThrow_whenPolicyRejectsSlot() {
        when(policy.isAllowed(slot)).thenReturn(false);

        assertThatThrownBy(() -> service.ensureSlotAllowed(policy, slot))
                .isInstanceOf(DomainException.class)
                .hasMessage("Timeslot violates business rule for delivery mode");
    }

    @Test
    void ensureSlotAllowed_shouldPass_whenPolicyAcceptsSlot() {
        when(policy.isAllowed(slot)).thenReturn(true);

        service.ensureSlotAllowed(policy, slot);
    }
}
