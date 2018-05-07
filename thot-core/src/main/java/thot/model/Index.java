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

import lombok.Data;

/**
 * Classe représentant un index soit de lecture ou soit d'enregistrement.
 *
 * @author Fabrice Alleau
 * @version 1.90
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
     *
     * @since version 0.90 - version 0.94
     */
    public Index(IndexType type) {
        this(type, -1);
    }

    /**
     * Initilise un index avec le temps initial.
     *
     * @param type le type d'index.
     * @param initialTime le temps initial.
     *
     * @since version 0.90 - version 1.01
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
     *
     * @since version 0.93
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
     *
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
     *
     * @since version 0.90 - version 0.94
     */
    public boolean isSubtitleType() {
        return type == IndexType.PLAY || type == IndexType.RECORD;
    }

    /**
     * Indique si l'index est un index de blanc.
     *
     * @return si c'est un index de blanc.
     *
     * @since version 0.90 - version 0.94
     */
    public boolean isBlankType() {
        return type == IndexType.BLANK || type == IndexType.BLANK_BEEP;
    }

    /**
     * Indique si l'index est un index de voix.
     *
     * @return si c'est un index de voix professeur.
     *
     * @since version 0.90 - version 0.94
     */
    public boolean isVoiceType() {
        return type == IndexType.VOICE;
    }

    /**
     * Indique si l'index est un index de fichier.
     *
     * @return si c'est un index de voix professeur.
     *
     * @since version 0.94 - version 1.02
     */
    public boolean isFileType() {
        return type == IndexType.FILE;
    }

    /**
     * Indique si l'index est un index de fichier.
     *
     * @return si c'est un index de voix professeur.
     *
     * @since version 0.97 - version 1.02
     */
    public boolean hasImage() {
        return image != null;
    }

    /**
     * Indique si l'index est un index de fichier.
     *
     * @return si c'est un index de voix professeur.
     *
     * @since version 0.94
     */
    public boolean isSelectionType() {
        return type == IndexType.SELECTION;
    }

    /**
     * Indique si l'index rajoute du temps.
     *
     * @return si c'est un index qui modifie la durée de la bande.
     *
     * @since version 0.94
     */
    public boolean isAllowedChildrenType() {
        return type == IndexType.FILE || type == IndexType.SELECTION;
    }

    /**
     * Indique si l'index rajoute du temps.
     *
     * @return si c'est un index qui modifie la durée de la bande.
     *
     * @since version 0.93 - version 1.01
     */
    public boolean isTimeLineModifier() {
        return type == IndexType.BLANK || type == IndexType.BLANK_BEEP || type == IndexType.VOICE
                || type == IndexType.FILE || type == IndexType.REPEAT || rate != NORMAL_RATE;
    }

    /**
     * Indique si l'index demande une phase d'enregistrement sur l'élève.
     *
     * @return si c'est un index qui modifie la durée de la bande.
     *
     * @since version 0.95
     */
    public boolean isStudentRecord() {
        return type == IndexType.RECORD || type == IndexType.REPEAT || type == IndexType.BLANK
                || type == IndexType.BLANK_BEEP;
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
     *
     * @since version 0.93 - version 0.95
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
