package com.librarymanagement.business;

import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserManagerTest {

    private InMemoryUserDAO dao;
    private UserManager manager;

    @BeforeEach
    void setUp() {
        dao = new InMemoryUserDAO();
        manager = new UserManager(dao);
    }

    static User validUser() {
        User user = new User();
        user.setFamilyName("Dupont");
        user.setFirstName("Jean");
        user.setEmail("jean.dupont@example.be");
        user.setPassword("motdepasse123");
        user.setBirthdate(LocalDate.of(1990, 5, 15));
        user.setStreetName("Rue de Fer");
        user.setStreetNumber(12);
        user.setPostcode("5000");
        user.setLocality("Namur");
        user.setCountry("Belgique");
        return user;
    }

    @Test
    void createsValidUser() {
        assertDoesNotThrow(() -> manager.createUser(validUser()));
        assertEquals(1, dao.users.size());
    }

    @Test
    void rejectsInvalidEmail() {
        User user = validUser();
        user.setEmail("pas-un-email");
        assertThrows(ValidationException.class, () -> manager.createUser(user));
    }

    @Test
    void rejectsShortPassword() {
        User user = validUser();
        user.setPassword("court");
        assertThrows(ValidationException.class, () -> manager.createUser(user));
    }

    @Test
    void requiresPasswordOnCreation() {
        User user = validUser();
        user.setPassword("");
        assertThrows(ValidationException.class, () -> manager.createUser(user));
    }

    @Test
    void keepsPasswordOptionalOnUpdate() throws ValidationException {
        manager.createUser(validUser());
        User update = validUser();
        update.setId(1);
        update.setPassword("");
        assertDoesNotThrow(() -> manager.updateUser(update));
    }

    @Test
    void rejectsFutureBirthdate() {
        User user = validUser();
        user.setBirthdate(LocalDate.now().plusDays(1));
        assertThrows(ValidationException.class, () -> manager.createUser(user));
    }

    @Test
    void rejectsNonNumericPhoneNumber() {
        User user = validUser();
        user.setPhoneNumber("04 70 12 34 56");
        assertThrows(ValidationException.class, () -> manager.createUser(user));
    }

    @Test
    void rejectsDuplicateEmail() throws ValidationException {
        manager.createUser(validUser());
        assertThrows(ValidationException.class, () -> manager.createUser(validUser()));
    }

    @Test
    void allowsKeepingOwnEmailOnUpdate() throws ValidationException {
        manager.createUser(validUser());
        User update = validUser();
        update.setId(1);
        update.setLocality("Jambes");
        assertDoesNotThrow(() -> manager.updateUser(update));
    }

    @Test
    void rejectsDuplicatePseudo() throws ValidationException {
        User first = validUser();
        first.setPseudo("jdupont");
        manager.createUser(first);

        User second = validUser();
        second.setEmail("autre@example.be");
        second.setPseudo("jdupont");
        assertThrows(ValidationException.class, () -> manager.createUser(second));
    }
}
