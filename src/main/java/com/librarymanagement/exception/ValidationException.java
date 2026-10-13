package com.librarymanagement.exception;

/**
 * Donnée invalide ou règle métier non respectée (champ obligatoire manquant, doublon...).
 * Le message est destiné à être affiché tel quel à l'utilisateur.
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }
}
