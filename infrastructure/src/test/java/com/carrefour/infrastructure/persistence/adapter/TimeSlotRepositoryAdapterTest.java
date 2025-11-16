package com.carrefour.infrastructure.persistence.adapter;

import com.carrefour.domain.model.DeliveryMode;
import com.carrefour.domain.model.TimeSlot;
import com.carrefour.infrastructure.persistence.entity.TimeSlotEntity;
import com.carrefour.infrastructure.persistence.mapper.TimeSlotEntityMapper;
import com.carrefour.infrastructure.persistence.repository.TimeSlotR2dbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TimeSlotRepositoryAdapterTest {

    @Mock
    private TimeSlotR2dbcRepository r2dbcRepository;

    @Mock
    private TimeSlotEntityMapper mapper;

    @InjectMocks
    private TimeSlotRepositoryAdapter adapter;

    private TimeSlot domainSlot;
    private TimeSlotEntity entity;

    private static final Long ID = 1L;
    private static final DeliveryMode MODE = DeliveryMode.DRIVE;
    private static final LocalDate DATE = LocalDate.now().plusDays(1);

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        domainSlot = new TimeSlot(
                ID,
                MODE,
                DATE,
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                5
        );

        entity = new TimeSlotEntity(
                ID,
                MODE,
                DATE,
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                5
        );
    }

    @Test
    void findByModeAndDate_shouldReturnMappedDomainObjects() {
        when(r2dbcRepository.findByModeAndDate(MODE, DATE))
                .thenReturn(Flux.just(entity));

        when(mapper.toDomain(entity)).thenReturn(domainSlot);

        StepVerifier.create(adapter.findByModeAndDate(MODE, DATE))
                .assertNext(slot -> {
                    assertThat(slot.getId()).isEqualTo(ID);
                    assertThat(slot.getMode()).isEqualTo(MODE);
                    assertThat(slot.getCapacity()).isEqualTo(5);
                })
                .verifyComplete();

        verify(r2dbcRepository).findByModeAndDate(MODE, DATE);
        verify(mapper).toDomain(entity);
    }

    @Test
    void findById_shouldReturnMappedDomainObject() {
        when(r2dbcRepository.findById(ID))
                .thenReturn(Mono.just(entity));

        when(mapper.toDomain(entity)).thenReturn(domainSlot);

        StepVerifier.create(adapter.findById(ID))
                .assertNext(slot -> assertThat(slot.getId()).isEqualTo(ID))
                .verifyComplete();

        verify(r2dbcRepository).findById(ID);
        verify(mapper).toDomain(entity);
    }

    @Test
    void save_shouldMapAndSaveEntity_thenReturnMappedDomainObject() {
        when(mapper.toEntity(domainSlot)).thenReturn(entity);
        when(r2dbcRepository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.toDomain(entity)).thenReturn(domainSlot);

        StepVerifier.create(adapter.save(domainSlot))
                .assertNext(slot ->
                        assertThat(slot.getCapacity()).isEqualTo(domainSlot.getCapacity()))
                .verifyComplete();

        verify(mapper).toEntity(domainSlot);
        verify(r2dbcRepository).save(entity);
        verify(mapper).toDomain(entity);
    }
}
