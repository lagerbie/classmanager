/*
 * ClassManager - Supervision de classes et Laboratoire de langue
 * Copyright (C) 2013 Fabrice Alleau <fabrice.alleau@siclic.fr>
 *
 * This file is part of ClassManager.
 *
 * ClassManager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ClassManager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ClassManager.  If not, see <http://www.gnu.org/licenses/>.
 */
package thot.model;

/**
 * Classe représentant un index soit de lecture ou soit d'enregistrement.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Index implements Cloneable {

    /**
     * Indentifiant inconu.
     */
    public static final String UNKNOWN = "unknown";
    /**
     * Indentifiant pour un index de lecture.
     */
    public static final String PLAY = "play";
    /**
     * Indentifiant pour un index d'enregistrement.
     */
    public static final String RECORD = "record";
    /**
     * Indentifiant pour un index de blanc.
     */
    public static final String BLANK = "blank";
    /**
     * Indentifiant pour un index de blanc avec beep.
     */
    public static final String BLANK_BEEP = "blankWithBeep";
    /**
     * Indentifiant pour un index de répétition.
     */
    public static final String REPEAT = "repeat";
    /**
     * Indentifiant pour un index de la voix du professeur.
     */
    public static final String VOICE = "voice";
    /**
     * Indentifiant pour un index d'incertion de fichier.
     */
    public static final String FILE = "file";
    /**
     * Indentifiant pour un index de la voix du professeur.
     */
    public static final String IMAGE = "image";
    /**
     * Indentifiant pour un index d'incertion de fichier.
     */
    public static final String SPEED = "speed";
    /**
     * Indentifiant pour un index de la voix du professeur.
     */
    public static final String SELECTION = "selection";

    /**
     * Référence pour les ids unique.
     */
    private static long idNumber = -1;

    /**
     * id unique de l'index.
     */
    private transient long id;
    /**
     * Temps de départ de l'index.
     */
    private long initialTime;
    /**
     * Temps de fin de l'index.
     */
    private long finalTime;
    /**
     * Type de l'index.
     */
    private String type;
    /**
     * Commentaire de l'index.
     */
    private String subtitle;
    /**
     * Nombre de lectures effectuées.
     */
    private int read;

    /**
     * Initilise un index sans temps initial.
     *
     * @param type le type d'index.
     */
    public Index(String type) {
        this(type, -1);
    }

    /**
     * Initilise un index avec le temps initial.
     *
     * @param type le type d'index.
     * @param initialTime le temps initial.
     */
    public Index(String type, long initialTime) {
        idNumber++;
        this.id = idNumber;
        this.initialTime = initialTime;
        this.finalTime = -1;
        this.type = type;
        this.subtitle = null;
        this.read = 0;
    }

    /**
     * Retourne l'id unique de l'index.
     *
     * @return l'id.
     */
    public long getId() {
        return id;
    }

    /**
     * Retourne le temps de début.
     *
     * @return le temps de début.
     */
    public long getInitialTime() {
        return initialTime;
    }

    /**
     * Retourne le temps de fin.
     *
     * @return le temps de fin.
     */
    public long getFinalTime() {
        return finalTime;
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
     * Retourne le type de cet index.
     *
     * @return le type.
     */
    public String getType() {
        return type;
    }

    /**
     * Retourne le commentaire.
     *
     * @return le commentaire.
     */
    public String getSubtitle() {
        return subtitle;
    }

    /**
     * Modifie le temps de début.
     *
     * @param initialTime le nouveau temps de début.
     */
    public void setInitialTime(long initialTime) {
        this.initialTime = initialTime;
    }

    /**
     * Modifie le temps de fin.
     *
     * @param finalTime le nouveau temps de fin.
     */
    public void setFinalTime(long finalTime) {
        this.finalTime = finalTime;
    }

    /**
     * Modifie le type de cet index.
     *
     * @param type le type de l'index.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Modifie le commentaire.
     *
     * @param subtitle le commentaire.
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * Retourne combien de fois l'index à été utilisé.
     *
     * @return le nombre de lectures effectuées.
     */
    public int getRead() {
        return read;
    }

    /**
     * Modifie le nombre de lectures de l'index.
     *
     * @param read le nombre de lectures de l'index.
     */
    public void setRead(int read) {
        this.read = read;
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
        return (type.contentEquals(PLAY) || type.contentEquals(RECORD));
    }

    /**
     * Indique si l'index est un index de blanc.
     *
     * @return si c'est un index de blanc.
     */
    public boolean isBlankType() {
        return (type.contentEquals(BLANK) || type.contentEquals(BLANK_BEEP));
    }

    /**
     * Indique si l'index est un index de voix.
     *
     * @return si c'est un index de voix professeur.
     */
    public boolean isVoiceType() {
        return type.contentEquals(VOICE);
    }

    /**
     * Indique si l'index est un index de fichier.
     *
     * @return si c'est un index de voix professeur.
     */
    public boolean isFileType() {
        return (type.contentEquals(FILE) || type.contentEquals(IMAGE));
    }

    /**
     * Indique si l'index est un index de fichier.
     *
     * @return si c'est un index de voix professeur.
     */
    public boolean isImageType() {
        return type.contentEquals(IMAGE);
    }

    /**
     * Indique si l'index est un index de fichier.
     *
     * @return si c'est un index de voix professeur.
     */
    public boolean isSpeedType() {
        return type.contentEquals(SPEED);
    }

    /**
     * Indique si l'index est un index de fichier.
     *
     * @return si c'est un index de voix professeur.
     */
    public boolean isSelectionType() {
        return type.contentEquals(SELECTION);
    }

    /**
     * Indique si l'index rajoute du temps.
     *
     * @return si c'est un index qui modifie la durée de la bande.
     */
    public boolean isAllowedChildrenType() {
        return (type.contentEquals(FILE) || type.contentEquals(SELECTION));
    }

    /**
     * Indique si l'index rajoute du temps.
     *
     * @return si c'est un index qui modifie la durée de la bande.
     */
    public boolean isTimeLineModifier() {
        return (type.contentEquals(BLANK) || type.contentEquals(BLANK_BEEP)
                || type.contentEquals(VOICE) || type.contentEquals(FILE)
                || type.contentEquals(REPEAT));
    }

    /**
     * Indique si l'index demande une phase d'enregistrement sur l'élève.
     *
     * @return si c'est un index qui modifie la durée de la bande.
     */
    public boolean isStudentRecord() {
        return (type.contentEquals(RECORD) || type.contentEquals(REPEAT)
                || type.contentEquals(BLANK) || type.contentEquals(BLANK_BEEP));
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
        Object o = null;
        try {
            o = super.clone();
        } catch (CloneNotSupportedException e) {
            throw e;
        }
        return o;
    }
}
