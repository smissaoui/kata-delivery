package com.carrefour.application.port;

import com.carrefour.domain.model.DeliveryMode;
import com.carrefour.domain.model.TimeSlot;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface TimeSlotRepository {

    Flux<TimeSlot> findByModeAndDate(DeliveryMode mode, LocalDate date);

    Mono<TimeSlot> findById(Long id);

    Mono<TimeSlot> save(TimeSlot timeSlot);
}
