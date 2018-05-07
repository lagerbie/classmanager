package eestudio.utils;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import eestudio.Constants;
import eestudio.Indexes;
import eestudio.ProjectFiles;

/*
 * v0.95: ajout de public static final Font defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
 * v0.95: ajout de private static boolean fileHasExtension(File file, String[] extensions)
 * v0.95: ajout de public static boolean html2rtf(File srcFile, File destFile)
 * v0.95: ajout de public static boolean rtf2html(File srcFile, File destFile)
 * v0.95: ajout de private static Document readStyledFile(File srcFile, EditorKit editorKit)
 * v0.95: ajout de private static boolean writeStyledFile(File destFile, JTextPane textPane)
 * v0.95: ajout de private static void addText(JTextPane textPane, Element element)
 *        throws BadLocationException
 * v0.95: ajout de private static void addText(JTextPane textPane, String text,
 *        AttributeSet attributeSet) throws BadLocationException
 * v0.95: ajout de public static boolean isBusyPort(int port)
 * v0.95: ajout de public static Process startProcess(String command,
 *        StringBuilder output, StringBuilder error)
 * v0.95: ajout de public static File getResource(String resourcePath, File destDirectory)
 * v0.95: ajout de public static void waitInNanosecond(long nanoseconds)
 * v0.95: modif de isImageFile(File file) [use fileHasExtension]
 * v0.95: modif de isAudioFile(File file) [use fileHasExtension]
 * v0.95: modif de isVideoFile(File file) [use fileHasExtension]
 * v0.95: modif de isTextFile(File file) [use fileHasExtension]
 * v0.95: modif de isTextStyledFile(File file) [use fileHasExtension]
 * v0.95: modif de isSubtitleFile(File file) [use fileHasExtension]
 * v0.95: modif de returnFileWithExtension(..) [use extension.equalsIgnoreCase(.)
 *        et plus getExtensionFile(file.equalsIgnoreCase(.)]
 * v0.95: modif de loadIndexes(..) en getIndexes(..) [load old version]
 * v0.95: modif de loadProject(..) en getProject(..) [load old version]
 * v0.95: modif de executeCommand(String command, ..) [process.destroy() à la fin]
 * v0.95: modif de executeCommand(String[] command, ..) [process.destroy() à la fin]
 * 
 * v0.96: ajout de public static final String UTF8 = "UTF-8";
 * v0.96: ajout de public static File searchFile(File directory, String extension)
 * v0.96: ajout de public static void killApplication(String application)
 * v0.96: ajout de private static void killApplicationOnLinux(String application)
 * v0.96: ajout de private static void killApplicationOnWindows(String application)
 * v0.96: supp de public static String osName()
 * v0.96: supp de public static void fileMove(File source, File dest)
 * v0.96: supp de public static int executeCommand(String command,
 *        StringBuilder output, StringBuilder error)
 * v0.96: supp de public static String getApplicationPath(Class<?> c)
 * v0.96: supp de public static void waitInNanosecond(long nanoseconds)
 * v0.96: modif de html2rtf(File srcFile, File destFile) [libération ressources]
 * v0.96: modif de rtf2html(File srcFile, File destFile) [libération ressources]
 * 
 * v0.97: ajout de public static final String DOS_CHARSET = "IBM850";
 * v0.97: ajout de public static final String WINDOWS_CHARSET = "windows-1252";
 * v0.97: modif de UTF8 en UTF8_CHARSET
 * v0.97: supp de public static boolean isDiaporamaFile(File file)
 * v0.97: supp de public static boolean isAudioFile(File file)
 * v0.97: supp de public static boolean isVideoFile(File file)
 * v0.97: supp de public static boolean isSubtitleFile(File file)
 * v0.97: supp de public static File searchFile(File directory, String name, int type)
 * v0.97: modif de getFileType(File file) [supp VIDEO_FILE, AUDIO_FILE, DIAPORAMA_FILE]
 * v0.97: modif de executeCommand(String[] command, ..) [ajout charset]
 * v0.97: modif de startProcess(String command,..) [ajout charset]
 * v0.97: modif de createReadThread(..) en createReadThread(.., String charset)
 * 
 * v0.98: supp de implements Constants
 * v0.98: supp de public static final String UTF8_CHARSET = "UTF-8";
 * v0.98: supp de public static final String DOS_CHARSET = "IBM850";
 * v0.98: supp de public static final String WINDOWS_CHARSET = "windows-1252";
 * v0.98: ajout de public static void fileDirectoryCopy(File srcDirectory, File destDirectory)
 * v0.98: ajout de public static void deteleFiles(File directory)
 * v0.98: modif de fileHasExtension(File file, String[] extensions) [boucle for]
 * v0.98: modif de searchFile(File directory, String name, String... extensions)
 *        [variable boucle for]
 * v0.98: modif de isBusyPort(int port) [libération ressources]
 * 
 * v0.99: ajout de public static File getApplicationPathOnLinux(String name)
 * 
 * v1.00: supp de public static int getFileType(File file)
 * 
 * v1.02: modif de fileCopy(File source, File dest) [fermeture flux dans finally]
 */

