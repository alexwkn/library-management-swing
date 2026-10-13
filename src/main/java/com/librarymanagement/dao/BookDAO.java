package com.librarymanagement.dao;

import com.librarymanagement.model.Book;
import com.librarymanagement.model.BookSearchResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookDAO {

    List<Book> findAll();

    Optional<Book> findById(int id);

    void create(Book book);

    void update(Book book);

    void delete(int id);

    /** Livres publiés à une date donnée dans une bibliothèque, éventuellement filtrés par catégorie. */
    List<BookSearchResult> findByPublicationDateAndLibrary(LocalDate publicationDate, String libraryName, String category);
}
