package eestudio.gui;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * Slider général pour le controle de volume et de vitesse.
 *
 * @author Fabrice Alleau
 * @since version 1.01
 */
public class BackgroundPanel extends JPanel {
    private static final long serialVersionUID = 10100L;

    /** Image de fond */
    private Image background;

    private int offset;
    private int width;
    private int height;

    /**
     * Initialisation du slider pour un volume.
     *
     * @param width la largeur du composant.
     * @param height la hauteur du composant.
     * @param offset la taille de la zone de transition.
     * @since version 1.01
     */
    public BackgroundPanel(int width, int height, int offset) {
        this.width = width;
        this.height = height;
        this.offset = offset;
        this.background = getBackgroundImage(width, height, offset);
    }

    /**
     * Dessine le composant.
     *
     * @param g le graphique.
     * @since version 1.01
     */
    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(background, 0, 0, getWidth(), getHeight(), 0, 0, width, height, null);
        if(width != getWidth() || height != getHeight()) {
            width = getWidth();
            height = getHeight();
            background = getBackgroundImage(width, height, offset);
        }
    }

    /**
     * Retourne l'image de fond pour le slider.
     * 
     * @param width la largeur du composant.
     * @param height la hauteur du composant.
     * @param offset la taille de la zone de transition.
     * @return l'Image correspondante.
     * @since version 1.01
     */
    private Image getBackgroundImage(int width, int height, int offset) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);

        Color color = new Color(140, 140, 140);
        Paint paint;

        //traçage couleur principale
        graphics.setColor(color);
	graphics.fillRect(0, 0, width, height);

        //traçage colonne de fin
        paint = new GradientPaint(width-offset, 0, color, width, 0, color.darker().darker());
	graphics.setPaint(paint);
	graphics.fillRect(width-offset, 0, offset, height);
        //traçage ligne de fin
        paint = new GradientPaint(0, height-offset, color, 0, height, color.darker().darker());
	graphics.setPaint(paint);
	graphics.fillRect(0, height-offset, width, offset);
        //traçage colonne de début
        paint = new GradientPaint(0, 0, color.brighter(), offset, 0, color);
	graphics.setPaint(paint);
	graphics.fillRect(0, 0, offset, height);
        //traçage ligne de début
        paint = new GradientPaint(0, 0, color.brighter(), 0, offset, color);
	graphics.setPaint(paint);
	graphics.fillRect(0, 0, width, offset);

        //traçage coin supérieur gauche
        paint = new RadialGradientPaint(offset, offset, offset,
                new float[]{0f, 1f}, new Color[]{color, color.brighter()});
	graphics.setPaint(paint);
	graphics.fillRect(0, 0, offset, offset);
        //traçage coin inférieur droit
        paint = new RadialGradientPaint(width - offset, height - offset, offset,
                new float[]{0f, 1f}, new Color[]{color, color.darker().darker()});
	graphics.setPaint(paint);
	graphics.fillRect(width - offset, height - offset, offset, offset);
        //traçage coin supérieur droit
        paint = new LinearGradientPaint(width-offset, 0, width, offset,
                new float[]{0f, 0.5f, 1f}, new Color[]{
                    new Color(200, 200, 200, 100),
                    new Color(140, 140, 140, 250),
                    new Color(67, 67, 67, 255)});
	graphics.setPaint(paint);
	graphics.fillRect(width-offset, 0, offset, offset);
        //traçage coin inférieur gauche
        paint = new LinearGradientPaint(0, height-offset, offset, height,
                new float[]{0f, 0.5f, 1f}, new Color[]{
                    new Color(200, 200, 200, 100),
                    new Color(140, 140, 140, 250),
                    new Color(67, 67, 67, 255)});
	graphics.setPaint(paint);
	graphics.fillRect(0, height-offset, offset, height);

        graphics.dispose();
        return image;
    }

//    public static void main(String[] args){
//        javax.swing.JFrame frame = new javax.swing.JFrame("test");
//        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//
//        BackgroundPanel panel = new BackgroundPanel(600, 400, 15);
//        frame.getContentPane().add(panel);
//        frame.setPreferredSize(new java.awt.Dimension(600, 400));
//        frame.pack();
//        frame.setVisible(true);
//    }

}//end
