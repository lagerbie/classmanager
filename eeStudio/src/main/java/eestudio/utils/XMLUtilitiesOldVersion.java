package eestudio.utils;

import java.io.File;

import eestudio.Index;
import eestudio.Indexes;
import eestudio.ProjectFiles;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parse XML pour les fichiers d'index et projet créer avant la version 0.94
 *
 * @author Fabrice Alleau
 * @version 0.99
 * @since version 0.95
 */
public class XMLUtilitiesOldVersion {
    /*
     * Descriptif pour le liste d'index:
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <java version="1.6.0_21" class="java.beans.XMLDecoder">
     *     <object class="labo.Indexes">
     *         <void property="indexes">
     *             <void method="add">
     *                 <object class="labo.Index">
     *                     <void property="finalTime">
     *                         <long>8000</long>
     *                     </void>
     *                     <void property="initialTime">
     *                         <long>4000</long>
     *                     </void>
     *                     <void property="subtitle">
     *                         <string>tst</string>
     *                     </void>
     *                     <void property="type">
     *                         <int>0</int>
     *                     </void>
     *                 </object>
     *             </void>
     *             <void method="add">
     *                 <object class="labo.Index">
     *                     <void property="finalTime">
     *                         <long>12000</long>
     *                     </void>
     *                     <void property="initialTime">
     *                         <long>8000</long>
     *                     </void>
     *                     <void property="subtitle">
     *                         <string/>
     *                     </void>
     *                     <void property="type">
     *                         <int>1</int>
     *                     </void>
     *                 </object>
     *             </void>
     *         </void>
     *         <void property="length">
     *             <long>194000</long>
     *         </void>
     *     </object>
     * </java>
     */
//    /** Balise pour l'élément java (unused) */
//    private static final String element_java = "java";
//    /** Balise pour l'élément object (unused) */
//    private static final String element_object = "object";
    /**
     * Balise d'attribut class
     */
    private static final String attribut_class = "class";
    /**
     * Nom de la classe pour le décodeur
     */
    private static final String class_XMLDecoder = "java.beans.XMLDecoder";

//    /** Balise pour l'élément void (unused) */
//    private static final String element_void = "void";
    /**
     * Balise d'attribut property
     */
    private static final String attribut_property = "property";
//    /** Balise d'attribut method (unused) */
//    private static final String attribut_method = "method";

    /**
     * Nom de la classe pour la liste d'index
     */
    private static final String class_indexes = "labo.Indexes";
    /**
     * Nom de la propriété pour la liste stricte
     */
    private static final String property_indexes = "indexes";
    /**
     * Nom de la propriété pour la durée
     */
    private static final String property_length = "length";
//    /** Nom de la propriété pour la durée du media */
//    private static final String property_mediaLength = "mediaLength";

    /**
     * Nom de la classe pour un index
     */
    private static final String class_index = "labo.Index";
    /**
     * Nom de la propriété pour le type
     */
    private static final String property_type = "type";
    /**
     * Nom de la propriété pour le temps de départ
     */
    private static final String property_initialTime = "initialTime";
    /**
     * Nom de la propriété pour le temps de fin
     */
    private static final String property_finalTime = "finalTime";
    /**
     * Nom de la propriété pour de soustitre
     */
    private static final String property_subtitle = "subtitle";

    /*
     * Descriptif pour le fichier projet:
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <java version="1.6.0_21" class="java.beans.XMLDecoder">
     *     <object class="labo.ProjectFiles">
     *         <void property="audioFile">
     *             <string>test.mp3</string>
     *         </void>
     *         <void property="multimediaFile">
     *             <string>video.mp4</string>
     *         </void>
     *         <void property="indexesFile">
     *             <string>test.index</string>
     *         </void>
     *         <void property="textFile">
     *             <string/>
     *         </void>
     *         <void property="subtitleFile">
     *             <string>test.index.srt</string>
     *         </void>
     *     </object>
     * </java>
     */
    /**
     * Nom de la classe pour un projet
     */
    private static final String class_project = "labo.ProjectFiles";
    /**
     * Nom de la propriété pour le fichier audio
     */
    private static final String property_audioFile = "audioFile";
    /**
     * Nom de la propriété pour le fichier multimédia
     */
    private static final String property_multimediaFile = "multimediaFile";
    /**
     * Nom de la propriété pour le fichier d'index
     */
    private static final String property_indexesFile = "indexesFile";
    /**
     * Nom de la propriété pour le fichier de soustitres
     */
    private static final String property_subtitleFile = "subtitleFile";
    /**
     * Nom de la propriété pour le fichier texte
     */
    private static final String property_textFile = "textFile";


