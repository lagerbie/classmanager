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
package thot.labo.gui;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

import thot.gui.ProcessingBar;
import thot.gui.Resources;
import thot.labo.ProjectFiles;
import thot.labo.ProjectManager;
import thot.utils.Constants;

/**
 * Fenêtre de dialogue pour l'effacement de données.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class EraseDialog extends JDialog {
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
     * Message pour la sélection des éléments.
     */
    private JLabel messageLabel;

    /**
     * Boutton pour valider.
     */
    private JButton validButton;
    /**
     * Boutton pour annuler.
     */
    private JButton cancelButton;

    /**
     * Boutton pour tout sélectionner.
     */
    private JRadioButton allButton;
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
    public EraseDialog(Window parent, ProjectManager manager, Resources resources, ProcessingBar processingBar) {
        super(parent, resources.getString("eraseTitle"), DEFAULT_MODALITY_TYPE);

        this.resources = resources;
        this.manager = manager;
        this.processingBar = processingBar;

        initComponents();
    }

    /**
     * Initialisation des composants graphiques.
     */
    private void initComponents() {
        int margin = 20;
        int offset = 40;

        messageLabel = new JLabel(resources.getString("eraseMessage"));

        validButton = new JButton(resources.getString("valid"));
        cancelButton = new JButton(resources.getString("cancel"));

        allButton = new JRadioButton(resources.getString("allChoice"));
        audioButton = new JCheckBox(resources.getString("audioChoice"));
        videoButton = new JCheckBox(resources.getString("videoChoice"));
        textButton = new JCheckBox(resources.getString("textChoice"));

        JPanel panel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setLayout(layout);

        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(2 * margin, margin, margin, margin);
        constraints.anchor = GridBagConstraints.BASELINE;
        layout.setConstraints(messageLabel, constraints);
        panel.add(messageLabel);

        constraints.insets = new Insets(0, margin + offset, 0, margin);
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;

        layout.setConstraints(allButton, constraints);
        panel.add(allButton);
        layout.setConstraints(videoButton, constraints);
        panel.add(videoButton);
        layout.setConstraints(audioButton, constraints);
        panel.add(audioButton);
        layout.setConstraints(textButton, constraints);
        panel.add(textButton);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(2 * margin, margin, 2 * margin, margin);
        constraints.anchor = GridBagConstraints.BASELINE;
        layout.setConstraints(validButton, constraints);
        panel.add(validButton);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(cancelButton, constraints);
        panel.add(cancelButton);

        this.getContentPane().add(panel);
        this.pack();

        ActionListener selectionAction = e -> {
            AbstractButton source = (AbstractButton) e.getSource();
            if (source == allButton) {
                videoButton.setSelected(source.isSelected());
                audioButton.setSelected(source.isSelected());
                textButton.setSelected(source.isSelected());
            } else if (!source.isSelected()) {
                allButton.setSelected(false);
            }
        };

        allButton.addActionListener(selectionAction);
        videoButton.addActionListener(selectionAction);
        audioButton.addActionListener(selectionAction);
        textButton.addActionListener(selectionAction);

        cancelButton.addActionListener(event -> close());

        validButton.addActionListener(event -> {
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
                processingBar.processBegin(false, resources.getString("processingTitle"),
                        resources.getString("processingMessage"));
                manager.removeProject(project);
                close();
            });
            thread.start();
        });
    }

    /**
     * Modification des textes pour un changement de langue.
     */
    public void updateLanguage() {
        this.setTitle(resources.getString("eraseTitle"));
        messageLabel.setText(resources.getString("eraseMessage"));

        validButton.setText(resources.getString("valid"));
        cancelButton.setText(resources.getString("cancel"));

        allButton.setText(resources.getString("allChoice"));
        audioButton.setText(resources.getString("audioChoice"));
        videoButton.setText(resources.getString("videoChoice"));
        textButton.setText(resources.getString("textChoice"));
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
     */
    public void showDialog() {
        boolean hasData = (manager.getRecordTimeMax() > 0);
        audioButton.setEnabled(hasData);
        audioButton.setSelected(false);

        boolean hasVideo = (manager.getProjectFiles().getVideoFile() != null);
        videoButton.setEnabled(hasVideo);
        videoButton.setSelected(false);

        boolean hasText = (manager.getText() != null && !manager.getText().isEmpty());
        textButton.setEnabled(hasText);
        textButton.setSelected(false);

        this.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width - getWidth()) / 2, (dim.height - getHeight()) / 2);
        this.setVisible(true);
    }
}
