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

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.MouseListener;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Thumbnail extends ImagePanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Label pour l'affichage d'un message.
     */
    private static final String MESSAGE = "message";
    /**
     * Label pour l'affichage de la création de groupe.
     */
    private static final String GROUP = "group";
    /**
     * Label pour l'affichage standard.
     */
    private static final String INFO = "info";

    /**
     * Couleur pour l'écriture du nom.
     */
    private final Color textColor = new Color(240, 240, 240);
    /**
     * Taille pour la pastille indiquant le groupe.
     */
    private int pastilleSize = 25;

    /**
     * Panneau principal affichant le groupe, le nom, la sélection.
     */
    private JPanel idendityPanel;
    /**
     * Composant pour le groupe.
     */
    private Pastille groupPastille;
    /**
     * Composant pour la sélection de groupe.
     */
    private GroupCheck groupCheck;
    /**
     * Composant pour le nom.
     */
    private JLabel nameLabel;
    /**
     * Composant pour la batterie.
     */
    private Battery battery;

    /**
     * Panneau principal.
     */
    private JPanel mainPanel;
    /**
     * Layout pour le panneau principal. Permet d'intervetir les différents
     * composants à afficher.
     */
    private CardLayout mainLayout;
    /**
     * Composant pour l'affichage d'information.
     */
    private JLabel infoPanel;
    /**
     * Composant pour l'affichage d'un message.
     */
    private PostIt messagePanel;
    /**
     * Composant pour l'affichage de la création de groupe.
     */
    private GroupCreation groupCreation;

    /**
     * Initilisation avec des images.
     *
     * @param backgroundImage l'image de fond pour la vignette.
     * @param checkImage l'image pour la sélection de groupe.
     * @param batteryImage l'image de la batterie.
     */
    public Thumbnail(Image backgroundImage, Image checkImage, Image batteryImage) {
        super(backgroundImage);
        Dimension dim;

        groupPastille = new Pastille(pastilleSize);
        groupPastille.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        groupCheck = new GroupCheck(checkImage);

        nameLabel = new JLabel();
        nameLabel.setHorizontalAlignment(JLabel.LEFT);
        nameLabel.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        nameLabel.setForeground(textColor);
        dim = new Dimension(backgroundImage.getWidth(null)
                - checkImage.getWidth(null) - pastilleSize - 4, 20);
        nameLabel.setPreferredSize(dim);

        battery = new Battery(batteryImage);
        battery.setBackground(GuiUtilities.TRANSPARENT_COLOR);

        messagePanel = new PostIt(textColor);
        messagePanel.setBackground(GuiUtilities.TRANSPARENT_COLOR);

        groupCreation = new GroupCreation(pastilleSize);
        groupCreation.setBackground(GuiUtilities.TRANSPARENT_COLOR);

        infoPanel = new JLabel();
        infoPanel.setBackground(GuiUtilities.TRANSPARENT_COLOR);

        mainPanel = new JPanel();
        mainPanel.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        mainLayout = new CardLayout();
        mainPanel.setLayout(mainLayout);
        mainPanel.add(groupCreation, GROUP);
        mainPanel.add(messagePanel, MESSAGE);
        mainPanel.add(infoPanel, INFO);
        mainLayout.show(mainPanel, INFO);

        idendityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        idendityPanel.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        dim = new Dimension(backgroundImage.getWidth(null), 35);
        idendityPanel.setMaximumSize(dim);
        idendityPanel.setPreferredSize(dim);
        idendityPanel.add(groupPastille);
        idendityPanel.add(groupCheck);
        idendityPanel.add(nameLabel);

        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(layout);

        this.add(idendityPanel);
        this.add(battery);
        this.add(mainPanel);
    }

    /**
     * Ajout d'un MouseListener pour l'interprétation de la création de groupes.
     *
     * @param listener gestionnaire pour la création de groupes.
     */
    public void addCreationListener(MouseListener listener) {
        groupCreation.addButtonListener(listener);
    }

    /**
     * Retourne le groupe affiché par la vignette.
     *
     * @return le groupe affiché par la vignette.
     * @see Pastille.getGroup()
     */
    public int getGroup() {
        return groupPastille.getGroup();
    }

    /**
     * Modifie le groupe affiché par la vignette.
     *
     * @param group le groupe affiché par la vignette.
     * @see Pastille.setGroup()
     */
    public void setGroup(int group) {
        groupPastille.setGroup(group);
        this.repaint();
    }

    /**
     * Indique si un message est affiché.
     *
     * @return <code>true</code> si un message est affiché.
     */
    public boolean isPostItVisible() {
        return messagePanel.isVisible();
    }

    /**
     * Affiche ou cache un message.
     *
     * @param message le message à afficher
     * @param visible <code>true</code> pour l'afficher, ou <code>false</code>
     * pour revenir à l'affichage standard.
     */
    public void showPostIt(String message, boolean visible) {
        messagePanel.setText(message);
        messagePanel.validate();
        if (visible) {
            mainLayout.show(mainPanel, MESSAGE);
        } else {
            mainLayout.show(mainPanel, INFO);
        }
    }

    /**
     * Affiche ou cache la fenêtre de création de groupes.
     *
     * @param visible <code>true</code> pour l'afficher, ou <code>false</code>
     * pour revenir à l'affichage standard.
     */
    public void showGroupCreation(boolean visible) {
        if (visible) {
            mainLayout.show(mainPanel, GROUP);
        } else {
            mainLayout.show(mainPanel, INFO);
        }
    }

    /**
     * Retourne si la vignette est sélectionnée.
     *
     * @return <code>true</code> si la vignette est sélectionnée.
     */
    public boolean isThumbnailSelected() {
        return !idendityPanel.getBackground().equals(GuiUtilities.TRANSPARENT_COLOR);
    }

    /**
     * Modifie la sélection de la vignette.
     *
     * @param selected <code>true</code> pour la sélectionner, ou
     * <code>false</code> pour la désélectionner.
     */
    public void setThumbnailSelected(boolean selected) {
        if (selected) {
            idendityPanel.setBackground(Color.RED);
        } else {
            idendityPanel.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        }
    }

    /**
     * Indique si le groupe de la vignette est sélectioné.
     *
     * @return <code>true</code> si le groupe de la vignette est sélectionné.
     */
    public boolean isGroupSelected() {
        return groupCheck.isSelected();
    }

    /**
     * Modifie l'état de sélection du groupe.
     *
     * @param selected <code>true</code> pour la sélection de groupe, ou
     * <code>false</code> pour la désélection de groupe.
     */
    public void setGroupSelected(boolean selected) {
        groupCheck.setSelected(selected);
    }

    /**
     * Modifie le nom à afficher.
     *
     * @param name le nouveau nom.
     */
    public void setSudentName(String name) {
        nameLabel.setText(name);
        this.repaint();
    }

    /**
     * Modifie le pourcentage de la batterie.
     *
     * @param percent le nouveau pourcentage.
     */
    public void setBatteryPercent(int percent) {
        battery.setPercent(percent);
        this.repaint();
    }
}
