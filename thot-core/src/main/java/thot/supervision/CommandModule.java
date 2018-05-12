package thot.supervision;

import java.util.Arrays;

import lombok.Getter;

/**
 * Liste des types possibles pour les commandes échangées entre le poste professeur et les postes élèves.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public enum CommandModule {
    /**
     * Type pour des commandes de supervision.
     */
    SUPERVISION("supervision"),
    /**
     * Type pour des commandes du laboratoire.
     */
    LABORATORY("laboratory"),

    THUMB("thumb");

    @Getter
    private String name;

    CommandModule(String name) {
        this.name = name;
    }

    public static CommandModule getCommandModule(String name) {
        return Arrays.stream(CommandModule.values()).filter(module -> module.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
}
