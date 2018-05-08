package thot.labo;

import java.awt.*;

import thot.labo.index.Index;
import thot.utils.ProgressPercentListener;

/**
 * Listener pour écouter les changement d'état du coeur du laboratoire de langue.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface LaboListener extends ProgressPercentListener {

    /**
     * Appelé quand un état (play/pause/stop ou chargement media) a changé.
     *
     * @param running le nouvel état.
     * @param media le type de média chargé.
     */
    void stateChanged(int running, int media);

    /**
     * Appelé quand le mode de lecture automatique est changé.
     *
     * @param indexesMode le nouveau mode.
     */
    void indexesModeChanged(boolean indexesMode);

    /**
     * Appelé quand le temps d'enregistrement maximun a changé.
     *
     * @param recordTimeMax le nouveau temps maximum.
     */
    void recordTimeMaxChanged(long recordTimeMax);

    /**
     * Appelé quand le temps a changé.
     *
     * @param time le nouveau temps en millisecondes.
     */
    void timeChanged(long time);

    /**
     * Appelé lorsqu'un texte a été chargé.
     *
     * @param text le texte chargé.
     */
    void textLoaded(String text);

    /**
     * Appelé quand le mode plein écran a changé.
     *
     * @param fullscreen le vnouvel état.
     */
    void fullScreenChanged(boolean fullscreen);

    /**
     * Appelé quand un index a changé.
     */
    void indexesChanged();

    /**
     * Appelé quand l'index courant à changé.
     *
     * @param index le nouveau index.
     */
    void currentIndexChanged(Index index);

    /**
     * Appelé quand une image est charger ou décharger.
     *
     * @param image la nouvelle image.
     */
    void imageChanged(Image image);
}
