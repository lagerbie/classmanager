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
 * Représentation des données d'un élèves.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Student {

    /**
     * Adresses IP des élèves.
     */
    private String addressIP;
    /**
     * Login des élèves.
     */
    private String login;
    /**
     * Numéro du groupe d'appartenance.
     */
    private int group;
    /**
     * Couple pour le pairing.
     */
    private Student pairing;
    /**
     * Niveau des batteries en pourcentage.
     */
    private int batteryLevel;
    /**
     * Login validé.
     */
    private boolean validate;
    /**
     * Connexion des élèves.
     */
    private boolean onLine;
    /**
     * Nombres de fois que l'élève n'est pas connecté.
     */
    private boolean firstOffLine;

    /**
     * Initialise l'élève avec son adresse IP.
     *
     * @param addressIP l'adresse IP.
     * @param group le groupe affecté à l'élève.
     */
    public Student(String addressIP, int group) {
        this.addressIP = addressIP;
        this.group = group;
        this.pairing = null;
        this.batteryLevel = 100;
        this.onLine = false;
        this.firstOffLine = true;
        //on valide le login pour empêcher la demande d'un nouveau login lors
        //du redémarrage du professeur
        //TODO : on ne devrait pas faire ça ici
        this.validate = true;
    }

    /**
     * Retourne l'adresse IP.
     *
     * @return l'adresse IP.
     */
    public String getAddressIP() {
        return addressIP;
    }

    /**
     * Retourne le login utilisé par l'élève.
     *
     * @return le login de l'élève.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Retourne le nom de l'élève.
     * Si l'élève est identifié il renvoit le login, sinon sont adresse IP.
     *
     * @return le nom de l'élève.
     */
    public String getName() {
        if (login != null) {
            return login;
        } else {
            return addressIP;
        }
    }

    /**
     * Retourne le niveau de la batterie en pourcentage.
     *
     * @return le niveau de la batterie en pourcentage.
     */
    public int getBatteryLevel() {
        return batteryLevel;
    }

    /**
     * Retourne le numéro du groupe au quel il appartient.
     *
     * @return le numéro du groupe.
     */
    public int getGroup() {
        return group;
    }

    /**
     * Retourne l'élève associé lors du pairing.
     *
     * @return l'élève associé lors du pairing.
     */
    public Student getPairing() {
        return pairing;
    }

    /**
     * Retourne si l'élève est identifié.
     *
     * @return si l'élève est identifié.
     */
    public boolean isChecked() {
        return validate;
    }

    /**
     * Reourne si l'élève est considéré comme connecté.
     *
     * @return si l'élève est connecté.
     */
    public boolean isOnLine() {
        return onLine;
    }

    /**
     * Modifie le niveau de batterie.
     *
     * @param batteryLevel le nouveau niveau de batterie.
     */
    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    /**
     * Modifie le numéro de groupe.
     *
     * @param groupe le nouveau numéro de groupe.
     */
    public void setGroup(int groupe) {
        this.group = groupe;
    }

    /**
     * Modifie le login de l'élève.
     *
     * @param login le nouveau login de l'élève.
     */
    public void setLogin(String login) {
        this.login = login;
        this.validate = (login != null);
    }

    /**
     * Modifie l'élève associé lors du pairing.
     *
     * @param pairing l'élève associé lors du pairing.
     */
    public void setPairing(Student pairing) {
        this.pairing = pairing;
    }

    /**
     * Modifie le statut connecté. Gére la possibilté d'avoir un ping sans
     * réponse.
     *
     * @param onLine si le poste élève est présent.
     */
    public void setOnLine(boolean onLine) {
        if (onLine) {
            this.onLine = true;
            firstOffLine = true;
        } else {
            if (firstOffLine) {
                firstOffLine = false;
            } else {
                this.onLine = false;
            }
        }
    }
}
