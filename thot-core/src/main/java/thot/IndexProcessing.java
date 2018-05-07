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
package thot;

import java.util.Iterator;

import thot.model.Index;

/**
 * Interface pour la gestion des index.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public interface IndexProcessing {

    /**
     * Retourne l'état du module audio.
     * <p>
     * Il peut prendre les valeurs suivantes:
     *
     * <code>StudentCore.PAUSE</code>,
     *
     * <code>StudentCore.PLAYING</code>,
     *
     * <code>StudentCore.RECORDING</code>.
     *
     * @return l'état du module audio.
     */
    int getRunningState();

    long getRecordTimeMax();

    boolean onIndex(double position);

    boolean onStudentIndex(double position);

    Iterator<Index> recordIndexIterator();

    Iterator<Index> mediaIndexIterator();

    void playOnIndex(double position);

    void recordOnIndex(double position);

    void setTimeBeginIndex(double position);

    void setTimeEndIndex(double position);

    void eraseIndex(double position);

    /**
     * Efface l'enregistrement effectué sur l'index sélectionné.
     *
     * @param position la position relative entre 0 et 1.
     */
    void eraseIndexRecord(double position);
}
