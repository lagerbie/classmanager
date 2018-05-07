package thot.model;

import lombok.Getter;

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
