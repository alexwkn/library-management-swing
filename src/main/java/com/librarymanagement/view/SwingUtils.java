package com.librarymanagement.view;

import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/** Petits utilitaires partagés par les vues. */
final class SwingUtils {

    private SwingUtils() {
    }

    /** Sélecteur de date au format jj/mm/aaaa, initialisé à {@code initial}. */
    static JSpinner dateSpinner(LocalDate initial) {
        JSpinner spinner = new JSpinner(new SpinnerDateModel(toDate(initial), null, null, Calendar.DAY_OF_MONTH));
        spinner.setEditor(new JSpinner.DateEditor(spinner, "dd/MM/yyyy"));
        return spinner;
    }

    static LocalDate getDate(JSpinner spinner) {
        return ((Date) spinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    static void setDate(JSpinner spinner, LocalDate date) {
        spinner.setValue(toDate(date));
    }

    private static Date toDate(LocalDate date) {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /** Modèle de tableau en lecture seule. */
    static DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    static JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoCreateRowSorter(true);
        return table;
    }

    static String yesNo(boolean value) {
        return value ? "Oui" : "Non";
    }
}
