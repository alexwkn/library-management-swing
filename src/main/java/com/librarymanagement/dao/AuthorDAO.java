package com.librarymanagement.dao;

import com.librarymanagement.model.Author;
import com.librarymanagement.model.BookSearchResult;

import java.util.List;

public interface AuthorDAO {

    List<Author> findAll();

    /** Livres d'un auteur (recherche partielle sur le nom et le prénom) dans une bibliothèque donnée. */
    List<BookSearchResult> findBooksByAuthorInLibrary(String firstName, String familyName, String libraryName);
}
