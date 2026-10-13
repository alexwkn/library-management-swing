package com.librarymanagement.dao;

import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.model.Category;
import com.librarymanagement.model.CategoryStat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcCategoryDAO implements CategoryDAO {

    @Override
    public List<Category> findAll() {
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement("SELECT label FROM category ORDER BY label");
             ResultSet rs = ps.executeQuery()) {
            List<Category> categories = new ArrayList<>();
            while (rs.next()) {
                categories.add(new Category(rs.getString("label")));
            }
            return categories;
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la récupération des catégories.", e);
        }
    }

    @Override
    public List<CategoryStat> findPreferredCategoriesByBirthdate(LocalDate bornFrom, LocalDate bornTo) {
        String sql = """
                SELECT c.label AS category,
                       COUNT(*) AS nb_users,
                       COUNT(*) * 100.0 / SUM(COUNT(*)) OVER () AS percentage
                FROM user u
                JOIN category c ON u.preferred_category = c.label
                WHERE u.birthdate BETWEEN ? AND ?
                GROUP BY c.label
                ORDER BY nb_users DESC, c.label
                """;
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)) {
            ps.setObject(1, bornFrom);
            ps.setObject(2, bornTo);
            try (ResultSet rs = ps.executeQuery()) {
                List<CategoryStat> stats = new ArrayList<>();
                while (rs.next()) {
                    stats.add(new CategoryStat(rs.getString("category"), rs.getInt("nb_users"), rs.getDouble("percentage")));
                }
                return stats;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors du calcul des catégories préférées.", e);
        }
    }
}
