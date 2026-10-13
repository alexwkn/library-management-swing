package com.librarymanagement.business;

import com.librarymanagement.dao.JdbcLoanDAO;
import com.librarymanagement.dao.LoanDAO;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.AuthorStat;
import com.librarymanagement.model.LoanSearchResult;
import com.librarymanagement.util.FormValidator;

import java.time.LocalDate;
import java.util.List;

public class LoanManager {

    private final LoanDAO loanDAO;

    public LoanManager() {
        this(new JdbcLoanDAO());
    }

    public LoanManager(LoanDAO loanDAO) {
        this.loanDAO = loanDAO;
    }

    public List<LoanSearchResult> getLoansByPeriodAndLibrary(LocalDate from, LocalDate to, String libraryName)
            throws ValidationException {
        checkPeriod(from, to);
        if (FormValidator.isBlank(libraryName)) throw new ValidationException("Veuillez choisir une bibliothèque.");
        return loanDAO.findByPeriodAndLibrary(from, to, libraryName);
    }

    public List<AuthorStat> getMostReadAuthors(LocalDate from, LocalDate to) throws ValidationException {
        checkPeriod(from, to);
        return loanDAO.findMostReadAuthors(from, to);
    }

    private void checkPeriod(LocalDate from, LocalDate to) throws ValidationException {
        if (from == null || to == null) throw new ValidationException("Veuillez choisir une période.");
        if (from.isAfter(to)) throw new ValidationException("La date de début doit être antérieure à la date de fin.");
    }
}
