package com.librarymanagement.dao;

import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.model.Author;
import com.librarymanagement.model.BookSearchResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcAuthorDAO implements AuthorDAO {

    @Override
    public List<Author> findAll() {
        String sql = "SELECT id, family_name, first_name FROM author ORDER BY family_name, first_name";
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Author> authors = new ArrayList<>();
            while (rs.next()) {
                authors.add(new Author(rs.getInt("id"), rs.getString("family_name"), rs.getString("first_name")));
            }
            return authors;
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la récupération des auteurs.", e);
        }
    }

    @Override
    public List<BookSearchResult> findBooksByAuthorInLibrary(String firstName, String familyName, String libraryName) {
        String sql = """
                SELECT DISTINCT l.address, l.email, l.phone_number, c.label AS category,
                       b.title, b.is_available, b.language
                FROM book b
                JOIN book_author ba ON b.id = ba.book_id
                JOIN author a ON ba.author_id = a.id
                JOIN library l ON b.library_id = l.id
                JOIN book_category bc ON b.id = bc.book_id
                JOIN category c ON bc.category_id = c.label
                WHERE a.family_name LIKE ? AND a.first_name LIKE ? AND l.name = ?
                ORDER BY b.title
                """;
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)) {
            ps.setString(1, "%" + familyName.trim() + "%");
            ps.setString(2, "%" + firstName.trim() + "%");
            ps.setString(3, libraryName);
            return BookSearchResults.read(ps);
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la recherche des livres par auteur.", e);
        }
    }
}
