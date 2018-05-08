package eestudio.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import eestudio.flash.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import thot.model.Index;
import thot.model.IndexFile;
import thot.model.IndexType;
import thot.model.Indexes;
import thot.model.ProjectFiles;
import thot.model.ProjectTarget;

/**
 * Utilitaires pour la manipulation de fichier XML.
 *
 * @author Fabrice Alleau
 */
@Deprecated
public class XMLUtilities {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtilities.class);

    /*
     * Descriptif pour la liste d'index:
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <indexes mode="exam" mediaLength="194000" length="194000" count="-1">
     *     <index type="play" initialTime="4000" finalTime="8000" read="0">
     *         <![CDATA[test &]]>
     *     </index>
     *     <index type="record" initialTime="8000" finalTime="12000" read="0">
     *         <![CDATA[test é]]>
     *     </index>
     *     <index type="play" initialTime="14000" finalTime="18000" read="0">
     *         <![CDATA[test >]]>
     *     </index>
     *     <index type="record" initialTime="18000" finalTime="22000" read="0">
     *         <![CDATA[test 4]]>
     *     </index>
     *     <index type="record" initialTime="25000" finalTime="30000" read="0">
     *         <![CDATA[test 5]]>
     *     </index>
     *     <index type="file" initialTime="35000" finalTime="40000" read="0">
     *         <![CDATA[test 5]]>
     *         <file>
     *             <![CDATA[fileName]]>
     *         </file>
     *     </index>
     * </indexes>
     */
    /**
     * Balise pour une liste d'index
     */
    private static final String element_indexes = "indexes";
    /**
     * Balise d'attribut pour le mode d'utilisation de la liste
     */
    private static final String attribut_mode = "mode";
    /**
     * Balise d'attribut pour la durée
     */
    private static final String attribut_length = "length";
    /**
     * Balise d'attribut pour la durée du média
     */
    private static final String attribut_mediaLength = "mediaLength";

    /**
     * Balise pour un index
     */
    public static final String element_index = "index";
    /**
     * Balise d'attribut pour le type d'index
     */
    private static final String attribut_type = "type";
    /**
     * Balise d'attribut pour le temps de départ
     */
    private static final String attribut_initialTime = "initialTime";
    /**
     * Balise d'attribut pour le temps de fin
     */
    private static final String attribut_finalTime = "finalTime";
    /**
     * Balise d'attribut pour le soustitre (unused sous-titre en CDATA)
     */
    private static final String attribut_subtitle = "subtitle";
    /**
     * Balise d'attribut pour le nombre de lecture
     */
    private static final String attribut_read = "read";
    /**
     * Balise d'attribut pour le nombre de lecture
     */
    private static final String attribut_rate = "rate";
    /**
     * Balise d'attribut pour le nom du fichier (unused cf element_file)
     */
    private static final String attribut_file = "file";
    /**
     * Balise pour le nom du fichier (nom en CDATA)
     */
    private static final String element_file = "file";

    /*
     * Descriptif pour le fichier projet:
     *
     * <?xml version="1.0" encoding="UTF-8" ?>
     * <project>
     *     <soft>
     *         <![CDATA[easyLab]]>          ou <![CDATA[common]]>
     *     </soft>
     *     <videoFile>
     *         <![CDATA[test.mp4]]>
     *     </videoFile>
     *     <audioFile>
     *         <![CDATA[test.mp3]]>
     *     </audioFile>
     *     <indexesFile>
     *         <![CDATA[test.index]]>
     *     </indexesFile>
     *     <textFile>
     *         <![CDATA[test.html]]>
     *     </textFile>
     *     <subtitleFile>
     *         <![CDATA[test.srt]]>
     *     </subtitleFile>
     *     <videoOriginalFile>
     *         <![CDATA[test_original.mp4]]>  (vidéo sans sous-titres incrustés)
     *     </videoOriginalFile>
     * </project>
     */
    /**
     * Balise pour un projet
     */
    private static final String element_project = "project";
    /**
     * Balise pour le nom du logiciel destinataire (nom en CDATA)
     */
    private static final String element_soft = "soft";
    /**
     * Balise pour le nom du fichier audio (nom en CDATA)
     */
    private static final String element_audioFile = "audioFile";
    /**
     * Balise pour le nom du vidéo (nom en CDATA)
     */
    private static final String element_videoFile = "videoFile";
    /**
     * Balise pour le nom du fichier d'index (nom en CDATA)
     */
    private static final String element_indexesFile = "indexesFile";
    /**
     * Balise pour le nom du fichier de soustitres (nom en CDATA)
     */
    private static final String element_subtitleFile = "subtitleFile";
    /**
     * Balise pour le nom du fichier texte (nom en CDATA)
     */
    private static final String element_textFile = "textFile";
    /**
     * Balise pour le nom du fichier des tags (nom en CDATA)
     */
    private static final String element_tagFile = "tagFile";
    /**
     * Balise pour le nom du vidéo (nom en CDATA)
     */
    private static final String element_videoOriginalFile = "videoOriginalFile";

    /*
     * Descriptif pour le fichier de tags:
     *
     * <?xml version="1.0" encoding="UTF-8" ?>
     * <tags>
     *     <title>
     *         <![CDATA[Titre]]>          ou <![CDATA[common]]>
     *     </title>
     *     <artist>
     *         <![CDATA[Artiste]]>
     *     </artist>
     *     <album>
     *         <![CDATA[Album pour fichier mp3]]>
     *     </album>
     *     <comments>
     *         <![CDATA[Commantaires courts]]>
     *     </comments>
     * </tags>
     */
    /**
     * Balise pour les tags
     */
    private static final String element_tags = "tags";
    /**
     * Balise pour le titre du document (en CDATA)
     */
    private static final String element_title = "title";
    /**
     * Balise pour le nom de l'artiste (en CDATA)
     */
    private static final String element_artist = "artist";
    /**
     * Balise pour le nom de l'album (en CDATA)
     */
    private static final String element_album = "album";
    /**
     * Balise pour les commantaires (en CDATA)
     */
    private static final String element_comments = "comments";

    /*
     * Descriptif pour la commande:
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <command>
     *     <action>
     *         action
     *         <parameter>
     *             <![CDATA[parameter]]>
     *         </parameter>
     *     </action>
     * </command>
     */
    /**
     * Balise pour une commande
     */
    private static final String element_command = "command";
    /**
     * Balise pour l'élément action
     */
    private static final String element_action = "action";
    /**
     * Balise pour le paramètre (en CDATA)
     */
    private static final String element_parameter = "parameter";

    /*
     * Descriptif pour la sauvegarde de la langue par défaut:
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <language>
     *      <language>String</language>
     * </language>
     */
    /**
     * Balise pour la langue
     */
    private static final String element_language = "language";

    /*
     * Descriptif pour la liste des versions des exécutable:
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <?xml version="1.0" encoding="UTF-8"?>
     * <command>
     *     <action>
     *         version
     *         <list>
     *             <name> version </name>
     *             <name> version </name>
     *         </list>
     *     </action>
     * </command>
     */
    /**
     * Balise pour une liste de versions
     */
    private static final String element_list = "list";

    /**
     * Entête du fichier xml
     */
    private static final String xml_header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";

    /**
     * Balise ouvrant une section CDATA
     */
    private static final String cdata_start = "<![CDATA[";
    /**
     * Balise fermant une section CDATA
     */
    private static final String cdata_end = "]]>";

