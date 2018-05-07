/*
 * ClassManager - Supervision de classes et Laboratoire de langue
 * Copyright (C) 2013 Fabrice Alleau <fabrice.alleau@siclic.fr>
 *
 * This file is part of ClassManager.
 *
 * ClassManager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ClassManager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ClassManager.  If not, see <http://www.gnu.org/licenses/>.
 */
package supervision.gui;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JWindow;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import java.awt.image.MemoryImageSource;

import supervision.CommonLogger;

/**
 * Classe bloquant le clavier et la souris.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class BlockWindow extends JWindow {
    private static final long serialVersionUID = 19000L;

    /**
     * Robot controlant la souris et le clavier.
     */
    private Robot robot = null;
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
            CommonLogger.error(e);
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
     * Affiche ou cache la fenêtre. Remplace la fonction
     * <code>setVisible(visible)</code>.
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
