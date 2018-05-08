package thot.supervision.gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;

import javax.swing.*;
import javax.swing.event.PopupMenuListener;

import thot.gui.Resources;

/**
 * @author Fabrice Alleau
 * @version 1.90
 */
public abstract class TabPanel extends JPanel {

    private Window parentWindow;
    private String tabName;
    /**
     * Panneau pour le menu.
     */
    private JPanel tabMenu;

    public TabPanel(Window parent) {
        this.parentWindow = parent;
    }

    public Window getParentWindow() {
        return parentWindow;
    }

    public String getTabName() {
        return tabName;
    }

    public JComponent getTabMenu() {
        return tabMenu;
    }

    public void setTabMenu(JPanel menu) {
        this.tabMenu = menu;
    }

    /**
     * Création d'un bouton avec un état avec son type.
     *
     * @param type le type de bouton
     *
     * @return le bouton créé.
     */
    protected StateButton createButton(String type) {
        StateButton button = new StateButton(parentWindow, type);
        return button;
    }

    abstract public StateButton getButton(String type);

    abstract public void updateLanguage(Resources resources);

    abstract public void updateButtonsFor(StateButton button, boolean hasGroup, boolean isGroupButton);

    abstract public void setGroupFonctionsEnabled(boolean enable);

    abstract public void setButtonActions(ActionListener buttonListener, MouseAdapter menuButtonListener,
            MouseAdapter menuMouseListener, PopupMenuListener popupMenuListener);
}
