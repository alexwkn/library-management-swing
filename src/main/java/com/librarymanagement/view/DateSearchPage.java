package com.librarymanagement.view;

import com.librarymanagement.controller.BookController;
import com.librarymanagement.controller.CategoryController;
import com.librarymanagement.controller.LibraryController;
import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.BookSearchResult;
import com.librarymanagement.model.Category;
import com.librarymanagement.model.Library;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.List;

/** Recherche des livres publiés à une date donnée dans une bibliothèque. */
public class DateSearchPage extends JPanel {

    private static final String ALL_CATEGORIES = "(toutes)";

    private final MainFrame mainFrame;
    private final BookController bookController = new BookController();
    private final JSpinner publicationDate = SwingUtils.dateSpinner(LocalDate.of(2000, 1, 1));
    private final JComboBox<Library> libraryChoice = new JComboBox<>();
    private final JComboBox<String> categoryChoice = new JComboBox<>();
    private final DefaultTableModel tableModel = SwingUtils.readOnlyModel(
            "Titre", "Catégorie", "Disponible", "Langue", "Adresse", "Email", "Téléphone");

    public DateSearchPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        new LibraryController().getAllLibraries().forEach(libraryChoice::addItem);
        categoryChoice.addItem(ALL_CATEGORIES);
        new CategoryController().getAllCategories().stream().map(Category::getLabel).forEach(categoryChoice::addItem);

        JPanel formPanel = new JPanel();
        FormLayout form = new FormLayout(formPanel);
        form.title("Livres par date de publication");
        form.field("Date de publication", publicationDate);
        form.field("Bibliothèque", libraryChoice);
        form.field("Catégorie", categoryChoice);
        JButton search = new JButton("Rechercher");
        search.addActionListener(e -> search());
        form.button(search);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(SwingUtils.table(tableModel)), BorderLayout.CENTER);
    }

    private void search() {
        Library library = (Library) libraryChoice.getSelectedItem();
        String category = (String) categoryChoice.getSelectedItem();
        try {
            List<BookSearchResult> results = bookController.getBooksByPublicationDateAndLibrary(
                    SwingUtils.getDate(publicationDate),
                    library == null ? null : library.getName(),
                    ALL_CATEGORIES.equals(category) ? null : category);
            SearchResults.showBooks(tableModel, results);
            if (results.isEmpty()) mainFrame.displayMessage("Aucun livre trouvé pour ces critères.", "Résultat vide");
        } catch (ValidationException | DataAccessException e) {
            mainFrame.displayException(e);
        }
    }
}
