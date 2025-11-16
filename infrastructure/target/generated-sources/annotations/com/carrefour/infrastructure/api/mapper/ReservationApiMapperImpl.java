package com.carrefour.infrastructure.api.mapper;

import com.carrefour.domain.model.Reservation;
import com.carrefour.infrastructure.api.dto.ReservationResponse;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-16T18:38:59+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class ReservationApiMapperImpl implements ReservationApiMapper {

    @Override
    public ReservationResponse toResponse(Reservation reservation) {
        if ( reservation == null ) {
            return null;
        }

        Long id = null;
        String customerId = null;
        Long timeSlotId = null;
        LocalDateTime createdAt = null;

        id = reservation.getId();
        customerId = reservation.getCustomerId();
        timeSlotId = reservation.getTimeSlotId();
        createdAt = reservation.getCreatedAt();

        ReservationResponse reservationResponse = new ReservationResponse( id, customerId, timeSlotId, createdAt );

        return reservationResponse;
    }
}
