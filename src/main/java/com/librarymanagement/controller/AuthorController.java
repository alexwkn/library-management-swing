package com.librarymanagement.controller;

import com.librarymanagement.business.AuthorManager;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.Author;
import com.librarymanagement.model.BookSearchResult;

import java.util.List;

/** Point d'entrée des vues pour tout ce qui concerne les auteurs. */
public class AuthorController {

    private final AuthorManager authorManager = new AuthorManager();

    public List<Author> getAllAuthors() {
        return authorManager.getAllAuthors();
    }

    public List<BookSearchResult> getBooksByAuthorInLibrary(String firstName, String familyName, String libraryName)
            throws ValidationException {
        return authorManager.getBooksByAuthorInLibrary(firstName, familyName, libraryName);
    }
}
