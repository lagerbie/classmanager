package eestudio.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.*;

import eestudio.Constants;
import eestudio.Core;
import eestudio.Index;
import eestudio.utils.TagList;
import eestudio.utils.Utilities;
import eestudio.utils.XMLUtilities;

/*
 * v0.95: supp de private VideoWindow videoWindow;
 * v0.95: ajout de private JFrame window
 * v0.95: modif de TextEditorWindow textEditorWindow en textEditor
 * v0.95: modif de IndexDialog editIndexDialog en indexEditor
 * v0.95: modif de IndexesDialog indexesDialog en indexesEditor
 * v0.95: ajout de protected void addWindowListener(WindowListener listener)
 * v0.95: supp de protected void flashUpdateWait(boolean wait)
 * v0.95: modif de GuiResourceWithFlash(softName, textResources, imageResources,
 *        core) en GuiResourceWithFlash(String softName, Core core)
 * 
 * v0.95.12: ajout de private ExportDialog exportDialog;
 * v0.95.12: ajout de private ImportDialog importDialog;
 * v0.95.12: ajout de private EraseDialog eraseDialog;
 * v0.95.12: ajout de private ProcessingBar processingBar;
 * v0.95.12: supp de private WaitDialog waitDialog;
 * v0.95.12: ajout de private void processBegin(boolean determinated, String title,
 *           String message, Object... formatValue) [test visbilité Dialog]
 * v0.95.12: ajout de protected void processTitleChanged(String title)
 * v0.95.12: ajout de protected void processMessageChanged(String message)
 * v0.95.12: ajout de protected void processDeterminatedChanged(boolean determinated)
 * v0.95.12: ajout de protected void processBegin(boolean determinated)
 * v0.95.12: ajout de protected void processEnded()
 * v0.95.12: ajout de protected void percentChanged(int percent)
 * v0.95.12: ajout de protected void flashIndexAfter(long begin, long end)
 * v0.95.12: supp de protected void flashIndexAdd(double position)
 * v0.95.12: modif de initComponents() [ajout Dialog]
 * v0.95.12: modif de flashChangeLanguage(String language) [ajout Dialog]
 * v0.95.12: modif de flashLoad(double position) en flashLoad() [importDialog]
 * v0.95.12: modif de flashSave() [exportDialog]
 * v0.95.12: modif de flashEdit() en flashEditAll(int nbIndexes)
 * v0.95.12: modif de flashSubtitle(double) en flashIndexSubtitle(long, long)
 * v0.95.12: modif de flashBlank(double) en flashIndexBlank(long, long)
 * v0.95.12: modif de flashVoice(double position) en flashIndexVoice(long begin)
 * v0.95.12: modif de flashFile(double position) en flashIndexFile(long begin)
 * v0.95.12: modif de flashErase() [eraseDialog]
 * v0.95.12: modif de flashIndexEdit(int indice) en flashIndexEdit(long time)
 * v0.95.12: modif de flashIndexRepeat(double) en flashIndexRepeat(long, long)
 * v0.95.12: modif de flashIndexErase(double ) en flashIndexErase(long begin)
 * v0.95.12: modif de flashIndexDelete(double) en flashIndexDelete(long time)
 * v0.95.12: modif de closeMainWindow() [ajout Dialog]
 * 
 * v0.95.13: ajout de private FileChooser fileChooser; pour la sauvegarde du répertoire
 * v0.95.13: modif de initComponents() [fileChooser]
 * v0.95.13: modif de flashLoad() [fileChooser]
 * v0.95.13: modif de flashSave() [fileChooser]
 * v0.95.13: modif de flashIndexFile(long begin) [fileChooser]
 * v0.95.13: modif de flashTextLoad() [fileChooser]
 * v0.95.13: modif de old_textLoaded(final String text) [supp invokeLater]
 * 
 * v0.96: ajout de private final File tempPath;
 * v0.96: ajout de protected void addProcessingBarWindowListener(WindowListener listener)
 * v0.96: modif de GuiResourceWithFlash(String softName, Core core) [tempPath]
 * v0.96: modif de initComponents() [tempPath]
 * v0.96: modif de addWindowListener(WindowListener listener) en
 *        addTextEditorWindowListener(WindowListener listener)
 * v0.96: modif de flashChangeLanguage(String language) [update de fileChooser]
 * v0.96: modif de flashIndexFile(long begin) [ajout du test si la durée < 100ms]
 * v0.96: modif de flashIndexRepeat(long begin, long end) en
 *        flashIndexRepeat(long begin, long end, String subtitle)
 * 
 * v0.97: supp de private final File userHome;
 * v0.97: modif de GuiResourceWithFlash(String softName, Core core) en
 *        GuiResourceWithFlash(Core core) [supp userHome]
 * v0.97: modif de initComponents() [supp userHome, processingBar]
 * v0.97: modif de addProcessingBarWindowListener(WindowListener listener) [processingBar]
 * v0.97: modif de processBegin(...) [processingBar]
 * v0.97: modif de processTitleChanged(String title) [processingBar]
 * v0.97: modif de processMessageChanged(String message) [processingBar]
 * v0.97: modif de processDeterminatedChanged(boolean determinated) [processingBar]
 * v0.97: modif de processBegin(boolean determinated) [processingBar]
 * v0.97: modif de processEnded() [processingBar]
 * v0.97: modif de percentChanged(int percent) [processingBar]
 * v0.97: ajout de public void flashLoad(File file)
 * 
 * v0.99: supp de implements Resources
 * v0.99: supp de public static final String eeStudioResources = "eestudio.resources.eeStudio";
 * v0.99: supp de private ResourceBundle texts;
 * v0.99: ajout de private Resources resources;
 * v0.99: ajout de private BlankDialog blankEditor;
 * v0.99: ajout de private TagsDialog tagsEditor;
 * v0.99: ajout de protected void flashInsertBlank(int nbIndexes)
 * v0.99: ajout de protected void flashEditTags(Mp3Tags tags)
 * v0.99: supp de @Override public String getString(String key)
 * v0.99: supp de protected processTitleChanged(String title)
 * v0.99: supp de protected processMessageChanged(String message)
 * v0.99: supp de protected void processDeterminatedChanged(boolean determinated)
 * v0.99: modif de GuiResourceWithFlash(Core core) [resources]
 * v0.99: modif de initComponents() [blankEditor, tagsEditor, resources]
 * v0.99: modif de flashChangeLanguage(String language) [blankEditor, tagsEditor, resources]
 * v0.99: modif de processBegin(...) [resources]
 * v0.99: modif de flashLoad() [resources]
 * v0.99: modif de flashSave() [resources]
 * v0.99: modif de flashEditAll(...) [resources]
 * v0.99: modif de flashIndexBlank(...) [resources]
 * v0.99: modif de flashIndexFile(...) [resources]
 * v0.99: modif de flashIndexVoice(...) [resources]
 * v0.99: modif de flashIndexAfter(...) [resources]
 * v0.99: modif de flashIndexRepeat(..) [resources]
 * v0.99: modif de flashIndexErase(...) [resources]
 * v0.99: modif de flashIndexDelete(...) [resources]
 * v0.99: modif de flashTextLoad() [resources]
 * v0.99: modif de flashTextErase() [resources]
 * 
 * v1.00: ajout de private final File languageFile;
 * v1.00: modif de GuiFlashResource(Core core) en GuiFlashResource(Core core, File languageFile)
 * 
 * v1.01: modif de initComponents() [initialisation des fenêtres avec le nouveau
 *        design]
 * 
 * v1.02: modif de initComponents() [ordre d'initialisation avec manageUI(..)]
 */

