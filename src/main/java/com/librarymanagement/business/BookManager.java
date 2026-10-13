package com.librarymanagement.business;

import com.librarymanagement.dao.BookDAO;
import com.librarymanagement.dao.JdbcBookDAO;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.Author;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.BookSearchResult;
import com.librarymanagement.util.FormValidator;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class BookManager {

    public static final int MAX_SUMMARY_LENGTH = 5000;
    public static final int MAX_COMMENTARY_LENGTH = 2000;
    private static final LocalDate OLDEST_PUBLICATION = LocalDate.of(1400, 1, 1);

    private final BookDAO bookDAO;

    public BookManager() {
        this(new JdbcBookDAO());
    }

    public BookManager(BookDAO bookDAO) {
        this.bookDAO = bookDAO;
    }

    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }

    public Optional<Book> getBook(int id) {
        return bookDAO.findById(id);
    }

    public void createBook(Book book) throws ValidationException {
        validate(book);
        bookDAO.create(book);
    }

    public void updateBook(Book book) throws ValidationException {
        if (book.getId() == null) {
            throw new ValidationException("L'identifiant du livre est requis pour la mise à jour.");
        }
        validate(book);
        bookDAO.update(book);
    }

    public void deleteBook(int id) {
        bookDAO.delete(id);
    }

    public List<BookSearchResult> getBooksByPublicationDateAndLibrary(LocalDate date, String libraryName, String category)
            throws ValidationException {
        if (date == null) throw new ValidationException("Veuillez choisir une date de publication.");
        if (FormValidator.isBlank(libraryName)) throw new ValidationException("Veuillez choisir une bibliothèque.");
        return bookDAO.findByPublicationDateAndLibrary(date, libraryName, category);
    }

    void validate(Book book) throws ValidationException {
        if (book.getIsbnCode() == null || String.valueOf(book.getIsbnCode()).length() != 13) {
            throw new ValidationException("Le code ISBN doit contenir exactement 13 chiffres.");
        }
        if (!FormValidator.hasLength(book.getTitle(), 2, 255)) {
            throw new ValidationException("Le titre doit contenir entre 2 et 255 caractères.");
        }
        LocalDate publication = book.getPublicationDate();
        if (publication == null || publication.isBefore(OLDEST_PUBLICATION) || publication.isAfter(LocalDate.now())) {
            throw new ValidationException("La date de publication est invalide (ni dans le futur, ni avant 1400).");
        }
        if (!FormValidator.hasLength(book.getLanguage(), 2, 50)) {
            throw new ValidationException("La langue doit contenir entre 2 et 50 caractères.");
        }
        if (book.getNbPages() < 1 || book.getNbPages() > 10_000) {
            throw new ValidationException("Le nombre de pages doit être compris entre 1 et 10 000.");
        }
        if (book.getPrice() < 0) {
            throw new ValidationException("Le prix ne peut pas être négatif.");
        }
        if (book.getSummary() != null && book.getSummary().length() > MAX_SUMMARY_LENGTH) {
            throw new ValidationException("Le résumé ne peut pas dépasser " + MAX_SUMMARY_LENGTH + " caractères.");
        }
        if (book.getCommentary() != null && book.getCommentary().length() > MAX_COMMENTARY_LENGTH) {
            throw new ValidationException("Le commentaire ne peut pas dépasser " + MAX_COMMENTARY_LENGTH + " caractères.");
        }
        if (book.getLibrary() == null || book.getLibrary().getId() == null) {
            throw new ValidationException("Veuillez choisir une bibliothèque.");
        }
        if (book.getCategories().isEmpty()) {
            throw new ValidationException("Le livre doit appartenir à au moins une catégorie.");
        }
        if (book.getAuthors().isEmpty()) {
            throw new ValidationException("Le livre doit avoir au moins un auteur.");
        }
        for (Author author : book.getAuthors()) {
            if (!FormValidator.hasLength(author.getFamilyName(), 1, 100)
                    || author.getFirstName() == null || author.getFirstName().length() > 100) {
                throw new ValidationException("Nom d'auteur invalide : " + author.getDisplayName());
            }
        }
    }
}
