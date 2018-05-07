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

import java.util.EventListener;

/**
 * Listener pour la progression d'un traitement.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public interface ProgressListener extends EventListener {

    /**
     * Notifiction du démmarrage du processus.
     *
     * @param source la source de l'évènement.
     * @param determinated le mode déterminé (<code>true</code> si un pourcentage de progression peut être
     *         affiché.
     */
    void processBegin(Object source, boolean determinated);

    /**
     * Notification de l'arrêt du processus.
     *
     * @param source la source de l'évènement.
     * @param exit la valeur de sortie (par convention 0 équvaut à une sortie normale)
     */
    void processEnded(Object source, int exit);
}
