package eestudio;

/**
 * Index d'insertion de fichier.
 *
 * @author Fabrice Alleau
 * @version 0.96
 * @since version 0.90
 */
@Deprecated
public class IndexFile extends Index {
    /**
     * Nom du fichier associé
     */
    private String fileName;

    /**
     * Initilise un index d'insetion de fichier sans temps initial.
     *
     * @param fileName le nom de fichier.
     *
     * @since version 0.90
     */
    public IndexFile(String fileName) {
        this(fileName, -1);
    }

    /**
     * Initilise un index avec le temps initial.
     *
     * @param fileName le nom de fichier.
     * @param initialTime le temps initial.
     *
     * @since version 0.90
     */
    public IndexFile(String fileName, long initialTime) {
        super(FILE, initialTime);
        this.fileName = fileName;
    }

    /**
     * Retourne le nom de fichier.
     *
     * @return le nom de fichier.
     *
     * @since version 0.90
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Modifie le nom du fichier.
     *
     * @param fileName le nom de fichier.
     *
     * @since version 0.90
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Nettoyage des références en vu d'une suppresion.
     *
     * @since version 0.96
     */
    @Override
    public void clean() {
        super.clean();
        this.fileName = null;
    }

}//end
