package com.librarymanagement.model;

import java.util.Objects;

public class Library {
    private final Integer id;
    private final String name;
    private final String address;
    private final String email;
    private final String phoneNumber;

    public Library(Integer id, String name, String address, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Library other && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /** Utilisé par les listes déroulantes. */
    @Override
    public String toString() {
        return name;
    }
}
