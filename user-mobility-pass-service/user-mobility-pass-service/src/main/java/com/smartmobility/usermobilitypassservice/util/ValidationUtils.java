package com.smartmobility.usermobilitypassservice.util;

import com.smartmobility.usermobilitypassservice.exception.ValidationException;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(\\+221)?[0-9]{9}$");

    /**
     * Valide qu'une chaîne n'est pas vide ou nulle
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " est obligatoire");
        }
    }

    /**
     * Valide la longueur d'une chaîne
     */
    public static void validateLength(String value, String fieldName, int minLength, int maxLength) {
        if (value == null) {
            return;
        }

        if (value.length() < minLength || value.length() > maxLength) {
            throw new ValidationException(
                    fieldName + " doit contenir entre " + minLength + " et " + maxLength + " caractères");
        }
    }

    /**
     * Valide le format d'un email
     */
    public static void validateEmail(String email) {
        validateNotEmpty(email, "Email");

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Format d'email invalide");
        }
    }

    /**
     * Valide le format d'un numéro de téléphone
     */
    public static void validatePhoneNumber(String phoneNumber) {
        validateNotEmpty(phoneNumber, "Numéro de téléphone");

        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new ValidationException(
                    "Format de numéro de téléphone invalide. Format attendu: +221XXXXXXXXX ou XXXXXXXXX");
        }
    }

    /**
     * Valide un mot de passe
     */
    public static void validatePassword(String password) {
        validateNotEmpty(password, "Mot de passe");

        if (password.length() < 6) {
            throw new ValidationException("Le mot de passe doit contenir au moins 6 caractères");
        }
    }

    /**
     * Valide un nom (prénom ou nom de famille)
     */
    public static void validateName(String name, String fieldName) {
        validateNotEmpty(name, fieldName);
        validateLength(name, fieldName, 2, 100);
    }
}