package com.carrefour.infrastructure.config;

import com.carrefour.application.port.ReservationRepository;
import com.carrefour.application.port.TimeSlotRepository;
import com.carrefour.application.service.ReservationService;
import com.carrefour.application.service.TimeSlotService;
import com.carrefour.domain.policy.DeliveryPolicyFactory;
import com.carrefour.domain.service.ReservationDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public TimeSlotService timeSlotService(TimeSlotRepository timeSlotRepository, ReservationDomainService reservationDomainService) {
        return new TimeSlotService(timeSlotRepository, reservationDomainService);
    }

    @Bean
    public ReservationService reservationService(TimeSlotRepository timeSlotRepository,
                                                 ReservationRepository reservationRepository,
                                                 ReservationDomainService reservationDomainService) {
        return new ReservationService(timeSlotRepository, reservationRepository, reservationDomainService);
    }

    @Bean
    public DeliveryPolicyFactory deliveryPolicyFactory(){
        return new DeliveryPolicyFactory();
    }

    @Bean
    public ReservationDomainService reservationDomainService(DeliveryPolicyFactory deliveryPolicyFactory) {
        return new ReservationDomainService(deliveryPolicyFactory);
    }
}
