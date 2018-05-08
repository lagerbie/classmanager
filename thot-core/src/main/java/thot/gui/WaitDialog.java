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

import javax.swing.*;

/**
 * Boîte de dialoque pour attendre.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class WaitDialog extends JDialog {
    private static final long serialVersionUID = 19000L;

    /**
     * Initialise et affiche la boite de dialogue.
     *
     * @param owner la fenêtre parente.
     * @param title le titre de la fenêtre.
     * @param message le message de la fenêtre.
     */
    public WaitDialog(Window owner, String title, String message) {
        super(owner, title);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setBackground(Color.LIGHT_GRAY);
        this.setResizable(false);

        JLabel label = new JLabel(message);
        this.getContentPane().setLayout(new FlowLayout());
        this.getContentPane().add(label);
        this.pack();

        //centrage de la fenêtre
        this.setLocation((owner.getWidth() - this.getWidth()) / 2, (owner.getHeight() - this.getHeight()) / 2);
    }
}
