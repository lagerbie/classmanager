/*
 * ClassManager - Supervision de classes et Laboratoire de langue
 * Copyright (C) 2013 Fabrice Alleau <fabrice.alleau@siclic.fr>
 *
 * This file is part of ClassManager.
 *
 * ClassManager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ClassManager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ClassManager.  If not, see <http://www.gnu.org/licenses/>.
 */
package thot.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Classe présentant une barre de progression.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class ProcessingBar {

    /**
     * Fenêtre affichant le progression.
     */
    private JDialog dialog;
    /**
     * Barre de progression affichant le pourcentage global.
     */
    private JProgressBar progressBar;
    /**
     * Barre de progression affichant le pourcentage du sous process.
     */
    private JProgressBar subProgressBar;
    /**
     * Label affichant un message.
     */
    private JLabel messageLabel;

    /**
     * Initialise la barre de progression.
     *
     * @param owner la fenêtre parente.
     * @param image l'image de fond.
     */
    public ProcessingBar(Window owner, Image image) {
        dialog = new JDialog(owner, JDialog.DEFAULT_MODALITY_TYPE);
        dialog.setUndecorated(true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(false);

        messageLabel = new JLabel();

        progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        progressBar.setStringPainted(true);
        subProgressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        subProgressBar.setStringPainted(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(subProgressBar);
        panel.add(progressBar);

        dialog.getContentPane().add(messageLabel, BorderLayout.NORTH);
        dialog.getContentPane().add(panel, BorderLayout.SOUTH);

        if (image != null) {
            JPanel imagePanel = new ImagePanel(image);
            dialog.getContentPane().add(imagePanel, BorderLayout.CENTER);
        }
        dialog.pack();

        dialog.setIconImages(GuiUtilities.getIcons());

        MouseAdapter mouseAdapter = new MouseAdapter() {
            private int mouseX = 0;
            private int mouseY = 0;

            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getXOnScreen();
                mouseY = e.getYOnScreen();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int x = dialog.getX() + e.getXOnScreen() - mouseX;
                int y = dialog.getY() + e.getYOnScreen() - mouseY;
                dialog.setLocation(x, y);
                mouseX = e.getXOnScreen();
                mouseY = e.getYOnScreen();
            }
        };
        dialog.addMouseListener(mouseAdapter);
        dialog.addMouseMotionListener(mouseAdapter);
    }

    /**
     * Change le titre de la barre de progression.
     *
     * @param title le nouveau titre.
     */
    public void setTitle(String title) {
        dialog.setTitle(title);
    }

    /**
     * Change le message affiché dans la barre de progression.
     *
     * @param message le nouveau message.
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
        update();
    }

    /**
     * Change la valeur de progrssion de la barre.
     *
     * @param value la nouvelle valeur.
     */
    public void setValue(int value) {
        progressBar.setValue(value);
        if (progressBar.isIndeterminate()) {
            progressBar.setString(" ");
        } else {
            progressBar.setString(value + " %");
        }
    }

    /**
     * Modifie le poucentage.
     *
     * @param total la nouvelle valeur de progression totale en pourcentage.
     * @param subTotal la nouvelle valeur de progression intermédiaire en
     * pourcentage.
     */
    public void setValue(int total, int subTotal) {
        progressBar.setValue(total);
        progressBar.setString(total + " %");
        subProgressBar.setValue(subTotal);
        subProgressBar.setString(subTotal + " %");
    }

    /**
     * Change le statut déterminé de la barre de progression.
     *
     * @param determinated <code>true</code> pour le mode déterminé, ou
     * <code>false</code> pour le mode indéterminé.
     */
    public void setDeterminate(boolean determinated) {
        progressBar.setIndeterminate(!determinated);
    }

    /**
     * Modifie l'affichage d'une barre secondaire pour les étapes intermédiares.
     *
     * @param doubleStatus <code>true</code> pour afficher deux barres de
     * progression, ou <code>false</code> pour une seule barre.
     */
    public void setDoubleProgress(boolean doubleStatus) {
        subProgressBar.setVisible(doubleStatus);
    }

    /**
     * Montre la barre de progrssion.
     */
    public void show() {
        setValue(0, 0);
        update();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.setAlwaysOnTop(true);
                dialog.setVisible(true);
                dialog.toFront();
            }
        });
    }

    /**
     * Ferme la barre de progression.
     */
    public void close() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
    }

    /**
     * Retourne si la fenêtre est affichée.
     *
     * @return si la fenêtre est affichée.
     */
    public boolean isVisible() {
        return dialog.isVisible();
    }

    /**
     * Ajoute d'une écoute du type WindowListener à la fenêtre principale.
     *
     * @param listener l'écoute à ajouter.
     */
    public void addWindowListener(WindowListener listener) {
        dialog.addWindowListener(listener);
    }

    /**
     * Met à jour les dimensions et la postion de la fenêtre.
     */
    private void update() {
        dialog.pack();

        //centrage de la fenêtre
        Window owner = dialog.getOwner();
        Dimension dim;
        if (owner != null) {
            dim = owner.getSize();
        } else {
            dim = Toolkit.getDefaultToolkit().getScreenSize();
        }

        dialog.setLocation((dim.width - dialog.getWidth()) / 2,
                (dim.height - dialog.getHeight()) / 2);
    }

    /**
     * Débute un processus d'attente.
     *
     * @param title le titre pour la fenêtre.
     * @param message le message pour la barre de progression.
     * @param formatValue les objets pour le message si il est formaté
     * @param determinated si le processus à un poucentage déterminé.
     */
    public void processBegin(boolean determinated, String title,
            String message, Object... formatValue) {
        setTitle(title);
        if (formatValue == null) {
            setMessage(message);
        } else {
            setMessage(String.format(message, formatValue));
        }
        processBegin(determinated);
    }

    /**
     * Débute un processus d'attente.
     *
     * @param determinated indique si le processus à un poucentage déterminé.
     */
    public void processBegin(boolean determinated) {
        setDeterminate(determinated);
        show();
    }
}
