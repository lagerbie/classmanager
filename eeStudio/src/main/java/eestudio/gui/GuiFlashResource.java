package eestudio.gui;

import java.awt.*;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.*;

import eestudio.Core;
import thot.labo.ProjectTarget;
import thot.labo.TagList;
import thot.labo.index.Index;
import thot.labo.index.IndexType;
import thot.supervision.CommandXMLUtilities;
import thot.utils.Constants;
import thot.utils.Utilities;

/**
 * Gestion des resources graphiques.
 *
 * @author Fabrice Alleau
 */
public class GuiFlashResource {
    /**
     * Référence sur le noyau de l'application
     */
    private final Core core;

    /**
     * Répertoire temporaire
     */
    private final File tempPath;
    /**
     * Fichier pour la sauvegarde de la langue de l'interface
     */
    private final File languageFile;

    /**
     * Gestion des différents textes suivant la langue
     */
    private Resources resources;

    /**
     * Références des textes des index pour récupérer les traduction en fonction du type
     */
    private Map<IndexType, String> indexTypesMap;

    /**
     * Fenêtre pour le texte
     */
    private TextEditorWindow textEditor;
    /**
     * Fenêtre d'étition d'un Index
     */
    private IndexDialog indexEditor;
    /**
     * Fenêtre d'étition de tous les Index
     */
    private IndexesDialog indexesEditor;
    /**
     * Fenêtre d'insertion de silence par lot
     */
    private BlankDialog blankEditor;
    /**
     * Fenêtre d'édition des tags
     */
    private TagsDialog tagsEditor;

    /**
     * Explorateur de fichier commun
     */
    private FileChooser fileChooser;
    /**
     * Fenêtre d'exportation du projet
     */
    private ExportDialog exportDialog;
    /**
     * Fenêtre d'importation de fichiers
     */
    private ImportDialog importDialog;
    /**
     * Fenêtre pour l'effacement d'éléments
     */
    private EraseDialog eraseDialog;
    /**
     * Fenêtre affichant une barre de progression
     */
    private ProcessingBar processingBar;

    /**
     * Fenêtre invisible servant de référence pour les autres fenêtres
     */
    private JFrame window;

    /**
     * Initialisation de l'interface graphique.
     *
     * @param core le noyau de l'application.
     * @param languageFile le fichier pour la sauvegarde de la langue.
     */
    public GuiFlashResource(Core core, File languageFile) {
        this.core = core;
        this.languageFile = languageFile;
        this.resources = new Resources();

        //création du répertoire utilisateur
        tempPath = new File(System.getProperty("java.io.tmpdir"), "edu4");
        tempPath.mkdirs();

        indexTypesMap = new HashMap<>(5);
        indexTypesMap.put(IndexType.PLAY, "playType");
        indexTypesMap.put(IndexType.RECORD, "recordType");
        indexTypesMap.put(IndexType.BLANK, "blankType");
        indexTypesMap.put(IndexType.BLANK_BEEP, "blankBeepType");
        indexTypesMap.put(IndexType.VOICE, "voiceType");
        indexTypesMap.put(IndexType.REPEAT, "repeatType");
        indexTypesMap.put(IndexType.FILE, "fileType");
//        indexTypesMap.put(IndexType.IMAGE, "imageType");
//        indexTypesMap.put(IndexType.SPEED, "speedType");
        indexTypesMap.put(IndexType.SELECTION, "selectionType");

        initComponents();
    }

