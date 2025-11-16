package com.carrefour.infrastructure.persistence.adapter;

import com.carrefour.domain.model.Reservation;
import com.carrefour.infrastructure.persistence.entity.ReservationEntity;
import com.carrefour.infrastructure.persistence.mapper.ReservationEntityMapper;
import com.carrefour.infrastructure.persistence.repository.ReservationR2dbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReservationRepositoryAdapterTest {

    @Mock
    private ReservationR2dbcRepository r2dbcRepository;

    @Mock
    private ReservationEntityMapper mapper;

    @InjectMocks
    private ReservationRepositoryAdapter adapter;

    private Reservation domainReservation;
    private ReservationEntity entityReservation;

    private static final Long ID = 1L;
    private static final Long TIMESLOT_ID = 42L;
    private static final String CUSTOMER_ID = "C123";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        domainReservation = new Reservation(CUSTOMER_ID, TIMESLOT_ID);
        domainReservation.setId(ID);

        entityReservation = new ReservationEntity(ID, CUSTOMER_ID, TIMESLOT_ID, LocalDateTime.now());
    }

    @Test
    void save_shouldMapAndPersistEntity_thenReturnDomainObject() {
        when(mapper.toEntity(domainReservation)).thenReturn(entityReservation);
        when(r2dbcRepository.save(entityReservation)).thenReturn(Mono.just(entityReservation));
        when(mapper.toDomain(entityReservation)).thenReturn(domainReservation);

        StepVerifier.create(adapter.save(domainReservation))
                .assertNext(result ->
                        assertThat(result.getCustomerId()).isEqualTo(CUSTOMER_ID))
                .verifyComplete();

        verify(mapper).toEntity(domainReservation);
        verify(r2dbcRepository).save(entityReservation);
        verify(mapper).toDomain(entityReservation);
    }

    @Test
    void findByCustomerIdAndTimeslotId_shouldReturnDomainObject() {
        when(r2dbcRepository.findByCustomerIdAndTimeSlotId(CUSTOMER_ID, TIMESLOT_ID))
                .thenReturn(Mono.just(entityReservation));

        when(mapper.toDomain(entityReservation)).thenReturn(domainReservation);

        StepVerifier.create(adapter.findByCustomerIdAndTimeslotId(CUSTOMER_ID, TIMESLOT_ID))
                .assertNext(result -> assertThat(result.getId()).isEqualTo(ID))
                .verifyComplete();

        verify(r2dbcRepository).findByCustomerIdAndTimeSlotId(CUSTOMER_ID, TIMESLOT_ID);
        verify(mapper).toDomain(entityReservation);
    }

    @Test
    void delete_shouldMapAndDeleteEntity() {
        when(mapper.toEntity(domainReservation)).thenReturn(entityReservation);
        when(r2dbcRepository.delete(entityReservation)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.delete(domainReservation))
                .verifyComplete();

        verify(mapper).toEntity(domainReservation);
        verify(r2dbcRepository).delete(entityReservation);
    }

    @Test
    void existsByCustomerIdAndTimeSlotId_shouldReturnRepositoryValue() {
        when(r2dbcRepository.existsByCustomerIdAndTimeSlotId(CUSTOMER_ID, TIMESLOT_ID))
                .thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByCustomerIdAndTimeSlotId(CUSTOMER_ID, TIMESLOT_ID))
                .expectNext(true)
                .verifyComplete();

        verify(r2dbcRepository).existsByCustomerIdAndTimeSlotId(CUSTOMER_ID, TIMESLOT_ID);
    }
}
