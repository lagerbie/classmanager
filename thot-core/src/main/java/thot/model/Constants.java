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

/**
 * Constantes.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public interface Constants {

    /**
     * IP multicast par défaut.
     */
    public static final String DEFAULT_MULTICAST_IP = "228.5.6.7";
    /**
     * Chaine pour la découverte du professeur.
     */
    public static final String XML_STUDENT_SEARCH
            = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><connection><searchStudent /></connection>";

    /**
     * Nom du logiciel.
     */
    public static final String softName = "ClassManager";
    /**
     * Nom du logiciel sans espace pour les chemins de fichiers.
     */
    public static final String softNamePath = softName.replace(" ", "");
    /**
     * Nom générique pour les dossiers de programmes de Windows.
     */
    public static final String PROGAM_FILES = "%ProgramFiles%";

    /**
     * Identifaint de groupe A.
     */
    public static final int GROUP_A = 0;
    /**
     * Identifaint de groupe B.
     */
    public static final int GROUP_B = 1;
    /**
     * Identifaint de groupe C.
     */
    public static final int GROUP_C = 2;
    /**
     * Identifaint de groupe D.
     */
    public static final int GROUP_D = 3;
    /**
     * Identifaint de groupe E.
     */
    public static final int GROUP_E = 4;
    /**
     * Identifaint de groupe F.
     */
    public static final int GROUP_F = 5;
    /**
     * Identifaint de groupe G.
     */
    public static final int GROUP_G = 6;
    /**
     * Identifaint de groupe H.
     */
    public static final int GROUP_H = 7;

    /**
     * Evènement pour le déplacement de la souris.
     */
    public static final int MOUSE_MOVED = 40;
    /**
     * Evènement pour l'appui d'un bouton de la souris.
     */
    public static final int MOUSE_PRESSED = 41;
    /**
     * Evènement pour le relachement d'un bouton de la souris.
     */
    public static final int MOUSE_RELEASED = 42;
    /**
     * Evènement pour l'appui d'une touche du clavier.
     */
    public static final int KEY_PRESSED = 43;
    /**
     * Evènement pour le relachement d'une touche du clavier.
     */
    public static final int KEY_RELEASED = 44;
    /**
     * Evènement d'arrêt.
     */
    public static final int CLOSE = 51;

    /**
     * Temps d'attente maximum pour l'envoi des ordres (=2s).
     */
    public static final int TIME_MAX_FOR_ORDER = 2000;
    /**
     * Temps d'attente maximum pour l'attente d'une connection (=10s).
     */
    public static final int TIME_MAX_FOR_CONNEXION = 10000;
    /**
     * Temps d'attente maximum pour l'attente pour un chargement (=1min).
     */
    public static final int TIME_MAX_FOR_LOAD = 60000;

    /**
     * Module en pause.
     */
    public static final int PAUSE = 0;
    /**
     * Module en lecture.
     */
    public static final int PLAYING = 1;
    /**
     * Module en enregistrement.
     */
    public static final int RECORDING = 2;
    /**
     * Module en insertion de voix.
     */
    public static final int RECORDING_INSERT = 3;

    /**
     * Fichier multimédia non chagé.
     */
    public static final int UNLOAD = 0;
    /**
     * Indentifiant pour un fichier audio.
     */
    public static final int AUDIO_FILE = 1;
    /**
     * Indentifiant pour un fichier video.
     */
    public static final int VIDEO_FILE = 2;
    /**
     * Indentifiant pour un fichier image.
     */
    public static final int IMAGE_FILE = 3;

    /**
     * Nom de l'extension d'un projet.
     */
    public static final String projectExtension = ".ees";
    /**
     * Nom de l'extension d'un projet interne.
     */
    public static final String projectInternExtension = ".project";
    /**
     * Nom de l'extension d'un fichier d'index.
     */
    public static final String indexesExtension = ".index";
    /**
     * Nom de l'extension par le format wav.
     */
    public static final String WAV_extension = ".wav";
    /**
     * Nom de l'extension par défaut pour l'enregistrement du fichier audio.
     */
    public static final String audioDefaultExtension = ".mp3";
    /**
     * Nom de l'extension par le format avi.
     */
    public static final String AVI_extension = ".avi";
    /**
     * Nom de l'extension par défaut pour l'enregistrement du fichier audio.
     */
    public static final String videoDefaultExtension = ".mp4";
    /**
     * Nom de l'extension d'un fichier texte brut.
     */
    public static final String TXT_extension = ".txt";
    /**
     * Nom de l'extension d'un fichier texte au format RTF.
     */
    public static final String RTF_extension = ".rtf";
    /**
     * Nom de l'extension d'un fichier texte au format HTML.
     */
    public static final String HTML_extension = ".html";
    /**
     * Nom de l'extension par défaut pour l'enregistrement de fichiers texte.
     */
    public static final String textDefaultExtension = HTML_extension;
    /**
     * Nom de l'extension d'un fichier de sous-titres au format SubRip.
     */
    public static final String SRT_extension = ".srt";
    /**
     * Nom de l'extension d'un fichier de sous-titres au format SubWiever.
     */
    public static final String SUB_extension = ".sub";
    /**
     * Nom de l'extension d'un fichier de sous-titres au format LyRiCs.
     */
    public static final String LRC_extension = ".lrc";
    /**
     * Extensions possibles pour les fichiers texte.
     */
    public static final String[] subtitleExtension = {
        SRT_extension, SUB_extension, LRC_extension
    };
    /**
     * Extensions possibles pour les fichiers texte.
     */
    public static final String[] textExtension = {
        TXT_extension,
        RTF_extension, HTML_extension, ".htm",
        SRT_extension, SUB_extension, LRC_extension
    };
    /**
     * Extensions possibles pour les fichiers texte avec gestion de styles.
     */
    public static final String[] textStyledExtension = {
        RTF_extension, HTML_extension, ".htm"
    };
    /**
     * Extensions possibles pour les fichiers audio.
     */
    public static final String[] audioExtension = {
        "wav", "mp3", "flac", "ogg", "wma"
    };
    /**
     * Extensions possibles pour les fichiers video.
     */
    public static final String[] videoExtension = {
        "avi", "mpg", "mpeg", "mp4", "wmv", "flv", "mkv"
    };
    /**
     * Extensions possibles pour les fichiers image.
     */
    public static final String[] imageExtension = {
        "png", "jpg", "jpeg", "gif", "bmp"
    };
}
