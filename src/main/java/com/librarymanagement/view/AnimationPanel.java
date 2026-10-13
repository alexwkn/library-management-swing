package com.librarymanagement.view;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.net.URL;

/**
 * Démonstration d'un thread : un livre grossit puis reprend sa taille.
 * Le calcul tourne dans un thread séparé ; les rafraîchissements sont renvoyés sur l'EDT de Swing.
 */
public class AnimationPanel extends JPanel {

    private static final float MIN_SCALE = 0.2f;
    private static final int STEPS = 10;
    private static final int FRAME_DELAY_MS = 20;

    private final ImageIcon bookIcon;
    private final JPanel bookPanel;
    private final JButton animateButton;
    private volatile float scale = MIN_SCALE;

    public AnimationPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 255));

        URL imageUrl = getClass().getResource("/assets/book.png");
        bookIcon = imageUrl == null ? null : new ImageIcon(imageUrl);

        bookPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bookIcon == null) return;
                int w = (int) (bookIcon.getIconWidth() * scale);
                int h = (int) (bookIcon.getIconHeight() * scale);
                g.drawImage(bookIcon.getImage(), (getWidth() - w) / 2, (getHeight() - h) / 2, w, h, this);
            }
        };
        bookPanel.setPreferredSize(new Dimension(220, 220));
        bookPanel.setOpaque(false);

        animateButton = new JButton("Lancer l'animation");
        animateButton.setFont(new Font("SansSerif", Font.PLAIN, 13));
        animateButton.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        animateButton.addActionListener(e -> startAnimation());

        JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        linePanel.setOpaque(false);
        linePanel.add(animateButton);
        linePanel.add(bookPanel);
        add(linePanel, BorderLayout.NORTH);
    }

    private void startAnimation() {
        animateButton.setEnabled(false);
        Thread animation = new Thread(() -> {
            for (int i = 0; i <= STEPS; i++) {
                setScale(0.4f + i * 0.04f);
            }
            for (int i = STEPS; i >= 0; i--) {
                setScale(MIN_SCALE + i * 0.04f);
            }
            SwingUtilities.invokeLater(() -> animateButton.setEnabled(true));
        }, "book-animation");
        animation.setDaemon(true);
        animation.start();
    }

    private void setScale(float newScale) {
        scale = newScale;
        SwingUtilities.invokeLater(bookPanel::repaint);
        try {
            Thread.sleep(FRAME_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
