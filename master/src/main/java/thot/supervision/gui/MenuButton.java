package thot.supervision.gui;

import javax.swing.*;

/**
 * Bouton pour les popmenu.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
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
