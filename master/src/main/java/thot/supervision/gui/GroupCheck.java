package thot.supervision.gui;

import java.awt.*;

import javax.swing.*;

/**
 * Composant pour la sélection de groupe. Ce composant sert à visualiser si l'élève est sélectionné (par son groupe)
 * pour les fonctions de groupes.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class GroupCheck extends JPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Image pour indiquer la sélection.
     */
    private Image checkImage;
    /**
     * Etat de la sélection.
     */
    private boolean selected = false;

    /**
     * Intialisation avec l'image de sélection.
     *
     * @param check l'image de sélection.
     */
    public GroupCheck(Image check) {
        super();
        this.checkImage = check;

        Dimension dim = new Dimension(checkImage.getWidth(null), checkImage.getHeight(null));
        this.setPreferredSize(dim);
        this.setMinimumSize(dim);
        this.setMaximumSize(dim);
    }

    /**
     * Indique si il est sélectionné.
     *
     * @return {@code true} si il est sélectionné.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Modifie l'état de sélection.
     *
     * @param selected {@code true} pour sélectionné.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Dessine l'image de sélection si il est sélectionné.
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        if (selected) {
            g.drawImage(checkImage, 0, 0, null);
        }
    }
}
