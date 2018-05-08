package thot.supervision;

import java.util.Arrays;

import lombok.Getter;

/**
 * Liste des paramètres possibles pour les commandes échangées entre le poste professeur et les postes élèves.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public enum CommandParamater {

    /**
     * paramètre général.
     */
    PARAMETER("parameter"),
    /**
     * paramètre pour le nom (login).
     */
    NAME("name"),
    /**
     * paramètre pour le niveau de la batterie.
     */
    BATTERY("battery"),
    /**
     * paramètre pour l'adresse IP.
     */
    IP_ADDRESS("ipAddress"),
    /**
     * paramètre pour le mot de passe.
     */
    PASSWORD("password"),
    /**
     * paramètre pour la validation login - mot de passe.
     */
    PASSWORD_CHECKED("passwordChecked"),
    /**
     * paramètre pour le numero de port général.
     */
    PORT("port"),
    /**
     * paramètre pour le numero de port du transfert d'écran.
     */
    SCREEN_PORT("screenPort"),
    /**
     * paramètre pour le numero de port du transfert audio.
     */
    AUDIO_PORT("audioPort"),
    /**
     * paramètre pour l'adresse IP d'un destinataire.
     */
    CLIENT_IP_ADDRESS("clientIP"),
    /**
     * paramètre pour le partage du clavier et de la souris.
     */
    REMOTE_HANDLING("remoteHandling"),
    /**
     * paramètre pour le nombre de destinataires.
     */
    CLIENT_NUMBER("clientNumber"),
    /**
     * paramètre pour le nombre de frame par seconde maximal.
     */
    FPS("fps"),
    /**
     * paramètre pour la qualité de compression des images pour le transfert d'écran.
     */
    QUALITY("quality"),
    /**
     * paramètre pour le nombre de lignes pour le transfert d'écran.
     */
    LINES("lines"),
    /**
     * paramètre pour le temps d'attende de connexions pour le transfert d'écran.
     */
    TIMEOUT("timeout"),
    /**
     * paramètre pour le nom du fichier.
     */
    FILE("file"),
    /**
     * paramètre pour la taille du fichier.
     */
    SIZE("size"),
    /**
     * paramètre pour la valeur du message.
     */
    MESSAGE("message"),
    /**
     * paramètre pour la valeur de blocage/déblocage.
     */
    BLOCK("block"),
    /**
     * paramètre pour une liste.
     */
    LIST("list"),
    /**
     * paramètre pour le nom d'une application.
     */
    APPLICATION("application"),

    /**
     * Action pour recevoir un fichier.
     */
    RECEIVE_FILE("receiveFile"),
    /**
     * Déchargement du fichier multimédia.
     */
    MEDIA_UNLOAD("11"),
    /**
     * Efface les données du module audio.
     */
    AUDIO_ERASE("22"),
    /**
     * Efface les données du module texte.
     */
    TEXT_ERASE("32");

    @Getter
    private String parameter;

    CommandParamater(String parameter) {
        this.parameter = parameter;
    }

    public static CommandParamater getCommandParamater(String parameter) {
        return Arrays.stream(CommandParamater.values())
                .filter(command -> command.getParameter().equalsIgnoreCase(parameter)).findFirst().orElse(null);
    }
}
