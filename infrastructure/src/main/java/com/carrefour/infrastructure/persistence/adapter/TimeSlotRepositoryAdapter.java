package com.carrefour.infrastructure.persistence.adapter;

import com.carrefour.application.port.TimeSlotRepository;
import com.carrefour.domain.model.DeliveryMode;
import com.carrefour.domain.model.TimeSlot;
import com.carrefour.infrastructure.persistence.entity.TimeSlotEntity;
import com.carrefour.infrastructure.persistence.mapper.TimeSlotEntityMapper;
import com.carrefour.infrastructure.persistence.repository.TimeSlotR2dbcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
@Slf4j
public class TimeSlotRepositoryAdapter implements TimeSlotRepository{

    private final TimeSlotR2dbcRepository repository;
    private final TimeSlotEntityMapper mapper;

    public TimeSlotRepositoryAdapter(TimeSlotR2dbcRepository repository, TimeSlotEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Cacheable(value = "timeslots", key = "#mode + '-' + #date")
    @Override
    public Flux<TimeSlot> findByModeAndDate(DeliveryMode mode, LocalDate date) {
        log.info("Fetching timeslots from database for mode='{}', date='{}'", mode, date);
        return repository.findByModeAndDate(mode, date)
                .map(mapper::toDomain);
    }

    @Cacheable(value = "timeslotById", key = "#id")
    @Override
    public Mono<TimeSlot> findById(Long id) {
        log.info("Fetching timeslot by id={}", id);
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Mono<TimeSlot> save(TimeSlot timeSlot) {
        log.info("Saving timeslot id={} (capacity={}, date={}, startTime={})",
                timeSlot.getId(),
                timeSlot.getCapacity(),
                timeSlot.getDate(),
                timeSlot.getStartTime()
        );
        TimeSlotEntity entity = mapper.toEntity(timeSlot);

        return repository.save(entity)
                .map(mapper::toDomain);
    }
}
