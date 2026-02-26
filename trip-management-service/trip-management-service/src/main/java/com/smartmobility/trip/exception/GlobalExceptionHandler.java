package com.smartmobility.trip.exception;

import com.smartmobility.trip.dto.TripDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<TripDto.ApiError> handleTripNotFound(TripNotFoundException ex) {
        log.warn("[EXCEPTION] TripNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                TripDto.ApiError.builder()
                        .status(404)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<TripDto.ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            fieldErrors.put(field, error.getDefaultMessage());
        });
        log.warn("[EXCEPTION] Validation failed: {}", fieldErrors);
        return ResponseEntity.badRequest().body(
                TripDto.ApiError.builder()
                        .status(400)
                        .message("Erreur de validation")
                        .errors(fieldErrors)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<TripDto.ApiError> handleGeneral(Exception ex) {
        log.error("[EXCEPTION] Unhandled: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                TripDto.ApiError.builder()
                        .status(500)
                        .message("Erreur interne du serveur")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
