package com.librarymanagement.business;

import com.librarymanagement.dao.JdbcLibraryDAO;
import com.librarymanagement.dao.LibraryDAO;
import com.librarymanagement.model.Library;

import java.util.List;
import java.util.Optional;

public class LibraryManager {

    private final LibraryDAO libraryDAO;

    public LibraryManager() {
        this(new JdbcLibraryDAO());
    }

    public LibraryManager(LibraryDAO libraryDAO) {
        this.libraryDAO = libraryDAO;
    }

    public List<Library> getAllLibraries() {
        return libraryDAO.findAll();
    }

    public Optional<Library> getLibraryByName(String name) {
        return libraryDAO.findByName(name);
    }
}