//    /** Doctype pour une liste d'index (unused) */
//    private static final String indexes_DTD
//            = "<!DOCTYPE indexes ["
//            + "<!ELEMENT indexes"
//            + "]>";

    /**
     * Retourne la liste d'index contenue dans le fichier.
     *
     * @param file le fichier contenant la liste d'index.
     *
     * @return la liste d'index contenue dans le fichier ou une liste vide.
     */
    public static Indexes loadIndexes(File file) {
        Indexes indexes = null;
        // lecture du contenu d'un fichier XML avec DOM
        Document document = getDocument(file);

        //traitement du document
        if (document != null) {
            indexes = parseNodeAsIndexes(document.getDocumentElement());
        }

        if (indexes == null) {
            indexes = new Indexes();
        }

        return indexes;
    }

    /**
     * Retourne le projet contenu dans le fichier.
     *
     * @param file le fichier contenant le projet.
     *
     * @return le projet contenu dans le fichier ou un projet vide.
     */
    public static ProjectFiles loadProject(File file) {
        ProjectFiles project = null;
        // lecture du contenu d'un fichier XML avec DOM
        Document document = getDocument(file);

        //traitement du document
        if (document != null) {
            project = parseNodeAsProject(document.getDocumentElement());
        }

        if (project == null) {
            project = new ProjectFiles();
        }

        return project;
    }

    /**
     * Retourne les tags contenus dans le fichier.
     *
     * @param file le fichier contenant les tags.
     *
     * @return les tags contenus dans le fichier ou des tags vides.
     */
    public static TagList loadTags(File file) {
        TagList tags = null;
        // lecture du contenu d'un fichier XML avec DOM
        Document document = getDocument(file);

        //traitement du document
        if (document != null) {
            tags = parseNodeAsTags(document.getDocumentElement());
        }

        if (tags == null) {
            tags = new TagList();
        }

        return tags;
    }

    /**
     * Retourne le xml complet pour l'enregistrement de la langue.
     *
     * @param language la langue par defaut.
     *
     * @return le xml complet.
     */
    public static String getLanguageXML(String language) {
        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(element_language));
        element.append(createCDATAElement(element_language, language));
        element.append(createElementEnd(element_language));
        return xml_header + element.toString();
    }

    /**
     * Retourne le langue contenu dans le fichier XML.
     *
     * @param languageFile le fichier dde la langue par default.
     *
     * @return la langue par defaut.
     */
    public static String getLanguage(File languageFile) {
        String language = null;

        // lecture du contenu d'un fichier XML avec DOM
        Document document = getDocument(languageFile);

        //traitement du document
        if (document != null) {
            language = parseNodeAsLanguage(document.getDocumentElement());
        }

        return language;
    }

    /**
     * Retourne la liste de commandes contenue dans la chaine XML.
     *
     * @param xml le xml contenant les commandes.
     *
     * @return la liste de commandes contenue dans le xml ou une liste vide.
     */
    public static List<Command> parseCommand(String xml) {
        List<Command> commands = null;

        // lecture du contenu d'un fichier XML avec DOM
        Document document = getDocument(xml);

        //traitement du document
        if (document != null) {
            commands = parseNodeAsCommand(document.getDocumentElement());
        }

        if (commands == null) {
            commands = new ArrayList<>(0);
        }

        return commands;
    }

    /**
     * Retourne l'index contenue dans la chaîne de caractères.
     *
     * @param xml le xml contenant l'index.
     *
     * @return l'index contenu dans dans le xml ou <code>null</code>.
     */
    public static Index parseIndex(String xml) {
        Index index = null;
        // lecture du contenu d'un fichier XML avec DOM
        Document document = getDocument(xml);

        //traitement du document
        if (document != null) {
            index = parseNodeAsIndex(document.getDocumentElement());
        }

        return index;
    }

    /**
     * Retourne la liste de String contenue dans la chaine XML.
     *
     * @param xml le xml contenant la liste.
     *
     * @return la liste de String ou une liste vide.
     */
    public static List<String> parseList(String xml) {
        List<String> list = null;

        // lecture du contenu d'un fichier XML avec DOM
        Document document = getDocument(xml);

        //traitement du document
        if (document != null) {
            list = parseNodeAsList(document.getDocumentElement());
        }

        if (list == null) {
            list = new ArrayList<>(0);
        }

        return list;
    }

    /**
     * Retourne le xml complet de la liste d'index.
     *
     * @param indexes la liste d'index.
     *
     * @return le xml complet.
     */
    public static String getXML(Indexes indexes) {
        return xml_header + getXMLDescription(indexes);
    }

    /**
     * Retourne le xml complet du projet.
     *
     * @param project le projet.
     *
     * @return le xml complet.
     */
    public static String getXML(ProjectFiles project) {
        return xml_header + getXMLDescription(project);
    }

    /**
     * Retourne le xml complet es tags.
     *
     * @param tags les tags.
     *
     * @return le xml complet.
     */
    public static String getXML(TagList tags) {
        return xml_header + getXMLDescription(tags);
    }

    /**
     * Retourne le xml complet de la commande.
     *
     * @param command la commande.
     *
     * @return le xml complet.
     */
    public static String getXML(Command command) {
        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(element_command));
        element.append(getXMLDescription(command));
        element.append(createElementEnd(element_command));
        return xml_header + element.toString();
    }

    /**
     * Retourne une description XML de la liste d'index.
     *
     * @param indexes la liste des index.
     *
     * @return une description de la liste d'index.
     */
    public static String getXMLDescription(Indexes indexes) {
        StringBuilder attributes = new StringBuilder(256);
        attributes.append(createAttribute(attribut_mode, indexes.getMode()));
        attributes.append(createAttribute(attribut_length, Long.toString(indexes.getLength())));
        attributes.append(createAttribute(attribut_mediaLength, Long.toString(indexes.getMediaLength())));

        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(element_indexes, attributes));
        for (Iterator<Index> it = indexes.iterator(); it.hasNext(); ) {
            element.append(getXMLDescription(it.next()));
        }
        element.append(createElementEnd(element_indexes));

        return element.toString();
    }

    /**
     * Retourne une description XML de l'index.
     *
     * @param index l'index.
     *
     * @return une description de l'index.
     */
    public static String getXMLDescription(Index index) {
        if (index == null) {
            return getXMLDescription(new Index(IndexType.UNKNOWN));
        }
        StringBuilder attributes = new StringBuilder(256);
        attributes.append(createAttribute(attribut_type, index.getType().getName()));
        attributes.append(createAttribute(attribut_initialTime, Long.toString(index.getInitialTime())));
        attributes.append(createAttribute(attribut_finalTime, Long.toString(index.getFinalTime())));
        attributes.append(createAttribute(attribut_read, Integer.toString(index.getRead())));
        attributes.append(createAttribute(attribut_rate, Float.toString(index.getRate())));

        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(element_index, attributes));

        if (index.getSubtitle() != null) {
            element.append(createCDATA(index.getSubtitle()));
        }
        if (index instanceof IndexFile) {
            String value = ((IndexFile) index).getFileName();
            if (value != null) {
                element.append(createCDATAElement(element_file, value));
            }
        }
        element.append(createElementEnd(element_index));

        return element.toString();
    }

    /**
     * Retourne une description XML du projet.
     *
     * @param project le projet.
     *
     * @return une description du projet.
     */
    public static String getXMLDescription(ProjectFiles project) {
        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(element_project));

        if (project.getSoft() != null) {
            element.append(createCDATAElement(element_soft, project.getSoft().getName()));
        }
        if (project.getVideoFile() != null) {
            element.append(createCDATAElement(element_videoFile, project.getVideoFile()));
        }
        if (project.getAudioFile() != null) {
            element.append(createCDATAElement(element_audioFile, project.getAudioFile()));
        }
        if (project.getIndexesFile() != null) {
            element.append(createCDATAElement(element_indexesFile, project.getIndexesFile()));
        }
        if (project.getTextFile() != null) {
            element.append(createCDATAElement(element_textFile, project.getTextFile()));
        }
        if (project.getSubtitleFile() != null) {
            element.append(createCDATAElement(element_subtitleFile, project.getSubtitleFile()));
        }
        if (project.getTagFile() != null) {
            element.append(createCDATAElement(element_tagFile, project.getTagFile()));
        }
        if (project.getVideoOriginalFile() != null) {
            element.append(createCDATAElement(element_videoOriginalFile, project.getVideoOriginalFile()));
        }

        element.append(createElementEnd(element_project));

        return element.toString();
    }

    /**
     * Retourne une description des tags.
     *
     * @param tags les tags.
     *
     * @return une description des tags.
     */
    public static String getXMLDescription(TagList tags) {
        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(element_tags));

        if (tags.getTag(TagList.TITLE) != null) {
            element.append(createCDATAElement(element_title, tags.getTag(TagList.TITLE)));
        }
        if (tags.getTag(TagList.ARTIST) != null) {
            element.append(createCDATAElement(element_artist, tags.getTag(TagList.ARTIST)));
        }
        if (tags.getTag(TagList.ALBUM) != null) {
            element.append(createCDATAElement(element_album, tags.getTag(TagList.ALBUM)));
        }
        if (tags.getTag(TagList.COMMENT) != null) {
            element.append(createCDATAElement(element_indexesFile, tags.getTag(TagList.COMMENT)));
        }
        element.append(createElementEnd(element_tags));
        return element.toString();
    }

    /**
     * Retourne une description XML de la commande.
     *
     * @param command la commande.
     *
     * @return une description de la commande.
     */
    public static String getXMLDescription(Command command) {
        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(element_action));
        element.append(command.getAction());

        if (command.getParameter() != null) {
            if (command.getAction().equals(Command.INDEXES) || command.getAction().equals(Command.VERSION)) {
//                    || command.getAction().equals(Command.INDEX)) {
                element.append(command.getParameter());
//                element.append(createElementStart(element_parameter));
//                element.append(command.getParameter());
//                element.append(createElementEnd(element_parameter));
            } else {
                element.append(createCDATAElement(element_parameter, command.getParameter()));
            }
        }

        element.append(createElementEnd(element_action));

        return element.toString();
    }

    /**
     * Retourne une description XML de la liste d'index.
     *
     * @param list la liste des versions (nom suivi du numéro).
     *
     * @return une description de la liste d'index.
     */
    public static String getXMLDescription(List<String> list) {
        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(element_list));
        for (int i = 1; i < list.size(); i += 2) {
            element.append(createElement(list.get(i - 1), list.get(i)));
        }
        element.append(createElementEnd(element_list));

        return element.toString();
    }

    /**
     * Crée le descriptif de l'attribut de balise avec nom et valeur. Descriptif de la forme ' name="value"'.
     *
     * @param name le nom de l'attribut.
     * @param value la valeur de l'attribut.
     *
     * @return le descriptif de l'attribut.
     */
    private static String createAttribute(String name, String value) {
        StringBuilder attribute = new StringBuilder(64);
        attribute.append(" ");
        attribute.append(name);
        attribute.append("=\"");
        attribute.append(value);
        attribute.append("\"");
        return attribute.toString();
    }

    /**
     * Crée un balise de début. Forme '<name>'.
     *
     * @param name le nom de la balise.
     *
     * @return la basile de départ du xml.
     */
    private static String createElementStart(String name) {
        return createElementStart(name, null);
    }

    /**
     * Crée un balise de début avec une liste d'attributs. Forme '<name attribut="value" attribut="value">'.
     *
     * @param name le nom de la balise.
     * @param attributes la liste d'attributs déjà en forme pour le xml.
     *
     * @return la basile de départ du xml.
     */
    private static String createElementStart(String name, StringBuilder attributes) {
        StringBuilder element = new StringBuilder(32);
        element.append("<");
        element.append(name);
        if (attributes != null) {
            element.append(attributes);
        }
        element.append(">");
        return element.toString();
    }

    /**
     * Crée un balise de fin. Forme '</name>'.
     *
     * @param name le nom de la balise.
     *
     * @return la basile de fin du xml.
     */
    private static String createElementEnd(String name) {
        StringBuilder element = new StringBuilder(32);
        element.append("</");
        element.append(name);
        element.append(">");
        return element.toString();
    }

    /**
     * Crée une balise complete avec une section CDATA. Forme '<name><![CDATA[value]]></name>'.
     *
     * @param name le nom de la balise.
     * @param value la valeur de la section CDATA.
     *
     * @return la balise complete.
     */
    private static String createElement(String name, String value) {
        StringBuilder element = new StringBuilder(256);
        element.append(createElementStart(name));
        element.append(value);
        element.append(createElementEnd(name));
        return element.toString();
    }

    /**
     * Crée une balise complete avec une section CDATA. Forme '<name><![CDATA[value]]></name>'.
     *
     * @param name le nom de la balise.
     * @param value la valeur de la section CDATA.
     *
     * @return la balise complete.
     */
    private static String createCDATAElement(String name, String value) {
        StringBuilder element = new StringBuilder(256);
        element.append(createElementStart(name));
        element.append(createCDATA(value));
        element.append(createElementEnd(name));
        return element.toString();
    }

    /**
     * Crée une section CDATA. Forme '<![CDATA[value]]>'.
     *
     * @param value la valeur de la section CDATA.
     *
     * @return la section CDATA.
     */
    private static String createCDATA(String value) {
        StringBuilder cdata = new StringBuilder(256);
        cdata.append(cdata_start);
        cdata.append(value);
        cdata.append(cdata_end);
        return cdata.toString();
    }

    /**
     * Retourne le document xml contenu dans le fichier.
     *
     * @param file le fichier.
     *
     * @return le document xml.
     */
    protected static Document getDocument(File file) {
        Document document = null;

        // création d'une fabrique de documents
        DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

        try {
            // création d'un constructeur de documents
            DocumentBuilder constructeur = fabrique.newDocumentBuilder();

            // lecture du contenu d'un fichier XML avec DOM
            document = constructeur.parse(file);
            if (document != null) {
                removeEmptyTextNode(document);
            }
        } catch (ParserConfigurationException e) {
            LOGGER.error("", e);
        } catch (SAXException e) {
            LOGGER.error("", e);
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return document;
    }

    /**
     * Retourne le document xml contenu dans la chaîne de caractères.
     *
     * @param xml la chaîne de caractères contenant le xml.
     *
     * @return le document xml.
     */
    protected static Document getDocument(String xml) {
        Document document = null;

        // création d'une fabrique de documents
        DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

        try {
            // création d'un constructeur de documents
            DocumentBuilder constructeur = fabrique.newDocumentBuilder();

            document = constructeur.parse(
                    new ByteArrayInputStream(xml.getBytes("UTF-8")));
            if (document != null) {
                removeEmptyTextNode(document);
            }
        } catch (ParserConfigurationException e) {
            LOGGER.error("", e);
        } catch (SAXException e) {
            LOGGER.error("", e);
        } catch (IOException e) {
            LOGGER.error("", e);
        }

        return document;
    }

    /**
     * Parse le noeud xml comme si c'était une liste d'index.
     *
     * @param node le noeud xml.
     *
     * @return la liste d'index ou <code>null</code>.
     */
    private static Indexes parseNodeAsIndexes(Node node) {
        Indexes indexes = null;

        if (node.getNodeName().equals(element_indexes)) {
            indexes = new Indexes();

            if (node.hasAttributes()) {
                NamedNodeMap attributes = node.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node attribute = attributes.item(i);
                    String name = attribute.getNodeName();
                    if (name.equals(attribut_mode)) {
                        indexes.setMode(attribute.getNodeValue());
                    } else if (name.equals(attribut_mediaLength)) {
                        indexes.setMediaLength(Utilities.parseStringAsLong(attribute.getNodeValue()));
                    }
                }
            }

            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                Index index = parseNodeAsIndex(child);
                if (index != null) {
                    indexes.add(index);
                }
            }
        }

        return indexes;
    }

    /**
     * Parse le noeud xml comme si c'était un index.
     *
     * @param node le noeud xml.
     *
     * @return l'index ou <code>null</code>.
     */
    private static Index parseNodeAsIndex(Node node) {
        Index index = null;
        if (node.getNodeName().equals(element_index)) {
            index = new Index(IndexType.UNKNOWN);
            if (node.hasAttributes()) {
                NamedNodeMap attributes = node.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node attribute = attributes.item(i);
                    String name = attribute.getNodeName();
                    if (name.equals(attribut_type)) {
                        index.setType(IndexType.getIndexType(attribute.getNodeValue()));
                        if (index.isFileType()) {
                            index = convertToIndexFile(index);
                        }
                    } else if (name.equals(attribut_initialTime)) {
                        index.setInitialTime(Utilities.parseStringAsLong(attribute.getNodeValue()));
                    } else if (name.equals(attribut_finalTime)) {
                        index.setFinalTime(Utilities.parseStringAsLong(attribute.getNodeValue()));
                    } else if (name.equals(attribut_subtitle)) {
                        index.setSubtitle(attribute.getNodeValue());
                    } else if (name.equals(attribut_read)) {
                        index.setRead(Utilities.parseStringAsInt(attribute.getNodeValue()));
                    } else if (name.equals(attribut_rate)) {
                        index.setRate(Utilities.parseStringAsFloat(attribute.getNodeValue()));
                    } else if (name.equals(attribut_file)) {
                        index = convertToIndexFile(index);
                        ((IndexFile) index).setFileName(attribute.getNodeValue());
                    }
                }
            }
            if (node.hasChildNodes()) {
                NodeList children = node.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if ((child.getNodeType() == Node.CDATA_SECTION_NODE) || (child.getNodeType() == Node.TEXT_NODE)) {
                        index.setSubtitle(child.getNodeValue());
                    } else if (child.getNodeName().equals(element_file) && child.hasChildNodes()) {
                        index = convertToIndexFile(index);
                        ((IndexFile) index).setFileName(child.getFirstChild().getNodeValue());
                    }
                }
            }
        }
        return index;
    }

    /**
     * Parse le noeud xml comme si c'était un projet.
     *
     * @param node le noeud xml.
     *
     * @return le projet ou <code>null</code>.
     */
    private static ProjectFiles parseNodeAsProject(Node node) {
        ProjectFiles project = null;

        if (node.getNodeName().equals(element_project)) {
            project = new ProjectFiles();
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (!child.hasChildNodes()) {
                    continue;
                }
                String name = child.getNodeName();
                if (name.equals(element_soft)) {
                    project.setSoft(ProjectTarget.getProjectTarget(child.getFirstChild().getNodeValue()));
                }
                if (name.equals(element_videoFile)) {
                    project.setVideoFile(child.getFirstChild().getNodeValue());
                } else if (name.equals(element_audioFile)) {
                    project.setAudioFile(child.getFirstChild().getNodeValue());
                } else if (name.equals(element_textFile)) {
                    project.setTextFile(child.getFirstChild().getNodeValue());
                } else if (name.equals(element_indexesFile)) {
                    project.setIndexesFile(child.getFirstChild().getNodeValue());
                } else if (name.equals(element_subtitleFile)) {
                    project.setSubtitleFile(child.getFirstChild().getNodeValue());
                } else if (name.equals(element_tagFile)) {
                    project.setTagFile(child.getFirstChild().getNodeValue());
                } else if (name.equals(element_videoOriginalFile)) {
                    project.setVideoOriginalFile(child.getFirstChild().getNodeValue());
                }
            }
        }

        return project;
    }

    /**
     * Parse le noeud xml comme si c'était des tags.
     *
     * @param node le noeud xml.
     *
     * @return les tags ou <code>null</code>.
     */
    private static TagList parseNodeAsTags(Node node) {
        TagList tags = null;

        if (node.getNodeName().equals(element_tags)) {
            tags = new TagList();
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (!child.hasChildNodes()) {
                    continue;
                }
                String name = child.getNodeName();
                if (name.equals(element_title)) {
                    tags.putTag(TagList.TITLE, child.getFirstChild().getNodeValue());
                }
                if (name.equals(element_artist)) {
                    tags.putTag(TagList.ARTIST, child.getFirstChild().getNodeValue());
                } else if (name.equals(element_album)) {
                    tags.putTag(TagList.ALBUM, child.getFirstChild().getNodeValue());
                } else if (name.equals(element_comments)) {
                    tags.putTag(TagList.COMMENT, child.getFirstChild().getNodeValue());
                }
            }
        }

        return tags;
    }

    /**
     * Parse le noeud xml comme si c'était une liste de commandes.
     *
     * @param node le noeud xml.
     *
     * @return la listes des commandes ou <code>null</code>.
     */
    private static List<Command> parseNodeAsCommand(Node node) {
        List<Command> commands = null;

        if (node.getNodeName().equals(element_command)) {
            commands = new ArrayList<>(2);
            NodeList actions = node.getChildNodes();
            for (int i = 0; i < actions.getLength(); i++) {
                Node action = actions.item(i);
                if (action.getNodeName().equals(element_action)) {
                    Command command = new Command(Command.UNKNOWN);
                    if (action.hasChildNodes()) {
                        NodeList children = action.getChildNodes();
                        for (int j = 0; j < children.getLength(); j++) {
                            Node child = children.item(j);
                            if ((child.getNodeType() == Node.CDATA_SECTION_NODE) || (child.getNodeType()
                                    == Node.TEXT_NODE)) {
                                command.setAction(child.getNodeValue());
                            } else if (child.getNodeName().equals(element_parameter) && child.hasChildNodes()) {
                                command.setParameter(child.getFirstChild().getNodeValue());
                            } else if (child.getNodeName().equals(element_index)) {
                                Index index = parseNodeAsIndex(child);
                                command.setParameter(getXMLDescription(index));
                            }
                        }
                    }
                    commands.add(command);
                }
            }
        }

        return commands;
    }

    /**
     * Parse le noeud xml comme si c'était une langue.
     *
     * @param node le noeud xml.
     *
     * @return la langue ou <code>null</code>.
     */
    private static String parseNodeAsLanguage(Node node) {
        String language = null;

        if (node.getNodeName().equals(element_language)) {
            if (node.hasChildNodes()) {
                NodeList nodes = node.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node child = nodes.item(i);
                    if (child.getNodeName().equalsIgnoreCase(element_language) && child.getFirstChild() != null) {
                        language = child.getFirstChild().getNodeValue();
                        break;
                    }
                }
            }
        }
        return language;
    }

    /**
     * Parse le noeud xml comme si c'était une liste de String.
     *
     * @param node le noeud xml.
     *
     * @return la liste de String.
     */
    private static List<String> parseNodeAsList(Node node) {
        List<String> list = new ArrayList<>(8);
        NodeList childList = node.getChildNodes();

        for (int i = 0; i < childList.getLength(); i++) {
            Node child = childList.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getNodeValue() != null) {
                list.add(child.getNodeValue());
            } else if (child.getFirstChild() != null
                    && child.getFirstChild().getNodeValue() != null) {
                list.add(child.getFirstChild().getNodeValue());
            }
        }
        return list;
    }

    /**
     * Convertie un index en un index d'insertion de fichier.
     *
     * @param index à convertir.
     *
     * @return l'index converti.
     */
    private static Index convertToIndexFile(Index index) {
        Index indexFile;
        if (index instanceof IndexFile) {
            indexFile = index;
        } else {
            indexFile = new IndexFile(null);
            indexFile.setInitialTime(index.getInitialTime());
            indexFile.setFinalTime(index.getFinalTime());
            indexFile.setSubtitle(index.getSubtitle());
            indexFile.setRead(index.getRead());
        }

        return indexFile;
    }

    /**
     * Supprime les noeuds texte contenu dans le noeud ne contenant aucune donnée.
     *
     * @param node le noeud à nettoyer.
     */
    private static void removeEmptyTextNode(Node node) {
        List<Node> removeList = new ArrayList<>(8);

        if (node.hasChildNodes()) {
            NodeList list = node.getChildNodes();
            for (int i = 0; i < list.getLength(); i++) {
                Node child = list.item(i);
                if (child.getNodeType() == Node.TEXT_NODE) {
                    String value = child.getNodeValue();
                    if (value == null || value.trim().isEmpty()) {
                        removeList.add(child);
                    }
                } else {
                    removeEmptyTextNode(child);
                }

            }
        }

        for (Node child : removeList) {
            node.removeChild(child);
        }
    }

