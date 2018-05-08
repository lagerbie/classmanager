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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.event.PopupMenuListener;

import thot.gui.EditorArea;
import thot.gui.FileChooser;
import thot.gui.FilterPanel;
import thot.gui.GuiUtilities;
import thot.gui.ImagePanel;
import thot.gui.ProcessingBar;
import thot.gui.Resources;
import thot.gui.VideoCanvas;
import thot.gui.VolumeSlider;
import thot.labo.LaboListener;
import thot.labo.LaboModule;
import thot.labo.index.Index;
import thot.supervision.gui.StateButton;
import thot.supervision.gui.TabPanel;
import thot.utils.Constants;
import thot.utils.Utilities;

/**
 * @author Fabrice alleau
 * @version 1.90
 */
public class LaboPanel extends TabPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Référence sur le noyau de l'application.
     */
    private final LaboModule core;
    /**
     * Resources pour les textes.
     */
    private Resources resources;
    /**
     * Répertoire de l'utilisateur.
     */
    private final File userHome;

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
     * Largeur de la fenêtre vidéo.
     */
    private int textEditorWidth = 335;
    /**
     * Hauteur de la fenêtre vidéo.
     */
    private int textEditorHeight = 472;

    private JButton loadButton;
    private JButton sendButton;
    private JButton saveButton;
    private JButton diffuseButton;
    private JButton blockButton;
    private JButton unloadButton;

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

    private JButton returnButton;
    private JButton playButton;
    private JButton pauseButton;
    private JButton recordButton;
    private JButton localControlButton;

    private JButton timeMaxButton;
    private JButton messageButton;
    private JButton launchButton;
    private JButton closeLaboButton;

    /**
     * Barre de défilement du temps.
     */
    private TimeIndexSlider timeIndexSlider;
    /**
     * Canvas pour l'affichage de la vidéo.
     */
    private VideoCanvas videoCanvas;
    /**
     * Zone de texte.
     */
    private EditorArea textArea;

    /**
     * Sélectionneur de fichier.
     */
    private FileChooser chooser;
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
     * Panneau pour le monitoring.
     */
    private FilterPanel mainMenu;
    /**
     * Panneau pour la diffusion.
     */
    private FilterPanel dynamicMenu;
    /**
     * Panneau pour le pairing.
     */
    private FilterPanel optionMenu;

    public LaboPanel(LaboModule core, File userHome, File tempPath, Window parent, int width, int height, int menuWidth,
            int menuHeight, int buttonSize, int margin, Resources resources) {
        super(parent);
        this.core = core;
        this.resources = resources;
        this.userHome = userHome;

        initPanel(parent, menuWidth, menuHeight, buttonSize, margin, resources);

        core.mediaPlayerSetVideoOutput(videoCanvas);
        Dimension dim = new Dimension(width, height);
        this.setMaximumSize(dim);
        this.setPreferredSize(dim);

        chooser = new FileChooser(parent, userHome);
        processingBar = new ProcessingBar(parent, null);
        importDialog = new ImportDialog(parent, core, resources, processingBar, tempPath);
        exportDialog = new ExportDialog(parent, core, resources, processingBar);
        eraseDialog = new EraseDialog(parent, core, resources, processingBar);

        addButtonListeners();
        addCoreListener();
    }

    /**
     * Ajout des méthodes pour le listener. Utilisation de SwingUtilities.invokeLater(new Runnable()) pour que les
     * modifications touchant l'interface graphique soit appelé par l'EDT.
     */
    private void addCoreListener() {
        core.addListener(new LaboListener() {
            @Override
            public void stateChanged(final int running, final int mediaType) {
                if (mediaType == Constants.UNLOAD) {
                    videoCanvas.setImage(null);
                }

                SwingUtilities.invokeLater(() -> updateButtons(running, mediaType));
            }

            @Override
            public void indexesModeChanged(boolean indexesMode) {
                updateIndexesMode(indexesMode);
            }

            @Override
            public void recordTimeMaxChanged(long recordTimeMax) {
                final String display = timeCount.getText()
                        .substring(0, timeCount.getText().indexOf('/'))
                        + String.format("/ %1$d:%2$02d",
                        (recordTimeMax / 1000) / 60, (recordTimeMax / 1000) % 60);

                SwingUtilities.invokeLater(() -> timeCount.setText(display));

                //la réprésentation graphique des index à changée
                if (core.getMediaIndexesCount() > 0 || core.getRecordIndexesCount() > 0) {
                    SwingUtilities.invokeLater(() -> {
                        timeIndexSlider.repaint();
                        getParentWindow().repaint();
                    });
                }
            }

            @Override
            public void timeChanged(long time) {
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
//                if (core.getMediaIndexesCount() == 0) {
//                    indexesModeButton.setEnabled(false);
//                    videoCanvas.setSubtitle(null);
//                } else {
//                    if (core.getRunningState() == Constants.PAUSE) {
//                        indexesModeButton.setEnabled(
//                                core.checkMultimediaIndexesValidity() == 0);
//                    }
//                }

                SwingUtilities.invokeLater(() -> {
                    timeIndexSlider.repaint();
                    getParentWindow().repaint();
                });
            }

            @Override
            public void imageChanged(Image image) {
                videoCanvas.setImage(image);
                SwingUtilities.invokeLater(() -> videoCanvas.repaint());
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

    private void initPanel(Window parent, int menuWidth, int menuHeight, int buttonSize, int margin,
            Resources resources) {
        Dimension dim;

        loadButton = getButton("load", true);
        sendButton = getButton("send", true);
        saveButton = getButton("save", true);
        diffuseButton = getButton("diffuse", true);
        blockButton = getButton("block", true);
        unloadButton = getButton("unload", true);

        returnButton = getButton("back", true);
        playButton = getButton("play", true);
        pauseButton = getButton("pause", true);
        recordButton = getButton("record", true);
        localControlButton = getButton("load", true);

        timeMaxButton = getButton("timeMax", true);
        messageButton = getButton("message", true);
        launchButton = getButton("launch", true);
        closeLaboButton = getButton("close", true);

        JPanel timePanel = new ImagePanel(GuiUtilities.getImage("timeCountImage"));
        timeCount = new JLabel("00:00 / 00:00");
//        timeCount.setPreferredSize(new Dimension(100, 15));
        timeCount.setHorizontalAlignment(JLabel.CENTER);
        timeCount.setVerticalAlignment(JLabel.TOP);
        timeCount.setForeground(Color.WHITE);
        timePanel.add(timeCount);

        //initialisation de la sortie vidéo
        videoCanvas = new VideoCanvas();
        videoCanvas.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        videoCanvas.setBounds(0, 0, canvasWidth, canvasHeight);
        videoCanvas.setDefaultImage(GuiUtilities.getImage("defaultImage"));
        videoCanvas.setDefaultImage(null);

        //Zone de texte
        textArea = new EditorArea(core.getStyledEditorKit(), core.getStyledDocument(), resources);
        textArea.setSizeMax(textEditorWidth, textEditorHeight);
        textArea.setSelectedTextColor(Color.RED);
        textArea.setSelectionColor(GuiUtilities.TRANSPARENT_COLOR);
        textArea.setBackground(GuiUtilities.TRANSPARENT_COLOR);
//        textArea.setBorder(null);
        Font font = GuiUtilities.defaultTextFont;
        textArea.setFont(font);

        mediaMuteButton = getButton("mediaMute", true);
        mediaVolume = new VolumeSlider(parent, mediaMuteButton, GuiUtilities.getImage("volumeImage"),
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

        //Barre de défilement du temps et des index
        timeIndexSlider = new TimeIndexSlider(core, parent, resources, canvasWidth) {
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


        GridBagLayout gridbag;
        GridBagConstraints c;

        dim = new Dimension(canvasWidth, 2 * (buttonSize + margin) + 2 * margin);
        mainMenu = new FilterPanel(resources.getString("monitoringLabel"), dim);
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        mainMenu.setLayout(gridbag);

        c.gridwidth = 1;
        gridbag.setConstraints(loadButton, c);
        mainMenu.add(loadButton);
        gridbag.setConstraints(sendButton, c);
        mainMenu.add(sendButton);
        gridbag.setConstraints(saveButton, c);
        mainMenu.add(saveButton);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(timePanel, c);
        mainMenu.add(timePanel);
        c.gridwidth = 1;
        gridbag.setConstraints(diffuseButton, c);
        mainMenu.add(diffuseButton);
        gridbag.setConstraints(blockButton, c);
        mainMenu.add(blockButton);
        gridbag.setConstraints(unloadButton, c);
        mainMenu.add(unloadButton);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(mediaVolume, c);
        mainMenu.add(mediaVolume);

        dim = new Dimension(canvasWidth, buttonSize + 2 * margin);
        dynamicMenu = new FilterPanel(resources.getString("diffusionLabel"), dim);
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        dynamicMenu.setLayout(gridbag);

        c.gridwidth = 1;
        gridbag.setConstraints(returnButton, c);
        dynamicMenu.add(returnButton);
        gridbag.setConstraints(playButton, c);
        dynamicMenu.add(playButton);
        gridbag.setConstraints(pauseButton, c);
        dynamicMenu.add(pauseButton);
        gridbag.setConstraints(recordButton, c);
        dynamicMenu.add(recordButton);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(localControlButton, c);
        dynamicMenu.add(localControlButton);

        dim = new Dimension(textEditorWidth, buttonSize + 2 * margin);
        optionMenu = new FilterPanel(resources.getString("pairingLabel"), dim);
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        optionMenu.setLayout(gridbag);

        c.gridwidth = 1;
        gridbag.setConstraints(timeMaxButton, c);
        optionMenu.add(timeMaxButton);
        gridbag.setConstraints(messageButton, c);
        optionMenu.add(messageButton);
        gridbag.setConstraints(launchButton, c);
        optionMenu.add(launchButton);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(closeLaboButton, c);
        optionMenu.add(closeLaboButton);

        JPanel laboMenu = new JPanel();
        laboMenu.setLayout(new BoxLayout(laboMenu, BoxLayout.Y_AXIS));
        laboMenu.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        dim = new Dimension(menuWidth, menuHeight);
        laboMenu.setMaximumSize(dim);
        laboMenu.setPreferredSize(dim);

//        //Menu pour le bouton droit de la souris sur la zone de texte.
//        initTextPopupMenu();

        //Initialisation de l'état des boutons
        loadButton.setEnabled(true);
        sendButton.setEnabled(false);
        saveButton.setEnabled(false);
        diffuseButton.setEnabled(true);
        blockButton.setEnabled(true);
        unloadButton.setEnabled(false);

        returnButton.setEnabled(true);
        playButton.setEnabled(true);
        pauseButton.setEnabled(false);
        recordButton.setEnabled(true);
        localControlButton.setEnabled(true);

        mediaMuteButton.setEnabled(true);
        timeMaxButton.setEnabled(true);
        messageButton.setEnabled(true);
        launchButton.setEnabled(true);
        closeLaboButton.setEnabled(true);

        updateTimeCounter(0);
    }

    /**
     * Ajout des méthodes sur les boutons.
     *
     * @since version 1.83
     */
    private void addButtonListeners() {
//        //anonymous listeners pour le bouton de changement de mode
//        indexesModeButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent event) {
//                core.setIndexesMode(!core.getIndexesMode());
//            }
//        });

        //anonymous listeners pour le boutton Charger du module multimédia
        loadButton.addActionListener(event -> {
            //création dun nouveau filechosser avec un filtre pour les
            //extension de fichiers
            chooser.addChoosableFileFilter(resources.getString("imageFilter"),
                    Constants.imageExtension);
            chooser.addChoosableFileFilter(resources.getString("audioFilter"),
                    Constants.audioExtension);
            chooser.addChoosableFileFilter(resources.getString("mediaFilter"),
                    Constants.videoExtension);
            chooser.addChoosableFileFilter(resources.getString("projectFilter"),
                    Constants.projectExtension);
            chooser.setAcceptAllFileFilterUsed(true);

            //affiche la boite de dialogue et récupère le nom du fichier
            //si l'action à été validée
            File file = chooser.getSelectedFile(FileChooser.LOAD);
            if (file != null) {
                importDialog.showDialog(file);
            }
        });

        //anonymous listeners pour le bouton Effacer du module audio
        unloadButton.addActionListener(event -> {
            int choix = showOptionDialog(resources.getString("eraseMedia"));
            if (choix == GuiUtilities.YES_OPTION) {
                eraseDialog.showDialog();
            }
        });

        //anonymous listeners pour le bouton Lecture du module audio
        playButton.addActionListener(event -> {
            //Lecture des données audio capturées.
            core.audioPlay();
        });

        //anonymous listeners pour le bouton Stop du module multimédia
        pauseButton.addActionListener(event -> {
            //Termine l'enregistrement ou la lecture des données audio.
            core.audioPause();
        });

        //anonymous listeners pour le bouton Enregistrement du module audio
        recordButton.addActionListener(event -> core.audioRecord());

        //anonymous listeners pour le bouton Sauvegarder du module audio
        saveButton.addActionListener(event -> {
            //création dun nouveau filechosser avec un filtre pour les
            //fichier audio
            chooser.addChoosableFileFilter(resources.getString("audioFilter"), Constants.audioExtension);
            chooser.addChoosableFileFilter(resources.getString("projectFilter"), Constants.projectExtension);

            //affiche la boite de dialogue et enregistre les données
            //capturées si un fichier a été validé
            File file = chooser.getSelectedFile(FileChooser.SAVE);
            if (file != null) {
                exportDialog.showDialog(file);
            }
        });

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
    }

    /**
     * Mise à jour du temps courant.
     *
     * @param time le temps en millisecondes.
     */
    private void updateTimeCounter(long time) {
        long timeMax = core.getRecordTimeMax();
        String display = String
                .format("%1$d:%2$02d / %3$d:%4$02d", (time / 1000) / 60, (time / 1000) % 60, (timeMax / 1000) / 60,
                        (timeMax / 1000) % 60);
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
        boolean isIndex = (core.getMediaIndexesCount() != 0) && (core.checkMultimediaIndexesValidity() == 0);
        boolean isAuto = core.getIndexesMode();

        playButton.setEnabled(isStop || (isRecording && !isAuto));
//        playButton.setEnabled(isStop || isRecording);
        pauseButton.setEnabled(isPlaying || isRecording);
        recordButton.setEnabled(isStop || (isPlaying && !isAuto));
//        recordButton.setEnabled(isStop || isPlaying);

        loadButton.setEnabled(isStop);
        saveButton.setEnabled(isStop);
        unloadButton.setEnabled(isStop && !isUnload);

        timeMaxButton.setEnabled(isStop && (isUnload || isImage));

//        indexesModeButton.setEnabled(isStop && isIndex);
    }

    /**
     * Actualise le texte pour la lecture automatique.
     *
     * @param indexesMode le mode de lecture automatique.
     *
     * @deprecated
     */
    @Deprecated
    private void updateIndexesMode(boolean indexesMode) {
//        if (indexesMode) {
//            indexesModeButton.setIcon(GuiUtilities.getImageIcon("modeAutoImage"));
//            indexesModeButton.setToolTipText(resources.getString("modeAuto"));
//        } else {
//            indexesModeButton.setIcon(GuiUtilities.getImageIcon("modeManuelImage"));
//            indexesModeButton.setToolTipText(resources.getString("modeManuel"));
//        }
//        indexesModeButton.repaint();
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

    @Override
    public StateButton getButton(String type) {
        return null;
    }

    @Override
    public void updateLanguage(Resources resources) {
    }

    /**
     * Affiche une boîte de dialogue avec une entrée texte.
     *
     * @param message le message à afficher.
     * @param initValue la valeur initiale ({@code null} si pas de valeur).
     *
     * @return le texte qui a été validé ou {@code null} si l'opération a été annulée.
     */
    private String showInputDialog(String message, String initValue) {
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
    private int showOptionDialog(String message) {
        return GuiUtilities.showOptionDialog(this.getParentWindow(), message, null, null);
    }

    @Override
    public void updateButtonsFor(StateButton button, boolean hasGroup, boolean isGroupButton) {
    }

    @Override
    public void setGroupFonctionsEnabled(boolean enable) {
    }

    @Override
    public void setButtonActions(ActionListener buttonListener,
            MouseAdapter menuButtonListener, MouseAdapter menuMouseListener,
            PopupMenuListener popupMenuListener) {
    }
}
