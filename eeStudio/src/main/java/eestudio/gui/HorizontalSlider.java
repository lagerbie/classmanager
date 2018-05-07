package eestudio.gui;

import java.awt.*;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

/**
 * Slider général pour le controle de volume et de vitesse.
 *
 * @author Fabrice Alleau
 * @version 1.02
 * @since version 1.01
 */
public class HorizontalSlider extends JPanel {
    private static final long serialVersionUID = 10100L;

    /**
     * Fenêtre principale où est le slider (pour le rafraîchissement)
     */
    private Window owner;
    /**
     * Limites du slider
     */
    private Rectangle bounds = null;

    /**
     * Position minimale
     */
    private int cursorMin;
    /**
     * Position maximale
     */
    private int cursorMax;
    /**
     * Position horizontale du curseur
     */
    private int cursorX;
    /**
     * Position verticale pour l'image du curseur
     */
    private int cursorY;
    /**
     * Offset horizontal du centre du curseur par rapport à sa position
     */
    private int middleX;

    /**
     * Image de fond
     */
    private Image background;
    /**
     * Image du curseur
     */
    private Image cursor;
    /**
     * Image du tick
     */
    private Image tick;

    /**
     * Position horizontale du tick
     */
    private int tickX;
    /**
     * Largeur pour l'image du tick
     */
    private int tickWidth;

    /**
     * Valeur minimale du slider
     */
    private double min = 0;
    /**
     * Valeur maximale du slider
     */
    private double max = 100;
    /**
     * Indicateur pour l'échalle logarithmique
     */
    private boolean log = false;

    /**
     * Initialisation du slider pour un volume.
     *
     * @param owner la frame parente.
     * @param width la largeur du composant.
     * @param height la hauteur du composant.
     *
     * @since version 1.01 - version 1.02
     */
    public HorizontalSlider(Window owner, int width, int height) {
        this.owner = owner;
        initComponents(width, height);
        addListeners();
        ToolTipManager.sharedInstance().setInitialDelay(200);
        ToolTipManager.sharedInstance().setReshowDelay(200);
//        ToolTipManager.sharedInstance().setDismissDelay(200);
    }

    /**
     * Initialisation des composants graphiques.
     *
     * @param width la largeur du composant.
     * @param height la hauteur du composant.
     *
     * @since version 1.01
     */
    private void initComponents(int width, int height) {
        double pos = 0.5;
        if (cursorMax != cursorMin) {
            pos = (double) (cursorX - cursorMin) / (cursorMax - cursorMin);
        }

        int offset = 2;  //offset pour l'ombre
        int cursorDim = height - offset; //dimension du curseur (carré)
        if (cursorDim <= 0) {
            return;
        }

        tickWidth = height / 2; //largeur pour le tick de la valeur centrale
        tickX = (width - tickWidth) / 2; //offset du tick

        middleX = cursorDim / 2;
        cursorY = (height - cursorDim) / 2;
        cursorMin = (cursorDim + 1) / 2 - middleX;
        cursorMax = width - (cursorDim + 1) / 2 - middleX;
        cursorX = (int) ((cursorMax - cursorMin) * pos) + cursorMin;

        this.background = getBackgroundImage(width, height, offset);
        this.tick = getTickImage(tickWidth, height);
        this.cursor = getCursorImage(cursorDim, cursorDim);
    }

    /**
     * Ajoute les gestionnaires d'évènements.
     *
     * @since version 1.01 - version 1.02
     */
    private void addListeners() {
        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                if (isEnabled()) {
                    setMousePosition(event.getX());
                }
                ToolTipManager.sharedInstance().mouseMoved(event);
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                owner.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (isEnabled()) {
                    setMousePosition(event.getX());
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                owner.repaint();
            }
        });

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                showValueLabel(event.getX());
                super.mouseMoved(event);
            }
        });
    }

    /**
     * Change la postion du curseur et modifie la valeur associée en conséquence.
     *
     * @param mousePosition la position du curseur.
     *
     * @since version 1.01
     */
    private void setMousePosition(int mousePosition) {
        cursorX = mousePosition - middleX;
        if (cursorX < cursorMin) {
            cursorX = cursorMin;
        } else if (cursorX > cursorMax) {
            cursorX = cursorMax;
        } else if (mousePosition >= tickX && mousePosition <= tickX + tickWidth) {
            cursorX = (cursorMax + cursorMin) / 2;
        }

        owner.repaint();
        showValueLabel(mousePosition);
    }

    /**
     * Affiche la valeur de la position.
     *
     * @param mousePosition la position du curseur.
     *
     * @since version 1.01
     */
    private void showValueLabel(int mousePosition) {
        int posX = mousePosition - middleX;
        if (posX < cursorMin) {
            posX = cursorMin;
        } else if (posX > cursorMax) {
            posX = cursorMax;
        } else if (mousePosition >= tickX && mousePosition <= tickX + tickWidth) {
            posX = (cursorMax + cursorMin) / 2;
        }
        double value = (double) (posX - cursorMin) / (cursorMax - cursorMin)
                * (max - min) + min;
        this.setToolTipText(String.format("%1$.2f", log ? Math.exp(value) : value));
    }

    /**
     * Indique les labels min et max du slider.
     *
     * @param min le label min.
     * @param max le label max.
     * @param log indique si l'échelle est logarithmique.
     *
     * @since version 1.01
     */
    public void setTickBounds(double min, double max, boolean log) {
        this.min = min;
        this.max = max;
        this.log = log;
        if (log) {
            this.min = Math.log(min);
            this.max = Math.log(max);
        }
    }

    /**
     * Retourne la valeur courante.
     *
     * @return la valeur courante.
     *
     * @since version 1.01
     */
    public double getValue() {
        double value = (double) (cursorX - cursorMin) / (cursorMax - cursorMin) * (max - min) + min;
        return log ? Math.exp(value) : value;
    }

    /**
     * Modifie la valeur courante.
     *
     * @param value la valeur courante.
     *
     * @since version 1.01
     */
    public void setValue(double value) {
        cursorX = (int) ((cursorMax - cursorMin) * ((log ? Math.log(value) : value) - min) / (max - min)) + cursorMin;
        this.repaint();
    }

