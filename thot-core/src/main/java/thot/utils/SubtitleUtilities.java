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

import java.util.Iterator;

import thot.model.Index;
import thot.model.Indexes;

/**
 * Utilitaires pour la conversion des soustitres.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class SubtitleUtilities {

    /**
     * Crée les soustitres au format SubRip.
     *
     * @param indexes les index.
     *
     * @return les soustitres au format SubRip.
     */
    public static String createSRTSubtitle(Indexes indexes) {
        indexes.sortIndexes();
        StringBuilder buffer = new StringBuilder(1024);

        //2 soustitres vides pour VLC
        buffer.append("00:00:00,000 --> 00:00:00,001\r\n\r\n");
        buffer.append("00:00:00,001 --> 00:00:00,002\r\n\r\n");

        int cnt = 0;
        for (Iterator<Index> it = indexes.iterator(); it.hasNext(); ) {
            Index index = it.next();

            if (index.getSubtitle() == null) {
                continue;
            }

            cnt++;
            buffer.append(cnt);
            buffer.append("\r\n");
            buffer.append(getSRTTime(index.getInitialTime()));
            buffer.append(" --> ");
            buffer.append(getSRTTime(index.getFinalTime()));
            buffer.append("\r\n");
            buffer.append(index.getSubtitle());
            buffer.append("\r\n");
            buffer.append("\r\n");
        }
        return buffer.toString();
    }

    /**
     * Retourne le temps formaté au format SubRip.
     *
     * @param time le temps en millisecondes.
     *
     * @return le temps formaté au format SubRip.
     */
    private static String getSRTTime(long time) {
        return String.format("%2$02d:%1$tM:%1$tS,%1$tL", time, time / 3600000);
    }

    /**
     * Crée les soustitres au format SubViewer.
     *
     * @param indexes les index.
     *
     * @return les soustitres au format SubViewer.
     */
    public static String createSUBSubtitle(Indexes indexes) {
        indexes.sortIndexes();
        StringBuilder buffer = new StringBuilder(1024);

        //Entête
        buffer.append("[INFORMATION]\r\n");
        buffer.append("[TITLE]\r\n");
        buffer.append("[AUTHOR]\r\n");
        buffer.append("[SOURCE]\r\n");
        buffer.append("[FILEPATH]\r\n");
        buffer.append("[DELAY]\r\n");
        buffer.append("[COMMENT]\r\n");
        buffer.append("[END INFORMATION]\r\n");

        buffer.append("[SUBTITLE]\r\n");
        buffer.append("[COLF]&HFFFFFF,[SIZE]12,[FONT]Times New Roman\r\n");

        //2 soustitres vides pour VLC
        buffer.append("00:00:00.00,00:00:00.01\r\n\r\n");
        buffer.append("00:00:00.01,00:00:00.02\r\n\r\n");

        for (Iterator<Index> it = indexes.iterator(); it.hasNext(); ) {
            Index index = it.next();

            if (index.getSubtitle() == null) {
                continue;
            }

            buffer.append(getSUBTime(index.getInitialTime()));
            buffer.append(",");
            buffer.append(getSUBTime(index.getFinalTime()));
            buffer.append("\r\n");
            buffer.append(index.getSubtitle());
            buffer.append("\r\n");
            buffer.append("\r\n");
        }
        return buffer.toString();
    }

    /**
     * Retourne le temps formaté au format SubViewer.
     *
     * @param time le temps en millisecondes.
     *
     * @return le temps formaté au format SubViewer.
     */
    private static String getSUBTime(long time) {
        return String.format("%2$02d:%1$tM:%1$tS.%3$02d", time, time / 3600000, (time % 1000) / 10);
    }

    /**
     * Crée les soustitres au format SubRip.
     *
     * @param indexes les index.
     *
     * @return les soustitres au format SubRip.
     */
    public static String createLRCSubtitle(Indexes indexes) {
        indexes.sortIndexes();
        StringBuilder buffer = new StringBuilder(1024);

        for (Iterator<Index> it = indexes.iterator(); it.hasNext(); ) {
            Index index = it.next();

            if (index.getSubtitle() == null) {
                continue;
            }

            buffer.append("[");
            buffer.append(getLRCTime(index.getInitialTime()));
            buffer.append("]");
            buffer.append(index.getSubtitle());
            buffer.append("\r\n");

//            buffer.append("[");
//            buffer.append(getLRCTime(index.getFinalTime()));
//            buffer.append("]");
//            buffer.append("\r\n");

        }
        return buffer.toString();
    }

    /**
     * Retourne le temps formaté au format LRC.
     *
     * @param time le temps en millisecondes.
     *
     * @return le temps formaté au format LRC.
     */
    private static String getLRCTime(long time) {
        return String.format("%1$02d:%2$02d.%3$02d", time / 60000, (time % 60000) / 1000, (time % 1000) / 10);
    }
}
