package thot.video;

import java.io.File;

import thot.utils.ProgressPercentListener;

/**
 * Interface poue la conversion de fichiers.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface Converter {

    /**
     * Arrête le processus.
     */
    void cancel();

    /**
     * Ajoute d'une écoute de type ProgessListener.
     *
     * @param listener l'écoute à ajouter.
     */
    void addListener(ProgressPercentListener listener);

    /**
     * Enlève une écoute de type ProgessListener.
     *
     * @param listener l'écoute à enlever.
     */
    void removeListener(ProgressPercentListener listener);

//    /**
//     * Modifie le bitrate de l'audio.
//     *
//     * @param audioBitrate le birate en bit/s.
//     */
//    void setAudioBitrate(int audioBitrate);

    /**
     * Modifie le nombre de canaux audio.
     *
     * @param audioChannels le nombre de canaux audio.
     */
    void setAudioChannels(int audioChannels);

    /**
     * Modifie le taux d'échantillonage.
     *
     * @param audioRate la fréquence en Hz.
     */
    void setAudioRate(int audioRate);

    /**
     * Modifie la taille de la vidéo.
     *
     * @param width la largeur.
     * @param height la hauteur.
     */
    void setVideoSize(int width, int height);

    /**
     * Retourne la durée du fichier en ms.
     *
     * @param file le fichier.
     *
     * @return la durée du fichier en ms.
     */
    long getDuration(File file);

    /**
     * Détermine si le fichier possède un flux audio.
     *
     * @param file le fichier.
     *
     * @return si le fichier possède un flux audio.
     */
    boolean hasAudioSrteam(File file);

    /**
     * Détermine si le fichier possède un flux vidéo.
     *
     * @param file le fichier.
     *
     * @return si le fichier possède un flux vidéo.
     */
    boolean hasVideoSrteam(File file);

    /**
     * Conversion de fichiers. La conversion est définie par le type du fichier destination.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     *
     * @return les messages de conversion.
     */
    String convert(File destFile, File srcFile);

    /**
     * Conversion de fichiers. La conversion est définie par le type du fichier destination.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @param audioRate la fréquence en Hz.
     * @param channels le nombre de canaux audio.
     *
     * @return les messages de conversion.
     */
    String convert(File destFile, File srcFile, int audioRate, int channels);
}