//    /**
//     * Retourne la position relative du curseur.
//     *
//     * @return la position relative.
//     * @since version 1.01
//     */
//    public double getPosition() {
//        return (double)(cursorX - cursorMin) / (cursorMax - cursorMin);
//    }
//
//    /**
//     * Positionne le curseur à la position relative voulue.
//     *
//     * @param position la position relative.
//     * @since version 1.01
//     */
//    public void setPosition(double position) {
//        cursorX = (int)((cursorMax - cursorMin) * position) + cursorMin;
//        this.repaint();
//    }

    /**
     * Dessine le composant.
     *
     * @param g le graphique.
     *
     * @since version 1.01
     */
    @Override
    public void paintComponent(Graphics g) {
        Rectangle currentBounds = getBounds();
        if (bounds == null || bounds.width != currentBounds.width || bounds.height != currentBounds.height) {
            initComponents(currentBounds.width, currentBounds.height);
            bounds = currentBounds;
        }
        g.drawImage(background, 0, 0, null);
        g.drawImage(tick, tickX, 0, null);
        g.drawImage(cursor, cursorX, cursorY, null);
    }

    /**
     * Retourne l'image du curseur.
     *
     * @param width la largeur du composant.
     * @param height la hauteur du composant.
     *
     * @return l'Image correspondante.
     *
     * @since version 1.01
     */
    private Image getCursorImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Point2D center = new Point2D.Float(width / 2, height / 2);
        Point2D focus = new Point2D.Float(4, 4);
        float radius = 25;
        float[] dist = {0.25f, 0.5f, 1f};
        Color[] colors = {Color.WHITE, Color.DARK_GRAY, Color.BLACK};

        RadialGradientPaint paint = new RadialGradientPaint(center, radius, focus, dist, colors, CycleMethod.NO_CYCLE);

        Area area = new Area(new Ellipse2D.Double(0, 0, width, height));
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        graphics.setPaint(paint);
        graphics.fill(area);

        area.reset();
        graphics.dispose();

        return image;
    }

    /**
     * Retourne l'image de fond pour le slider.
     *
     * @param width la largeur du composant.
     * @param height la hauteur du composant.
     * @param offset la taille de l'ombre.
     *
     * @return l'Image correspondante.
     *
     * @since version 1.01
     */
    private Image getBackgroundImage(int width, int height, int offset) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(0, height);
        float[] dist = {0f, 0.2f, 0.8f, 1f};
        Color color = new Color(50, 128, 168);

        Color[] colors = new Color[]{Color.WHITE.darker(), color, color.darker().darker(), Color.BLACK.darker()};
        LinearGradientPaint paint = new LinearGradientPaint(start, end, dist, colors, CycleMethod.NO_CYCLE);
        Area area = new Area(new RoundRectangle2D.Double(offset, 0, width - offset, height, height, height));
        graphics.setPaint(paint);
        graphics.fill(area);
        area.reset();

        colors = new Color[]{Color.WHITE, color.brighter(), color.darker(), Color.BLACK};
        paint = new LinearGradientPaint(start, end, dist, colors, CycleMethod.NO_CYCLE);
        area = new Area(new RoundRectangle2D.Double(0, 0, width - offset, height, height, height));
        graphics.setPaint(paint);
        graphics.fill(area);
        area.reset();

        graphics.dispose();
        return image;
    }

    /**
     * Retourne l'image de fond pour le slider.
     *
     * @param width la largeur du composant.
     * @param height la hauteur du composant.
     *
     * @return l'Image correspondante.
     *
     * @since version 1.01
     */
    private Image getTickImage(int width, int height) {
        int offset = 1;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        Point2D start = new Point2D.Float(0, 0);
        Point2D end = new Point2D.Float(width, 0);
        float[] dist = {0f, 0.3f, 0.7f, 1f};
        Color color = new Color(50, 128, 168);

        Color[] colors = new Color[]{
                Color.WHITE.darker(), color, color.darker().darker(), Color.BLACK.darker()};
        LinearGradientPaint paint = new LinearGradientPaint(start, end, dist, colors, CycleMethod.NO_CYCLE);
        Area area = new Area(new RoundRectangle2D.Double(0, 0, width - offset, height, width, width));
        graphics.setPaint(paint);
        graphics.fill(area);
        area.reset();

        colors = new Color[]{
                Color.WHITE, color.brighter(), color.darker(), Color.BLACK};
        paint = new LinearGradientPaint(start, end, dist, colors, CycleMethod.NO_CYCLE);
        area = new Area(new RoundRectangle2D.Double(offset, 0, width - offset, height, width, width));
        graphics.setPaint(paint);
        graphics.fill(area);
        area.reset();

        graphics.dispose();
        return image;
    }

//    public static void main(String[] args){
//        javax.swing.JFrame frame = new javax.swing.JFrame("test");
//        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//
//        HorizontalSlider slider = new HorizontalSlider(frame, 400, 18);
//        slider.setTickBounds(0.5, 2, true);
//        slider.setPreferredSize(new java.awt.Dimension(400, 18));
//        slider.setMaximumSize(new java.awt.Dimension(400, 18));
//        frame.getContentPane().add(slider);
//        frame.setSize(500, 100);
//        frame.setVisible(true);
//        slider.setSize(400, 18);
//        frame.pack();
//    }

}
