package com.carrefour.infrastructure.api;

import com.carrefour.application.dto.CreateReservationCommand;
import com.carrefour.application.service.ReservationService;
import com.carrefour.domain.model.Reservation;
import com.carrefour.infrastructure.api.dto.CreateReservationRequest;
import com.carrefour.infrastructure.api.dto.ReservationResponse;
import com.carrefour.infrastructure.api.mapper.ReservationApiMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/v1/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationApiMapper mapper;

    public ReservationController(ReservationService reservationService, ReservationApiMapper mapper) {
        this.reservationService = reservationService;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create a new reservation",
            description = "Reserves a specific time slot for a given customer."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Reservation successfully created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "409", description = "Time slot already reserved or capacity full"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public Mono<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request
    ) {
        log.info("Received request to create a reservation for customerId='{}', timeSlotId='{}'",
                request.customerId(), request.timeSlotId());
        CreateReservationCommand command = new CreateReservationCommand(
                request.customerId(),
                request.timeSlotId()
        );

        return reservationService.createReservation(command)
                .map(mapper::toResponse);
    }

    @DeleteMapping("/{customerId}/{timeSlotId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Cancel an existing reservation",
            description = "Releases the associated time slot capacity and deletes the reservation."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Reservation successfully cancelled"
            ),
            @ApiResponse(responseCode = "400", description = "Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "404", description = "Reservation not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public Mono<Void> cancelReservation(@PathVariable @NotBlank(message = "customerId must not be blank") String customerId,
                                        @PathVariable @NotNull(message = "timeSlotId must not be null")
                                        @Positive(message = "timeSlotId must be a positive number")Long timeSlotId) {
        log.info("Received request to cancel reservation for customerId='{}', timeSlotId='{}'",
                customerId, timeSlotId);
        return reservationService.cancelReservation(customerId, timeSlotId);
    }
}