    /**
     * Initialise la fenêtre principale.
     */
    private void initComponents() {
        processingBar = new ProcessingBar();
        fileChooser = new FileChooser(null);

        GuiUtilities.manageUI(true);
        window = new JFrame();
        window.setUndecorated(true);
        window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        window.setIconImages(GuiUtilities.icones);
        window.setLocation(512, 384);

        textEditor = new TextEditorWindow(core.getStyledEditorKit(), core.getStyledDocument(), resources);
        textEditor.setIconImages(GuiUtilities.icones);
        textEditor.setLocation(600, 100);
        textEditor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        indexEditor = new IndexDialog(window, core, resources, indexTypesMap);
        indexesEditor = new IndexesDialog(window, core, resources, indexTypesMap, core.getStyledEditorKit(),
                core.getStyledDocument());
        blankEditor = new BlankDialog(window, core, resources, indexTypesMap, core.getStyledEditorKit(),
                core.getStyledDocument());
        tagsEditor = new TagsDialog(window, core, resources);

        exportDialog = new ExportDialog(window, core, resources, ProjectTarget.COMMON_SOFT, processingBar);
        importDialog = new ImportDialog(window, core, resources, processingBar, tempPath);
        eraseDialog = new EraseDialog(window, core, resources, processingBar);

        textEditor.addLoadActionListener(e -> {
            if (e.getSource() instanceof JButton) {
                ((JButton) e.getSource()).setEnabled(false);
                flashTextLoad();
                ((JButton) e.getSource()).setEnabled(true);
            }
        });

        textEditor.addEraseActionListener(e -> {
            if (e.getSource() instanceof JButton) {
                ((JButton) e.getSource()).setEnabled(false);
                flashTextErase();
                ((JButton) e.getSource()).setEnabled(true);
            }
        });
    }

    /**
     * Ajoute un WindowListener à la fenêtre d'édition du texte.
     *
     * @param listener le WindowListener.
     */
    public void addEditorWindowListener(WindowListener listener) {
        textEditor.addWindowListener(listener);
        indexEditor.addWindowListener(listener);
    }

    /**
     * Ajoute un WindowListener aux fenêtres de progression.
     *
     * @param listener le WindowListener.
     */
    public void addProcessingBarWindowListener(WindowListener listener) {
        processingBar.addWindowListener(listener);
    }

    /**
     * Change la langue de l'interface.
     *
     * @param language la nouvelle langue de l'interface.
     */
    public void flashChangeLanguage(String language) {
        Locale locale = null;
        if (language.contentEquals("Français") || language.contentEquals("fr")) {
            locale = Locale.FRENCH;
        } else if (language.contentEquals("English") || language.contentEquals("en")) {
            locale = Locale.ENGLISH;
        } else if (language.contentEquals("Español") || language.contentEquals("es")) {
            locale = new Locale("es");
        } else if (language.contentEquals("Deutsch") || language.contentEquals("de")) {
            locale = Locale.GERMAN;
        } else if (language.contentEquals("Italiano") || language.contentEquals("it")) {
            locale = Locale.ITALIAN;
        } else if (language.contentEquals("Català") || language.contentEquals("ca")) {
            locale = new Locale("ca", "ES");
        } else if (language.contentEquals("Euskara") || language.contentEquals("eu")) {
            locale = new Locale("eu");
        }

        GuiUtilities.setDefaultLocale(locale);
        Utilities.saveText(CommandXMLUtilities.getLanguageXML(locale.getLanguage()), languageFile);

        //internalisation des différents textes
        resources.updateLocale(locale);

        textEditor.setLocale(locale);
        indexEditor.setLocale(locale);
        indexesEditor.setLocale(locale);
        blankEditor.setLocale(locale);
        exportDialog.setLocale(locale);
        importDialog.setLocale(locale);
        eraseDialog.setLocale(locale);
        tagsEditor.setLocale(locale);

        textEditor.updateLanguage();
        indexEditor.updateLanguage();
        indexesEditor.updateLanguage();
        blankEditor.updateLanguage();
        exportDialog.updateLanguage();
        importDialog.updateLanguage();
        eraseDialog.updateLanguage();
        tagsEditor.updateLanguage();
        fileChooser.updateLanguage();
    }

    /**
     * Débute un processus d'attente.
     *
     * @param title le titre pour la fenêtre.
     * @param message le message pour la barre de progression.
     * @param formatValue les objets pour le message si il est formaté
     * @param determinated si le processus à un poucentage déterminé.
     */
    public void processBegin(boolean determinated, String title, String message, Object... formatValue) {
        processingBar.processBegin(determinated, resources.getString(title), resources.getString(message), formatValue);
    }

