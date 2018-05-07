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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import thot.model.Command;
import thot.model.CommandAction;
import thot.model.CommandParamater;
import thot.model.CommandType;
import thot.model.Index;
import thot.model.IndexFile;
import thot.model.IndexType;
import thot.model.Indexes;
import thot.model.ProjectFiles;

/**
 * Utilitaires pour la manipulation de fichier XML.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class XMLUtilities {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtilities.class);

    /*
     * Descriptif pour une commande:
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <command>
     *     <action> valeur supervision ou laboratory
     *         action
     *         <key>
     *             <![CDATA[value]]>
     *         </key>
     *     </action>
     * </command>
     */
    /**
     * Balise pour une commande.
     */
    private static final String element_command = "command";
    /**
     * Balise pour l'élément action de supervision.
     */
    private static final String element_supervision = "supervision";
    /**
     * Balise pour l'élément action du labo.
     */
    private static final String element_laboratory = "laboratory";

    /*
     * Descriptif pour une login:
     * <?xml version="1.0" encoding="UTF-8"?>
     * <login>
     *     <Password></Password>
     * </login>
     * ou
     * <login>
     *     <password><![CDATA[String]]></password>
     * </login>
     */
    /**
     * Balise pour un login.
     */
    private static final String element_login = "login";
    /**
     * Balise pour un mot de passe.
     */
    private static final String element_password = "password";

    /*
     * Descriptif pour la sauvegarde de la langue par défaut:
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <language>
     *      <language>String</language>
     * </language>
     */
    /**
     * Balise pour la langue.
     */
    private static final String element_language = "language";

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
     * Balise pour une liste d'index.
     */
    private static final String element_indexes = "indexes";
    /**
     * Balise d'attribut pour le mode d'utilisation de la liste.
     */
    private static final String attribut_mode = "mode";
    /**
     * Balise d'attribut pour la durée.
     */
    private static final String attribut_length = "length";
    /**
     * Balise d'attribut pour la durée du média.
     */
    private static final String attribut_mediaLength = "mediaLength";
    /**
     * Balise pour un index.
     */
    private static final String element_index = "index";
    /**
     * Balise d'attribut pour le type d'index.
     */
    private static final String attribut_type = "type";
    /**
     * Balise d'attribut pour le temps de départ.
     */
    private static final String attribut_initialTime = "initialTime";
    /**
     * Balise d'attribut pour le temps de fin.
     */
    private static final String attribut_finalTime = "finalTime";
    /**
     * Balise d'attribut pour le soustitre (unused sous-titre en CDATA).
     */
    private static final String attribut_subtitle = "subtitle";
    /**
     * Balise d'attribut pour le nombre de lecture.
     */
    private static final String attribut_read = "read";
    /**
     * Balise d'attribut pour le nom du fichier (unused cf element_file).
     */
    private static final String attribut_file = "file";
    /**
     * Balise pour le nom du fichier (nom en CDATA).
     */
    private static final String element_file = "file";

    /*
     * Descriptif pour le fichier projet:
     *
     * <?xml version="1.0" encoding="UTF-8" ?>
     * <project>
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
     * </project>
     */
    /**
     * Balise pour un projet.
     */
    private static final String element_project = "project";
    /**
     * Balise pour le nom du fichier audio (nom en CDATA).
     */
    private static final String element_audioFile = "audioFile";
    /**
     * Balise pour le nom du vidéo (nom en CDATA).
     */
    private static final String element_videoFile = "videoFile";
    /**
     * Balise pour le nom du fichier d'index (nom en CDATA).
     */
    private static final String element_indexesFile = "indexesFile";
    /**
     * Balise pour le nom du fichier de soustitres (nom en CDATA).
     */
    private static final String element_subtitleFile = "subtitleFile";
    /**
     * Balise pour le nom du fichier texte (nom en CDATA).
     */
    private static final String element_textFile = "textFile";

    /**
     * Entête du fichier xml.
     */
    protected static final String xml_header
            = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    /**
     * Balise ouvrant une section CDATA.
     */
    private static final String cdata_start = "<![CDATA[";
    /**
     * Balise fermant une section CDATA.
     */
    private static final String cdata_end = "]]>";

    /**
     * Retourne la liste de commandes contenue dans la chaine XML.
     *
     * @param xml le xml contenant les commandes.
     *
     * @return la liste des commandes contenue dans le xml ou une liste vide.
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
     * Retourne une description XML de la commande.
     *
     * @param command la commande.
     *
     * @return une description de la commande.
     */
    private static String getXMLDescription(Command command) {
        StringBuilder element = new StringBuilder(1024);
        if (command.getAction() != null) {
            String actionType = element_supervision;
            switch (command.getType()) {
                case TYPE_LABORATORY:
                    actionType = element_laboratory;
                    break;
            }

            element.append(createElementStart(actionType));
            element.append(command.getAction());

            Set<CommandParamater> parameters = command.getParameters();
            for (CommandParamater key : parameters) {
                if (Command.protectionNeeded(key)) {
                    element.append(createCDATAElement(key.getParameter(), command.getParameter(key)));
                } else {
                    element.append(createElement(key.getParameter(), command.getParameter(key)));
                }
            }
            element.append(createElementEnd(actionType));
        }

        return element.toString();
    }

    /**
     * Retourne le xml complet pour l'enregistrement d'un login.
     *
     * @param password le mot de passe associé au login.
     *
     * @return le xml complet.
     */
    public static String getLoginXML(String password) {
        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(element_login));
        element.append(createCDATAElement(element_password, password));
        element.append(createElementEnd(element_login));

        return xml_header + element.toString();
    }

    /**
     * Retourne le mot de passe contenu dans le fichier XML.
     *
     * @param loginFile le fichier du login.
     *
     * @return le mot de passe associé au login.
     */
    public static String getPassword(File loginFile) {
        String password = null;

        // lecture du contenu d'un fichier XML avec DOM
        Document document = getDocument(loginFile);

        //traitement du document
        if (document != null) {
            password = parseNodeAsLogin(document.getDocumentElement());
        }

        return password;
    }

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

        element.append(createElementEnd(element_project));

        return element.toString();
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
     * Parse le noeud xml comme si c'était une liste de commandes.
     *
     * @param node le noeud xml.
     *
     * @return la liste des commandes ou <code>null</code>.
     */
    private static List<Command> parseNodeAsCommand(Node node) {
        List<Command> commands = new ArrayList<>(2);

        if (node.getNodeName().equals(element_command)) {
            NodeList actions = node.getChildNodes();
            for (int i = 0; i < actions.getLength(); i++) {
                Node action = actions.item(i);
                Command command = null;
                switch (action.getNodeName()) {
                    case element_supervision:
                        command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.UNKNOWN);
                        break;
                    case element_laboratory:
                        command = new Command(CommandType.TYPE_LABORATORY, CommandAction.UNKNOWN);
                        break;
                }
                if (command != null) {
                    if (action.hasChildNodes()) {
                        NodeList children = action.getChildNodes();
                        for (int j = 0; j < children.getLength(); j++) {
                            Node child = children.item(j);
                            if ((child.getNodeType() == Node.CDATA_SECTION_NODE) || (child.getNodeType()
                                    == Node.TEXT_NODE)) {
                                command.setAction(CommandAction.getCommandAction(child.getNodeValue()));
                            } else {
                                if (child.getNodeName().contentEquals(CommandParamater.LIST.getParameter())) {
                                    List<String> list = parseNodeAsList(child);
                                    String elementName = child.getFirstChild().getNodeName();
                                    StringBuilder ipList = new StringBuilder(1024);
                                    for (String item : list) {
                                        ipList.append(XMLUtilities.createElement(elementName, item));
                                    }
                                    command.putParameter(CommandParamater.LIST,
                                            createElement(CommandParamater.LIST.getParameter(), ipList.toString()));
                                } else if (child.getFirstChild() != null) {
                                    command.putParameter(CommandParamater.getCommandParamater(child.getNodeName()),
                                            child.getFirstChild().getNodeValue());
                                } else {
                                    command.putParameter(CommandParamater.getCommandParamater(child.getNodeName()), "");
                                }
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
            } else if (child.getFirstChild() != null && child.getFirstChild().getNodeValue() != null) {
                list.add(child.getFirstChild().getNodeValue());
            }
        }
        return list;
    }

    /**
     * Parse le noeud xml comme si c'était un login.
     *
     * @param node le noeud xml.
     *
     * @return le mot de passe ou <code>null</code>.
     */
    private static String parseNodeAsLogin(Node node) {
        String password = null;

        if (node.getNodeName().equals(element_login)) {
            if (node.hasChildNodes()) {
                NodeList nodes = node.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node child = nodes.item(i);
                    if (child.getNodeName().equalsIgnoreCase(element_password) && child.hasChildNodes()) {
                        password = child.getFirstChild().getNodeValue();
                        break;
                    }
                }
            }
        }

        return password;
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
     * Crée un balise de début. Forme '<name>'.
     *
     * @param name le nom de la balise.
     *
     * @return la basile de départ du xml.
     */
    protected static String createElementStart(String name) {
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
    protected static String createElementStart(String name, StringBuilder attributes) {
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
    protected static String createElementEnd(String name) {
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
    public static String createElement(String name, String value) {
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
    protected static String createCDATA(String value) {
        StringBuilder cdata = new StringBuilder(256);
        cdata.append(cdata_start);
        cdata.append(value);
        cdata.append(cdata_end);
        return cdata.toString();
    }

    /**
     * Crée le descriptif de l'attribut de balise avec nom et valeur. Descriptif de la forme 'name="value"'.
     *
     * @param name le nom de l'attribut.
     * @param value la valeur de l'attribut.
     *
     * @return le descriptif de l'attribut.
     */
    protected static String createAttribute(String name, String value) {
        StringBuilder attribute = new StringBuilder(64);
        attribute.append(" ");
        attribute.append(name);
        attribute.append("=\"");
        attribute.append(value);
        attribute.append("\"");
        return attribute.toString();
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
        } catch (IOException | ParserConfigurationException | SAXException e) {
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
    private static Document getDocument(String xml) {
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
        } catch (IOException | ParserConfigurationException | SAXException e) {
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
                Node attribute;
                String name;
                String nodeValue;
                for (int i = 0; i < attributes.getLength(); i++) {
                    attribute = attributes.item(i);
                    name = attribute.getNodeName();
                    nodeValue = attribute.getNodeValue();
                    switch (name) {
                        case attribut_mode:
                            indexes.setMode(nodeValue);
                            break;
                        case attribut_mediaLength:
                            indexes.setMediaLength(
                                    Utilities.parseStringAsLong(nodeValue));
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
     * @return l'index ou <code>null</code>.
     */
    protected static Index parseNodeAsIndex(Node node) {
        Index index = null;
        if (node.getNodeName().equals(element_index)) {
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
                        case attribut_type:
                            index.setType(IndexType.getIndexType(nodeValue));
                            if (index.isFileType()) {
                                index = convertToIndexFile(index);
                            }
                            break;
                        case attribut_initialTime:
                            index.setInitialTime(Utilities.parseStringAsLong(nodeValue));
                            break;
                        case attribut_finalTime:
                            index.setFinalTime(Utilities.parseStringAsLong(nodeValue));
                            break;
                        case attribut_subtitle:
                            index.setSubtitle(nodeValue);
                            break;
                        case attribut_read:
                            index.setRead(Utilities.parseStringAsInt(nodeValue));
                            break;
                        case attribut_file:
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
                    case element_videoFile:
                        project.setVideoFile(nodeValue);
                        break;
                    case element_audioFile:
                        project.setAudioFile(nodeValue);
                        break;
                    case element_textFile:
                        project.setTextFile(nodeValue);
                        break;
                    case element_indexesFile:
                        project.setIndexesFile(nodeValue);
                        break;
                    case element_subtitleFile:
                        project.setSubtitleFile(nodeValue);
                        break;
                }
            }
        }

        return project;
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
            Node child;
            for (int i = 0; i < list.getLength(); i++) {
                child = list.item(i);
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
//        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><command>"
//                + "<action>sendScreen"
//                + "<remoteHandling>1</remoteHandling>"
//                + "<fps>20</fps>"
//                + "<quality>80</quality>"
//                + "<list>"
////                + "<clientIP>192.168.9.17</clientIP>"
////                + "<clientIP>192.168.9.16</clientIP>"
////                + "<clientIP>192.168.9.15</clientIP>"
//                + "</list>"
//                + "<clientNumber>4</clientNumber>"
//                + "<lines>32</lines>"
//                + "<screenPort>7300</screenPort>"
//                + "<audioPort>7225</audioPort>"
//                + "<ipAddress>192.168.9.251</ipAddress>"
//                + "</action></command>";
//        
//        List<Command> commands = parseCommand(xml);
//        for(Command command : commands) {
//            println(command.getParameter(Command.LIST));
//            String list = command.getParameter(Command.LIST);
//            if(list != null) {
//                List<String> ipList = XMLUtilities.parseList(list);
//                for(String ip : ipList) {
//                    println(ip);
//                }
//            }
//        }
////        parseDOM(xml);
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
////            printNodeInfo(document.getDocumentElement());
//            List<Command> commands = parseNodeAsCommand(document);
//            for(Command command : commands) {
//                println(getXMLDescription(command));
//            }
//            
//        } catch(Exception e) {
//            println(e);
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
//            org.w3c.dom.NamedNodeMap attributes = node.getAttributes();
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