/**
 * Utilitaires divers.
 *
 * @author Fabrice Alleau
 * @since version 0.94
 * @version 1.02
 */
public class Utilities {
    /** Font par defaut (Sans_serif, 12) */
    public static final Font defaultFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);

    /**
     * Parse la chaîne de caractères comme un entier (int).
     *
     * @param value la chaîne de caractères.
     * @return l'entier correspondant ou -1.
     * @since version 0.94
     */
    public static int parseStringAsInt(String value) {
        int parseValue = -1;
        try {
            if(value != null)
                parseValue = Integer.parseInt(value);
        } catch(NumberFormatException e) {
            Edu4Logger.warning("parseStringAsInt: " + e.getMessage());
        }
        return parseValue;
    }

    /**
     * Parse la chaîne de caractères comme un entier long (long).
     *
     * @param value la chaîne de caractères.
     * @return l'entier correspondant ou -1.
     * @since version 0.94
     */
    public static long parseStringAsLong(String value) {
        long parseValue = -1;
        try {
            if(value != null)
                parseValue = Long.parseLong(value);
        } catch(NumberFormatException e) {
            Edu4Logger.warning("parseStringAsLong: " + e.getMessage());
        }
        return parseValue;
    }

    /**
     * Parse la chaîne de caractères comme un nombre (float).
     *
     * @param value la chaîne de caractères.
     * @return le nombre correspondant ou -1.
     * @since version 0.94
     */
    public static float parseStringAsFloat(String value) {
        float parseValue = -1;
        try {
            if(value != null)
                parseValue = Float.parseFloat(value);
        } catch(NumberFormatException e) {
            Edu4Logger.warning("parseStringAsDouble: " + e.getMessage());
        }
        return parseValue;
    }

    /**
     * Parse la chaîne de caractères comme un nombre (double).
     *
     * @param value la chaîne de caractères.
     * @return le nombre correspondant ou -1.
     * @since version 0.94
     */
    public static double parseStringAsDouble(String value) {
        double parseValue = -1;
        try {
            if(value != null)
                parseValue = Double.parseDouble(value);
        } catch(NumberFormatException e) {
            Edu4Logger.warning("parseStringAsDouble: " + e.getMessage());
        }
        return parseValue;
    }

    /**
     * Parse la chaîne de caractères comme un booléen (boolean).
     *
     * @param value la chaîne de caractères.
     * @return le booléen correspondant (par défaut false).
     * @since version 0.94
     */
    public static boolean parseStringAsBoolean(String value) {
        boolean parseValue = false;
        try {
            if(value != null)
                parseValue = Boolean.parseBoolean(value);
        } catch(NumberFormatException e) {
            Edu4Logger.warning("parseStringAsBoolean: " + e.getMessage());
        }
        return parseValue;
    }

    /**
     * Indique si le fichier porte l'extension des fichiers Edu4.
     *
     * @param file le fichier.
     * @return si le fichier est du type Edu4.
     * @since version 0.94
     */
    public static boolean isEdu4File(File file) {
        return file.getName().toLowerCase().endsWith(Constants.edu4Extension);
    }

    /**
     * Indique si le fichier porte l'extension des fichiers Project.
     *
     * @param file le fichier.
     * @return si le fichier est du type Project.
     * @since version 0.94
     */
    public static boolean isProjectFile(File file) {
        return file.getName().toLowerCase().endsWith(Constants.projectExtension);
    }

    /**
     * Indique si le fichier porte l'extension des fichiers Index.
     *
     * @param file le fichier.
     * @return si le fichier est du type Index.
     * @since version 0.94
     */
    public static boolean isIndexFile(File file) {
        return file.getName().toLowerCase().endsWith(Constants.indexesExtension);
    }

    /**
     * Indique si le fichier porte une des extensions de fichiers image supportées.
     *
     * @param file le fichier.
     * @return si le fichier est du type Image.
     * @since version 0.94 - version 0.95
     */
    public static boolean isImageFile(File file) {
        return fileHasExtension(file, Constants.imageExtension);
    }

    /**
     * Indique si le fichier porte une des extensions de fichiers texte supportées.
     * 
     * @param file le fichier.
     * @return si le fichier est du type texte.
     * @since version 0.94 - version 0.95
     */
    public static boolean isTextFile(File file) {
        return fileHasExtension(file, Constants.textExtension);
    }

    /**
     * Indique si le fichier porte une des extensions de fichiers texte avec
     * gestion de styles supportées.
     *
     * @param file le fichier.
     * @return si le fichier est du type texte formatté (HTML, RTF).
     * @since version 0.94 - version 0.95
     */
    public static boolean isTextStyledFile(File file) {
        return fileHasExtension(file, Constants.textStyledExtension);
    }

    /**
     * Indique si le fichier porte une des extensions.
     *
     * @param file le fichier.
     * @param extensions les différentes extensions possibles.
     * @return si le fichier est une des extensions.
     * @since version 0.95 - version 0.98
     */
    private static boolean fileHasExtension(File file, String[] extensions) {
        boolean has = false;
        String fileExtension = getExtensionFile(file);
        if(fileExtension == null)
            return false;

        for(String extension : extensions) {
            if(fileExtension.endsWith(extension)) {
                has = true;
                break;
            }//end if
        }//end for
        return has;
    }

