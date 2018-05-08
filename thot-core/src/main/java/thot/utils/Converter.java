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

import java.io.File;

/**
 * Interface poue la conversion de fichiers.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface Converter {

    /**
     * Arrête le processus.
     */
    void cancel();

    /**
     * Ajoute d'une écoute de type ProgessListener.
     *
     * @param listener l'écoute à ajouter.
     */
    void addListener(ProgressPercentListener listener);

    /**
     * Enlève une écoute de type ProgessListener.
     *
     * @param listener l'écoute à enlever.
     */
    void removeListener(ProgressPercentListener listener);

//    /**
//     * Modifie le bitrate de l'audio.
//     *
//     * @param audioBitrate le birate en bit/s.
//     */
//    void setAudioBitrate(int audioBitrate);

    /**
     * Modifie le nombre de canaux audio.
     *
     * @param audioChannels le nombre de canaux audio.
     */
    void setAudioChannels(int audioChannels);

    /**
     * Modifie le taux d'échantillonage.
     *
     * @param audioRate la fréquence en Hz.
     */
    void setAudioRate(int audioRate);

    /**
     * Modifie la taille de la vidéo.
     *
     * @param width la largeur.
     * @param height la hauteur.
     */
    void setVideoSize(int width, int height);

    /**
     * Retourne la durée du fichier en ms.
     *
     * @param file le fichier.
     * @return la durée du fichier en ms.
     */
    long getDuration(File file);

    /**
     * Détermine si le fichier possède un flux audio.
     *
     * @param file le fichier.
     * @return si le fichier possède un flux audio.
     */
    boolean hasAudioSrteam(File file);

    /**
     * Détermine si le fichier possède un flux vidéo.
     *
     * @param file le fichier.
     * @return si le fichier possède un flux vidéo.
     */
    boolean hasVideoSrteam(File file);

    /**
     * Conversion de fichiers. La conversion est définie par le type du fichier
     * destination.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @return les messages de conversion.
     */
    String convert(File destFile, File srcFile);

    /**
     * Conversion de fichiers. La conversion est définie par le type du fichier
     * destination.
     *
     * @param srcFile le fichier à convertir.
     * @param destFile le fichier de destination.
     * @param audioRate la fréquence en Hz.
     * @param channels le nombre de canaux audio.
     * @return les messages de conversion.
     */
    String convert(File destFile, File srcFile, int audioRate, int channels);
}
