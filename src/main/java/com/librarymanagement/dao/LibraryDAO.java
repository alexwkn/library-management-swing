package com.librarymanagement.dao;

import com.librarymanagement.model.Library;

import java.util.List;
import java.util.Optional;

public interface LibraryDAO {

    List<Library> findAll();

    Optional<Library> findByName(String name);
}
