package com.librarymanagement.view;

import com.librarymanagement.dao.DatabaseConnection;
import com.librarymanagement.exception.ValidationException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainFrame extends JFrame {

    private static final String WINDOW_TITLE = "Réseau des bibliothèques de Namur";
    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 700;

    private final JPanel homePage;
    private final JPanel animationPage;
    private final JPanel authorSearchPage;
    private final JPanel dateSearchPage;
    private final JPanel loanSearchPage;
    private final UserListingPage userListingPage;
    private final BookListingPage bookListingPage;
    private final JPanel authorStatPage;
    private final JPanel categoryStatPage;

    public MainFrame() {
        super(WINDOW_TITLE);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);

        homePage = new HomePage();
        animationPage = new AnimationPanel();
        authorSearchPage = new AuthorSearchPage(this);
        dateSearchPage = new DateSearchPage(this);
        loanSearchPage = new LoanSearchPage(this);
        userListingPage = new UserListingPage(this);
        bookListingPage = new BookListingPage(this);
        authorStatPage = new AuthorStatPage(this);
        categoryStatPage = new CategoryStatPage(this);

        setJMenuBar(new MenuBar(this).getMenuBar());
        switchPage(homePage);
    }

    public void switchPage(JPanel page) {
        getContentPane().removeAll();
        getContentPane().add(page);
        revalidate();
        repaint();
    }

    public void displayError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    public void displayMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /** Affiche une erreur métier (saisie invalide) ou technique (base de données) de façon adaptée. */
    public void displayException(Exception e) {
        if (e instanceof ValidationException) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Saisie invalide", JOptionPane.WARNING_MESSAGE);
        } else {
            displayError(e.getMessage());
        }
    }

    public boolean confirm(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public void exit() {
        DatabaseConnection.close();
        dispose();
        System.exit(0);
    }

    public JPanel getHomePage() { return homePage; }
    public JPanel getAnimationPage() { return animationPage; }
    public JPanel getAuthorSearchPage() { return authorSearchPage; }
    public JPanel getDateSearchPage() { return dateSearchPage; }
    public JPanel getLoanSearchPage() { return loanSearchPage; }
    public JPanel getAuthorStatPage() { return authorStatPage; }
    public JPanel getCategoryStatPage() { return categoryStatPage; }

    public void showUserListing() {
        userListingPage.refresh();
        switchPage(userListingPage);
    }

    public void showBookListing() {
        bookListingPage.refresh();
        switchPage(bookListingPage);
    }
}
