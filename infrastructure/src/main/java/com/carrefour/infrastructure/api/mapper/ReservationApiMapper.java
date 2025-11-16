package com.carrefour.infrastructure.api.mapper;

import com.carrefour.domain.model.Reservation;
import com.carrefour.infrastructure.api.dto.ReservationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReservationApiMapper {

    ReservationResponse toResponse(Reservation reservation);
}
