package com.librarymanagement.model;

import java.time.LocalDate;

/**
 * Ligne de résultat de la recherche d'emprunts.
 * {@code returnDate} peut être {@code null} si aucune date de retour n'est encore fixée.
 */
public record LoanSearchResult(String bookTitle, LocalDate loanDate, LocalDate returnDate, boolean returned,
                               String libraryAddress, String libraryPhone, String libraryEmail) {
}
