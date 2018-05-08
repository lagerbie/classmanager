package thot.supervision;

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
    private Map<CommandParamater, String> parameters = new HashMap<>(8);

    /**
     * Initialistion du type de la commande et l'action de la commande.
     *
     * @param type le type de la commande.
     * @param action l'action de la commande.
     */
    public Command(CommandType type, CommandAction action) {
        this.type = type;
        this.action = action;
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
     * @return la valeur du paramètre (valeur par défaut {@code -1}.
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
     * @return la valeur du paramètre (valeur par défaut {@code -1}.
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
     * @return la valeur du paramètre (valeur par défaut {@code -1}.
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
     * @return la valeur du paramètre (valeur par défaut {@code false}.
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