    /**
     * Retourne la liste d'index contenue dans le fichier.
     *
     * @param file le fichier contenant la liste d'index.
     *
     * @return la liste d'index contenue dans le fichier ou une liste vide.
     *
     * @since version 0.95
     */
    public static Indexes loadIndexes(File file) {
        Indexes indexes = null;
        // lecture du contenu d'un fichier XML avec DOM
        Document document = XMLUtilities.getDocument(file);

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
     *
     * @since version 0.95
     */
    public static ProjectFiles loadProject(File file) {
        ProjectFiles project = null;
        // lecture du contenu d'un fichier XML avec DOM
        Document document = XMLUtilities.getDocument(file);

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
     * Parse le noeud xml comme si c'était une liste d'index.
     *
     * @param node le noeud xml.
     *
     * @return la liste d'index ou <code>null</code>.
     *
     * @since version 0.95
     */
    private static Indexes parseNodeAsIndexes(Node node) {
        Indexes indexes = null;

        if (node.hasAttributes()) {
            NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                String name = attribute.getNodeName();
                if (name.contentEquals(attribut_class) && attribute.getNodeValue().contentEquals(class_indexes)) {
                    indexes = new Indexes();
                } else if (name.contentEquals(attribut_class) && attribute.getNodeValue()
                        .contentEquals(class_XMLDecoder)) {
                    if (node.hasChildNodes()) {
                        return parseNodeAsIndexes(node.getFirstChild());
                    }
                }
            }
        }

        if (indexes == null) {
            return null;
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);

            NamedNodeMap attributes = child.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Node attribute = attributes.item(j);
                String name = attribute.getNodeName();
                if (name.contentEquals(attribut_property)) {
                    if (attribute.getNodeValue().contentEquals(property_length)) {
                        if (child.hasChildNodes() && child.getFirstChild().hasChildNodes()) {
                            long value = Utilities
                                    .parseStringAsLong(child.getFirstChild().getFirstChild().getNodeValue());
                            indexes.setMediaLength(value);
                        }
                    } else if (attribute.getNodeValue().contentEquals(property_indexes)) {
                        NodeList indexesChildren = child.getChildNodes();
                        for (int k = 0; k < indexesChildren.getLength(); k++) {
                            if (!indexesChildren.item(k).hasChildNodes()) {
                                continue;
                            }

                            Index index = parseNodeAsIndex(indexesChildren.item(k).getFirstChild());
                            if (index != null) {
                                indexes.add(index);
                            }
                        }
                    }
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
     *
     * @since version 0.95
     */
    private static Index parseNodeAsIndex(Node node) {
        Index index = null;

        if (node.hasAttributes()) {
            NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                String name = attribute.getNodeName();
                if (name.contentEquals(attribut_class) && attribute.getNodeValue().contentEquals(class_index)) {
                    index = new Index(Index.UNKNOWN);
                }
            }
        }

        if (index == null) {
            return null;
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            NamedNodeMap attributes = child.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Node attribute = attributes.item(j);
                String name = attribute.getNodeName();
                if (name.contentEquals(attribut_property)) {
                    if (attribute.getNodeValue().contentEquals(property_initialTime)) {
                        if (child.hasChildNodes() && child.getFirstChild().hasChildNodes()) {
                            long value = Utilities
                                    .parseStringAsLong(child.getFirstChild().getFirstChild().getNodeValue());
                            index.setInitialTime(value);
                        }
                    } else if (attribute.getNodeValue().contentEquals(property_finalTime)) {
                        if (child.hasChildNodes() && child.getFirstChild().hasChildNodes()) {
                            long value = Utilities
                                    .parseStringAsLong(child.getFirstChild().getFirstChild().getNodeValue());
                            index.setFinalTime(value);
                        }
                    } else if (attribute.getNodeValue().contentEquals(property_subtitle)) {
                        if (child.hasChildNodes() && child.getFirstChild().hasChildNodes()) {
                            String value = child.getFirstChild().getFirstChild().getNodeValue();
                            index.setSubtitle(value);
                        }
                    } else if (attribute.getNodeValue().contentEquals(property_type)) {
                        if (child.hasChildNodes() && child.getFirstChild().hasChildNodes()) {
                            int value = Utilities
                                    .parseStringAsInt(child.getFirstChild().getFirstChild().getNodeValue());
                            index.setType(getType(value));
                        }
                    }
                }
            }
        }

        return index;
    }

    /**
     * Convertie l'ancien type pour le nouveau type.
     *
     * @param oldType l'ancien type.
     *
     * @return le type actuel correspondant.
     *
     * @since version 0.95
     */
    private static String getType(int oldType) {
        String type = Index.UNKNOWN;
        switch (oldType) {
            case 0:
                type = Index.PLAY;
                break;
            case 1:
                type = Index.RECORD;
                break;
        }

        return type;
    }

    /**
     * Parse le noeud xml comme si c'était un projet.
     *
     * @param node le noeud xml.
     *
     * @return le projet ou <code>null</code>.
     *
     * @since version 0.95
     */
    private static ProjectFiles parseNodeAsProject(Node node) {
        ProjectFiles project = null;

        if (node.hasAttributes()) {
            NamedNodeMap attributes = node.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attribute = attributes.item(i);
                String name = attribute.getNodeName();
                if (name.contentEquals(attribut_class) && attribute.getNodeValue().contentEquals(class_project)) {
                    project = new ProjectFiles();
                } else if (name.contentEquals(attribut_class)
                        && attribute.getNodeValue().contentEquals(class_XMLDecoder)) {
                    if (node.hasChildNodes()) {
                        return parseNodeAsProject(node.getFirstChild());
                    }
                }
            }
        }

        if (project == null) {
            return null;
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            NamedNodeMap attributes = child.getAttributes();
            for (int j = 0; j < attributes.getLength(); j++) {
                Node attribute = attributes.item(j);
                String name = attribute.getNodeName();
                if (name.contentEquals(attribut_property)) {
                    if (attribute.getNodeValue().contentEquals(property_audioFile)) {
                        if (child.hasChildNodes() && child.getFirstChild().hasChildNodes()) {
                            String value = child.getFirstChild().getFirstChild().getNodeValue();
                            project.setAudioFile(value);
                        }
                    } else if (attribute.getNodeValue().contentEquals(property_multimediaFile)) {
                        if (child.hasChildNodes() && child.getFirstChild().hasChildNodes()) {
                            String value = child.getFirstChild().getFirstChild().getNodeValue();
                            project.setVideoFile(value);
                        }
                    } else if (attribute.getNodeValue().contentEquals(property_indexesFile)) {
                        if (child.hasChildNodes() && child.getFirstChild().hasChildNodes()) {
                            String value = child.getFirstChild().getFirstChild().getNodeValue();
                            project.setIndexesFile(value);
                        }
                    } else if (attribute.getNodeValue().contentEquals(property_subtitleFile)) {
                        if (child.hasChildNodes() && child.getFirstChild().hasChildNodes()) {
                            String value = child.getFirstChild().getFirstChild().getNodeValue();
                            project.setSubtitleFile(value);
                        }
                    } else if (attribute.getNodeValue().contentEquals(property_textFile)) {
                        if (child.hasChildNodes() && child.getFirstChild().hasChildNodes()) {
                            String value = child.getFirstChild().getFirstChild().getNodeValue();
                            project.setTextFile(value);
                        }
                    }
                }
            }
        }

        return project;
    }

}
