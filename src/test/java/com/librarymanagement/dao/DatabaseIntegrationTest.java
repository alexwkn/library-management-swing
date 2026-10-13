package com.librarymanagement.dao;

import com.librarymanagement.model.Library;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests en lecture seule sur la base de démonstration ({@code docker compose up -d}).
 * Ils sont ignorés automatiquement si la base n'est pas joignable.
 */
class DatabaseIntegrationTest {

    @BeforeAll
    static void requireDatabase() {
        boolean available;
        try {
            DatabaseConnection.get();
            available = true;
        } catch (RuntimeException e) {
            available = false;
        }
        assumeTrue(available, "Base de données indisponible : tests d'intégration ignorés");
    }

    @Test
    void loadsTheFourLibraries() {
        List<Library> libraries = new JdbcLibraryDAO().findAll();
        assertEquals(4, libraries.size());
    }

    @Test
    void loadsBooksWithAuthorsAndCategories() {
        new JdbcBookDAO().findAll().forEach(book -> {
            assertFalse(book.getAuthors().isEmpty(), "Livre sans auteur : " + book.getTitle());
            assertFalse(book.getCategories().isEmpty(), "Livre sans catégorie : " + book.getTitle());
        });
    }

    @Test
    void findsBooksByAuthor() {
        var results = new JdbcAuthorDAO().findBooksByAuthorInLibrary("Victor", "Hugo", "Bibliothèque communale de Jambes");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(r -> r.title().equals("Les misérables")));
    }

    @Test
    void computesMostReadAuthors() {
        var stats = new JdbcLoanDAO().findMostReadAuthors(LocalDate.of(2000, 1, 1), LocalDate.now());
        assertFalse(stats.isEmpty());
        double total = stats.stream().mapToDouble(s -> s.percentage()).sum();
        assertEquals(100.0, total, 0.01);
    }

    @Test
    void storedPasswordsAreHashed() throws Exception {
        try (var ps = DatabaseConnection.get().prepareStatement("SELECT password FROM user");
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                assertTrue(rs.getString(1).startsWith("pbkdf2$"), "Mot de passe non haché en base");
            }
        }
    }
}