    /**
     * Débute un processus d'attente.
     *
     * @param determinated indique si le processus à un poucentage déterminé.
     */
    public void processBegin(boolean determinated) {
        processingBar.processBegin(determinated);
    }

    /**
     * Ferme la barre de progression.
     */
    public void processEnded() {
        if (!indexEditor.isVisible()) {
            closeMainWindow();
        }
        processingBar.close();
    }

    /**
     * Change la valeur de la barre de progression.
     *
     * @param percent la nouvelle valeur de progression totale en pourcentage.
     */
    public void percentChanged(int percent) {
        processingBar.setValue(percent);
    }

    /**
     * chargement d'un fichier.
     */
    public void flashLoad() {
//        chooser.addChoosableFileFilter(resources.getString("imageFilter"), Constants.imageExtension);
        fileChooser.addChoosableFileFilter(resources.getString("textFilter"), Constants.textExtension);
        fileChooser.addChoosableFileFilter(resources.getString("audioFilter"), Constants.audioExtension);
        fileChooser.addChoosableFileFilter(resources.getString("videoFilter"), Constants.videoExtension);
        fileChooser.addChoosableFileFilter(resources.getString("projectFilter"), Constants.projectExtension);
        fileChooser.setAcceptAllFileFilterUsed(true);

        File file = fileChooser.getSelectedFile(getMainWindow(), FileChooser.LOAD);
        closeMainWindow();
        if (file != null && file.exists()) {
            importDialog.showDialog(file);
        }
    }

    /**
     * chargement d'un fichier prédéterminé.
     *
     * @param file le fichier.
     */
    public void flashLoad(File file) {
        closeMainWindow();
        importDialog.showDialog(file);
    }

    /**
     * sauvegarde du projet.
     */
    public void flashSave() {
        fileChooser.setFileFilter(resources.getString("projectFilter"), Constants.projectExtension);

        File file = fileChooser.getSelectedFile(getMainWindow(), FileChooser.SAVE);
        closeMainWindow();
        if (file != null) {
            exportDialog.showDialog(file);
        }
    }

    /**
     * Edition de tous les index.
     *
     * @param nbIndexes le nombre d'index à créer.
     */
    public void flashEditAll(int nbIndexes) {
        if (nbIndexes > 0) {
            indexesEditor.showDialog(0);
        } else {
            Object input = GuiUtilities
                    .showInputDialog(getMainWindow(), resources.getString("howIndexesInput"), null, null);

            closeMainWindow();
            if (input != null) {
                int nb = Utilities.parseStringAsInt((String) input);

                if (nb > 0) {
                    indexesEditor.showDialog(nb);
                }
            }
        }
    }

    /**
     * Edition de tous les index.
     *
     * @param nbIndexes le nombre d'index à créer.
     */
    public void flashInsertBlank(int nbIndexes) {
        if (nbIndexes > 0) {
            blankEditor.showDialog(0);
        } else {
            Object input = GuiUtilities
                    .showInputDialog(getMainWindow(), resources.getString("howIndexesInput"), null, null);

            closeMainWindow();
            if (input != null) {
                int nb = Utilities.parseStringAsInt((String) input);

                if (nb > 0) {
                    blankEditor.showDialog(nb);
                }
            }
        }
    }

    /**
     * Edition des tags.
     *
     * @param tags Les tags initiaux
     */
    public void flashEditTags(TagList tags) {
        tagsEditor.showDialog(tags);
    }

    /**
     * effacement du projet.
     */
    public void flashErase() {
        eraseDialog.showDialog();
    }

    /**
     * insérer un soustitre.
     *
     * @param begin le temps de début de l'index.
     * @param end le temps de fin de l'index.
     */
    public void flashIndexSubtitle(long begin, long end) {
        long length = end - begin;
        core.addMediaIndexAt(begin, length, IndexType.PLAY, null);
        core.sortIndexes();
        flashIndexEdit(begin + length / 2);
    }

