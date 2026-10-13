package com.librarymanagement.view;

import com.librarymanagement.controller.LibraryController;
import com.librarymanagement.controller.LoanController;
import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.Library;
import com.librarymanagement.model.LoanSearchResult;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.List;

/** Recherche des emprunts effectués sur une période dans une bibliothèque. */
public class LoanSearchPage extends JPanel {

    private final MainFrame mainFrame;
    private final LoanController loanController = new LoanController();
    private final JSpinner from = SwingUtils.dateSpinner(LocalDate.now().minusYears(1));
    private final JSpinner to = SwingUtils.dateSpinner(LocalDate.now());
    private final JComboBox<Library> libraryChoice = new JComboBox<>();
    private final DefaultTableModel tableModel = SwingUtils.readOnlyModel(
            "Titre du livre", "Date d'emprunt", "Date de retour", "Rendu", "Adresse", "Téléphone", "Email");

    public LoanSearchPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        new LibraryController().getAllLibraries().forEach(libraryChoice::addItem);

        JPanel formPanel = new JPanel();
        FormLayout form = new FormLayout(formPanel);
        form.title("Emprunts sur une période");
        form.field("Emprunts à partir du", from);
        form.field("Jusqu'au", to);
        form.field("Bibliothèque", libraryChoice);
        JButton search = new JButton("Rechercher");
        search.addActionListener(e -> search());
        form.button(search);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(SwingUtils.table(tableModel)), BorderLayout.CENTER);
    }

    private void search() {
        Library library = (Library) libraryChoice.getSelectedItem();
        try {
            List<LoanSearchResult> results = loanController.getLoansByPeriodAndLibrary(
                    SwingUtils.getDate(from), SwingUtils.getDate(to), library == null ? null : library.getName());
            tableModel.setRowCount(0);
            for (LoanSearchResult r : results) {
                tableModel.addRow(new Object[]{
                        r.bookTitle(), r.loanDate(), r.returnDate() == null ? "—" : r.returnDate(),
                        SwingUtils.yesNo(r.returned()), r.libraryAddress(), r.libraryPhone(), r.libraryEmail()
                });
            }
            if (results.isEmpty()) mainFrame.displayMessage("Aucun emprunt trouvé sur cette période.", "Résultat vide");
        } catch (ValidationException | DataAccessException e) {
            mainFrame.displayException(e);
        }
    }
}
