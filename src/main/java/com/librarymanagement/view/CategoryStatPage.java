package com.librarymanagement.view;

import com.librarymanagement.controller.CategoryController;
import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.model.CategoryStat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

/** Tâche métier : catégories préférées des membres selon leur tranche d'âge. */
public class CategoryStatPage extends JPanel {

    /** Tranche d'âge ; {@code maxAge == null} signifie « et plus ». */
    private record AgeRange(int minAge, Integer maxAge) {
        @Override
        public String toString() {
            return maxAge == null ? minAge + " ans et plus" : minAge + " à " + maxAge + " ans";
        }
    }

    private final MainFrame mainFrame;
    private final CategoryController categoryController = new CategoryController();
    private final JComboBox<AgeRange> ageRange = new JComboBox<>(new AgeRange[]{
            new AgeRange(0, 17), new AgeRange(18, 25), new AgeRange(26, 49),
            new AgeRange(50, 64), new AgeRange(65, null)
    });
    private final DefaultTableModel tableModel = SwingUtils.readOnlyModel(
            "Catégorie", "Nombre de membres", "Pourcentage");

    public CategoryStatPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        FormLayout form = new FormLayout(formPanel);
        form.title("Catégories préférées par tranche d'âge");
        form.field("Tranche d'âge", ageRange);
        JButton search = new JButton("Calculer");
        search.addActionListener(e -> compute());
        form.button(search);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(SwingUtils.table(tableModel)), BorderLayout.CENTER);
    }

    private void compute() {
        AgeRange range = (AgeRange) ageRange.getSelectedItem();
        if (range == null) return;
        try {
            List<CategoryStat> stats = categoryController.getPreferredCategoriesByAge(range.minAge(), range.maxAge());
            tableModel.setRowCount(0);
            for (CategoryStat stat : stats) {
                tableModel.addRow(new Object[]{stat.label(), stat.nbUsers(), String.format("%.2f %%", stat.percentage())});
            }
            if (stats.isEmpty()) mainFrame.displayMessage("Aucun membre dans cette tranche d'âge.", "Résultat vide");
        } catch (DataAccessException e) {
            mainFrame.displayException(e);
        }
    }
}
