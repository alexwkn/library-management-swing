package com.librarymanagement.model;

/**
 * Ligne de résultat des recherches de livres (par auteur ou par date de publication).
 */
public record BookSearchResult(String libraryAddress, String libraryEmail, String libraryPhone,
                               String category, String title, boolean available, String language) {
}
