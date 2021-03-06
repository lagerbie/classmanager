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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import thot.ProjectManager;
import thot.model.Constants;
import thot.model.ProjectFiles;
import thot.utils.Utilities;

/*
 * resources:
 *  exportTitle, exportMessage, saveTitle, saveMessage
 *  saveChoice, audioChoice, videoChoice, textChoice
 *  valid, cancel, eraseFile (%s)
 */

/**
 * Fenêtre de dialogue pour l'exportation.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class ExportDialog extends JDialog {
    private static final long serialVersionUID = 19000L;

    /**
     * Resources textuelles.
     */
    private Resources resources;
    /**
     * Gestionnaire de projets.
     */
    private ProjectManager manager;
    /**
     * Barre de progression.
     */
    private ProcessingBar processingBar;
    /**
     * Fichier principal de l'exportation.
     */
    private File file;

    /**
     * Message pour l'exportation.
     */
    private JLabel messageLabel;
    /**
     * Message pour le choix des fichiers à exporter.
     */
    private JLabel filesChoiceLabel;

    /**
     * Boutton pour valider.
     */
    private JButton validButton;
    /**
     * Boutton pour annuler.
     */
    private JButton cancelButton;

    /**
     * Boutton pour le choix de la piste vidéo.
     */
    private JCheckBox videoButton;
    /**
     * Boutton pour le choix de la piste audio.
     */
    private JCheckBox audioButton;
    /**
     * Boutton pour le choix du texte associé.
     */
    private JCheckBox textButton;

    /**
     * Initialisation de la fenêtre.
     *
     * @param parent la fenêtre parente.
     * @param manager le gestionnaire de projets.
     * @param resources les resources textuelles.
     * @param processingBar barre de progression pour les traitements.
     */
    public ExportDialog(Window parent, ProjectManager manager, Resources resources, ProcessingBar processingBar) {
        super(parent, resources.getString("exportTitle"), DEFAULT_MODALITY_TYPE);

        this.resources = resources;
        this.manager = manager;
        this.processingBar = processingBar;
        initComponents();
    }

    /**
     * Initialisation des composants graphiques.
     */
    private void initComponents() {
        messageLabel = new JLabel(resources.getString("exportMessage"));
        filesChoiceLabel = new JLabel(resources.getString("saveChoice"));

        audioButton = new JCheckBox(resources.getString("audioChoice"));
        videoButton = new JCheckBox(resources.getString("videoChoice"));
        textButton = new JCheckBox(resources.getString("textChoice"));

        validButton = new JButton(resources.getString("valid"));
        cancelButton = new JButton(resources.getString("cancel"));
        JPanel buttonMenu = new JPanel();
        buttonMenu.add(validButton);
        buttonMenu.add(cancelButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(filesChoiceLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(videoButton);
        mainPanel.add(audioButton);
        mainPanel.add(textButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonMenu);

        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
        this.getContentPane().add(mainPanel);
        this.getContentPane().add(Box.createHorizontalStrut(100));


        cancelButton.addActionListener(event -> close());

        validButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                final ProjectFiles project = new ProjectFiles();

                if (audioButton != null && audioButton.isSelected()) {
                    project.setAudioFile(Constants.audioDefaultExtension);
                }
                if (videoButton != null && videoButton.isSelected()) {
                    project.setVideoFile(Constants.videoDefaultExtension);
                }
                if (textButton != null && textButton.isSelected()) {
                    project.setTextFile(Constants.textDefaultExtension);
                }

                Thread thread = new Thread(() -> {
                    processingBar
                            .processBegin(true, resources.getString("saveTitle"), resources.getString("saveMessage"),
                                    (file == null) ? null : file.getAbsolutePath());
                    boolean success = manager.saveProject(file, project);
                    close();
                }, this.getClass().getName());
                thread.start();

            }
        });
    }

    /**
     * Modification des textes pour un changement de langue.
     */
    public void updateLanguage() {
        this.setTitle(resources.getString("exportTitle"));
        messageLabel.setText(resources.getString("exportMessage"));
        filesChoiceLabel.setText(resources.getString("saveChoice"));

        audioButton.setText(resources.getString("audioChoice"));
        videoButton.setText(resources.getString("videoChoice"));
        textButton.setText(resources.getString("textChoice"));

        validButton.setText(resources.getString("valid"));
        cancelButton.setText(resources.getString("cancel"));
    }

    /**
     * Ferme la fenêtre et les resources associées.
     */
    public void close() {
        processingBar.close();
        this.setVisible(false);
        this.dispose();
    }

    /**
     * Affiche la fenêtre et initialise les différents éléments de choix.
     *
     * @param file le fichier principal de l'exportation.
     */
    public void showDialog(File file) {
        this.file = file;

        if (!Utilities.isProjectFile(file)) {
            this.file = Utilities.returnFileWithExtension(file, Constants.projectExtension);
        }

        //Vérification si le fichier de sauvegarde existe déjà
        if (this.file.exists()) {
            int choix = GuiUtilities.showOptionDialog(getOwner(),
                    String.format(resources.getString("eraseFile"), this.file), null, null);

            if (choix != GuiUtilities.YES_OPTION) {
                return;
            }
        }

        boolean hasData = (manager.getRecordTimeMax() > 0);
        audioButton.setEnabled(hasData);
        audioButton.setSelected(hasData);

        boolean hasVideo = (manager.getProjectFiles().getVideoFile() != null);
        videoButton.setEnabled(hasVideo);
        videoButton.setSelected(hasVideo);

        boolean hasText = (manager.getText() != null && !manager.getText().isEmpty());
        textButton.setEnabled(hasText);
        textButton.setSelected(hasText);

        this.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width - this.getWidth()) / 2, (dim.height - this.getHeight()) / 2);
        this.setVisible(true);
    }
}
