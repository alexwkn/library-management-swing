package com.librarymanagement.model;

import java.util.Objects;

public class Author {
    private Integer id;
    private String familyName;
    private String firstName;

    public Author() {
    }

    public Author(Integer id, String familyName, String firstName) {
        this.id = id;
        this.familyName = familyName;
        this.firstName = firstName;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /** Nom affiché : « Nom, Prénom » (ou seulement le nom si pas de prénom, ex. Molière). */
    public String getDisplayName() {
        return firstName == null || firstName.isBlank() ? familyName : familyName + ", " + firstName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author other)) return false;
        return Objects.equals(familyName, other.familyName) && Objects.equals(firstName, other.firstName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(familyName, firstName);
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
