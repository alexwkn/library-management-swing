package com.librarymanagement.business;

import com.librarymanagement.dao.BookDAO;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.Author;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.BookSearchResult;
import com.librarymanagement.model.Category;
import com.librarymanagement.model.Library;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookManagerTest {

    /** DAO factice qui se contente de mémoriser les livres créés. */
    private final List<Book> created = new ArrayList<>();
    private final BookManager manager = new BookManager(new BookDAO() {
        @Override public List<Book> findAll() { return created; }
        @Override public Optional<Book> findById(int id) { return Optional.empty(); }
        @Override public void create(Book book) { created.add(book); }
        @Override public void update(Book book) { }
        @Override public void delete(int id) { }
        @Override public List<BookSearchResult> findByPublicationDateAndLibrary(LocalDate d, String l, String c) {
            return List.of();
        }
    });

    private static Book validBook() {
        Book book = new Book();
        book.setIsbnCode(9782070360024L);
        book.setTitle("L'Étranger");
        book.setPublicationDate(LocalDate.of(1942, 5, 19));
        book.setLanguage("Français");
        book.setNbPages(184);
        book.setPrice(8.90);
        book.setLibrary(new Library(1, "Bibliothèque de test", null, null, null));
        book.setAuthors(List.of(new Author(null, "Camus", "Albert")));
        book.setCategories(List.of(new Category("Roman noir")));
        return book;
    }

    @Test
    void createsValidBook() {
        assertDoesNotThrow(() -> manager.createBook(validBook()));
        assertEquals(1, created.size());
    }

    @Test
    void keepsApostrophesInTitle() throws ValidationException {
        manager.createBook(validBook());
        assertEquals("L'Étranger", created.get(0).getTitle());
    }

    @Test
    void rejectsInvalidIsbn() {
        Book book = validBook();
        book.setIsbnCode(123L);
        assertThrows(ValidationException.class, () -> manager.createBook(book));
    }

    @Test
    void rejectsFuturePublicationDate() {
        Book book = validBook();
        book.setPublicationDate(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> manager.createBook(book));
    }

    @Test
    void requiresAtLeastOneAuthor() {
        Book book = validBook();
        book.setAuthors(List.of());
        assertThrows(ValidationException.class, () -> manager.createBook(book));
    }

    @Test
    void requiresAtLeastOneCategory() {
        Book book = validBook();
        book.setCategories(List.of());
        assertThrows(ValidationException.class, () -> manager.createBook(book));
    }

    @Test
    void acceptsAuthorWithoutFirstName() {
        Book book = validBook();
        book.setAuthors(List.of(new Author(null, "Molière", "")));
        assertDoesNotThrow(() -> manager.createBook(book));
    }

    @Test
    void rejectsNegativePrice() {
        Book book = validBook();
        book.setPrice(-1);
        assertThrows(ValidationException.class, () -> manager.createBook(book));
    }

    @Test
    void requiresIdForUpdate() {
        assertThrows(ValidationException.class, () -> manager.updateBook(validBook()));
    }
}
