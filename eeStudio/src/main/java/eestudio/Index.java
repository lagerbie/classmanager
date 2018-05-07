package eestudio;

/*
 * v0.93: ajout de public long getLength()
 * v0.93: ajout de public void move(long timeOffset)
 * v0.93: ajout de public boolean isTimeLineModifier()
 * v0.93: ajout de public Object clone()
 *
 * v0.94: supp de implements Serializable
 * v0.94: supp de private static final long serialVersionUID
 * v0.94: supp de @Deprecated public Index()
 * v0.94: ajout de implements Cloneable
 * v0.94: ajout de private int read
 * v0.94: ajout de public int getRead()
 * v0.94: ajout de public void setRead(int read)
 * v0.94: ajout de public boolean isFileType()
 * v0.94: ajout de public boolean isSpeedType()
 * v0.94: ajout de public boolean isSelectionType()
 * v0.94: ajout de public boolean isAllowedChildrenType()
 * v0.94: modif de type de int en String
 * v0.94: modif des constantes pour le type de int en String
 * v0.94: modif des fonctions de test de type pour tester des String
 * 
 * v0.95: ajout de private static long idNumber = -1;
 * v0.95: ajout de private transient long id;
 * v0.95: ajout de public long getId()
 * v0.95: ajout de public boolean isStudentRecord()
 * v0.95: ajout de throws CloneNotSupportedException dans public Object clone()
 * v0.95: modif de Index(String type, long initialTime) [id, idNumber]
 * 
 * v0.96: ajout de public void clean()
 * 
 * v0.97: ajout de public boolean isImageType()
 * 
 * v1.01: supp de public static final String SPEED = "speed";
 * v1.01: ajout de public static final float NORMAL_RATE = 1;
 * v1.01: ajout de public static final float RATE_MIN = 0.5f;
 * v1.01: ajout de public static final float RATE_MAX = 2.0f;
 * v1.01: ajout de private float rate;
 * v1.01: ajout de public float getRate()
 * v1.01: ajout de public void setRate(float rate)
 * v1.01: supp de public boolean isSpeedType()
 * v1.01: modif de public Index(String type, long initialTime) [add rate]
 * v1.01: modif de isTimeLineModifier() [add rate != NORMAL_RATE]
 * 
 * v1.02: supp de public static final String IMAGE = "image";
 * v1.02: ajout de private String image;
 * v1.02: ajout de public String getImage()
 * v1.02: ajout de public void setImage(String image)
 * v1.02: modif de isFileType()
 * v1.02: modif de isImageType() en hasImage()
 */

/**
 * Classe représentant un index soit de lecture ou soit d'enregistrement.
 * 
 * @author Fabrice Alleau
 * @since version 0.90
 * @version 1.02
 */
public class Index implements Cloneable {
    /** Indentifiant inconu */
    public static final String UNKNOWN = "unknown";
    /** Indentifiant pour un index de lecture */
    public static final String PLAY = "play";
    /** Indentifiant pour un index d'enregistrement */
    public static final String RECORD = "record";
    /** Indentifiant pour un index de blanc */
    public static final String BLANK = "blank";
    /** Indentifiant pour un index de blanc avec beep */
    public static final String BLANK_BEEP = "blankWithBeep";
    /** Indentifiant pour un index de répétition */
    public static final String REPEAT = "repeat";
    /** Indentifiant pour un index de la voix du professeur */
    public static final String VOICE = "voice";
    /** Indentifiant pour un index d'incertion de fichier */
    public static final String FILE = "file";
    /** Indentifiant pour un index de sélection */
    public static final String SELECTION = "selection";

    /** Indentifiant pour un index de la voix du professeur */
    public static final float NORMAL_RATE = 1;
    /** Minimum rate pour la vitesse de lecture du média */
    public static final float RATE_MIN = 0.5f;
    /** Maximum rate pour la vitesse de lecture du média */
    public static final float RATE_MAX = 2.0f;

    /** Référence pour les ids unique */
    private static long idNumber = -1;

    /** id unique de l'index */
    private transient long id;

    /** Temps de départ de l'index */
    private long initialTime;
    /** Temps de fin de l'index */
    private long finalTime;

    /** Type de l'indes */
    private String type;
    /** Commentaire de l'index */
    private String subtitle;

    /** Vitesse de l'index par rapport à la vitesse originelle */
    private float rate;
    /** Fichier image associés à l'index */
    private String image;

    /** Nombre de lectures effectuées */
    private int read;

    /**
     * Initilise un index sans temps initial.
     *
     * @param type le type d'index.
     * @since version 0.90 - version 0.94
     */
    public Index(String type) {
        this(type, -1);
    }

