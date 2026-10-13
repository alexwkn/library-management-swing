package com.librarymanagement.model;

import java.util.Objects;

/**
 * Catégorie (genre littéraire). Le libellé sert d'identifiant en base.
 */
public class Category {
    private String label;

    public Category(String label) {
        this.label = label;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Category other && Objects.equals(label, other.label);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(label);
    }

    @Override
    public String toString() {
        return label;
    }
}
