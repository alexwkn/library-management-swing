package com.librarymanagement.view;

import com.librarymanagement.controller.BookController;
import com.librarymanagement.exception.DataAccessException;
import com.librarymanagement.model.Author;
import com.librarymanagement.model.Book;
import com.librarymanagement.model.Category;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.stream.Collectors;

public class BookListingPage extends JPanel {

    private final MainFrame mainFrame;
    private final BookController bookController = new BookController();
    private final DefaultTableModel tableModel = SwingUtils.readOnlyModel(
            "ID", "ISBN", "Titre", "Publication", "Langue", "Pages", "Disponible", "Prix (€)",
            "Bibliothèque", "Auteurs", "Catégories", "Résumé", "Commentaire");
    private final JTable booksTable = SwingUtils.table(tableModel);

    public BookListingPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        add(new JScrollPane(booksTable), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Actualiser");
        JButton addButton = new JButton("Ajouter un livre");
        JButton updateButton = new JButton("Modifier le livre");
        JButton deleteButton = new JButton("Supprimer le livre");
        refreshButton.addActionListener(e -> refresh());
        addButton.addActionListener(e -> mainFrame.switchPage(new BookFormPanel(mainFrame, null)));
        updateButton.addActionListener(e -> editSelectedBook());
        deleteButton.addActionListener(e -> deleteSelectedBook());

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
            for (Book book : bookController.getAllBooks()) {
                tableModel.addRow(new Object[]{
                        book.getId(), book.getIsbnCode(), book.getTitle(), book.getPublicationDate(),
                        book.getLanguage(), book.getNbPages(), SwingUtils.yesNo(book.isAvailable()),
                        String.format("%.2f", book.getPrice()),
                        book.getLibrary().getName(),
                        book.getAuthors().stream().map(Author::getDisplayName).collect(Collectors.joining(" ; ")),
                        book.getCategories().stream().map(Category::getLabel).collect(Collectors.joining(", ")),
                        book.getSummary(), book.getCommentary()
                });
            }
        } catch (DataAccessException e) {
            mainFrame.displayException(e);
        }
    }

    private Integer selectedBookId() {
        int row = booksTable.getSelectedRow();
        return row == -1 ? null : (Integer) tableModel.getValueAt(booksTable.convertRowIndexToModel(row), 0);
    }

    private void editSelectedBook() {
        Integer bookId = selectedBookId();
        if (bookId == null) {
            mainFrame.displayError("Veuillez sélectionner un livre à modifier.");
            return;
        }
        try {
            bookController.getBook(bookId).ifPresentOrElse(
                    book -> mainFrame.switchPage(new BookFormPanel(mainFrame, book)),
                    () -> {
                        mainFrame.displayError("Ce livre n'existe plus.");
                        refresh();
                    });
        } catch (DataAccessException e) {
            mainFrame.displayException(e);
        }
    }

    private void deleteSelectedBook() {
        Integer bookId = selectedBookId();
        if (bookId == null) {
            mainFrame.displayError("Veuillez sélectionner un livre à supprimer.");
            return;
        }
        if (!mainFrame.confirm("Supprimer le livre sélectionné ?", "Confirmer la suppression")) {
            return;
        }
        try {
            bookController.deleteBook(bookId);
            mainFrame.displayMessage("Livre supprimé avec succès.", "Succès");
            refresh();
        } catch (DataAccessException e) {
            mainFrame.displayException(e);
        }
    }
}