    /**
     * insérer un blanc.
     *
     * @param begin le temps de début de l'index.
     * @param end le temps de fin de l'index.
     */
    public void flashIndexBlank(long begin, long end) {
        long lengthMax = end;
        if (lengthMax < 0) {
            GuiUtilities.showMessageDialog(getMainWindow(), resources.getString("onIndex"));
            closeMainWindow();
            return;
        }

        double length = 10.0;
        Object input = GuiUtilities.showInputDialog(getMainWindow(), resources.getString("indexLengthInput"), null,
                String.format("%1$.2f", length));

        closeMainWindow();
        if (input != null) {
            String value = (String) input;
            //passage de secondes en millisecondes
            length = Utilities.parseStringAsDouble(value.replace(',', '.')) * 1000;
            long timeMax = core.getRemainingTime();
            if (length > timeMax) {
                GuiUtilities.showMessageDialog(getMainWindow(),
                        String.format(resources.getString("insertionDurationMessage"), (long) length / 1000,
                                timeMax / 1000, core.getDurationMax() / 1000));
                closeMainWindow();
            } else {
                if (length > 0) {
                    processBegin(false, "processingTitle", "processingMessage");
                    boolean success = core.addMediaIndexAt(begin, (long) length, IndexType.BLANK, null);
                    processEnded();
                }
            }
        }
    }

    /**
     * insérer un fichier.
     *
     * @param begin le temps de début de l'index.
     */
    public void flashIndexFile(long begin) {
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.addChoosableFileFilter(resources.getString("videoFilter"), Constants.videoExtension);
        fileChooser.addChoosableFileFilter(resources.getString("audioFilter"), Constants.audioExtension);

        File file = fileChooser.getSelectedFile(getMainWindow(), FileChooser.LOAD);
        closeMainWindow();

        if (file != null) {
            long timeMax = Math.min(core.getInsertionDurationMax(), core.getRemainingTime());
            long fileDuration = core.getFileDuration(file);
            if (fileDuration < 100) {
                GuiUtilities.showMessageDialog(getMainWindow(),
                        String.format(resources.getString("fileFormatNotSupported"), file));
            } else if (fileDuration > timeMax) {
                GuiUtilities.showMessageDialog(getMainWindow(),
                        String.format(resources.getString("insertionDurationMessage"), fileDuration / 1000 + 1,
                                timeMax / 1000, core.getDurationMax() / 1000));
                closeMainWindow();
            } else {
                processBegin(true, "conversionTitle", "conversionMessage", file.getAbsolutePath());
                boolean success = core.insertFile(file, begin);
                processEnded();
            }
        }
    }

    /**
     * insérer la voix.
     *
     * @param begin le temps de début de l'index.
     */
    public void flashIndexVoice(long begin) {
        Index currentIndex = core.getIndexAtTime(begin);
        if (currentIndex == null || begin == currentIndex.getInitialTime() || begin == currentIndex.getFinalTime()) {
            core.addVoiceRecord(begin);
        } else {
            GuiUtilities.showMessageDialog(getMainWindow(), resources.getString("onIndex"));
            closeMainWindow();
        }
    }

    /**
     * éditer un index.
     *
     * @param time le temps de l'index à éditer.
     */
    public void flashIndexEdit(long time) {
        indexEditor.showDialog(core.getIndexAtTime(time));
    }

    /**
     * ajouter un index après.
     *
     * @param begin le temps de début de l'index.
     * @param end le temps de fin de l'index.
     */
    public void flashIndexAfter(long begin, long end) {
        double length = (end - begin) / 1000.0;

        Object input = GuiUtilities.showInputDialog(getMainWindow(), resources.getString("indexLengthInput"), null,
                String.format("%1$.2f", length));

        closeMainWindow();
        if (input != null) {
            String value = (String) input;
            length = Utilities.parseStringAsDouble(value.replace(',', '.')) * 1000;
            if (length > 0) {
                core.addMediaIndexAt(end, (long) length, IndexType.PLAY, null);
            }
        }
    }

