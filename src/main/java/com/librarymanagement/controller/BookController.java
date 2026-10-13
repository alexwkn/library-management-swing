package com.librarymanagement.controller;

import com.librarymanagement.business.BookManager;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.BookSearchResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Point d'entrée des vues pour tout ce qui concerne les livres. */
public class BookController {

    private final BookManager bookManager = new BookManager();

    public List<Book> getAllBooks() {
        return bookManager.getAllBooks();
    }

    public Optional<Book> getBook(int id) {
        return bookManager.getBook(id);
    }

    public void createBook(Book book) throws ValidationException {
        bookManager.createBook(book);
    }

    public void updateBook(Book book) throws ValidationException {
        bookManager.updateBook(book);
    }

    public void deleteBook(int id) {
        bookManager.deleteBook(id);
    }

    public List<BookSearchResult> getBooksByPublicationDateAndLibrary(LocalDate date, String libraryName, String category)
            throws ValidationException {
        return bookManager.getBooksByPublicationDateAndLibrary(date, libraryName, category);
    }
}
