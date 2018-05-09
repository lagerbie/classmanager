package thot.video;

import java.io.File;

import thot.exception.ThotException;
import thot.labo.TagList;
import thot.utils.ProgressListener;

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
    void addListener(ProgressListener listener);

    /**
     * Enlève une écoute de type ProgessListener.
     *
     * @param listener l'écoute à enlever.
     */
    void removeListener(ProgressListener listener);

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
    long getDuration(File file) throws ThotException;

    /**
     * Détermine si le fichier possède un flux audio.
     *
     * @param file le fichier.
     *
     * @return si le fichier possède un flux audio.
     */
    boolean hasAudioSrteam(File file) throws ThotException;

    /**
     * Détermine si le fichier possède un flux vidéo.
     *
     * @param file le fichier.
     *
     * @return si le fichier possède un flux vidéo.
     */
    boolean hasVideoSrteam(File file) throws ThotException;

    /**
     * Conversion de fichiers. La conversion est définie par le type du fichier destination.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @param tags les tags au format mp3.
     */
    void convert(File destFile, File srcFile, TagList tags) throws ThotException;

    /**
     * Conversion de fichiers. La conversion est définie par le type du fichier destination.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @param tags les tags au format mp3.
     * @param audioRate la fréquence en Hz.
     * @param channels le nombre de canaux audio.
     */
    void convert(File destFile, File srcFile, TagList tags, int audioRate, int channels) throws ThotException;

    /**
     * Conversion de fichiers. La conversion est définie par le type du fichier destination : Types supportés: .wav,
     * .mp3, .mp4, .flv Paramètres par défaut: - audio mono à 44,1kHz - 128kbit/s pour le mp3 - VBR (quality 10) pour le
     * ogg - taille de video "640x480" - 25 images par seconde - pas d'audio pour le flv - audio en mp3 pour le mp4
     *
     * @param destFile le fichier de destination.
     * @param videoFile le fichier pour la piste vidéo.
     * @param audioFile le fichier pour la piste audio.
     * @param subtitleFile le fichier pour les sous
     * @param tags les tags au format mp3.titres.
     */
    void convert(File destFile, File audioFile, File videoFile, File subtitleFile, TagList tags) throws ThotException;

    /**
     * Extrait les pistes audio et vidéo du fichier source au format WAV (mono à 44kHz) et FLV ("640x480", 25 fps)
     *
     * @param srcFile le fichier source contenant les deux pistes.
     * @param audioFile le fichier de destination pour la piste audio.
     * @param videoFile le fichier de destination pour la piste vidéo.
     */
    void extractToWAVandFLV(File srcFile, File audioFile, File videoFile) throws ThotException;

    /**
     * Insére une vidéo "blanche" (image fixe) sur une vidéo.
     *
     * @param file la vidéo dans la quelle on insère la vidéo "blanche".
     * @param imageFile l'image fixe à insérer.
     * @param begin le temps de départ de l'insertion dans la vidéo initiale.
     * @param duration la durée de la vidéo blanche à insérer.
     */
    void insertBlankVideo(File file, File imageFile, long begin, long duration) throws ThotException;

    /**
     * Duplique la plage donnée de la vidéo et l'insére à la fin de la plage.
     *
     * @param file la vidéo à modifier.
     * @param begin le temps de départ de la partie à dupliquer.
     * @param end le temps de fin de la partie à dupliquer.
     */
    void insertDuplicatedVideo(File file, long begin, long end) throws ThotException;

    /**
     * Insére une vidéo dans une vidéo.
     *
     * @param file la vidéo dans la quelle on insère la vidéo.
     * @param insertFile le fichier vidéo à insérer.
     * @param begin le temps de départ de l'insertion dans la vidéo initiale.
     */
    void insertVideo(File file, File insertFile, long begin) throws ThotException;

    /**
     * Crée une vidéo "blanche" (image fixe) d'une durée spécifique.
     *
     * @param destFile le fichier de destination de la vidéo.
     * @param imageFile l'image fixe à insérer.
     * @param duration la durée de la vidéo blanche.
     */
    void createBlankVideo(File destFile, File imageFile, long duration) throws ThotException;

    /**
     * Supprime une partie de la vidéo.
     *
     * @param file la vidéo dans la quelle on supprime une plage de temps.
     * @param begin le temps de départ de la partie à supprimer.
     * @param end le temps de fin de la partie à supprimer.
     */
    void removeVideo(File file, long begin, long end) throws ThotException;

    /**
     * Déplace et redimensionne une partie de la vidéo courante.
     *
     * @param file la vidéo.
     * @param imageFile l'image fixe à insérer.
     * @param begin le temps de départ de la partie à déplacer.
     * @param end le temps de fin de la partie à déplacer.
     * @param newBegin le nouveau temps de départ de la partie à déplacer.
     * @param duration la nouvelle durée de la partie sélectionnée.
     */
    void moveVideoAndResize(File file, File imageFile, long begin, long end, long newBegin, long duration)
            throws ThotException;

    /**
     * Modifie la vitesse d'une partie de la vidéo.
     *
     * @param file la vidéo.
     * @param begin le temps de départ de la partie à modifier.
     * @param end le temps de fin de la partie à modifier.
     * @param oldRate l'ancienne vitesse de la partie à modifier.
     * @param newRate la nouvelle vitesse de la partie à modifier.
     * @param normalFile la vidéo correspondante au temps à un vitesse normale.
     */
    void setVideoRate(File file, long begin, long end, float oldRate, float newRate, File normalFile)
            throws ThotException;

}
