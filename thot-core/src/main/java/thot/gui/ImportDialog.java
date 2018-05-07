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
import java.io.File;

import javax.swing.*;

import thot.ProjectManager;
import thot.model.Constants;
import thot.model.ProjectFiles;
import thot.utils.Utilities;

/*
 * resources:
 *  importTitle, importMessage, loadTitle, loadMessage
 *  loadChoice, audioChoice, videoChoice, textChoice
 *  valid, cancel
 *  loadError (%s), fileFormatNotSupported (%s), noFile (%s)
 */

/**
 * Fenêtre de dialogue pour l'exportation.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class ImportDialog extends JDialog {
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
     * Chemin du dossier temporarire.
     */
    private File tempPath;
    /**
     * Fichier principal de l'importation.
     */
    private File file;

    /**
     * Fichier pour la piste vidéo.
     */
    private File videoFile;
    /**
     * Fichier pour les index.
     */
    private File indexesFile;
    /**
     * Fichier pour la piste audio.
     */
    private File audioFile;
    /**
     * Fichier pour le texte associé.
     */
    private File textFile;

    /**
     * Message pour l'importation.
     */
    private JLabel messageLabel;
    /**
     * Message pour le choix des fichiers à importer.
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
     * Boutton pour le choix de la piste vidéo à importer.
     */
    private JCheckBox videoButton;
    /**
     * Boutton pour le choix de la piste audio à importer.
     */
    private JCheckBox audioButton;
    /**
     * Boutton pour le choix du texte associé à importer.
     */
    private JCheckBox textButton;

    /**
     * Initialisation de la fenêtre.
     *
     * @param parent la fenêtre parente.
     * @param manager le gestionnaire de projets.
     * @param resources les resources textuelles.
     * @param processingBar barre de progression pour les traitements.
     * @param tempPath le dossier temporaire pour des manipulation.
     */
    public ImportDialog(Window parent, ProjectManager manager, Resources resources, ProcessingBar processingBar,
            File tempPath) {
        super(parent, resources.getString("importTitle"), DEFAULT_MODALITY_TYPE);

        this.resources = resources;
        this.manager = manager;
        this.processingBar = processingBar;
        this.tempPath = tempPath;

        this.setIconImages(GuiUtilities.getIcons());
        initComponents();
    }

    /**
     * Initialisation des composants graphiques.
     */
    private void initComponents() {
        messageLabel = new JLabel(resources.getString("importMessage"));
        filesChoiceLabel = new JLabel(resources.getString("loadChoice"));

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
        mainPanel.add(audioButton);
        mainPanel.add(videoButton);
        mainPanel.add(textButton);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonMenu);

        this.getContentPane().setLayout(
                new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
        this.getContentPane().add(mainPanel);
        this.getContentPane().add(Box.createHorizontalStrut(100));

        cancelButton.addActionListener(event -> close());

        validButton.addActionListener(event -> validAction());
    }

    /**
     * Modification des textes pour un changement de langue.
     */
    public void updateLanguage() {
        this.setTitle(resources.getString("importTitle"));

        messageLabel.setText(resources.getString("importMessage"));
        filesChoiceLabel.setText(resources.getString("loadChoice"));

        audioButton.setText(resources.getString("audioChoice"));
        videoButton.setText(resources.getString("videoChoice"));
        textButton.setText(resources.getString("textChoice"));

        validButton.setText(resources.getString("valid"));
        cancelButton.setText(resources.getString("cancel"));
    }

    /**
     * Teste la validité des choix et importe ou efface les éléments en conséquence.
     */
    private void validAction() {
        ProjectFiles project = new ProjectFiles();

        if (audioButton != null && audioButton.isSelected()) {
            project.setAudioFile(audioFile.getAbsolutePath());
        }
        if (videoButton != null && videoButton.isSelected()) {
            project.setVideoFile(videoFile.getAbsolutePath());
            if (indexesFile != null) {
                project.setIndexesFile(indexesFile.getAbsolutePath());
            }
        }
        if (textButton != null && textButton.isSelected()) {
            project.setTextFile(textFile.getAbsolutePath());
        }

        performLoad(project);
    }

    /**
     * Importe ou efface les éléments présents ou non dans le projet. Si un élément doit être importer, le chemin
     * complet du fichier est indiqué. Si un élément doit être garder, l'extension par défaut du type de l'élément est
     * indique. Si un élément doit être effacer, null est indiqué.
     *
     * @param project les différents éléments.
     */
    private void performLoad(final ProjectFiles project) {
        processingBar.processBegin(true, resources.getString("loadTitle"), resources.getString("loadMessage"),
                (file == null) ? null : file.getAbsolutePath());

        Thread thread = new Thread(() -> {
            boolean success = manager.loadProject(project);
            close();
        }, this.getClass().getName());
        thread.start();
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
     * @param file le fichier principal de l'importation.
     */
    public void showDialog(File file) {
        this.file = file;
        updateLanguage();

        ProjectFiles newProject;
        indexesFile = null;
        audioFile = null;
        videoFile = null;
        textFile = null;

        String name = Utilities.getNameWithoutExtension(file);
        File path = null;
        File projectFile = null;

        //on récupère le fichier projet associé si c'est un fichier projet, on le décompresse
        if (Utilities.isProjectFile(file)) {
            path = new File(tempPath, name);
            boolean hasFile = Utilities.extractArchive(file, path);
            if (!hasFile) {
                GuiUtilities.showMessageDialog(getOwner(),
                        String.format(resources.getString("loadError"), file));
            }

            projectFile = Utilities.searchFile(path, name, Constants.projectInternExtension);
            //nom différent (renommage du fichier projet après la sauvegarde)
            if (projectFile == null) {
                projectFile = Utilities.searchFile(path, Constants.projectInternExtension);
            }
        } else if (Utilities.isProjectInternFile(file)) {
            projectFile = file;
        }

        //si il y a un projet, on récupère les fichiers
        if (projectFile != null && projectFile.exists()) {
            newProject = Utilities.getProject(projectFile);
        } else {
            newProject = new ProjectFiles();
            if (Utilities.isTextFile(file)) {
                newProject.setTextFile(file.getAbsolutePath());
            } else if (Utilities.isIndexFile(file)) {
                newProject.setIndexesFile(file.getAbsolutePath());
            } else {
                if (!manager.hasAudioSrteam(file) && !manager.hasVideoSrteam(file)) {
                    GuiUtilities.showMessageDialog(getOwner(),
                            String.format(resources.getString("fileFormatNotSupported"), file));
                    return;
                }
                newProject.setVideoFile(file.getAbsolutePath());
            }
        }

        //intitialisation des fichiers
        if (newProject.getVideoFile() != null) {
            videoFile = new File(path, newProject.getVideoFile());
        }
        if (newProject.getIndexesFile() != null) {
            indexesFile = new File(path, newProject.getIndexesFile());
        }
        if (newProject.getAudioFile() != null) {
            audioFile = new File(path, newProject.getAudioFile());
        }
        if (newProject.getTextFile() != null) {
            textFile = new File(path, newProject.getTextFile());
        }

        //vérification que les fichiers en chargement sont disponibles
        if (indexesFile != null && !indexesFile.exists()) {
            GuiUtilities.showMessageDialog(getOwner(), String.format(resources.getString("noFile"), indexesFile));
            indexesFile = null;
            newProject.setIndexesFile(null);
        }
        if (audioFile != null && !audioFile.exists()) {
            GuiUtilities.showMessageDialog(getOwner(), String.format(resources.getString("noFile"), audioFile));
            audioFile = null;
            newProject.setAudioFile(null);
        }
        if (videoFile != null && !videoFile.exists()) {
            GuiUtilities.showMessageDialog(getOwner(), String.format(resources.getString("noFile"), videoFile));
            videoFile = null;
            newProject.setVideoFile(null);
        }
        if (textFile != null && !textFile.exists()) {
            GuiUtilities.showMessageDialog(getOwner(), String.format(resources.getString("noFile"), textFile));
            textFile = null;
            newProject.setTextFile(null);
        }

        if (newProject.isEmptyProject()) {
            return;
        }

        //si il y a un seul fichier (avec ou sans index), on charge directement
        if ((videoFile != null && audioFile == null && textFile == null)
                || (videoFile == null && audioFile != null && textFile == null)
                || (videoFile == null && audioFile == null && textFile != null)) {
            performLoad(newProject);
            return;
        }

        boolean hasAudio = (audioFile != null);
        audioButton.setEnabled(hasAudio);
        audioButton.setSelected(hasAudio);

        boolean hasVideo = (videoFile != null);
        videoButton.setEnabled(hasVideo);
        videoButton.setSelected(hasVideo);

        boolean hasText = (textFile != null);
        textButton.setEnabled(hasText);
        textButton.setSelected(hasText);

        this.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width - this.getWidth()) / 2, (dim.height - this.getHeight()) / 2);
        this.setVisible(true);
    }
}
