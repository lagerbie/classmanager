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

import thot.labo.LaboListener;

/**
 * Listener pour écouter les changement d'état du coeur du laboratoire de
 * langue.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public interface LaboratoryListener extends LaboListener {

    /**
     * Appelé quand la langue a été changée.
     *
     * @param language la nouvelle langue pour l'interface.
     */
    void languageChanged(String language);

    /**
     * Appelé quand le volume du module multimédia a changé.
     *
     * @param volume le nouveau volume en poucentage (de 0 à 100).
     */
    void mediaVolumeChanged(int volume);

    /**
     * Appelé quand le volume du module audio a changé.
     *
     * @param volume le nouveau volume en poucentage (de 0 à 100).
     */
    void audioVolumeChanged(int volume);

    /**
     * Appelé quand l'état des commandes de l'élève ont changé.
     *
     * @param freeze le nouvel état.
     */
    void studentControlChanged(boolean freeze);

    /**
     * Appelé pour afficher un message à l'écran.
     *
     * @param message le message à afficher.
     */
    void newMessage(String message);

    /**
     * Appelé quand une demande d'aide à été envoyée.
     *
     * @param success le succes de la commande.
     */
    void helpDemandSuccess(boolean success);
}
