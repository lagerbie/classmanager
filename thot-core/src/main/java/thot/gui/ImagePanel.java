package thot.gui;

import java.awt.*;

import javax.swing.*;

/**
 * Panneau simple pour l'affichage d'une image.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class ImagePanel extends JPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Imge de fond.
     */
    private Image background;

    /**
     * Initialisation du panneau avec une image. Le panneau est redimensionné pour s'ajuster aux dimensions de l'image.
     *
     * @param image l'image à afficher.
     */
    public ImagePanel(Image image) {
        this(image, -1, -1);
    }

    /**
     * Initialisation du panneau avec une image. L'image est redimensionnée aux dimensions indiquées.
     *
     * @param image l'image à afficher.
     * @param width la largeur du panneau.
     * @param height la hauteur du panneau.
     */
    public ImagePanel(Image image, int width, int height) {
        super();
        this.background = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        Dimension dim = new Dimension(background.getWidth(null), background.getHeight(null));
        this.setPreferredSize(dim);
        this.setMinimumSize(dim);
        this.setMaximumSize(dim);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(background, 0, 0, this);
    }
}
