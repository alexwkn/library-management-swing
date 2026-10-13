package com.librarymanagement.business;

import com.librarymanagement.dao.CategoryDAO;
import com.librarymanagement.dao.JdbcCategoryDAO;
import com.librarymanagement.model.Category;
import com.librarymanagement.model.CategoryStat;

import java.time.LocalDate;
import java.util.List;

public class CategoryManager {

    private final CategoryDAO categoryDAO;

    public CategoryManager() {
        this(new JdbcCategoryDAO());
    }

    public CategoryManager(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }

    /**
     * Catégories préférées des membres dont l'âge est compris entre {@code minAge} et {@code maxAge} ans (inclus).
     * {@code maxAge} peut être {@code null} pour « et plus ».
     */
    public List<CategoryStat> getPreferredCategoriesByAge(int minAge, Integer maxAge) {
        LocalDate today = LocalDate.now();
        LocalDate bornTo = today.minusYears(minAge);
        // Une personne a encore « maxAge » ans jusqu'à la veille de son (maxAge + 1)e anniversaire
        LocalDate bornFrom = maxAge == null ? LocalDate.of(1900, 1, 1) : today.minusYears(maxAge + 1L).plusDays(1);
        return categoryDAO.findPreferredCategoriesByBirthdate(bornFrom, bornTo);
    }
}
