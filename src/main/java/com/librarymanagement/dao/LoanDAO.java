package com.librarymanagement.dao;

import com.librarymanagement.model.AuthorStat;
import com.librarymanagement.model.LoanSearchResult;

import java.time.LocalDate;
import java.util.List;

public interface LoanDAO {

    /** Emprunts effectués entre deux dates dans une bibliothèque. */
    List<LoanSearchResult> findByPeriodAndLibrary(LocalDate from, LocalDate to, String libraryName);

    /** Nombre d'emprunts par auteur sur une période, trié du plus lu au moins lu. */
    List<AuthorStat> findMostReadAuthors(LocalDate from, LocalDate to);
}
