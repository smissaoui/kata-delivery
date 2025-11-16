package com.carrefour.infrastructure.api.error;

import com.carrefour.application.exception.ReservationNotFoundException;
import com.carrefour.application.exception.TimeSlotNotFoundException;
import com.carrefour.domain.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse build(HttpStatus status, String message) {
        return new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message
        );
    }

    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<ErrorResponse> handleDomainException(DomainException ex) {
        return Mono.just(
                build(HttpStatus.CONFLICT, ex.getMessage())
        );
    }

    @ExceptionHandler(TimeSlotNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ErrorResponse> handleTimeSlotNotFoundException(TimeSlotNotFoundException ex) {
        return Mono.just(
                build(HttpStatus.NOT_FOUND, ex.getMessage())
        );
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ErrorResponse> handleReservationNotFoundException(ReservationNotFoundException ex) {
        return Mono.just(
                build(HttpStatus.NOT_FOUND, ex.getMessage())
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Mono<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return Mono.just(
                build(HttpStatus.CONFLICT, ex.getMessage())
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return Mono.just(
                build(HttpStatus.BAD_REQUEST, ex.getMessage())
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Mono<ErrorResponse> handleGeneric(Exception ex) {
        return Mono.just(
                build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage())
        );
    }
}