//    /**
//     * Retourne le type du fichier selon son extension.
//     *
//     * @param file le fichier.
//     * @return le type du fichier ou <code>-1</code> si inconnu.
//     * @since version 0.94 - version 0.97
//     */
//    public static int getFileType(File file) {
//        if(isEdu4File(file))
//            return Constants.EDU4_FILE;
//        else if(isProjectFile(file))
//            return Constants.PROJECT_FILE;
//        else if(isIndexFile(file))
//            return Constants.INDEX_FILE;
//        else if(isImageFile(file))
//            return Constants.IMAGE_FILE;
//        else if(fileHasExtension(file, Constants.subtitleExtension))
//            return Constants.SUBTITLE_FILE;
//        else if(isTextFile(file))
//            return Constants.TEXT_FILE;
//        else
//            return -1;
//    }

    /**
     * Retourne l'extension d'un fichier.
     *
     * @param file le fichier.
     * @return l'extension ou <code>null</code>.
     * @since version 0.94
     */
    public static String getExtensionFile(File file) {
        String extension = null;
        String name = file.getName().toLowerCase();
        int index = name.lastIndexOf('.');
        if(index >= 0)
            extension = name.substring(index);
        return extension;
    }

    /**
     * Retourne le nom du fichier sans l'extension.
     *
     * @param file le fichier.
     * @return le nom du fichier sans l'extension.
     * @since version 0.94
     */
    public static String getNameWithoutExtension(File file) {
        String name = file.getName();
        int index = name.lastIndexOf('.');
        if(index < 0)
            index = name.length();
        name = name.substring(0, index);
        return name;
    }

    /**
     * Recherche un fichier existant dans le répertoire, avec le nom et une liste
     * possible d'extensions du fichier.
     *
     * @param directory le répertoire de recherche.
     * @param name le nom du fichier sans extension.
     * @param extensions la liste d'extensions possible du fichier.
     * @return le fichier existant trouvé ou <code>null</code>.
     * @since version 0.94 - version 0.98
     */
    public static File searchFile(File directory, String name, String... extensions) {
        for(String extension : extensions) {
            File searchFile = new File(directory, name+extension);
            if(searchFile.exists())
                return searchFile;
        }
        return null;
    }

    /**
     * Recherche un fichier existant dans le répertoire, avec le nom et une liste
     * possible d'extensions du fichier.
     *
     * @param directory le répertoire de recherche.
     * @param extension la liste d'extensions possible du fichier.
     * @return le fichier existant trouvé ou <code>null</code>.
     * @since version 0.96
     */
    public static File searchFile(File directory, String extension) {
        File[] files = directory.listFiles();
        for(File file : files) {
            if(file.getName().toLowerCase().endsWith(extension))
                return file;
        }
        return null;
    }

    /**
     * Retourne un fichier avec l'extension indiquée.
     *
     * @param file le fichier.
     * @param extension l'extension voulue.
     * @return le fichier avec automatiquement l'extension voulue.
     * @since version 0.94 - version 0.95
     */
    public static File returnFileWithExtension(File file, String extension) {
        if(extension.equalsIgnoreCase(getExtensionFile(file)))
            return file;
        else {
            String name = getNameWithoutExtension(file);
            return new File(file.getParentFile(), name+extension);
        }
    }

    /**
     * Récupère le texte du fichier suivant le chaset indiqué.
     *
     * @param file le fichier à lire.
     * @param charset le charset de lecture ("UTF-8", "windows-1252").
     * @return le texte décodé ou <code>null<\code>
     * @since version 0.94
     */
    public static String getTextInFile(File file, String charset) {
        StringBuilder text = new StringBuilder(1024);
        try {//pour le charset en UTF-8
            Scanner scanner = new Scanner(file, charset);
            while(scanner.hasNext()) {
                text.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
        } catch(FileNotFoundException e) {
            Edu4Logger.error(e);
            return null;
        }//end try

        if(text.length() == 0)
            return null;
        else
            //on enlève le dernier saut de ligne
            return text.substring(0, text.length()-1);
    }

    /**
     * Sauvegarde d'un texte brut dans un fichier.
     *
     * @param text le texte à sauvegarder.
     * @param file le fichier.
     * @return la réussite.
     * @since version 0.94
     */
    public static boolean saveText(String text, File file) {
        try {
            OutputStreamWriter writer
                    = new OutputStreamWriter(new FileOutputStream(file),
                            Constants.UTF8_CHARSET);
            // le texte doit être non null
            if(text == null)
                writer.write("");
            else
                writer.write(text);
            writer.close();
        } catch(IOException e) {
            Edu4Logger.error(e);
            return false;
        }//end try

        return true;
    }

    /**
     * Convertie un fichier au format html en un fichier au format rtf.
     *
     * @param srcFile le fichier au format html.
     * @param destFile le fichier au format rtf.
     * @return si le fichier à été créé.
     * @since version 0.95 - version 0.96
     */
    public static boolean html2rtf(File srcFile, File destFile) {
        EditorKit srcEditorKit = new HTMLEditorKit();
        Document srcDocument = readStyledFile(srcFile, srcEditorKit);
        if(srcDocument == null)
            return false;

        JTextPane textPane = new JTextPane();
        textPane.setFont(defaultFont);
        EditorKit destEditorKit = new RTFEditorKit();
        Document destDocument = destEditorKit.createDefaultDocument();
        textPane.setEditorKit(destEditorKit);
	textPane.setDocument(destDocument);

        try {
            //index=0 -> head
            //index=1 -> body
            Element rootElement = srcDocument.getDefaultRootElement().getElement(1);
            for(int i=0; i<rootElement.getElementCount(); i++) {
                Element element = rootElement.getElement(i);
                for(int j=0; j<element.getElementCount(); j++) {
                    Element child = element.getElement(j);
                    String text = srcDocument.getText(child.getStartOffset(),
                            child.getEndOffset()-child.getStartOffset());
                    AttributeSet attributeSet = child.getAttributes();
                    addText(textPane, text, attributeSet);
                }
            }
        } catch(BadLocationException e) {
            Edu4Logger.error(e);
            return false;
        }

        destEditorKit.deinstall(textPane);
        srcEditorKit.deinstall(textPane);
        textPane.removeAll();

        return writeStyledFile(destFile, textPane);
    }

    /**
     * Convertie un fichier au format rtf en un fichier au format html.
     *
     * @param srcFile le fichier au format rtf.
     * @param destFile le fichier au format html.
     * @return si le fichier à été créé.
     * @since version 0.95 - version 0.96
     */
    public static boolean rtf2html(File srcFile, File destFile) {
        EditorKit srcEditorKit = new RTFEditorKit();
        Document srcDocument = readStyledFile(srcFile, srcEditorKit);
        if(srcDocument == null)
            return false;

        JTextPane textPane = new JTextPane();
        textPane.setFont(defaultFont);
        EditorKit destEditorKit = new HTMLEditorKit();
        Document destDocument = destEditorKit.createDefaultDocument();
        textPane.setEditorKit(destEditorKit);
	textPane.setDocument(destDocument);

        try {
            Element rootElement = srcDocument.getDefaultRootElement();
            addText(textPane, rootElement);
        } catch(BadLocationException e) {
            Edu4Logger.error(e);
            return false;
        }

        destEditorKit.deinstall(textPane);
        srcEditorKit.deinstall(textPane);
        textPane.removeAll();

        return writeStyledFile(destFile, textPane);
    }

    /**
     * Lit un fichier texte avec gestion de style (html ou rtf).
     *
     * @param srcFile le fichier au format rtf ou html.
     * @param editorKit l'éditeur de style pour la lecture.
     * @return le document correspondant ou <code>null</code>.
     * @since version 0.95
     */
    private static Document readStyledFile(File srcFile, EditorKit editorKit) {
        Document document = editorKit.createDefaultDocument();
        try {
            FileInputStream fileInputStream = new FileInputStream(srcFile);
            editorKit.read(fileInputStream, document, 0);
   	    fileInputStream.close();
	} catch(Exception e) {
            Edu4Logger.error(e);
            document = null;
        }
        return document;
    }

    /**
     * Sauvegarde un texte avec gestion de style dans un fichier (html ou rtf).
     *
     * @param destFile le fichier au format rtf ou html.
     * @param textPane l'éditeur de style qui gère le document avec gestion de style.
     * @return la réussite de la sauvegarde.
     * @since version 0.95
     */
    private static boolean writeStyledFile(File destFile, JTextPane textPane) {
        boolean success = false;
        Document document = textPane.getStyledDocument();
        try {
            FileOutputStream output = new FileOutputStream(destFile);
            textPane.getEditorKit().write(output, document, 0, document.getLength());
            output.flush();
            output.close();
            success = true;
	} catch(Exception e) {
            Edu4Logger.error(e);
        }
        return success;
    }

    /**
     * Ajoute un élément de style au gestionnaire de documents stylés.
     *
     * @param textPane le gestionnaire de documents stylés.
     * @param element l'élément de style.
     * @throws BadLocationException
     * @since version 0.95
     */
    private static void addText(JTextPane textPane, Element element)
            throws BadLocationException {
        Document srcDocument = element.getDocument();
        for(int i=0; i<element.getElementCount(); i++) {
            Element child = element.getElement(i);

            if(child.getElementCount() > 0) {
                addText(textPane, child);
                continue;
            }
            String text = srcDocument.getText(child.getStartOffset(),
                    child.getEndOffset()-child.getStartOffset());
            AttributeSet attributeSet = child.getAttributes();
            addText(textPane, text, attributeSet);
         }
    }

    /**
     * Ajoute du texte avec un style au gestionnaire de documents stylés.
     *
     * @param textPane le gestionnaire de documents stylés.
     * @param text le texte.
     * @param attributeSet le style du texte.
     * @throws BadLocationException
     * @since version 0.95
     */
    private static void addText(JTextPane textPane, String text,
            AttributeSet attributeSet) throws BadLocationException {
        Document styledDocument = textPane.getStyledDocument();
        int startOffset = styledDocument.getLength();
        styledDocument.insertString(startOffset, text, null);

        textPane.select(startOffset, startOffset+text.length());

        MutableAttributeSet mutableAttributeSet = textPane.getInputAttributes();

        boolean bold = StyleConstants.isBold(attributeSet);
        boolean italic = StyleConstants.isItalic(attributeSet);
        boolean underline = StyleConstants.isUnderline(attributeSet);
        boolean strikeThrough = StyleConstants.isStrikeThrough(attributeSet);
        String fontName = StyleConstants.getFontFamily(attributeSet);
        if(fontName.equalsIgnoreCase(Font.MONOSPACED))
            fontName = defaultFont.getFontName();
        int size = StyleConstants.getFontSize(attributeSet);
        Color color = StyleConstants.getForeground(attributeSet);
//        int alignment = StyleConstants.getAlignment(attributeSet);

        // on applique les différents styles
        StyleConstants.setFontFamily(mutableAttributeSet, fontName);
        StyleConstants.setFontSize(mutableAttributeSet, size);
        StyleConstants.setForeground(mutableAttributeSet, color);

        StyleConstants.setBold(mutableAttributeSet, bold);
        StyleConstants.setItalic(mutableAttributeSet, italic);
        StyleConstants.setUnderline(mutableAttributeSet, underline);
        StyleConstants.setStrikeThrough(mutableAttributeSet, strikeThrough);

        textPane.setCharacterAttributes(mutableAttributeSet, true);

//        StyleConstants.setAlignment(mutableAttributeSet, alignment);
//        styledDocument.setParagraphAttributes(textPane.getSelectionStart(),
//                textPane.getSelectionEnd()-textPane.getSelectionStart(),
//                mutableAttributeSet, false);
    }

    /**
     * Extrait les fichiers d'une archive dans un répertoire.
     *
     * @param archive l'archive à extraire.
     * @param path le répertoire où extraire les fichiers.
     * @return la réussite de l'extraction.
     * @since version 0.94
     */
    public static boolean extractArchive(File archive, File path) {
        boolean success = false;
        try {
            ZipUtilities.unzipFileIntoDirectory(archive, path);
            success = true;
        } catch(IOException e) {
            Edu4Logger.error(e);
        }
        return success;
    }

    /**
     * Ajoute un répertoire ou un fichier dans une archive compressée.
     *
     * @param directory le répertoire ou le fichier.
     * @param archive l'archive compressée.
     * @return la réussite de l'opération.
     * @since version 0.94
     */
    public static boolean compressFile(File directory, File archive) {
        boolean success = false;
        try {
            ZipUtilities.fileToZip(directory, archive, true);
            success = true;
        } catch(IOException e) {
            Edu4Logger.error(e);
        }
        return success;
    }

    /**
     * Retourne la liste d'index contenu dans le fichier.
     *
     * @param file le fichier contenant la liste d'index.
     * @return la liste d'index ou une liste vide si pas d'index.
     * @since version 0.94 - version 0.95
     */
    public static Indexes getIndexes(File file) {
        Indexes indexes = XMLUtilities.loadIndexes(file);

        if(indexes.getIndexesCount() == 0)
            indexes = XMLUtilitiesOldVersion.loadIndexes(file);

        return indexes;
    }

    /**
     * Retourne le projet contenu dans le fichier.
     *
     * @param file le fichier contenant le projet.
     * @return le projet ou un projet vide.
     * @since version 0.94 - version 0.95
     */
    public static ProjectFiles getProject(File file) {
        ProjectFiles project = XMLUtilities.loadProject(file);

        if(project.isEmptyProject())
            project= XMLUtilitiesOldVersion.loadProject(file);

        return project;
    }

    /**
     * Sauvegarde la liste d'index dans un fichier.
     *
     * @param indexes la liste d'index.
     * @param file le fichier.
     * @return la réussite de l'opération.
     * @since version 0.94
     */
    public static boolean saveObject(Indexes indexes, File file) {
        return saveText(XMLUtilities.getXML(indexes), file);
    }

    /**
     * Sauvegarde le projet dans un fichier.
     *
     * @param project le projet.
     * @param file le fichier.
     * @return la réussite de l'opération.
     * @since version 0.94
     */
    public static boolean saveObject(ProjectFiles project, File file) {
        return saveText(XMLUtilities.getXML(project), file);
    }

    /**
     * Sauvegarde les soustitres aux format SubRip.
     *
     * @param indexes les index.
     * @param file le fichier.
     * @return <code>true<\code> si la sauvegarde s'est bien passée.
     * @since version 0.94
     */
    public static boolean saveSRTSubtitleFile(Indexes indexes, File file) {
        return Utilities.saveText(SubtitleUtilities.createSRTSubtitle(indexes), file);
    }

    /**
     * Sauvegarde les soustitres aux format SubViewer.
     *
     * @param indexes les index.
     * @param file le fichier.
     * @return <code>true<\code> si la sauvegarde s'est bien passée.
     * @since version 0.94
     */
    public static boolean saveSUBSubtitleFile(Indexes indexes, File file) {
        return Utilities.saveText(SubtitleUtilities.createSUBSubtitle(indexes), file);
    }

    /**
     * Sauvegarde les soustitres aux format LRC.
     *
     * @param indexes les index.
     * @param file le fichier.
     * @return <code>true<\code> si la sauvegarde s'est bien passée.
     * @since version 0.94
     */
    public static boolean saveLRCSubtitleFile(Indexes indexes, File file) {
        return Utilities.saveText(SubtitleUtilities.createLRCSubtitle(indexes), file);
    }

    /**
     * Copie un fichier.
     *
     * @param source le fichier source.
     * @param dest le fichier de destination.
     * @since version 0.94 - version 1.02
     */
    public static void fileCopy(File source, File dest) {
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();

            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        } catch(Exception e) {
            Edu4Logger.error(e);
        } finally {
            if(sourceChannel != null) {
                try {
                    sourceChannel.close();
                } catch(IOException e) {
                }
            }
            if(destChannel != null) {
                try {
                    destChannel.close();
                } catch(IOException e) {
                }
            }
        }
    }

    /**
     * Copie un fichier.
     *
     * @param srcDirectory le fichier source.
     * @param destDirectory le fichier de destination.
     * @since version 0.98
     */
    public static void fileDirectoryCopy(File srcDirectory, File destDirectory) {
        destDirectory.mkdirs();

        File[] files = srcDirectory.listFiles();
        for(File source : files) {
            File destFile = new File(destDirectory, source.getName());
            fileCopy(source, destFile);
        }
    }

    /**
     * Efface tous les fichiers et répetoires contenus dans le répertoire
     * indiqué.
     * 
     * @param directory le répartoire dont il faut effacer les fichiers.
     * @since version 0.98
     */
    public static void deteleFiles(File directory) {
        File[] files = directory.listFiles();
        for(File file : files) {
           if(file.isDirectory())
               deteleFiles(file);

           if(!file.delete())
               file.deleteOnExit();
        }
    }

    /**
     * Vérifie si un port est occupé.
     * 
     * @param port le numéro du port.
     * @return true si le port est occupé.
     * @since version 0.95 - version 0.98
     */
    public static boolean isBusyPort(int port) {
        boolean occuped = true;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            occuped = false;
        } catch(IOException e) {
            Edu4Logger.error(e);
        }
        finally {
            if(serverSocket != null) {
                try {
                    serverSocket.close();
                } catch(IOException e) {
                    Edu4Logger.error(e);
                }
            }
        }

        return occuped;
    }

    /**
     * Execute une commande native.
     *
     * @param command la commande.
     * @param output un StringBuilder initialisé pour afficher la sortie standard.
     * @param error un StringBuilder initialisé pour afficher la sortie des erreur.
     * @return la valeur de sortie du processus résultat de la commande.
     * @since version 0.94 - version 0.97
     */
    public static int executeCommand(String command,
            StringBuilder output, StringBuilder error) {
        int end = -1;
        Runtime runtime = Runtime.getRuntime();
        Process process = null;

        String charset = Constants.UTF8_CHARSET;
        if(Constants.WINDOWS_PLATFORM) {
            if(command.contains("cmd"))
                charset = Constants.DOS_CHARSET;
            else
                charset = Constants.WINDOWS_CHARSET;
        }

        try {
            process = runtime.exec(command);
            Thread outputThread = createReadThread(process.getInputStream(),
                    output, charset);
            Thread errorThread = createReadThread(process.getErrorStream(),
                    error, charset);
            outputThread.start();
            errorThread.start();

            try {
                end = process.waitFor();
            } catch(InterruptedException e) {
                Edu4Logger.error(e);
            }
        } catch(IOException e) {
            Edu4Logger.error(e);
        }

        if(process != null) {
            process.destroy();
        }

        return end;
    }

    /**
     * Execute une commande native.
     *
     * @param command la commande.
     * @param output un StringBuilder initialisé pour afficher la sortie standard.
     * @param error un StringBuilder initialisé pour afficher la sortie des erreur.
     * @return la valeur de sortie du processus résultat de la commande.
     * @since version 0.94 - version 0.97
     */
    public static int executeCommand(String[] command,
            StringBuilder output, StringBuilder error) {
        int end = -1;
        Runtime runtime = Runtime.getRuntime();
        Process process = null;

        String charset = Constants.UTF8_CHARSET;
        if(Constants.WINDOWS_PLATFORM) {
            if(command[0].contains("cmd"))
                charset = Constants.DOS_CHARSET;
            else
                charset = Constants.WINDOWS_CHARSET;
        }

        try {
            Edu4Logger.debug("execute command:" + command);
            process = runtime.exec(command);
            Thread outputThread = createReadThread(process.getInputStream(),
                    output, charset);
            Thread errorThread = createReadThread(process.getErrorStream(),
                    error, charset);
            outputThread.start();
            errorThread.start();

            try {
                end = process.waitFor();
            } catch(InterruptedException e) {
                Edu4Logger.error(e);
            }
        } catch(IOException e) {
            Edu4Logger.error(e);
        }

        if(process != null) {
            process.destroy();
        }

        return end;
    }

    /**
     * Démarre une commande native.
     *
     * @param command la commande.
     * @param output un StringBuilder initialisé pour afficher la sortie standard.
     * @param error un StringBuilder initialisé pour afficher la sortie des erreur.
     * @return le processus démarré ou <code>null</code>.
     * @since version 0.95 - version 0.97
     */
    public static Process startProcess(String command,
            StringBuilder output, StringBuilder error) {
        Runtime runtime = Runtime.getRuntime();
        Process process;

        String charset = Constants.UTF8_CHARSET;
        if(Constants.WINDOWS_PLATFORM) {
            if(command.contains("cmd"))
                charset = Constants.DOS_CHARSET;
            else
                charset = Constants.WINDOWS_CHARSET;
        }

        try {
            process = runtime.exec(command);
            Thread outputThread = createReadThread(process.getInputStream(),
                    output, charset);
            Thread errorThread = createReadThread(process.getErrorStream(),
                    error, charset);
            outputThread.start();
            errorThread.start();
        } catch(IOException e) {
            Edu4Logger.error(e);
            return null;
        }

        return process;
    }

    /**
     * Créer une thread de traitement d'un flux.
     *
     * @param inputStream le flux à gérer.
     * @param output un StringBuilder initialisé pour afficher la sortie.
     * @return la thread de gestion du flux.
     * @since version 0.94 - version 0.97
     */
    private static Thread createReadThread(final InputStream inputStream,
            final StringBuilder output, final String charset) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                byte[] data = new byte[1024];
                try {
                    int cnt = inputStream.read(data);
                    while(cnt > 0) {
                        output.append(new String(data, 0, cnt, charset));
                        cnt = inputStream.read(data);
                    }
                } catch(IOException e) {
                    Edu4Logger.error(e);
                }
            }//end run
        };
        return thread;
    }

    /**
     * Retourne le chemin d'une application suivant son nom sous Linux.
     * 
     * @param name le nom de l'application.
     * @return le chemin de l'application.
     * @since version 0.99
     */
    public static File getApplicationPathOnLinux(String name) {
        File file = null;
        String[] command = new String[]{"/bin/sh", "-c", "which " + name};
        StringBuilder input = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);
        
        executeCommand(command, input, error);
        if(!input.toString().isEmpty())
            file = new File(input.toString().trim());

        return file;
    }

    /**
     * Fermeture des applications interdites.
     *
     * @param application le nom de l'application.
     * @since version 0.96
     */
    public static void killApplication(String application) {
        if(Constants.WINDOWS_PLATFORM) {
            killApplicationOnWindows(application);
        }
        else if(Constants.LINUX_PLATFORM) {
            killApplicationOnLinux(application);
        }
    }

    /**
     * Fermeture des applications interdites sous Linux.
     *
     * @param application le nom de l'application.
     * @since version 0.96
     */
    private static void killApplicationOnLinux(String application) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Edu4Logger.info("pkill -f " + application);
            runtime.exec(new String[]{"/bin/sh", "-c", "pkill -f " + application});
        } catch(IOException e) {
            Edu4Logger.error(e);
        }
    }

    /**
     * Fermeture des applications interdites sous Windows.
     *
     * @param application le nom de l'application.
     * @since version 0.96
     */
    private static void killApplicationOnWindows(String application) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Edu4Logger.info("taskkill /F /IM " + application);
            runtime.exec("taskkill /F /IM " + application);
        } catch(IOException e) {
            Edu4Logger.error(e);
        }
    }

    /**
     * Récupère un fichier de resources inclu dans un jar.
     * 
     * @param resourcePath le chemin de la resource dans les jar.
     * @param destDirectory le répertoire ou sera la resource si il faut la décompressés.
     * @return le chemin de la resource trouvée.
     * @since version 0.95
     */
    public static File getResource(String resourcePath, File destDirectory) {
        String fileProtocol = "file:";
        String jarFileProtocol = "jar:file:";
        URL url = ClassLoader.getSystemResource(resourcePath);
        //file:/C:/..../beep.wav (si le fichier .jar n'existe pas) (Windows)
        //jar:file:/C:/.../eeStudio095.jar!/eestudio/beep.wav (Windows)
        //jar:file:/opt/.../eeStudio095.jar!/eestudio/beep.wav (Linux)
        if(url == null)
            return null;

        String path;
        try {
            path = URLDecoder.decode(url.toString(), Constants.UTF8_CHARSET);
        } catch(UnsupportedEncodingException e) {
            Edu4Logger.error(e);
            return null;
        }//end try

        int offset = 0;
        if(Constants.WINDOWS_PLATFORM) {
            offset = 1;
        }

        File destFile = null;
        if(path.startsWith(fileProtocol)) {
            //si le fichier n'est pas intégré dans le jar
            int begin = fileProtocol.length() + offset;
            destFile = new File(path.substring(begin));
        }
        else if(path.startsWith(jarFileProtocol)) {
            //fichier intégré dans le jar
            int begin = jarFileProtocol.length() + offset;
            int end = path.lastIndexOf('!');
            String jarFilePath = path.substring(begin, end);

            try {
                JarFile jarFile = new JarFile(jarFilePath);
                JarEntry entry = jarFile.getJarEntry(resourcePath);

                destFile = new File(destDirectory, resourcePath);
                destFile.getParentFile().mkdirs();//important de créer les répertoires
                destFile.createNewFile();

                FileOutputStream outputStream = new FileOutputStream(destFile);
                InputStream inputStream = jarFile.getInputStream(entry);

                byte[] data = new byte[1024];

                int read = inputStream.read(data);
                while(read > 0) {
                    outputStream.write(data, 0, read);
                    read = inputStream.read(data);
                }

                inputStream.close();
                outputStream.close();
            } catch(IOException e) {
                Edu4Logger.error(e);
                destFile = null;
            }
        }
        return destFile;
    }

    /**
     * Permet de suspendre une Thread pendant une certaine durée.
     *
     * @param millisecond la durée à attendre en millisecondes.
     * @since version 0.94
     */
    public static void waitInMillisecond(long millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch(InterruptedException e) {
            Edu4Logger.error(e);
        }
    }

}//end
