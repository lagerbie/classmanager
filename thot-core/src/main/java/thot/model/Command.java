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
package thot.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import thot.utils.Utilities;

/**
 * Cette classe représente les différentes commandes échangées entre le poste professeur et les postes élèves.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
@Data
public class Command {

    /**
     * Identifiant pour le type de commande.
     */
    private CommandType type;
    /**
     * Identifiant de la commande.
     */
    private CommandAction action;
    /**
     * Liste de paramètres de la commande.
     */
    private Map<CommandParamater, String> parameters;

    /**
     * Initialistion du type de la commande et l'action de la commande.
     *
     * @param type le type de la commande (soit <code>TYPE_ACTION</code>,
     *         <code>TYPE_SUPERVISION</code> ou <code>TYPE_LABO</code>.
     * @param action le nom de la commande.
     */
    public Command(CommandType type, CommandAction action) {
        this.type = type;
        this.action = action;
        this.parameters = new HashMap<>(8);
    }

    /**
     * Retourne les identifiants des paramètres.
     *
     * @return les identifiants des paramètres.
     */
    public Set<CommandParamater> getParameters() {
        return parameters.keySet();
    }

    /**
     * Retourne la valeur du paramètre correspondand à l'identifiant.
     *
     * @param key l'identifiant du paramètre.
     *
     * @return la valeur du paramètre.
     */
    public String getParameter(CommandParamater key) {
        return parameters.get(key);
    }

    /**
     * Retourne la valeur du paramètre correspondand à l'identifiant en parsant la valeur en Integer.
     *
     * @param key l'identifiant du paramètre.
     *
     * @return la valeur du paramètre (valeur par défaut <code>-1</code>.
     */
    public int getParameterAsInt(CommandParamater key) {
        String value = parameters.get(key);
        return Utilities.parseStringAsInt(value);
    }

    /**
     * Retourne la valeur du paramètre correspondand à l'identifiant en parsant la valeur en Long.
     *
     * @param key l'identifiant du paramètre.
     *
     * @return la valeur du paramètre (valeur par défaut <code>-1</code>.
     */
    public long getParameterAsLong(CommandParamater key) {
        String value = parameters.get(key);
        return Utilities.parseStringAsLong(value);
    }

    /**
     * Retourne la valeur du paramètre correspondand à l'identifiant en parsant la valeur en Double.
     *
     * @param key l'identifiant du paramètre.
     *
     * @return la valeur du paramètre (valeur par défaut <code>-1</code>.
     */
    public double getParameterAsDouble(CommandParamater key) {
        String value = parameters.get(key);
        return Utilities.parseStringAsDouble(value);
    }

    /**
     * Retourne la valeur du paramètre correspondand à l'identifiant en parsant la valeur en Boolean.
     *
     * @param key l'identifiant du paramètre.
     *
     * @return la valeur du paramètre (valeur par défaut <code>false</code>.
     */
    public boolean getParameterAsBoolean(CommandParamater key) {
        String value = parameters.get(key);
        return Utilities.parseStringAsBoolean(value);
    }

    /**
     * Ajoute ou modifie un paramètre.
     *
     * @param key l'identifiant du paramètre.
     * @param value la valeur du paramètre.
     */
    public void putParameter(CommandParamater key, Object value) {
        if (key != null && value != null) {
            parameters.put(key, value.toString());
        }
    }

    /**
     * Indique si la valeur du paramètre doit être protégé dans un descriptif xml.
     *
     * @param key l'identifiant du paramètre.
     *
     * @return le besoin de protection.
     */
    public static boolean protectionNeeded(CommandParamater key) {
        return key == CommandParamater.FILE || key == CommandParamater.MESSAGE;
    }
}
