package com.librarymanagement.dao;

import com.librarymanagement.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {

    List<User> findAll();

    Optional<User> findById(int id);

    void create(User user);

    /** Met à jour l'utilisateur. Le mot de passe n'est modifié que s'il est renseigné. */
    void update(User user);

    void delete(int id);

    /** Vrai si un autre utilisateur (id différent de {@code excludedId}) utilise déjà cet email. */
    boolean emailExists(String email, Integer excludedId);

    /** Vrai si un autre utilisateur (id différent de {@code excludedId}) utilise déjà ce pseudo. */
    boolean pseudoExists(String pseudo, Integer excludedId);
}
