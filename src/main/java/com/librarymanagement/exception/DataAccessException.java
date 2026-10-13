package com.librarymanagement.exception;

/**
 * Erreur technique lors d'un accès à la base de données (connexion, requête SQL...).
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
