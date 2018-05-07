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
package supervision;

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
    public static final String XML_STUDENT_SEARCH = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><connection><searchStudent /></connection>";

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
}
