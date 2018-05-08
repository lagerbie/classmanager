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
package labo.gui;

import java.awt.*;

import thot.gui.GuiUtilities;

/**
 * Interfaces graphiques.
 *
 * @author Fabrice alleau
 * @version 1.82
 */
@Deprecated
public class Gui {

    public static final int YES_OPTION = GuiUtilities.YES_OPTION;
    private Window parent;

    /**
     * Initialisation des textes des boîtes de dialogue.
     *
     * @param parent la fenêtre ou sera affiché le composant.
     * @version 1.82
     */
    public Gui(Window parent) {
        this.parent = parent;
    }

    /**
     * Affiche une boîte de dialogue avec une entrée texte.
     *
     * @param message le message à afficher.
     * @param initValue la valeur initiale ({@code null} si pas de valeur).
     * @return le texte qui a été validé ou {@code null} si l'opération a
     * été annulée.
     * @version 1.82
     */
    public String showInputDialog(String message, String initValue) {
        return (String) GuiUtilities.showInputDialog(parent, message, null, initValue);
    }

    /**
     * Affiche une boîte de dialogue avec une liste de choix.
     *
     * @param message le message à afficher.
     * @param title le titre de la fenêtre.
     * @param values les valeurs que l'on peut sélectionnées.
     * @param initialValue la valeur sélectionnée au départ.
     * @return l'Object sélectionnée ou {@code null} si pas de sélection.
     * @version 1.82
     */
    public Object showInputDialog(String message, String title,
            Object[] values, Object initialValue) {
        return GuiUtilities.showInputDialog(parent, message, title, values, initialValue);
    }

    /**
     * Afficge une boîte de dialogue posant une question.
     *
     * @param message le message à afficher.
     * @return {@code JOptionPane.YES_OPTION} si le bouton oui a été cliqué
     * ou {@code JOptionPane.NO_OPTION} si c'est le bouton non.
     * @version 1.82
     */
    public int showOptionDialog(String message) {
        return GuiUtilities.showOptionDialog(parent, message, null, null);
    }
}
