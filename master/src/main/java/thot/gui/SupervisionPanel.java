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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author fabrice
 */
public class SupervisionPanel extends TabPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Bouton pour l'écoute discrète.
     */
    private StateButton listeningButton;
    /**
     * Bouton pour la prise en main.
     */
    private StateButton studentControlButton;
    /**
     * Bouton pour la scrutation automatique.
     */
    private StateButton scanningButton;
    /**
     * Bouton pour la mosaïque.
     */
    private StateButton mosaiqueButton;

    /**
     * Bouton pour l'envoi de fichiers.
     */
    private StateButton sendFileButton;
    /**
     * Bouton pour l'envoi de l'écran noir.
     */
    private StateButton blackScreenButton;
    /**
     * Bouton pour l'envoi de l'écran professeur.
     */
    private StateButton masterScreenButton;
    /**
     * Bouton pour l'envoi de l'écran d'un élève.
     */
    private StateButton studentScreenButton;
    /**
     * Bouton pour l'envoi d'un message texte.
     */
    private StateButton sendTextButton;

    /**
     * Bouton pour le pairing.
     */
    private StateButton pairingButton;
    /**
     * Bouton pour la validation du pairing.
     */
    private StateButton pairingValidButton;

    /**
     * Bouton pour la création de groupe.
     */
    private StateButton groupCreationButton;
    /**
     * Bouton pour la fermeture des élèves.
     */
    private StateButton studentCloseButton;

    /**
     * Menu de choix pour l'envoi d'écran professeur.
     */
    private JMenu masterScreenMenu;
    /**
     * Bouton pour l'envoi de l'écran et de la voix.
     */
    private MenuButton masterScreenVoiceButton;
    /**
     * Bouton pour l'envoi de la voix seule.
     */
    private MenuButton masterVoiceButton;

    /**
     * Menu de choix pour la fermeture sur les élèves.
     */
    private JMenu studentCloseMenu;
    /**
     * Bouton pour éteindre les ordinateurs élèves.
     */
    private MenuButton computerPowerButton;
    /**
     * Bouton pour fermer la session des ordinateurs élèves.
     */
    private MenuButton osSessionButton;
    /**
     * Bouton pour réinitialiser les logins élèves.
     */
    private MenuButton loginSessionButton;

    /**
     * Panneau pour le monitoring.
     */
    private FilterPanel monitoringMenu;
    /**
     * Panneau pour la diffusion.
     */
    private FilterPanel diffusionMenu;
    /**
     * Panneau pour le pairing.
     */
    private FilterPanel pairingMenu;
    /**
     * Panneau pour la gestion.
     */
    private FilterPanel gestionMenu;

    public SupervisionPanel(Window parent,
            int width, int height, int menuWidth, int menuHeight,
            int buttonSize, int margin, Resources resources) {
        super(parent);
        initMenu(menuWidth, menuHeight, buttonSize, margin, resources);
        initPopupMenu(resources);
        //Initialisation de l'état des boutons
        pairingValidButton.setVisible(false);

        Dimension dim = new Dimension(width, height);
        this.setMaximumSize(dim);
        this.setPreferredSize(dim);
    }

    private void initMenu(int menuWidth, int menuHeight, int buttonSize, int margin,
            Resources resources) {
        Dimension dim;
        //Boutons pour le suivi
        listeningButton = createButton(GuiConstants.listening);
        studentControlButton = createButton(GuiConstants.studentControl);
        scanningButton = createButton(GuiConstants.scanning);
        mosaiqueButton = createButton(GuiConstants.mosaique);

        //Boutons pour la diffusion
        sendFileButton = createButton(GuiConstants.sendFile);
        blackScreenButton = createButton(GuiConstants.blackScreen);
        masterScreenButton = createButton(GuiConstants.masterScreen);
        studentScreenButton = createButton(GuiConstants.studentScreen);
        sendTextButton = createButton(GuiConstants.sendMessage);

        //Boutons pour le pairing
        pairingButton = createButton(GuiConstants.pairing);
        pairingValidButton = createButton(GuiConstants.pairingValid);

        //Boutons pour la gestion
        groupCreationButton = createButton(GuiConstants.groupCreation);
        studentCloseButton = createButton(GuiConstants.studentClose);

        GridBagLayout gridbag;
        GridBagConstraints c;

        dim = new Dimension(menuWidth, 2 * (buttonSize + margin) + 2 * margin);
        monitoringMenu = new FilterPanel(resources.getString("monitoringLabel"), dim);
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        monitoringMenu.setLayout(gridbag);

        c.gridwidth = 1;
        gridbag.setConstraints(listeningButton, c);
        monitoringMenu.add(listeningButton);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(studentControlButton, c);
        monitoringMenu.add(studentControlButton);
        c.gridwidth = 1;
        gridbag.setConstraints(scanningButton, c);
        monitoringMenu.add(scanningButton);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(mosaiqueButton, c);
        monitoringMenu.add(mosaiqueButton);

        dim = new Dimension(menuWidth, 3 * (buttonSize + margin) + 2 * margin);
        diffusionMenu = new FilterPanel(resources.getString("diffusionLabel"), dim);
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        diffusionMenu.setLayout(gridbag);

        c.gridwidth = 1;
        gridbag.setConstraints(sendFileButton, c);
        diffusionMenu.add(sendFileButton);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(blackScreenButton, c);
        diffusionMenu.add(blackScreenButton);
        c.gridwidth = 1;
        gridbag.setConstraints(masterScreenButton, c);
        diffusionMenu.add(masterScreenButton);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(studentScreenButton, c);
        diffusionMenu.add(studentScreenButton);
        c.gridwidth = 2;
        gridbag.setConstraints(sendTextButton, c);
        diffusionMenu.add(sendTextButton);

        dim = new Dimension(menuWidth, (buttonSize + margin) + 2 * margin);
        pairingMenu = new FilterPanel(resources.getString("pairingLabel"), dim);
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        pairingMenu.setLayout(gridbag);

        c.gridwidth = 1;
        gridbag.setConstraints(pairingButton, c);
        pairingMenu.add(pairingButton);
        c.anchor = GridBagConstraints.SOUTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(pairingValidButton, c);
        pairingMenu.add(pairingValidButton);

        dim = new Dimension(menuWidth, (buttonSize + margin) + 2 * margin);
        gestionMenu = new FilterPanel(resources.getString("gestionLabel"), dim);
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        gestionMenu.setLayout(gridbag);

        c.gridwidth = 1;
        gridbag.setConstraints(groupCreationButton, c);
        gestionMenu.add(groupCreationButton);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(studentCloseButton, c);
        gestionMenu.add(studentCloseButton);

        JPanel supervisionMenu = new JPanel();
        supervisionMenu.setLayout(new BoxLayout(supervisionMenu, BoxLayout.Y_AXIS));
        supervisionMenu.add(Box.createVerticalStrut(margin));
        supervisionMenu.add(monitoringMenu);
        supervisionMenu.add(Box.createVerticalStrut(2 * margin));
        supervisionMenu.add(diffusionMenu);
        supervisionMenu.add(Box.createVerticalStrut(2 * margin));
        supervisionMenu.add(pairingMenu);
        supervisionMenu.add(Box.createVerticalStrut(2 * margin));
        supervisionMenu.add(gestionMenu);
        supervisionMenu.add(Box.createVerticalStrut(2 * margin));
        supervisionMenu.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        dim = new Dimension(menuWidth, menuHeight);
        supervisionMenu.setMaximumSize(dim);
        supervisionMenu.setPreferredSize(dim);
        setTabMenu(supervisionMenu);
    }

    /**
     * Initialise les menus.
     */
    private void initPopupMenu(Resources resources) {
        masterScreenMenu = new JMenu();
        masterScreenVoiceButton = new MenuButton(GuiConstants.masterScreenVoice,
                masterScreenButton, resources.getString(GuiConstants.masterScreenVoice));
        masterVoiceButton = new MenuButton(GuiConstants.masterVoice,
                masterScreenButton, resources.getString(GuiConstants.masterVoice));
        masterScreenMenu.add(masterScreenVoiceButton);
        masterScreenMenu.add(masterVoiceButton);
        masterScreenButton.setMenu(masterScreenMenu);

        studentCloseMenu = new JMenu();
        computerPowerButton = new MenuButton(GuiConstants.computerPower,
                studentCloseButton, resources.getString(GuiConstants.computerPower));
        osSessionButton = new MenuButton(GuiConstants.osSession,
                studentCloseButton, resources.getString(GuiConstants.osSession));
        loginSessionButton = new MenuButton(GuiConstants.loginSession,
                studentCloseButton, resources.getString(GuiConstants.loginSession));
        studentCloseMenu.add(computerPowerButton);
        studentCloseMenu.add(osSessionButton);
        studentCloseMenu.add(loginSessionButton);
        studentCloseButton.setMenu(studentCloseMenu);
    }

    @Override
    public void setButtonActions(ActionListener buttonListener,
            MouseAdapter menuButtonListener, MouseAdapter menuMouseListener,
            PopupMenuListener popupMenuListener) {
        listeningButton.addActionListener(buttonListener);
        studentControlButton.addActionListener(buttonListener);
        scanningButton.addActionListener(buttonListener);
        mosaiqueButton.addActionListener(buttonListener);

        sendFileButton.addActionListener(buttonListener);
        blackScreenButton.addActionListener(buttonListener);
        masterScreenButton.addActionListener(buttonListener);
        studentScreenButton.addActionListener(buttonListener);

        pairingButton.addActionListener(buttonListener);
        pairingValidButton.addActionListener(buttonListener);

        groupCreationButton.addActionListener(buttonListener);
        studentCloseButton.addActionListener(buttonListener);

        sendTextButton.addActionListener(buttonListener);

        masterScreenVoiceButton.addMouseListener(menuButtonListener);
        masterVoiceButton.addMouseListener(menuButtonListener);
        computerPowerButton.addMouseListener(menuButtonListener);
        osSessionButton.addMouseListener(menuButtonListener);
        loginSessionButton.addMouseListener(menuButtonListener);

        masterScreenMenu.getPopupMenu().addPopupMenuListener(popupMenuListener);
        studentCloseMenu.getPopupMenu().addPopupMenuListener(popupMenuListener);
        masterScreenMenu.getPopupMenu().addMouseListener(menuMouseListener);
        studentCloseMenu.getPopupMenu().addMouseListener(menuMouseListener);
    }

    @Override
    public void updateLanguage(Resources resources) {
        //Boutons pour le suivi
        listeningButton.setToolTipText(getToolTipText(resources, GuiConstants.listening));
        studentControlButton.setToolTipText(getToolTipText(resources, GuiConstants.studentControl));
        scanningButton.setToolTipText(getToolTipText(resources, GuiConstants.scanning));
        mosaiqueButton.setToolTipText(getToolTipText(resources, GuiConstants.mosaique));

        //Boutons pour la diffusion
        sendFileButton.setToolTipText(getToolTipText(resources, GuiConstants.sendFile));
        blackScreenButton.setToolTipText(getToolTipText(resources, GuiConstants.blackScreen));
        masterScreenButton.setToolTipText(getToolTipText(resources, GuiConstants.masterScreen));
        studentScreenButton.setToolTipText(getToolTipText(resources, GuiConstants.studentScreen));
        sendTextButton.setToolTipText(getToolTipText(resources, GuiConstants.sendMessage));

        //Boutons pour le pairing
        pairingButton.setToolTipText(getToolTipText(resources, GuiConstants.pairing));

        //Boutons pour la gestion
        groupCreationButton.setToolTipText(getToolTipText(resources, GuiConstants.groupCreation));
        studentCloseButton.setToolTipText(getToolTipText(resources, GuiConstants.studentClose));

        monitoringMenu.changeTitle(resources.getString("monitoringLabel"));
        diffusionMenu.changeTitle(resources.getString("diffusionLabel"));
        pairingMenu.changeTitle(resources.getString("pairingLabel"));
        gestionMenu.changeTitle(resources.getString("gestionLabel"));

        masterScreenVoiceButton.setText(resources.getString(GuiConstants.masterScreenVoice));
        masterVoiceButton.setText(resources.getString(GuiConstants.masterVoice));

        computerPowerButton.setText(resources.getString(GuiConstants.computerPower));
        osSessionButton.setText(resources.getString(GuiConstants.osSession));
        loginSessionButton.setText(resources.getString(GuiConstants.loginSession));
    }

    @Override
    public void updateButtonsFor(StateButton button, boolean hasGroup, boolean isGroupButton) {
        boolean enable = button.isOn();
//        boolean hasGroup = getGroupMenu().hasGroupSelected();
//        boolean isGroupButton = getGroupMenu().isGroupButton(button);

        listeningButton.setEnabled(enable || isGroupButton);
        studentControlButton.setEnabled(enable || isGroupButton);
        scanningButton.setEnabled(enable && hasGroup);
        mosaiqueButton.setEnabled(enable && hasGroup);

        sendFileButton.setEnabled(enable && hasGroup);
        blackScreenButton.setEnabled(enable && hasGroup);
        masterScreenButton.setEnabled(enable && hasGroup);
        studentScreenButton.setEnabled(enable && hasGroup);
        sendTextButton.setEnabled(enable && hasGroup);

        pairingButton.setEnabled(enable || isGroupButton);
        pairingValidButton.setEnabled(enable || isGroupButton);
        groupCreationButton.setEnabled(enable && !hasGroup);
        studentCloseButton.setEnabled(enable && hasGroup);

        button.setEnabled(true);

        if (button == pairingButton) {
            pairingValidButton.setVisible(!button.isOn());
            pairingValidButton.setOn(button.isOn());
            pairingValidButton.setEnabled(true);
        } else if (button == pairingValidButton) {
            pairingButton.setEnabled(true);
        }
    }

    @Override
    public void setGroupFonctionsEnabled(boolean enable) {
        scanningButton.setEnabled(enable);
        mosaiqueButton.setEnabled(enable);

        sendFileButton.setEnabled(enable);
        blackScreenButton.setEnabled(enable);
        masterScreenButton.setEnabled(enable);
        studentScreenButton.setEnabled(enable);
        sendTextButton.setEnabled(enable);

        groupCreationButton.setEnabled(!enable);
        studentCloseButton.setEnabled(enable);
    }

    @Override
    public StateButton getButton(String type) {
        StateButton button = null;
        switch (type) {
            case GuiConstants.mosaique:
                button = mosaiqueButton;
                break;
            case GuiConstants.scanning:
                button = scanningButton;
                break;
            case GuiConstants.pairing:
                button = pairingButton;
                break;
        }
        return button;
    }

    public StateButton getButton(JPopupMenu menu) {
        StateButton button = null;
        if (masterScreenMenu.getPopupMenu() == menu) {
            button = masterScreenButton;
        } else if (studentCloseMenu.getPopupMenu() == menu) {
            button = studentCloseButton;
        }
        return button;
    }

    private String getToolTipText(Resources resources, String type) {
        String text;
        switch (type) {
            case GuiConstants.scanning:
            case GuiConstants.mosaique:
            case GuiConstants.sendFile:
            case GuiConstants.blackScreen:
            case GuiConstants.masterScreen:
            case GuiConstants.studentScreen:
            case GuiConstants.sendMessage:
            case GuiConstants.studentClose:
                text = "<html><center>" + resources.getString(type) + "<br />"
                        + resources.getString("groupFunction") + "</center></html>";
                break;
            case GuiConstants.groupCreation:
                text = "<html><center>" + resources.getString(type) + "<br />"
                        + resources.getString("noGroupFunction") + "</center></html>";
                break;
            default:
                text = resources.getString(type);
        }
        return text;
    }
}
