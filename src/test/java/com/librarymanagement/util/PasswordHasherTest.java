package com.librarymanagement.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordHasherTest {

    @Test
    void verifiesCorrectPassword() {
        String hash = PasswordHasher.hash("motdepasse123");
        assertTrue(PasswordHasher.verify("motdepasse123", hash));
    }

    @Test
    void rejectsWrongPassword() {
        String hash = PasswordHasher.hash("motdepasse123");
        assertFalse(PasswordHasher.verify("mauvais", hash));
    }

    @Test
    void usesRandomSalt() {
        assertNotEquals(PasswordHasher.hash("identique"), PasswordHasher.hash("identique"));
    }

    @Test
    void neverStoresPasswordInClear() {
        assertFalse(PasswordHasher.hash("motdepasse123").contains("motdepasse123"));
    }
}
