package com.carrefour.infrastructure.persistence.mapper;

import com.carrefour.domain.model.Reservation;
import com.carrefour.infrastructure.persistence.entity.ReservationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)

public interface ReservationEntityMapper {

    ReservationEntityMapper INSTANCE = Mappers.getMapper(ReservationEntityMapper.class);

    ReservationEntity toEntity(Reservation domain);

    Reservation toDomain(ReservationEntity entity);
}
