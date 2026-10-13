package com.librarymanagement.business;

import com.librarymanagement.dao.CategoryDAO;
import com.librarymanagement.model.Category;
import com.librarymanagement.model.CategoryStat;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryManagerTest {

    private LocalDate requestedFrom;
    private LocalDate requestedTo;

    private final CategoryManager manager = new CategoryManager(new CategoryDAO() {
        @Override public List<Category> findAll() { return List.of(); }
        @Override public List<CategoryStat> findPreferredCategoriesByBirthdate(LocalDate from, LocalDate to) {
            requestedFrom = from;
            requestedTo = to;
            return List.of();
        }
    });

    @Test
    void convertsAgeRangeToBirthdateRange() {
        LocalDate today = LocalDate.now();
        manager.getPreferredCategoriesByAge(18, 25);

        // Le plus jeune vient d'avoir 18 ans, le plus âgé aura 26 ans demain
        assertEquals(today.minusYears(18), requestedTo);
        assertEquals(today.minusYears(26).plusDays(1), requestedFrom);
    }

    @Test
    void openEndedRangeStartsIn1900() {
        manager.getPreferredCategoriesByAge(65, null);
        assertEquals(LocalDate.of(1900, 1, 1), requestedFrom);
    }
}
