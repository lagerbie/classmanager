package thot.gui;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Interface pour la gestion de ressources textuelles.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class Resources {

    /**
     * Nom pour les fichiers de ressources textes.
     */
    private String textResources;
    /**
     * Resources textuelles courantes.
     */
    private ResourceBundle texts;

    /**
     * Initailisation des resources textuelles.
     *
     * @param textResources le chemin des resources si déférentes.
     */
    public Resources(String textResources) {
        this.textResources = textResources;
        texts = ResourceBundle.getBundle(this.textResources);
    }

    /**
     * Retourne le texte localisé suivant la clé.
     *
     * @param key l'identifiant de la ressource.
     *
     * @return le texte localisé correspondant
     */
    public String getString(String key) {
        String text;
        try {
            text = texts.getString(key);
        } catch (MissingResourceException e) {
            text = "#" + key;
        }
        return text;
    }

    /**
     * Modifie la locale utilisée.
     *
     * @param locale la nouvelle locale.
     */
    public void updateLocale(Locale locale) {
        texts = ResourceBundle.getBundle(textResources, locale);
    }
}
