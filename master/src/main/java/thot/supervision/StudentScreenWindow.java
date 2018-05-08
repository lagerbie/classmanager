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
package thot.supervision;

import thot.supervision.screen.ScreenWindow;

/**
 * Fenêtre pour afficher l'écran d'un élève.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class StudentScreenWindow extends ScreenWindow {
    private static final long serialVersionUID = 19000L;

    /**
     * Référence sur le gestionnaire des élèves.
     */
    private MasterCore core;

    /**
     * Initialisation.
     *
     * @param core référence sur le gestionnaire des élèves.
     * @param keyboardAndMousePort le port de communication pour le control du clavier et de la souris.
     */
    public StudentScreenWindow(MasterCore core, int keyboardAndMousePort) {
        super(true, keyboardAndMousePort);
        this.core = core;
    }

    @Override
    protected void closeCommand() {
        sendClose();
        core.closeRemoteScreen();
    }
}
