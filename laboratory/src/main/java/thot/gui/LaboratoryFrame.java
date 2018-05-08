package thot.gui;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.DefaultEditorKit;

import thot.LaboratoryCore;
import thot.LaboratoryListener;
import thot.labo.gui.EraseDialog;
import thot.labo.gui.ExportDialog;
import thot.labo.gui.ImportDialog;
import thot.labo.gui.TimeIndexSlider;
import thot.labo.index.Index;
import thot.supervision.CommandXMLUtilities;
import thot.utils.Constants;
import thot.utils.Utilities;

/*
 * resources:
 *  laboratoryTitle
 *  mediaLabel, audioLabel, textParamLabel, textLabel
 *  help, mini, close, appel
 *  mediaLoad, mediaErase, mediaMute, mediaVolume
 *  audioLoad, back, play, pause, record, audioErase, audioSave, timeMax
 *  modeAuto, modeManuel, audioMute, audioVolume
 *  zoom+, zoom-, textLoad, textSave, textErase, textMute, textSpeed
 *  languagesTitle, languagesMessage
 *  imageFilter, audioFilter, mediaFilter, projectFilter, textFilter
 *  eraseMedia, eraseAudio, eraseText, changeTime, confirmClose
 *  fileFormatNotSupported (%s)
 *  cut, copy, paste
 *  valid, cancel, yes, no
 */

