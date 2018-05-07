package eestudio.gui;

import java.util.Locale;
import java.util.ResourceBundle;

/*
 * v0.99: modif de interface Resources en class Resources
 * v0.99: ajout de private String textResources = "eestudio.resources.eeStudio";
 * v0.99: ajout de private ResourceBundle texts;
 * v0.99: ajout de public Resources()
 * v0.99: ajout de public String getString(String key) [avant abstraite]
 * v0.99: ajout de public void updateLocale(Locale locale)
 */

/**
 * Interface pour la gestion de ressources textuelles.
 * 
 * @author Fabrice Alleau
 * @since version 0.94
 * @version 0.99
 */
public class Resources {
    /** Nom pour les fichiers de ressources textes */
    private String textResources = "eestudio.resources.eeStudio";
    /** Resources textuelles courantes */
    private ResourceBundle texts;

    /**
     * Initialisation des resources texte.
     * 
     * @since version 0.99
     */
    public Resources() {
        texts = ResourceBundle.getBundle(textResources);
    }

    /**
     * Retourne le texte localisé suivant la clé.
     * 
     * @param key l'identifiant de la ressource.
     * @return le texte localisé correspondant
     * @since version 0.99
     */
    public String getString(String key) {
        return texts.getString(key);
    }

    /**
     * Modifie la locale utilisée.
     * 
     * @param locale la nouvelle locale.
     * @since version 0.99
     */
    public void updateLocale(Locale locale) {
        texts = ResourceBundle.getBundle(textResources, locale);
    }

}//end