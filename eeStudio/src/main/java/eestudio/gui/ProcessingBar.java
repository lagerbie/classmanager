package eestudio.gui;

import java.awt.*;
import java.awt.event.WindowListener;

import javax.swing.*;

/**
 * Classe présentant une barre de progression.
 *
 * @author Fabrice Alleau
 * @version 0.99
 * @since version 0.95
 */
public class ProcessingBar {
    private JDialog frame;
    /**
     * Barre de progression affichant le pourcentage
     */
    private JProgressBar progressBar;
    /**
     * Label affichant un message
     */
    private JLabel label;

    /**
     * Initialise la barre de progression.
     *
     * @since version 0.95 - version 0.99
     */
    public ProcessingBar() {
        frame = new JDialog(null, JDialog.DEFAULT_MODALITY_TYPE);
        frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);
        frame.setIconImages(GuiUtilities.icones);

        label = new JLabel();

        progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        progressBar.setStringPainted(true);

        BoxLayout layout = new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS);
        frame.getContentPane().setLayout(layout);
        frame.getContentPane().add(label);
        frame.getContentPane().add(progressBar);
    }

    /**
     * Change le titre de la barre de progression.
     *
     * @param title le nouveau titre.
     *
     * @since version 0.95
     */
    private void setTitle(String title) {
        frame.setTitle(title);
    }

    /**
     * Change le message affiché dans la barre de progression.
     *
     * @param message le nouveau message.
     *
     * @since version 0.95
     */
    private void setMessage(String message) {
        label.setText(message);
        update();
    }

    /**
     * Change la valeur de la barre de progression.
     *
     * @param total la nouvelle valeur de progression totale en pourcentage.
     *
     * @since version 0.95 - version 0.96
     */
    public void setValue(int total) {
        progressBar.setValue(total);
        if (progressBar.isIndeterminate()) {
            progressBar.setString(" ");
        } else {
            progressBar.setString(total + " %");
        }
    }

    /**
     * Change le statut déterminé de la barre de progression.
     *
     * @param determinated {@code true} pour le mode déterminé, ou
     *         {@code false} pour le mode indéterminé.
     *
     * @since version 0.95 - version 0.96
     */
    private void setDeterminate(boolean determinated) {
        progressBar.setIndeterminate(!determinated);
    }

    /**
     * Montre la barre de progrssion.
     *
     * @since version 0.95 - version 0.97
     */
    public void show() {
        setValue(0);
        update();
        SwingUtilities.invokeLater(() -> {
            frame.setAlwaysOnTop(true);
            frame.setVisible(true);
            frame.toFront();
        });
    }

    /**
     * Ferme la barre de progression.
     *
     * @since version 0.95 - version 0.97
     */
    public void close() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(false);
            frame.dispose();
        });
    }

//    /**
//     * Retourne si la fenêtre est affichée.
//     * 
//     * @return si la fenêtre est affichée.
//     * @since version 0.95.12
//     */
//    public boolean isVisible() {
//        return frame.isVisible();
//    }

    /**
     * Ajoute d'une écoute du type WindowListener à la fenêtre principale.
     *
     * @param listener l'écoute à ajouter.
     *
     * @since version 0.96
     */
    public void addWindowListener(WindowListener listener) {
        frame.addWindowListener(listener);
    }

    /**
     * Met à jour les dimensions et la postion de la fenêtre.
     *
     * @since version 0.95
     */
    private void update() {
        frame.pack();
        Dimension dim = new Dimension(1024, 768);

        //centrage de la fenêtre
        frame.setLocation((dim.width - frame.getWidth()) / 2, (dim.height - frame.getHeight()) / 2);
    }

    /**
     * Débute un processus d'attente.
     *
     * @param title le titre pour la fenêtre.
     * @param message le message pour la barre de progression.
     * @param formatValue les objets pour le message si il est formaté
     * @param determinated si le processus à un poucentage déterminé.
     *
     * @since version 0.95.12
     */
    public void processBegin(boolean determinated, String title, String message, Object... formatValue) {
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
     *
     * @since version 0.95.10 - version 0.96
     */
    public void processBegin(boolean determinated) {
        setDeterminate(determinated);
        show();
    }

}
