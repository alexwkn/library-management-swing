package com.librarymanagement.view;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

public class HomePage extends JPanel {

    public HomePage() {
        setLayout(new BorderLayout());
        JLabel infoText = new JLabel("""
                <html><div style='text-align:center'>
                <h2>Réseau des bibliothèques de Namur</h2>
                Application de gestion des membres, des livres et des emprunts.<br><br>
                <b>Gestion</b> : ajout, modification et suppression des utilisateurs et des livres<br>
                <b>Recherches</b> : livres par auteur, par date de publication, emprunts sur une période<br>
                <b>Statistiques</b> : auteurs les plus lus, catégories préférées par tranche d'âge<br>
                <b>Animation</b> : démonstration d'un traitement dans un thread séparé<br><br>
                Pour commencer, choisissez une option dans le menu.
                </div></html>
                """);
        infoText.setHorizontalAlignment(SwingConstants.CENTER);
        add(infoText, BorderLayout.CENTER);
    }
}