    /**
     * ajouter un index après.
     *
     * @param begin le temps de début de l'index.
     * @param end le temps de fin de l'index.
     * @param subtitle le sous-titre de l'indes.
     */
    public void flashIndexRepeat(long begin, long end, String subtitle) {
        long length = end - begin;
        long timeMax = core.getRemainingTime();
        if (length > timeMax) {
            GuiUtilities.showMessageDialog(getMainWindow(),
                    String.format(resources.getString("insertionDurationMessage"), length / 1000, timeMax / 1000,
                            core.getDurationMax() / 1000));
        } else {
            core.addMediaIndexAt(end, length, IndexType.REPEAT, subtitle);
        }
    }

    /**
     * efface l'enregistrement audio de l'index.
     *
     * @param begin le temps de début de l'index.
     */
    public void flashIndexErase(long begin) {
        int choix = GuiUtilities.showOptionDialog(getMainWindow(), resources.getString("eraseIndex"), null, null);

        closeMainWindow();
        if (choix == GuiUtilities.YES_OPTION) {
            processBegin(false, "processingTitle", "processingMessage");
            core.eraseIndexRecord(begin);
            processEnded();
        }
    }

    /**
     * efface l'index et les données.
     *
     * @param time le temps de l'index à supprimer.
     */
    public void flashIndexDelete(long time) {
        int choix = GuiUtilities.showOptionDialog(getMainWindow(), resources.getString("deleteIndex"), null, null);

        closeMainWindow();
        if (choix == GuiUtilities.YES_OPTION) {
            processBegin(false, "processingTitle", "processingMessage");
            core.removeIndexAtTime(time);
            processEnded();
        }
    }

    /**
     * affiche l'éditeur de texte.
     *
     * @param visible
     */
    public void flashText(boolean visible) {
        textEditor.showWindow(visible);
    }

    /**
     * charge un fichier texte.
     */
    private void flashTextLoad() {
        fileChooser.setFileFilter(resources.getString("textFilter"), Constants.textExtension);

        //affiche la boite de dialogue et récupère le nom du fichier
        //si l'action à été validée
        File file = fileChooser.getSelectedFile(textEditor, FileChooser.LOAD);
        if (file != null) {
            processBegin(false, "conversionTitle", "conversionMessage", file.getAbsolutePath());
            boolean success = core.loadText(file);
            processEnded();
        }//end if
    }

//    protected void flashTextSave() {
//        chooser.setFileFilter(getString("textFilter"), Core.textExtension);
//        //affiche la boite de dialogue et enregistre les données
//        //tapées si un fichier a été validé
//        File file = chooser.getSelectedFile(textEditor, FileChooser.SAVE);
//        if(file != null) {
//            processBegin(false, "conversionTitle", "conversionMessage", file.getAbsolutePath());
//            core.saveText(file);
//            processEnded();
//        }
//    }

    /**
     * efface le texte.
     */
    private void flashTextErase() {
        int choix = GuiUtilities.showOptionDialog(textEditor, resources.getString("eraseTextConfirm"), null, null);
        if (choix == GuiUtilities.YES_OPTION) {
            core.eraseText();
        }
    }

    /**
     * Retourne le fenêtre principale tout en la mettant eu premier plan.
     *
     * @return la fenêtre principale.
     */
    private Window getMainWindow() {
        window.setAlwaysOnTop(true);
        window.setVisible(true);
        return window;
    }

    /**
     */
    private void closeMainWindow() {
        importDialog.close();
        exportDialog.close();
        eraseDialog.close();
        processingBar.close();
        window.setVisible(false);
    }

    /**
     * @param text
     */
    public void old_textLoaded(final String text) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
        textEditor.setText(text);
//            }
//        });
    }

    /**
     * @param state
     */
    public void old_runningStateChanged(final int state) {
        SwingUtilities.invokeLater(() -> {
            if (indexEditor.isVisible()) {
                indexEditor.updateButtons((state == Constants.PLAYING));
            }
        });
    }

}
