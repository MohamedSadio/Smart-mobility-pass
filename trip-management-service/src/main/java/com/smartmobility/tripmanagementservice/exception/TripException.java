package com.smartmobility.tripmanagementservice.exception;

/**
 * Levée quand le trajet est rejeté pour raison métier
 * (pass inactif ou solde insuffisant).
 */
public class TripException extends RuntimeException {
    private final String reason;

    public TripException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}