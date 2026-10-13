package com.librarymanagement.dao;

import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.model.Author;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.BookSearchResult;
import com.librarymanagement.model.Category;
import com.librarymanagement.model.Library;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcBookDAO implements BookDAO {

    private static final String SELECT_BOOKS = """
            SELECT b.*, l.name AS lib_name, l.address AS lib_address,
                   l.email AS lib_email, l.phone_number AS lib_phone
            FROM book b
            JOIN library l ON b.library_id = l.id
            """;

    @Override
    public List<Book> findAll() {
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(SELECT_BOOKS + " ORDER BY b.title")) {
            Map<Integer, Book> books = readBooks(ps);
            loadAuthorsAndCategories(books);
            return new ArrayList<>(books.values());
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la récupération des livres.", e);
        }
    }

    @Override
    public Optional<Book> findById(int id) {
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(SELECT_BOOKS + " WHERE b.id = ?")) {
            ps.setInt(1, id);
            Map<Integer, Book> books = readBooks(ps);
            loadAuthorsAndCategories(books);
            return books.values().stream().findFirst();
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la récupération du livre.", e);
        }
    }

    @Override
    public void create(Book book) {
        save(book, true);
    }

    @Override
    public void update(Book book) {
        save(book, false);
    }

    /**
     * Enregistre le livre et ses associations (auteurs, catégories) dans une seule transaction :
     * en cas d'erreur, rien n'est modifié en base.
     */
    private void save(Book book, boolean isCreation) {
        String insert = "INSERT INTO book (isbncode, title, publication_date, language, nb_pages, is_available, "
                + "price, summary, commentary, library_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String update = "UPDATE book SET isbncode = ?, title = ?, publication_date = ?, language = ?, nb_pages = ?, "
                + "is_available = ?, price = ?, summary = ?, commentary = ?, library_id = ? WHERE id = ?";

        Connection connection = DatabaseConnection.get();
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement ps = connection.prepareStatement(isCreation ? insert : update,
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, book.getIsbnCode());
                ps.setString(2, book.getTitle());
                ps.setObject(3, book.getPublicationDate());
                ps.setString(4, book.getLanguage());
                ps.setInt(5, book.getNbPages());
                ps.setBoolean(6, book.isAvailable());
                ps.setDouble(7, book.getPrice());
                ps.setString(8, book.getSummary());
                ps.setString(9, book.getCommentary());
                ps.setInt(10, book.getLibrary().getId());
                if (!isCreation) ps.setInt(11, book.getId());

                if (ps.executeUpdate() == 0) {
                    throw new DataAccessException("Le livre n'existe plus en base.");
                }
                if (isCreation) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (!keys.next()) throw new DataAccessException("Identifiant du nouveau livre introuvable.");
                        book.setId(keys.getInt(1));
                    }
                }
            }
            replaceAssociations(connection, book);
            connection.commit();
        } catch (SQLException | DataAccessException e) {
            rollback(connection);
            if (e instanceof DataAccessException dae) throw dae;
            throw new DataAccessException("Erreur lors de l'enregistrement du livre : " + e.getMessage(), e);
        } finally {
            restoreAutoCommit(connection);
        }
    }

    private void replaceAssociations(Connection connection, Book book) throws SQLException {
        int bookId = book.getId();
        try (PreparedStatement deleteAuthors = connection.prepareStatement("DELETE FROM book_author WHERE book_id = ?");
             PreparedStatement deleteCategories = connection.prepareStatement("DELETE FROM book_category WHERE book_id = ?")) {
            deleteAuthors.setInt(1, bookId);
            deleteAuthors.executeUpdate();
            deleteCategories.setInt(1, bookId);
            deleteCategories.executeUpdate();
        }
        try (PreparedStatement insertAuthor = connection.prepareStatement(
                "INSERT INTO book_author (book_id, author_id) VALUES (?, ?)")) {
            for (Author author : book.getAuthors()) {
                insertAuthor.setInt(1, bookId);
                insertAuthor.setInt(2, findOrCreateAuthor(connection, author));
                insertAuthor.addBatch();
            }
            insertAuthor.executeBatch();
        }
        try (PreparedStatement insertCategory = connection.prepareStatement(
                "INSERT INTO book_category (book_id, category_id) VALUES (?, ?)")) {
            for (Category category : book.getCategories()) {
                insertCategory.setInt(1, bookId);
                insertCategory.setString(2, category.getLabel());
                insertCategory.addBatch();
            }
            insertCategory.executeBatch();
        }
    }

    private int findOrCreateAuthor(Connection connection, Author author) throws SQLException {
        try (PreparedStatement search = connection.prepareStatement(
                "SELECT id FROM author WHERE family_name = ? AND first_name = ?")) {
            search.setString(1, author.getFamilyName());
            search.setString(2, author.getFirstName());
            try (ResultSet rs = search.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO author (family_name, first_name) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, author.getFamilyName());
            insert.setString(2, author.getFirstName());
            insert.executeUpdate();
            try (ResultSet keys = insert.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Identifiant du nouvel auteur introuvable.");
                return keys.getInt(1);
            }
        }
    }

    @Override
    public void delete(int id) {
        Connection connection = DatabaseConnection.get();
        try {
            connection.setAutoCommit(false);
            for (String sql : List.of(
                    "DELETE FROM book_author WHERE book_id = ?",
                    "DELETE FROM book_category WHERE book_id = ?",
                    "DELETE FROM book WHERE id = ?")) {
                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, id);
                    ps.executeUpdate();
                }
            }
            connection.commit();
        } catch (SQLIntegrityConstraintViolationException e) {
            rollback(connection);
            throw new DataAccessException("Impossible de supprimer ce livre : il est lié à un ou plusieurs emprunts.", e);
        } catch (SQLException e) {
            rollback(connection);
            throw new DataAccessException("Erreur lors de la suppression du livre.", e);
        } finally {
            restoreAutoCommit(connection);
        }
    }

    @Override
    public List<BookSearchResult> findByPublicationDateAndLibrary(LocalDate publicationDate, String libraryName, String category) {
        boolean filterCategory = category != null && !category.isBlank();
        String sql = """
                SELECT DISTINCT l.address, l.email, l.phone_number, c.label AS category,
                       b.title, b.is_available, b.language
                FROM book b
                JOIN library l ON b.library_id = l.id
                JOIN book_category bc ON b.id = bc.book_id
                JOIN category c ON bc.category_id = c.label
                WHERE b.publication_date = ? AND l.name = ?
                """ + (filterCategory ? " AND c.label = ?" : "") + " ORDER BY b.title";
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)) {
            ps.setObject(1, publicationDate);
            ps.setString(2, libraryName);
            if (filterCategory) ps.setString(3, category);
            return BookSearchResults.read(ps);
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la recherche des livres par date.", e);
        }
    }

    private Map<Integer, Book> readBooks(PreparedStatement ps) throws SQLException {
        Map<Integer, Book> books = new LinkedHashMap<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setIsbnCode(rs.getLong("isbncode"));
                book.setTitle(rs.getString("title"));
                book.setPublicationDate(rs.getObject("publication_date", LocalDate.class));
                book.setLanguage(rs.getString("language"));
                book.setNbPages(rs.getInt("nb_pages"));
                book.setAvailable(rs.getBoolean("is_available"));
                book.setPrice(rs.getDouble("price"));
                book.setSummary(rs.getString("summary"));
                book.setCommentary(rs.getString("commentary"));
                book.setLibrary(new Library(rs.getInt("library_id"), rs.getString("lib_name"),
                        rs.getString("lib_address"), rs.getString("lib_email"), rs.getString("lib_phone")));
                books.put(book.getId(), book);
            }
        }
        return books;
    }

    /**
     * Charge auteurs et catégories en deux requêtes au lieu de deux requêtes par livre.
     * La clause IN ne contient que des identifiants entiers lus en base : pas de risque d'injection.
     */
    private void loadAuthorsAndCategories(Map<Integer, Book> books) throws SQLException {
        if (books.isEmpty()) return;
        String ids = String.join(",", books.keySet().stream().map(String::valueOf).toList());
        Connection connection = DatabaseConnection.get();

        String authorsSql = "SELECT ba.book_id, a.id, a.family_name, a.first_name FROM book_author ba "
                + "JOIN author a ON a.id = ba.author_id WHERE ba.book_id IN (" + ids + ") ORDER BY a.family_name";
        try (PreparedStatement ps = connection.prepareStatement(authorsSql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.get(rs.getInt("book_id")).getAuthors().add(
                        new Author(rs.getInt("id"), rs.getString("family_name"), rs.getString("first_name")));
            }
        }

        String categoriesSql = "SELECT book_id, category_id FROM book_category WHERE book_id IN (" + ids + ") "
                + "ORDER BY category_id";
        try (PreparedStatement ps = connection.prepareStatement(categoriesSql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                books.get(rs.getInt("book_id")).getCategories().add(new Category(rs.getString("category_id")));
            }
        }
    }

    private static void rollback(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // La transaction sera de toute façon abandonnée
        }
    }

    private static void restoreAutoCommit(Connection connection) {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException ignored) {
            // Connexion probablement fermée : elle sera rouverte au prochain appel
        }
    }
}
