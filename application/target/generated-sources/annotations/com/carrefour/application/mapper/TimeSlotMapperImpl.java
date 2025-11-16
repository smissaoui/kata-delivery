package com.carrefour.application.mapper;

import com.carrefour.application.dto.TimeSlotDto;
import com.carrefour.domain.model.DeliveryMode;
import com.carrefour.domain.model.TimeSlot;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-16T18:38:58+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
public class TimeSlotMapperImpl implements TimeSlotMapper {

    @Override
    public TimeSlotDto toDto(TimeSlot timeSlot) {
        if ( timeSlot == null ) {
            return null;
        }

        Long id = null;
        DeliveryMode mode = null;
        LocalDate date = null;
        LocalTime startTime = null;
        LocalTime endTime = null;
        int capacity = 0;

        id = timeSlot.getId();
        mode = timeSlot.getMode();
        date = timeSlot.getDate();
        startTime = timeSlot.getStartTime();
        endTime = timeSlot.getEndTime();
        capacity = timeSlot.getCapacity();

        TimeSlotDto timeSlotDto = new TimeSlotDto( id, mode, date, startTime, endTime, capacity );

        return timeSlotDto;
    }
}
