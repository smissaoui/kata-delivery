package com.carrefour.infrastructure.persistence.mapper;

import com.carrefour.domain.model.Reservation;
import com.carrefour.infrastructure.persistence.entity.ReservationEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-16T18:38:59+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class ReservationEntityMapperImpl implements ReservationEntityMapper {

    @Override
    public ReservationEntity toEntity(Reservation domain) {
        if ( domain == null ) {
            return null;
        }

        ReservationEntity.ReservationEntityBuilder reservationEntity = ReservationEntity.builder();

        reservationEntity.id( domain.getId() );
        reservationEntity.customerId( domain.getCustomerId() );
        reservationEntity.timeSlotId( domain.getTimeSlotId() );
        reservationEntity.createdAt( domain.getCreatedAt() );

        return reservationEntity.build();
    }

    @Override
    public Reservation toDomain(ReservationEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Reservation reservation = new Reservation();

        reservation.setId( entity.getId() );
        reservation.setCustomerId( entity.getCustomerId() );
        reservation.setTimeSlotId( entity.getTimeSlotId() );
        reservation.setCreatedAt( entity.getCreatedAt() );

        return reservation;
    }
}
