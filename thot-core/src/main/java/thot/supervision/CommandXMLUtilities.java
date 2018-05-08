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
package thot.supervision;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import thot.utils.XMLUtilities;

/**
 * Utilitaires pour la manipulation de fichier XML.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class CommandXMLUtilities extends XMLUtilities {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandXMLUtilities.class);

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
    private static final String ELEMENT_COMMAND = "command";
    /**
     * Balise pour l'élément action de supervision.
     */
    private static final String ELEMENT_SUPERVISION = "supervision";
    /**
     * Balise pour l'élément action du labo.
     */
    private static final String ELEMENT_LABORATORY = "laboratory";

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
    private static final String ELEMENT_LOGIN = "login";
    /**
     * Balise pour un mot de passe.
     */
    private static final String ELEMENT_PASSWORD = "password";

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
    private static final String ELEMENT_LANGUAGE = "language";

    /**
     * Retourne la liste de commandes contenue dans la chaine XML.
     *
     * @param xml le xml contenant les commandes.
     *
     * @return la liste des commandes contenue dans le xml ou une liste vide.
     */
    public static List<Command> parseCommand(String xml) {
        LOGGER.info("Parsing du xml {}", xml);
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
     * Retourne le xml complet de la commande.
     *
     * @param command la commande.
     *
     * @return le xml complet.
     */
    public static String getXML(Command command) {
        return XML_HEADER + createElement(ELEMENT_COMMAND, getXMLDescription(command));
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
            String actionType = ELEMENT_SUPERVISION;
            switch (command.getType()) {
                case TYPE_LABORATORY:
                    actionType = ELEMENT_LABORATORY;
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
        return XML_HEADER + createElement(ELEMENT_LOGIN, createCDATAElement(ELEMENT_PASSWORD, password));
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
     * Retourne le xml complet pour l'enregistrement de la langue.
     *
     * @param language la langue par defaut.
     *
     * @return le xml complet.
     */
    public static String getLanguageXML(String language) {
        return XML_HEADER + createElement(ELEMENT_LANGUAGE, createCDATAElement(ELEMENT_LANGUAGE, language));
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
     * @return la liste des commandes ou {@code null}.
     */
    private static List<Command> parseNodeAsCommand(Node node) {
        List<Command> commands = new ArrayList<>(2);

        if (node.getNodeName().equals(ELEMENT_COMMAND)) {
            NodeList actions = node.getChildNodes();
            for (int i = 0; i < actions.getLength(); i++) {
                Node action = actions.item(i);
                Command command = null;
                switch (action.getNodeName()) {
                    case ELEMENT_SUPERVISION:
                        command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.UNKNOWN);
                        break;
                    case ELEMENT_LABORATORY:
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
                                        ipList.append(CommandXMLUtilities.createElement(elementName, item));
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
     * Parse le noeud xml comme si c'était un login.
     *
     * @param node le noeud xml.
     *
     * @return le mot de passe ou {@code null}.
     */
    private static String parseNodeAsLogin(Node node) {
        String password = null;

        if (node.getNodeName().equals(ELEMENT_LOGIN)) {
            if (node.hasChildNodes()) {
                NodeList nodes = node.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node child = nodes.item(i);
                    if (child.getNodeName().equalsIgnoreCase(ELEMENT_PASSWORD) && child.hasChildNodes()) {
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
     * @return la langue ou {@code null}.
     */
    private static String parseNodeAsLanguage(Node node) {
        String language = null;

        if (node.getNodeName().equals(ELEMENT_LANGUAGE)) {
            if (node.hasChildNodes()) {
                NodeList nodes = node.getChildNodes();
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node child = nodes.item(i);
                    if (child.getNodeName().equalsIgnoreCase(ELEMENT_LANGUAGE) && child.getFirstChild() != null) {
                        language = child.getFirstChild().getNodeValue();
                        break;
                    }
                }
            }
        }

        return language;
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
