package com.librarymanagement.dao;

import com.librarymanagement.exception.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connexion JDBC unique partagée par toute l'application.
 * <p>
 * Les paramètres sont lus dans les variables d'environnement {@code DB_URL}, {@code DB_USER}
 * et {@code DB_PASSWORD} (ou les propriétés système Java du même nom). Les valeurs par défaut
 * correspondent à la base MySQL lancée par le {@code docker-compose.yml} du projet.
 * <p>
 * Les DAO ne doivent jamais fermer cette connexion : seuls leurs statements et result sets le sont.
 */
public final class DatabaseConnection {

    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3307/librarynamur?allowPublicKeyRetrieval=true&useSSL=false";
    private static final String DEFAULT_USER = "library";
    private static final String DEFAULT_PASSWORD = "library";

    private static Connection connection;

    private DatabaseConnection() {
    }

    public static synchronized Connection get() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(
                        setting("DB_URL", DEFAULT_URL),
                        setting("DB_USER", DEFAULT_USER),
                        setting("DB_PASSWORD", DEFAULT_PASSWORD));
            }
            return connection;
        } catch (SQLException e) {
            throw new DataAccessException("Impossible de se connecter à la base de données : " + e.getMessage(), e);
        }
    }

    public static synchronized void close() {
        if (connection == null) return;
        try {
            connection.close();
        } catch (SQLException ignored) {
            // Fermeture en fin de programme : rien d'autre à faire
        } finally {
            connection = null;
        }
    }

    private static String setting(String name, String defaultValue) {
        String value = System.getProperty(name, System.getenv(name));
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
