package com.librarymanagement.model;

/**
 * Résultat de la tâche métier « auteurs les plus lus sur une période ».
 */
public record AuthorStat(Author author, int nbLoans, double percentage) {
}
