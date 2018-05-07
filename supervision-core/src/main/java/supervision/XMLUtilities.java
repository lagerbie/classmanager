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
package supervision;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utilitaires pour la manipulation de fichier XML.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class XMLUtilities {

    /*
     * Descriptif pour une commande:
     * 
     * <?xml version="1.0" encoding="UTF-8"?>
     * <command>
     *     <action> ou supervisionAction ou laboAction
     *         action
     *         <parameter>
     *             <![CDATA[parameter]]>
     *         </parameter>
     *         <key>
     *             <![CDATA[value]]>
     *         </key>
     *     </action>
     * </command>
     */
    /**
     * Balise pour une commande.
     */
    public static final String element_command = "command";
    /**
     * Balise pour l'élément action.
     */
    public static final String element_action = "action";
    /**
     * Balise pour l'élément action de supervision.
     */
    public static final String element_supervision_action = "supervisionAction";
    /**
     * Balise pour l'élément action du labo.
     */
    public static final String element_labo_action = "laboAction";
    /**
     * Balise pour le paramètre (en CDATA).
     */
    public static final String element_parameter = "parameter";

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
    public static final String element_login = "login";
    /**
     * Balise pour un mot de passe.
     */
    public static final String element_password = "password";

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
    public static final String element_language = "language";

    /**
     * Entête du fichier xml.
     */
    public static final String xml_header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    /**
     * Balise ouvrant une section CDATA.
     */
    public static final String cdata_start = "<![CDATA[";
    /**
     * Balise fermant une section CDATA.
     */
    public static final String cdata_end = "]]>";

    /**
     * Retourne la liste de commandes contenue dans la chaine XML.
     *
     * @param xml le xml contenant les commandes.
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
     * @return une description de la commande.
     */
    private static String getXMLDescription(Command command) {
        StringBuilder element = new StringBuilder(1024);
        if (command.getAction() != null) {
            String actionType = element_action;
            switch (command.getType()) {
                case Command.TYPE_SUPERVISION:
                    actionType = element_supervision_action;
                    break;
                case Command.TYPE_LABO:
                    actionType = element_labo_action;
                    break;
            }

            element.append(createElementStart(actionType));
            element.append(command.getAction());

            Set<String> parameters = command.getParameters();
            for (String key : parameters) {
                if (Command.protectionNeeded(key)) {
                    element.append(createCDATAElement(key, command.getParameter(key)));
                } else {
                    element.append(createElement(key, command.getParameter(key)));
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
     * Retourne le xml complet pour l'enregistrement de la langue.
     *
     * @param language la langue par defaut.
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
     * @return la liste des commandes ou <code>null</code>.
     */
    private static List<Command> parseNodeAsCommand(Node node) {
        List<Command> commands = null;

        if (node.getNodeName().equals(element_command)) {
            commands = new ArrayList<>(2);
            NodeList actions = node.getChildNodes();
            for (int i = 0; i < actions.getLength(); i++) {
                Node action = actions.item(i);
                Command command = null;
                switch (action.getNodeName()) {
                    case element_action:
                        command = new Command(Command.TYPE_ACTION, Command.UNKNOWN);
                        break;
                    case element_supervision_action:
                        command = new Command(Command.TYPE_SUPERVISION, Command.UNKNOWN);
                        break;
                    case element_labo_action:
                        command = new Command(Command.TYPE_LABO, Command.UNKNOWN);
                        break;
                }
                if (command != null) {
                    if (action.hasChildNodes()) {
                        NodeList children = action.getChildNodes();
                        for (int j = 0; j < children.getLength(); j++) {
                            Node child = children.item(j);
                            if ((child.getNodeType() == Node.CDATA_SECTION_NODE)
                                    || (child.getNodeType() == Node.TEXT_NODE)) {
                                command.setAction(child.getNodeValue());
                            } else {
                                if (child.getNodeName().contentEquals(Command.LIST)) {
                                    List<String> list = parseNodeAsList(child);
                                    String elementName = child.getFirstChild().getNodeName();
                                    StringBuilder ipList = new StringBuilder(1024);
                                    for (String item : list) {
                                        ipList.append(XMLUtilities.createElement(
                                                elementName, item));
                                    }
                                    command.putParameter(Command.LIST,
                                            createElement(Command.LIST, ipList.toString()));
                                } else if (child.getFirstChild() != null) {
                                    command.putParameter(child.getNodeName(),
                                            child.getFirstChild().getNodeValue());
                                } else {
                                    command.putParameter(child.getNodeName(), "");
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
     * Parse le noeud xml comme si c'était un login.
     *
     * @param node le noeud xml.
     * @return le mot de passe ou <code>null</code>.
     */
    private static String parseNodeAsLogin(Node node) {
        String password = null;

        if (node.getNodeName().equals(element_login)) {
            if (node.hasChildNodes()) {
                NodeList nodes = node.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node child = nodes.item(i);
                    if (child.getNodeName().equalsIgnoreCase(element_password)
                            && child.hasChildNodes()) {
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
     * @return la langue ou <code>null</code>.
     */
    private static String parseNodeAsLanguage(Node node) {
        String language = null;

        if (node.getNodeName().equals(element_language)) {
            if (node.hasChildNodes()) {
                NodeList nodes = node.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node child = nodes.item(i);
                    if (child.getNodeName().equalsIgnoreCase(element_language)
                            && child.getFirstChild() != null) {
                        language = child.getFirstChild().getNodeValue();
                        break;
                    }
                }
            }
        }

        return language;
    }

    /**
     * Crée un balise de début.
     * Forme '<name>'.
     *
     * @param name le nom de la balise.
     * @return la basile de départ du xml.
     */
    protected static String createElementStart(String name) {
        return createElementStart(name, null);
    }

    /**
     * Crée un balise de début avec une liste d'attributs.
     * Forme '<name attribut="value" attribut="value">'.
     *
     * @param name le nom de la balise.
     * @param attributes la liste d'attributs déjà en forme pour le xml.
     * @return la basile de départ du xml.
     */
    protected static String createElementStart(String name,
            StringBuilder attributes) {
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
     * Crée un balise de fin.
     * Forme '</name>'.
     *
     * @param name le nom de la balise.
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
     * Crée une balise complete avec une section CDATA.
     * Forme '<name><![CDATA[value]]></name>'.
     *
     * @param name le nom de la balise.
     * @param value la valeur de la section CDATA.
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
     * Crée une balise complete avec une section CDATA.
     * Forme '<name><![CDATA[value]]></name>'.
     *
     * @param name le nom de la balise.
     * @param value la valeur de la section CDATA.
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
     * Crée une section CDATA.
     * Forme '<![CDATA[value]]>'.
     *
     * @param value la valeur de la section CDATA.
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
     * Crée le descriptif de l'attribut de balise avec nom et valeur.
     * Descriptif de la forme ' name="value"'.
     *
     * @param name le nom de l'attribut.
     * @param value la valeur de l'attribut.
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
            CommonLogger.error(e);
        }

        return document;
    }

    /**
     * Retourne le document xml contenu dans la chaîne de caractères.
     *
     * @param xml la chaîne de caractères contenant le xml.
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
            CommonLogger.error(e);
        }

        return document;
    }

    /**
     * Supprime les noeuds texte contenu dans le noeud ne contenant aucune
     * donnée.
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
