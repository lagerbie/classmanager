package thot.labo.index;

import lombok.Data;

/**
 * Classe représentant un index soit de lecture ou soit d'enregistrement.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
@Data
public class Index implements Cloneable {

    /**
     * Indentifiant pour un index de la voix du professeur
     */
    public static final float NORMAL_RATE = 1;
    /**
     * Minimum rate pour la vitesse de lecture du média
     */
    public static final float RATE_MIN = 0.5f;
    /**
     * Maximum rate pour la vitesse de lecture du média
     */
    public static final float RATE_MAX = 2.0f;

    /**
     * Référence pour les ids unique
     */
    private static long idNumber = -1;

    /**
     * id unique de l'index
     */
    private transient long id;

    /**
     * Temps de départ de l'index
     */
    private long initialTime;
    /**
     * Temps de fin de l'index
     */
    private long finalTime;

    /**
     * Type de l'index
     */
    private IndexType type;
    /**
     * Commentaire de l'index
     */
    private String subtitle;

    /**
     * Vitesse de l'index par rapport à la vitesse originelle
     */
    private float rate;
    /**
     * Fichier image associés à l'index
     */
    private String image;

    /**
     * Nombre de lectures effectuées
     */
    private int read;

    /**
     * Initilise un index sans temps initial.
     *
     * @param type le type d'index.
     */
    public Index(IndexType type) {
        this(type, -1);
    }

    /**
     * Initilise un index avec le temps initial.
     *
     * @param type le type d'index.
     * @param initialTime le temps initial.
     */
    public Index(IndexType type, long initialTime) {
        idNumber++;
        this.id = idNumber;
        this.initialTime = initialTime;
        this.finalTime = -1;
        this.type = type;
        this.subtitle = null;
        this.read = 0;
        this.rate = NORMAL_RATE;
    }

    /**
     * Retourne la durée de l'index.
     *
     * @return la durée de l'index.
     */
    public long getLength() {
        if (finalTime < initialTime) {
            return -1;
        } else {
            return finalTime - initialTime;
        }
    }

    /**
     * Déplace l'index dans le temps d'un certaine valeur.
     *
     * @param timeOffset l'offset de déplacement.
     */
    public void move(long timeOffset) {
        this.initialTime += timeOffset;
        this.finalTime += timeOffset;
    }

    /**
     * Indique si l'index est un index de soustitre (Lecture ou Enregistrement).
     *
     * @return si c'est un index de soustitre.
     */
    public boolean isSubtitleType() {
        return type == IndexType.PLAY || type == IndexType.RECORD;
    }

    /**
     * Indique si l'index est un index de blanc.
     *
     * @return si c'est un index de blanc.
     */
    public boolean isBlankType() {
        return type == IndexType.BLANK || type == IndexType.BLANK_BEEP;
    }

    /**
     * Indique si l'index est un index de voix.
     *
     * @return si c'est un index de voix professeur.
     */
    public boolean isVoiceType() {
        return type == IndexType.VOICE;
    }

    /**
     * Indique si l'index est un index de fichier.
     *
     * @return si c'est un index de voix professeur.
     */
    public boolean isFileType() {
        return type == IndexType.FILE;
    }

    /**
     * Indique si l'index est un index de fichier.
     *
     * @return si c'est un index de voix professeur.
     */
    public boolean hasImage() {
        return image != null;
    }

    /**
     * Indique si l'index est un index de fichier.
     *
     * @return si c'est un index de voix professeur.
     */
    public boolean isSelectionType() {
        return type == IndexType.SELECTION;
    }

    /**
     * Indique si l'index rajoute du temps.
     *
     * @return si c'est un index qui modifie la durée de la bande.
     */
    public boolean isAllowedChildrenType() {
        return type == IndexType.FILE || type == IndexType.SELECTION;
    }

    /**
     * Indique si l'index rajoute du temps.
     *
     * @return si c'est un index qui modifie la durée de la bande.
     */
    public boolean isTimeLineModifier() {
        return type == IndexType.BLANK || type == IndexType.BLANK_BEEP || type == IndexType.VOICE
                || type == IndexType.FILE || type == IndexType.REPEAT || rate != NORMAL_RATE;
    }

    /**
     * Indique si l'index demande une phase d'enregistrement sur l'élève.
     *
     * @return si c'est un index qui modifie la durée de la bande.
     */
    public boolean isStudentRecord() {
        return type == IndexType.RECORD || type == IndexType.REPEAT || type == IndexType.BLANK
                || type == IndexType.BLANK_BEEP;
    }

    /**
     * Nettoyage des références en vue d'une suppresion.
     */
    public void clean() {
        this.subtitle = null;
    }

    /**
     * Clone de l'index.
     *
     * @return un nouveau Index avec les même propriété.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Object o;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException e) {
            throw e;
        }
        return o;
    }
}
