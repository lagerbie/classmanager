package thot.labo.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.exception.ThotException;
import thot.gui.GuiUtilities;
import thot.gui.ProcessingBar;
import thot.gui.Resources;
import thot.labo.ProjectFiles;
import thot.labo.ProjectManager;
import thot.utils.Constants;
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
 * @version 1.8.4
 */
public class ExportDialog extends JDialog {
    private static final long serialVersionUID = 19000L;

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportDialog.class);

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
                    try {
                        manager.saveProject(file, project);
                    } catch (ThotException e) {
                        LOGGER.error("Une erreur est survenue dans la sauvegarde du projet", e);
                        GuiUtilities
                                .showMessage("Une erreur est survenue dans la sauvegarde du projet " + e.getMessage());
                    }
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
