package eestudio.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.*;

import eestudio.Constants;
import eestudio.Core;
import eestudio.ProjectFiles;
import eestudio.utils.Utilities;

/*
 * v0.95.12: ajout de public void close()
 * 
 * v0.95.13: modif de ButtonGroup softGroup (var global -> var local)
 * 
 * v0.96: ajout de public void processBegin(boolean determinated)
 * v0.96: ajout de public void addProcessingBarWindowListener(WindowListener listener)
 * v0.96: modif de initComponents() [video pour easyLab en .avi, project.setSoft(soft);]
 * v0.96: modif de updateLanguage() [video pour easyLab en .avi]
 * 
 * v0.97: modif de ExportDialog(..) en ExportDialog(.., ProcessingBar processingBar)
 * v0.97: supp de public void addProcessingBarWindowListener(WindowListener listener)
 * v0.97: supp de public void processBegin(boolean determinated)
 * v0.97: supp de public void percentChanged(int percent)
 * 
 * v0.98: ajout de l'exportation en simle fichier
 * v0.98: ajout de private JRadioButton simpleExportButton;
 * v0.98: modif de initComponents() [simpleExport]
 * v0.98: modif de updateLanguage() [simpleExport]
 * v0.98: modif de showDialog(File file) [simpleExport]
 * 
 * v0.99: modif de ExportDialog(Window parent, List<Image> icones, ...) en
 *        ExportDialog(Window parent, ...)
 * 
 * v1.01: modif de initComponents() [use de GridBagLayout et de BackgroundPanel,
 *        ajout de MouseListener et Focus sur les élèments sélectionables,
 *        ajout de repaint sur les actions des boutons, gestion des fonctions
 *        non prises en charge par les baladeurs sur easyLab]
 * 
 * v1.01: modif de initComponents() [ajout de la gestion du fichier des tags sur
 *        la sélection de l'audio ou vidéo sur validButton]
 */

/**
 * Fenêtre de dialogue pour l'exportation.
 * 
 * @author Fabrice Alleau
 * @since version 0.95
 * @version 1.02
 */
public class ExportDialog extends JDialog {
    private static final long serialVersionUID = 9800L;

    /** Resources textuelles */
    private Resources resources;
    /** Noyau pour la création des index */
    private Core core;

    /** Barre de progression */
    private ProcessingBar processingBar;

    /** Logiciel de destination (easyLab ou commun) */
    private String soft;

    /** Fichier principal de l'exportation */
    private File file;

    /** Message pour l'exportation */
    private JLabel messageLabel;
    /** Message pour le choix du logiciel de destination */
    private JLabel softChoiceLabel;
    /** Message pour le choix des fichiers à exporter */
    private JLabel filesChoiceLabel;

    /** Boutton pour valider */
    private JButton validButton;
    /** Boutton pour annuler */
    private JButton cancelButton;

    /** Boutton pour le choix du logiciel easyLab */
    private JRadioButton easyLabButton;
    /** Boutton pour le choix des logiciels Vocalab3 et eeVision2 */
    private JRadioButton commonSoftButton;
    /** Boutton pour le choix des logiciels Vocalab3 et eeVision2 */
    private JRadioButton simpleExportButton;

    /** Boutton pour le choix des index */
    private JCheckBox indexesButton;
    /** Boutton pour le choix de la piste vidéo */
    private JCheckBox videoButton;
    /** Boutton pour le choix de la piste audio */
    private JCheckBox audioButton;
    /** Boutton pour le choix du texte associé */
    private JCheckBox textButton;
    /** Boutton pour le choix les soustitres */
    private JCheckBox subtitleButton;

    /**
     * Initialisation de la fenêtre.
     * 
     * @param parent la fenêtre parente.
     * @param core le noyau de l'application.
     * @param resources les resources textuelles.
     * @param soft le choix du logiciel initial.
     * @param processingBar barre de progression pour les traitements.
     * @since version 0.95 - version 0.99
     */
    public ExportDialog(Window parent, Core core, Resources resources,
            String soft, ProcessingBar processingBar) {
        super(parent, resources.getString("exportTitle"), DEFAULT_MODALITY_TYPE);

        this.resources = resources;
        this.core = core;
        this.processingBar = processingBar;
        this.soft = soft;

        initComponents();
    }

