package com.carrefour.infrastructure.persistence.repository;

import com.carrefour.domain.model.Reservation;
import com.carrefour.infrastructure.persistence.entity.ReservationEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ReservationR2dbcRepository extends ReactiveCrudRepository<ReservationEntity, Long> {

    Mono<ReservationEntity> findByCustomerIdAndTimeSlotId(String customerId, Long timeSlotId);
    Mono<Boolean> existsByCustomerIdAndTimeSlotId(String customerId, Long timeSlotId);
}
