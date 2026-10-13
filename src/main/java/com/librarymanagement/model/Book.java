package com.librarymanagement.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Book {
    private Integer id;
    private Long isbnCode;
    private String title;
    private LocalDate publicationDate;
    private String language;
    private int nbPages;
    private boolean available;
    private double price;
    private String summary;
    private String commentary;
    private Library library;
    private List<Author> authors = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Long getIsbnCode() { return isbnCode; }
    public void setIsbnCode(Long isbnCode) { this.isbnCode = isbnCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDate publicationDate) { this.publicationDate = publicationDate; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public int getNbPages() { return nbPages; }
    public void setNbPages(int nbPages) { this.nbPages = nbPages; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getCommentary() { return commentary; }
    public void setCommentary(String commentary) { this.commentary = commentary; }

    public Library getLibrary() { return library; }
    public void setLibrary(Library library) { this.library = library; }

    public List<Author> getAuthors() { return authors; }
    public void setAuthors(List<Author> authors) { this.authors = authors == null ? new ArrayList<>() : authors; }

    public List<Category> getCategories() { return categories; }
    public void setCategories(List<Category> categories) { this.categories = categories == null ? new ArrayList<>() : categories; }
}
