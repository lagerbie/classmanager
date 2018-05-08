package thot.labo;

import java.util.Arrays;

import lombok.Getter;

/**
 * Liste descibles possibles pour un projet.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public enum ProjectTarget {

    /**
     * Logiciel standard (Vocalab 3, eeVision 2)
     */
    COMMON_SOFT("common"),
    /**
     * Logiciel standard (Vocalab 3, eeVision 2)
     */
    SIMPLE_EXPORT("simple"),
    /**
     * Logiciel EasyLab
     */
    EASYLAB("easyLab");

    @Getter
    private String name;

    ProjectTarget(String name) {
        this.name = name;
    }

    /**
     * Retourne l'enum en fonction de la cible.
     *
     * @param name le nom de la cible.
     *
     * @return l'enum correspondant ou {@code null} si non trouvé.
     */
    public static ProjectTarget getProjectTarget(String name) {
        return Arrays.stream(ProjectTarget.values()).filter(target -> target.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
}
