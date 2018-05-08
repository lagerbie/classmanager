package thot.supervision.gui;

import java.awt.*;

import javax.swing.*;

/**
 * Composant affichant un message.
 *
 * @version 1.8.4
 */
public class PostIt extends JPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Composant pour l'affichage du message.
     */
    private JLabel messageLabel;

    /**
     * Initialisation.
     *
     * @param textColor la couleur du texte à afficher.
     */
    public PostIt(Color textColor) {
        super();

        messageLabel = new JLabel();
        messageLabel.setForeground(textColor);
        this.add(messageLabel);
    }

    /**
     * Modifie le message affiché.
     *
     * @param message le nouveau message.
     */
    public void setText(String message) {
        messageLabel.setText(message);
    }
}
