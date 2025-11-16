package com.carrefour.application.services;

import com.carrefour.application.dto.CreateReservationCommand;
import com.carrefour.application.exception.ReservationNotFoundException;
import com.carrefour.application.exception.TimeSlotNotFoundException;
import com.carrefour.application.port.ReservationRepository;
import com.carrefour.application.port.TimeSlotRepository;
import com.carrefour.application.service.ReservationService;
import com.carrefour.domain.model.DeliveryMode;
import com.carrefour.domain.model.Reservation;
import com.carrefour.domain.model.TimeSlot;
import com.carrefour.domain.policy.DeliveryPolicy;
import com.carrefour.domain.service.ReservationDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationDomainService domainService;

    @InjectMocks
    private ReservationService reservationService;

    private TimeSlot slot;

    private static final Long SLOT_ID = 10L;
    private static final String CUSTOMER_ID = "C123";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        slot = new TimeSlot(
                SLOT_ID,
                DeliveryMode.DRIVE,
                java.time.LocalDate.now(),
                java.time.LocalTime.of(10, 0),
                java.time.LocalTime.of(12, 0),
                5
        );
    }

    @Test
    void createReservation_shouldThrow_whenTimeSlotNotFound() {
        CreateReservationCommand cmd = new CreateReservationCommand(CUSTOMER_ID, SLOT_ID);

        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Mono.empty());

        StepVerifier.create(reservationService.createReservation(cmd))
                .expectError(TimeSlotNotFoundException.class)
                .verify();
    }

    @Test
    void createReservation_shouldThrow_whenUserAlreadyReserved() {
        CreateReservationCommand cmd = new CreateReservationCommand(CUSTOMER_ID, SLOT_ID);

        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Mono.just(slot));
        when(reservationRepository.existsByCustomerIdAndTimeSlotId(CUSTOMER_ID, SLOT_ID))
                .thenReturn(Mono.just(true));

        doThrow(new RuntimeException("User already reserved"))
                .when(domainService).ensureUserCanReserve(true);

        StepVerifier.create(reservationService.createReservation(cmd))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void createReservation_shouldApplyBusinessRules_andSaveReservation() {
        CreateReservationCommand cmd = new CreateReservationCommand(CUSTOMER_ID, SLOT_ID);

        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Mono.just(slot));
        when(reservationRepository.existsByCustomerIdAndTimeSlotId(CUSTOMER_ID, SLOT_ID))
                .thenReturn(Mono.just(false));

        DeliveryPolicy policy = mock(DeliveryPolicy.class);
        when(domainService.getPolicyForMode(slot.getMode())).thenReturn(policy);

        when(timeSlotRepository.save(slot)).thenReturn(Mono.just(slot));

        Reservation saved = new Reservation(CUSTOMER_ID, SLOT_ID);
        when(reservationRepository.save(any())).thenReturn(Mono.just(saved));

        StepVerifier.create(reservationService.createReservation(cmd))
                .expectNext(saved)
                .verifyComplete();

        verify(domainService).ensureCapacityAvailable(slot);
        verify(domainService).reserve(slot);
        verify(reservationRepository).save(any());
    }

    @Test
    void cancelReservation_shouldThrow_whenReservationNotFound() {
        when(reservationRepository.findByCustomerIdAndTimeslotId(CUSTOMER_ID, SLOT_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(reservationService.cancelReservation(CUSTOMER_ID, SLOT_ID))
                .expectError(ReservationNotFoundException.class)
                .verify();
    }

    @Test
    void cancelReservation_shouldThrow_whenAssociatedTimeSlotNotFound() {
        Reservation reservation = new Reservation(CUSTOMER_ID, SLOT_ID);

        when(reservationRepository.findByCustomerIdAndTimeslotId(CUSTOMER_ID, SLOT_ID))
                .thenReturn(Mono.just(reservation));

        when(timeSlotRepository.findById(SLOT_ID))
                .thenReturn(Mono.empty());

        StepVerifier.create(reservationService.cancelReservation(CUSTOMER_ID, SLOT_ID))
                .expectError(TimeSlotNotFoundException.class)
                .verify();
    }

    @Test
    void cancelReservation_shouldReleaseCapacity_andDeleteReservation() {
        Reservation reservation = new Reservation(CUSTOMER_ID, SLOT_ID);

        when(reservationRepository.findByCustomerIdAndTimeslotId(CUSTOMER_ID, SLOT_ID))
                .thenReturn(Mono.just(reservation));

        when(timeSlotRepository.findById(SLOT_ID)).thenReturn(Mono.just(slot));
        when(timeSlotRepository.save(slot)).thenReturn(Mono.just(slot));

        when(reservationRepository.delete(reservation)).thenReturn(Mono.empty());

        StepVerifier.create(reservationService.cancelReservation(CUSTOMER_ID, SLOT_ID))
                .verifyComplete();

        verify(domainService).release(slot);
        verify(timeSlotRepository).save(slot);
        verify(reservationRepository).delete(reservation);
    }
}
