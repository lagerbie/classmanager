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
package supervision.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import java.awt.event.WindowListener;
import javax.swing.JPanel;

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
    private JDialog frame;
    /**
     * Barre de progression affichant le pourcentage global.
     */
    private JProgressBar progressBar = null;
    /**
     * Barre de progression affichant le pourcentage du sous process.
     */
    private JProgressBar subProgressBar = null;
    /**
     * Label affichant un message.
     */
    private JLabel messageLabel;

    /**
     * Initialise la barre de progression.
     *
     * @param owner la fenêtre parente.
     */
    public ProcessingBar(Window owner) {
        frame = new JDialog(owner, JDialog.DEFAULT_MODALITY_TYPE);
        frame.setUndecorated(true);
        frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);

        messageLabel = new JLabel();

        progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        progressBar.setStringPainted(true);
        subProgressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        subProgressBar.setStringPainted(true);

        JPanel imagePanel = new ImagePanel(GuiUtilities.getImage("fileScreen"));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(subProgressBar);
        panel.add(progressBar);

        frame.getContentPane().add(messageLabel, BorderLayout.NORTH);
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        frame.getContentPane().add(imagePanel, BorderLayout.CENTER);
        frame.pack();

        frame.setIconImages(GuiUtilities.getIcons());

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
                int x = frame.getX() + e.getXOnScreen() - mouseX;
                int y = frame.getY() + e.getYOnScreen() - mouseY;
                frame.setLocation(x, y);
                mouseX = e.getXOnScreen();
                mouseY = e.getYOnScreen();
            }
        };
        frame.addMouseListener(mouseAdapter);
        frame.addMouseMotionListener(mouseAdapter);
    }

    /**
     * Change le titre de la barre de progression.
     *
     * @param title le nouveau titre.
     */
    public void setTitle(String title) {
        frame.setTitle(title);
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
                frame.setAlwaysOnTop(true);
                frame.setVisible(true);
                frame.toFront();
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
                frame.setVisible(false);
                frame.dispose();
            }
        });
    }

    /**
     * Retourne si la fenêtre est affichée.
     *
     * @return si la fenêtre est affichée.
     */
    public boolean isVisible() {
        return frame.isVisible();
    }

    /**
     * Ajoute d'une écoute du type WindowListener à la fenêtre principale.
     *
     * @param listener l'écoute à ajouter.
     */
    public void addWindowListener(WindowListener listener) {
        frame.addWindowListener(listener);
    }

    /**
     * Met à jour les dimensions et la postion de la fenêtre.
     */
    private void update() {
        frame.pack();

        //centrage de la fenêtre
        Window owner = frame.getOwner();
        Dimension dim;
        if (owner != null) {
            dim = owner.getSize();
        } else {
            dim = Toolkit.getDefaultToolkit().getScreenSize();
        }

        frame.setLocation((dim.width - frame.getWidth()) / 2,
                (dim.height - frame.getHeight()) / 2);
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
