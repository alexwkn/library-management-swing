package com.librarymanagement.dao;

import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.model.Library;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcLibraryDAO implements LibraryDAO {

    @Override
    public List<Library> findAll() {
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement("SELECT * FROM library ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            List<Library> libraries = new ArrayList<>();
            while (rs.next()) {
                libraries.add(read(rs));
            }
            return libraries;
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la récupération des bibliothèques.", e);
        }
    }

    @Override
    public Optional<Library> findByName(String name) {
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement("SELECT * FROM library WHERE name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(read(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la récupération de la bibliothèque.", e);
        }
    }

    private Library read(ResultSet rs) throws SQLException {
        return new Library(rs.getInt("id"), rs.getString("name"), rs.getString("address"),
                rs.getString("email"), rs.getString("phone_number"));
    }
}
