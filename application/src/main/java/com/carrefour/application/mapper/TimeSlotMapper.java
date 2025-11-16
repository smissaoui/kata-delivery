package com.carrefour.application.mapper;

import com.carrefour.application.dto.TimeSlotDto;
import com.carrefour.domain.model.TimeSlot;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TimeSlotMapper {

    TimeSlotMapper INSTANCE = Mappers.getMapper(TimeSlotMapper.class);

    TimeSlotDto toDto(TimeSlot timeSlot);

}
