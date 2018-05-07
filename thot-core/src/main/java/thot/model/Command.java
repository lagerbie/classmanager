/*
 * ClassManager - Supervision de classes et Laboratoire de langue
 * Copyright (C) 2013 Fabrice Alleau <fabrice.alleau@siclic.fr>
 *
 * This file is part of ClassManager.
 *
 * ClassManager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ClassManager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ClassManager.  If not, see <http://www.gnu.org/licenses/>.
 */
package thot.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import thot.utils.Utilities;

/**
 * Cette classe représente les différentes commandes échangées entre le poste
 * professeur et les postes élèves.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Command {

    /**
     * Valeur de commande inconnue.
     */
    public final static String UNKNOWN = "unknown";
    /**
     * Type pour des commandes de supervision.
     */
    public final static String TYPE_SUPERVISION = "supervision";
    /**
     * Type pour des commandes du laboratoire.
     */
    public final static String TYPE_LABORATORY = "laboratory";

    /**
     * commande pour rechercher des élèves.
     */
    public final static String FIND = "find";
    /**
     * commande pour tester la présence des élèves.
     */
    public final static String PING = "ping";
    /**
     * commande pour de réponse au PING des élèves.
     */
    public final static String PONG = "pong";
    /**
     * commande pour une demande d'aide.
     */
    public final static String HELP_CALL = "help";
    /**
     * commande pour envoyer son écran.
     */
    public final static String SEND_SCREEN = "sendScreen";
    /**
     * commande pour arrêter d'envoyer son écran.
     */
    public final static String SEND_SCREEN_STOP = "stopSendScreen";
    /**
     * commande pour envoyer sa voix.
     */
    public final static String SEND_VOICE = "sendVoice";
    /**
     * commande pour recevoir un écran.
     */
    public final static String RECEIVE_SCREEN = "receiveScreen";
    /**
     * commande pour stopper la réception d'écran.
     */
    public final static String RECEIVE_SCREEN_STOP = "stopReceiveScreen";
    /**
     * commande pour afficher un écran noir.
     */
    public final static String RECEIVE_BLACK_SCREEN = "receiveBlackScreen";
    /**
     * commande pour commencer la communication orale avec un élève.
     */
    public final static String PAIRING = "pairing";
    /**
     * commande pour stopper la communication orale avec un élève.
     */
    public final static String PAIRING_STOP = "stopPairing";
    /**
     * commande pour ouvrir un document.
     */
    public final static String LAUNCH_FILE = "launchFile";
    /**
     * commande pour recevoir un fichier.
     */
    public final static String RECEIVE_FILE = "receiveFile";
    /**
     * commande pour afficher un message.
     */
    public final static String RECEIVE_MESSAGE = "receiveMessage";
    /**
     * commande pour éteindre l'ordinateur.
     */
    public final static String SHUTDOWN = "shutdown";
    /**
     * commande pour fermer le session sur l'ordinateur.
     */
    public final static String SHUTDOWN_SESSION = "shutdownSession";
    /**
     * commande pour réinitialiser le login de supervision.
     */
    public final static String RESET_LOGIN = "resetLogin";
    /**
     * commande pour exécuter un programme.
     */
    public final static String EXECUTE = "execute";
    /**
     * commande pour bloquer/débloquer le clavier et la souris.
     */
    public final static String BLOCK_KEYBOARD = "blockKeyboard";
    /**
     * commande pour bloquer/débloquer l'accès à Internet.
     */
    public final static String BLOCK_INTERNET = "blokInternet";
    /**
     * commande pour bloquer/débloquer l'accès à une application.
     */
    public final static String RECEIVE_INTREDICTION = "receiveInterdiction";
    /**
     * commande pour effacer le dossier utilisateur et les documents distribués.
     */
    public final static String DELETE_DOCUMENT = "deleteDocument";
    /**
     * commande pour informer la fin de la supervision.
     */
    public final static String MASTER_CLOSED = "masterClosed";
    /**
     * commande de notification d'erreur.
     */
    public final static String END_ERROR = "endError";
    /**
     * commande pour fermer (utiliser pour la mosaique).
     */
    public static final String CLOSE = "close";

    /**
     * paramètre général.
     */
    public final static String PARAMETER = "parameter";
    /**
     * paramètre pour le nom (login).
     */
    public final static String NAME = "name";
    /**
     * paramètre pour le niveau de la batterie.
     */
    public final static String BATTERY = "battery";
    /**
     * paramètre pour l'adresse IP.
     */
    public final static String IP_ADDRESS = "ipAddress";
    /**
     * paramètre pour le mot de passe.
     */
    public final static String PASSWORD = "password";
    /**
     * paramètre pour la validation login - mot de passe.
     */
    public final static String PASSWORD_CHECKED = "passwordChecked";
    /**
     * paramètre pour le numero de port général.
     */
    public final static String PORT = "port";
    /**
     * paramètre pour le numero de port du transfert d'écran.
     */
    public final static String SCREEN_PORT = "screenPort";
    /**
     * paramètre pour le numero de port du transfert audio.
     */
    public final static String AUDIO_PORT = "audioPort";
    /**
     * paramètre pour l'adresse IP d'un destinataire.
     */
    public final static String CLIENT_IP_ADDRESS = "clientIP";
    /**
     * paramètre pour le partage du clavier et de la souris.
     */
    public final static String REMOTE_HANDLING = "remoteHandling";
    /**
     * paramètre pour le nombre de destinataires.
     */
    public final static String CLIENT_NUMBER = "clientNumber";
    /**
     * paramètre pour le nombre de frame par seconde maximal.
     */
    public final static String FPS = "fps";
    /**
     * paramètre pour la qualité de compression des images pour le transfert
     * d'écran.
     */
    public final static String QUALITY = "quality";
    /**
     * paramètre pour le nombre de lignes pour le transfert d'écran.
     */
    public final static String LINES = "lines";
    /**
     * paramètre pour le temps d'attende de connexions pour le transfert
     * d'écran.
     */
    public final static String TIMEOUT = "timeout";
    /**
     * paramètre pour le nom du fichier.
     */
    public final static String FILE = "file";
    /**
     * paramètre pour la taille du fichier.
     */
    public final static String SIZE = "size";
    /**
     * paramètre pour la valeur du message.
     */
    public final static String MESSAGE = "message";
    /**
     * paramètre pour la valeur de blocage/déblocage.
     */
    public final static String BLOCK = "block";
    /**
     * paramètre pour une liste.
     */
    public final static String LIST = "list";
    /**
     * paramètre pour le nom d'une application.
     */
    public final static String APPLICATION = "application";

    /**
     * Find de communication.
     */
    public final static String END = "0";
    /**
     * Change la langue sur le poste élève. paramètre attendu : String pour
     * Locale
     */
    public final static String LANGUAGE = "2";
    /**
     * Gèle des commandes du poste élève. paramètre attendu : boolean
     */
    public final static String FREEZE = "3";
    /**
     * Charge un fichier dans le module multimédia. paramètre attendu : String
     */
    public final static String MEDIA_LOAD = "10";
    /**
     * Déchargement du fichier multimédia.
     */
    public final static String MEDIA_UNLOAD = "11";
    /**
     * Change le volume du média. paramètre attendu : int de 0 à 100
     */
    public final static String MEDIA_VOLUME = "12";
    /**
     * Change le mode plein écran. paramètre attendu : boolean
     */
    public final static String MEDIA_FULL_SCREEN = "13";
    /**
     * Charge un fichier d'index multimédia. paramètre attendu : String
     */
    public final static String MEDIA_LOAD_INDEXES = "14";
    /**
     * Charge un fichier de soustitres. paramètre attendu : String
     */
    public final static String MEDIA_LOAD_SUBTITLE = "15";
    /**
     * Charge un fichier dans le module audio. paramètre attendu : String
     */
    public final static String AUDIO_LOAD = "20";
    /**
     * Sauvegarde d'un fichier audio. paramètre attendu : String
     */
    public final static String AUDIO_SAVE = "21";
    /**
     * Efface les données du module audio.
     */
    public final static String AUDIO_ERASE = "22";
    /**
     * Déclenche la lecture du module audio. paramètre attendu : long
     */
    public final static String AUDIO_PLAY = "23";
    /**
     * Déclenche l'enregistrement du module audio. paramètre attendu : long
     */
    public final static String AUDIO_RECORD = "24";
    /**
     * Met en pause le module audio.
     */
    public final static String AUDIO_PAUSE = "25";
    /**
     * Change le volume du média paramètre. attendu : int de 0 à 100
     */
    public final static String AUDIO_VOLUME = "26";
    /**
     * Récupère un fichier audio.
     */
    public final static String AUDIO_GET_FILE = "27";
    /**
     * Charge un fichier texte. paramètre attendu : String
     */
    public final static String TEXT_LOAD = "30";
    /**
     * Sauvegarde d'un fichier texte. paramètre attendu : String
     */
    public final static String TEXT_SAVE = "31";
    /**
     * Efface les données du module texte.
     */
    public final static String TEXT_ERASE = "32";
    /**
     * Change le temps d'attente entre les mots. paramètre attendu : long
     */
    public final static String TEXT_WAIT = "33";
    /**
     * Récupère un fichier texte.
     */
    public final static String TEXT_GET_FILE = "35";
    /**
     * Change le temps maximum d'enregistrement.
     */
    public final static String TIME_MAX = "40";
    /**
     * Déplace le curseur du temps. paramètre attendu : long
     */
    public final static String TIME_MOVE = "41";
    /**
     * Retour à zéro.
     */
    public final static String TIME_TO_ZERO = "42";
    /**
     * Récupère un fichier.
     */
    public final static String FILE_GET = "50";
    /**
     * Envoie un fichier.
     */
    public final static String FILE_SEND = "51";
    /**
     * Envoi d'un message. paramètre attendu : String
     */
    public final static String SEND_MESSAGE = "65";

    /**
     * Identifiant pour le type de commande.
     */
    private String type;
    /**
     * Identifiant de la commande.
     */
    private String action;
    /**
     * Liste de paramètres de la commande.
     */
    private Map<String, String> parameters;

    /**
     * Initialistion du type de la commande et l'action de la commande.
     *
     * @param type le type de la commande (soit <code>TYPE_ACTION</code>,
     * <code>TYPE_SUPERVISION</code> ou <code>TYPE_LABO</code>.
     * @param action le nom de la commande.
     */
    public Command(String type, String action) {
        this.type = type;
        this.action = action;
        this.parameters = new HashMap<>(8);
    }

    /**
     * Retourne le type de commande.
     *
     * @return le type de la commande.
     */
    public String getType() {
        return type;
    }

    /**
     * Retourne le nom de la commande.
     *
     * @return le nom de la commande.
     */
    public String getAction() {
        return action;
    }

    /**
     * Modifie le nom de la commande.
     *
     * @param action le nom de la commande.
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Retourne les identifiants des paramètres.
     *
     * @return les identifiants des paramètres.
     */
    public Set<String> getParameters() {
        return parameters.keySet();
    }

    /**
     * Retourne la valeur du paramètre correspondand à l'identifiant.
     *
     * @param key l'identifiant du paramètre.
     * @return la valeur du paramètre.
     */
    public String getParameter(String key) {
        return parameters.get(key);
    }

    /**
     * Retourne la valeur du paramètre correspondand à l'identifiant en parsant
     * la valeur en Integer.
     *
     * @param key l'identifiant du paramètre.
     * @return la valeur du paramètre (valeur par défaut <code>-1</code>.
     */
    public int getParameterAsInt(String key) {
        String value = parameters.get(key);
        return Utilities.parseStringAsInt(value);
    }

    /**
     * Retourne la valeur du paramètre correspondand à l'identifiant en parsant
     * la valeur en Long.
     *
     * @param key l'identifiant du paramètre.
     * @return la valeur du paramètre (valeur par défaut <code>-1</code>.
     */
    public long getParameterAsLong(String key) {
        String value = parameters.get(key);
        return Utilities.parseStringAsLong(value);
    }

    /**
     * Retourne la valeur du paramètre correspondand à l'identifiant en parsant
     * la valeur en Double.
     *
     * @param key l'identifiant du paramètre.
     * @return la valeur du paramètre (valeur par défaut <code>-1</code>.
     */
    public double getParameterAsDouble(String key) {
        String value = parameters.get(key);
        return Utilities.parseStringAsDouble(value);
    }

    /**
     * Retourne la valeur du paramètre correspondand à l'identifiant en parsant
     * la valeur en Boolean.
     *
     * @param key l'identifiant du paramètre.
     * @return la valeur du paramètre (valeur par défaut <code>false</code>.
     */
    public boolean getParameterAsBoolean(String key) {
        String value = parameters.get(key);
        return Utilities.parseStringAsBoolean(value);
    }

    /**
     * Ajoute ou modifie un paramètre.
     *
     * @param key l'identifiant du paramètre.
     * @param value la valeur du paramètre.
     */
    public void putParameter(String key, Object value) {
        if (key != null && value != null) {
            parameters.put(key, value.toString());
        }
    }

    /**
     * Indique si la valeur du paramètre doit être protégé dans un descriptif
     * xml.
     *
     * @param key l'identifiant du paramètre.
     * @return le besoin de protection.
     */
    public static boolean protectionNeeded(String key) {
        if (key.contentEquals(FILE) || key.contentEquals(MESSAGE)) {
            return true;
        } else {
            return false;
        }
    }
}