    /**
     * Initialisation des composants graphiques.
     * 
     * @since version 0.95 - version 1.02
     */
    private void initComponents() {
        int margin = 20;
        int panelWidth = 360;
        int width = panelWidth+2*margin;
        int height = 500;
        int offset = 40;

        messageLabel = new JLabel(resources.getString("exportMessage"));
        softChoiceLabel = new JLabel(resources.getString("softChoice"));
        filesChoiceLabel = new JLabel(resources.getString("saveChoice"));

        validButton = new JButton(resources.getString("valid"));
        cancelButton = new JButton(resources.getString("cancel"));

        ButtonGroup softGroup = new ButtonGroup();
        easyLabButton = new JRadioButton(resources.getString("easyLabChoice"));
        commonSoftButton = new JRadioButton(resources.getString("commonSoftChoice"));
        simpleExportButton = new JRadioButton(resources.getString("simpleChoice"));
        softGroup.add(commonSoftButton);
        softGroup.add(easyLabButton);
        softGroup.add(simpleExportButton);
        if(soft.contentEquals(Constants.EASYLAB))
            softGroup.setSelected(easyLabButton.getModel(), true);
        else if(soft.contentEquals(Constants.SIMPLE_EXPORT))
            softGroup.setSelected(simpleExportButton.getModel(), true);
        else
            softGroup.setSelected(commonSoftButton.getModel(), true);

        indexesButton = new JCheckBox(
                String.format(resources.getString("indexesFile"),
                Constants.indexesExtension));
        subtitleButton = new JCheckBox(
                String.format(resources.getString("subtitleFile"),
                soft.contentEquals(Constants.EASYLAB)
                    ? Constants.LRC_extension : Constants.SRT_extension));
        audioButton = new JCheckBox(
                String.format(resources.getString("audioFile"),
                Constants.audioDefaultExtension));
        videoButton = new JCheckBox(
                String.format(resources.getString("videoFile"),
                soft.contentEquals(Constants.EASYLAB)
                    ? Constants.AVI_extension : Constants.videoDefaultExtension));
        textButton = new JCheckBox(
                String.format(resources.getString("textFile"),
                Constants.textDefaultExtension));

        BackgroundPanel panel = new BackgroundPanel(width, height, 15);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setLayout(layout);

        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(2*margin, margin, margin, margin);
        constraints.anchor = GridBagConstraints.BASELINE;
        layout.setConstraints(messageLabel, constraints);
        panel.add(messageLabel);

        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        constraints.insets = new Insets(margin, offset, margin, margin);
        layout.setConstraints(softChoiceLabel, constraints);
        panel.add(softChoiceLabel);
        constraints.insets = new Insets(0, margin + offset, 0, margin);
        layout.setConstraints(commonSoftButton, constraints);
        panel.add(commonSoftButton);
        layout.setConstraints(easyLabButton, constraints);
        panel.add(easyLabButton);
        layout.setConstraints(simpleExportButton, constraints);
        panel.add(simpleExportButton);

        constraints.insets = new Insets(margin, offset, margin, margin);
        layout.setConstraints(filesChoiceLabel, constraints);
        panel.add(filesChoiceLabel);
        constraints.insets = new Insets(0, margin + offset, 0, margin);
        layout.setConstraints(indexesButton, constraints);
        panel.add(indexesButton);
        layout.setConstraints(videoButton, constraints);
        panel.add(videoButton);
        layout.setConstraints(audioButton, constraints);
        panel.add(audioButton);
        layout.setConstraints(subtitleButton, constraints);
        panel.add(subtitleButton);
        layout.setConstraints(textButton, constraints);
        panel.add(textButton);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(2*margin, margin, 2*margin, margin);
        constraints.anchor = GridBagConstraints.BASELINE;
        layout.setConstraints(validButton, constraints);
        panel.add(validButton);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
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

        commonSoftButton.addMouseListener(mouseListener);
        easyLabButton.addMouseListener(mouseListener);
        simpleExportButton.addMouseListener(mouseListener);
        indexesButton.addMouseListener(mouseListener);
        audioButton.addMouseListener(mouseListener);
        videoButton.addMouseListener(mouseListener);
        textButton.addMouseListener(mouseListener);
        subtitleButton.addMouseListener(mouseListener);
        commonSoftButton.addFocusListener(focusListener);
        easyLabButton.addFocusListener(focusListener);
        simpleExportButton.addFocusListener(focusListener);
        indexesButton.addFocusListener(focusListener);
        audioButton.addFocusListener(focusListener);
        videoButton.addFocusListener(focusListener);
        textButton.addFocusListener(focusListener);
        subtitleButton.addFocusListener(focusListener);

        ActionListener softAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getContentPane().repaint();
                if(easyLabButton.isSelected()) {
                    if(core.hasStudentRecordIndex()) {
                        int option = GuiUtilities.showOptionDialog(getOwner(),
                                resources.getString("easyLabCompatibility"),
                                null, null);
                        if(option == GuiUtilities.YES_OPTION) {
                            soft = Constants.EASYLAB;
                        }
                        else {
                            if(soft.contentEquals(Constants.SIMPLE_EXPORT))
                                simpleExportButton.setSelected(true);
                            else
                                commonSoftButton.setSelected(true);
                        }
                    }
                    else
                        soft = Constants.EASYLAB;
                }
                else if(simpleExportButton.isSelected())
                    soft = Constants.SIMPLE_EXPORT;
                else
                    soft = Constants.COMMON_SOFT;

                videoButton.setText(
                        String.format(resources.getString("videoFile"),
                        soft.contentEquals(Constants.EASYLAB)
                        ? Constants.AVI_extension : Constants.videoDefaultExtension));
                textButton.setText(
                        String.format(resources.getString("textFile"),
                        soft.contentEquals(Constants.EASYLAB)
                        ? Constants.TXT_extension : Constants.textDefaultExtension));
                subtitleButton.setText(
                        String.format(resources.getString("subtitleFile"),
                        soft.contentEquals(Constants.EASYLAB)
                        ? Constants.LRC_extension : Constants.SRT_extension));

                boolean hasIndex = (core.getIndexesCount() > 0)
                        && !soft.contentEquals(Constants.SIMPLE_EXPORT);
                indexesButton.setEnabled(hasIndex);
                indexesButton.setSelected(hasIndex);
            }
        };

        commonSoftButton.addActionListener(softAction);
        easyLabButton.addActionListener(softAction);
        simpleExportButton.addActionListener(softAction);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                getContentPane().repaint();
                close();
            }
        });

        validButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                getContentPane().repaint();
                final ProjectFiles project = new ProjectFiles();
                project.setSoft(soft);

                if(indexesButton.isSelected()) {
                    project.setIndexesFile(Constants.indexesExtension);
                }
                if(audioButton.isSelected()) {
                    project.setAudioFile(Constants.audioDefaultExtension);
                }
                if(videoButton.isSelected()) {
                    project.setVideoFile((soft.contentEquals(Constants.EASYLAB)
                            ? Constants.AVI_extension : Constants.videoDefaultExtension));
                }
                if(textButton.isSelected()) {
                    project.setTextFile((soft.contentEquals(Constants.EASYLAB)
                            ? Constants.TXT_extension : Constants.textDefaultExtension));
                }
                if(subtitleButton.isSelected()) {
                    project.setSubtitleFile((soft.contentEquals(Constants.EASYLAB)
                            ? Constants.LRC_extension : Constants.SRT_extension));
                }
                if(audioButton.isSelected() || videoButton.isSelected()) {
                    if(!core.getTags().isEmpty())
                        project.setTagFile(Constants.tagExtension);
                }

                if(soft.contentEquals(Constants.SIMPLE_EXPORT)) {
                    file = new File(file.getParentFile(),
                            Utilities.getNameWithoutExtension(file));
                }

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        processingBar.processBegin(true,
                                resources.getString("conversionTitle"),
                                resources.getString("conversionMessage"),
                                (file == null) ? null : file.getAbsolutePath());
                        boolean success = core.saveProject(file, project);
                        close();
                    }
                });
                thread.start();
            }
        });//end addActionListener
    }//end initComponents()

    /**
     * Modification des textes pour un changement de langue.
     * 
     * @since version 0.95 - version 0.98
     */
    public void updateLanguage() {
        this.setTitle(resources.getString("exportTitle"));
        messageLabel.setText(resources.getString("exportMessage"));
        softChoiceLabel.setText(resources.getString("softChoice"));
        filesChoiceLabel.setText(resources.getString("saveChoice"));

        validButton.setText(resources.getString("valid"));
        cancelButton.setText(resources.getString("cancel"));

        easyLabButton.setText(resources.getString("easyLabChoice"));
        commonSoftButton.setText(resources.getString("commonSoftChoice"));
        simpleExportButton.setText(resources.getString("simpleChoice"));

        indexesButton.setText(String.format(resources.getString("indexesFile"),
                Constants.indexesExtension));
        subtitleButton.setText(String.format(resources.getString("subtitleFile"),
                soft.contentEquals(Constants.EASYLAB)
                    ? Constants.LRC_extension : Constants.SRT_extension));
        audioButton.setText(String.format(resources.getString("audioFile"),
                Constants.audioDefaultExtension));
        videoButton.setText(String.format(resources.getString("videoFile"),
                soft.contentEquals(Constants.EASYLAB)
                    ? Constants.AVI_extension : Constants.videoDefaultExtension));
        textButton.setText(String.format(resources.getString("textFile"),
                soft.contentEquals(Constants.EASYLAB)
                    ? Constants.TXT_extension : Constants.textDefaultExtension));
    }

    /**
     * Ferme la fenêtre et les resources associées.
     * 
     * @since version 0.95.12
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
     * @since version 0.95 - version 0.98
     */
    public void showDialog(File file) {
        this.file = file;

        if(!Utilities.isEdu4File(file)) {
            this.file = Utilities.returnFileWithExtension(file, Constants.edu4Extension);
        }

        //Vérification si le fichier de sauvegarde existe déjà
        if(this.file.exists()) {
            int choix = GuiUtilities.showOptionDialog(getOwner(),
                    String.format(resources.getString("eraseFile"), this.file),
                    null, null);

            if(choix != GuiUtilities.YES_OPTION)
                return;
        }

        boolean hasIndex = (core.getIndexesCount() > 0);
        indexesButton.setEnabled(hasIndex && !soft.contentEquals(Constants.SIMPLE_EXPORT));
        indexesButton.setSelected(hasIndex && !soft.contentEquals(Constants.SIMPLE_EXPORT));
        subtitleButton.setEnabled(hasIndex);
        subtitleButton.setSelected(hasIndex);

        boolean hasData = (core.getRecordTimeMax() > 0);
        audioButton.setEnabled(hasData);
        audioButton.setSelected(hasData);

        boolean hasVideo = core.hasVideo();
        videoButton.setEnabled(hasVideo);
        videoButton.setSelected(hasVideo);

        boolean hasText = core.hasText();
        textButton.setEnabled(hasText);
        textButton.setSelected(hasText);

        this.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width-this.getWidth())/2,
                (dim.height-this.getHeight())/2);
        this.setVisible(true);
    }//end showDialog(File file)

//    public static void main(String[] args){
//        GuiUtilities.manageUI();
//        javax.swing.JFrame frame = new javax.swing.JFrame("test");
//        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//        ProcessingBar processingBar = new ProcessingBar();
//        File file = new File("C:/Users/fabrice.au/eeStudio/test.ees");
//        try {
//            Core core = new Core(null);
//            ExportDialog dialog = new ExportDialog(frame, core,
//                    new Resources(), Constants.COMMON_SOFT, processingBar);
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

}//end
