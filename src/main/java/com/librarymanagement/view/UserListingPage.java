package com.librarymanagement.view;

import com.librarymanagement.controller.UserController;
import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.model.User;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class UserListingPage extends JPanel {

    private final MainFrame mainFrame;
    private final UserController userController = new UserController();
    private final DefaultTableModel tableModel = SwingUtils.readOnlyModel(
            "ID", "Nom", "Prénom", "Pseudo", "Email", "Date de naissance", "Téléphone", "N° rue", "Rue",
            "Code postal", "Localité", "Boîte", "Pays", "Étudiant", "Admin", "Catégorie préférée");
    private final JTable usersTable = SwingUtils.table(tableModel);

    public UserListingPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        add(new JScrollPane(usersTable), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Actualiser");
        JButton addButton = new JButton("Ajouter un utilisateur");
        JButton updateButton = new JButton("Modifier l'utilisateur");
        JButton deleteButton = new JButton("Supprimer");
        refreshButton.addActionListener(e -> refresh());
        addButton.addActionListener(e -> mainFrame.switchPage(new UserFormPanel(mainFrame, null)));
        updateButton.addActionListener(e -> editSelectedUser());
        deleteButton.addActionListener(e -> deleteSelectedUsers());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refresh() {
        try {
            tableModel.setRowCount(0);
            for (User user : userController.getAllUsers()) {
                tableModel.addRow(new Object[]{
                        user.getId(), user.getFamilyName(), user.getFirstName(), user.getPseudo(), user.getEmail(),
                        user.getBirthdate(), user.getPhoneNumber(), user.getStreetNumber(), user.getStreetName(),
                        user.getPostcode(), user.getLocality(), user.getPostbox(), user.getCountry(),
                        SwingUtils.yesNo(user.isStudent()), SwingUtils.yesNo(user.isAdmin()), user.getPreferredCategory()
                });
            }
        } catch (DataAccessException e) {
            mainFrame.displayException(e);
        }
    }

    private Integer selectedUserId(int viewRow) {
        return (Integer) tableModel.getValueAt(usersTable.convertRowIndexToModel(viewRow), 0);
    }

    private void editSelectedUser() {
        int row = usersTable.getSelectedRow();
        if (row == -1) {
            mainFrame.displayError("Veuillez sélectionner un utilisateur à modifier.");
            return;
        }
        try {
            userController.getUser(selectedUserId(row)).ifPresentOrElse(
                    user -> mainFrame.switchPage(new UserFormPanel(mainFrame, user)),
                    () -> {
                        mainFrame.displayError("Cet utilisateur n'existe plus.");
                        refresh();
                    });
        } catch (DataAccessException e) {
            mainFrame.displayException(e);
        }
    }

    private void deleteSelectedUsers() {
        int[] rows = usersTable.getSelectedRows();
        if (rows.length == 0) {
            mainFrame.displayError("Veuillez sélectionner un ou plusieurs utilisateurs à supprimer.");
            return;
        }
        String question = rows.length == 1
                ? "Supprimer l'utilisateur sélectionné ?"
                : "Supprimer les " + rows.length + " utilisateurs sélectionnés ?";
        if (!mainFrame.confirm(question + "\nUn utilisateur ayant des emprunts ne peut pas être supprimé.",
                "Confirmer la suppression")) {
            return;
        }
        int deleted = 0;
        StringBuilder errors = new StringBuilder();
        for (int row : rows) {
            int userId = selectedUserId(row);
            try {
                userController.deleteUser(userId);
                deleted++;
            } catch (DataAccessException e) {
                errors.append("Utilisateur ").append(userId).append(" : ").append(e.getMessage()).append('\n');
            }
        }
        refresh();
        if (errors.isEmpty()) {
            mainFrame.displayMessage(deleted + " utilisateur(s) supprimé(s).", "Suppression réussie");
        } else {
            mainFrame.displayError(deleted + " utilisateur(s) supprimé(s).\n\n" + errors);
        }
    }
}
