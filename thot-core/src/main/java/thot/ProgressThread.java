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

import javax.swing.event.EventListenerList;

/**
 * Gestion de listeners.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public abstract class ProgressThread {

    /**
     * Liste d'écouteur pour répercuter les évènements du convertisseur.
     */
    private final EventListenerList listeners;

    /**
     * Initialisation.
     */
    public ProgressThread() {
        listeners = new EventListenerList();
    }

    /**
     * Ajoute d'une écoute de type ProgessListener.
     *
     * @param listener l'écoute à ajouter.
     */
    public void addListener(ProgressListener listener) {
        listeners.add(ProgressListener.class, listener);
    }

    /**
     * Notification du début du traitement.
     *
     * @param determinated le status déterminé du process.
     */
    protected void fireProcessBegin(boolean determinated) {
        for (ProgressListener listener : listeners.getListeners(ProgressListener.class)) {
            listener.processBegin(this, determinated);
        }
    }

    /**
     * Notification de fin du traitement.
     *
     * @param exit la façon dont il est sorti.
     */
    protected void fireProcessEnded(int exit) {
        for (ProgressListener listener : listeners.getListeners(ProgressListener.class)) {
            listener.processEnded(this, exit);
        }
    }
}
