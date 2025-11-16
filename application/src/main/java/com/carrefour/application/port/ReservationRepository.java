package com.carrefour.application.port;

import com.carrefour.domain.model.Reservation;
import reactor.core.publisher.Mono;

public interface ReservationRepository {

    Mono<Reservation> save(Reservation reservation);

    Mono<Reservation> findByCustomerIdAndTimeslotId(String customerId, Long timeSlotId);

    Mono<Void> delete(Reservation reservation);

    Mono<Boolean> existsByCustomerIdAndTimeSlotId(String customerId, Long timeSlotId);

}
