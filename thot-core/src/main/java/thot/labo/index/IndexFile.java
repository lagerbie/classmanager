package thot.labo.index;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Index d'insertion de fichier.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IndexFile extends Index {

    /**
     * Nom du fichier associé.
     */
    private String fileName;

    /**
     * Initilise un index d'insetion de fichier sans temps initial.
     *
     * @param fileName le nom de fichier.
     */
    public IndexFile(String fileName) {
        this(fileName, -1);
    }

    /**
     * Initilise un index avec le temps initial.
     *
     * @param fileName le nom de fichier.
     * @param initialTime le temps initial.
     */
    public IndexFile(String fileName, long initialTime) {
        super(IndexType.FILE, initialTime);
        this.fileName = fileName;
    }

    @Override
    public void clean() {
        super.clean();
        this.fileName = null;
    }
}
