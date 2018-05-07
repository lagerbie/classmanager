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
package supervision.application;

/**
 * Représentation d'une application.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Application {

    /**
     * Référence pour les ids unique.
     */
    private static long idNumber = 0;
    /**
     * id unique de l'index.
     */
    private transient long id;
    /**
     * Nom pour une application inconnue.
     */
    public static final String UNKNOWN = "unknown";
    /**
     * Nom de l'application.
     */
    private String name;
    /**
     * Chemin de l'exécutable de l'application.
     */
    private String path;
    /**
     * Etat du statut autorisé.
     */
    private transient boolean allowed;

    /**
     * Initilise une application.
     *
     * @param name le nom de l'application.
     * @param path le chemin de l'exécutable de l'application.
     */
    public Application(String name, String path) {
        idNumber++;
        this.id = idNumber;
        this.name = name;
        this.path = path;
        this.allowed = true;
    }

    /**
     * Retourne l'id unique de l'application.
     *
     * @return l'id.
     */
    public long getId() {
        return id;
    }

    /**
     * Retourne le nom de l'application.
     *
     * @return le nom de l'application.
     */
    public String getName() {
        return name;
    }

    /**
     * Modifi le nom de l'application.
     *
     * @param name le nom de l'application.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retourne le chemin de l'exécutable de l'application.
     *
     * @return le chemin de l'exécutable de l'application.
     */
    public String getPath() {
        return path;
    }

    /**
     * Modifie le chemin de l'exécutable de l'application.
     *
     * @param path le chemin de l'exécutable de l'application.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Retourne si l'application est autorisée.
     *
     * @return l'autorisation de l'application.
     */
    public boolean isAllowed() {
        return allowed;
    }

    /**
     * Modifie l'autorisation de l'application.
     *
     * @param allowed l'autorisation de l'application.
     */
    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }
}
