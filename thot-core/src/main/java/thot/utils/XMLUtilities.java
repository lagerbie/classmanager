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
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utilitaires pour la manipulation de fichier XML.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class XMLUtilities {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtilities.class);

    /**
     * Entête du fichier xml.
     */
    protected static final String xml_header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    /**
     * Balise ouvrant une section CDATA.
     */
    private static final String cdata_start = "<![CDATA[";
    /**
     * Balise fermant une section CDATA.
     */
    private static final String cdata_end = "]]>";

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
    protected static String createCDATAElement(String name, String value) {
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
    protected static Document getDocument(String xml) {
        Document document = null;

        // création d'une fabrique de documents
        DocumentBuilderFactory fabrique = DocumentBuilderFactory.newInstance();

        try {
            // création d'un constructeur de documents
            DocumentBuilder constructeur = fabrique.newDocumentBuilder();

            document = constructeur.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            if (document != null) {
                removeEmptyTextNode(document);
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            LOGGER.error("", e);
        }

        return document;
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

}
