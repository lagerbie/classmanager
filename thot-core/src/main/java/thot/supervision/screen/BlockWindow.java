package thot.supervision.screen;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.MemoryImageSource;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe bloquant le clavier et la souris.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class BlockWindow extends JWindow {
    private static final long serialVersionUID = 19000L;

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockWindow.class);

    /**
     * Robot controlant la souris et le clavier.
     */
    private Robot robot;
    /**
     * Frame pour capturer le clavier (JWindow ne peut pas avoir le focus !!!!).
     */
    private JDialog frame;
    /**
     * Position horizontale de la souris.
     */
    private int x = 100;
    /**
     * Position verticale de la souris.
     */
    private int y = 100;

    /**
     * Initialisation.
     */
    public BlockWindow() {
        super();
        this.setAlwaysOnTop(true);
        this.setFocusable(true);
        hideCursor();

        try {
            robot = new Robot();
        } catch (AWTException e) {
            LOGGER.error("", e);
            System.exit(-1);
        }

        frame = new JDialog();
        frame.setUndecorated(true);
        frame.pack();
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                e.consume();//consomme l'évènement
                //System.out.println("Key Pressed: " + e.getKeyChar());
                int code = e.getKeyCode();
                //la touche ALT n'est pas consommé sur Linux
                if (code == KeyEvent.VK_ALT) {
                    robot.keyRelease(code);
                }
            }
        };

        frame.addKeyListener(keyAdapter);
        frame.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (frame.isVisible()) {
                    forceVisible();
                }
            }
        });

        this.addKeyListener(keyAdapter);
        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                robot.mouseMove(x, y);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                robot.mouseMove(x, y);
            }
        });
    }

    /**
     * Affiche ou cache la fenêtre. Remplace la fonction {@code setVisible(visible)}.
     *
     * @param visible la visibilté de la fenêtre.
     */
    public void showWindow(boolean visible) {
        if (visible) {
            forceVisible();
        } else {
            //initialise l'affichage pour que la frame est le focus du clavier.
            if (!this.isVisible()) {
                this.setVisible(true);
            }

            frame.setVisible(false);
            this.setVisible(false);
        }
    }

    /**
     * Force la fenêtre à être au dessus et d'avoir les focus clavier-souris.
     */
    private void forceVisible() {
        frame.setVisible(true);
        frame.requestFocus();

        this.setVisible(true);
        this.requestFocus();
        this.toFront();
        this.repaint();
    }

    /**
     * Cache le curseur de la souris.
     */
    private void hideCursor() {
        int[] pixels = new int[16 * 16];
        Image imageCursor = Toolkit.getDefaultToolkit()
                .createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
        Cursor transparentCursor = Toolkit.getDefaultToolkit()
                .createCustomCursor(imageCursor, new Point(0, 0), "invisibleCursor");
        this.setCursor(transparentCursor);
    }
}
