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

import java.awt.Image;
import java.awt.Window;
import javax.swing.JDialog;

/**
 * Fenêtre pour afficher un A propos de.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class AboutDialog extends JDialog {
    private static final long serialVersionUID = 19000L;

    public AboutDialog(Window owner, String title, Image image) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setResizable(false);

        ImagePanel imagePanel = new ImagePanel(image, 600, -1);

        this.getContentPane().add(imagePanel);
        this.pack();
        this.setLocation((owner.getWidth() - imagePanel.getWidth()) / 2,
                (owner.getHeight() - imagePanel.getHeight()) / 2);

    }
}
