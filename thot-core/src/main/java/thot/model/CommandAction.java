package thot.model;

import java.util.Arrays;

import lombok.Getter;

public enum CommandAction {

    /**
     * Valeur de commande inconnue.
     */
    UNKNOWN("unknown"),

    /**
     * Action pour rechercher des élèves.
     */
    FIND("find"),
    /**
     * Action pour tester la présence des élèves.
     */
    PING("ping"),
    /**
     * Action pour de réponse au PING des élèves.
     */
    PONG("pong"),
    /**
     * Action pour une demande d'aide.
     */
    HELP_CALL("help"),
    /**
     * Action pour envoyer son écran.
     */
    SEND_SCREEN("sendScreen"),
    /**
     * Action pour arrêter d'envoyer son écran.
     */
    SEND_SCREEN_STOP("stopSendScreen"),
    /**
     * Action pour envoyer sa voix.
     */
    SEND_VOICE("sendVoice"),
    /**
     * Action pour recevoir un écran.
     */
    RECEIVE_SCREEN("receiveScreen"),
    /**
     * Action pour stopper la réception d'écran.
     */
    RECEIVE_SCREEN_STOP("stopReceiveScreen"),
    /**
     * Action pour afficher un écran noir.
     */
    RECEIVE_BLACK_SCREEN("receiveBlackScreen"),
    /**
     * Action pour commencer la communication orale avec un élève.
     */
    PAIRING("pairing"),
    /**
     * Action pour stopper la communication orale avec un élève.
     */
    PAIRING_STOP("stopPairing"),
    /**
     * Action pour ouvrir un document.
     */
    LAUNCH_FILE("launchFile"),
    /**
     * Action pour recevoir un fichier.
     */
    RECEIVE_FILE("receiveFile"),
    /**
     * Action pour afficher un message.
     */
    RECEIVE_MESSAGE("receiveMessage"),
    /**
     * Action pour éteindre l'ordinateur.
     */
    SHUTDOWN("shutdown"),
    /**
     * Action pour fermer le session sur l'ordinateur.
     */
    SHUTDOWN_SESSION("shutdownSession"),
    /**
     * Action pour réinitialiser le login de supervision.
     */
    RESET_LOGIN("resetLogin"),
    /**
     * Action pour exécuter un programme.
     */
    EXECUTE("execute"),
    /**
     * Action pour bloquer/débloquer le clavier et la souris.
     */
    BLOCK_KEYBOARD("blockKeyboard"),
    /**
     * Action pour bloquer/débloquer l'accès à Internet.
     */
    BLOCK_INTERNET("blokInternet"),
    /**
     * Action pour bloquer/débloquer l'accès à une application.
     */
    RECEIVE_INTREDICTION("receiveInterdiction"),
    /**
     * Action pour effacer le dossier utilisateur et les documents distribués.
     */
    DELETE_DOCUMENT("deleteDocument"),
    /**
     * Action pour informer la fin de la supervision.
     */
    MASTER_CLOSED("masterClosed"),
    /**
     * Action de notification d'erreur.
     */
    END_ERROR("endError"),
    /**
     * Action pour fermer (utiliser pour la mosaique).
     */
    CLOSE("close"),

    /**
     * Find de communication.
     */
    END("0"),
    /**
     * Change la langue sur le poste élève. paramètre attendu : String pour Locale
     */
    LANGUAGE("2"),
    /**
     * Gèle des commandes du poste élève. paramètre attendu : boolean
     */
    FREEZE("3"),
    /**
     * Charge un fichier dans le module multimédia. paramètre attendu : String
     */
    MEDIA_LOAD("10"),
    /**
     * Déchargement du fichier multimédia.
     */
    MEDIA_UNLOAD("11"),
    /**
     * Change le volume du média. paramètre attendu : int de 0 à 100
     */
    MEDIA_VOLUME("12"),
    /**
     * Change le mode plein écran. paramètre attendu : boolean
     */
    MEDIA_FULL_SCREEN("13"),
    /**
     * Charge un fichier d'index multimédia. paramètre attendu : String
     */
    MEDIA_LOAD_INDEXES("14"),
    /**
     * Charge un fichier de soustitres. paramètre attendu : String
     */
    MEDIA_LOAD_SUBTITLE("15"),
    /**
     * Charge un fichier dans le module audio. paramètre attendu : String
     */
    AUDIO_LOAD("20"),
    /**
     * Sauvegarde d'un fichier audio. paramètre attendu : String
     */
    AUDIO_SAVE("21"),
    /**
     * Efface les données du module audio.
     */
    AUDIO_ERASE("22"),
    /**
     * Déclenche la lecture du module audio. paramètre attendu : long
     */
    AUDIO_PLAY("23"),
    /**
     * Déclenche l'enregistrement du module audio. paramètre attendu : long
     */
    AUDIO_RECORD("24"),
    /**
     * Met en pause le module audio.
     */
    AUDIO_PAUSE("25"),
    /**
     * Change le volume du média paramètre. attendu : int de 0 à 100
     */
    AUDIO_VOLUME("26"),
    /**
     * Récupère un fichier audio.
     */
    AUDIO_GET_FILE("27"),
    /**
     * Charge un fichier texte. paramètre attendu : String
     */
    TEXT_LOAD("30"),
    /**
     * Sauvegarde d'un fichier texte. paramètre attendu : String
     */
    TEXT_SAVE("31"),
    /**
     * Efface les données du module texte.
     */
    TEXT_ERASE("32"),
    /**
     * Change le temps d'attente entre les mots. paramètre attendu : long
     */
    TEXT_WAIT("33"),
    /**
     * Récupère un fichier texte.
     */
    TEXT_GET_FILE("35"),
    /**
     * Change le temps maximum d'enregistrement.
     */
    TIME_MAX("40"),
    /**
     * Déplace le curseur du temps. paramètre attendu : long
     */
    TIME_MOVE("41"),
    /**
     * Retour à zéro.
     */
    TIME_TO_ZERO("42"),
    /**
     * Récupère un fichier.
     */
    FILE_GET("50"),
    /**
     * Envoie un fichier.
     */
    FILE_SEND("51"),
    /**
     * Envoi d'un message. paramètre attendu : String
     */
    SEND_MESSAGE("65");

    @Getter
    private String action;

    CommandAction(String action) {
        this.action = action;
    }

    public static CommandAction getCommandAction(String action) {
        return Arrays.stream(CommandAction.values())
                .filter(commandAction -> commandAction.getAction().equalsIgnoreCase(action)).findFirst().orElse(null);
    }
}
