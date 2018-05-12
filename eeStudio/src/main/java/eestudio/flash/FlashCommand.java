package eestudio.flash;

import lombok.Getter;
import lombok.Setter;

/**
 * Cette classe représente une commande à effectuer.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
@Getter
@Setter
public class FlashCommand {
    /**
     * Commande pour les numéros de versions
     */
    public final static String VERSION = "version";
    /**
     * Commande pour le changement de langue
     */
    public final static String LANGUAGE = "language";
    /**
     * Commande pour le changement d'état de lecture/enregistremnt
     */
    public final static String RUNNING_STATE = "runningState";
    /**
     * Commande pour le changement de la durée de la piste
     */
    public final static String TIME_MAX = "timeMax";
    /**
     * Commande pour le changement de texte associé
     */
    public final static String TEXT = "text";
    /**
     * Commande pour le changement de texte associé
     */
    public final static String TIME = "time";
    /**
     * Commande pour le changement de temps en insertion de voix
     */
    public final static String TIME_INSERT_VOICE = "timeVoice";
    /**
     * Commande pour le changement dans les index
     */
    public final static String INDEXES = "indexes";

    /**
     * Commande pour le changement de la réprésentation du canal audio gauche
     */
    public final static String AUDIO_LEFT_CHANNEl_FILE = "audioLchannelFile";
    /**
     * Commande pour le changement de la réprésentation du canal audio droit
     */
    public final static String AUDIO_RIGHT_CHANNEl_FILE = "audioRchannelFile";
    /**
     * Commande pour le changement de fichier vidéo
     */
    public final static String PLAYER_FILE = "playerFile";

    /**
     * Etat pause
     */
    public static final String PAUSE = "pause";
    /**
     * Etat lecture
     */
    public static final String PLAYING = "playing";
    /**
     * Etat enregistrement de la voix
     */
    public static final String RECORDING = "recording";
    /**
     * Etat d'insertion de la voix
     */
    public static final String INSERT = "insert";

    /**
     * Le nom de la commande
     */
    private String action;
    /**
     * Paramètre
     */
    private String parameter;

    /**
     * Initialisation d'une commande sans paramètre. Equivalant à {@code  FlashCommand(action, null)}.
     *
     * @param action le nom de l'action.
     */
    FlashCommand(String action) {
        this(action, null);
    }

    /**
     * Initialisation d'une commande avec paramètres.
     *
     * @param action le nom de l'action.
     * @param parameter les parmètres.
     */
    FlashCommand(String action, String parameter) {
        this.action = action;
        this.parameter = parameter;
    }

}
