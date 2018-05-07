package eestudio;

import java.awt.*;
import java.io.File;

import eestudio.utils.ProgessListener;

/*
 * v0.95: ajout de extends ProgessListener (extends EventListener)
 * v0.95: ajout de void insertVoiceTimeChanged(long time);
 * v0.95: ajout de void audioWaveFileChanged(File, File);
 * v0.95: ajout de void videoFileChanged(File file);
 * v0.95: supp de void currentIndexChanged(Index index);
 * v0.95: supp de void waitProcessing(boolean wait);
 * v0.95: modif de indexesChanged() en indexesChanged(String)
 * 
 * v0.96: modif de textLoaded(String) en textLoaded(String, boolean)
 * 
 * v0.99: supp de void audioVolumeChanged(int volume);
 */

/**
 * Listener pour écouter les changement d'état du coeur de l'application.
 * 
 * @author Fabrice Alleau
 * @since version 0.94
 * @version 0.99
 */
public interface Listener extends ProgessListener {

    /**
     * Appelé quand l'état du modue audio a changé.
     *
     * @param state le nouvel état.
     * @since version 0.94
     */
    void runningStateChanged(int state);

    /**
     * Appelé quand le temps d'enregistrement maximun a changé.
     *
     * @param recordTimeMax le nouveau temps maximum.
     * @since version 0.94
     */
    void recordTimeMaxChanged(long recordTimeMax);

    /**
     * Appelé lorsqu'un texte a été chargé.
     *
     * @param text le texte chargé.
     * @param styled indique si le texte comporte des styles.
     * @since version 0.94 - version 0.96
     */
    void textLoaded(String text, boolean styled);

    /**
     * Appelé quand le temps a changé.
     *
     * @param time le nouveau temps en millisecondes.
     * @since version 0.94
     */
    void timeChanged(long time);

    /**
     * Appelé quand le temps a changé lors de l'insertion de la voix.
     *
     * @param time le nouveau temps en millisecondes.
     * @since version 0.95
     */
    void insertVoiceTimeChanged(long time);

    /**
     * Appelé quand un index a changé.
     *
     * @param xmlIndexesDescription la description de la liste d'index.
     * @since version 0.94 - version 0.95
     */
    void indexesChanged(String xmlIndexesDescription);

    /**
     * Appelé quand une image est charger ou décharger.
     *
     * @param image la nouvelle image.
     * @since version 0.94
     */
    void imageChanged(Image image);

    /**
     * Appelé quand le fichier des représentation des données audio a changé.
     *
     * @param leftChannelFile le fichier image représentant les données audio
     *        du canal gauche.
     * @param rigthChannelFile le fichier image représentant les données audio
     *        du canal droit.
     * @since version 0.95
     */
    void audioWaveFileChanged(File leftChannelFile, File rigthChannelFile);

    /**
     * Appelé quand le fichier vidéo a changé.
     *
     * @param file le fichier vidéo.
     * @since version 0.95
     */
    void videoFileChanged(File file);

}//end
