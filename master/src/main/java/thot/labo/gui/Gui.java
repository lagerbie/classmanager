package thot.labo.gui;

import java.awt.*;

import thot.gui.GuiUtilities;

/**
 * Interfaces graphiques.
 *
 * @author Fabrice alleau
 * @version 1.8;4
 */
@Deprecated
public class Gui {

    public static final int YES_OPTION = GuiUtilities.YES_OPTION;
    private Window parent;

    /**
     * Initialisation des textes des boîtes de dialogue.
     *
     * @param parent la fenêtre ou sera affiché le composant.
     */
    public Gui(Window parent) {
        this.parent = parent;
    }

    /**
     * Affiche une boîte de dialogue avec une entrée texte.
     *
     * @param message le message à afficher.
     * @param initValue la valeur initiale ({@code null} si pas de valeur).
     *
     * @return le texte qui a été validé ou {@code null} si l'opération a été annulée.
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
     *
     * @return l'Object sélectionnée ou {@code null} si pas de sélection.
     */
    public Object showInputDialog(String message, String title, Object[] values, Object initialValue) {
        return GuiUtilities.showInputDialog(parent, message, title, values, initialValue);
    }

    /**
     * Afficge une boîte de dialogue posant une question.
     *
     * @param message le message à afficher.
     *
     * @return {@code JOptionPane.YES_OPTION} si le bouton oui a été cliqué ou {@code JOptionPane.NO_OPTION} si c'est le
     *         bouton non.
     */
    public int showOptionDialog(String message) {
        return GuiUtilities.showOptionDialog(parent, message, null, null);
    }
}