/**
 * Gestion des resources graphiques.
 *
 * @author Fabrice Alleau
 * @since version 0.94
 * @version 1.02
 */
public class GuiFlashResource {
    /** Référence sur le noyau de l'application */
    private final Core core;

    /** Répertoire temporaire */
    private final File tempPath;
    /** Fichier pour la sauvegarde de la langue de l'interface */
    private final File languageFile;

    /** Gestion des différents textes suivant la langue */
    private Resources resources;

    /** Références des textes des index pour récupérer les traduction en fonction du type */
    private Map<String,String> indexTypesMap;

    /** Fenêtre pour le texte */
    private TextEditorWindow textEditor;
    /** Fenêtre d'étition d'un Index */
    private IndexDialog indexEditor;
    /** Fenêtre d'étition de tous les Index */
    private IndexesDialog indexesEditor;
    /** Fenêtre d'insertion de silence par lot */
    private BlankDialog blankEditor;
    /** Fenêtre d'édition des tags */
    private TagsDialog tagsEditor;

    /** Explorateur de fichier commun */
    private FileChooser fileChooser;
    /** Fenêtre d'exportation du projet */
    private ExportDialog exportDialog;
    /** Fenêtre d'importation de fichiers */
    private ImportDialog importDialog;
    /** Fenêtre pour l'effacement d'éléments */
    private EraseDialog eraseDialog;
    /** Fenêtre affichant une barre de progression */
    private ProcessingBar processingBar;

