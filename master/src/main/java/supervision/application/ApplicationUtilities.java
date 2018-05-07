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
package supervision.application;

import java.io.File;

import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import thot.model.Constants;
import thot.utils.Utilities;
import thot.utils.XMLUtilities;

/**
 * Utilitaires pour la manipulation de fichier XML pour le gestionnaire
 * d'applications.
 *
 * @author Fabrice Alleau
 * @version 1.82
 */
public class ApplicationUtilities extends XMLUtilities {

    /*
     * <?xml version="1.0" encoding="UTF-8"?>
     * <applications>
     *     <application name="play">
     *         <![CDATA[test &]]>
     *     </application>
     *     <application name="record">
     *         <![CDATA[test é]]>
     *     </application>
     *     <application name="play">
     *         <![CDATA[test >]]>
     *     </application>
     *     <application name="record">
     *         <![CDATA[test 4]]>
     *     </application>
     *     <application name="record">
     *         <![CDATA[test 5]]>
     *     </application>
     *     </application>
     * </applications>
     */
    /**
     * Balise pour une liste d'applications.
     */
    private static final String element_applications = "applications";
    /**
     * Balise pour une application.
     */
    private static final String element_application = "application";
    /**
     * Balise d'attribut pour le nom de l'application.
     */
    private static final String attribut_name = "name";

    /**
     * Retourne la liste d'applications contenue dans le fichier.
     *
     * @param file le fichier contenant la liste d'applications.
     * @return la liste d'index contenue dans le fichier ou une liste vide.
     */
    public static ApplicationsList loadApplicationsList(File file) {
        ApplicationsList applications = null;
        // lecture du contenu d'un fichier XML avec DOM
        Document document = getDocument(file);

        //traitement du document
        if (document != null) {
            applications = parseNodeAsApplicationsList(document.getDocumentElement());
        }

        if (applications == null) {
            applications = new ApplicationsList();
        }

        return applications;
    }

    /**
     * Sauvegarde la liste d'applications dans un fichier.
     *
     * @param applications la liste d'applications.
     * @param file le fichier.
     * @return la réussite de l'opération.
     * @version 1.82
     */
    public static boolean saveObject(ApplicationsList applications, File file) {
        if (!file.exists()) {
//            ApplicationsList oldList = loadApplicationsList(file);
//            if(applications.isIdentical(oldList))
//                return true;
//        }
//        else {
            file.getParentFile().mkdirs();
        }

        return Utilities.saveText(getXML(applications), file);
    }

    /**
     * Retourne le xml complet de la liste d'applications.
     *
     * @param applications la liste d'applications.
     * @return le xml complet.
     */
    private static String getXML(ApplicationsList applications) {
        StringBuilder element = new StringBuilder(1024);
        element.append(xml_header);
        element.append(createElementStart(element_applications));
        for (Iterator<Application> it = applications.iterator(); it.hasNext();) {
            element.append(getXMLDescription(it.next()));
        }
        element.append(createElementEnd(element_applications));

        return element.toString();
    }

    /**
     * Retourne une description XML d'une application.
     *
     * @param application l'application.
     * @return une description de l'application.
     * @version 1.82
     */
    private static String getXMLDescription(Application application) {
        if (application == null) {
            return getXMLDescription(new Application(Application.UNKNOWN, null));
        }
        StringBuilder attributes = new StringBuilder(256);
        attributes.append(createAttribute(attribut_name, application.getName()));

        StringBuilder element = new StringBuilder(1024);
        element.append(createElementStart(element_application, attributes));

        if (application.getPath() != null) {
            File file = new File(application.getPath());
            String path = Utilities.pathWithoutWindowsProgramFiles(file);
            if (path == null) {
                path = application.getPath();
            } else {
                path = Constants.PROGAM_FILES + path;
            }
            element.append(createCDATA(path));
        }
        element.append(createElementEnd(element_application));

        return element.toString();
    }

    /**
     * Parse le noeud xml comme si c'était une liste d'applications.
     *
     * @param node le noeud xml.
     * @return la liste d'applications ou <code>null</code>.
     */
    private static ApplicationsList parseNodeAsApplicationsList(Node node) {
        ApplicationsList applications = null;

        if (node.getNodeName().equals(element_applications)) {
            applications = new ApplicationsList();

            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                Application application = parseNodeAsApplication(child);
                if (application != null) {
                    applications.addApplication(application);
                }
            }
        }

        return applications;
    }

    /**
     * Parse le noeud xml comme si c'était une application.
     *
     * @param node le noeud xml.
     * @return l'application ou <code>null</code>.
     */
    private static Application parseNodeAsApplication(Node node) {
        Application application = null;
        if (node.getNodeName().equals(element_application)) {
            application = new Application(Application.UNKNOWN, null);
            if (node.hasAttributes()) {
                NamedNodeMap attributes = node.getAttributes();
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node attribute = attributes.item(i);
                    String name = attribute.getNodeName();
                    if (name.equals(attribut_name)) {
                        application.setName(attribute.getNodeValue());
                    }
                }
            }
            if (node.hasChildNodes()) {
                NodeList children = node.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if ((child.getNodeType() == Node.CDATA_SECTION_NODE)
                            || (child.getNodeType() == Node.TEXT_NODE)) {
                        application.setPath(child.getNodeValue());
                    }
                }
            }
        }
        return application;
    }

//    public static void main(String[] args) {
//        File file = new File("C:\\Users\\fabrice\\liste.xml");
//        
//        ApplicationsList applications = null;
//        // lecture du contenu d'un fichier XML avec DOM
//        Document document = getDocument(file);
//        
//        printNodeInfo(document);
//        
//        //traitement du document
//        if(document != null)
//            applications = parseNodeAsApplicationsList(document.getDocumentElement());
//
//        if(applications == null)
//            applications = new ApplicationsList();
//        
//        printApplicationsInfo(applications);
//    }
//
//    private static void printNodeInfo(Node node) {
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
//    private static void printApplicationsInfo(ApplicationsList list) {
//        for (Iterator<Application> it = list.iterator(); it.hasNext();) {
//            Application application = it.next();
//            println("application:" + application.getName() + " path:" + application.getPath());
//            
//            if(application.getPath() == null)
//                continue;
//            
//            File file = new File(application.getPath());
//            if(file.exists()) {
//                String command = "cmd /c \"" + file.getAbsolutePath() + "\"";
//                
//                StringBuilder result = new StringBuilder(64);
//                StringBuilder error = new StringBuilder(64);
//                SupervisionUtilities.startProcess(command, result, error);
//            }
//        }
//    }
//
//    private static void println(String message) {
//        System.out.println(message);
//    }
}
