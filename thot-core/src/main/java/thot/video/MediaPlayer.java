package thot.video;

import java.awt.*;

import thot.video.event.MediaPlayerListener;

/**
 * Interface générique pour le lecteur multimedia.
 */
public interface MediaPlayer {

    /**
     * Change le média à lire.
     *
     * @param mrl le nouveau média à lire.
     */
    void setMedia(String mrl);

    /**
     * Lit le média.
     */
    void play();

    /**
     * Mise en toggle pause la lecture du média.
     */
    void pause();

    /**
     * Arrête la lecture du média.
     */
    void stop();

    /**
     * Donne la durée du média en ms.
     *
     * @return la durée du média en ms.
     */
    long getMediaLength();

    /**
     * Donne la durée du média en ms.
     *
     * @return la durée du média en ms.
     */
    long getLength();

    /**
     * Donne le temps où l'on est (en ms).
     *
     * @return le temps où l'on est (en ms).
     */
    long getTime();

    /**
     * Déplace la lecture à l'endroit voulu (en ms).
     *
     * @param time l'endroit voulu (en ms).
     */
    void setTime(long time);

    /**
     * Donne la position dans le média en pourcentage (de 0 à 1).
     *
     * @return la position dans le média.
     */
    float getPosition();

    /**
     * Déplace la lecture à l'endroit voulu en pourcentage (de 0 à 1).
     *
     * @param position l'endroit voulu.
     */
    void setPosition(float position);

    /**
     * Donne la valeur du niveau sonore en pourcentage (de 0 à 200).
     *
     * @return la valeur du niveau sonore.
     */
    int getVolume();

    /**
     * Change la valeur du niveau sonore en pourcentage (de 0 à 200).
     *
     * @param volume la valeur du niveau sonore entre 0 à 200.
     */
    void setVolume(int volume);

    /**
     * Modifie le mode plein écran.
     *
     * @param fullscreen l'état du plein écran.
     */
    void setFullScreen(boolean fullscreen);

    /**
     * Indique si on est en plein écran ou non.
     *
     * @return le mode plein écran.
     */
    boolean isFullScreen();

    /**
     * Configure la sortie vidéo.
     *
     * @param component le composant graphique où sera la vidéo.
     */
    void setVideoOutput(Component component);

    /**
     * Indique le fichier de soustitrage à utiliser.
     *
     * @param fileName le nom du fichier de soustitrage.
     */
    void setVideoSubtitleFile(String fileName);

    /**
     * Retourne l'état du lecteur.
     *
     * @return la valeur de l'état
     */
    MediaPlayerState getState();

    /**
     * Indique si le lecteur a fini.
     *
     * @return {@code true} si le lecteur a fini.
     */
    default boolean isEndReached() {
        return MediaPlayerState.ENDED == getState();
    }

    /**
     * Retourne si le lecteur est en mode lecture.
     *
     * @return {@code true} si le lecteur est en lecture.
     */
    default boolean isPlaying() {
        return MediaPlayerState.PLAYING == getState();
    }

    /**
     * Retourne si une erreur à eu lieu.
     *
     * @return {@code true} si une erreur est survenue.
     */
    default boolean isErrorOccured() {
        return MediaPlayerState.ERROR == getState();
    }

    /**
     * Ajoute un listener au lecteur.
     *
     * @param listener à ajouter.
     */
    void addMediaPlayerListener(MediaPlayerListener listener);

    /**
     * Enlève un listener au lecteur.
     *
     * @param listener à ajouter.
     */
    void removeMediaPlayerListener(MediaPlayerListener listener);

    /**
     * Libère la mémoire utilisée par le lecteur.
     */
    void release();
}
