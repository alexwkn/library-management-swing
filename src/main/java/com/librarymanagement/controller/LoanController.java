package com.librarymanagement.controller;

import com.librarymanagement.business.LoanManager;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.AuthorStat;
import com.librarymanagement.model.LoanSearchResult;

import java.time.LocalDate;
import java.util.List;

/** Point d'entrée des vues pour tout ce qui concerne les emprunts. */
public class LoanController {

    private final LoanManager loanManager = new LoanManager();

    public List<LoanSearchResult> getLoansByPeriodAndLibrary(LocalDate from, LocalDate to, String libraryName)
            throws ValidationException {
        return loanManager.getLoansByPeriodAndLibrary(from, to, libraryName);
    }

    public List<AuthorStat> getMostReadAuthors(LocalDate from, LocalDate to) throws ValidationException {
        return loanManager.getMostReadAuthors(from, to);
    }
}
