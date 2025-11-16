package com.carrefour.infrastructure.api;

import com.carrefour.application.dto.TimeSlotDto;
import com.carrefour.application.service.TimeSlotService;
import com.carrefour.domain.model.DeliveryMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.Mockito.*;

class TimeSlotControllerTest {

    @Mock
    private TimeSlotService timeSlotService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        TimeSlotController controller = new TimeSlotController(timeSlotService);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void getTimeSlots_shouldReturn200_andListOfTimeSlots() {
        LocalDate date = LocalDate.of(2025, 1, 1);

        TimeSlotDto dto = new TimeSlotDto(
                1L,
                DeliveryMode.DRIVE,
                date,
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                5
        );

        when(timeSlotService.getTimeSlots(DeliveryMode.DRIVE, date))
                .thenReturn(Flux.just(dto));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/timeslots")
                        .queryParam("mode", "DRIVE")
                        .queryParam("date", "2025-01-01")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TimeSlotDto.class)
                .hasSize(1)
                .contains(dto);

        verify(timeSlotService).getTimeSlots(DeliveryMode.DRIVE, date);
    }

    @Test
    void getTimeSlots_shouldReturn400_whenModeMissing() {

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/timeslots")
                        .queryParam("date", "2025-01-01")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getTimeSlots_shouldReturn400_whenDateMissing() {

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/timeslots")
                        .queryParam("mode", "DRIVE")
                        .build())
                .exchange()
                .expectStatus().isBadRequest();
    }
}
