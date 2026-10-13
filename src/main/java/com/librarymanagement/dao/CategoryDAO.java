package com.librarymanagement.dao;

import com.librarymanagement.model.Category;
import com.librarymanagement.model.CategoryStat;

import java.time.LocalDate;
import java.util.List;

public interface CategoryDAO {

    List<Category> findAll();

    /** Catégories préférées des membres nés entre {@code bornFrom} et {@code bornTo} (inclus). */
    List<CategoryStat> findPreferredCategoriesByBirthdate(LocalDate bornFrom, LocalDate bornTo);
}
