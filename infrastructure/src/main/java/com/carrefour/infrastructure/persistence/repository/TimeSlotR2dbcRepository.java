package com.carrefour.infrastructure.persistence.repository;

import com.carrefour.domain.model.DeliveryMode;
import com.carrefour.infrastructure.persistence.entity.TimeSlotEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface TimeSlotR2dbcRepository extends ReactiveCrudRepository<TimeSlotEntity, Long> {

    Flux<TimeSlotEntity> findByModeAndDate(DeliveryMode mode, LocalDate date);
}
