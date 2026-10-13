package com.librarymanagement.view;

import com.librarymanagement.controller.AuthorController;
import com.librarymanagement.controller.LibraryController;
import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.BookSearchResult;
import com.librarymanagement.model.Library;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

/** Recherche des livres d'un auteur dans une bibliothèque. */
public class AuthorSearchPage extends JPanel {

    private final MainFrame mainFrame;
    private final AuthorController authorController = new AuthorController();
    private final JComboBox<Library> libraryChoice = new JComboBox<>();
    private final JTextField familyName = new JTextField(20);
    private final JTextField firstName = new JTextField(20);
    private final DefaultTableModel tableModel = SwingUtils.readOnlyModel(
            "Titre", "Catégorie", "Disponible", "Langue", "Adresse", "Email", "Téléphone");

    public AuthorSearchPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        new LibraryController().getAllLibraries().forEach(libraryChoice::addItem);

        JPanel formPanel = new JPanel();
        FormLayout form = new FormLayout(formPanel);
        form.title("Livres d'un auteur dans une bibliothèque");
        form.field("Bibliothèque", libraryChoice);
        form.field("Nom de l'auteur", familyName);
        form.field("Prénom de l'auteur", firstName);
        JButton search = new JButton("Rechercher");
        search.addActionListener(e -> search());
        form.button(search);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(SwingUtils.table(tableModel)), BorderLayout.CENTER);
    }

    private void search() {
        Library library = (Library) libraryChoice.getSelectedItem();
        try {
            List<BookSearchResult> results = authorController.getBooksByAuthorInLibrary(
                    firstName.getText(), familyName.getText(), library == null ? null : library.getName());
            SearchResults.showBooks(tableModel, results);
            if (results.isEmpty()) mainFrame.displayMessage("Aucun livre trouvé.", "Résultat vide");
        } catch (ValidationException | DataAccessException e) {
            mainFrame.displayException(e);
        }
    }
}
