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
package supervision;

/**
 * Les différents ports utilisés par l'application.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public interface SupervisionPort {

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
//    /** Port de demande d'aide */
//    int helpDemandPort = 7204;
    /**
     * Port du serveur microphone.
     */
    int microphoneServerPort = 7205;
    /**
     * Port de communication labo pour les commandes du professeur.
     */
    int masterToStudentLaboPort = 7206;
    /**
     * Port de communication labo pour les commandes de l'élève.
     */
    int studentToMasterLaboPort = 7207;
    /**
     * Port de communication labo -> supervision.
     */
    int laboToSupervisionPort = 7208;
    /**
     * Port de communication supervision -> labo.
     */
    int supervisionToLaboPort = 7209;
    /**
     * Port pour la communication thumb -> mosaïque.
     */
    int thumbToMosaiquePort = 7210;
    /**
     * Port de base pour l'envoi d'écran.
     */
    int screenRemotePort = 7215;
    /**
     * Port pour les évènements de control clavier / souris.
     */
    int keyboardAndMousePort = 7216;
    /**
     * Port de base pour le transfert de fichiers.
     */
    int fileTransfertPort = 7217;
    /**
     * Port pour le lancement d'application.
     */
    int linuxConverterPort = 7218;
    /**
     * Port d'envoi audio principal.
     */
    int audioPort = 7220;
    /**
     * Port d'envoi audio secondaire.
     */
    int audioPairingPort = 7221;
//    /** Port d'envoi audio tertiaire */
//    int audioTriPort = 7222;
//    /** Port d'envoi audio qua */
//    int audioQuaPort = 7223;
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