//    public static void main(String[] args) {
//        parseDOM(xml);
//    }
//
//    protected static void parseDOM(String xml) {
//        try {
//            // création d'une fabrique de documents
//            DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
//            // création d'un constructeur de documents
//            DocumentBuilder constructeur = fabrique.newDocumentBuilder();
//            Document document = constructeur.parse(
//                new ByteArrayInputStream(xml.getBytes()));
//            removeEmptyTextNode(document);
//            println("document:");
//            printNodeInfo(document.getDocumentElement());
//            Indexes indexes = parseNodeAsIndexes(document.getDocumentElement());
//            if(indexes != null) {
//                printIndexesInfo(indexes);
//                println("xml:" + getXMLDescription(indexes));
//            }
//        } catch(ParserConfigurationException e) {
//            println(e);
//        } catch(SAXException e) {
//            println(e);
//        } catch(IOException e) {
//            println(e);
//        }
//    }
//
//    protected static void printIndexesInfo(Indexes indexes) {
//        println("indexes:" + indexes);
//        println("mode:" + indexes.getMode());
//        println("count:" + indexes.getIndexesCount());
//        println("media length:" + indexes.getMediaLength());
//        println("length:" + indexes.getLength());
//
//        for(Iterator<Index> it = indexes.iterator(); it.hasNext();) {
//            Index index = it.next();
//            println("index:" + index);
//            println("type:" + index.getType());
//            println("initi:" + index.getInitialTime());
//            println("final:" + index.getFinalTime());
//            println("subtitle:" + index.getSubtitle());
//            println("read:" + index.getRead());
//        }
//    }
//
//    protected static void printNodeInfo(Node node) {
//        println("node:" + node);
//        println("node type:" + node.getNodeType());
//        println("node name:" + node.getNodeName());
//        println("node value:" + node.getNodeValue());
//        println("node has attributes:" + node.hasAttributes());
//        println("node has children:" + node.hasChildNodes());
//        if(node.hasAttributes()) {
//            NamedNodeMap attributes = node.getAttributes();
//            for(int i=0; i<attributes.getLength(); i++){
//                Node attribute = attributes.item(i);
//                println("attribute " + i);
//                printNodeInfo(attribute);
//            }
//        }
//        if(node.hasChildNodes() && node.getNodeType()!=Node.ATTRIBUTE_NODE) {
//            NodeList list = node.getChildNodes();
//            for(int i=0; i<list.getLength(); i++) {
//                Node child = list.item(i);
//                println("child " + i);
//                printNodeInfo(child);
//            }
//        }
//        println("");
//    }
//
//    protected static void println(String message) {
//        System.out.println(message);
//    }
//
//    protected static void println(Throwable throwable) {
//        throwable.printStackTrace();
//    }

}
