package com.librarymanagement.view;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MenuBar {

    private final JMenuBar menuBar = new JMenuBar();
    private final MainFrame mainFrame;

    public MenuBar(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        JMenu menu = new JMenu("Menu");
        menu.add(item("Accueil", () -> mainFrame.switchPage(mainFrame.getHomePage())));
        menu.addSeparator();
        menu.add(item("Quitter", mainFrame::exit));

        JMenu crud = new JMenu("Gestion");
        crud.add(item("Utilisateurs", mainFrame::showUserListing));
        crud.addSeparator();
        crud.add(item("Livres", mainFrame::showBookListing));

        JMenu searches = new JMenu("Recherches");
        searches.add(item("Livres par auteur", () -> mainFrame.switchPage(mainFrame.getAuthorSearchPage())));
        searches.addSeparator();
        searches.add(item("Livres par date de publication", () -> mainFrame.switchPage(mainFrame.getDateSearchPage())));
        searches.addSeparator();
        searches.add(item("Emprunts sur une période", () -> mainFrame.switchPage(mainFrame.getLoanSearchPage())));

        JMenu statistics = new JMenu("Statistiques");
        statistics.add(item("Auteurs les plus lus sur une période", () -> mainFrame.switchPage(mainFrame.getAuthorStatPage())));
        statistics.addSeparator();
        statistics.add(item("Catégories préférées par tranche d'âge", () -> mainFrame.switchPage(mainFrame.getCategoryStatPage())));

        JMenu animation = new JMenu("Animation");
        animation.add(item("Animation (thread)", () -> mainFrame.switchPage(mainFrame.getAnimationPage())));

        menuBar.add(menu);
        menuBar.add(crud);
        menuBar.add(searches);
        menuBar.add(statistics);
        menuBar.add(animation);
    }

    public JMenuBar getMenuBar() {
        return menuBar;
    }

    private JMenuItem item(String label, Runnable action) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(e -> action.run());
        return item;
    }
}
