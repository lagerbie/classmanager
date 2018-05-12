package thot.supervision;

import java.util.HashMap;
import java.util.Map;

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
     * Identifiant pour le module de commande.
     */
    private CommandModule module;
    /**
     * Identifiant de la commande.
     */
    private CommandAction action;
    /**
     * Liste de paramètres de la commande.
     */
    private Map<CommandParamater, String> parameters = new HashMap<>(8);

    /**
     * Initialistion du module de la commande et l'action de la commande.
     *
     * @param module le module de la commande.
     * @param action l'action de la commande.
     */
    public Command(CommandModule module, CommandAction action) {
        this.module = module;
        this.action = action;
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

}
