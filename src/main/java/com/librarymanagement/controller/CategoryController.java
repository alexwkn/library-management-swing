package com.librarymanagement.controller;

import com.librarymanagement.business.CategoryManager;
import com.librarymanagement.model.Category;
import com.librarymanagement.model.CategoryStat;

import java.util.List;

/** Point d'entrée des vues pour tout ce qui concerne les catégories. */
public class CategoryController {

    private final CategoryManager categoryManager = new CategoryManager();

    public List<Category> getAllCategories() {
        return categoryManager.getAllCategories();
    }

    public List<CategoryStat> getPreferredCategoriesByAge(int minAge, Integer maxAge) {
        return categoryManager.getPreferredCategoriesByAge(minAge, maxAge);
    }
}
