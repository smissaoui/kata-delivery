package com.carrefour.infrastructure.persistence.mapper;

import com.carrefour.domain.model.DeliveryMode;
import com.carrefour.domain.model.TimeSlot;
import com.carrefour.infrastructure.persistence.entity.TimeSlotEntity;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-16T18:38:59+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class TimeSlotEntityMapperImpl implements TimeSlotEntityMapper {

    @Override
    public TimeSlotEntity toEntity(TimeSlot domain) {
        if ( domain == null ) {
            return null;
        }

        TimeSlotEntity.TimeSlotEntityBuilder timeSlotEntity = TimeSlotEntity.builder();

        timeSlotEntity.id( domain.getId() );
        timeSlotEntity.mode( domain.getMode() );
        timeSlotEntity.date( domain.getDate() );
        timeSlotEntity.startTime( domain.getStartTime() );
        timeSlotEntity.endTime( domain.getEndTime() );
        timeSlotEntity.capacity( domain.getCapacity() );

        return timeSlotEntity.build();
    }

    @Override
    public TimeSlot toDomain(TimeSlotEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Long id = null;
        DeliveryMode mode = null;
        LocalDate date = null;
        LocalTime startTime = null;
        LocalTime endTime = null;
        int capacity = 0;

        id = entity.getId();
        mode = entity.getMode();
        date = entity.getDate();
        startTime = entity.getStartTime();
        endTime = entity.getEndTime();
        capacity = entity.getCapacity();

        TimeSlot timeSlot = new TimeSlot( id, mode, date, startTime, endTime, capacity );

        return timeSlot;
    }
}
