package eestudio.utils;

import java.io.File;

/**
 * Interface poue la conversion de fichiers.
 * 
 * @author Fabrice Alleau
 * @since version 0.99
 * @version 1.03
 */
public interface Converter {
    /** Conversion réussi avec success */
     int SUCCESS = 0;
    /** Erreur lors de la conversion due à des fichiers manquants */
     int FILE_NOT_FIND = -1;
    /** Erreur lors de la conversion */
     int CONVERSION_ERROR = -2;

    /**
     * Initialise le convertisseur
     * 
     * @since version 1.01
     */
    void init();

    /**
     * Arrête le processus.
     * 
     * @since version 0.99
     */
    void cancel();

    /**
     * Ajoute d'une écoute de type ProgessListener.
     *
     * @param listener l'écoute à ajouter.
     * @since version 0.99
     */
    void addListener(ProgessListener listener);

    /**
     * Enlève une écoute de type ProgessListener.
     *
     * @param listener l'écoute à enlever.
     * @since version 0.99
     */
    void removeListener(ProgessListener listener);

    /**
     * Modifie le nombre de canaux audio.
     *
     * @param audioChannels le nombre de canaux audio.
     * @since version 0.99
     */
    void setAudioChannels(int audioChannels);

    /**
     * Modifie le taux d'échantillonage.
     *
     * @param audioRate la fréquence en Hz.
     * @since version 0.99
     */
    void setAudioRate(int audioRate);

    /**
     * Modifie la taille de la vidéo.
     * 
     * @param width la largeur.
     * @param height la hauteur.
     * @since version 0.99
     */
    void setVideoSize(int width, int height);

    /**
     * Retourne la durée du fichier en ms.
     * 
     * @param file le fichier.
     * @return la durée du fichier en ms.
     * @since version 0.99
     */
    long getDuration(File file);

    /**
     * Détermine si le fichier possède un flux audio.
     * 
     * @param file le fichier.
     * @return si le fichier possède un flux audio.
     * @since version 0.99
     */
    boolean hasAudioSrteam(File file);

    /**
     * Détermine si le fichier possède un flux vidéo.
     * 
     * @param file le fichier.
     * @return si le fichier possède un flux vidéo.
     * @since version 0.99
     */
    boolean hasVideoSrteam(File file);

    /**
     * Conversion de fichiers.
     * La conversion est définie par le type du fichier destination.
     * 
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @param tags les tags au format mp3.
     * @return 0 si la conversion s'est bien terminée.
     * @since version 0.99 - version 1.03
     */
    int convert(File destFile, File srcFile, TagList tags);

    /**
     * Conversion de fichiers.
     * La conversion est définie par le type du fichier destination :
     * Types supportés: .wav, .mp3, .mp4, .flv
     *      Paramètres par défaut:
     *          - audio mono à 44,1kHz
     *          - 128kbit/s pour le mp3
     *          - VBR (quality 10) pour le ogg
     *          - taille de video "640x480"
     *          - 25 images par seconde
     *          - pas d'audio pour le flv
     *          - audio en mp3 pour le mp4
     * 
     * @param destFile le fichier de destination.
     * @param videoFile le fichier pour la piste vidéo.
     * @param audioFile le fichier pour la piste audio.
     * @param subtitleFile le fichier pour les sous
     * @param tags les tags au format mp3.titres.
     * @return 0 si la conversion s'est bien terminée.
     * @since version 0.99 - version 1.03
     */
    int convert(File destFile, File audioFile, File videoFile, File subtitleFile, TagList tags);

    /**
     * Extrait les pistes audio et vidéo du fichier source au format WAV (mono à 44kHz) et FLV ("640x480", 25 fps)
     * 
     * @param srcFile le fichier source contenant les deux pistes.
     * @param audioFile le fichier de destination pour la piste audio.
     * @param videoFile le fichier de destination pour la piste vidéo.
     * @return 0 si la conversion s'est bien terminée.
     * @since version 0.99 - version 1.03
     */
    int extractToWAVandFLV(File srcFile, File audioFile, File videoFile);

    /**
     * Insére une vidéo "blanche" (image fixe) sur une vidéo.
     * 
     * @param file la vidéo dans la quelle on insère la vidéo "blanche".
     * @param imageFile l'image fixe à insérer.
     * @param begin le temps de départ de l'insertion dans la vidéo initiale.
     * @param duration la durée de la vidéo blanche à insérer.
     * @return 0 si la conversion s'est bien terminée.
     * @since version 0.99 - version 1.03
     */
    int insertBlankVideo(File file, File imageFile, long begin, long duration);

    /**
     * Duplique la plage donnée de la vidéo et l'insére à la fin de la plage.
     * 
     * @param file la vidéo à modifier.
     * @param begin le temps de départ de la partie à dupliquer.
     * @param end le temps de fin de la partie à dupliquer.
     * @return 0 si la conversion s'est bien terminée.
     * @since version 0.99 - version 1.03
     */
    int insertDuplicatedVideo(File file, long begin, long end);

    /**
     * Insére une vidéo dans une vidéo.
     * 
     * @param file la vidéo dans la quelle on insère la vidéo.
     * @param insertFile le fichier vidéo à insérer.
     * @param begin le temps de départ de l'insertion dans la vidéo initiale.
     * @return 0 si la conversion s'est bien terminée.
     * @since version 0.99 - version 1.03
     */
    int insertVideo(File file, File insertFile, long begin);

    /**
     * Crée une vidéo "blanche" (image fixe) d'une durée spécifique.
     * 
     * @param destFile le fichier de destination de la vidéo.
     * @param imageFile l'image fixe à insérer.
     * @param duration la durée de la vidéo blanche.
     * @return 0 si la conversion s'est bien terminée.
     * @since version 0.99 - version 1.03
     */
    int createBlankVideo(File destFile, File imageFile, long duration);

    /**
     * Supprime une partie de la vidéo.
     * 
     * @param file la vidéo dans la quelle on supprime une plage de temps.
     * @param begin le temps de départ de la partie à supprimer.
     * @param end le temps de fin de la partie à supprimer.
     * @return 0 si la conversion s'est bien terminée.
     * @since version 0.99 - version 1.03
     */
    int removeVideo(File file, long begin, long end);

    /**
     * Déplace et redimensionne une partie de la vidéo courante.
     * 
     * @param file la vidéo.
     * @param imageFile l'image fixe à insérer.
     * @param begin le temps de départ de la partie à déplacer.
     * @param end le temps de fin de la partie à déplacer.
     * @param newBegin le nouveau temps de départ de la partie à déplacer.
     * @param duration la nouvelle durée de la partie sélectionnée.
     * @return 0 si la conversion s'est bien terminée.
     * @since version 0.99 - version 1.03
     */
    int moveVideoAndResize(File file, File imageFile, long begin, long end, long newBegin, long duration);
    
    /**
     * Modifie la vitesse d'une partie de la vidéo.
     * 
     * @param file la vidéo.
     * @param begin le temps de départ de la partie à modifier.
     * @param end le temps de fin de la partie à modifier.
     * @param oldRate l'ancienne vitesse de la partie à modifier.
     * @param newRate la nouvelle vitesse de la partie à modifier.
     * @param normalFile la vidéo correspondante au temps à un vitesse normale.
     * @return 0 si la conversion s'est bien terminée.
     * @since version 1.01 - version 1.03
     */
    int setVideoRate(File file, long begin, long end, float oldRate, float newRate, File normalFile);
}
