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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Composant pour afficher l'état de la batterie.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Battery extends JPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Composant pour afficher le pourcentage.
     */
    private JLabel percent;

    /**
     * Initialisation avec l'image de la batterie.
     *
     * @param batteryImage l'image de la batterie.
     */
    public Battery(Image batteryImage) {
        super();
        ImagePanel battery = new ImagePanel(batteryImage);

        percent = new JLabel(String.format("%1$d %%", 100));
        percent.setHorizontalAlignment(JLabel.LEFT);
        percent.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        percent.setForeground(Color.WHITE);

        LayoutManager layout = new FlowLayout(FlowLayout.LEFT, 5, 5);
        this.setLayout(layout);

        this.add(battery);
        this.add(percent);
    }

    /**
     * Modifie le pourcentage affiché.
     *
     * @param percent le nouveau pourcentage.
     */
    public void setPercent(int percent) {
        this.percent.setText(String.format("%1$d %%", percent));
    }
}
