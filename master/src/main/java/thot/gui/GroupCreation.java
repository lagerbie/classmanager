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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

import thot.model.Constants;

/**
 * Panneau pour afficher les différents groupes lors de la création de groupes.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class GroupCreation extends JPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Composant pour le groupe A.
     */
    private Pastille groupA;
    /**
     * Composant pour le groupe B.
     */
    private Pastille groupB;
    /**
     * Composant pour le groupe C.
     */
    private Pastille groupC;
    /**
     * Composant pour le groupe D.
     */
    private Pastille groupD;
    /**
     * Composant pour le groupe E.
     */
    private Pastille groupE;
    /**
     * Composant pour le groupe F.
     */
    private Pastille groupF;
    /**
     * Composant pour le groupe G.
     */
    private Pastille groupG;
    /**
     * Composant pour le groupe H.
     */
    private Pastille groupH;

    /**
     * Initialisation avec la taille des composants groupes.
     *
     * @param pastilleSize la taille des composants groupes.
     */
    public GroupCreation(int pastilleSize) {
        super();
        this.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        this.setPreferredSize(new Dimension(150, 100));

        groupA = new Pastille(pastilleSize, Constants.GROUP_A);
        groupB = new Pastille(pastilleSize, Constants.GROUP_B);
        groupC = new Pastille(pastilleSize, Constants.GROUP_C);
        groupD = new Pastille(pastilleSize, Constants.GROUP_D);
        groupE = new Pastille(pastilleSize, Constants.GROUP_E);
        groupF = new Pastille(pastilleSize, Constants.GROUP_F);
        groupG = new Pastille(pastilleSize, Constants.GROUP_G);
        groupH = new Pastille(pastilleSize, Constants.GROUP_H);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(gridbag);

        c.ipadx = 5;
        c.ipady = 5;
        c.gridwidth = 1;
        gridbag.setConstraints(groupA, c);
        this.add(groupA);
        gridbag.setConstraints(groupB, c);
        this.add(groupB);
        gridbag.setConstraints(groupC, c);
        this.add(groupC);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(groupD, c);
        this.add(groupD);

        c.gridwidth = 1;
        gridbag.setConstraints(groupE, c);
        this.add(groupE);
        gridbag.setConstraints(groupF, c);
        this.add(groupF);
        gridbag.setConstraints(groupG, c);
        this.add(groupG);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(groupH, c);
        this.add(groupH);
    }

    /**
     * Ajoute le même gestionnaire d'évènements aux différents composants.
     *
     * @param listener le gestionnaire d'évènements.
     */
    public void addButtonListener(MouseListener listener) {
        groupA.addMouseListener(listener);
        groupB.addMouseListener(listener);
        groupC.addMouseListener(listener);
        groupD.addMouseListener(listener);
        groupE.addMouseListener(listener);
        groupF.addMouseListener(listener);
        groupG.addMouseListener(listener);
        groupH.addMouseListener(listener);
    }
}
