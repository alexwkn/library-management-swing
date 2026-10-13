package com.librarymanagement.view;

import com.librarymanagement.model.BookSearchResult;

import javax.swing.table.DefaultTableModel;
import java.util.List;

/** Remplissage commun des tableaux de résultats de recherche de livres. */
final class SearchResults {

    private SearchResults() {
    }

    static void showBooks(DefaultTableModel model, List<BookSearchResult> results) {
        model.setRowCount(0);
        for (BookSearchResult r : results) {
            model.addRow(new Object[]{
                    r.title(), r.category(), SwingUtils.yesNo(r.available()), r.language(),
                    r.libraryAddress(), r.libraryEmail(), r.libraryPhone()
            });
        }
    }
}
