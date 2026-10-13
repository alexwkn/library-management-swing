package com.librarymanagement.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormValidatorTest {

    @Test
    void validatesEmails() {
        assertTrue(FormValidator.isValidEmail("jean.dupont@example.be"));
        assertFalse(FormValidator.isValidEmail("jean.dupont@example"));
        assertFalse(FormValidator.isValidEmail("jean dupont@example.be"));
        assertFalse(FormValidator.isValidEmail(null));
    }

    @Test
    void validatesNumbers() {
        assertTrue(FormValidator.isNumber("0470123456"));
        assertFalse(FormValidator.isNumber("+32470123456"));
        assertFalse(FormValidator.isNumber(""));
    }

    @Test
    void validatesBirthdates() {
        assertTrue(FormValidator.isValidBirthdate(LocalDate.of(1985, 5, 15)));
        assertFalse(FormValidator.isValidBirthdate(LocalDate.of(1899, 12, 31)));
        assertFalse(FormValidator.isValidBirthdate(LocalDate.now().plusDays(1)));
    }

    @Test
    void trimsToNull() {
        assertEquals("Namur", FormValidator.trimToNull("  Namur "));
        assertNull(FormValidator.trimToNull("   "));
        assertNull(FormValidator.trimToNull(null));
    }
}