/**
 * Fenêtre principale du poste élève.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class LaboratoryFrame extends JFrame {
    private static final long serialVersionUID = 18200L;

    /**
     * Référence sur le noyau de l'application.
     */
    private final LaboratoryCore core;
    /**
     * Resources pour les textes.
     */
    private Resources resources;
    /**
     * Répertoire de l'utilisateur.
     */
    private final File userHome;

    /**
     * Etat du contrôle de commandes.
     */
    private boolean freeze = false;

    /**
     * Hauteur du logo.
     */
    private int logoHeight = 40;
    /**
     * Hauteur d'un bouton.
     */
    private int buttonHeight = 80;
    /**
     * Largeur de la fenêtre vidéo.
     */
    private int canvasWidth = 588;
    /**
     * Hauteur de la fenêtre vidéo.
     */
    private int canvasHeight = 424;
    /**
     * Taille du texte du module texte.
     */
    private int textSize = 20;

    /**
     * Mode plein écran actif.
     */
    private boolean fullscreen = false;
    /**
     * Position et dimension de la fenêtre vidéo en mode normal.
     */
    private Rectangle videoBounds;
    /**
     * Position et dimension de la fenêtre principale en mode normal.
     */
    private Rectangle frameBounds;
    /**
     * Position et dimension pour la fenêtre plein écran.
     */
    private Rectangle screenBounds;
    /**
     * Device de l'écran utilisé pour le plein écran.
     */
    private GraphicsDevice device = null;

    /**
     * Canvas pour l'affichage de la vidéo.
     */
    private VideoCanvas videoCanvas;
    /**
     * Largeur de la fenêtre vidéo.
     */
    private int textEditorWidth = 335;
    /**
     * Hauteur de la fenêtre vidéo.
     */
    private int textEditorHeight = 612;

    /**
     * Menu pour le bouton droit de la souris sur la zone de texte.
     */
    private JPopupMenu textPopupMenu;
    /**
     * Bouton appel professeur.
     */
    private JButton helpDemandButton;
    /**
     * Bouton de changement de langue.
     */
    private JButton helpButton;
    /**
     * Bouton de minimisation.
     */
    private JButton miniButton;
    /**
     * Bouton de fermeture.
     */
    private JButton closeButton;

    /**
     * Bouton de chargement d'un fichier texte.
     */
    private JButton loadButton;
    /**
     * Bouton de sauvegarde du bloc-notes.
     */
    private JButton saveButton;
    /**
     * Bouton d"effacement du bloc-notes.
     */
    private JButton eraseButton;

    /**
     * Bouton de retour en début de bande.
     */
    private JButton returnButton;
    /**
     * Bouton de lecture.
     */
    private JButton playButton;
    /**
     * Bouton de pause.
     */
    private JButton pauseButton;
    /**
     * Bouton d'enregistrement.
     */
    private JButton recordButton;

    /**
     * Affichage du temps.
     */
    private JLabel timeCount;
    /**
     * Bouton pour mettre la piste multimédia en sourdine.
     */
    private JButton mediaMuteButton;
    /**
     * Controle du volume multimédia.
     */
    private VolumeSlider mediaVolume;
    /**
     * Bouton pour mettre la piste élève en sourdine.
     */
    private JButton audioMuteButton;
    /**
     * Controle du volume de la piste élève.
     */
    private VolumeSlider audioVolume;
    /**
     * Barre de défilement du temps.
     */
    private TimeIndexSlider timeIndexSlider;

    /**
     * Bouton de modification de la durée de la bande.
     */
    private JButton timeMaxButton;
    /**
     * Bouton pour le basculement automatique/manuel sur les index multimédia.
     */
    private JButton indexesModeButton;
    /**
     * Bouton pour augmenter la taille du texte.
     */
    private JButton textZoomPlusButton;
    /**
     * Bouton pour diminuer la taille du texte.
     */
    private JButton textZoomMinusButton;

    /**
     * Panneau du menu multimédia.
     */
    private FilterPanel mediaMenu;
    /**
     * Panneau du menu de la piste élève.
     */
    private FilterPanel audioMenu;
    /**
     * Panneau du menu des paramétrage du texte.
     */
    private FilterPanel textParamMenu;
    /**
     * Zone de texte.
     */
    private EditorArea textArea;

    /**
     * Sélectionneur de fichier.
     */
    private final FileChooser chooser;

    /**
     * Fenêtre affichant une barre de progression.
     */
    private ProcessingBar processingBar;
    /**
     * Fenêtre d'importation de fichiers.
     */
    private ImportDialog importDialog;
    /**
     * Fenêtre d'exportation du projet.
     */
    private ExportDialog exportDialog;
    /**
     * Fenêtre pour la suppression d'éléments.
     */
    private EraseDialog eraseDialog;

    /**
     * Initialisation de l'interface graphique.
     *
     * @param core le noyau du laboratoire de langue.
     * @param resources le gestionnaires de resources textuelles
     * @param userHome
     */
    public LaboratoryFrame(LaboratoryCore core, Resources resources, File userHome) {
        super(resources.getString("laboratoryTitle"));
        this.core = core;
        this.resources = resources;
        this.userHome = userHome;

        this.setUndecorated(true);
        this.setResizable(false);
        this.setIconImages(GuiUtilities.getIcons());

        initFrame();

        chooser = new FileChooser(this, userHome, true);
        //création du répertoire utilisateur
        File tempPath = new File(System.getProperty("java.io.tmpdir"), "Siclic");
        tempPath.mkdirs();
        processingBar = new ProcessingBar(this, null);
        importDialog = new ImportDialog(this, core, resources, processingBar, tempPath);
        exportDialog = new ExportDialog(this, core, resources, processingBar);
        eraseDialog = new EraseDialog(this, core, resources, processingBar);

        addButtonListeners();
        addCoreListener();
    }

    public void showApplication() {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Dimension dim = graphicsEnvironment.getMaximumWindowBounds().getSize();

        GraphicsDevice[] deviceList = graphicsEnvironment.getScreenDevices();
        if (deviceList.length > 1) {
            device = graphicsEnvironment.getDefaultScreenDevice();
            JFrame frame = new JFrame();
            frame.setUndecorated(true);
            for (GraphicsDevice gd : deviceList) {
                gd.setFullScreenWindow(frame);
                if (gd == device) {
                    screenBounds = frame.getBounds();
                }
                gd.setFullScreenWindow(null);
            }
            frame.dispose();
        }

        if (Utilities.LINUX_PLATFORM && dim.height < 800) {
            dim = new Dimension(getScreenBounds().getSize());
        }

        if (dim.height < 768) {
            canvasHeight -= (768 - dim.height);
            textEditorHeight -= (768 - dim.height);
        } else {
            dim.height = 768;
        }

        if (dim.width < 1024) {
            canvasWidth -= (1024 - dim.width);
        } else {
            dim.width = 1024;
        }

        this.setVisible(true);
        core.mediaPlayerSetVideoOutput(videoCanvas);
        this.setSize(dim);
    }

    /**
     * Ajout des méthodes pour le listener. Utilisation de SwingUtilities.invokeLater(new Runnable()) pour que les
     * modifications touchant l'interface graphique soit appelé par l'EDT.
     */
    private void addCoreListener() {
        core.addListener(new LaboratoryListener() {
            @Override
            public void languageChanged(final String language) {
                SwingUtilities.invokeLater(() -> {
                    if (language != null) {
                        changeLanguage(language);
                    }
                });
            }

            @Override
            public void stateChanged(final int running, final int mediaType) {
                if (mediaType == Constants.UNLOAD) {
                    videoCanvas.setImage(null);
                }
                if (!freeze) {
                    SwingUtilities.invokeLater(() -> updateButtons(running, mediaType));
                }
            }

            @Override
            public void indexesModeChanged(boolean indexesMode) {
                if (!freeze) {
                    updateIndexesMode(indexesMode);
                }
            }

            @Override
            public void recordTimeMaxChanged(long recordTimeMax) {
                final String display = timeCount.getText()
                        .substring(0, timeCount.getText().indexOf('/'))
                        + String.format("/ %1$d:%2$02d",
                        (recordTimeMax / 1000) / 60, (recordTimeMax / 1000) % 60);

                SwingUtilities.invokeLater(() -> timeCount.setText(display));

                //la réprésentation graphique des index à changée
                if (core.getMediaIndexesCount() > 0
                        || core.getRecordIndexesCount() > 0) {
                    SwingUtilities.invokeLater(() -> {
                        timeIndexSlider.repaint();
                        getStudentFrame().repaint();
                    });
                }
            }

            @Override
            public void timeChanged(final long time) {
                timeIndexSlider.setPosition((double) time / core.getRecordTimeMax());
                updateTimeCounter(time);
            }

            @Override
            public void currentIndexChanged(Index index) {
                String subtitle = null;
                if (index != null) {
                    subtitle = index.getSubtitle();
                }
                videoCanvas.setSubtitle(subtitle);
            }

            @Override
            public void textLoaded(final String text) {
                SwingUtilities.invokeLater(() -> textArea.setText(text));
            }

            @Override
            public void studentControlChanged(final boolean freeze) {
                freezeCommands(freeze);
            }

            @Override
            public void fullScreenChanged(final boolean fullscreen) {
                SwingUtilities.invokeLater(() -> {
                    if (core.getMediaType() == Constants.VIDEO_FILE) {
                        SwingUtilities.invokeLater(() -> {
//                                    setFullscreen(fullscreen);
                            videoCanvas.setFullScreen(fullscreen);
                        });
                    } else if (core.getMediaType() == Constants.IMAGE_FILE) {
                        videoCanvas.setFullScreen(fullscreen);
                    }
                });
            }

            @Override
            public void indexesChanged() {
                if (core.getMediaIndexesCount() == 0) {
                    indexesModeButton.setEnabled(false);
                    videoCanvas.setSubtitle(null);
                } else {
                    if (!freeze && core.getRunningState() == Constants.PAUSE) {
                        indexesModeButton.setEnabled(
                                core.checkMultimediaIndexesValidity() == 0);
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    timeIndexSlider.repaint();
                    getStudentFrame().repaint();
                });
            }

            @Override
            public void newMessage(final String message) {
                SwingUtilities.invokeLater(() -> showMessageDialog(message));
            }

            @Override
            public void imageChanged(Image image) {
                videoCanvas.setImage(image);
                SwingUtilities.invokeLater(() -> videoCanvas.repaint());
            }

            @Override
            public void mediaVolumeChanged(final int volume) {
                SwingUtilities.invokeLater(() -> {
                    mediaVolume.setPosition(volume / 100.0);
                    updateMediaMuteMode(volume == 0);
                });
            }

            @Override
            public void audioVolumeChanged(final int volume) {
                SwingUtilities.invokeLater(() -> {
                    audioVolume.setPosition(volume / 100.0);
                    updateAudioMuteMode(volume == 0);
                });
            }

            @Override
            public void helpDemandSuccess(boolean success) {
                if (success) {
                    updateHelpDemandButton(true);
                    new Thread("helpDemand") {
                        @Override
                        public void run() {
                            Utilities.waitInMillisecond(5000);
                            updateHelpDemandButton(false);
                        }
                    }.start();
                }
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
            }

            @Override
            public void processDoubleStatusChanged(Object source, boolean doubleStatus) {
            }
        });
    }

    /**
     * Initialise la fenêtre principale.
     */
    private void initFrame() {
        //initialisation de la sortie vidéo
        videoCanvas = new VideoCanvas() {
            private static final long serialVersionUID = 18300L;

            @Override
            protected void fireFullScreenStateChanged(boolean fullscreen) {
                setFullscreen(fullscreen);
            }
        };
        videoCanvas.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        videoCanvas.setBounds(0, 0, canvasWidth, canvasHeight);
        videoCanvas.setDefaultImage(GuiUtilities.getImage("defaultImage"));
        videoCanvas.setDefaultImage(null);

        //Zone de texte
        textArea = new EditorArea(core.getStyledEditorKit(),
                core.getStyledDocument(), resources);
        textArea.setSizeMax(textEditorWidth, textEditorHeight);
        textArea.setSelectedTextColor(Color.RED);
        textArea.setSelectionColor(GuiUtilities.TRANSPARENT_COLOR);
        textArea.setBackground(GuiUtilities.TRANSPARENT_COLOR);
//        textArea.setBorder(null);
        Font font = GuiUtilities.defaultTextFont;
        textArea.setFont(font);

        //Menu pour le bouton droit de la souris sur la zone de texte.
        initTextPopupMenu();


        //Boutons généraux
        helpButton = getButton("help", false);
        miniButton = getButton("mini", false);
        closeButton = getButton("close", false);

        //Bouton appel professeur
        helpDemandButton = getButton("appel", false);

        //Boutons principaux
        loadButton = getButton("textLoad", true);
        saveButton = getButton("textSave", true);
        eraseButton = getButton("textErase", true);
        returnButton = getButton("back", true);
        playButton = getButton("play", true);
        pauseButton = getButton("pause", true);
        recordButton = getButton("record", true);

        //volume multimédia
        mediaMuteButton = getButton("mediaMute", true);
        mediaVolume = new VolumeSlider(this, mediaMuteButton,
                GuiUtilities.getImage("volumeImage"),
                GuiUtilities.getImage("volumeCursorImage")) {
            private static final long serialVersionUID = 54L;

            @Override
            public void setValue(double position) {
                core.mediaSetVolume((int) (position * 100));
                updateMediaMuteMode(core.mediaGetVolume() == 0);
                setPosition((double) core.mediaGetVolume() / 100);
            }

            @Override
            public void toggleMute() {
                core.mediaToggleMute();
                updateMediaMuteMode(core.mediaGetVolume() == 0);
                setPosition((double) core.mediaGetVolume() / 100);
            }
        };
        mediaVolume.setPosition(core.mediaGetVolume() / 100.0);
        mediaVolume.setToolTipText(resources.getString("mediaVolume"));

        // volume audio
        audioMuteButton = getButton("audioMute", true);
        audioVolume = new VolumeSlider(this, audioMuteButton,
                GuiUtilities.getImage("volumeImage"),
                GuiUtilities.getImage("volumeCursorImage")) {
            private static final long serialVersionUID = 54L;

            @Override
            public void setValue(double position) {
                core.audioSetVolume((int) (position * 100));
                updateAudioMuteMode(core.audioGetVolume() == 0);
                setPosition((double) core.audioGetVolume() / 100);
            }

            @Override
            public void toggleMute() {
                core.audioToggleMute();
                updateAudioMuteMode(core.audioGetVolume() == 0);
                setPosition((double) core.audioGetVolume() / 100);
            }
        };
        audioVolume.setPosition(core.audioGetVolume() / 100.0);
        audioVolume.setToolTipText(resources.getString("audioVolume"));

        JPanel timePanel = new ImagePanel(
                GuiUtilities.getImage("timeCountImage"));
        timeCount = new JLabel("00:00 / 00:00");
//        timeCount.setPreferredSize(new Dimension(100, 15));
        timeCount.setHorizontalAlignment(JLabel.CENTER);
        timeCount.setVerticalAlignment(JLabel.TOP);
        timeCount.setForeground(Color.WHITE);
        timePanel.add(timeCount);

        //Barre de défilement du temps et des index
        timeIndexSlider = new TimeIndexSlider(core, this, resources, canvasWidth) {
            private static final long serialVersionUID = 54L;

            @Override
            public void releasedPosition(double position) {
                core.setPosition(position);
            }

            @Override
            public void draggedPosition(double position) {
                long time = (long) (position * core.getRecordTimeMax());
                updateTimeCounter(time);
            }
        };

        //Boutons pout le module texte
        textZoomPlusButton = getButton("zoom+", true);
        textZoomMinusButton = getButton("zoom-", true);

        timeMaxButton = getButton("timeMax", true);
        indexesModeButton = getButton("modeManuel", false);

        //constitution du menu principal
        JPanel mainMenu = new JPanel();
        Insets insets = new Insets(0, -3, 0, -3);
        helpButton.setMargin(insets);
        miniButton.setMargin(insets);
        closeButton.setMargin(insets);
        mainMenu.setLayout(new BoxLayout(mainMenu, BoxLayout.X_AXIS));
        mainMenu.add(helpButton);
        mainMenu.add(miniButton);
        mainMenu.add(closeButton);
        mainMenu.setBackground(GuiUtilities.TRANSPARENT_COLOR);

        Dimension dim = new Dimension(canvasWidth, buttonHeight);
        //Constitution du menu du module multimédia
        mediaMenu = new FilterPanel(
                resources.getString("mediaLabel"), FilterPanel.RIGHT, dim, 32);
        mediaMenu.add(timePanel);
        mediaMenu.add(loadButton);
        mediaMenu.add(saveButton);
        mediaMenu.add(eraseButton);

        dim = new Dimension(canvasWidth, 2 * buttonHeight);
        //Constitution du menu du module audio
        audioMenu = new FilterPanel(
                resources.getString("audioLabel"), FilterPanel.RIGHT, dim, 100);

        audioMenu.setYOffset(40);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        audioMenu.setLayout(gridbag);

        c.gridwidth = 1;
        gridbag.setConstraints(playButton, c);
        audioMenu.add(playButton);
        gridbag.setConstraints(pauseButton, c);
        audioMenu.add(pauseButton);
        gridbag.setConstraints(recordButton, c);
        audioMenu.add(recordButton);
        gridbag.setConstraints(mediaMuteButton, c);
        audioMenu.add(mediaMuteButton);

        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(mediaVolume, c);
        audioMenu.add(mediaVolume);

        c.gridwidth = 1;
        gridbag.setConstraints(returnButton, c);
        audioMenu.add(returnButton);
        gridbag.setConstraints(timeMaxButton, c);
        audioMenu.add(timeMaxButton);
        gridbag.setConstraints(indexesModeButton, c);
        audioMenu.add(indexesModeButton);
        gridbag.setConstraints(audioMuteButton, c);
        audioMenu.add(audioMuteButton);

        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(audioVolume, c);
        audioMenu.add(audioVolume);


        dim = new Dimension(textEditorWidth, buttonHeight);
        //Constitution du menu du module texte
        textParamMenu = new FilterPanel(
                resources.getString("textParamLabel"), FilterPanel.LEFT, dim, 32);
        textParamMenu.add(textZoomPlusButton);
        textParamMenu.add(textZoomMinusButton);
        textParamMenu.add(helpDemandButton);


        //Initialisation de l'état des boutons
        loadButton.setEnabled(true);
        saveButton.setEnabled(true);
        eraseButton.setEnabled(true);
        returnButton.setEnabled(true);
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        recordButton.setEnabled(true);

        mediaMuteButton.setEnabled(true);
        audioMuteButton.setEnabled(true);
        timeMaxButton.setEnabled(true);
        indexesModeButton.setEnabled(false);
        textZoomPlusButton.setEnabled(true);
        textZoomMinusButton.setEnabled(true);

        JPanel logo = new ImagePanel(
                GuiUtilities.getImage("logoImage"), -1, logoHeight);

        JLabel softLabel = new JLabel(Constants.softName, JLabel.LEFT);
        softLabel.setFont(font);


        JPanel panel = new ImagePanel(GuiUtilities.getImage("backgroundImage"));
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setLayout(layout);

        constraints.weightx = 0.0;
        constraints.weighty = 0.0;

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.anchor = GridBagConstraints.FIRST_LINE_END;
        constraints.insets = new Insets(6, 0, 6, 10);
        layout.setConstraints(mainMenu, constraints);
        panel.add(mainMenu);

        constraints.anchor = GridBagConstraints.FIRST_LINE_START;

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(15, 33, 0, 0);
        layout.setConstraints(mediaMenu, constraints);
        panel.add(mediaMenu);

        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.insets = new Insets(15, 35, 0, 33);
        layout.setConstraints(textParamMenu, constraints);
        panel.add(textParamMenu);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(0, 33, 0, 0);
        layout.setConstraints(videoCanvas, constraints);
        panel.add(videoCanvas);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.insets = new Insets(10, 33, 0, 0);
        layout.setConstraints(timeIndexSlider, constraints);
        panel.add(timeIndexSlider);

        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0, 35, 10, 33);
        layout.setConstraints(textArea, constraints);
        panel.add(textArea);

        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 1;
        constraints.ipadx = 0;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0, 33, 0, 0);
        layout.setConstraints(audioMenu, constraints);
        panel.add(audioMenu);

        this.setSize(panel.getPreferredSize());
        this.getContentPane().add(panel);
        this.validate();

        updateTimeCounter(0);
    }

    /**
     * Ajout des méthodes sur les boutons.
     *
     * @since version 1.83
     */
    private void addButtonListeners() {
        //anonymous listeners pour le bouton de changement de langue
        helpButton.addActionListener(event -> {
            String[] languages = {"Deutsch", "English", "Español", "Français", "Italiano"};
//                String[] languages = {"Català", "Deutsch", "English", "Español", "Euskara", "Français", "Italiano"};
            String language = (String) showInputDialog(
                    resources.getString("languagesMessage"),
                    resources.getString("languagesTitle"), languages, "Français");

            if (language != null) {
                changeLanguage(language);
            }
        });

        //anonymous listeners pour le bouton minimiser
        miniButton.addActionListener(event -> setExtendedState(JFrame.ICONIFIED));

        //anonymous listeners pour le bouton de fermeture
        closeButton.addActionListener(event -> {
            int choix = showOptionDialog(resources.getString("confirmClose"));
            if (choix == GuiUtilities.YES_OPTION) {
                core.closeApplication();
            }
        });

        //anonymous listeners pour le bouton de d'appel prof
        helpDemandButton.addActionListener(event -> core.sendHelpDemand());

        //anonymous listeners pour le bouton de changement de mode
        indexesModeButton.addActionListener(event -> core.setIndexesMode(!core.getIndexesMode()));

        //anonymous listeners pour le boutton Charger du module multimédia
        loadButton.addActionListener(event -> {
            //création dun nouveau filechosser avec un filtre pour les extension de fichiers
            chooser.addChoosableFileFilter(resources.getString("imageFilter"), Constants.imageExtension);
            chooser.addChoosableFileFilter(resources.getString("textFilter"), Constants.textExtension);
            chooser.addChoosableFileFilter(resources.getString("audioFilter"), Constants.audioExtension);
            chooser.addChoosableFileFilter(resources.getString("mediaFilter"), Constants.videoExtension);
            chooser.addChoosableFileFilter(resources.getString("projectFilter"), Constants.projectExtension);
            chooser.setAcceptAllFileFilterUsed(true);

            //affiche la boite de dialogue et récupère le nom du fichier si l'action à été validée
            File file = chooser.getSelectedFile(FileChooser.LOAD);
            if (file != null) {
                importDialog.showDialog(file);
            }
        });

        //anonymous listeners pour le boutton Effacer du module texte
        eraseButton.addActionListener(event -> {
            int choix = showOptionDialog(resources.getString("eraseText"));
            if (choix == GuiUtilities.YES_OPTION) {
                eraseDialog.showDialog();
            }
        });

        //anonymous listeners pour le bouton Sauvegarder du module audio
        saveButton.addActionListener(event -> {
            //création dun nouveau filechosser avec un filtre pour les fichier texte
            chooser.addChoosableFileFilter(resources.getString("textFilter"), Constants.textExtension);
            chooser.addChoosableFileFilter(resources.getString("audioFilter"), Constants.audioExtension);
            chooser.addChoosableFileFilter(resources.getString("projectFilter"), Constants.projectExtension);

            //affiche la boite de dialogue et enregistre les données tapées si un fichier a été validé
            File file = chooser.getSelectedFile(FileChooser.SAVE);
            if (file != null) {
                exportDialog.showDialog(file);
            }
        });

        //anonymous listeners pour le bouton Lecture du module audio
        playButton.addActionListener(event -> core.audioPlay());

        //anonymous listeners pour le bouton Stop du module multimédia
        pauseButton.addActionListener(event -> core.audioPause());

        //anonymous listeners pour le bouton Enregistrement du module audio
        recordButton.addActionListener(event -> core.audioRecord());

        //anonymous listeners pour le bouton retour à zéro
        returnButton.addActionListener(event -> core.timeToZero());

        //anonymous listeners pour le bouton Sauvegarder du module audio
        timeMaxButton.addActionListener(event -> {
            String timeString = showInputDialog(
                    resources.getString("changeTime"),
                    Long.toString(core.getRecordTimeMax() / 1000 / 60));

            if (timeString != null) {
                int timeMax = Utilities.parseStringAsInt(timeString);

                if (timeMax > 0) {
                    //passage de minutes en millisecondes
                    timeMax = timeMax * 60 * 1000;
                    core.setRecordTimeMax(timeMax);
                }
            }
        });

        //anonymous listeners pour le boutton Effacer du module texte
        textZoomPlusButton.addActionListener(event -> {
            textSize += 4;
            if (textSize > 40) {
                textSize = 40;
            }
            Font font = new Font(GuiUtilities.defaultFontName, Font.PLAIN, textSize);
            textArea.setFont(font);
            textArea.grabFocus();
        });

        //anonymous listeners pour le boutton Effacer du module texte
        textZoomMinusButton.addActionListener(event -> {
            textSize -= 4;
            if (textSize == 0) {
                textSize = 4;
            }
            Font font = new Font(GuiUtilities.defaultFontName, Font.PLAIN, textSize);
            textArea.setFont(font);
            textArea.grabFocus();
        });

        //anonymous mouse listeners pour la zone de texte et montrer le menu.
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (SwingUtilities.isRightMouseButton(event)) {
                    textPopupMenu.show(textArea, event.getX(), event.getY());
                }
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                core.closeApplication();
            }
        });

        MouseAdapter mouseAdapter = new MouseAdapter() {
            private int mouseX = 0;
            private int mouseY = 0;

            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getXOnScreen();
                mouseY = e.getYOnScreen();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int x = getX() + e.getXOnScreen() - mouseX;
                int y = getY() + e.getYOnScreen() - mouseY;
                setLocation(x, y);
                mouseX = e.getXOnScreen();
                mouseY = e.getYOnScreen();
            }
        };

        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
    }

    /**
     * Met la composant de la vidéo en plein écran ou en mode normal.
     *
     * @param fullscreen l'état plein écran.
     *
     * @since version 1.83
     */
    private void setFullscreen(boolean fullscreen) {
        if (fullscreen == this.fullscreen) {
            return;
        }

        if (fullscreen) {
            if (device != getGraphicsConfiguration().getDevice()) {
                device = null;
            }
            videoBounds = videoCanvas.getBounds();
            frameBounds = this.getBounds();
            //la fenêtre doit être invisible et on doit mettre les limites de
            //la vidéo si on ne veut pas voir des récurences des boutons.
            if (device == null) {
                screenBounds = getScreenBounds();
            }

            this.setVisible(false);
            this.setAlwaysOnTop(true);
            this.setBounds(screenBounds);
            videoCanvas.setBounds(screenBounds);
            this.setVisible(true);
            this.fullscreen = true;
            //pour replacer correctement la vidéo avec les autres composants
            videoCanvas.setBounds(screenBounds);
        } else {
            this.fullscreen = false;
            this.setAlwaysOnTop(false);
            this.setBounds(frameBounds);
            videoCanvas.setBounds(videoBounds);
        }
    }

    @Override
    public void paint(Graphics g) {
        if (fullscreen) {
            videoCanvas.setBounds(screenBounds);
        } else {
            super.paint(g);
        }
    }

    /**
     * Retourne la taille de l'écran. Initialise screenBounds lors du premier appel
     *
     * @return la taille de l'écran.
     *
     * @since version 1.83
     */
    private Rectangle getScreenBounds() {
        if (screenBounds == null) {
            device = getGraphicsConfiguration().getDevice();
//            screenBounds = device.getDefaultConfiguration().getBounds();

            GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            JFrame frame = new JFrame();
            frame.setUndecorated(true);
            GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
            graphicsDevice.setFullScreenWindow(frame);
            screenBounds = frame.getBounds();
            frame.dispose();
        }

        return screenBounds;
    }

    /**
     * Change la langue de l'interface.
     *
     * @param language la nouvelle langue de l'interface.
     */
    private void changeLanguage(String language) {
        Locale locale = null;
        switch (language) {
            case "Français":
            case "fr":
                locale = Locale.FRENCH;
                break;
            case "English":
            case "en":
                locale = Locale.ENGLISH;
                break;
            case "Español":
            case "es":
                locale = new Locale("es");
                break;
            case "Deutsch":
            case "de":
                locale = Locale.GERMAN;
                break;
            case "Italiano":
            case "it":
                locale = Locale.ITALIAN;
                break;
            case "Català":
            case "ca":
                locale = new Locale("ca", "ES");
                break;
            case "Euskara":
            case "eu":
                locale = new Locale("eu");
                break;
        }

        if (locale != null) {
            changeLanguage(locale);
        }
    }

    /**
     * Change la langue de l'interface.
     *
     * @param locale la nouvelle langue de l'interface.
     */
    private void changeLanguage(Locale locale) {
        GuiUtilities.setDefaultLocale(locale);
        this.setLocale(locale);

        File languageFile = new File(userHome, "language.xml");
        Utilities.saveText(CommandXMLUtilities.getLanguageXML(locale.getLanguage()), languageFile);

        //internalisation des différents textes
        resources.updateLocale(locale);

        this.setTitle(resources.getString("laboratoryTitle"));

        //Boutons
        helpButton.setToolTipText(resources.getString("help"));
        miniButton.setToolTipText(resources.getString("mini"));
        closeButton.setToolTipText(resources.getString("close"));
        helpDemandButton.setToolTipText(resources.getString("appel"));

        //Boutons pour le module multimédia
        mediaMuteButton.setToolTipText(resources.getString("mediaMute"));
        mediaVolume.setToolTipText(resources.getString("mediaVolume"));

        //Boutons pour le module audio
        returnButton.setToolTipText(resources.getString("back"));
        playButton.setToolTipText(resources.getString("play"));
        pauseButton.setToolTipText(resources.getString("pause"));
        recordButton.setToolTipText(resources.getString("record"));
        timeMaxButton.setToolTipText(resources.getString("timeMax"));

        if (core.getIndexesMode()) {
            indexesModeButton.setToolTipText(resources.getString("modeAuto"));
        } else {
            indexesModeButton.setToolTipText(resources.getString("modeManuel"));
        }

        audioMuteButton.setToolTipText(resources.getString("audioMute"));
        audioVolume.setToolTipText(resources.getString("audioVolume"));

        //Boutons pout le module texte
        textZoomPlusButton.setToolTipText(resources.getString("zoom+"));
        textZoomMinusButton.setToolTipText(resources.getString("zoom-"));
        loadButton.setToolTipText(resources.getString("textLoad"));
        saveButton.setToolTipText(resources.getString("textSave"));
        eraseButton.setToolTipText(resources.getString("textErase"));

        mediaMenu.changeTitle(resources.getString("mediaLabel"));
        audioMenu.changeTitle(resources.getString("audioLabel"));
        textParamMenu.changeTitle(resources.getString("textParamLabel"));

        textArea.updateTexts(resources);
        chooser.updateLanguage();
        importDialog.updateLanguage();
        exportDialog.updateLanguage();

        timeIndexSlider.changePopupMenuText();
        initTextPopupMenu();

        this.repaint();
    }

    /**
     * Création d'un bouton avec son type.
     *
     * @param type le type de bouton
     * @param animation pour savoir si il y a des effets sur le bouton.
     *
     * @return le bouton créé.
     */
    public JButton getButton(String type, boolean animation) {
        JButton button = new JButton(GuiUtilities.getImageIcon(type + "Image"));

        button.setMargin(new Insets(0, 0, 0, 0));

        if (animation) {
            button.setDisabledIcon(GuiUtilities.getImageIcon(type + "ImageOff"));
//            button.setRolloverIcon(resources.getImageIcon(type + "ImageSurvol"));
        }

        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        button.setToolTipText(resources.getString(type));

        //anonymous listeners pour rafraichir la frame lors d'afichage des toolTip
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                repaint();
            }
        });

        button.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });

        //pour éviter d'avoir un reste d'image quand le programme désactive le bouton
        button.addPropertyChangeListener(evt -> repaint());

        return button;
    }

    /**
     * Initialise le popup menu sur la zone de texte.
     */
    private void initTextPopupMenu() {
        textPopupMenu = new JPopupMenu();

        Action action;

        //Action pour couper
        action = new DefaultEditorKit.CutAction();
        action.putValue(Action.NAME, resources.getString("cut"));
        textPopupMenu.add(action);

        //Action pour copier
        action = new DefaultEditorKit.CopyAction();
        action.putValue(Action.NAME, resources.getString("copy"));
        textPopupMenu.add(action);

        //Action pour coller
        action = new DefaultEditorKit.PasteAction();
        action.putValue(Action.NAME, resources.getString("paste"));
        textPopupMenu.add(action);

        textPopupMenu.addSeparator();

        //Action pour rendre le texte plus gros
        JMenuItem menuItem = new JMenuItem(resources.getString("zoom+"));
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                textSize += 4;
                if (textSize > 40) {
                    textSize = 40;
                }
                Font font = GuiUtilities.defaultTextFont.deriveFont(textSize);
                textArea.setFont(font);
            }
        });
        textPopupMenu.add(menuItem);

        //Action pour rendre le texte plus petit
        menuItem = new JMenuItem(resources.getString("zoom-"));
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                textSize -= 4;
                if (textSize == 0) {
                    textSize = 4;
                }
                Font font = GuiUtilities.defaultTextFont.deriveFont(textSize);
                textArea.setFont(font);
            }
        });
        textPopupMenu.add(menuItem);

        //Pour éviter des bugs graphiques
        textPopupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                getStudentFrame().repaint();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                getStudentFrame().repaint();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                getStudentFrame().repaint();
            }
        });
    }

    /**
     * Mise à jour du temps courant.
     *
     * @param time le temps en millisecondes.
     */
    private void updateTimeCounter(long time) {
        long timeMax = core.getRecordTimeMax();
        String display = String.format("%1$d:%2$02d / %3$d:%4$02d",
                (time / 1000) / 60, (time / 1000) % 60,
                (timeMax / 1000) / 60, (timeMax / 1000) % 60);
        timeCount.setText(display);
        timeCount.repaint();
    }

    /**
     * Met à jour l'état des boutons.
     *
     * @param runningState l'état dynamique du poste élève.
     * @param mediaType le type de média dans le module multimédia.
     */
    protected void updateButtons(int runningState, int mediaType) {
        boolean isUnload = (mediaType == Constants.UNLOAD);
        boolean isImage = (mediaType == Constants.IMAGE_FILE);
        boolean isPlaying = (runningState == Constants.PLAYING);
        boolean isRecording = (runningState == Constants.RECORDING);
        boolean isStop = (runningState == Constants.PAUSE);
        boolean isIndex = (core.getMediaIndexesCount() != 0)
                && (core.checkMultimediaIndexesValidity() == 0);
        boolean isAuto = core.getIndexesMode();

        playButton.setEnabled(isStop || (isRecording && !isAuto));
//        playButton.setEnabled(isStop || isRecording);
        pauseButton.setEnabled(isPlaying || isRecording);
        recordButton.setEnabled(isStop || (isPlaying && !isAuto));
//        recordButton.setEnabled(isStop || isPlaying);

        loadButton.setEnabled(isStop);
        saveButton.setEnabled(isStop);
        eraseButton.setEnabled(isStop);

        timeMaxButton.setEnabled(isStop && (isUnload || isImage));

        indexesModeButton.setEnabled(isStop && isIndex);
    }

    /**
     * Actualise le texte pour la lecture automatique.
     *
     * @param indexesMode le mode de lecture automatique.
     */
    private void updateIndexesMode(boolean indexesMode) {
        if (indexesMode) {
            indexesModeButton.setIcon(GuiUtilities.getImageIcon("modeAutoImage"));
            indexesModeButton.setToolTipText(resources.getString("modeAuto"));
        } else {
            indexesModeButton.setIcon(GuiUtilities.getImageIcon("modeManuelImage"));
            indexesModeButton.setToolTipText(resources.getString("modeManuel"));
        }
        indexesModeButton.repaint();
        this.repaint();
    }

    /**
     * Actualise le bouton de coupure du son du module multimédia.
     *
     * @param mute l'état du bouton.
     */
    private void updateMediaMuteMode(boolean mute) {
        if (mute) {
            mediaMuteButton.setIcon(GuiUtilities.getImageIcon("mediaMuteImageOff"));
        } else {
            mediaMuteButton.setIcon(GuiUtilities.getImageIcon("mediaMuteImage"));
        }
        mediaMuteButton.repaint();
        this.repaint();
    }

    /**
     * Actualise le bouton de coupure du son du module audio.
     *
     * @param mute l'état du bouton.
     */
    private void updateAudioMuteMode(boolean mute) {
        if (mute) {
            audioMuteButton.setIcon(GuiUtilities.getImageIcon("audioMuteImageOff"));
        } else {
            audioMuteButton.setIcon(GuiUtilities.getImageIcon("audioMuteImage"));
        }
        audioMuteButton.repaint();
        this.repaint();
    }

    /**
     * Actualise l'état du bouton d'appel.
     *
     * @param appel état de l'appel.
     */
    private void updateHelpDemandButton(boolean appel) {
        if (appel) {
            helpDemandButton.setIcon(GuiUtilities.getImageIcon("appelImageOn"));
        } else {
            helpDemandButton.setIcon(GuiUtilities.getImageIcon("appelImage"));
        }
        helpDemandButton.repaint();
        this.repaint();
    }

    /**
     * Gèle l'état des commandes élèves.
     */
    private void freezeCommands(boolean freeze) {
        this.freeze = freeze;
        if (freeze) {
            playButton.setEnabled(false);
            pauseButton.setEnabled(false);
            recordButton.setEnabled(false);
            loadButton.setEnabled(false);
            saveButton.setEnabled(false);
            eraseButton.setEnabled(false);

            timeMaxButton.setEnabled(false);
            indexesModeButton.setEnabled(false);
        } else {
            updateButtons(core.getRunningState(), core.getMediaType());
        }

        returnButton.setEnabled(!freeze);

        textZoomPlusButton.setEnabled(!freeze);
        textZoomMinusButton.setEnabled(!freeze);

        audioVolume.setEnabled(!freeze);
        mediaVolume.setEnabled(!freeze);
        timeIndexSlider.setEnabled(!freeze);

        helpButton.setEnabled(!freeze);
        miniButton.setEnabled(!freeze);
        closeButton.setEnabled(!freeze);
        helpDemandButton.setEnabled(!freeze);
    }

    /**
     * Affiche une boîte de dialogue avec une entrée texte.
     *
     * @param message le message à afficher.
     * @param initValue la valeur initiale ({@code null} si pas de valeur).
     *
     * @return le texte qui a été validé ou {@code null} si l'opération a été annulée.
     */
    protected String showInputDialog(String message, String initValue) {
        return (String) GuiUtilities.showInputDialog(this, message, null, initValue);
    }

    /**
     * Afficge une boîte de dialogue posant une question.
     *
     * @param message le message à afficher.
     *
     * @return {@code JOptionPane.YES_OPTION} si le bouton oui a été cliqué ou {@code JOptionPane.NO_OPTION} si c'est le
     *         bouton non.
     */
    protected int showOptionDialog(String message) {
        return GuiUtilities.showOptionDialog(this, message, null, null);
    }

    /**
     * Affiche un message à l'écran.
     *
     * @param message le message à afficher.
     */
    protected void showMessageDialog(String message) {
        GuiUtilities.showMessageDialog(this, message);
    }

    /**
     * Affiche une boîte de dialogue avec une liste de choix.
     *
     * @param message le message à afficher.
     * @param title le titre de la fenêtre.
     * @param values les valeurs que l'on peut sélectionnées.
     * @param initialValue la valeur sélectionnée au départ.
     *
     * @return l'Object sélectionnée ou {@code null} si pas de sélection.
     */
    private Object showInputDialog(String message, String title,
            Object[] values, Object initialValue) {
        return GuiUtilities.showInputDialog(this, message, title, values, initialValue);
    }

    /**
     * Retourne la fenêtre principale. Utilisée dans les actionPerformed.
     *
     * @return la fenêtre principale.
     */
    private LaboratoryFrame getStudentFrame() {
        return this;
    }
}
