package thot.supervision.screen;

import java.awt.*;

import thot.gui.GuiUtilities;

/**
 * Fenêtre noire bloquante.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class BlackWindow extends BlockWindow {
    private static final long serialVersionUID = 19000L;

    /**
     * Image de fond.
     */
    private Image backgroundImage;
    /**
     * largeur de l'image.
     */
    private int imageWidth;
    /**
     * largeur de l'image.
     */
    private int imageHeight;

    /**
     * Initialisation.
     */
    public BlackWindow() {
        super();
        backgroundImage = GuiUtilities.getImage("blackScreen");
        imageWidth = backgroundImage.getWidth(null);
        imageHeight = backgroundImage.getHeight(null);

        this.getContentPane().setBackground(Color.BLACK);
        this.setBackground(Color.BLACK);
    }

    @Override
    public void paint(Graphics g) {
        //Dessine l'image centrée sans la redimensionner.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dim = toolkit.getScreenSize();

        int xOffset = (dim.width - imageWidth) / 2;
        int yOffset = (dim.height - imageHeight) / 2;

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, dim.width, dim.height);
        g.drawImage(backgroundImage, xOffset, yOffset, null);
    }
}
