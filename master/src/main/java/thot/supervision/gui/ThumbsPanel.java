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
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import thot.gui.GuiUtilities;

/**
 * Panneau avec scrollbar d'affichage des vignettes de supervision.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class ThumbsPanel extends JScrollPane {
    private static final long serialVersionUID = 19000L;

    /**
     * Nombre de ligne de vignette dans le panneau.
     */
    private int nbLine = 4;
    /**
     * Nombre de vignette par ligne.
     */
    private int nbPerLine = 6;
    /**
     * Nombre de vignette dans le panneau.
     */
    private int thumbCount = 0;
    /**
     * Liste des panneaux en lignes.
     */
    private List<JPanel> panels;
    /**
     * Dimension des panneaux en ligne.
     */
    private Dimension panelDim;
    /**
     * Panneau principal.
     */
    private JPanel mainPanel;

    /**
     * Initialistaion avec la taille du panneau principal et la hauteur d'une vignette.
     *
     * @param width largeur du panneau principal.
     * @param height hauteur du panneau principal.
     * @param thumbHeight hauteur d'une vignette.
     */
    public ThumbsPanel(int width, int height, int thumbHeight) {
        super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);
        setBorder(null);
        setBackground(GuiUtilities.TRANSPARENT_COLOR);

        panels = new ArrayList<>(nbLine);
        panelDim = new Dimension(width, thumbHeight);

        mainPanel = new JPanel();
        BoxLayout layout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
        mainPanel.setLayout(layout);
        mainPanel.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        setViewportView(mainPanel);

        for (int i = 0; i < nbLine; i++) {
            addPanel();
        }

        Dimension dim = new Dimension(width, height);
        this.setMaximumSize(dim);
        this.setPreferredSize(dim);
    }

    /**
     * Ajoute une vignette. Ajoute un panneau en forme de ligne si nécessaire.
     *
     * @param thumbnail la vignette à ajouter.
     */
    public void addThumb(Thumbnail thumbnail) {
        int line = thumbCount / nbPerLine;

        if (line == nbLine) {
            addPanel();
            nbLine++;
            this.getParent().validate();
        }
        thumbCount++;

        panels.get(line).add(thumbnail);
        this.validate();
    }

    /**
     * Ajoute un panneau en forme de ligne.
     */
    private void addPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        panel.setMaximumSize(panelDim);
        panel.setPreferredSize(panelDim);
        panels.add(panel);
        mainPanel.add(panel);
    }
}
