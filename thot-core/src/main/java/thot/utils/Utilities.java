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
package thot.utils;

import java.awt.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
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

import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.gui.GuiUtilities;
import thot.model.Constants;
import thot.model.Indexes;
import thot.model.ProjectFiles;

/**
 * Utilitaires divers.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Utilities {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Utilities.class);

    /**
     * Vérification si la plateforme est Linux.
     */
    public static final boolean LINUX_PLATFORM = Platform.isLinux();
    /**
     * Vérification si la plateforme est Windows.
     */
    public static final boolean WINDOWS_PLATFORM = Platform.isWindows();
    /**
     * Vérification si la plateforme est Machintoch.
     */
    public static final boolean MAC_PLATFORM = Platform.isMac();

    /**
     * Nom du charset pour le format UTF-8.
     */
    public static final String UTF8_CHARSET = "UTF-8";
    /**
     * Nom du charset pour le format des fenêtres DOS.
     */
    private static final String DOS_CHARSET = "IBM850";
    /**
     * Nom du charset pour le format par défaut de Windows.
     */
    public static final String WINDOWS_CHARSET = "windows-1252";

    /**
     * Parse la chaîne de caractères comme un entier (int).
     *
     * @param value la chaîne de caractères.
     *
     * @return l'entier correspondant ou -1.
     */
    public static int parseStringAsInt(String value) {
        int parseValue = -1;
        if (value != null) {
            try {
                parseValue = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                LOGGER.error("Impossible de parser '{}' as Int", e, value);
            }
        }
        return parseValue;
    }

    /**
     * Parse la chaîne de caractères comme un entier long (long).
     *
     * @param value la chaîne de caractères.
     *
     * @return l'entier correspondant ou -1.
     */
    public static long parseStringAsLong(String value) {
        long parseValue = -1;
        if (value != null) {
            try {
                parseValue = Long.parseLong(value);
            } catch (NumberFormatException e) {
                LOGGER.error("Impossible de parser '{}' as Long", e, value);
            }
        }
        return parseValue;
    }

    /**
     * Parse la chaîne de caractères comme un nombre (float).
     *
     * @param value la chaîne de caractères.
     *
     * @return le nombre correspondant ou -1.
     */
    public static float parseStringAsFloat(String value) {
        float parseValue = -1;
        try {
            if (value != null) {
                parseValue = Float.parseFloat(value);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Impossible de parser '{}' as Float", e, value);
        }
        return parseValue;
    }

    /**
     * Parse la chaîne de caractères comme un nombre (double).
     *
     * @param value la chaîne de caractères.
     *
     * @return le nombre correspondant ou -1.
     */
    public static double parseStringAsDouble(String value) {
        double parseValue = -1;
        if (value != null) {
            try {
                parseValue = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                LOGGER.error("Impossible de parser '{}' as Double", e, value);
            }
        }
        return parseValue;
    }

    /**
     * Parse la chaîne de caractères comme un booléen (boolean).
     *
     * @param value la chaîne de caractères.
     *
     * @return le booléen correspondant (par défaut false).
     */
    public static boolean parseStringAsBoolean(String value) {
        boolean parseValue = false;
        if (value != null) {
            try {
                parseValue = Boolean.parseBoolean(value);
            } catch (NumberFormatException e) {
                LOGGER.error("Impossible de parser '{}' as Boolean", e, value);
            }
        }
        return parseValue;
    }

    /**
     * Indique si le fichier porte l'extension des fichiers Projet
     *
     * @param file le fichier.
     *
     * @return si le fichier est du type Projet.
     */
    public static boolean isProjectFile(File file) {
        return file.getName().toLowerCase().endsWith(Constants.projectExtension);
    }

    /**
     * Indique si le fichier porte l'extension des fichiers Projet interne.
     *
     * @param file le fichier.
     *
     * @return si le fichier est du type Projet interne.
     */
    public static boolean isProjectInternFile(File file) {
        return file.getName().toLowerCase().endsWith(Constants.projectInternExtension);
    }

    /**
     * Indique si le fichier porte l'extension des fichiers Index.
     *
     * @param file le fichier.
     *
     * @return si le fichier est du type Index.
     */
    public static boolean isIndexFile(File file) {
        return file.getName().toLowerCase().endsWith(Constants.indexesExtension);
    }

    /**
     * Indique si le fichier porte une des extensions de fichiers image supportées.
     *
     * @param file le fichier.
     *
     * @return si le fichier est du type Image.
     */
    public static boolean isImageFile(File file) {
        return fileHasExtension(file, Constants.imageExtension);
    }

    /**
     * Indique si le fichier porte une des extensions de fichiers audio.
     * <p>
     * Liste d'extensions non exhautive.
     *
     * @param file le fichier.
     *
     * @return si le fichier est du type audio.
     */
    public static boolean isAudioFile(File file) {
        return fileHasExtension(file, Constants.audioExtension);
    }

    /**
     * Indique si le fichier porte une des extensions de fichiers texte supportées.
     * <p>
     * Liste d'extensions non exhautive.
     *
     * @param file le fichier.
     *
     * @return si le fichier est du type texte.
     */
    public static boolean isTextFile(File file) {
        return fileHasExtension(file, Constants.textExtension);
    }

    /**
     * Indique si le fichier porte une des extensions de fichiers texte avec gestion de styles supportées.
     *
     * @param file le fichier.
     *
     * @return si le fichier est du type texte formatté (HTML, RTF).
     */
    public static boolean isTextStyledFile(File file) {
        return fileHasExtension(file, Constants.textStyledExtension);
    }

    /**
     * Indique si le fichier porte une des extensions de fichiers de soustitres supportées.
     *
     * @param file le fichier.
     *
     * @return si le fichier est du type soustitres.
     */
    public static boolean isSubtitleFile(File file) {
        return fileHasExtension(file, Constants.subtitleExtension);
    }

    /**
     * Indique si le fichier porte une des extensions.
     *
     * @param file le fichier.
     * @param extensions les différentes extensions possibles.
     *
     * @return si le fichier est une des extensions.
     */
    private static boolean fileHasExtension(File file, String[] extensions) {
        boolean has = false;
        String fileExtension = getExtensionFile(file);
        if (fileExtension == null) {
            return false;
        }

        for (String extension : extensions) {
            if (fileExtension.endsWith(extension)) {
                has = true;
                break;
            }
        }
        return has;
    }

    /**
     * Retourne l'extension d'un fichier.
     *
     * @param file le fichier.
     *
     * @return l'extension ou <code>null</code>.
     */
    public static String getExtensionFile(File file) {
        String extension = null;
        String name = file.getName().toLowerCase();
        int index = name.lastIndexOf('.');
        if (index >= 0) {
            extension = name.substring(index);
        }
        return extension;
    }

    /**
     * Retourne le nom du fichier sans l'extension.
     *
     * @param file le fichier.
     *
     * @return le nom du fichier sans l'extension.
     */
    public static String getNameWithoutExtension(File file) {
        String name = file.getName();
        int index = name.lastIndexOf('.');
        if (index < 0) {
            index = name.length();
        }
        name = name.substring(0, index);
        return name;
    }

    /**
     * Recherche un fichier existant dans le répertoire, avec le nom et une liste possible d'extensions du fichier.
     *
     * @param directory le répertoire de recherche.
     * @param name le nom du fichier sans extension.
     * @param extensions la liste d'extensions possible du fichier.
     *
     * @return le fichier existant trouvé ou <code>null</code>.
     */
    public static File searchFile(File directory, String name, String... extensions) {
        for (String extension : extensions) {
            File searchFile = new File(directory, name + extension);
            if (searchFile.exists()) {
                return searchFile;
            }
        }
        return null;
    }

    /**
     * Recherche un fichier existant dans le répertoire, avec le nom et une liste possible d'extensions du fichier.
     *
     * @param directory le répertoire de recherche.
     * @param extension la liste d'extensions possible du fichier.
     *
     * @return le fichier existant trouvé ou <code>null</code>.
     */
    public static File searchFile(File directory, String extension) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().toLowerCase().endsWith(extension)) {
                    return file;
                }
            }
        }
        return null;
    }

    /**
     * Retourne un fichier avec l'extension indiquée.
     *
     * @param file le fichier.
     * @param extension l'extension voulue.
     *
     * @return le fichier avec automatiquement l'extension voulue.
     */
    public static File returnFileWithExtension(File file, String extension) {
        if (extension.equalsIgnoreCase(getExtensionFile(file))) {
            return file;
        } else {
            String name = getNameWithoutExtension(file);
            return new File(file.getParentFile(), name + extension);
        }
    }

    /**
     * Récupère le texte du fichier suivant le chaset indiqué.
     *
     * @param file le fichier à lire.
     * @param charset le charset de lecture ("UTF-8", "windows-1252").
     *
     * @return le texte décodé ou <code>null<\code>
     */
    public static String getTextInFile(File file, String charset) {
        StringBuilder text = new StringBuilder(1024);
        try (Scanner scanner = new Scanner(file, charset)) {
            while (scanner.hasNext()) {
                text.append(scanner.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("", e);
            return null;
        }

        if (text.length() == 0) {
            return null;
        } else { //on enlève le dernier saut de ligne
            return text.substring(0, text.length() - 1);
        }
    }

    /**
     * Sauvegarde d'un texte brut dans un fichier.
     *
     * @param text le texte à sauvegarder.
     * @param file le fichier.
     *
     * @return la réussite.
     */
    public static boolean saveText(String text, File file) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), UTF8_CHARSET)) {
            // le texte doit être non null
            if (text == null) {
                writer.write("");
            } else {
                writer.write(text);
            }
        } catch (IOException e) {
            LOGGER.error("", e);
            return false;
        }

        return true;
    }

    /**
     * Indique si le fichier est une archive jar.
     *
     * @param file le fichier à tester.
     *
     * @return <code>true</code> si le fichier est une archive jar.
     */
    public static boolean isJarFile(File file) {
        return file.getName().toLowerCase().endsWith(".jar");
    }

    /**
     * Convertie un fichier au format html en un fichier au format rtf.
     *
     * @param srcFile le fichier au format html.
     * @param destFile le fichier au format rtf.
     *
     * @return si le fichier à été créé.
     */
    public static boolean html2rtf(File srcFile, File destFile) {
        EditorKit srcEditorKit = new HTMLEditorKit();
        Document srcDocument = readStyledFile(srcFile, srcEditorKit);
        if (srcDocument == null) {
            return false;
        }

        JTextPane textPane = new JTextPane();
        textPane.setFont(GuiUtilities.defaultFont);
        EditorKit destEditorKit = new RTFEditorKit();
        Document destDocument = destEditorKit.createDefaultDocument();
        textPane.setEditorKit(destEditorKit);
        textPane.setDocument(destDocument);

        try {
            //index=0 -> head
            //index=1 -> body
            Element rootElement = srcDocument.getDefaultRootElement().getElement(1);
            for (int i = 0; i < rootElement.getElementCount(); i++) {
                Element element = rootElement.getElement(i);
                for (int j = 0; j < element.getElementCount(); j++) {
                    Element child = element.getElement(j);
                    String text = srcDocument.getText(child.getStartOffset(),
                            child.getEndOffset() - child.getStartOffset());
                    AttributeSet attributeSet = child.getAttributes();
                    addText(textPane, text, attributeSet);
                }
            }
        } catch (BadLocationException e) {
            LOGGER.error("", e);
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
     *
     * @return si le fichier à été créé.
     */
    public static boolean rtf2html(File srcFile, File destFile) {
        EditorKit srcEditorKit = new RTFEditorKit();
        Document srcDocument = readStyledFile(srcFile, srcEditorKit);
        if (srcDocument == null) {
            return false;
        }

        JTextPane textPane = new JTextPane();
        textPane.setFont(GuiUtilities.defaultFont);
        EditorKit destEditorKit = new HTMLEditorKit();
        Document destDocument = destEditorKit.createDefaultDocument();
        textPane.setEditorKit(destEditorKit);
        textPane.setDocument(destDocument);

        try {
            Element rootElement = srcDocument.getDefaultRootElement();
            addText(textPane, rootElement);
        } catch (BadLocationException e) {
            LOGGER.error("", e);
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
     *
     * @return le document correspondant ou <code>null</code>.
     */
    private static Document readStyledFile(File srcFile, EditorKit editorKit) {
        Document document = editorKit.createDefaultDocument();
        try (FileInputStream fileInputStream = new FileInputStream(srcFile)) {
            editorKit.read(fileInputStream, document, 0);
        } catch (Exception e) {
            LOGGER.error("", e);
            document = null;
        }
        return document;
    }

    /**
     * Sauvegarde un texte avec gestion de style dans un fichier (html ou rtf).
     *
     * @param destFile le fichier au format rtf ou html.
     * @param textPane l'éditeur de style qui gère le document avec gestion de style.
     *
     * @return la réussite de la sauvegarde.
     */
    private static boolean writeStyledFile(File destFile, JTextPane textPane) {
        boolean success = false;
        Document document = textPane.getStyledDocument();
        try (FileOutputStream output = new FileOutputStream(destFile)) {
            textPane.getEditorKit().write(output, document, 0, document.getLength());
            output.flush();
            success = true;
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return success;
    }

    /**
     * Ajoute un élément de style au gestionnaire de documents stylés.
     *
     * @param textPane le gestionnaire de documents stylés.
     * @param element l'élément de style.
     *
     * @throws BadLocationException
     */
    private static void addText(JTextPane textPane, Element element) throws BadLocationException {
        Document srcDocument = element.getDocument();
        for (int i = 0; i < element.getElementCount(); i++) {
            Element child = element.getElement(i);

            if (child.getElementCount() > 0) {
                addText(textPane, child);
                continue;
            }
            String text = srcDocument.getText(child.getStartOffset(),
                    child.getEndOffset() - child.getStartOffset());
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
     *
     * @throws BadLocationException
     */
    private static void addText(JTextPane textPane, String text, AttributeSet attributeSet)
            throws BadLocationException {
        Document styledDocument = textPane.getStyledDocument();
        int startOffset = styledDocument.getLength();
        styledDocument.insertString(startOffset, text, null);

        textPane.select(startOffset, startOffset + text.length());

        MutableAttributeSet mutableAttributeSet = textPane.getInputAttributes();

        boolean bold = StyleConstants.isBold(attributeSet);
        boolean italic = StyleConstants.isItalic(attributeSet);
        boolean underline = StyleConstants.isUnderline(attributeSet);
        boolean strikeThrough = StyleConstants.isStrikeThrough(attributeSet);
        String fontName = StyleConstants.getFontFamily(attributeSet);
        if (fontName.equalsIgnoreCase(Font.MONOSPACED)) {
            fontName = GuiUtilities.defaultFont.getFontName();
        }
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
//        styledDocument.setParagraphAttributes(textPane.getSelectionStart(), textPane.getSelectionEnd()-textPane.getSelectionStart(), mutableAttributeSet, false);
    }

    /**
     * Extrait les fichiers d'une archive dans un répertoire.
     *
     * @param archive l'archive à extraire.
     * @param path le répertoire où extraire les fichiers.
     *
     * @return la réussite de l'extraction.
     */
    public static boolean extractArchive(File archive, File path) {
        boolean success = false;
        try {
            ZipUtilities.unzipFileIntoDirectory(archive, path);
            success = true;
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return success;
    }

    /**
     * Ajoute un répertoire ou un fichier dans une archive compressée.
     *
     * @param directory le répertoire ou le fichier.
     * @param archive l'archive compressée.
     *
     * @return la réussite de l'opération.
     */
    public static boolean compressFile(File directory, File archive) {
        boolean success = false;
        try {
            ZipUtilities.fileToZip(directory, archive, true);
            success = true;
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return success;
    }

    /**
     * Retourne la liste d'index contenu dans le fichier.
     *
     * @param file le fichier contenant la liste d'index.
     *
     * @return la liste d'index ou une liste vide si pas d'index.
     */
    public static Indexes getIndexes(File file) {
        return LaboXMLUtilities.loadIndexes(file);
    }

    /**
     * Retourne le projet contenu dans le fichier.
     *
     * @param file le fichier contenant le projet.
     *
     * @return le projet ou un projet vide.
     */
    public static ProjectFiles getProject(File file) {
        return LaboXMLUtilities.loadProject(file);
    }

    /**
     * Sauvegarde la liste d'index dans un fichier.
     *
     * @param indexes la liste d'index.
     * @param file le fichier.
     *
     * @return la réussite de l'opération.
     */
    public static boolean saveObject(Indexes indexes, File file) {
        return saveText(LaboXMLUtilities.getXML(indexes), file);
    }

    /**
     * Sauvegarde le projet dans un fichier.
     *
     * @param project le projet.
     * @param file le fichier.
     *
     * @return la réussite de l'opération.
     */
    public static boolean saveObject(ProjectFiles project, File file) {
        return saveText(LaboXMLUtilities.getXML(project), file);
    }

    /**
     * Sauvegarde les soustitres aux format SubRip.
     *
     * @param indexes les index.
     * @param file le fichier.
     *
     * @return <code>true<\code> si la sauvegarde s'est bien passée.
     */
    public static boolean saveSRTSubtitleFile(Indexes indexes, File file) {
        return Utilities.saveText(SubtitleUtilities.createSRTSubtitle(indexes), file);
    }

    /**
     * Sauvegarde les soustitres aux format SubViewer.
     *
     * @param indexes les index.
     * @param file le fichier.
     *
     * @return <code>true<\code> si la sauvegarde s'est bien passée.
     */
    public static boolean saveSUBSubtitleFile(Indexes indexes, File file) {
        return Utilities.saveText(SubtitleUtilities.createSUBSubtitle(indexes), file);
    }

    /**
     * Sauvegarde les soustitres aux format LRC.
     *
     * @param indexes les index.
     * @param file le fichier.
     *
     * @return <code>true<\code> si la sauvegarde s'est bien passée.
     */
    public static boolean saveLRCSubtitleFile(Indexes indexes, File file) {
        return Utilities.saveText(SubtitleUtilities.createLRCSubtitle(indexes), file);
    }

    /**
     * Copie un fichier.
     *
     * @param source le fichier source.
     * @param dest le fichier de destination.
     */
    public static void fileCopy(File source, File dest) {
        FileChannel sourceChannel;
        FileChannel destChannel;

        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();

            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);

            sourceChannel.close();
            destChannel.close();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    /**
     * Copie un fichier.
     *
     * @param srcDirectory le fichier source.
     * @param destDirectory le fichier de destination.
     */
    public static void fileDirectoryCopy(File srcDirectory, File destDirectory) {
        destDirectory.mkdirs();

        File[] files = srcDirectory.listFiles();
        if (files != null) {
            for (File source : files) {
                File destFile = new File(destDirectory, source.getName());
                fileCopy(source, destFile);
            }
        }
    }

    /**
     * Efface tous les fichiers et répetoires contenus dans le répertoire indiqué.
     *
     * @param directory le répartoire dont il faut effacer les fichiers.
     */
    public static void deteleFiles(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deteleFiles(file);
                }

                if (!file.delete()) {
                    file.deleteOnExit();
                }
            }
        }
    }

    /**
     * Vérifie si un port est occupé.
     *
     * @param port le numéro du port.
     *
     * @return true si le port est occupé.
     */
    public static boolean isBusyPort(int port) {
        boolean occuped = true;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            occuped = false;
        } catch (IOException e) {
            LOGGER.error("", e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }

        return occuped;
    }

    /**
     * Envoi une commande xml à l'adresse et sur le port indiqués.
     *
     * @param xml la commande xml.
     * @param addressIP l'adresse d'envoi.
     * @param port le port d'envoi.
     *
     * @return 1 si la commande a été transmise, sinon l'erreur (<0).
     */
    public static boolean sendXml(String xml, String addressIP, int port) {
        if (xml == null) {
            return false;
        }

        InetSocketAddress sockaddr = new InetSocketAddress(addressIP, port);

        DataOutputStream outputStream;
        boolean send = false;
        try (Socket socket = new Socket();) {
            LOGGER.info("sendXML {} à {}:{}", xml, addressIP, port);
            socket.connect(sockaddr, Constants.TIME_MAX_FOR_ORDER);

            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.write(xml.getBytes(UTF8_CHARSET));
            //out.write((byte)0);
            outputStream.flush();
            send = true;
        } catch (IOException e) {
            LOGGER.error("erreur sendXML {} à {}:{}", e, xml, addressIP, port);
        }
        return send;
    }

    /**
     * Execute une commande native.
     *
     * @param name nom pour le process.
     * @param command la commande.
     * @param output un builder initialisé pour afficher la sortie standard.
     * @param error un builder initialisé pour afficher la sortie des erreur.
     *
     * @return la valeur de sortie du processus résultat de la commande.
     */
    public static int executeCommand(String name, StringBuilder output, StringBuilder error, String... command) {
        int end = -1;
        Runtime runtime = Runtime.getRuntime();
        Process process = null;

        String charset = UTF8_CHARSET;
        if (WINDOWS_PLATFORM) {
            if (command[0].contains("cmd")) {
                charset = DOS_CHARSET;
            } else {
                charset = WINDOWS_CHARSET;
            }
        }

        try {
            if (command.length == 1) {
                process = runtime.exec(command[0]);
            } else {
                process = runtime.exec(command);
            }
            Thread outputThread = createReadThread(name + " (out)", process.getInputStream(), output, charset);
            Thread errorThread = createReadThread(name + " (err)", process.getErrorStream(), error, charset);
            outputThread.start();
            errorThread.start();

            try {
                end = process.waitFor();
                while (outputThread.isAlive()) {
                    waitInMillisecond(10);
                }
            } catch (InterruptedException e) {
                LOGGER.error("", e);
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        if (process != null) {
            process.destroy();
        }
        return end;
    }

    /**
     * Démarre une commande native.
     *
     * @param name nom pour le process.
     * @param command la commande.
     * @param output un builder initialisé pour afficher la sortie standard.
     * @param error un builder initialisé pour afficher la sortie des erreur.
     *
     * @return le processus démarré ou <code>null</code>.
     */
    public static Process startProcess(String name, StringBuilder output, StringBuilder error, String... command) {
        return startProcess(name, output, error, null, command);
    }

    /**
     * Démarre une commande native.
     *
     * @param name nom pour le process.
     * @param command la commande.
     * @param output un builder initialisé pour afficher la sortie standard.
     * @param error un builder initialisé pour afficher la sortie des erreur.
     * @param workingDirectory le répertoire de travail.
     *
     * @return le processus démarré ou <code>null</code>.
     */
    public static Process startProcess(String name, StringBuilder output, StringBuilder error,
            File workingDirectory, String... command) {
        Runtime runtime = Runtime.getRuntime();
        Process process;

        String charset = UTF8_CHARSET;
        if (WINDOWS_PLATFORM) {
            if (command[0].contains("cmd")) {
                charset = DOS_CHARSET;
            } else {
                charset = WINDOWS_CHARSET;
            }
        }

        try {
            if (command.length == 1) {
                process = runtime.exec(command[0], null, workingDirectory);
            } else {
                process = runtime.exec(command, null, workingDirectory);
            }

            Thread outputThread = createReadThread(name + " (out)", process.getInputStream(), output, charset);
            Thread errorThread = createReadThread(name + " (err)", process.getErrorStream(), error, charset);
            outputThread.start();
            errorThread.start();
        } catch (IOException e) {
            LOGGER.error("", e);
            return null;
        }

        return process;
    }

    /**
     * Créer une thread de traitement d'un flux.
     *
     * @param name le nom de la thread.
     * @param inputStream le flux à gérer.
     * @param output un StringBuilder initialisé pour afficher la sortie.
     *
     * @return la thread de gestion du flux.
     */
    private static Thread createReadThread(final String name, final InputStream inputStream, final StringBuilder output,
            final String charset) {
        Thread thread = new Thread(name) {
            @Override
            public void run() {
                byte[] data = new byte[1024];
                try {
                    int cnt = inputStream.read(data);
                    while (cnt > 0) {
                        output.append(new String(data, 0, cnt, charset));
                        cnt = inputStream.read(data);
                    }
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        };
        return thread;
    }

    /**
     * Retourne le répertoire où est située le fichier associé à la classe.
     *
     * @param c la classe.
     *
     * @return le répertoire où est situé le fichier associé à la classe.
     */
    public static String getApplicationPath(Class<?> c) {
        String classPath = c.getName().replace(".", "/") + ".class";
        URL url = ClassLoader.getSystemResource(classPath);// "test/Launcher.class"

        if (url == null) {
            return null;
        }

        String path = url.toString();
        //file:/C:/..../Launcher.class (si le fichier .jar n'existe pas)
        //jar:file:/C:/.../test.jar!/test/Launcher.class (Windows)
        //jar:file:/opt/.../test.jar!/test/Launcher.class (Linux)

        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("", e);
            return null;
        }

        int offset = 0;
        if (WINDOWS_PLATFORM) {
            offset = 1;
        }

        int begin = path.indexOf('/') + offset;
        int end = path.indexOf('!');

        try {
            if (end < 0) {//no jar
                end = path.lastIndexOf('/') + 1;
                path = path.substring(begin, end);
            } else {
                path = path.substring(begin, end);// = current jar
                end = path.lastIndexOf('/') + 1;
                path = path.substring(0, end);
            }
        } catch (IndexOutOfBoundsException e) {
            LOGGER.error("", e);
            return null;
        }

        return path;
    }

    /**
     * Retourne le chemin d'une application suivant son nom sous Linux.
     *
     * @param name le nom de l'application.
     *
     * @return le chemin de l'application.
     */
    public static File getApplicationPathOnLinux(String name) {
        File file = null;
        String[] command = new String[]{"/bin/sh", "-c", "which " + name};
        StringBuilder input = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        executeCommand("which", input, error, command);
        if (!input.toString().isEmpty()) {
            file = new File(input.toString().trim());
        }

        return file;
    }

    /**
     * Retourne les chemins d'une commande suivant son nom sous Linux.
     *
     * @param name le nom de l'application.
     *
     * @return le chemin de l'application.
     *
     * @since version 1.82 - version 1.83
     */
    public static boolean hasFileOnLinux(String name) {
        boolean has = false;
        String[] command = new String[]{"/bin/sh", "-c", "whereis " + name};
        StringBuilder input = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        executeCommand("whereis", input, error, command);
        String[] split = input.toString().split(":");
        if (split.length > 1) {
            has = split[1].trim().contains(name);
        }
        return has;
    }

    /**
     * Fermeture des applications interdites.
     *
     * @param application le nom de l'application.
     */
    public static void killApplication(String application) {
        if (WINDOWS_PLATFORM) {
            killApplicationOnWindows(application);
        } else if (LINUX_PLATFORM) {
            killApplicationOnLinux(application);
        }
    }

    /**
     * Fermeture des applications interdites sous Linux.
     *
     * @param application le nom de l'application.
     */
    private static void killApplicationOnLinux(String application) {
        String[] command = new String[]{"/bin/sh", "-c", "pkill -f " + application};
        StringBuilder out = new StringBuilder(1024);
        StringBuilder err = new StringBuilder(1024);
        startProcess("pkill", out, err, command);
    }

    /**
     * Fermeture des applications interdites sous Windows.
     *
     * @param application le nom de l'application.
     */
    private static void killApplicationOnWindows(String application) {
        Runtime runtime = Runtime.getRuntime();
        try {
            LOGGER.info("taskkill /F /IM " + application);
            runtime.exec("taskkill /F /IM " + application);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    /**
     * Retourne la commande pour lancer JClic sous Windows.
     *
     * @return la commande pour lancer JClic sous Windows.
     */
    public static String getJClicCommand() {
        String command = "reg query HKLM\\SOFTWARE\\Classes\\JClic.install\\shell\\open\\command /ve";

        StringBuilder result = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);
        executeCommand("reg query", result, error, command);

        String[] splitResult = result.toString().split("REG_SZ");
        if (splitResult.length > 1) {
            return splitResult[splitResult.length - 1].trim();
        } else {
            return null;
        }
    }

    /**
     * Retourne la commande pour lancer JClicReports sous Windows.
     *
     * @return la commande pour lancer JClicReports sous Windows.
     */
    public static String getJClicReportsCommand() {
        String jclic = getJClicCommand();

        if (jclic == null) {
            return null;
        } else {
            return jclic.replace("jclic.jar", "jclicreports.jar");
        }
    }

    /**
     * Retourne le chemin absolu de l'application relative contenue dans les "Program Files" en testant les répertoires
     * 32 et 64bits.
     *
     * @param path le chemin relatif de l'application dans "Program Files".
     *
     * @return le chemin absolu de l'application.
     */
    public static File pathOnWindowsProgramFiles(String path) {
        File file;
        StringBuilder out = new StringBuilder(1024);
        StringBuilder err = new StringBuilder(1024);

        //Récupère le chemin de "Program Files" pour un Windows 64bits
        executeCommand("programFiles", out, err, "cmd /c echo %ProgramW6432%");
        String progamPath = out.toString().trim();

        //Si Windows 32bits
        if (progamPath.startsWith("%")) {
            out = new StringBuilder(1024);
            err = new StringBuilder(1024);
            executeCommand("programFiles", out, err, "cmd /c echo %ProgramFiles%");
            progamPath = out.toString().trim();

            file = new File(progamPath, path);
        } else {
            //Windows 64bits
            file = new File(progamPath, path);

            //Si ce n'est pas dans le répertoire des programmes 64bits, on
            //recherche dans les programmes 32bits
            if (!file.exists()) {
                out = new StringBuilder(1024);
                err = new StringBuilder(1024);
                executeCommand("programFiles", out, err, "cmd /c echo %ProgramFiles(x86)%");
                progamPath = out.toString().trim();

                file = new File(progamPath, path);
            }
        }

        return file;
    }

    /**
     * Retourne le chemin relatif dans "Program Files" d'après le chemin absolu.
     *
     * @param file le chemin absolu.
     *
     * @return le chemin relatif dans "Program Files" ou <code>null</code> fichier.
     */
    public static String pathWithoutWindowsProgramFiles(File file) {
        String absolutePath = file.getAbsolutePath();
        String relativePath = null;
        StringBuilder out = new StringBuilder(1024);
        StringBuilder err = new StringBuilder(1024);

        //Récupère le chemin de "Program Files" pour un Windows 64bits
        executeCommand("programFiles", out, err, "cmd /c echo %ProgramW6432%");
        String progamPath = out.toString().trim();

        if (progamPath.startsWith("%")) {
            //cas Windows 32bits
            out = new StringBuilder(1024);
            err = new StringBuilder(1024);
            executeCommand("programFiles", out, err, "cmd /c echo %ProgramFiles%");
            progamPath = out.toString().trim();
            //si dans répertoire le Program Files
            if (absolutePath.startsWith(progamPath)) {
                relativePath = absolutePath.substring(progamPath.length());
            }
        } else {
            //test si c'est dans le répertoire Program Files 32bits
            out = new StringBuilder(1024);
            err = new StringBuilder(1024);
            executeCommand("programFiles", out, err, "cmd /c echo %ProgramFiles(x86)%");
            String progamPath32bits = out.toString().trim();

            //cas Windows 64bits
            if (absolutePath.startsWith(progamPath)
                    && !absolutePath.startsWith(progamPath32bits)) {
                //si dans le répertoire Program Files 64bits
                relativePath = absolutePath.substring(progamPath.length());
            } else if (absolutePath.startsWith(progamPath32bits)) {
                //si dans le répertoire Program Files 32bits
                relativePath = absolutePath.substring(progamPath32bits.length());
            }
        }

        return relativePath;
    }

    /**
     * Récupère un fichier de resources inclu dans un jar.
     *
     * @param resourcePath le chemin de la resource dans les jar.
     *
     * @return le chemin de la resource trouvée.
     */
    public static InputStream getResource(String resourcePath) {
        return ClassLoader.getSystemResourceAsStream(resourcePath);
    }

    /**
     * Récupère un fichier de resources inclu dans un jar.
     *
     * @param resourcePath le chemin de la resource dans les jar.
     * @param destDirectory le répertoire ou sera la resource si il faut la décompressés.
     *
     * @return le chemin de la resource trouvée.
     */
    public static File getResource(String resourcePath, File destDirectory) {
        String fileProtocol = "file:";
        String jarFileProtocol = "jar:file:";
        URL url = ClassLoader.getSystemResource(resourcePath);
        //file:/C:/..../beep.wav (si le fichier .jar n'existe pas) (Windows)
        //jar:file:/C:/.../master066.jar!/supevision/beep.wav (Windows)
        //jar:file:/opt/.../master066.jar!/supervision/beep.wav (Linux)
        if (url == null) {
            return null;
        }

        String path;
        try {
            path = URLDecoder.decode(url.toString(), UTF8_CHARSET);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("", e);
            return null;
        }

        int offset = 0;
        if (WINDOWS_PLATFORM) {
            offset = 1;
        }

        File destFile = null;
        if (path.startsWith(fileProtocol)) {
            //si le fichier n'est pas intégré dans le jar
            int begin = fileProtocol.length() + offset;
            destFile = new File(path.substring(begin));
        } else if (path.startsWith(jarFileProtocol)) {
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

                try (FileOutputStream outputStream = new FileOutputStream(destFile);
                     InputStream inputStream = jarFile.getInputStream(entry)) {

                    byte[] data = new byte[1024];
                    int read = inputStream.read(data);
                    while (read > 0) {
                        outputStream.write(data, 0, read);
                        read = inputStream.read(data);
                    }
                }
            } catch (IOException e) {
                LOGGER.error("", e);
                destFile = null;
            }
        }
        return destFile;
    }

    /**
     * Lancement d'un clavier virtuel.
     */
    public static void virtualKeyboard() {
        if (WINDOWS_PLATFORM) {
            windowsVirtualKeyboard();
        }
    }

    /**
     * Lancement d'un clavier virtuel sous Windows.
     */
    private static void windowsVirtualKeyboard() {
        String command = "cmd /C osk";

        StringBuilder result = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);
        startProcess("virtualKeyboard", result, error, command);
    }

    /**
     * Retourne l'adresse IP sur le réseau.
     *
     * @return la première adresse IP valide sur le réseu connecté.
     */
    public static String getAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    if (inetAddress instanceof Inet4Address
                            && !inetAddress.isLoopbackAddress()) {
                        LOGGER.info("Nom Machine = " + inetAddress.getHostName());
                        LOGGER.info("IP = " + inetAddress.getHostAddress());

                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            LOGGER.error("", e);
        }

        try {
            InetAddress localHost = InetAddress.getLocalHost();
            LOGGER.info("Computer name = " + localHost.getHostName());
            LOGGER.info("IP = " + localHost.getHostAddress());
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            LOGGER.error("", e);
        }

        return null;
    }

    /**
     * Permet de suspendre une Thread pendant une certaine durée.
     *
     * @param millisecond la durée à attendre en millisecondes.
     */
    public static void waitInMillisecond(long millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    /**
     * Permet de suspendre une Thread pendant une certaine durée.
     *
     * @param nanoseconds la durée à attendre en nanosecondes.
     */
    public static void waitInNanosecond(long nanoseconds) {
        try {
            Thread.sleep(nanoseconds / 1000000, (int) (nanoseconds % 1000000));
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }
}
