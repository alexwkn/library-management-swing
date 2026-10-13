package com.librarymanagement.business;

import com.librarymanagement.dao.UserDAO;
import com.librarymanagement.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Implémentation en mémoire du DAO, pour tester la couche métier sans base de données. */
class InMemoryUserDAO implements UserDAO {

    final List<User> users = new ArrayList<>();
    private int nextId = 1;

    @Override
    public List<User> findAll() {
        return users;
    }

    @Override
    public Optional<User> findById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst();
    }

    @Override
    public void create(User user) {
        user.setId(nextId++);
        users.add(user);
    }

    @Override
    public void update(User user) {
        users.replaceAll(u -> u.getId().equals(user.getId()) ? user : u);
    }

    @Override
    public void delete(int id) {
        users.removeIf(u -> u.getId() == id);
    }

    @Override
    public boolean emailExists(String email, Integer excludedId) {
        return users.stream().anyMatch(u -> u.getEmail().equals(email) && !Objects.equals(u.getId(), excludedId));
    }

    @Override
    public boolean pseudoExists(String pseudo, Integer excludedId) {
        return pseudo != null && users.stream()
                .anyMatch(u -> pseudo.equals(u.getPseudo()) && !Objects.equals(u.getId(), excludedId));
    }
}
