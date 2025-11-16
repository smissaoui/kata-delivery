package com.carrefour.infrastructure.persistence.mapper;

import com.carrefour.domain.model.TimeSlot;
import com.carrefour.infrastructure.persistence.entity.TimeSlotEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TimeSlotEntityMapper {

    TimeSlotEntityMapper INSTANCE = Mappers.getMapper(TimeSlotEntityMapper.class);

    TimeSlotEntity toEntity(TimeSlot domain);

    TimeSlot toDomain(TimeSlotEntity entity);
}
