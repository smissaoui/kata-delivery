package com.carrefour.infrastructure.api;

import com.carrefour.application.dto.TimeSlotDto;
import com.carrefour.application.service.TimeSlotService;
import com.carrefour.domain.model.DeliveryMode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Slf4j
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/v1/timeslots")
@Tag(name = "Time Slots", description = "API for querying available delivery time slots")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    public TimeSlotController(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    @GetMapping
    @Operation(
            summary = "Fetch available time slots",
            description = "Returns all time slots filtered by delivery mode and date.",
            parameters = {
                    @Parameter(
                            name = "mode",
                            description = "Desired delivery mode",
                            required = true,
                            example = "DRIVE",
                            schema = @Schema(implementation = DeliveryMode.class)
                    ),
                    @Parameter(
                            name = "date",
                            description = "Desired date (YYYY-MM-DD)",
                            required = true,
                            example = "2025-01-01",
                            schema = @Schema(type = "string", format = "date")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of available time slots",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = TimeSlotDto.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid parameters"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing token"),
                    @ApiResponse(responseCode = "500", description = "Unexpected server error")
            }
    )
    public Flux<TimeSlotDto> getTimeSlots(
            @Parameter(description = "Desired delivery mode", example = "DRIVE", required = true)
            @RequestParam @NotNull(message = "mode must not be null") DeliveryMode mode,

            @Parameter(description = "Desired date (YYYY-MM-DD)", example = "2025-01-01", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("Received request to fetch time slots for mode='{}' and date='{}'",
                mode, date);
        return timeSlotService.getTimeSlots(mode, date);
    }
}
