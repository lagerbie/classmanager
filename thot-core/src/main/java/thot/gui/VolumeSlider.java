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
import javax.swing.JButton;

/**
 * Slider pour le controle de volume.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public abstract class VolumeSlider extends ControlSlider {
    private static final long serialVersionUID = 19000L;

    /**
     * Initialisation de slider.
     *
     * @param owner la frame parente.
     * @param mute le bouton pour le mute.
     * @param backgroundImage le chemin de l'image de fond.
     * @param cursorImage le chemin de l'image du curseur.
     */
    public VolumeSlider(Window owner, JButton mute,
            Image backgroundImage, Image cursorImage) {
        super(owner, mute, backgroundImage, cursorImage);

        int backgroundXoffset = 0;
        int backgroundYoffset = 25;
        setBackgroundOffset(backgroundXoffset, backgroundYoffset);

        int cursorXoffset = 0;
        int cursorYoffset = backgroundYoffset + 6;
        setCursorOffset(cursorXoffset, cursorYoffset);
    }
}
