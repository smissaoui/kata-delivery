package com.carrefour.application.service;

import com.carrefour.application.dto.TimeSlotDto;
import com.carrefour.application.mapper.TimeSlotMapper;
import com.carrefour.application.port.TimeSlotRepository;
import com.carrefour.domain.model.DeliveryMode;
import com.carrefour.domain.model.TimeSlot;
import com.carrefour.domain.policy.DeliveryPolicy;
import com.carrefour.domain.service.ReservationDomainService;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Predicate;

@AllArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository repository;
    private final TimeSlotMapper mapper = TimeSlotMapper.INSTANCE;
    private final ReservationDomainService reservationDomainService;

    public Flux<TimeSlotDto> getTimeSlots(DeliveryMode mode, LocalDate date) {
        DeliveryPolicy policy = reservationDomainService.getPolicyForMode(mode);
        return repository.findByModeAndDate(mode, date)
                .filter(isEligibleSlot(policy))
                .map(mapper::toDto);
    }

    private Predicate<TimeSlot> isEligibleSlot(DeliveryPolicy policy) {
        return slot -> LocalDateTime.of(slot.getDate(), slot.getStartTime()).isAfter(LocalDateTime.now())
                && policy.isAllowed(slot)
                && slot.getCapacity() > 0;
    }

}