    /** Fenêtre invisible servant de référence pour les autres fenêtres */
    private JFrame window;

    /**
     * Initialisation de l'interface graphique.
     *
     * @param core le noyau de l'application.
     * @param languageFile le fichier pour la sauvegarde de la langue.
     * @since version 0.94 - version 1.00
     */
    public GuiFlashResource(Core core, File languageFile) {
        this.core = core;
        this.languageFile = languageFile;
        this.resources = new Resources();

        //création du répertoire utilisateur
        tempPath = new File(System.getProperty("java.io.tmpdir"), "edu4");
        tempPath.mkdirs();

        indexTypesMap = new HashMap<String, String>(5);
        indexTypesMap.put(Index.PLAY, "playType");
        indexTypesMap.put(Index.RECORD, "recordType");
        indexTypesMap.put(Index.BLANK, "blankType");
        indexTypesMap.put(Index.BLANK_BEEP, "blankBeepType");
        indexTypesMap.put(Index.VOICE, "voiceType");
        indexTypesMap.put(Index.REPEAT, "repeatType");
        indexTypesMap.put(Index.FILE, "fileType");
//        indexTypesMap.put(Index.IMAGE, "imageType");
//        indexTypesMap.put(Index.SPEED, "speedType");
        indexTypesMap.put(Index.SELECTION, "selectionType");

        initComponents();
    }

    /**
     * Initialise la fenêtre principale.
     *
     * @since version 0.94 - version 1.02
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

        textEditor = new TextEditorWindow(
                core.getStyledEditorKit(), core.getStyledDocument(), resources);
        textEditor.setIconImages(GuiUtilities.icones);
        textEditor.setLocation(600, 100);
        textEditor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        indexEditor = new IndexDialog(window, core, resources, indexTypesMap);
        indexesEditor = new IndexesDialog(window, core, resources, indexTypesMap,
                core.getStyledEditorKit(), core.getStyledDocument());
        blankEditor = new BlankDialog(window, core, resources, indexTypesMap,
                core.getStyledEditorKit(), core.getStyledDocument());
        tagsEditor = new TagsDialog(window, core, resources);

        exportDialog = new ExportDialog(window, core, resources,
                Constants.COMMON_SOFT, processingBar);
        importDialog = new ImportDialog(window, core, resources,
                processingBar, tempPath);
        eraseDialog = new EraseDialog(window, core, resources, processingBar);

        textEditor.addLoadActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() instanceof JButton){
                    ((JButton)e.getSource()).setEnabled(false);
                    flashTextLoad();
                    ((JButton)e.getSource()).setEnabled(true);
                }
            }
        });

        textEditor.addEraseActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() instanceof JButton){
                    ((JButton)e.getSource()).setEnabled(false);
                    flashTextErase();
                    ((JButton)e.getSource()).setEnabled(true);
                }
            }
        });
    }//end initComponents()

    /**
     * Ajoute un WindowListener à la fenêtre d'édition du texte.
     * 
     * @param listener le WindowListener.
     * @since version 0.95 - version 0.97
     */
    public void addEditorWindowListener(WindowListener listener) {
        textEditor.addWindowListener(listener);
        indexEditor.addWindowListener(listener);
    }

    /**
     * Ajoute un WindowListener aux fenêtres de progression.
     * 
     * @param listener le WindowListener.
     * @since version 0.96 - version 0.97
     */
    public void addProcessingBarWindowListener(WindowListener listener) {
        processingBar.addWindowListener(listener);
    }

