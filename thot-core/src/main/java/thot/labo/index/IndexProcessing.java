package thot.labo.index;

import java.util.Iterator;

/**
 * Interface pour la gestion des index.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public interface IndexProcessing {

    /**
     * Retourne l'état du module audio.
     * <p>
     * Il peut prendre les valeurs suivantes:
     * <p>
     * {@code StudentCore.PAUSE},
     * <p>
     * {@code StudentCore.PLAYING},
     * <p>
     * {@code StudentCore.RECORDING}.
     *
     * @return l'état du module audio.
     */
    int getRunningState();

    long getRecordTimeMax();

    boolean onIndex(double position);

    boolean onStudentIndex(double position);

    Iterator<Index> recordIndexIterator();

    Iterator<Index> mediaIndexIterator();

    void playOnIndex(double position);

    void recordOnIndex(double position);

    void setTimeBeginIndex(double position);

    void setTimeEndIndex(double position);

    void eraseIndex(double position);

    /**
     * Efface l'enregistrement effectué sur l'index sélectionné.
     *
     * @param position la position relative entre 0 et 1.
     */
    void eraseIndexRecord(double position);
}
