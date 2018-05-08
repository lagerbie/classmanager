package thot.gui;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Canvas pour afficher la vidéo ou des images.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class VideoCanvas extends Canvas {
    private static final long serialVersionUID = 19000L;

    /**
     * Image par défaut dans la fenêtre vidéo.
     */
    private Image defaultImage = null;
//    /** Image par défaut dans la fenêtre vidéo */
//    private Image defaultFullScreenImage = null;
    /**
     * Image recadrée dans la fenêtre vidéo.
     */
    private Image scaledImage = null;
    /**
     * Image pour le mode plein écran.
     */
    private Image fullScreenImage = null;
    /**
     * Soustitre à afficher.
     */
    private String subtitle = null;
    /**
     * Fenêtre pour le mode plein écran.
     */
    private Window fullScreen;
    /**
     * Frame pour capturer le clavier (JWindow ne peut pas avoir le focus !!!!).
     */
    private JDialog keyFrame;

    /**
     * Initialisation qui permet de passer en plein écran avec une image.
     */
    public VideoCanvas() {
        super();

        fullScreen = new Window(null) {
            private static final long serialVersionUID = 54L;

            @Override
            public void paint(Graphics g) {
                if (fullScreenImage != null) {
                    g.fillRect(0, 0, getWidth(), getHeight());
                    int xOffset = (getWidth() - fullScreenImage.getWidth(null)) / 2;
                    int yOffset = (getHeight() - fullScreenImage.getHeight(null)) / 2;
                    g.drawImage(fullScreenImage, xOffset, yOffset, this);
                }
//                else {
//                    g.fillRect(0, 0, getWidth(), getHeight());
//                    int xOffset = (getWidth() - defaultFullScreenImage.getWidth(null)) / 2;
//                    int yOffset = (getHeight() - defaultFullScreenImage.getHeight(null)) / 2;
//                    g.drawImage(defaultFullScreenImage, xOffset, yOffset, this);
//                }

                if (subtitle != null) {
                    paintSubtitle(g, getWidth(), getHeight());
                }
            }
        };
        fullScreen.setIconImages(GuiUtilities.getIcons());
        fullScreen.setSize(getToolkit().getScreenSize());

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
//                System.out.println("canvas click");
                if (event.getClickCount() > 1) {
                    setFullScreen(true);
                }
            }
        });

        fullScreen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    setFullScreen(false);
                }
            }
        });

        keyFrame = new JDialog();
        keyFrame.setUndecorated(true);
        keyFrame.pack();

        keyFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
