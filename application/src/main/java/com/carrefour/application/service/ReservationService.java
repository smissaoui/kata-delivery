package com.carrefour.application.service;

import com.carrefour.application.dto.CreateReservationCommand;
import com.carrefour.application.exception.ReservationNotFoundException;
import com.carrefour.application.exception.TimeSlotNotFoundException;
import com.carrefour.application.port.ReservationRepository;
import com.carrefour.application.port.TimeSlotRepository;
import com.carrefour.domain.model.Reservation;
import com.carrefour.domain.model.TimeSlot;
import com.carrefour.domain.policy.DeliveryPolicy;
import com.carrefour.domain.service.ReservationDomainService;
import org.springframework.cache.annotation.CacheEvict;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReservationService {

    private final TimeSlotRepository timeSlotRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationDomainService reservationDomainService;

    public ReservationService(TimeSlotRepository timeSlotRepository,
                              ReservationRepository reservationRepository,
                              ReservationDomainService domainService) {
        this.timeSlotRepository = timeSlotRepository;
        this.reservationRepository = reservationRepository;
        this.reservationDomainService = domainService;
    }

    @CacheEvict(value = {"timeslotById", "timeslots", "reservationExists"}, allEntries = true)
    public Mono<Reservation> createReservation(CreateReservationCommand command) {

        return fetchTimeSlot(command)
                .flatMap(slot -> checkUserReservationEligibility(command, slot))
                .flatMap(this::checkAndApplyReservationBusinessRules)
                .flatMap(slot -> saveReservationForUser(command, slot));
    }

    private Mono<TimeSlot> fetchTimeSlot(CreateReservationCommand command) {

        return timeSlotRepository.findById(command.timeSlotId())
                .switchIfEmpty(Mono.error(new TimeSlotNotFoundException(
                        "TimeSlot not found: " + command.timeSlotId()
                )));
    }

    private Mono<TimeSlot> checkAndApplyReservationBusinessRules(TimeSlot slot) {

        reservationDomainService.ensureCapacityAvailable(slot);
        reservationDomainService.reserve(slot);

        return timeSlotRepository.save(slot);
    }

    private Mono<Reservation> saveReservationForUser(CreateReservationCommand command, TimeSlot slot) {
        return saveReservation(command, slot);
    }

    private Mono<Reservation> saveReservation(CreateReservationCommand command, TimeSlot slot) {
        return reservationRepository.save(
                new Reservation(
                        command.customerId(),
                        slot.getId()
                )
        );
    }

    private Mono<TimeSlot> checkUserReservationEligibility(CreateReservationCommand command, TimeSlot slot) {

        return reservationRepository.existsByCustomerIdAndTimeSlotId(command.customerId(), slot.getId())
                .flatMap(alreadyReserved -> {
                    reservationDomainService.ensureUserCanReserve(alreadyReserved);

                    DeliveryPolicy policy = reservationDomainService.getPolicyForMode(slot.getMode());
                    reservationDomainService.ensureSlotAllowed(policy, slot);

                    return Mono.just(slot);
                });
    }

    @CacheEvict(value = {"timeslotById", "timeslots", "reservationExists"}, allEntries = true)
    public Mono<Void> cancelReservation(String customerId, Long timeSlotId) {

        return findReservation(customerId, timeSlotId)
                .flatMap(this::findAssociatedTimeSlot)
                .flatMap(tuple -> releaseAndUpdateTimeSlot(tuple.reservation(), tuple.timeSlot()))
                .flatMap(reservationRepository::delete);
    }

    private Mono<Reservation> findReservation(String customerId, Long timeSlotId) {
        return reservationRepository.findByCustomerIdAndTimeslotId(customerId, timeSlotId)
                .switchIfEmpty(Mono.error(new ReservationNotFoundException(
                        "Reservation not found for customerId=" + customerId + " and timeSlotId=" + timeSlotId
                )));
    }

    private Mono<ReservationSlotTuple> findAssociatedTimeSlot(Reservation reservation) {
        return timeSlotRepository.findById(reservation.getTimeSlotId())
                .switchIfEmpty(Mono.error(new TimeSlotNotFoundException(
                        "TimeSlot not found for reservation timeSlotId=" + reservation.getTimeSlotId()
                )))
                .map(slot -> new ReservationSlotTuple(reservation, slot));
    }

    private record ReservationSlotTuple(Reservation reservation, TimeSlot timeSlot) {}

    private Mono<Reservation> releaseAndUpdateTimeSlot(Reservation reservation, TimeSlot slot) {

        reservationDomainService.release(slot);

        return timeSlotRepository.save(slot)
                .thenReturn(reservation);
    }



}
