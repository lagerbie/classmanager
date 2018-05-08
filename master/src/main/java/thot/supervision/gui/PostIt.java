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
 * Composant affichant un message.
 *
 * @version 1.8.4
 */
public class PostIt extends JPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Composant pour l'affichage du message.
     */
    private JLabel messageLabel;

    /**
     * Initialisation.
     *
     * @param textColor la couleur du texte à afficher.
     */
    public PostIt(Color textColor) {
        super();

        messageLabel = new JLabel();
        messageLabel.setForeground(textColor);
        this.add(messageLabel);
    }

    /**
     * Modifie le message affiché.
     *
     * @param message le nouveau message.
     */
    public void setText(String message) {
        messageLabel.setText(message);
    }
}
