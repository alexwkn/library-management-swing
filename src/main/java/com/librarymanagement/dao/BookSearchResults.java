package com.librarymanagement.dao;

import com.librarymanagement.model.BookSearchResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Lecture commune des résultats de recherche de livres. */
final class BookSearchResults {

    private BookSearchResults() {
    }

    static List<BookSearchResult> read(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            List<BookSearchResult> results = new ArrayList<>();
            while (rs.next()) {
                results.add(new BookSearchResult(
                        rs.getString("address"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("category"),
                        rs.getString("title"),
                        rs.getBoolean("is_available"),
                        rs.getString("language")));
            }
            return results;
        }
    }
}
