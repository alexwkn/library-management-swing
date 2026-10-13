package com.librarymanagement.model;

/**
 * Résultat de la tâche métier « catégories préférées par tranche d'âge ».
 */
public record CategoryStat(String label, int nbUsers, double percentage) {
}
