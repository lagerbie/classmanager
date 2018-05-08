package thot.supervision.gui;

import java.awt.*;

import javax.swing.*;

import thot.gui.ImagePanel;

/**
 * Fenêtre pour afficher un A propos de.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class AboutDialog extends JDialog {
    private static final long serialVersionUID = 19000L;

    public AboutDialog(Window owner, String title, Image image) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);

        ImagePanel imagePanel = new ImagePanel(image, 600, -1);

        this.getContentPane().add(imagePanel);
        this.pack();
        this.setLocation((owner.getWidth() - imagePanel.getWidth()) / 2,
                (owner.getHeight() - imagePanel.getHeight()) / 2);
    }
}
