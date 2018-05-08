package thot.gui;

import java.awt.*;

import javax.swing.*;

/**
 * Slider pour le controle de volume.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
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
    public VolumeSlider(Window owner, JButton mute, Image backgroundImage, Image cursorImage) {
        super(owner, mute, backgroundImage, cursorImage);

        int backgroundXoffset = 0;
        int backgroundYoffset = 25;
        setBackgroundOffset(backgroundXoffset, backgroundYoffset);

        int cursorXoffset = 0;
        int cursorYoffset = backgroundYoffset + 6;
        setCursorOffset(cursorXoffset, cursorYoffset);
    }
}
