package thot.utils;

/**
 * Les différents ports utilisés par l'application.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface ThotPort {

    /**
     * Port pour l'auto-détection multicast.
     */
    int multicastPort = 7201;
    /**
     * Port de communication professeur -> élève.
     */
    int masterToStudentPort = 7202;
    /**
     * Port de communication élève -> professeur.
     */
    int studentToMasterPort = 7203;
    /**
     * Port de communication labo pour les commandes du professeur.
     */
    int masterToStudentLaboPort = 7204;
    /**
     * Port de communication labo pour les commandes de l'élève.
     */
    int studentToMasterLaboPort = 7205;
    /**
     * Port du serveur microphone.
     */
    int soundServerPort = 7206;
    /**
     * Port pour le lancement d'application.
     */
    int launcherPort = 7207;

    /**
     * Port pour la communication thumb -> mosaïque.
     */
    int thumbToMosaiquePort = 7210;
    /**
     * Port pour les évènements de control clavier / souris.
     */
    int keyboardAndMousePort = 7212;

    /**
     * Port d'envoi audio principal.
     */
    int audioPort = 7220;
    /**
     * Port d'envoi audio secondaire.
     */
    int audioPairingPort = 7221;

    /**
     * Port d'envoi audio principal du professeur.
     */
    int audioPortMaster = 7225;
    /**
     * Port d'envoi audio secondaire du professeur.
     */
    int audioPairingPortMaster = 7226;
    /**
     * Port pour la ligne de microphone gérée par un serveur.
     */
    int microphonePort = 7230;
    /**
     * Port pour la ligne de microphone gérée par un serveur.
     */
    int microphoneMasterPort = 7231;
    /**
     * Port pour la ligne de microphone gérée par un serveur.
     */
    int microphoneLaboPort = 7232;
    /**
     * Port de base pour la communication mosaïque -> thumb.
     */
    int mosaiqueToThumbPortBase = 7250;
    /**
     * Port de base pour l'envoi d'écran.
     */
    int screenRemotePortBase = 7300;
    /**
     * Port de base pour le transfert de fichiers.
     */
    int fileTransfertPortBase = 7350;
}
