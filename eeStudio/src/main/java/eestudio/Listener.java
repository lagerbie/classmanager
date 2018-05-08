package eestudio;

import java.awt.*;
import java.io.File;

import thot.utils.ProgressListener;


/**
 * Listener pour écouter les changement d'état du coeur de l'application.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface Listener extends ProgressListener {

    /**
     * Appelé quand l'état du modue audio a changé.
     *
     * @param state le nouvel état.
     */
    void runningStateChanged(int state);

    /**
     * Appelé quand le temps d'enregistrement maximun a changé.
     *
     * @param recordTimeMax le nouveau temps maximum.
     */
    void recordTimeMaxChanged(long recordTimeMax);

    /**
     * Appelé lorsqu'un texte a été chargé.
     *
     * @param text le texte chargé.
     * @param styled indique si le texte comporte des styles.
     */
    void textLoaded(String text, boolean styled);

    /**
     * Appelé quand le temps a changé.
     *
     * @param time le nouveau temps en millisecondes.
     */
    void timeChanged(long time);

    /**
     * Appelé quand le temps a changé lors de l'insertion de la voix.
     *
     * @param time le nouveau temps en millisecondes.
     */
    void insertVoiceTimeChanged(long time);

    /**
     * Appelé quand un index a changé.
     *
     * @param xmlIndexesDescription la description de la liste d'index.
     */
    void indexesChanged(String xmlIndexesDescription);

    /**
     * Appelé quand une image est charger ou décharger.
     *
     * @param image la nouvelle image.
     */
    void imageChanged(Image image);

    /**
     * Appelé quand le fichier des représentation des données audio a changé.
     *
     * @param leftChannelFile le fichier image représentant les données audio du canal gauche.
     * @param rigthChannelFile le fichier image représentant les données audio du canal droit.
     */
    void audioWaveFileChanged(File leftChannelFile, File rigthChannelFile);

    /**
     * Appelé quand le fichier vidéo a changé.
     *
     * @param file le fichier vidéo.
     */
    void videoFileChanged(File file);

}
