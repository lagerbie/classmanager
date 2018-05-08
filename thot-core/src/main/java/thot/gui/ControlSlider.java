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
package thot.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

/**
 * Slider général pour le controle de volume et de vitesse.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public abstract class ControlSlider extends JPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Fenêtre parente.
     */
    private Window owner;

    /**
     * Position minimale.
     */
    private int min;
    /**
     * Position maximale.
     */
    private int max;
    /**
     * Position du curseur.
     */
    private int position;
    /**
     * Imge de fond.
     */
    private Image background;
    /**
     * Imge du curseur.
     */
    private Image cursor;
    /**
     * Bouton pour couper le son.
     */
    private JButton mute;
    /**
     * Offset horizontal pour l'image de fond.
     */
    private int backgroundXoffset = 0;
    /**
     * Offset vertical pour l'image de fond.
     */
    private int backgroundYoffset = 0;
    /**
     * Offset horizontal pour l'image du curseur.
     */
    private int cursorXoffset = 0;
    /**
     * Offset vertical pour l'image du curseur.
     */
    private int cursorYoffset = 0;

    /**
     * Initialisation du slider pour un volume.
     *
     * @param owner la frame parente.
     * @param mute le bouton pour le mute.
     * @param backgroundImage l'image de fond du slider.
     * @param cursorImage l'image du curseur.
     */
    public ControlSlider(final Window owner, final JButton mute, Image backgroundImage, Image cursorImage) {
        this.owner = owner;
        this.mute = mute;

        this.background = backgroundImage;
        this.cursor = cursorImage;

        min = cursor.getWidth(null) / 2 + 2;
        max = background.getWidth(null) - cursor.getWidth(null) / 2 - 2;

        this.setAlignmentX(JPanel.RIGHT_ALIGNMENT);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setPreferredSize(new Dimension(background.getWidth(null), cursor.getHeight(null)));

        this.setPreferredSize(new Dimension(background.getWidth(null), 69));

        this.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                if (isEnabled()) {
                    setPosition(event.getX());
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isEnabled()) {
                    owner.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (isEnabled()) {
                    setPosition(event.getX());
                }
            }

            @Override
            public void mouseExited(MouseEvent event) {
                owner.repaint();
            }
        });

        //anonymous listeners pour le controle du volume
        this.mute.addActionListener(event -> setMute());
    }

    /**
     * Modifie l'offset de l'image de fond du slider.
     *
     * @param x la postion horizontale.
     * @param y la postion verticale.
     */
    public void setBackgroundOffset(int x, int y) {
        backgroundXoffset = x;
        backgroundYoffset = y;
    }

    /**
     * Modifie l'offset de l'image de curseur.
     *
     * @param x la postion horizontale.
     * @param y la postion verticale.
     */
    public void setCursorOffset(int x, int y) {
        cursorXoffset = x;
        cursorYoffset = y;
    }

    /**
     * Modifie l'état du mute.
     */
    private void setMute() {
        toggleMute();
        this.repaint();
    }

    /**
     * Change la postion du curseur et modifie la valeur associése en conséquence.
     *
     * @param mousePosition la position du curseur.
     */
    private void setPosition(int mousePosition) {
        this.position = mousePosition;
        if (position < min) {
            position = min;
        }
        if (position > max) {
            position = max;
        }

        owner.repaint();

        double value = (double) (position - min) / (max - min);
        setValue(value);
    }

    /**
     * Positionne le curseur à la position relative voulue.
     *
     * @param position la position relative.
     */
    public void setPosition(double position) {
        this.position = (int) ((max - min) * position) + min;
        this.repaint();
    }

    /**
     * Change la valeur par la position relative.
     *
     * @param position la position relative.
     */
    abstract public void setValue(double position);

    /**
     * Modifie l'état du son coupé ou non attribué au controle.
     */
    abstract public void toggleMute();

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mute.setEnabled(enabled);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(background, backgroundXoffset, backgroundYoffset, null);
        if (position > min) {
            g.drawImage(cursor, cursorXoffset + position - cursor.getWidth(null) / 2, cursorYoffset, null);
        }
    }
}