    /**
     * Change la langue de l'interface.
     *
     * @param language la nouvelle langue de l'interface.
     * @since version 0.94 - version 1.00
     */
    public void flashChangeLanguage(String language) {
        Locale locale = null;
        if(language.contentEquals("Français") || language.contentEquals("fr"))
             locale = Locale.FRENCH;
        else if(language.contentEquals("English") || language.contentEquals("en"))
            locale = Locale.ENGLISH;
        else if(language.contentEquals("Español") || language.contentEquals("es"))
            locale = new Locale("es");
        else if(language.contentEquals("Deutsch") || language.contentEquals("de"))
            locale = Locale.GERMAN;
        else if(language.contentEquals("Italiano") || language.contentEquals("it"))
            locale = Locale.ITALIAN;
        else if(language.contentEquals("Català") || language.contentEquals("ca"))
            locale = new Locale("ca", "ES");
        else if(language.contentEquals("Euskara") || language.contentEquals("eu"))
            locale = new Locale("eu");

        GuiUtilities.setDefaultLocale(locale);
        Utilities.saveText(XMLUtilities.getLanguageXML(locale.getLanguage()), languageFile);

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
     * @since version 0.94 - version 0.99
     */
    public void processBegin(boolean determinated,
            String title, String message, Object... formatValue) {
        processingBar.processBegin(determinated, resources.getString(title),
                resources.getString(message), formatValue);
    }

    /**
     * Débute un processus d'attente.
     * 
     * @param determinated indique si le processus à un poucentage déterminé.
     * @since version 0.94 - version 0.97
     */
    public void processBegin(boolean determinated) {
        processingBar.processBegin(determinated);
    }

    /**
     * Ferme la barre de progression.
     * 
     * @since version 0.94 - version 0.97
     */
    public void processEnded() {
        if(!indexEditor.isVisible())
            closeMainWindow();
        processingBar.close();
    }

    /**
     * Change la valeur de la barre de progression.
     *
     * @param percent la nouvelle valeur de progression totale en pourcentage.
     * @since version 0.94 - version 0.97
     */
    public void percentChanged(int percent) {
        processingBar.setValue(percent);
    }

    /**
     * chargement d'un fichier.
     * 
     * @since version 0.94 - version 0.99
     */
    public void flashLoad() {
//        chooser.addChoosableFileFilter(resources.getString("imageFilter"),
//                Core.imageExtension);
        fileChooser.addChoosableFileFilter(resources.getString("textFilter"),
                Constants.textExtension);
        fileChooser.addChoosableFileFilter(resources.getString("audioFilter"),
                Constants.audioExtension);
        fileChooser.addChoosableFileFilter(resources.getString("videoFilter"),
                Constants.videoExtension);
        fileChooser.addChoosableFileFilter(resources.getString("projectFilter"),
                Constants.indexesExtension, Constants.projectExtension, Constants.edu4Extension);
        fileChooser.setAcceptAllFileFilterUsed(true);

        File file = fileChooser.getSelectedFile(getMainWindow(), FileChooser.LOAD);
        closeMainWindow();
        if(file != null && file.exists()) {
            importDialog.showDialog(file);
        }//end if
    }

    /**
     * chargement d'un fichier prédéterminé.
     * 
     * @param file le fichier.
     * @since version 0.97
     */
    public void flashLoad(File file) {
        closeMainWindow();
        importDialog.showDialog(file);
    }

    /**
     * sauvegarde du projet.
     * 
     * @since version 0.94 - version 0.99
     */
    public void flashSave() {
        fileChooser.setFileFilter(resources.getString("projectFilter"),
                Constants.indexesExtension, Constants.projectExtension, Constants.edu4Extension);

        File file = fileChooser.getSelectedFile(getMainWindow(), FileChooser.SAVE);
        closeMainWindow();
        if(file != null) {
            exportDialog.showDialog(file);
        }
    }

    /**
     * Edition de tous les index.
     *
     * @param nbIndexes le nombre d'index à créer.
     * @since version 0.94 - version 0.99
     */
    public void flashEditAll(int nbIndexes) {
        if(nbIndexes > 0)
            indexesEditor.showDialog(0);
        else {
            Object input = GuiUtilities.showInputDialog(getMainWindow(),
                    resources.getString("howIndexesInput"), null, null);

            closeMainWindow();
            if(input != null) {
                int nb = Utilities.parseStringAsInt((String)input);

                if(nb > 0) {
                    indexesEditor.showDialog(nb);
                }
            }
        }
    }

    /**
     * Edition de tous les index.
     *
     * @param nbIndexes le nombre d'index à créer.
     * @since version 0.99
     */
    public void flashInsertBlank(int nbIndexes) {
        if(nbIndexes > 0)
            blankEditor.showDialog(0);
        else {
            Object input = GuiUtilities.showInputDialog(getMainWindow(),
                    resources.getString("howIndexesInput"), null, null);

            closeMainWindow();
            if(input != null) {
                int nb = Utilities.parseStringAsInt((String)input);

                if(nb > 0) {
                    blankEditor.showDialog(nb);
                }
            }
        }
    }

    /**
     * Edition des tags.
     *
     * @param tags Les tags initiaux
     * @since version 0.99
     */
    public void flashEditTags(TagList tags) {
        tagsEditor.showDialog(tags);
    }

    /**
     * effacement du projet.
     * 
     * @since version 0.94 - version 0.95.12
     */
    public void flashErase() {
        eraseDialog.showDialog();
    }

    /**
     * insérer un soustitre.
     *
     * @param begin le temps de début de l'index.
     * @param end le temps de fin de l'index.
     * @since version 0.94
     */
    public void flashIndexSubtitle(long begin, long end) {
        long length = end - begin;
        core.addMediaIndexAt(begin, length, Index.PLAY, null);
        core.sortIndexes();
        flashIndexEdit(begin+length/2);
    }

    /**
     * insérer un blanc.
     *
     * @param begin le temps de début de l'index.
     * @param end le temps de fin de l'index.
     * @since version 0.94 - version 0.99
     */
    public void flashIndexBlank(long begin, long end) {
        long lengthMax = end;
        if(lengthMax < 0) {
            GuiUtilities.showMessageDialog(getMainWindow(), resources.getString("onIndex"));
            closeMainWindow();
            return;
        }

        double length = 10.0;
        Object input = GuiUtilities.showInputDialog(getMainWindow(),
                resources.getString("indexLengthInput"), null, String.format("%1$.2f", length));

        closeMainWindow();
        if(input != null) {
            String value = (String)input;
            //passage de secondes en millisecondes
            length = Utilities.parseStringAsDouble(value.replace(',', '.')) * 1000;
            long timeMax = core.getRemainingTime();
            if(length > timeMax) {
                GuiUtilities.showMessageDialog(getMainWindow(),
                        String.format(resources.getString("insertionDurationMessage"),
                        (long)length/1000, timeMax/1000, core.getDurationMax()/1000));
                closeMainWindow();
            }
            else {
                if(length > 0) {
                    processBegin(false, "processingTitle", "processingMessage");
                    boolean success = core.addMediaIndexAt(begin, (long)length,
                            Index.BLANK, null);
                    processEnded();
                }//end if
            }
        }//end if
    }

    /**
     * insérer un fichier.
     *
     * @param begin le temps de début de l'index.
     * @since version 0.94 - version 0.99
     */
    public void flashIndexFile(long begin) {
        fileChooser.setAcceptAllFileFilterUsed(true);
        fileChooser.addChoosableFileFilter(resources.getString("videoFilter"),
                Constants.videoExtension);
        fileChooser.addChoosableFileFilter(resources.getString("audioFilter"),
                Constants.audioExtension);

        File file = fileChooser.getSelectedFile(getMainWindow(), FileChooser.LOAD);
        closeMainWindow();

        if(file != null) {
            long timeMax = Math.min(core.getInsertionDurationMax(), core.getRemainingTime());
            long fileDuration = core.getFileDuration(file);
            if(fileDuration < 100) {
                GuiUtilities.showMessageDialog(getMainWindow(),
                        String.format(resources.getString("fileFormatNotSupported"), file));
            }
            else if(fileDuration > timeMax) {
                GuiUtilities.showMessageDialog(getMainWindow(),
                        String.format(resources.getString("insertionDurationMessage"),
                        fileDuration/1000+1, timeMax/1000, core.getDurationMax()/1000));
                closeMainWindow();
            }
            else {
                processBegin(true, "conversionTitle", "conversionMessage",
                        file.getAbsolutePath());
                boolean success = core.insertFile(file, begin);
                processEnded();
            }
        }//end if
    }

    /**
     * insérer la voix.
     *
     * @param begin le temps de début de l'index.
     * @since version 0.94 - version 0.99
     */
    public void flashIndexVoice(long begin) {
        Index currentIndex = core.getIndexAtTime(begin);
        if(currentIndex == null
                || begin == currentIndex.getInitialTime()
                || begin == currentIndex.getFinalTime()) {
            core.addVoiceRecord(begin);
        }
        else {
            GuiUtilities.showMessageDialog(getMainWindow(), resources.getString("onIndex"));
            closeMainWindow();
        }
    }

    /**
     * éditer un index.
     *
     * @param time le temps de l'index à éditer.
     * @since version 0.94
     */
    public void flashIndexEdit(long time) {
        indexEditor.showDialog(core.getIndexAtTime(time));
    }

    /**
     * ajouter un index après.
     *
     * @param begin le temps de début de l'index.
     * @param end le temps de fin de l'index.
     * @since version 0.94 - version 0.99
     */
    public void flashIndexAfter(long begin, long end) {
        double length = (end-begin) / 1000.0;

        Object input = GuiUtilities.showInputDialog(getMainWindow(),
                resources.getString("indexLengthInput"), null, String.format("%1$.2f", length));

        closeMainWindow();
        if(input != null) {
            String value = (String)input;
            length = Utilities.parseStringAsDouble(value.replace(',', '.')) * 1000;
            if(length > 0){
                core.addMediaIndexAt(end, (long)length, Index.PLAY, null);
            }
        }
    }

    /**
     * ajouter un index après.
     *
     * @param begin le temps de début de l'index.
     * @param end le temps de fin de l'index.
     * @param subtitle le sous-titre de l'indes.
     * @since version 0.94 - version 0.99
     */
    public void flashIndexRepeat(long begin, long end, String subtitle) {
        long length = end - begin;
        long timeMax = core.getRemainingTime();
        if(length > timeMax) {
            GuiUtilities.showMessageDialog(getMainWindow(),
                    String.format(resources.getString("insertionDurationMessage"),
                    length/1000, timeMax/1000, core.getDurationMax()/1000));
        }
        else {
            core.addMediaIndexAt(end, length, Index.REPEAT, subtitle);
        }
    }

    /**
     * efface l'enregistrement audio de l'index.
     *
     * @param begin le temps de début de l'index.
     * @since version 0.94 - version 0.99
     */
    public void flashIndexErase(long begin) {
        int choix = GuiUtilities.showOptionDialog(getMainWindow(),
                resources.getString("eraseIndex"), null, null);

        closeMainWindow();
        if(choix == GuiUtilities.YES_OPTION) {
            processBegin(false, "processingTitle", "processingMessage");
            core.eraseIndexRecord(begin);
            processEnded();
        }
    }

    /**
     * efface l'index et les données.
     *
     * @param time le temps de l'index à supprimer.
     * @since version 0.94 - version 0.99
     */
     public void flashIndexDelete(long time) {
        int choix = GuiUtilities.showOptionDialog(getMainWindow(),
                resources.getString("deleteIndex"), null, null);

        closeMainWindow();
        if(choix == GuiUtilities.YES_OPTION) {
            processBegin(false, "processingTitle", "processingMessage");
            core.removeIndexAtTime(time);
            processEnded();
        }
    }

    /**
     * affiche l'éditeur de texte.
     *
     * @param visible
     * @since version 0.94
     */
    public void flashText(boolean visible) {
        textEditor.showWindow(visible);
    }

    /**
     * charge un fichier texte.
     * 
     * @since version 0.94 - version 0.99
     */
    private void flashTextLoad() {
        fileChooser.setFileFilter(resources.getString("textFilter"), Constants.textExtension);

        //affiche la boite de dialogue et récupère le nom du fichier
        //si l'action à été validée
        File file = fileChooser.getSelectedFile(textEditor, FileChooser.LOAD);
        if(file != null) {
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
     * 
     * @since version 0.94 - version 0.99
     */
    private void flashTextErase() {
        int choix = GuiUtilities.showOptionDialog(textEditor,
                resources.getString("eraseTextConfirm"), null, null);
        if(choix == GuiUtilities.YES_OPTION) {
            core.eraseText();
        }
    }

    /**
     * Retourne le fenêtre principale tout en la mettant eu premier plan.
     * 
     * @return la fenêtre principale.
     * @since version 0.94
     */
    private Window getMainWindow() {
        window.setAlwaysOnTop(true);
        window.setVisible(true);
        return window;
    }

    /**
     * 
     * @since version 0.94 - version 0.95.12
     */
    private void closeMainWindow() {
        importDialog.close();
        exportDialog.close();
        eraseDialog.close();
        processingBar.close();
        window.setVisible(false);
    }

    /**
     * 
     * @param text 
     * @since version 0.94 - version 0.95.13
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
     * 
     * @param state 
     * @since version 0.94
     */
    public void old_runningStateChanged(final int state) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(indexEditor.isVisible())
                    indexEditor.updateButtons((state==Constants.PLAYING));
            }
        });
    }

}//end
