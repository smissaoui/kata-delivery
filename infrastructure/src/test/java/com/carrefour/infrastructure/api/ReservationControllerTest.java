package com.carrefour.infrastructure.api;

import com.carrefour.application.dto.CreateReservationCommand;
import com.carrefour.application.service.ReservationService;
import com.carrefour.domain.model.Reservation;
import com.carrefour.infrastructure.api.dto.CreateReservationRequest;
import com.carrefour.infrastructure.api.dto.ReservationResponse;
import com.carrefour.infrastructure.api.mapper.ReservationApiMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private ReservationApiMapper mapper;

    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        ReservationController controller = new ReservationController(reservationService, mapper);

        webTestClient = WebTestClient.bindToController(controller)
                .configureClient()
                .build();
    }

    @Test
    void createReservation_shouldReturn201_andResponseBody() {
        LocalDateTime now = LocalDateTime.now();

        CreateReservationRequest request = new CreateReservationRequest("C123", 10L);

        Reservation reservation = new Reservation("C123", 10L);
        reservation.setId(1L);
        reservation.setCreatedAt(now);

        ReservationResponse response = new ReservationResponse(
                1L,
                "C123",
                10L,
                now
        );

        when(reservationService.createReservation(any(CreateReservationCommand.class)))
                .thenReturn(Mono.just(reservation));

        when(mapper.toResponse(reservation)).thenReturn(response);

        webTestClient.post()
                .uri("/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {"customerId":"C123","timeSlotId":10}
                """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ReservationResponse.class)
                .isEqualTo(response);

        verify(reservationService).createReservation(any(CreateReservationCommand.class));
        verify(mapper).toResponse(reservation);
    }

    @Test
    void createReservation_shouldReturn400_whenPayloadInvalid() {
        // Missing "customerId"
        webTestClient.post()
                .uri("/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {"timeSlotId":10}
                """)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void cancelReservation_shouldReturn204() {
        when(reservationService.cancelReservation("C123", 10L))
                .thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/v1/reservations/C123/10")
                .exchange()
                .expectStatus().isNoContent();

        verify(reservationService).cancelReservation("C123", 10L);
    }

    @Test
    void cancelReservation_shouldReturn400_whenServiceThrowsBadRequest() {

        when(reservationService.cancelReservation("C123", -1L))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid timeSlotId")));

        webTestClient.delete()
                .uri("/v1/reservations/C123/-1")
                .exchange()
                .expectStatus().isBadRequest();
    }

}
