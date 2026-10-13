package com.librarymanagement.dao;

import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.model.User;
import com.librarymanagement.util.PasswordHasher;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcUserDAO implements UserDAO {

    /** Toutes les colonnes sauf le mot de passe, qui n'est jamais relu par l'application. */
    private static final String SELECT_USERS = """
            SELECT id, family_name, first_name, birthdate, pseudo, phone_number, email, street_number,
                   street_name, postcode, locality, postbox, country, is_student, is_admin, preferred_category
            FROM user
            """;

    @Override
    public List<User> findAll() {
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(SELECT_USERS + " ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(read(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la récupération des utilisateurs.", e);
        }
    }

    @Override
    public Optional<User> findById(int id) {
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(SELECT_USERS + " WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(read(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la récupération de l'utilisateur.", e);
        }
    }

    @Override
    public void create(User user) {
        String sql = "INSERT INTO user (family_name, first_name, birthdate, pseudo, phone_number, email, "
                + "street_number, street_name, postcode, locality, postbox, country, is_student, is_admin, "
                + "preferred_category, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindCommonFields(ps, user);
            ps.setString(16, PasswordHasher.hash(user.getPassword()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) user.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la création de l'utilisateur : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(User user) {
        boolean changePassword = user.getPassword() != null && !user.getPassword().isEmpty();
        String sql = "UPDATE user SET family_name = ?, first_name = ?, birthdate = ?, pseudo = ?, phone_number = ?, "
                + "email = ?, street_number = ?, street_name = ?, postcode = ?, locality = ?, postbox = ?, country = ?, "
                + "is_student = ?, is_admin = ?, preferred_category = ?"
                + (changePassword ? ", password = ?" : "") + " WHERE id = ?";
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)) {
            bindCommonFields(ps, user);
            int index = 16;
            if (changePassword) ps.setString(index++, PasswordHasher.hash(user.getPassword()));
            ps.setInt(index, user.getId());
            if (ps.executeUpdate() == 0) {
                throw new DataAccessException("L'utilisateur n'existe plus en base.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement("DELETE FROM user WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DataAccessException("Impossible de supprimer cet utilisateur : il a des emprunts enregistrés.", e);
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la suppression de l'utilisateur.", e);
        }
    }

    @Override
    public boolean emailExists(String email, Integer excludedId) {
        return exists("email", email, excludedId);
    }

    @Override
    public boolean pseudoExists(String pseudo, Integer excludedId) {
        return pseudo != null && exists("pseudo", pseudo, excludedId);
    }

    /** {@code column} est toujours une constante de cette classe, jamais une saisie utilisateur. */
    private boolean exists(String column, String value, Integer excludedId) {
        String sql = "SELECT 1 FROM user WHERE " + column + " = ? AND id <> ?";
        try (PreparedStatement ps = DatabaseConnection.get().prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setInt(2, excludedId == null ? -1 : excludedId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erreur lors de la vérification de l'utilisateur.", e);
        }
    }

    private void bindCommonFields(PreparedStatement ps, User user) throws SQLException {
        ps.setString(1, user.getFamilyName());
        ps.setString(2, user.getFirstName());
        ps.setObject(3, user.getBirthdate());
        setNullable(ps, 4, user.getPseudo());
        setNullable(ps, 5, user.getPhoneNumber());
        ps.setString(6, user.getEmail());
        ps.setInt(7, user.getStreetNumber());
        ps.setString(8, user.getStreetName());
        ps.setString(9, user.getPostcode());
        ps.setString(10, user.getLocality());
        setNullable(ps, 11, user.getPostbox());
        ps.setString(12, user.getCountry());
        ps.setBoolean(13, user.isStudent());
        ps.setBoolean(14, user.isAdmin());
        setNullable(ps, 15, user.getPreferredCategory());
    }

    private static void setNullable(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null) ps.setNull(index, Types.VARCHAR);
        else ps.setString(index, value);
    }

    private User read(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFamilyName(rs.getString("family_name"));
        user.setFirstName(rs.getString("first_name"));
        user.setBirthdate(rs.getObject("birthdate", LocalDate.class));
        user.setPseudo(rs.getString("pseudo"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setEmail(rs.getString("email"));
        user.setStreetNumber(rs.getInt("street_number"));
        user.setStreetName(rs.getString("street_name"));
        user.setPostcode(rs.getString("postcode"));
        user.setLocality(rs.getString("locality"));
        user.setPostbox(rs.getString("postbox"));
        user.setCountry(rs.getString("country"));
        user.setStudent(rs.getBoolean("is_student"));
        user.setAdmin(rs.getBoolean("is_admin"));
        user.setPreferredCategory(rs.getString("preferred_category"));
        return user;
    }
}
