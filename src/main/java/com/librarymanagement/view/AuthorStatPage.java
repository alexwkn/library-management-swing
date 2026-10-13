package com.librarymanagement.view;

import com.librarymanagement.controller.LoanController;
import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.exception.ValidationException;
import com.librarymanagement.model.AuthorStat;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.util.List;

/** Tâche métier : auteurs les plus lus (nombre d'emprunts) sur une période. */
public class AuthorStatPage extends JPanel {

    private final MainFrame mainFrame;
    private final LoanController loanController = new LoanController();
    private final JSpinner from = SwingUtils.dateSpinner(LocalDate.now().minusYears(1));
    private final JSpinner to = SwingUtils.dateSpinner(LocalDate.now());
    private final DefaultTableModel tableModel = SwingUtils.readOnlyModel(
            "Nom", "Prénom", "Nombre d'emprunts", "Part des emprunts");

    public AuthorStatPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        FormLayout form = new FormLayout(formPanel);
        form.title("Auteurs les plus lus sur une période");
        form.field("Du", from);
        form.field("Au", to);
        JButton search = new JButton("Calculer");
        search.addActionListener(e -> compute());
        form.button(search);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(SwingUtils.table(tableModel)), BorderLayout.CENTER);
    }

    private void compute() {
        try {
            List<AuthorStat> stats = loanController.getMostReadAuthors(SwingUtils.getDate(from), SwingUtils.getDate(to));
            tableModel.setRowCount(0);
            for (AuthorStat stat : stats) {
                tableModel.addRow(new Object[]{
                        stat.author().getFamilyName(), stat.author().getFirstName(), stat.nbLoans(),
                        String.format("%.2f %%", stat.percentage())
                });
            }
            if (stats.isEmpty()) mainFrame.displayMessage("Aucun emprunt sur cette période.", "Résultat vide");
        } catch (ValidationException | DataAccessException e) {
            mainFrame.displayException(e);
        }
    }
}
