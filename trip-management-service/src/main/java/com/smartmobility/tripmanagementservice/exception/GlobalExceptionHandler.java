package com.smartmobility.tripmanagementservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TripException.class)
    public ProblemDetail handleTripRejected(TripException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setTitle("Trajet rejeté");
        pd.setType(URI.create("trip-service/trip-rejected"));
        pd.setProperty("reason", ex.getReason());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
    

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errors);
        pd.setTitle("Données invalides");
        pd.setType(URI.create("trip-service/validation-error"));
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur interne s'est produite");
        pd.setTitle("Erreur interne");
        pd.setType(URI.create("trip-service/internal-error"));
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ProblemDetail handleServiceUnavailable(ServiceUnavailableException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        pd.setTitle("Service indisponible");
        pd.setType(URI.create("trip-service/service-unavailable"));
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(feign.FeignException.class)
    public ProblemDetail handleFeignException(feign.FeignException ex) {
        // ex.request().url() contient l'URL appelée → on extrait le nom du service
        String serviceName = ex.request() != null
                ? URI.create(ex.request().url()).getHost()
                : "service inconnu";

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Le service '" + serviceName + "' est indisponible.");
        pd.setTitle("Service indisponible");
        pd.setType(URI.create("trip-service/service-unavailable"));
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(io.github.resilience4j.circuitbreaker.CallNotPermittedException.class)
    public ProblemDetail handleCircuitBreaker(io.github.resilience4j.circuitbreaker.CallNotPermittedException ex) {
        // ex.getCausingCircuitBreakerName() retourne le nom exact du CB (ex: "pricingCB")
        String cbName = ex.getCausingCircuitBreakerName();

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Circuit breaker '" + cbName + "' ouvert — service temporairement indisponible.");
        pd.setTitle("Service indisponible");
        pd.setType(URI.create("trip-service/circuit-open/" + cbName));
        pd.setProperty("circuitBreaker", cbName);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}