    /**
     * Initilise un index avec le temps initial.
     *
     * @param type le type d'index.
     * @param initialTime le temps initial.
     * @since version 0.90 - version 1.01
     */
    public Index(String type, long initialTime) {
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
     * Retourne l'id unique de l'index.
     *
     * @return l'id.
     * @since version 0.95
     */
    public long getId() {
        return id;
    }

    /**
     * Retourne le temps de début.
     *
     * @return le temps de début.
     * @since version 0.90
     */
    public long getInitialTime() {
        return initialTime;
    }

    /**
     * Retourne le temps de fin.
     *
     * @return le temps de fin.
     * @since version 0.90
     */
    public long getFinalTime() {
        return finalTime;
    }

    /**
     * Retourne la durée de l'index.
     *
     * @return la durée de l'index.
     * @since version 0.93
     */
    public long getLength() {
        if(finalTime < initialTime)
            return -1;
        else
            return finalTime - initialTime;
    }

    /**
     * Retourne le type de cet index.
     *
     * @return le type.
     * @since version 0.90 - version 0.94
     */
    public String getType() {
        return type;
    }

    /**
     * Retourne le commentaire.
     *
     * @return le commentaire.
     * @since version 0.90
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Retourne la vitesse associée à l'index.
     * 
     * @return la vitesse associée à l'index.
     * @since version 1.01
     */
    public float getRate() {
        return rate;
    }

    /**
     * Retourne le nom du fichier image associé.
     * 
     * @return le nom du fichier image.
     * @since version 1.02
     */
    public String getImage() {
        return image;
    }

    /**
     * Modifie le temps de début.
     *
     * @param initialTime le nouveau temps de début.
     * @since version 0.90
     */
    public void setInitialTime(long initialTime) {
        this.initialTime = initialTime;
    }

    /**
     * Modifie le temps de fin.
     *
     * @param finalTime le nouveau temps de fin.
     * @since version 0.90
     */
    public void setFinalTime(long finalTime) {
        this.finalTime = finalTime;
    }

    /**
     * Modifie le type de cet index.
     *
     * @param type le type de l'index.
     * @since version 0.90 - version 0.94
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Modifie le commentaire.
     *
     * @param subtitle le commentaire.
     * @since version 0.90
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * Modifie la vitesse associée à l'index.
     * 
     * @param rate la nouvelle vitesse.
     * @since version 1.01
     */
    public void setRate(float rate) {
        this.rate = rate;
    }

    /**
     * Modifie le nom du fichier image associé.
     * 
     * @param image le nom du fichier image.
     * @since version 1.02
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Retourne combien de fois l'index à été utilisé.
     *
     * @return le nombre de lectures effectuées.
     * @since version 0.94
     */
    public int getRead() {
        return read;
    }

    /**
     * Modifie le nombre de lectures de l'index.
     *
     * @param read le nombre de lectures de l'index.
     * @since version 0.94
     */
    public void setRead(int read) {
        this.read = read;
    }

    /**
     * Déplace l'index dans le temps d'un certaine valeur.
     *
     * @param timeOffset l'offset de déplacement.
     * @since version 0.93
     */
    public void move(long timeOffset) {
        this.initialTime += timeOffset;
        this.finalTime += timeOffset;
    }

    /**
     * Indique si l'index est un index de soustitre (Lecture ou Enregistrement).
     *
     * @return si c'est un index de soustitre.
     * @since version 0.90 - version 0.94
     */
    public boolean isSubtitleType() {
        return (type.contentEquals(PLAY) || type.contentEquals(RECORD));
    }

    /**
     * Indique si l'index est un index de blanc.
     *
     * @return si c'est un index de blanc.
     * @since version 0.90 - version 0.94
     */
    public boolean isBlankType() {
        return (type.contentEquals(BLANK) || type.contentEquals(BLANK_BEEP));
    }

    /**
     * Indique si l'index est un index de voix.
     *
     * @return si c'est un index de voix professeur.
     * @since version 0.90 - version 0.94
     */
    public boolean isVoiceType() {
        return type.contentEquals(VOICE);
    }

    /**
     * Indique si l'index est un index de fichier.
     *
     * @return si c'est un index de voix professeur.
     * @since version 0.94 - version 1.02
     */
    public boolean isFileType() {
        return type.contentEquals(FILE);
    }

    /**
     * Indique si l'index est un index de fichier.
     *
     * @return si c'est un index de voix professeur.
     * @since version 0.97 - version 1.02
     */
    public boolean hasImage() {
        return image != null;
    }

    /**
     * Indique si l'index est un index de fichier.
     *
     * @return si c'est un index de voix professeur.
     * @since version 0.94
     */
    public boolean isSelectionType() {
        return type.contentEquals(SELECTION);
    }

    /**
     * Indique si l'index rajoute du temps.
     *
     * @return si c'est un index qui modifie la durée de la bande.
     * @since version 0.94
     */
    public boolean isAllowedChildrenType() {
        return (type.contentEquals(FILE) || type.contentEquals(SELECTION));
    }

    /**
     * Indique si l'index rajoute du temps.
     *
     * @return si c'est un index qui modifie la durée de la bande.
     * @since version 0.93 - version 1.01
     */
    public boolean isTimeLineModifier() {
        return (type.contentEquals(BLANK) || type.contentEquals(BLANK_BEEP)
                || type.contentEquals(VOICE) || type.contentEquals(FILE)
                || type.contentEquals(REPEAT)
                || rate != NORMAL_RATE);
    }

    /**
     * Indique si l'index demande une phase d'enregistrement sur l'élève.
     *
     * @return si c'est un index qui modifie la durée de la bande.
     * @since version 0.95
     */
    public boolean isStudentRecord() {
        return (type.contentEquals(RECORD) || type.contentEquals(REPEAT)
                || type.contentEquals(BLANK) || type.contentEquals(BLANK_BEEP));
    }

    /**
     * Nettoyage des références en vue d'une suppresion.
     * 
     * @since version 0.96
     */
    public void clean() {
        this.subtitle = null;
    }

    /**
     * Clone de l'index.
     *
     * @return un nouveau Index avec les même propriété.
     * @since version 0.93 - version 0.95
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Object o = null;
        try {
            o = super.clone();
        } catch(CloneNotSupportedException e) {
            throw e;
        }
        return o;
    }

}//end
