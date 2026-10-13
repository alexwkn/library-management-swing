package com.librarymanagement.view;

import com.librarymanagement.dao.DatabaseConnection;
import com.librarymanagement.exception.DataAccessException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Le look & feel par défaut de Swing sera utilisé
            }

            try {
                DatabaseConnection.get();
            } catch (DataAccessException e) {
                JOptionPane.showMessageDialog(null,
                        e.getMessage() + "\n\nVérifiez que la base est démarrée (docker compose up -d)\n"
                                + "ou que les variables DB_URL, DB_USER et DB_PASSWORD sont correctes.",
                        "Base de données inaccessible", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }

            new MainFrame().setVisible(true);
        });
    }
}
