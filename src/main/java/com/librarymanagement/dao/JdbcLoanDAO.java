package com.librarymanagement.dao;

import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.model.Author;
import com.librarymanagement.model.AuthorStat;
import com.librarymanagement.model.LoanSearchResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcLoanDAO implements LoanDAO {

    @Override
    public List<LoanSearchResult> findByPeriodAndLibrary(LocalDate from, LocalDate to, String libraryName) {
        String sql = """
                SELECT b.title, lo.loan_date, lo.return_date, lo.is_returned,
                       lib.address, lib.phone_number, lib.email
                FROM loan lo
                JOIN book b ON lo.book_id = b.id
                JOIN library lib ON b.library_id = lib.id
                WHERE lo.loan_date BETWEEN ? AND ? AND lib.name = ?
                ORDER BY lo.loan_date DESC
                """;
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)) {
            ps.setObject(1, from);
            ps.setObject(2, to);
            ps.setString(3, libraryName);
            try (ResultSet rs = ps.executeQuery()) {
                List<LoanSearchResult> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(new LoanSearchResult(
                            rs.getString("title"),
                            rs.getObject("loan_date", LocalDate.class),
                            rs.getObject("return_date", LocalDate.class),
                            rs.getBoolean("is_returned"),
                            rs.getString("address"),
                            rs.getString("phone_number"),
                            rs.getString("email")));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la recherche des emprunts.", e);
        }
    }

    @Override
    public List<AuthorStat> findMostReadAuthors(LocalDate from, LocalDate to) {
        String sql = """
                SELECT a.id, a.family_name, a.first_name,
                       COUNT(*) AS nb_loans,
                       COUNT(*) * 100.0 / SUM(COUNT(*)) OVER () AS percentage
                FROM loan lo
                JOIN book_author ba ON lo.book_id = ba.book_id
                JOIN author a ON ba.author_id = a.id
                WHERE lo.loan_date BETWEEN ? AND ?
                GROUP BY a.id, a.family_name, a.first_name
                ORDER BY nb_loans DESC, a.family_name
                """;
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)) {
            ps.setObject(1, from);
            ps.setObject(2, to);
            try (ResultSet rs = ps.executeQuery()) {
                List<AuthorStat> stats = new ArrayList<>();
                while (rs.next()) {
                    Author author = new Author(rs.getInt("id"), rs.getString("family_name"), rs.getString("first_name"));
                    stats.add(new AuthorStat(author, rs.getInt("nb_loans"), rs.getDouble("percentage")));
                }
                return stats;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors du calcul des auteurs les plus lus.", e);
        }
    }
}
