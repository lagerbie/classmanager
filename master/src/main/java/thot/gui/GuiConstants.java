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
package thot.gui;

import java.util.ResourceBundle;

/**
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public interface GuiConstants {

    /**
     * Chemin interne des images.
     */
    public static final String imagesPath = "thot/gui/images/";
    /**
     * Réfrérences sur les images selon leur type.
     */
    public static final ResourceBundle images = ResourceBundle.getBundle("supervision.gui.images");

    /**
     * identifiant pour la notification d'aide.
     */
    public static final String callMessage = "callMessage";
    /**
     * identifiant pour la notification de réception de fichier.
     */
    public static final String receiveFile = "receiveFile";
    /**
     * identifiant pour modifier la langue.
     */
    public static final String language = "language";
    /**
     * identifiant pour modifier l'aide.
     */
    public static final String help = "help";
    /**
     * identifiant pour minimiser.
     */
    public static final String mini = "mini";
    /**
     * identifiant pour fermer.
     */
    public static final String close = "close";
    /**
     * identifiant pour la sélection d'un élève.
     */
    public static final String student = "student";

    //Boutons pour le suivi
    /**
     * identifiant pour l'écoute discrète.
     */
    public static final String listening = "listening";
    /**
     * identifiant pour la prise en main.
     */
    public static final String studentControl = "studentControl";
    /**
     * identifiant pour la scrutation automatique.
     */
    public static final String scanning = "scanning";
    /**
     * identifiant pour la mosaïque.
     */
    public static final String mosaique = "mosaique";

    //Boutons pour la diffusion
    /**
     * identifiant pour l'envoi de fichiers.
     */
    public static final String sendFile = "sendFile";
    /**
     * identifiant pour l'envoi d'un écran noir.
     */
    public static final String blackScreen = "blackScreen";
    /**
     * identifiant pour l'envoi de l'écran professeur.
     */
    public static final String masterScreen = "masterScreen";
    /**
     * identifiant pour l'envoi de l'écran professeur et de sa voix.
     */
    public static final String masterScreenVoice = "masterScreenVoice";
    /**
     * identifiant pour l'envoi de la voix du professeur.
     */
    public static final String masterVoice = "masterVoice";
    /**
     * identifiant pour l'envoi de l'écran d'un élève.
     */
    public static final String studentScreen = "studentScreen";
    /**
     * identifiant pour l'envoi d'un message texte.
     */
    public static final String sendMessage = "sendTextMessage";

    //Boutons pour le pairing
    /**
     * identifiant pour le pairing.
     */
    public static final String pairing = "pairing";
    /**
     * identifiant pour la validation du pairing.
     */
    public static final String pairingValid = "pairingValid";

    //Boutons pour la gestion
    /**
     * identifiant pour la création de groupes.
     */
    public static final String groupCreation = "groupCreation";
    /**
     * identifiant pour l'extinction sur les élèves.
     */
    public static final String studentClose = "studentClose";
    /**
     * identifiant pour la réinitialisation du login.
     */
    public static final String loginSession = "loginSession";
    /**
     * identifiant pour la fermeture de la session de l'ordinateur.
     */
    public static final String osSession = "osSession";
    /**
     * identifiant pour éteintre l'ordinateur.
     */
    public static final String computerPower = "computerPower";

    //Boutons pour les groupes
    /**
     * identifiant de base pour les groupes.
     */
    public static final String group = "group";
    /**
     * identifiant pour le groupe A.
     */
    public static final String groupA = group + "A";
    /**
     * identifiant pour le groupe B.
     */
    public static final String groupB = group + "B";
    /**
     * identifiant pour le groupe C.
     */
    public static final String groupC = group + "C";
    /**
     * identifiant pour le groupe D.
     */
    public static final String groupD = group + "D";
    /**
     * identifiant pour le groupe E.
     */
    public static final String groupE = group + "E";
    /**
     * identifiant pour le groupe F.
     */
    public static final String groupF = group + "F";
    /**
     * identifiant pour le groupe G.
     */
    public static final String groupG = group + "G";
    /**
     * identifiant pour le groupe H.
     */
    public static final String groupH = group + "H";
    /**
     * identifiant pour la flèche des groupes supplémentaires.
     */
    public static final String arrow = "arrow";
    /**
     * identifiant de base pour les groupes lors de la création.
     */
    public static final String creationGroup = "creationGroup";

    
    public static final String jclicReports = "mcJClicReports";
    public static final String jclic = "mcJClic";

    public static final String bloqueClavier = "mcBlocageClavier";
    public static final String bloqueInternet = "mcBlocageInternet";
    public static final String interdireApplication = "mcBlocageApplication";
    public static final String deleteDocument = "mcDeleteDocument";

    public static final String changeLanguage = "mcLanguage";
    public static final String freeze = "mcBlock";
    public static final String timeMax = "mcTimeMax";
    public static final String rapatriate = "mcRapatriate";
    public static final String rapatriateAudio = "Audio";
    public static final String rapatriateText = "Text";
    public static final String rapatriateAll = "All";
    public static final String message = "mcMessage";
    public static final String mediaLoad = "mcMediaLoad";
    public static final String mediaSend = "mcMediaSend";
    public static final String mediaUnload = "mcMediaErase";
    public static final String mediaVolume = "mcVolumeProf";
    public static final String mediaDiffuse = "mcDiffuse";
    public static final String fullScreen = "mcFullScreen";
    public static final String back = "mcBack";
    public static final String play = "mcPlay";
    public static final String stop = "mcPause";
    public static final String record = "mcRecord";
    public static final String time = "mcTime";
    public static final String audioSend = "mcAudioSend";
    public static final String audioErase = "mcAudioErase";
    public static final String audioSave = "mcAudioSave";
    public static final String audioVolume = "mcVolumeEleve";
    public static final String masterTextLoad = "mcTextLoad";
    public static final String masterTextSave = "mcMasterTextSave";
    public static final String masterTextErase = "mcMasterTextErase";
    public static final String textSave = "mcTextSave";
    public static final String textErase = "mcTextErase";
    public static final String textSpeed = "mcSpeed";
    public static final String textSend = "mcTextSend";
    public static final String lanceLabo = "mcLanceLabo";
    public static final String closeLabo = "mcCloseLabo";

    public static final String unknown = "unknown";
    public static final String playing = "playing";
    public static final String recording = "recording";
    public static final String pause = "pause";
    public static final String runningState = "runningState";
    public static final String unload = "unload";
    public static final String image = "image";
    public static final String audio = "audio";
    public static final String video = "video";
    public static final String mediaType = "mediaType";
    public static final String trackTimeMax = "timeMax";
    public static final String timePosition = "timePosition";
    public static final String text = "text";
    public static final String removeIndexes = "removeIndexes";
    public static final String secure = "secure";
}
