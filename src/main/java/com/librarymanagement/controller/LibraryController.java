package com.librarymanagement.controller;

import com.librarymanagement.business.LibraryManager;
import com.librarymanagement.model.Library;

import java.util.List;
import java.util.Optional;

/** Point d'entrée des vues pour tout ce qui concerne les bibliothèques. */
public class LibraryController {

    private final LibraryManager libraryManager = new LibraryManager();

    public List<Library> getAllLibraries() {
        return libraryManager.getAllLibraries();
    }

    public Optional<Library> getLibraryByName(String name) {
        return libraryManager.getLibraryByName(name);
    }
}
