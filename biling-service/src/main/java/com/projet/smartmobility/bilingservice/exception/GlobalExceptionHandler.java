package com.projet.smartmobility.bilingservice.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    /** Erreur métier : solde insuffisant ou pass inactif (levée par user-mobility-pass-service via Feign). */
    @ExceptionHandler(FeignException.BadRequest.class)
    public ProblemDetail handleFeignBadRequest(FeignException.BadRequest ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Opération refusée par le service de pass : " + ex.getMessage());
        pd.setTitle("Opération invalide");
        pd.setType(URI.create("billing-service/invalid-operation"));
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    /** user-mobility-pass-service introuvable (Feign 404). */
    @ExceptionHandler(FeignException.NotFound.class)
    public ProblemDetail handleFeignNotFound(FeignException.NotFound ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                "Pass ou utilisateur introuvable : " + ex.getMessage());
        pd.setTitle("Ressource introuvable");
        pd.setType(URI.create("billing-service/not-found"));
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    /** Circuit ouvert — user-mobility-pass-service indisponible. */
    @ExceptionHandler(ServiceUnavailableException.class)
    public ProblemDetail handleServiceUnavailable(ServiceUnavailableException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        pd.setTitle("Service indisponible");
        pd.setType(URI.create("billing-service/service-unavailable"));
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    /** Erreurs de validation Bean Validation. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, details);
        pd.setTitle("Erreur de validation");
        pd.setType(URI.create("billing-service/validation-error"));
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    /** Erreur générique. */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Une erreur interne s'est produite");
        pd.setTitle("Erreur interne");
        pd.setType(URI.create("billing-service/internal-error"));
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}