//                System.out.println("Key Pressed: " + e.getKeyChar());
                int code = e.getKeyCode();
                //la touche ALT n'est pas consommé sur Linux
                if (code == KeyEvent.VK_ESCAPE) {
                    setFullScreen(false);
                }
            }
        });

        keyFrame.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (keyFrame.isVisible()) {
                    forceVisible();
                }
            }
        });
    }

    /**
     * Force la fenêtre à être au dessus et d'avoir les focus clavier-souris.
     */
    private void forceVisible() {
        keyFrame.setVisible(true);
        keyFrame.requestFocus();
    }

    /**
     * Notification du changement du mode écran.
     *
     * @param fullscreen l'état du mode plein écran.
     */
    protected void fireFullScreenStateChanged(boolean fullscreen) {
    }

    /**
     * Permet de passer l'image en plein écran.
     *
     * @param full {@code true} pour le plein écran et {@code false} pour revenir en taille normale.
     */
    public void setFullScreen(boolean full) {
        fireFullScreenStateChanged(full);
        if (scaledImage != null) {// || defaultImage != null) {
            this.setVisible(!full);
            fullScreen.setVisible(full);
        } else {
            if (full) {
                forceVisible();
            } else {
                keyFrame.setVisible(false);
            }
        }
    }

    /**
     * Change l'image par défaut.
     *
     * @param image l'image par défaut.
     */
    public void setDefaultImage(Image image) {
        if (image == null) {
            defaultImage = null;
//            defaultFullScreenImage = null;
        } else {
            double width = (double) image.getWidth(null) / getWidth();
            double height = (double) image.getHeight(null) / getHeight();

            if (width > height) {
                defaultImage = image.getScaledInstance(
                        getWidth(), -1, Image.SCALE_DEFAULT);
            } else {
                defaultImage = image.getScaledInstance(
                        -1, getHeight(), Image.SCALE_DEFAULT);
            }

//            width = (double) image.getWidth(null) / fullScreen.getWidth();
//            height = (double) image.getHeight(null) / fullScreen.getHeight();

//            if(width > height)
//                defaultFullScreenImage = image.getScaledInstance(fullScreen.getWidth(), -1, Image.SCALE_DEFAULT);
//            else
//                defaultFullScreenImage = image.getScaledInstance(-1, fullScreen.getHeight(), Image.SCALE_DEFAULT);
        }
        this.repaint();
    }

    /**
     * Permet de mettre une image à la place d'une vidéo.
     *
     * @param image l'image.
     */
    public void setImage(Image image) {
        if (image == null) {
            scaledImage = null;
            fullScreenImage = null;
        } else {
            double width = (double) image.getWidth(this) / getWidth();
            double height = (double) image.getHeight(this) / getHeight();

            if (width > height) {
                scaledImage = image.getScaledInstance(getWidth(), -1, Image.SCALE_DEFAULT);
            } else {
                scaledImage = image.getScaledInstance(-1, getHeight(), Image.SCALE_DEFAULT);
            }

            width = (double) image.getWidth(this) / fullScreen.getWidth();
            height = (double) image.getHeight(this) / fullScreen.getHeight();

            if (width > height) {
                fullScreenImage = image.getScaledInstance(fullScreen.getWidth(), -1, Image.SCALE_DEFAULT);
            } else {
                fullScreenImage = image.getScaledInstance(-1, fullScreen.getHeight(), Image.SCALE_DEFAULT);
            }
        }
        this.repaint();
    }

    /**
     * Modifie le sous-titre à afficher.
     *
     * @param subtitle le sous-titre.
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (scaledImage != null) {
            int xOffset = (getWidth() - scaledImage.getWidth(null)) / 2;
            int yOffset = (getHeight() - scaledImage.getHeight(null)) / 2;
            g.drawImage(scaledImage, xOffset, yOffset, this);
        } else if (defaultImage != null) {
            int xOffset = (getWidth() - defaultImage.getWidth(null)) / 2;
            int yOffset = (getHeight() - defaultImage.getHeight(null)) / 2;
            g.drawImage(defaultImage, xOffset, yOffset, this);
        }

        if (subtitle != null) {
            paintSubtitle(g, getWidth(), getHeight());
        }

        if (fullScreen.isVisible()) {
            fullScreen.repaint();
        }
    }

    /**
     * Dessine le sous-titre sur le graphique avec la largeur et la hauteur de la fenêtre de dessin.
     *
     * @param g le graphique où l'on dessine.
     * @param witdh la largeur du graphique.
     * @param height la hauteur du graphique.
     */
    private void paintSubtitle(Graphics g, int witdh, int height) {
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 24));
        g.setColor(Color.WHITE);

        FontMetrics fontMetrics = g.getFontMetrics();
        int length = fontMetrics.stringWidth(subtitle);

        //si la longueur du sous-titre est inférieure à la taille de la vidéo
        if (length < getWidth()) {
            int xOffset = (witdh - length) / 2;
            int yOffset = height - 2 * fontMetrics.getHeight();
            g.drawString(subtitle, xOffset, yOffset);
        } else {
            //découpage du sous-titre en plusieurs lignes
            String[] words = subtitle.split(" ");
            ArrayList<String> lines = new ArrayList<>(2);

            StringBuilder buffer = new StringBuilder(32);
            for (String word : words) {
                if (fontMetrics.stringWidth(buffer.toString() + word + " ") < witdh) {
                    buffer.append(word).append(" ");
                } else {
                    lines.add(buffer.toString());
                    buffer = new StringBuilder(word + " ");
                }
            }
            lines.add(buffer.toString());

            //affichage des lignes
            int nbLines = lines.size();
            for (int i = 0; i < nbLines; i++) {
                String line = lines.get(i);
                length = fontMetrics.stringWidth(line);
                int xOffset = (witdh - length) / 2;
                int yOffset = height - (nbLines - i) * fontMetrics.getHeight();
                g.drawString(line, xOffset, yOffset);
            }
        }
    }
}
