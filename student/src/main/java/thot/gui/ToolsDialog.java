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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.ProgressPercentListener;
import thot.StudentCore;
import thot.model.Constants;

/**
 * Barre d'outils.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class ToolsDialog extends JFrame implements ProgressPercentListener {
    private static final long serialVersionUID = 19000L;
    /*
     * Resources textes : fileSend, help
     * Resources images : file, fileOn, appel, appelOn
     */

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ToolsDialog.class);

    /**
     * Référence du noyau.
     */
    private StudentCore core;
    /**
     * Gestion des différents textes suivant la langue.
     */
    private Resources resources;

    /**
     * Bouton pour envoyer fichier.
     */
    private JButton fileButton;
    /**
     * Bouton appel professeur.
     */
    private JButton helpDemandButton;
    /**
     * Image du bouton d'aide normale.
     */
    private ImageIcon helpImage;
    /**
     * Image du bouton d'aide lors de la réussite de la transmission.
     */
    private ImageIcon helpOnImage;
    /**
     * Fenêtre affichant une barre de progression.
     */
    private ProcessingBar processingBar;

    /**
     * Initialisation.
     *
     * @param core le coeur de l'application.
     * @param resources les resources textuelles.
     */
    public ToolsDialog(StudentCore core, Resources resources) {
        super(Constants.softName);
        this.core = core;
        this.resources = resources;

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setIconImage(GuiUtilities.getIcons().get(0));
        this.setResizable(false);

        initFrame();

        processingBar = new ProcessingBar(null, null);
    }

    /**
     * Initialisation de la fenêtre.
     */
    private void initFrame() {
        fileButton = getButton("file", "fileOn", resources.getString("fileSend"));

        helpDemandButton = getButton("appel", null, resources.getString("help"));
        helpImage = GuiUtilities.getImageIcon("appel");
        helpOnImage = GuiUtilities.getImageIcon("appelOn");

        JPanel panel = new JPanel();
        panel.add(fileButton);
        panel.add(helpDemandButton);
        this.getContentPane().add(panel);
        this.pack();

        fileButton.addActionListener(event -> {
            JFileChooser chooser = new JFileChooser(core.getUserHome());
            int returnVal = chooser.showOpenDialog(getFrame());

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                final File file = chooser.getSelectedFile();
                if (file != null && file.exists()) {
                    new Thread("sendFile") {
                        @Override
                        public void run() {
                            fileButton.setEnabled(false);
                            core.sendFile(file);
                            fileButton.setEnabled(true);
                        }
                    }.start();
                }
            }
        });

        helpDemandButton.addActionListener(event -> {
            boolean success = core.sendHelpDemand();
            if (success) {
                updateHelpDemandButton(true);
                new Thread("senHelpDemand") {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            LOGGER.error("", e);
                        }
                        updateHelpDemandButton(false);
                    }
                }.start();
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setState(JFrame.ICONIFIED);
            }
        });
    }

    @Override
    public void processTitleChanged(Object source, String title) {
        processingBar.setTitle(title);
    }

    @Override
    public void processMessageChanged(Object source, String message) {
        processingBar.setMessage(message);
    }

    @Override
    public void processDeterminatedChanged(Object source, boolean determinated) {
        processingBar.setDeterminate(determinated);
    }

    @Override
    public void processDoubleStatusChanged(Object source, boolean doubleStatus) {
        processingBar.setDoubleProgress(doubleStatus);
    }

    @Override
    public void processBegin(Object source, boolean determinated) {
        processingBar.processBegin(determinated);
    }

    @Override
    public void processEnded(Object source, int exit) {
        processingBar.close();
    }

    @Override
    public void percentChanged(Object source, int percent) {
        processingBar.setValue(percent);
    }

    @Override
    public void percentChanged(Object source, int total, int subTotal) {
        processingBar.setValue(total, subTotal);
    }

    /**
     * Change la langue.
     */
    public void updateLanguage() {
        fileButton.setToolTipText(resources.getString("fileSend"));
        helpDemandButton.setToolTipText(resources.getString("help"));
    }

    /**
     * Création d'un bouton avec son type.
     *
     * @param type le type de bouton
     * @param animation pour savoir si il y a des effets sur le bouton.
     * @return le bouton créé.
     */
    private JButton getButton(String pathImageOn, String pathImageOff, String toolTipText) {
        JButton button = new JButton(GuiUtilities.getImageIcon((pathImageOn)));

        if (pathImageOff != null) {
            button.setDisabledIcon(GuiUtilities.getImageIcon((pathImageOff)));
        }

        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        button.setToolTipText(toolTipText);
        return button;
    }

    /**
     * Actualise l'état du bouton d'appel.
     *
     * @param appel état de l'appel.
     */
    private void updateHelpDemandButton(boolean appel) {
        if (appel) {
            helpDemandButton.setIcon(helpOnImage);
        } else {
            helpDemandButton.setIcon(helpImage);
        }
        helpDemandButton.repaint();
        this.repaint();
    }

    private JFrame getFrame() {
        return this;
    }
}
