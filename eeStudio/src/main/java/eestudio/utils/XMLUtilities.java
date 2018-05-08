package eestudio.utils;

import java.util.ArrayList;
import java.util.List;

import eestudio.flash.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import thot.model.Index;
import thot.utils.LaboXMLUtilities;

/**
 * Utilitaires pour la manipulation de fichier XML.
 *
 * @author Fabrice Alleau
 */
@Deprecated
public class XMLUtilities extends thot.utils.XMLUtilities {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtilities.class);

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


//    /** Doctype pour une liste d'index (unused) */
//    private static final String indexes_DTD
//            = "<!DOCTYPE indexes ["
//            + "<!ELEMENT indexes"
//            + "]>";


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
                            } else if (child.getNodeName().equals(LaboXMLUtilities.element_index)) {
                                Index index = LaboXMLUtilities.parseNodeAsIndex(child);
                                command.setParameter(LaboXMLUtilities.getXMLDescription(index));
                            }
                        }
                    }
                    commands.add(command);
                }
            }
        }

        return commands;
    }

}
