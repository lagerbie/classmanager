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

import javax.swing.JMenuItem;

/**
 *
 * Bouton pour les popmenu.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class MenuButton extends JMenuItem {
    private static final long serialVersionUID = 19000L;

    /**
     * l'identifiant pour les ressources.
     */
    private String type;
    /**
     * le bouton affichant le menu où est ce bouton.
     */
    private StateButton parent;

    /**
     * Initialisation du bouton de menu.
     *
     * @param type l'identifiant pour les ressources.
     * @param parent le bouton affichant le menu où est ce bouton.
     * @param text le texte affiché sur l'item.
     */
    public MenuButton(String type, StateButton parent, String text) {
        super(text);
        this.type = type;
        this.parent = parent;
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
     * Retourne le bouton affichant le menu où est ce bouton.
     *
     * @return le bouton affichant le menu où est ce bouton.
     */
    public StateButton getParentButton() {
        return parent;
    }
}
