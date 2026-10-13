package com.librarymanagement.view;

import com.librarymanagement.controller.BookController;
import com.librarymanagement.controller.CategoryController;
import com.librarymanagement.controller.LibraryController;
import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.Author;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Category;
import com.librarymanagement.model.Library;
import com.librarymanagement.util.FormValidator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Formulaire de création ({@code existingBook == null}) ou de modification d'un livre.
 */
public class BookFormPanel extends JPanel {

    private static final int COLUMNS = 25;

    private final MainFrame mainFrame;
    private final BookController bookController = new BookController();
    private final Book existingBook;

    private final JTextField isbn = new JTextField(COLUMNS);
    private final JTextField title = new JTextField(COLUMNS);
    private final JSpinner publicationDate = SwingUtils.dateSpinner(LocalDate.now());
    private final JTextField language = new JTextField("Français", COLUMNS);
    private final JTextField nbPages = new JTextField(COLUMNS);
    private final JTextField price = new JTextField(COLUMNS);
    private final JCheckBox available = new JCheckBox("", true);
    private final JTextArea summary = new JTextArea(3, COLUMNS);
    private final JTextArea commentary = new JTextArea(3, COLUMNS);
    private final JComboBox<Library> library = new JComboBox<>();

    private final JComboBox<Category> categoryChoice = new JComboBox<>();
    private final DefaultListModel<Category> selectedCategories = new DefaultListModel<>();
    private final JList<Category> categoryList = new JList<>(selectedCategories);

    private final JTextField authorFamilyName = new JTextField(10);
    private final JTextField authorFirstName = new JTextField(10);
    private final DefaultListModel<Author> selectedAuthors = new DefaultListModel<>();
    private final JList<Author> authorList = new JList<>(selectedAuthors);

    public BookFormPanel(MainFrame mainFrame, Book existingBook) {
        this.mainFrame = mainFrame;
        this.existingBook = existingBook;
        boolean isUpdate = existingBook != null;

        try {
            new LibraryController().getAllLibraries().forEach(library::addItem);
            new CategoryController().getAllCategories().forEach(categoryChoice::addItem);
        } catch (DataAccessException e) {
            mainFrame.displayException(e);
        }

        FormLayout form = new FormLayout(this);
        form.title(isUpdate ? "Modification du livre n°" + existingBook.getId() : "Nouveau livre");
        form.field("ISBN * (13 chiffres)", isbn);
        form.field("Titre *", title);
        form.field("Date de publication *", publicationDate);
        form.field("Langue *", language);
        form.field("Nombre de pages *", nbPages);
        form.field("Prix (€) *", price);
        form.field("Disponible", available);
        form.field("Résumé", scrollable(summary));
        form.field("Commentaire", scrollable(commentary));
        form.field("Bibliothèque *", library);

        JButton addCategory = new JButton("Ajouter");
        JButton removeCategory = new JButton("Retirer");
        addCategory.addActionListener(e -> {
            Category category = (Category) categoryChoice.getSelectedItem();
            if (category != null && !selectedCategories.contains(category)) selectedCategories.addElement(category);
        });
        removeCategory.addActionListener(e -> categoryList.getSelectedValuesList().forEach(selectedCategories::removeElement));
        form.field("Catégories *", row(categoryChoice, addCategory, removeCategory));
        form.field("", scrollable(categoryList));

        JButton addAuthor = new JButton("Ajouter");
        JButton removeAuthor = new JButton("Retirer");
        addAuthor.addActionListener(e -> addAuthor());
        removeAuthor.addActionListener(e -> authorList.getSelectedValuesList().forEach(selectedAuthors::removeElement));
        form.field("Auteurs *", row(new JLabel("Nom :"), authorFamilyName, new JLabel("Prénom :"), authorFirstName,
                addAuthor, removeAuthor));
        form.field("", scrollable(authorList));

        JButton cancel = new JButton("Annuler");
        cancel.addActionListener(e -> mainFrame.showBookListing());
        JButton submit = new JButton(isUpdate ? "Enregistrer les modifications" : "Créer le livre");
        submit.addActionListener(e -> submit());
        form.button(row(cancel, submit));

        if (isUpdate) fill(existingBook);
    }

