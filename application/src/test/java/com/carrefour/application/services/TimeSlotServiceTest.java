package com.carrefour.application.services;

import com.carrefour.application.port.TimeSlotRepository;
import com.carrefour.application.service.TimeSlotService;
import com.carrefour.domain.model.DeliveryMode;
import com.carrefour.domain.model.TimeSlot;
import com.carrefour.domain.policy.DeliveryPolicy;
import com.carrefour.domain.service.ReservationDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TimeSlotServiceTest {

    @Mock
    private TimeSlotRepository repository;

    @Mock
    private ReservationDomainService reservationDomainService;

    @InjectMocks
    private TimeSlotService timeSlotService;

    private DeliveryPolicy policy;

    private static final DeliveryMode MODE = DeliveryMode.DRIVE;
    private static final LocalDate DATE = LocalDate.now().plusDays(1);

    private TimeSlot futureAllowedSlot;
    private TimeSlot futureNotAllowedSlot;
    private TimeSlot pastSlot;
    private TimeSlot zeroCapacitySlot;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        policy = mock(DeliveryPolicy.class);

        when(reservationDomainService.getPolicyForMode(MODE))
                .thenReturn(policy);

        // A valid future slot
        futureAllowedSlot = new TimeSlot(
                1L, MODE, DATE, LocalTime.now().plusHours(2), LocalTime.now().plusHours(3), 5
        );

        // A future slot NOT allowed by policy
        futureNotAllowedSlot = new TimeSlot(
                2L, MODE, DATE, LocalTime.now().plusHours(2), LocalTime.now().plusHours(3), 5
        );

        // A slot in the past
        pastSlot = new TimeSlot(
                3L, MODE, LocalDate.now().minusDays(1), LocalTime.now(), LocalTime.now(), 5
        );

        // A future slot with zero capacity
        zeroCapacitySlot = new TimeSlot(
                4L, MODE, DATE, LocalTime.now().plusHours(1), LocalTime.now().plusHours(2), 0
        );
    }

    @Test
    void getTimeSlots_shouldReturnEmpty_whenNoValidSlots() {

        when(policy.isAllowed(any())).thenReturn(false);

        when(repository.findByModeAndDate(MODE, DATE))
                .thenReturn(Flux.just(futureAllowedSlot, futureNotAllowedSlot));

        StepVerifier.create(timeSlotService.getTimeSlots(MODE, DATE))
                .verifyComplete(); // empty flux
    }

    @Test
    void getTimeSlots_shouldMapTimeSlotToDtoCorrectly() {

        when(policy.isAllowed(any())).thenReturn(true);

        when(repository.findByModeAndDate(MODE, DATE))
                .thenReturn(Flux.just(futureAllowedSlot));

        StepVerifier.create(timeSlotService.getTimeSlots(MODE, DATE))
                .assertNext(dto -> {
                    assertThat(dto.id()).isEqualTo(futureAllowedSlot.getId());
                    assertThat(dto.mode()).isEqualTo(futureAllowedSlot.getMode());
                    assertThat(dto.date()).isEqualTo(futureAllowedSlot.getDate());
                    assertThat(dto.startTime()).isEqualTo(futureAllowedSlot.getStartTime());
                    assertThat(dto.endTime()).isEqualTo(futureAllowedSlot.getEndTime());
                    assertThat(dto.capacity()).isEqualTo(futureAllowedSlot.getCapacity());
                })
                .verifyComplete();
    }

    @Test
    void getTimeSlots_shouldReturnOnlySlotsAllowedByPolicy() {

        when(policy.isAllowed(futureAllowedSlot)).thenReturn(true);
        when(policy.isAllowed(futureNotAllowedSlot)).thenReturn(false);

        when(repository.findByModeAndDate(MODE, DATE))
                .thenReturn(Flux.just(
                        futureAllowedSlot,
                        futureNotAllowedSlot,
                        pastSlot,
                        zeroCapacitySlot
                ));

        StepVerifier.create(timeSlotService.getTimeSlots(MODE, DATE))
                .assertNext(dto -> {
                    assertThat(dto.id()).isEqualTo(futureAllowedSlot.getId());
                    assertThat(dto.capacity()).isEqualTo(futureAllowedSlot.getCapacity());
                })
                .verifyComplete();

        verify(policy).isAllowed(futureAllowedSlot);
        verify(policy).isAllowed(futureNotAllowedSlot);
    }

}
