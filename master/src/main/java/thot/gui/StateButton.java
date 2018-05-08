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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

/**
 * Bouton affichant des images avec deux états sélectionnables différents.
 *
 * Le type sert à la fois pour la recherche des aides des info-bulles et la
 * recherche des images.
 *
 * @author Fabrice Alleau
 * @version 1.91
 */
public class StateButton extends JButton {
    private static final long serialVersionUID = 19100L;

    /**
     * Identifiant du type du bouton. Sert pour les images.
     */
    private String type;
    /**
     * Etat du bouton (normal ou enfoncé).
     */
    private boolean on = true;
    /**
     * Menu optionnel associé au bouton.
     */
    private JMenu menu;

    /**
     * Initialisation.
     *
     * @param parent l'élément graphiqque parent (pour les repaint).
     * @param type l'identifiant pour les ressources.
     */
    public StateButton(final Component parent, String type) {
        super(GuiUtilities.getImageIcon(type + "Image", GuiConstants.imagesPath, GuiConstants.images));
        this.type = type;

        this.setMargin(new Insets(0, 0, 0, 0));
        this.setBorderPainted(false);
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);

        //anonymous listeners pour rafraichir la frame lors d'afichage des toolTip
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                parent.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                parent.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                parent.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                parent.repaint();
            }
        });

        this.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                parent.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                parent.repaint();
            }
        });

        //pour éviter d'avoir un reste d'image quand le programme désactive le bouton
        this.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                parent.repaint();
            }
        });
    }

    /**
     * Modifie l'état du bouton.
     *
     * @param on {@code true} si le bouton n'est pas enfoncé, ou
     * {@code false} si le bouton est enfoncé.
     */
    public void setOn(boolean on) {
        this.on = on;
        String image;
        if (on) {
            image = type.concat("Image");
        } else {
            image = type.concat("Image").concat("Off");
        }
        this.setIcon(GuiUtilities.getImageIcon(image, GuiConstants.imagesPath, GuiConstants.images));
    }

    /**
     * Retourne l'état du bouton.
     *
     * @return {@code true} si le bouton n'est pas enclenché, ou
     * {@code false} si le bouton est enclenché.
     */
    public boolean isOn() {
        return on;
    }

    /**
     * Retourne l'identifiant pour les ressources.
     *
     * @return l'identifiant pour les ressources.
     */
    public String getType() {
        return type;
    }

    /**
     * Inverse l'état du bouton.
     * Equivalant à {@code setOn(!isOn())}.
     */
    public void toggle() {
        setOn(!on);
    }

    /**
     * Ajoute un menu associé au bouton.
     *
     * @param menu le menu associé.
     */
    public void setMenu(JMenu menu) {
        this.menu = menu;
    }

    /**
     * Retourne le menu optionnel associé au bouton.
     *
     * @return le menu associé.
     */
    public JPopupMenu getPopupMenu() {
        return menu.getPopupMenu();
    }
}