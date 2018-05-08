package thot.labo.utils;

import java.io.File;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import thot.labo.ProjectFiles;
import thot.labo.ProjectTarget;
import thot.labo.TagList;
import thot.labo.index.Index;
import thot.labo.index.IndexFile;
import thot.labo.index.IndexType;
import thot.labo.index.Indexes;
import thot.utils.Utilities;
import thot.utils.XMLUtilities;

/**
 * Utilitaires pour la manipulation de fichier XML.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class LaboXMLUtilities extends XMLUtilities {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LaboXMLUtilities.class);

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
    private static final String ELEMENT_INDEXES = "indexes";
    /**
     * Balise d'attribut pour le mode d'utilisation de la liste
     */
    private static final String ATTRIBUT_MODE = "mode";
    /**
     * Balise d'attribut pour la durée
     */
    private static final String ATTRIBUT_LENGTH = "length";
    /**
     * Balise d'attribut pour la durée du média
     */
    private static final String ATTRIBUT_MEDIA_LENGTH = "mediaLength";

    /**
     * Balise pour un index
     */
    public static final String ELEMENT_INDEX = "index";
    /**
     * Balise d'attribut pour le type d'index
     */
    private static final String ATTRIBUT_TYPE = "type";
    /**
     * Balise d'attribut pour le temps de départ
     */
    private static final String ATTRIBUT_INITIAL_TIME = "initialTime";
    /**
     * Balise d'attribut pour le temps de fin
     */
    private static final String ATTRIBUT_FINAL_TIME = "finalTime";
    /**
     * Balise d'attribut pour le soustitre (unused sous-titre en CDATA)
     */
    private static final String ATTRIBUT_SUBTITLE = "subtitle";
    /**
     * Balise d'attribut pour le nombre de lecture
     */
    private static final String ATTRIBUT_READ = "read";
    /**
     * Balise d'attribut pour le nombre de lecture
     */
    private static final String ATTRIBUT_RATE = "rate";
    /**
     * Balise d'attribut pour le nom du fichier (unused cf element_file)
     */
    private static final String ATTRIBUT_FILE = "file";
    /**
     * Balise pour le nom du fichier (nom en CDATA)
     */
    private static final String ELEMENT_FILE = "file";

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
    private static final String ELEMENT_PROJECT = "project";
    /**
     * Balise pour le nom du logiciel destinataire (nom en CDATA)
     */
    private static final String ELEMENT_SOFT = "soft";
    /**
     * Balise pour le nom du fichier audio (nom en CDATA)
     */
    private static final String ELEMENT_AUDIO_FILE = "audioFile";
    /**
     * Balise pour le nom du vidéo (nom en CDATA)
     */
    private static final String ELEMENT_VIDEO_FILE = "videoFile";
    /**
     * Balise pour le nom du fichier d'index (nom en CDATA)
     */
    private static final String ELEMENT_INDEXES_FILE = "indexesFile";
    /**
     * Balise pour le nom du fichier de soustitres (nom en CDATA)
     */
    private static final String ELEMENT_SUBTITLE_FILE = "subtitleFile";
    /**
     * Balise pour le nom du fichier texte (nom en CDATA)
     */
    private static final String ELEMENT_TEXT_FILE = "textFile";
    /**
     * Balise pour le nom du fichier des tags (nom en CDATA)
     */
    private static final String ELEMENT_TAG_FILE = "tagFile";
    /**
     * Balise pour le nom du vidéo (nom en CDATA)
     */
    private static final String ELEMENT_VIDEO_ORIGINAL_FILE = "videoOriginalFile";

    /*
     * Descriptif pour le fichier de tags:
     *
     * <?xml version="1.0" encoding="UTF-8" ?>
     * <tags>
     *     <title>
     *         <![CDATA[Titre]]>
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
    private static final String ELEMENT_TAGS = "tags";
    /**
     * Balise pour le titre du document (en CDATA)
     */
    private static final String ELEMENT_TITLE = "title";
    /**
     * Balise pour le nom de l'artiste (en CDATA)
     */
    private static final String ELEMENT_ARTIST = "artist";
    /**
     * Balise pour le nom de l'album (en CDATA)
     */
    private static final String ELEMENT_ALBUM = "album";
    /**
     * Balise pour les commantaires (en CDATA)
     */
    private static final String ELEMENT_COMMENTS = "comments";

    /**
     * Retourne la liste d'index contenue dans le fichier.
     *
     * @param file le fichier contenant la liste d'index.
     *
     * @return la liste d'index contenue dans le fichier ou une liste vide.
     */
    public static Indexes loadIndexes(File file) {
        LOGGER.info("Parsing du fichier d'index {}", file.getAbsolutePath());
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
        LOGGER.info("Parsing du fichier de projet {}", file.getAbsolutePath());
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
        LOGGER.info("Parsing du fichier de tags {}", file.getAbsolutePath());
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
     * Retourne l'index contenue dans la chaîne de caractères.
     *
     * @param xml le xml contenant l'index.
     *
     * @return l'index contenu dans dans le xml ou {@code null}.
     */
    public static Index parseIndex(String xml) {
        LOGGER.info("Parsing du xml pour un index {}", xml);
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
     * Retourne le xml complet de la liste d'index.
     *
     * @param indexes la liste d'index.
     *
     * @return le xml complet.
     */
    public static String getXML(Indexes indexes) {
        return XML_HEADER + getXMLDescription(indexes);
    }

    /**
     * Retourne le xml complet du projet.
     *
     * @param project le projet.
     *
     * @return le xml complet.
     */
    public static String getXML(ProjectFiles project) {
        return XML_HEADER + getXMLDescription(project);
    }

    /**
     * Retourne le xml complet es tags.
     *
     * @param tags les tags.
     *
     * @return le xml complet.
     */
    public static String getXML(TagList tags) {
        return XML_HEADER + getXMLDescription(tags);
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
        attributes.append(createAttribute(ATTRIBUT_MODE, indexes.getMode()));
        attributes.append(createAttribute(ATTRIBUT_LENGTH, Long.toString(indexes.getLength())));
        attributes.append(createAttribute(ATTRIBUT_MEDIA_LENGTH, Long.toString(indexes.getMediaLength())));

        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(ELEMENT_INDEXES, attributes));
        for (Iterator<Index> it = indexes.iterator(); it.hasNext(); ) {
            element.append(getXMLDescription(it.next()));
        }
        element.append(createElementEnd(ELEMENT_INDEXES));

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
        attributes.append(createAttribute(ATTRIBUT_TYPE, index.getType().getName()));
        attributes.append(createAttribute(ATTRIBUT_INITIAL_TIME, Long.toString(index.getInitialTime())));
        attributes.append(createAttribute(ATTRIBUT_FINAL_TIME, Long.toString(index.getFinalTime())));
        attributes.append(createAttribute(ATTRIBUT_READ, Integer.toString(index.getRead())));
        attributes.append(createAttribute(ATTRIBUT_RATE, Float.toString(index.getRate())));

        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(ELEMENT_INDEX, attributes));

        if (index.getSubtitle() != null) {
            element.append(createCDATA(index.getSubtitle()));
        }
        if (index instanceof IndexFile) {
            String value = ((IndexFile) index).getFileName();
            if (value != null) {
                element.append(createCDATAElement(ELEMENT_FILE, value));
            }
        }
        element.append(createElementEnd(ELEMENT_INDEX));

        return element.toString();
    }

    /**
     * Retourne une description XML du projet.
     *
     * @param project le projet.
     *
     * @return une description du projet.
     */
    private static String getXMLDescription(ProjectFiles project) {
        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(ELEMENT_PROJECT));

        if (project.getSoft() != null) {
            element.append(createCDATAElement(ELEMENT_SOFT, project.getSoft().getName()));
        }
        if (project.getVideoFile() != null) {
            element.append(createCDATAElement(ELEMENT_VIDEO_FILE, project.getVideoFile()));
        }
        if (project.getAudioFile() != null) {
            element.append(createCDATAElement(ELEMENT_AUDIO_FILE, project.getAudioFile()));
        }
        if (project.getIndexesFile() != null) {
            element.append(createCDATAElement(ELEMENT_INDEXES_FILE, project.getIndexesFile()));
        }
        if (project.getTextFile() != null) {
            element.append(createCDATAElement(ELEMENT_TEXT_FILE, project.getTextFile()));
        }
        if (project.getSubtitleFile() != null) {
            element.append(createCDATAElement(ELEMENT_SUBTITLE_FILE, project.getSubtitleFile()));
        }
        if (project.getTagFile() != null) {
            element.append(createCDATAElement(ELEMENT_TAG_FILE, project.getTagFile()));
        }
        if (project.getVideoOriginalFile() != null) {
            element.append(createCDATAElement(ELEMENT_VIDEO_ORIGINAL_FILE, project.getVideoOriginalFile()));
        }

        element.append(createElementEnd(ELEMENT_PROJECT));

        return element.toString();
    }

    /**
     * Retourne une description des tags.
     *
     * @param tags les tags.
     *
     * @return une description des tags.
     */
    private static String getXMLDescription(TagList tags) {
        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(ELEMENT_TAGS));

        if (tags.getTag(TagList.TITLE) != null) {
            element.append(createCDATAElement(ELEMENT_TITLE, tags.getTag(TagList.TITLE)));
        }
        if (tags.getTag(TagList.ARTIST) != null) {
            element.append(createCDATAElement(ELEMENT_ARTIST, tags.getTag(TagList.ARTIST)));
        }
        if (tags.getTag(TagList.ALBUM) != null) {
            element.append(createCDATAElement(ELEMENT_ALBUM, tags.getTag(TagList.ALBUM)));
        }
        if (tags.getTag(TagList.COMMENT) != null) {
            element.append(createCDATAElement(ELEMENT_INDEXES_FILE, tags.getTag(TagList.COMMENT)));
        }
        element.append(createElementEnd(ELEMENT_TAGS));
        return element.toString();
    }

    /**
     * Parse le noeud xml comme si c'était une liste d'index.
     *
     * @param node le noeud xml.
     *
     * @return la liste d'index ou {@code null}.
     */
    private static Indexes parseNodeAsIndexes(Node node) {
        Indexes indexes = null;

        if (node.getNodeName().equals(ELEMENT_INDEXES)) {
            indexes = new Indexes();

            if (node.hasAttributes()) {
                NamedNodeMap attributes = node.getAttributes();
                Node attribute;
                String name;
                String nodeValue;
                for (int i = 0; i < attributes.getLength(); i++) {
                    attribute = attributes.item(i);
                    name = attribute.getNodeName();
                    nodeValue = attribute.getNodeValue();
                    switch (name) {
                        case ATTRIBUT_MODE:
                            indexes.setMode(nodeValue);
                            break;
                        case ATTRIBUT_MEDIA_LENGTH:
                            indexes.setMediaLength(Utilities.parseStringAsLong(nodeValue));
                            break;
                    }
                }
            }

            NodeList children = node.getChildNodes();
            Node child;
            Index index;
            for (int i = 0; i < children.getLength(); i++) {
                child = children.item(i);
                index = parseNodeAsIndex(child);
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
     * @return l'index ou {@code null}.
     */
    public static Index parseNodeAsIndex(Node node) {
        Index index = null;
        if (node.getNodeName().equals(ELEMENT_INDEX)) {
            index = new Index(IndexType.UNKNOWN);
            if (node.hasAttributes()) {
                NamedNodeMap attributes = node.getAttributes();
                Node attribute;
                String name;
                String nodeValue;
                for (int i = 0; i < attributes.getLength(); i++) {
                    attribute = attributes.item(i);
                    name = attribute.getNodeName();
                    nodeValue = attribute.getNodeValue();
                    switch (name) {
                        case ATTRIBUT_TYPE:
                            index.setType(IndexType.getIndexType(nodeValue));
                            if (index.isFileType()) {
                                index = convertToIndexFile(index);
                            }
                            break;
                        case ATTRIBUT_INITIAL_TIME:
                            index.setInitialTime(Utilities.parseStringAsLong(nodeValue));
                            break;
                        case ATTRIBUT_FINAL_TIME:
                            index.setFinalTime(Utilities.parseStringAsLong(nodeValue));
                            break;
                        case ATTRIBUT_SUBTITLE:
                            index.setSubtitle(nodeValue);
                            break;
                        case ATTRIBUT_READ:
                            index.setRead(Utilities.parseStringAsInt(nodeValue));
                            break;
                        case ATTRIBUT_RATE:
                            index.setRate(Utilities.parseStringAsFloat(attribute.getNodeValue()));
                            break;
                        case ATTRIBUT_FILE:
                            index = convertToIndexFile(index);
                            ((IndexFile) index).setFileName(nodeValue);
                            break;
                    }
                }
            }
            if (node.hasChildNodes()) {
                NodeList children = node.getChildNodes();
                Node child;
                for (int i = 0; i < children.getLength(); i++) {
                    child = children.item(i);
                    if ((child.getNodeType() == Node.CDATA_SECTION_NODE) || (child.getNodeType() == Node.TEXT_NODE)) {
                        index.setSubtitle(child.getNodeValue());
                    } else if (child.getNodeName().equals(ELEMENT_FILE) && child.hasChildNodes()) {
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
     * @return le projet ou {@code null}.
     */
    private static ProjectFiles parseNodeAsProject(Node node) {
        ProjectFiles project = null;

        if (node.getNodeName().equals(ELEMENT_PROJECT)) {
            project = new ProjectFiles();
            NodeList children = node.getChildNodes();
            Node child;
            String name;
            String nodeValue;
            for (int i = 0; i < children.getLength(); i++) {
                child = children.item(i);
                if (!child.hasChildNodes()) {
                    continue;
                }
                name = child.getNodeName();
                nodeValue = child.getFirstChild().getNodeValue();
                switch (name) {
                    case ELEMENT_SOFT:
                        project.setSoft(ProjectTarget.getProjectTarget(nodeValue));
                        break;
                    case ELEMENT_VIDEO_FILE:
                        project.setVideoFile(nodeValue);
                        break;
                    case ELEMENT_AUDIO_FILE:
                        project.setAudioFile(nodeValue);
                        break;
                    case ELEMENT_TEXT_FILE:
                        project.setTextFile(nodeValue);
                        break;
                    case ELEMENT_INDEXES_FILE:
                        project.setIndexesFile(nodeValue);
                        break;
                    case ELEMENT_SUBTITLE_FILE:
                        project.setSubtitleFile(nodeValue);
                        break;
                    case ELEMENT_TAG_FILE:
                        project.setTagFile(nodeValue);
                        break;
                    case ELEMENT_VIDEO_ORIGINAL_FILE:
                        project.setVideoOriginalFile(nodeValue);
                        break;
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
     * @return les tags ou {@code null}.
     */
    private static TagList parseNodeAsTags(Node node) {
        TagList tags = null;

        if (node.getNodeName().equals(ELEMENT_TAGS)) {
            tags = new TagList();
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                if (!child.hasChildNodes()) {
                    continue;
                }
                String name = child.getNodeName();
                switch (name) {
                    case ELEMENT_TITLE:
                        tags.putTag(TagList.TITLE, child.getFirstChild().getNodeValue());
                        break;
                    case ELEMENT_ARTIST:
                        tags.putTag(TagList.ARTIST, child.getFirstChild().getNodeValue());
                        break;
                    case ELEMENT_ALBUM:
                        tags.putTag(TagList.ALBUM, child.getFirstChild().getNodeValue());
                        break;
                    case ELEMENT_COMMENTS:
                        tags.putTag(TagList.COMMENT, child.getFirstChild().getNodeValue());
                        break;
                }
            }
        }

        return tags;
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


//    public static void main(String[] args) {
//        parseDOM(xml);
//    }
//
//    private static void parseDOM(String xml) {
//        try {
//            // création d'une fabrique de documents
//            DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();
//            // création d'un constructeur de documents
//            DocumentBuilder constructeur = fabrique.newDocumentBuilder();
//            Document document = constructeur.parse(new ByteArrayInputStream(xml.getBytes()));
//            removeEmptyTextNode(document);
//            LOGGER.info("document:");
//            printNodeInfo(document.getDocumentElement());
//            Indexes indexes = parseNodeAsIndexes(document.getDocumentElement());
//            if (indexes != null) {
//                printIndexesInfo(indexes);
//                LOGGER.info("xml: {}", getXMLDescription(indexes));
//            }
//        } catch (ParserConfigurationException | SAXException | IOException e) {
//            LOGGER.error("erreur", e);
//        }
//    }
//
//    private static void printIndexesInfo(Indexes indexes) {
//        LOGGER.info("indexes: {}", indexes);
//        LOGGER.info("mode: {}", indexes.getMode());
//        LOGGER.info("count: {}", indexes.getIndexesCount());
//        LOGGER.info("media length: {}", indexes.getMediaLength());
//        LOGGER.info("length: {}", indexes.getLength());
//
//        for (Iterator<Index> it = indexes.iterator(); it.hasNext(); ) {
//            Index index = it.next();
//            LOGGER.info("index: {}", index);
//            LOGGER.info("type: {}", index.getType());
//            LOGGER.info("initi: {}", index.getInitialTime());
//            LOGGER.info("final: {}", index.getFinalTime());
//            LOGGER.info("subtitle: {}", index.getSubtitle());
//            LOGGER.info("read: {}", index.getRead());
//        }
//    }
//
//    private static void printNodeInfo(Node node) {
//        LOGGER.info("node: {}", node);
//        LOGGER.info("node type: {}", node.getNodeType());
//        LOGGER.info("node name: {}", node.getNodeName());
//        LOGGER.info("node value: {}", node.getNodeValue());
//        LOGGER.info("node has attributes: {}", node.hasAttributes());
//        LOGGER.info("node has children: {}", node.hasChildNodes());
//        if (node.hasAttributes()) {
//            NamedNodeMap attributes = node.getAttributes();
//            for (int i = 0; i < attributes.getLength(); i++) {
//                Node attribute = attributes.item(i);
//                LOGGER.info("attribute {}", i);
//                printNodeInfo(attribute);
//            }
//        }
//        if (node.hasChildNodes() && node.getNodeType() != Node.ATTRIBUTE_NODE) {
//            NodeList list = node.getChildNodes();
//            for (int i = 0; i < list.getLength(); i++) {
//                Node child = list.item(i);
//                LOGGER.info("child {}", i);
//                printNodeInfo(child);
//            }
//        }
//        LOGGER.info("");
//    }

}
