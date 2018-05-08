package eestudio.flash;

/**
 * Cette classe représente une commande à effectuer.
 *
 * @author Fabrice Alleau
 * @version 1.03
 * @since version 0.94
 */
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
    public static final String pause = "pause";
    /**
     * Etat lecture
     */
    public static final String playing = "playing";
    /**
     * Etat enregistrement de la voix
     */
    public static final String recording = "recording";
    /**
     * Etat d'insertion de la voix
     */
    public static final String insert = "insert";

    /**
     * Commande inconnue
     */
    public final static String UNKNOWN = "unknown";

    /**
     * Le nom de la commande
     */
    private String action;
    /**
     * Paramètre
     */
    private String parameter;

    /**
     * Initialisation d'une commande sans paramètre. Equivalant à <code> FlashCommand(action, null)</code>.
     *
     * @param action le nom de l'action.
     *
     * @since version 0.94
     */
    public FlashCommand(String action) {
        this(action, null);
    }

    /**
     * Initialisation d'une commande avec paramètres.
     *
     * @param action le nom de l'action.
     * @param parameter les parmètres.
     *
     * @since version 0.94
     */
    public FlashCommand(String action, String parameter) {
        this.action = action;
        this.parameter = parameter;
    }

    /**
     * Donne le nom de l'action.
     *
     * @return le nom de l'action.
     *
     * @since version 0.94
     */
    public String getAction() {
        return action;
    }

    /**
     * Retourne la valeur du paramètre.
     *
     * @return la valeur du paramètre.
     *
     * @since version 0.94
     */
    public String getParameter() {
        return parameter;
    }

    /**
     * Modifie le type de l'action.
     *
     * @param action le nom de l'action.
     *
     * @since version 0.94
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Modifie les parmètres.
     *
     * @param parameter les parmètres.
     *
     * @since version 0.94
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    /**
     * Nettoyage des références en vue d'une suppresion.
     *
     * @since version 0.96
     */
    public void clean() {
        this.action = null;
        this.parameter = null;
    }

}
