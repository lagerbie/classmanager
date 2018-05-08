package thot.supervision;

import lombok.Getter;

/**
 * Liste des types possibles pour les commandes échangées entre le poste professeur et les postes élèves.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public enum CommandType {
    /**
     * Type pour des commandes de supervision.
     */
    TYPE_SUPERVISION("supervision"),
    /**
     * Type pour des commandes du laboratoire.
     */
    TYPE_LABORATORY("laboratory");

    @Getter
    private String type;

    CommandType(String type) {
        this.type = type;
    }
}
