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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Gestionaire de liste d'applications.
 *
 * @author Fabrice Alleau
 * @version 1.82
 */
public class ApplicationsList {

    /**
     * Liste des applications.
     */
    private List<Application> applications;

    /**
     * Initialisation.
     */
    public ApplicationsList() {
        applications = new ArrayList<>(32);
    }

    /**
     * Retourne le nombre d'application.
     *
     * @return le nombre d'index.
     */
    public int getApplicationsCount() {
        return applications.size();
    }

    /**
     * Ajoute une application.
     *
     * @param application l'application à ajouter.
     * @return <code>true</true> si l'application a été ajoutée.
     */
    public boolean addApplication(Application application) {
        return applications.add(application);
    }

    /**
     * Enleve l'application.
     *
     * @param application l'application à enlever.
     * @return si l'application a été supprimée.
     */
    public boolean removeApplication(Application application) {
        return applications.remove(application);
    }

    /**
     * Réinitialise la liste des applications.
     */
    public void removeAll() {
        applications.clear();
    }

    /**
     * Retourne l'application avec l'id.
     *
     * @param id l'id de l'application.
     * @return l'application.
     */
    public Application getApplicationWithId(long id) {
        for (Application application : applications) {
            if (id == application.getId()) {
                return application;
            }
        }
        return null;
    }

    /**
     * Retourne la première application dans l'ordre alphabétique.
     *
     * @return la première application.
     */
    private Application getFirstApplication() {
        if (applications.isEmpty()) {
            return null;
        }

        Application first = applications.get(0);
        String name = applications.get(0).getName();
        for (Application application : applications) {
            if (application.getName().compareToIgnoreCase(name) < 0) {
                first = application;
                name = application.getName();
            }
        }

        return first;
    }

    /**
     * Trie par ordre croissant les applications.
     */
    public void sortApplications() {
        List<Application> newApplications = new ArrayList<>(applications.size());
        while (!applications.isEmpty()) {
            Application application = getFirstApplication();
            newApplications.add(application);
            applications.remove(application);
        }
        applications = newApplications;
    }

    /**
     * Retourne un iterateur sur les applications.
     *
     * @return un iterateur sur les applications.
     */
    public Iterator<Application> iterator() {
        return applications.iterator();
    }

    /**
     * Retourne l'application avec le nom donné.
     *
     * @param name le nom de l'application.
     * @return l'application avec le nom donné ou <code>null</code>.
     */
    private Application getApplicationByName(String name) {
        for (Application application : applications) {
            if (application.getName().contentEquals(name)) {
                return application;
            }
        }
        return null;
    }

//    /**
//     * 
//     * @param list
//     * @return 
//     */
//    public boolean isIdentical(ApplicationsList list) {
//        if(getApplicationsCount() != list.getApplicationsCount())
//            return false;
//        
//        for(Application application : applications) {
//            Application oldApplication = list.getApplicationByName(application.getName());
//            if(oldApplication == null)
//                return false;
//            else if(application.getPath() == null && oldApplication.getPath() != null)
//                return false;
//            else if(application.getPath() != null
//                    && application.getPath().equalsIgnoreCase(oldApplication.getPath()))
//                return false;
//        }
//        
//        return true;
//    }
}
