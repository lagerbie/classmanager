package eestudio.gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.*;

import eestudio.Core;
import eestudio.utils.Utilities;
import thot.model.Constants;
import thot.model.ProjectFiles;

/**
 * Fenêtre de dialogue pour l'exportation.
 *
 * @author Fabrice Alleau
 */
public class ImportDialog extends JDialog {
    private static final long serialVersionUID = 9512L;

    /**
     * Resources textuelles
     */
    private Resources resources;
    /**
     * Noyau pour la création des index
     */
    private Core core;

    /**
     * Barre de progression
     */
    private ProcessingBar processingBar;

    /**
     * Chemin du dossier temporarire
     */
    private File tempPath;
    /**
     * Fichier principal de l'importation
     */
    private File file;

    /**
     * Fichier pour les index
     */
    private File indexesFile;
    /**
     * Fichier pour la piste audio
     */
    private File audioFile;
    /**
     * Fichier pour la piste vidéo
     */
    private File videoFile;
    /**
     * Fichier pour le texte associé
     */
    private File textFile;
    /**
     * Fichier pour le texte associé
     */
    private File tagFile;

    /**
     * Message pour l'importation
     */
    private JLabel messageLabel;
    /**
     * Message pour le choix des fichiers à importer
     */
    private JLabel filesChoiceLabel;
    /**
     * Message pour le choix des éléments déjà présents à garder
     */
    private JLabel oldChoiceLabel;

    /**
     * Boutton pour valider
     */
    private JButton validButton;
    /**
     * Boutton pour annuler
     */
    private JButton cancelButton;

    /**
     * Boutton pour le choix des index à importer
     */
    private JCheckBox indexesButton;
    /**
     * Boutton pour le choix de la piste vidéo à importer
     */
    private JCheckBox videoButton;
    /**
     * Boutton pour le choix de la piste audio à importer
     */
    private JCheckBox audioButton;
    /**
     * Boutton pour le choix du texte associé à importer
     */
    private JCheckBox textButton;

    /**
     * Boutton pour le choix des index à garder
     */
    private JCheckBox oldIndexesButton;
    /**
     * Boutton pour le choix de la piste audio à garder
     */
    private JCheckBox oldAudioButton;
    /**
     * Boutton pour le choix de la piste vidéo à garder
     */
    private JCheckBox oldVideoButton;
    /**
     * Boutton pour le choix du texte associé à garder
     */
    private JCheckBox oldTextButton;

    /**
     * Initialisation de la fenêtre.
     *
     * @param parent la fenêtre parente.
     * @param core le noyau de l'application.
     * @param resources les resources textuelles.
     * @param processingBar barre de progression pour les traitements.
     * @param tempPath le dossier temporaire pour des manipulation.
     */
    public ImportDialog(Window parent, Core core, Resources resources, ProcessingBar processingBar, File tempPath) {
        super(parent, resources.getString("importTitle"), DEFAULT_MODALITY_TYPE);

        this.resources = resources;
        this.core = core;
        this.processingBar = processingBar;
        this.tempPath = tempPath;

        initComponents();
    }

