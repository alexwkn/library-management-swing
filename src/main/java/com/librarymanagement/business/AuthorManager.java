package com.librarymanagement.business;

import com.librarymanagement.dao.AuthorDAO;
import com.librarymanagement.dao.JdbcAuthorDAO;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.Author;
import com.librarymanagement.model.BookSearchResult;
import com.librarymanagement.util.FormValidator;

import java.util.List;

public class AuthorManager {

    private final AuthorDAO authorDAO;

    public AuthorManager() {
        this(new JdbcAuthorDAO());
    }

    public AuthorManager(AuthorDAO authorDAO) {
        this.authorDAO = authorDAO;
    }

    public List<Author> getAllAuthors() {
        return authorDAO.findAll();
    }

    public List<BookSearchResult> getBooksByAuthorInLibrary(String firstName, String familyName, String libraryName)
            throws ValidationException {
        String first = firstName == null ? "" : firstName.trim();
        String family = familyName == null ? "" : familyName.trim();
        if (first.isEmpty() && family.isEmpty()) {
            throw new ValidationException("Veuillez renseigner au moins un nom ou un prénom d'auteur.");
        }
        if (FormValidator.isBlank(libraryName)) {
            throw new ValidationException("Veuillez choisir une bibliothèque.");
        }
        return authorDAO.findBooksByAuthorInLibrary(first, family, libraryName);
    }
}
