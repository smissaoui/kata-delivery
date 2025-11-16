package com.carrefour.infrastructure.persistence.adapter;

import com.carrefour.application.port.ReservationRepository;
import com.carrefour.domain.model.Reservation;
import com.carrefour.infrastructure.persistence.mapper.ReservationEntityMapper;
import com.carrefour.infrastructure.persistence.repository.ReservationR2dbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class ReservationRepositoryAdapter implements ReservationRepository {

    private final ReservationR2dbcRepository repository;
    private final ReservationEntityMapper mapper;

    public ReservationRepositoryAdapter(ReservationR2dbcRepository repository, ReservationEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Reservation> save(Reservation reservation) {
        log.info("Saving reservation for customerId='{}', timeSlotId='{}'",
                reservation.getCustomerId(), reservation.getTimeSlotId());
        return repository.save(mapper.toEntity(reservation))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Reservation> findByCustomerIdAndTimeslotId(String customerId, Long timeSlotId) {
        log.info("Fetching reservation for customerId='{}', timeSlotId='{}'",
                customerId, timeSlotId);
        return repository.findByCustomerIdAndTimeSlotId(customerId, timeSlotId)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Void> delete(Reservation reservation) {
        log.info("Deleting reservation id={}, customerId='{}', timeSlotId='{}'",
                reservation.getId(), reservation.getCustomerId(), reservation.getTimeSlotId());
        return repository.delete(mapper.toEntity(reservation));
    }

    @Cacheable(value = "reservationExists", key = "#customerId + '-' + #timeSlotId")
    @Override
    public Mono<Boolean> existsByCustomerIdAndTimeSlotId(String customerId, Long timeSlotId) {
        log.info("Checking if reservation exists for customerId='{}', timeSlotId='{}'",
                customerId, timeSlotId);
        return repository.existsByCustomerIdAndTimeSlotId(customerId, timeSlotId);
    }

}
