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

import thot.utils.Constants;

/**
 * Composant pour afficher le groupe.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class Pastille extends JPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Couleur de fond pour le groupe A.
     */
    public final static Color GROUP_A = new Color(68, 102, 176);
    /**
     * Couleur de fond pour le groupe B.
     */
    public final static Color GROUP_B = new Color(203, 25, 109);
    /**
     * Couleur de fond pour le groupe C.
     */
    public final static Color GROUP_C = new Color(71, 160, 148);
    /**
     * Couleur de fond pour le groupe D.
     */
    public final static Color GROUP_D = new Color(67, 187, 221);
    /**
     * Couleur de fond pour le groupe E.
     */
    public final static Color GROUP_E = new Color(206, 122, 24);
    /**
     * Couleur de fond pour le groupe F.
     */
    public final static Color GROUP_F = new Color(119, 120, 115);
    /**
     * Couleur de fond pour le groupe G.
     */
    public final static Color GROUP_G = new Color(219, 122, 173);
    /**
     * Couleur de fond pour le groupe H.
     */
    public final static Color GROUP_H = new Color(122, 78, 33);

    /**
     * Taille du composant.
     */
    private int size;
    /**
     * Couleur de fond.
     */
    private Color color;
    /**
     * Composant pour afficher le label du groupe.
     */
    private JLabel groupLabel;
    /**
     * Idendifiant du groupe.
     */
    private int group;

    /**
     * Initialisation de la pastille. Equivalant à Pastille(size, Constants.GROUP_A)
     *
     * @param size la taille du composant.
     */
    public Pastille(int size) {
        this(size, Constants.GROUP_A);
    }

    /**
     * Initialisation de la pastille.
     *
     * @param size la taille du composant.
     * @param group le groupe d'appartenance.
     */
    public Pastille(int size, int group) {
        super();
        this.size = size;
        groupLabel = new JLabel();
        groupLabel.setHorizontalAlignment(JLabel.LEFT);
        groupLabel.setAlignmentX(0.0f);
        setGroup(group);

        this.add(groupLabel);

        Dimension dim = new Dimension(size + 2, size + 2);
        this.setPreferredSize(dim);
        this.setMinimumSize(dim);
        this.setMaximumSize(dim);
    }

    /**
     * Indique le groupe affiché.
     *
     * @return le groupe affiché.
     */
    public int getGroup() {
        return group;
    }

    /**
     * Indique le label de groupe affiché.
     *
     * @return le label de groupe affiché, soit "A", "B", ...
     */
    public String getGroupLabel() {
        return groupLabel.getText();
    }

    /**
     * Modifie le groupe affiché.
     *
     * @param group le groupe affiché, il doit être de la forme suivante: supervision.Constants.GROUP_A,
     *         supervision.Constants.GROUP_B, ...
     */
    public void setGroup(int group) {
        this.group = group;
        String label;
        switch (group) {
            case Constants.GROUP_A:
                color = GROUP_A;
                label = "A";
                break;
            case Constants.GROUP_B:
                color = GROUP_B;
                label = "B";
                break;
            case Constants.GROUP_C:
                color = GROUP_C;
                label = "C";
                break;
            case Constants.GROUP_D:
                color = GROUP_D;
                label = "D";
                break;
            case Constants.GROUP_E:
                color = GROUP_E;
                label = "E";
                break;
            case Constants.GROUP_F:
                color = GROUP_F;
                label = "F";
                break;
            case Constants.GROUP_G:
                color = GROUP_G;
                label = "G";
                break;
            case Constants.GROUP_H:
                color = GROUP_H;
                label = "H";
                break;
            default:
                color = GROUP_A;
                label = "A";
        }
        groupLabel.setText(label);
    }

    /**
     * Dessine un cercle avec la couleur du groupe.
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(color);
        g.fillOval(0, 0, size, size);
    }
}
