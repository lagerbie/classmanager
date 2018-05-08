package thot.gui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * Panel pour l'image de fond.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class FilterPanel extends JPanel {
    private static final long serialVersionUID = 19000L;

    public static final int RIGHT = TitledBorder.RIGHT;
    public static final int LEFT = TitledBorder.LEFT;

    /**
     * Couleur de fond.
     */
    private Color backgroundColor = new Color(60, 60, 60, 97);
    /**
     * Bordure avec titre.
     */
    private TitledBorder titledBorder;
    /**
     * Offset vertical pour dessiner l'image.
     */
    private int yOffset;
    /**
     * Largeur de la fenêtre.
     */
    private int width;
    /**
     * Hauteur de la fenêtre.
     */
    private int rectHeight;

    /**
     * Initialisation avec l'étiquette à afficher et ses dimensions.
     *
     * @param title l'étiquette à afficher.
     * @param dim les dimensions du panneau.
     */
    public FilterPanel(String title, Dimension dim) {
        this(title, LEFT, dim, -1);

        this.setMaximumSize(dim);
        this.setPreferredSize(dim);
//        this.setBorder(BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(-10, -10, -10, -10)));
    }

    /**
     * Initialisation avec le chemin de l'image.
     *
     * @param title le nom sur le panneau.
     * @param position la position du nom.
     * @param dimension les dimensions du panneau.
     * @param rectHeight la hauteur du rectangle dans le panneau.
     */
    public FilterPanel(String title, int position, Dimension dimension, int rectHeight) {
        super();
        this.width = dimension.width;
        this.rectHeight = rectHeight;

        this.setPreferredSize(dimension);
        this.setMinimumSize(dimension);
        this.setMaximumSize(dimension);

        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 11);
        Color color = new Color(0, 0, 0, 97);

        titledBorder = BorderFactory
                .createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), title, position, TitledBorder.TOP,
                        font, color);

        this.setBorder(
                BorderFactory.createCompoundBorder(titledBorder, BorderFactory.createEmptyBorder(-20, -20, -20, -20)));

        yOffset = (dimension.height - rectHeight) / 2;
    }

    /**
     * Change le titre du panneau.
     *
     * @param title le nouveau titre.
     */
    public void changeTitle(String title) {
        titledBorder.setTitle(title);
    }

    /**
     * Modifie l'offset vertical pour dessiner le filtre.
     *
     * @param yOffset l'offset vertical.
     */
    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (rectHeight > 0) {
            g.setColor(backgroundColor);
            g.fillRect(0, yOffset, width, rectHeight);
        }
    }
}