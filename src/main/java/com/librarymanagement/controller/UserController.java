package com.librarymanagement.controller;

import com.librarymanagement.business.UserManager;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.User;

import java.util.List;
import java.util.Optional;

/** Point d'entrée des vues pour tout ce qui concerne les utilisateurs. */
public class UserController {

    private final UserManager userManager = new UserManager();

    public List<User> getAllUsers() {
        return userManager.getAllUsers();
    }

    public Optional<User> getUser(int id) {
        return userManager.getUser(id);
    }

    public void createUser(User user) throws ValidationException {
        userManager.createUser(user);
    }

    public void updateUser(User user) throws ValidationException {
        userManager.updateUser(user);
    }

    public void deleteUser(int id) {
        userManager.deleteUser(id);
    }
}