    private static JScrollPane scrollable(java.awt.Component component) {
        if (component instanceof JTextArea area) {
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
        }
        JScrollPane scroll = new JScrollPane(component);
        scroll.setPreferredSize(new Dimension(320, 60));
        return scroll;
    }

    private static JPanel row(java.awt.Component... components) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        for (java.awt.Component c : components) panel.add(c);
        return panel;
    }

    private void addAuthor() {
        String family = authorFamilyName.getText().trim();
        String first = authorFirstName.getText().trim();
        if (family.isEmpty()) {
            mainFrame.displayError("Le nom de l'auteur est obligatoire (le prénom est facultatif, ex. : Molière).");
            return;
        }
        Author author = new Author(null, family, first);
        if (!selectedAuthors.contains(author)) selectedAuthors.addElement(author);
        authorFamilyName.setText("");
        authorFirstName.setText("");
    }

    private void fill(Book book) {
        isbn.setText(String.valueOf(book.getIsbnCode()));
        title.setText(book.getTitle());
        SwingUtils.setDate(publicationDate, book.getPublicationDate());
        language.setText(book.getLanguage());
        nbPages.setText(String.valueOf(book.getNbPages()));
        price.setText(String.valueOf(book.getPrice()));
        available.setSelected(book.isAvailable());
        summary.setText(book.getSummary());
        commentary.setText(book.getCommentary());
        library.setSelectedItem(book.getLibrary());
        book.getCategories().forEach(selectedCategories::addElement);
        book.getAuthors().forEach(selectedAuthors::addElement);
    }

    private void submit() {
        try {
            Book book = readForm();
            if (existingBook == null) {
                bookController.createBook(book);
                mainFrame.displayMessage("Livre ajouté avec succès.", "Succès");
            } else {
                bookController.updateBook(book);
                mainFrame.displayMessage("Livre mis à jour avec succès.", "Succès");
            }
            mainFrame.showBookListing();
        } catch (ValidationException | DataAccessException e) {
            mainFrame.displayException(e);
        }
    }

    private Book readForm() throws ValidationException {
        Book book = new Book();
        if (existingBook != null) book.setId(existingBook.getId());

        String isbnText = isbn.getText().trim();
        if (!isbnText.matches("\\d{13}")) throw new ValidationException("Le code ISBN doit contenir exactement 13 chiffres.");
        book.setIsbnCode(Long.parseLong(isbnText));
        book.setTitle(FormValidator.trimToNull(title.getText()));
        book.setPublicationDate(SwingUtils.getDate(publicationDate));
        book.setLanguage(FormValidator.trimToNull(language.getText()));
        book.setNbPages(parseInt(nbPages.getText(), "Le nombre de pages doit être un nombre entier."));
        book.setPrice(parsePrice(price.getText()));
        book.setAvailable(available.isSelected());
        book.setSummary(FormValidator.trimToNull(summary.getText()));
        book.setCommentary(FormValidator.trimToNull(commentary.getText()));
        book.setLibrary((Library) library.getSelectedItem());
        book.setCategories(toList(selectedCategories));
        book.setAuthors(toList(selectedAuthors));
        return book;
    }

    private static int parseInt(String text, String error) throws ValidationException {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException(error);
        }
    }

    private static double parsePrice(String text) throws ValidationException {
        try {
            return Double.parseDouble(text.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new ValidationException("Le prix doit être un nombre (ex. : 12.50).");
        }
    }

    private static <T> List<T> toList(DefaultListModel<T> model) {
        return new ArrayList<>(Collections.list(model.elements()));
    }
}
