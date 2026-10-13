package com.librarymanagement.model;

import java.time.LocalDate;

/**
 * Membre du réseau de bibliothèques.
 * Le champ {@code password} contient le mot de passe en clair saisi dans le formulaire ;
 * il n'est jamais relu depuis la base (seul le hash y est stocké).
 */
public class User {
    private Integer id;
    private String familyName;
    private String firstName;
    private LocalDate birthdate;
    private String pseudo;
    private String phoneNumber;
    private String email;
    private int streetNumber;
    private String streetName;
    private String postcode;
    private String locality;
    private String postbox;
    private String country;
    private boolean student;
    private boolean admin;
    private String password;
    private String preferredCategory;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFamilyName() { return familyName; }
    public void setFamilyName(String familyName) { this.familyName = familyName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getStreetNumber() { return streetNumber; }
    public void setStreetNumber(int streetNumber) { this.streetNumber = streetNumber; }

    public String getStreetName() { return streetName; }
    public void setStreetName(String streetName) { this.streetName = streetName; }

    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }

    public String getLocality() { return locality; }
    public void setLocality(String locality) { this.locality = locality; }

    public String getPostbox() { return postbox; }
    public void setPostbox(String postbox) { this.postbox = postbox; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public boolean isStudent() { return student; }
    public void setStudent(boolean student) { this.student = student; }

    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPreferredCategory() { return preferredCategory; }
    public void setPreferredCategory(String preferredCategory) { this.preferredCategory = preferredCategory; }
}
