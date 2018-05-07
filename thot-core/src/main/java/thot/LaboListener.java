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

import java.awt.*;

import thot.model.Index;

/**
 * Listener pour écouter les changement d'état du coeur du laboratoire de langue.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public interface LaboListener extends ProgressPercentListener {

    /**
     * Appelé quand un état (play/pause/stop ou chargement media) a changé.
     *
     * @param running le nouvel état.
     * @param media le type de média chargé.
     */
    void stateChanged(int running, int media);

    /**
     * Appelé quand le mode de lecture automatique est changé.
     *
     * @param indexesMode le nouveau mode.
     */
    void indexesModeChanged(boolean indexesMode);

    /**
     * Appelé quand le temps d'enregistrement maximun a changé.
     *
     * @param recordTimeMax le nouveau temps maximum.
     */
    void recordTimeMaxChanged(long recordTimeMax);

    /**
     * Appelé quand le temps a changé.
     *
     * @param time le nouveau temps en millisecondes.
     */
    void timeChanged(long time);

    /**
     * Appelé lorsqu'un texte a été chargé.
     *
     * @param text le texte chargé.
     */
    void textLoaded(String text);

    /**
     * Appelé quand le mode plein écran a changé.
     *
     * @param fullscreen le vnouvel état.
     */
    void fullScreenChanged(boolean fullscreen);

    /**
     * Appelé quand un index a changé.
     */
    void indexesChanged();

    /**
     * Appelé quand l'index courant à changé.
     *
     * @param index le nouveau index.
     */
    void currentIndexChanged(Index index);

    /**
     * Appelé quand une image est charger ou décharger.
     *
     * @param image la nouvelle image.
     */
    void imageChanged(Image image);
}