    /**
     * Initialisation des composants graphiques.
     */
    private void initComponents() {
        int margin = 20;
        int panelWidth = 310;
        int width = 2 * (panelWidth + 2 * margin);
        int height = 500;
        int offset = 80;
        Dimension dim;

        messageLabel = new JLabel(resources.getString("importMessage"));
        filesChoiceLabel = new JLabel(resources.getString("loadChoice"));
        oldChoiceLabel = new JLabel(resources.getString("oldChoice"));

        indexesButton = new JCheckBox(resources.getString("indexesChoice"));
        audioButton = new JCheckBox(resources.getString("audioChoice"));
        videoButton = new JCheckBox(resources.getString("videoChoice"));
        textButton = new JCheckBox(resources.getString("textChoice"));

        oldIndexesButton = new JCheckBox(resources.getString("indexesChoice"));
        oldAudioButton = new JCheckBox(resources.getString("audioChoice"));
        oldVideoButton = new JCheckBox(resources.getString("videoChoice"));
        oldTextButton = new JCheckBox(resources.getString("textChoice"));

        dim = new Dimension(panelWidth, 20);
        oldChoiceLabel.setPreferredSize(dim);
        filesChoiceLabel.setPreferredSize(dim);

        ActionListener deselectionAction = e -> {
            JCheckBox source = (JCheckBox) e.getSource();
            if (source.isSelected()) {
                JCheckBox opposite = null;
                if (source == indexesButton) {
                    opposite = oldIndexesButton;
                } else if (source == oldIndexesButton) {
                    opposite = indexesButton;
                } else if (source == audioButton) {
                    opposite = oldAudioButton;
                } else if (source == oldAudioButton) {
                    opposite = audioButton;
                } else if (source == videoButton) {
                    opposite = oldVideoButton;
                } else if (source == oldVideoButton) {
                    opposite = videoButton;
                } else if (source == textButton) {
                    opposite = oldTextButton;
                } else if (source == oldTextButton) {
                    opposite = textButton;
                }

                if (opposite != null && opposite.isSelected()) {
                    opposite.setSelected(false);
                }

                getContentPane().repaint();
            }
        };

        indexesButton.addActionListener(deselectionAction);
        audioButton.addActionListener(deselectionAction);
        videoButton.addActionListener(deselectionAction);
        textButton.addActionListener(deselectionAction);

        oldIndexesButton.addActionListener(deselectionAction);
        oldAudioButton.addActionListener(deselectionAction);
        oldVideoButton.addActionListener(deselectionAction);
        oldTextButton.addActionListener(deselectionAction);

        validButton = new JButton(resources.getString("valid"));
        cancelButton = new JButton(resources.getString("cancel"));

        BackgroundPanel panel = new BackgroundPanel(width, height, 15);
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

        constraints.gridwidth = 1;
        constraints.insets = new Insets(margin, offset, margin, margin);
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        layout.setConstraints(oldChoiceLabel, constraints);
        panel.add(oldChoiceLabel);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(margin, 0, margin, margin);
        layout.setConstraints(filesChoiceLabel, constraints);
        panel.add(filesChoiceLabel);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, margin + offset, 0, margin);
        layout.setConstraints(oldIndexesButton, constraints);
        panel.add(oldIndexesButton);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0, margin, 0, margin);
        layout.setConstraints(indexesButton, constraints);
        panel.add(indexesButton);
        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, margin + offset, 0, margin);
        layout.setConstraints(oldAudioButton, constraints);
        panel.add(oldAudioButton);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0, margin, 0, margin);
        layout.setConstraints(audioButton, constraints);
        panel.add(audioButton);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, margin + offset, 0, margin);
        layout.setConstraints(oldVideoButton, constraints);
        panel.add(oldVideoButton);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0, margin, 0, margin);
        layout.setConstraints(videoButton, constraints);
        panel.add(videoButton);
        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, margin + offset, 0, margin);
        layout.setConstraints(oldTextButton, constraints);
        panel.add(oldTextButton);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0, margin, 0, margin);
        layout.setConstraints(textButton, constraints);
        panel.add(textButton);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(2 * margin, margin + offset + 50, 2 * margin, margin);
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        layout.setConstraints(validButton, constraints);
        panel.add(validButton);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(2 * margin, margin, 2 * margin, margin + offset + 50);
        constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        layout.setConstraints(cancelButton, constraints);
        panel.add(cancelButton);

        this.getContentPane().add(panel);
        this.pack();

        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                getContentPane().repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                getContentPane().repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                getContentPane().repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                getContentPane().repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                getContentPane().repaint();
            }
        };

        FocusListener focusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                getContentPane().repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                getContentPane().repaint();
            }
        };

        oldIndexesButton.addMouseListener(mouseListener);
        oldAudioButton.addMouseListener(mouseListener);
        oldVideoButton.addMouseListener(mouseListener);
        oldTextButton.addMouseListener(mouseListener);
        indexesButton.addMouseListener(mouseListener);
        audioButton.addMouseListener(mouseListener);
        videoButton.addMouseListener(mouseListener);
        textButton.addMouseListener(mouseListener);
        oldIndexesButton.addFocusListener(focusListener);
        oldAudioButton.addFocusListener(focusListener);
        oldVideoButton.addFocusListener(focusListener);
        oldTextButton.addFocusListener(focusListener);
        indexesButton.addFocusListener(focusListener);
        audioButton.addFocusListener(focusListener);
        videoButton.addFocusListener(focusListener);
        textButton.addFocusListener(focusListener);

        cancelButton.addActionListener(event -> {
            getContentPane().repaint();
            close();
        });

        validButton.addActionListener(event -> {
            getContentPane().repaint();
            validAction();
        });
    }

    /**
     * Modification des textes pour un changement de langue.
     */
    public void updateLanguage() {
        this.setTitle(resources.getString("importTitle"));

        messageLabel.setText(resources.getString("importMessage"));
        filesChoiceLabel.setText(resources.getString("loadChoice"));
        oldChoiceLabel.setText(resources.getString("oldChoice"));

        indexesButton.setText(resources.getString("indexesChoice"));
        audioButton.setText(resources.getString("audioChoice"));
        videoButton.setText(resources.getString("videoChoice"));
        textButton.setText(resources.getString("textChoice"));

        oldIndexesButton.setText(resources.getString("indexesChoice"));
        oldAudioButton.setText(resources.getString("audioChoice"));
        oldVideoButton.setText(resources.getString("videoChoice"));
        oldTextButton.setText(resources.getString("textChoice"));

        validButton.setText(resources.getString("valid"));
        cancelButton.setText(resources.getString("cancel"));
    }

    /**
     * Teste la validité des choix et importe ou efface les éléments en conséquence.
     */
    private void validAction() {
        ProjectFiles project = new ProjectFiles();

        if (indexesButton.isSelected()) {
            project.setIndexesFile(indexesFile.getAbsolutePath());
        }
        if (audioButton.isSelected()) {
            project.setAudioFile(audioFile.getAbsolutePath());
            if (tagFile != null) {
                project.setTagFile(tagFile.getAbsolutePath());
            }
        }
        if (videoButton.isSelected()) {
            project.setVideoFile(videoFile.getAbsolutePath());
        }
        if (textButton.isSelected()) {
            project.setTextFile(textFile.getAbsolutePath());
        }

        if (oldIndexesButton.isSelected()) {
            project.setIndexesFile(Constants.indexesExtension);
        }
        if (oldAudioButton.isSelected()) {
            project.setAudioFile(Constants.audioDefaultExtension);
            if (!core.getTags().isEmpty()) {
                project.setTagFile(Constants.tagExtension);
            }
        }
        if (oldVideoButton.isSelected()) {
            project.setVideoFile(Constants.videoDefaultExtension);
        }
        if (oldTextButton.isSelected()) {
            project.setTextFile(Constants.textDefaultExtension);
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
        processingBar
                .processBegin(true, resources.getString("conversionTitle"), resources.getString("conversionMessage"),
                        (file == null) ? null : file.getAbsolutePath());

        Thread thread = new Thread(() -> {
            boolean success = core.loadProject(project);
            close();
        });
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

        ProjectFiles oldProject = new ProjectFiles();

        boolean hasIndex = (core.getIndexesCount() > 0);
        oldIndexesButton.setEnabled(hasIndex);
        oldIndexesButton.setSelected(false);

        if (hasIndex) {
            oldProject.setIndexesFile(Constants.indexesExtension);
        }

        boolean hasData = (core.getRecordTimeMax() > 0);
        oldAudioButton.setEnabled(hasData);
        oldAudioButton.setSelected(false);
        if (hasData) {
            oldProject.setAudioFile(Constants.audioDefaultExtension);
        }

        boolean hasVideo = core.hasVideo();
        oldVideoButton.setEnabled(hasVideo);
        oldVideoButton.setSelected(false);
        if (hasVideo) {
            oldProject.setVideoFile(Constants.videoDefaultExtension);
        }

        boolean hasText = core.hasText();
        oldTextButton.setEnabled(hasText);
        oldTextButton.setSelected(false);
        if (hasText) {
            oldProject.setTextFile(Constants.textDefaultExtension);
        }

        ProjectFiles newProject;
        indexesFile = null;
        audioFile = null;
        videoFile = null;
        textFile = null;

        File path = null;
        File projectFile = null;

        //on récupère le fichier projet associé si c'est un fichier edu4, on le décompresse
        if (Utilities.isProjectFile(file)) {
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
//                if(core.getFileDuration(file) < 100) {
//                    GuiUtilities.showMessageDialog(getOwner(), String.format(resources.getString("fileFormatNotSupported"), file));
//                    return;
//                }
                if (core.hasAudioSrteam(file)) {
                    newProject.setAudioFile(file.getAbsolutePath());
                }
                if (core.hasVideoSrteam(file)) {
                    newProject.setVideoFile(file.getAbsolutePath());
                }
            }
        }

//        int fileType = Utilities.getFileType(file);
//        switch(fileType) {
//            case Constants.EDU4_FILE:
//            case Constants.PROJECT_FILE:
//                break;
//            case Constants.INDEX_FILE:
//                newProject.setIndexesFile(file.getAbsolutePath());
//                break;
//            case Constants.TEXT_FILE:
//                newProject.setTextFile(file.getAbsolutePath());
//                break;
//            case Constants.SUBTITLE_FILE:
//                newProject.setSubtitleFile(file.getAbsolutePath());
//                break;
//            default:
//                if(core.getFileDuration(file) < 100) {
//                    GuiUtilities.showMessageDialog(getOwner(), String.format(
//                            resources.getString("fileFormatNotSupported"), file));
//                    return;
//                }
//                if(core.hasAudioSrteam(file))
//                    newProject.setAudioFile(file.getAbsolutePath());
//                if(core.hasVideoSrteam(file))
//                    newProject.setVideoFile(file.getAbsolutePath());
//        }

        //intitialisation des fichiers
        if (newProject.getIndexesFile() != null) {
            indexesFile = new File(path, newProject.getIndexesFile());
        }
        if (newProject.getAudioFile() != null) {
            audioFile = new File(path, newProject.getAudioFile());
        }
        if (newProject.getVideoFile() != null) {
            videoFile = new File(path, newProject.getVideoFile());
        }
        if (newProject.getTextFile() != null) {
            textFile = new File(path, newProject.getTextFile());
        }
        if (newProject.getTagFile() != null) {
            tagFile = new File(path, newProject.getTagFile());
        }

        //vérification que les fichiers en chargement sont disponibles
        if (indexesFile != null && !indexesFile.exists()) {
            GuiUtilities.showMessageDialog(getOwner(), String.format(resources.getString("noFile"), indexesFile));
            indexesFile = null;
            newProject.setIndexesFile(null);
        }//end indexesFile
        if (audioFile != null && !audioFile.exists()) {
            GuiUtilities.showMessageDialog(getOwner(), String.format(resources.getString("noFile"), audioFile));
            audioFile = null;
            newProject.setAudioFile(null);
        }//end audioFile
        if (videoFile != null && !videoFile.exists()) {
            GuiUtilities.showMessageDialog(getOwner(), String.format(resources.getString("noFile"), videoFile));
            videoFile = null;
            newProject.setVideoFile(null);
        }//end videoFile
        if (textFile != null && !textFile.exists()) {
            GuiUtilities.showMessageDialog(getOwner(), String.format(resources.getString("noFile"), textFile));
            textFile = null;
            newProject.setTextFile(null);
        }//end textFile
        if (tagFile != null && !tagFile.exists()) {
            GuiUtilities.showMessageDialog(getOwner(), String.format(resources.getString("noFile"), tagFile));
            tagFile = null;
            newProject.setTagFile(null);
        }//end tagFile

        if (videoFile != null && audioFile == null && core.hasAudioSrteam(videoFile)) {
            audioFile = videoFile;
            newProject.setAudioFile(audioFile.getAbsolutePath());
        }

        if (newProject.getVideoOriginalFile() != null) {
            if (new File(path, newProject.getVideoOriginalFile()).exists()) {
                videoFile = new File(path, newProject.getVideoOriginalFile());
            }
        }

        if (oldProject.isEmptyProject()) {
            //si on est en chargement et que c'est un fichier audio et qu'il n'y a
            //pas de fichiers associés, on charge directement le fichier.
            if (newProject.isIndexesProject() || newProject.isAudioProject() || newProject.isVideoProject()
                    || newProject.isTextProject()) {
                performLoad(newProject);
                return;
            }//end chargement simple
        }

        hasIndex = (indexesFile != null);
        indexesButton.setEnabled(hasIndex);
        indexesButton.setSelected(hasIndex);

        hasData = (audioFile != null || (videoFile != null && core.hasAudioSrteam(videoFile)));
        audioButton.setEnabled(hasData);
        audioButton.setSelected(hasData);
        if (hasData && audioFile == null) {
            audioFile = videoFile;
        }

        hasVideo = (videoFile != null && core.hasVideoSrteam(videoFile));
        videoButton.setEnabled(hasVideo);
        videoButton.setSelected(hasVideo);

        hasText = (textFile != null);
        textButton.setEnabled(hasText);
        textButton.setSelected(hasText);

        this.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width - this.getWidth()) / 2, (dim.height - this.getHeight()) / 2);
        this.setVisible(true);
    }

//    public static void main(String[] args){
//        GuiUtilities.manageUI(true);
//        javax.swing.JFrame frame = new javax.swing.JFrame("test");
//        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//        ProcessingBar processingBar = new ProcessingBar();
//        File tempPath = new File("C:/Users/fabrice.au/AppData/Local/Temp");
//        File file = new File("C:/Users/fabrice.au/eeStudio/séance anglais.ees");
//        File mencoder = new File("C:/Users/fabrice.au/Documents/FlashProjects/MPlayer/mencoder.exe");
//        File mplayer = new File("C:/Users/fabrice.au/Documents/FlashProjects/MPlayer/mplayer.exe");
//        eestudio.utils.MEncoder encoder = new eestudio.utils.MEncoder(mencoder, mplayer);
//        try {
//            Core core = new Core(encoder);
//            ImportDialog dialog = new ImportDialog(frame, core,
//                    new Resources(), processingBar, tempPath);
//
//            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                @Override
//                public void windowClosing(java.awt.event.WindowEvent e) {
//                    System.exit(0);
//                }
//            });
//
//            dialog.showDialog(file);
//        } catch(Exception e) {
//            eestudio.utils.Edu4Logger.error(e);
//        }
//    }

}
