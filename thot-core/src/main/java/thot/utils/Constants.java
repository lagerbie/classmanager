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
package thot.utils;

/**
 * Constantes.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface Constants {

    /**
     * IP multicast par défaut.
     */
    String DEFAULT_MULTICAST_IP = "228.5.6.7";
    /**
     * Chaine pour la découverte du professeur.
     */
    String XML_STUDENT_SEARCH = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><connection><searchStudent /></connection>";

    /**
     * Nom du logiciel.
     */
    String softName = "ClassManager";
    /**
     * Nom du logiciel sans espace pour les chemins de fichiers.
     */
    String softNamePath = softName.replace(" ", "");
    /**
     * Nom générique pour les dossiers de programmes de Windows.
     */
    String PROGAM_FILES = "%ProgramFiles%";

    /**
     * Identifaint de groupe A.
     */
    int GROUP_A = 0;
    /**
     * Identifaint de groupe B.
     */
    int GROUP_B = 1;
    /**
     * Identifaint de groupe C.
     */
    int GROUP_C = 2;
    /**
     * Identifaint de groupe D.
     */
    int GROUP_D = 3;
    /**
     * Identifaint de groupe E.
     */
    int GROUP_E = 4;
    /**
     * Identifaint de groupe F.
     */
    int GROUP_F = 5;
    /**
     * Identifaint de groupe G.
     */
    int GROUP_G = 6;
    /**
     * Identifaint de groupe H.
     */
    int GROUP_H = 7;

    /**
     * Evènement pour le déplacement de la souris.
     */
    int MOUSE_MOVED = 40;
    /**
     * Evènement pour l'appui d'un bouton de la souris.
     */
    int MOUSE_PRESSED = 41;
    /**
     * Evènement pour le relachement d'un bouton de la souris.
     */
    int MOUSE_RELEASED = 42;
    /**
     * Evènement pour l'appui d'une touche du clavier.
     */
    int KEY_PRESSED = 43;
    /**
     * Evènement pour le relachement d'une touche du clavier.
     */
    int KEY_RELEASED = 44;
    /**
     * Evènement d'arrêt.
     */
    int CLOSE = 51;

    /**
     * Temps d'attente maximum pour l'envoi des ordres (=2s).
     */
    int TIME_MAX_FOR_ORDER = 2000;
    /**
     * Temps d'attente maximum pour l'attente d'une connection (=10s).
     */
    int TIME_MAX_FOR_CONNEXION = 10000;
    /**
     * Temps d'attente maximum pour l'attente pour un chargement (=1min).
     */
    int TIME_MAX_FOR_LOAD = 60000;

    /**
     * Module en pause.
     */
    int PAUSE = 0;
    /**
     * Module en lecture.
     */
    int PLAYING = 1;
    /**
     * Module en enregistrement.
     */
    int RECORDING = 2;
    /**
     * Module en insertion de voix.
     */
    int RECORDING_INSERT = 3;

    /**
     * Fichier multimédia non chagé.
     */
    int UNLOAD = 0;
    /**
     * Indentifiant pour un fichier audio.
     */
    int AUDIO_FILE = 1;
    /**
     * Indentifiant pour un fichier video.
     */
    int VIDEO_FILE = 2;
    /**
     * Indentifiant pour un fichier image.
     */
    int IMAGE_FILE = 3;

    /**
     * Nom de l'extension d'un projet.
     */
    String projectExtension = ".ees";
    /**
     * Nom de l'extension d'un projet interne.
     */
    String projectInternExtension = ".project";
    /**
     * Nom de l'extension d'un fichier d'index.
     */
    String indexesExtension = ".index";
    /**
     * Nom de l'extension d'un fichier de tags
     */
    String tagExtension = ".tag";
    /**
     * Nom de l'extension par le format wav.
     */
    String WAV_extension = ".wav";
    /**
     * Nom de l'extension par défaut pour l'enregistrement du fichier audio.
     */
    String audioDefaultExtension = ".mp3";
    /**
     * Nom de l'extension par le format avi.
     */
    String AVI_extension = ".avi";
    /**
     * Nom de l'extension par le format flv
     */
    String FLV_extension = ".flv";
    /**
     * Nom de l'extension par défaut pour l'enregistrement du fichier audio.
     */
    String videoDefaultExtension = ".mp4";
    /**
     * Nom de l'extension d'un fichier texte brut.
     */
    String TXT_extension = ".txt";
    /**
     * Nom de l'extension d'un fichier texte au format RTF.
     */
    String RTF_extension = ".rtf";
    /**
     * Nom de l'extension d'un fichier texte au format HTML.
     */
    String HTML_extension = ".html";
    /**
     * Nom de l'extension par défaut pour l'enregistrement de fichiers texte.
     */
    String textDefaultExtension = HTML_extension;
    /**
     * Nom de l'extension d'un fichier de sous-titres au format SubRip.
     */
    String SRT_extension = ".srt";
    /**
     * Nom de l'extension d'un fichier de sous-titres au format SubWiever.
     */
    String SUB_extension = ".sub";
    /**
     * Nom de l'extension d'un fichier de sous-titres au format LyRiCs.
     */
    String LRC_extension = ".lrc";
    /**
     * Extensions possibles pour les fichiers texte.
     */
    String[] subtitleExtension = {
            SRT_extension, SUB_extension, LRC_extension
    };
    /**
     * Extensions possibles pour les fichiers texte.
     */
    String[] textExtension = {
            TXT_extension,
            RTF_extension, HTML_extension, ".htm",
            SRT_extension, SUB_extension, LRC_extension
    };
    /**
     * Extensions possibles pour les fichiers texte avec gestion de styles.
     */
    String[] textStyledExtension = {
            RTF_extension, HTML_extension, ".htm"
    };
    /**
     * Extensions possibles pour les fichiers audio.
     */
    String[] audioExtension = {
            "wav", "mp3", "flac", "ogg", "wma"
    };
    /**
     * Extensions possibles pour les fichiers video.
     */
    String[] videoExtension = {
            "avi", "mpg", "mpeg", "mp4", "wmv", "flv", "mkv"
    };
    /**
     * Extensions possibles pour les fichiers image.
     */
    String[] imageExtension = {
            "png", "jpg", "jpeg", "gif", "bmp"
    };
}
