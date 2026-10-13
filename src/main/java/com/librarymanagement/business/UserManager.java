package com.librarymanagement.business;

import com.librarymanagement.dao.JdbcUserDAO;
import com.librarymanagement.dao.UserDAO;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.User;
import com.librarymanagement.util.FormValidator;

import java.util.List;
import java.util.Optional;

public class UserManager {

    public static final int MIN_PASSWORD_LENGTH = 8;

    private final UserDAO userDAO;

    public UserManager() {
        this(new JdbcUserDAO());
    }

    public UserManager(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public Optional<User> getUser(int id) {
        return userDAO.findById(id);
    }

    public void createUser(User user) throws ValidationException {
        validate(user, true);
        checkUniqueness(user);
        userDAO.create(user);
    }

    /** Si le mot de passe est laissé vide, l'ancien est conservé. */
    public void updateUser(User user) throws ValidationException {
        if (user.getId() == null) {
            throw new ValidationException("L'identifiant de l'utilisateur est requis pour la mise à jour.");
        }
        validate(user, false);
        checkUniqueness(user);
        userDAO.update(user);
    }

    public void deleteUser(int id) {
        userDAO.delete(id);
    }

    private void checkUniqueness(User user) throws ValidationException {
        if (userDAO.emailExists(user.getEmail(), user.getId())) {
            throw new ValidationException("Un utilisateur avec cet email existe déjà.");
        }
        if (userDAO.pseudoExists(user.getPseudo(), user.getId())) {
            throw new ValidationException("Un utilisateur avec ce nom d'utilisateur existe déjà.");
        }
    }

    void validate(User user, boolean passwordRequired) throws ValidationException {
        require(FormValidator.hasLength(user.getFamilyName(), 1, 100), "Le nom est obligatoire (100 caractères maximum).");
        require(FormValidator.hasLength(user.getFirstName(), 1, 100), "Le prénom est obligatoire (100 caractères maximum).");

        require(FormValidator.hasLength(user.getEmail(), 1, 100), "L'email est obligatoire (100 caractères maximum).");
        require(!FormValidator.containsSpace(user.getEmail()), "L'email ne doit pas contenir d'espaces.");
        require(FormValidator.isValidEmail(user.getEmail()), "Le format de l'email est invalide (ex. : nom@domaine.be).");

        String password = user.getPassword();
        boolean passwordProvided = password != null && !password.isEmpty();
        require(passwordProvided || !passwordRequired, "Le mot de passe est obligatoire.");
        if (passwordProvided) {
            require(!FormValidator.containsSpace(password), "Le mot de passe ne doit pas contenir d'espaces.");
            require(FormValidator.hasLength(password, MIN_PASSWORD_LENGTH, 100),
                    "Le mot de passe doit contenir entre " + MIN_PASSWORD_LENGTH + " et 100 caractères.");
        }

        require(FormValidator.isValidBirthdate(user.getBirthdate()), "La date de naissance est invalide.");

        require(user.getStreetNumber() > 0, "Le numéro de rue doit être un nombre positif.");
        require(FormValidator.hasLength(user.getStreetName(), 1, 255), "Le nom de rue est obligatoire.");
        require(FormValidator.isNumber(user.getPostcode()) && FormValidator.hasLength(user.getPostcode(), 4, 6),
                "Le code postal doit contenir entre 4 et 6 chiffres.");
        require(FormValidator.hasLength(user.getLocality(), 1, 100), "La localité est obligatoire.");
        require(FormValidator.hasLength(user.getCountry(), 1, 100), "Le pays est obligatoire.");

        if (user.getPseudo() != null) {
            require(!FormValidator.containsSpace(user.getPseudo()), "Le nom d'utilisateur ne doit pas contenir d'espaces.");
            require(FormValidator.hasLength(user.getPseudo(), 3, 100), "Le nom d'utilisateur doit contenir entre 3 et 100 caractères.");
        }
        if (user.getPhoneNumber() != null) {
            require(FormValidator.isNumber(user.getPhoneNumber()) && FormValidator.hasLength(user.getPhoneNumber(), 8, 20),
                    "Le numéro de téléphone est invalide (8 à 20 chiffres, sans espaces).");
        }
        if (user.getPostbox() != null) {
            require(FormValidator.hasLength(user.getPostbox(), 1, 10), "La boîte postale ne peut pas dépasser 10 caractères.");
        }
    }

    private static void require(boolean condition, String message) throws ValidationException {
        if (!condition) throw new ValidationException(message);
    }
}
