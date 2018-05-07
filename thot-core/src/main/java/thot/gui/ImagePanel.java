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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * Panneau simple pour l'affichage d'une image.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class ImagePanel extends JPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Imge de fond.
     */
    private Image background;

    /**
     * Initialisation du panneau avec une image. Le panneau est redimensionné
     * pour s'ajuster aux dimensions de l'image.
     *
     * @param image l'image à afficher.
     */
    public ImagePanel(Image image) {
        this(image, -1, -1);
    }

    /**
     * Initialisation du panneau avec une image. L'image est redimensionnée aux
     * dimensions indiquées.
     *
     * @param image l'image à afficher.
     * @param width la largeur du panneau.
     * @param height la hauteur du panneau.
     */
    public ImagePanel(Image image, int width, int height) {
        super();
        this.background = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        Dimension dim = new Dimension(background.getWidth(null), background.getHeight(null));
        this.setPreferredSize(dim);
        this.setMinimumSize(dim);
        this.setMaximumSize(dim);
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(background, 0, 0, this);
    }
}
