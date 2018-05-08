package thot.labo.index;

import java.util.Arrays;

import lombok.Getter;

/**
 * Types d'index possibles.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public enum IndexType {

    /**
     * Indentifiant inconu
     */
    UNKNOWN("unknown"),
    /**
     * Indentifiant pour un index de lecture
     */
    PLAY("play"),
    /**
     * Indentifiant pour un index d'enregistrement
     */
    RECORD("record"),
    /**
     * Indentifiant pour un index de blanc
     */
    BLANK("blank"),
    /**
     * Indentifiant pour un index de blanc avec beep
     */
    BLANK_BEEP("blankWithBeep"),
    /**
     * Indentifiant pour un index de répétition
     */
    REPEAT("repeat"),
    /**
     * Indentifiant pour un index de la voix du professeur
     */
    VOICE("voice"),
    /**
     * Indentifiant pour un index d'incertion de fichier
     */
    FILE("file"),
    /**
     * Indentifiant pour un index de sélection
     */
    SELECTION("selection");

    @Getter
    private String name;

    IndexType(String name) {
        this.name = name;
    }

    /**
     * Retourne le type en fonction de son nom.
     *
     * @param name le nom.
     *
     * @return le type correspondant ou {@code null} si non trouvé.
     */
    public static IndexType getIndexType(String name) {
        return Arrays.stream(IndexType.values()).filter(target -> target.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
}
