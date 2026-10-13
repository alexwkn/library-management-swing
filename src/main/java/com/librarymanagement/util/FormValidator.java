package com.librarymanagement.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Règles de validation des champs de formulaire, partagées par les vues et la couche métier.
 */
public final class FormValidator {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern DIGITS = Pattern.compile("^[0-9]+$");
    private static final LocalDate MIN_BIRTHDATE = LocalDate.of(1900, 1, 1);

    private FormValidator() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL.matcher(email).matches();
    }

    public static boolean isNumber(String value) {
        return value != null && DIGITS.matcher(value).matches();
    }

    public static boolean isValidBirthdate(LocalDate birthdate) {
        return birthdate != null && !birthdate.isBefore(MIN_BIRTHDATE) && !birthdate.isAfter(LocalDate.now());
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean containsSpace(String value) {
        return value != null && value.chars().anyMatch(Character::isWhitespace);
    }

    public static boolean hasLength(String value, int min, int max) {
        return value != null && value.length() >= min && value.length() <= max;
    }

    /** Retourne la chaîne sans espaces superflus, ou {@code null} si elle est vide. */
    public static String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
