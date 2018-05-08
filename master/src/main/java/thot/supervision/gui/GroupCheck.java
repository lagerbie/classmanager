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
package thot.supervision.gui;

import java.awt.*;

import javax.swing.*;

/**
 * Composant pour la sélection de groupe. Ce composant sert à visualiser si l'élève est sélectionné (par son groupe)
 * pour les fonctions de groupes.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class GroupCheck extends JPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Image pour indiquer la sélection.
     */
    private Image checkImage;
    /**
     * Etat de la sélection.
     */
    private boolean selected = false;

    /**
     * Intialisation avec l'image de sélection.
     *
     * @param check l'image de sélection.
     */
    public GroupCheck(Image check) {
        super();
        this.checkImage = check;

        Dimension dim = new Dimension(checkImage.getWidth(null), checkImage.getHeight(null));
        this.setPreferredSize(dim);
        this.setMinimumSize(dim);
        this.setMaximumSize(dim);
    }

    /**
     * Indique si il est sélectionné.
     *
     * @return {@code true} si il est sélectionné.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Modifie l'état de sélection.
     *
     * @param selected {@code true} pour sélectionné.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Dessine l'image de sélection si il est sélectionné.
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        if (selected) {
            g.drawImage(checkImage, 0, 0, null);
        }
    }
}
