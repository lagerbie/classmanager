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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.Data;

/**
 * Classes regroupant une suite d'index.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
@Data
public class Indexes {

    /**
     * Mode d'examin (une seule lecture).
     */
    public static final String EXAMEN_MODE = "exam";
    /**
     * Mode libre (plusieurs lectures possibles).
     */
    public static final String OPEN_MODE = "free";
    /**
     * Succesion d'index normale.
     */
    public static final int NORMAL = 0;
    /**
     * Index de taille nulle.
     */
    public static final int NULL_SIZE = 2;
    /**
     * Des index se superposent.
     */
    public static final int OVERLAP = 4;
    /**
     * Des index sont indentiques.
     */
    public static final int IDENTICAL = 8;

    /**
     * Mode d'utilisation.
     */
    private String mode;
    /**
     * Durée totale du média.
     */
    private long mediaLength;
    /**
     * Sauvegarde la liste des index.
     */
    private List<Index> indexes;

    /**
     * Initialise la liste des index.
     */
    public Indexes() {
        mode = OPEN_MODE;
        mediaLength = 0;
        indexes = new ArrayList<>(32);
    }

    /**
     * Retourne la durée totale du média avec les insertions.
     *
     * @return la durée totale en millisecondes.
     */
    public long getLength() {
        long length = mediaLength;
        for (Index index : indexes) {
            if (index.isTimeLineModifier()) {
                if (index.getRate() == Index.NORMAL_RATE) {
                    length += index.getLength();
                } else {
                    length += index.getLength() * (1 - index.getRate());
                }
            }
        }
        return length;
    }

    /**
     * Retourne le nombre d'index.
     *
     * @return le nombre d'index.
     */
    public int getIndexesCount() {
        return indexes.size();
    }

    /**
     * Ajoute un index.
     * <p>
     * WARNING: pas de changements pour les index modifiant la durée.
     *
     * @param index l'index à ajouter.
     *
     * @return {@code true} si l'index a été ajouté.
     *
     * @see public boolean addIndex(Index index)
     */
    public boolean add(Index index) {
        return indexes.add(index);
    }

    /**
     * Ajoute un index.
     *
     * @param index l'index à ajouter.
     *
     * @return <code>true</true> si l'index a été ajouté.
     */
    public boolean addIndex(Index index) {
        updateIndexes(index, null);
        return indexes.add(index);
    }

    /**
     * Ajoute un index sans valeur de type lecture.
     *
     * @return l'identifiant de l'index.
     */
    public long addNullIndex() {
        Index index = new Index(IndexType.PLAY);
        indexes.add(index);
        return index.getId();
    }

    /**
     * Ajoute un demi-index de soustitre au temps voulu.
     *
     * @param time le temps voulu.
     *
     * @return si le demi index a été ajouté.
     */
    public boolean addHalfSubtitleIndexAtTime(long time) {
        boolean added = true;
        Index index = getHalfIndex();
        if (index == null) {
            added = indexes.add(new Index(IndexType.PLAY, time));
        } else {
            long begin = index.getInitialTime();
            if (begin > time) {
                index.setInitialTime(time);
                index.setFinalTime(begin);
            } else {
                index.setFinalTime(time);
            }
        }
        return added;
    }

    /**
     * Retoune le dernier index créé (pas de temps de fin).
     *
     * @return le dernier index créé.
     *
     * @since version 0.94
     */
    private Index getHalfIndex() {
        for (Index index : indexes) {
            if (index.getFinalTime() < 0) {
                return index;
            }
        }
        return null;
    }

    /**
     * Ajoute un demi-index de fin de soustitre au temps voulu. Supprime et fusionne les index si nécessaire.
     *
     * @param beginTime le temps de départ initial.
     * @param endTime le temps de fin de l'index.
     */
    public void addHalfSubtitleIndex(long beginTime, long endTime) {
        long initialTime = beginTime;
        long finalTime = endTime;

        Index beginIndex = getIndexAtTime(initialTime);
        Index endIndex = getIndexAtTime(finalTime);
        Index last = getLast();

        //si l'on commence dans un index le temps initial est celui de cet index
        if (beginIndex != null) {
            initialTime = beginIndex.getInitialTime();
            //si on fini dans cet index, on ajuste le temps de fin
            if (beginIndex.getFinalTime() > finalTime) {
                finalTime = beginIndex.getFinalTime();
            } else {
                if (endIndex != null && endIndex != beginIndex) {
                    finalTime = endIndex.getFinalTime();
                    removeIndex(endIndex);
                }
                beginIndex.setFinalTime(finalTime);
            }

            //si un index à été créé mais pas finalisé, on le supprime.
            if (last != null) {
                removeIndex(last);
                last = null;
            }
        } else if (endIndex != null) {
            finalTime = endIndex.getFinalTime();

            //si on fini dans cet index, on ajuste le temps de fin
            if (endIndex.getInitialTime() < initialTime) {
                initialTime = endIndex.getInitialTime();
            } else {
                endIndex.setInitialTime(initialTime);
            }

            if (last != null) {
                removeIndex(last);
                last = null;
            }
        }

        if (last != null) {
            last.setFinalTime(finalTime);
        }

        removeIndexesIn(initialTime, finalTime);
    }


//    /**
//     * Ajoute un demi-index de fin de soustitre au temps voulu.
//     * Supprime et fusionne les index si nécessaire.
//     *
//     * @param beginTime le temps de départ initial.
//     * @param endTime le temps de fin de l'index.
//     * @since version 0.99
//     * @deprecated
//     */
//    @Deprecated
//    public void addHalfSubtitleIndex(long beginTime, long endTime) {
//        long initialTime = beginTime;
//        long finalTime = endTime;
//
//        Index beginIndex = getIndexAtTime(initialTime);
//        Index endIndex = getIndexAtTime(finalTime);
//        Index last = getHalfIndex();
//
//        //si l'on commence dans un index le temps initial est celui de cet index
//        if(beginIndex != null) {
//            initialTime = beginIndex.getInitialTime();
//            //si on fini dans cet index, on ajuste le temps de fin
//            if(beginIndex.getFinalTime() > finalTime) {
//                finalTime = beginIndex.getFinalTime();
//            } else {
//                if(endIndex != null && endIndex != beginIndex) {
//                    finalTime = endIndex.getFinalTime();
//                    removeIndex(endIndex);
//                }
//                beginIndex.setFinalTime(finalTime);
//            }
//
//            //si un index à été créé mais pas finalisé, on le supprime.
//            if(last != null) {
//                removeIndex(last);
//                last = null;
//            }
//        }
//        else if(endIndex != null) {
//            finalTime = endIndex.getFinalTime();
//
//            //si on fini dans cet index, on ajuste le temps de fin
//            if(endIndex.getInitialTime() < initialTime) {
//                initialTime = endIndex.getInitialTime();
//            } else {
//                endIndex.setInitialTime(initialTime);
//            }
//
//            if(last != null) {
//                removeIndex(last);
//                last = null;
//            }
//        }
//
//        if(last != null) {
//            last.setFinalTime(finalTime);
//        }
//
//        removeIndexesIn(initialTime, finalTime);
//    }

    /**
     * Enleve l'index.
     *
     * @param index l'index à enlever.
     *
     * @return si l'index supprimé.
     */
    public boolean removeIndex(Index index) {
        boolean remove = indexes.remove(index);

        if (remove) {
            updateIndexes(null, index);
        }

        return remove;
    }

    /**
     * Enleve l'index contenant le temps ou le demi index le plus proche.
     *
     * @param time le temps compris dans l'index.
     *
     * @return l'index supprimé.
     */
    public Index removeIndexAtTime(long time) {
        Index index = getIndexAtTime(time);
        boolean remove = false;

        if (index != null) {//si on est sur un index
            remove = indexes.remove(index);
        } else {
            index = getNearestIndexAtTime(time);
            //si c'est le dernier index
            if (index != null && index.getFinalTime() < 0) {
                remove = indexes.remove(index);
            }
        }

        if (!remove) {
            index = null;
        }

        updateIndexes(null, index);

        return index;
    }

    /**
     * Supprime les index dont le temps final est inférieur ou égal au temps initial.
     */
    public void removeNullIndex() {
        List<Index> removeIndexes = new ArrayList<>(indexes.size());

        for (Index index : indexes) {
            if (index.getFinalTime() <= index.getInitialTime()) {
                removeIndexes.add(index);
            }
        }

        for (Index index : removeIndexes) {
            indexes.remove(index);
            index.clean();
        }

        removeIndexes.clear();
    }

    /**
     * Réinitialise la liste des index.
     */
    public void removeAll() {
        for (Index index : indexes) {
            index.clean();
        }
        indexes.clear();
    }

    /**
     * Enlève les index qui sont entièrement compris dans l'index courant.
     *
     * @param beginTime le temps de départ initial.
     * @param endTime le temps de fin de l'index.
     *
     * @deprecated
     */
    @Deprecated
    private void removeIndexesIn(long beginTime, long endTime) {
        List<Index> removeIndexes = new ArrayList<>(indexes.size());

        for (Index index : indexes) {
            if (index.getInitialTime() >= beginTime
                    && index.getFinalTime() <= endTime) {
                //supp de l'index compris dans l'intervale sans égalé
                //l'intervale
                if (index.getInitialTime() != beginTime
                        || index.getFinalTime() != endTime) {
                    removeIndexes.add(index);
                }
            }
        }

        for (Index index : removeIndexes) {
            indexes.remove(index);
            index.clean();
        }

        removeIndexes.clear();
    }

    /**
     * Retourne l'index avec l'id.
     *
     * @param id l'id de l'index.
     *
     * @return l'index.
     */
    public Index getIndexWithId(long id) {
        for (Index index : indexes) {
            if (id == index.getId()) {
                return index;
            }
        }
        return null;
    }

    /**
     * Retourne le premier Index qui contient le temps demandé.
     *
     * @param time le temps demandé.
     *
     * @return l'index.
     */
    public Index getIndexAtTime(long time) {
        for (Index index : indexes) {
            if (time >= index.getInitialTime() && time <= index.getFinalTime()) {
                return index;
            }
        }
        return null;
    }

    /**
     * Retourne le premier index dans le temps.
     *
     * @return le premier index.
     */
    private Index getFirstIndex() {
        Index first = null;
        long time = Long.MAX_VALUE;
        for (Index index : indexes) {
            if (index.getInitialTime() < time) {
                first = index;
                time = first.getInitialTime();
            }
        }
        return first;
    }

    /**
     * Retoune le dernier index créé (pas de temps de fin).
     *
     * @return le dernier index créé.
     *
     * @deprecated
     */
    @Deprecated
    private Index getLast() {
        for (Index index : indexes) {
            if (index.getFinalTime() < 0) {
                return index;
            }
        }
        return null;
    }

    /**
     * Retourne l'index le plus proche du temps demandé.
     *
     * @param time time le temps demandé.
     *
     * @return l'index.
     */
    private Index getNearestIndexAtTime(long time) {
        Index nearestIndex = null;
        long min = Long.MAX_VALUE;

        for (Index index : indexes) {
            if (Math.abs(time - index.getInitialTime()) < min) {
                min = Math.abs(time - index.getInitialTime());
                nearestIndex = index;
            }

            if (Math.abs(index.getFinalTime() - time) < min) {
                min = Math.abs(index.getFinalTime() - time);
                nearestIndex = index;
            }
        }
        return nearestIndex;
    }

    /**
     * Retourne le temps minimum pour le temps de départ de l'index.
     *
     * @param currentIndex l'index dont on veut redimmensionner.
     *
     * @return le temps minimum pour le début de l'index.
     */
    public long getMinimalTime(Index currentIndex) {
        long timeMin = 0;
        for (Index index : indexes) {
            long time = index.getFinalTime();
            if (time > timeMin && time <= currentIndex.getInitialTime()) {
                timeMin = time;
            }
        }
        return timeMin;
    }

    /**
     * Retourne le temps maximum pour le temps de fin de l'index.
     *
     * @param currentIndex l'index dont on veut redimmensionner.
     *
     * @return le temps maximum pour le temps de fin de l'index.
     */
    public long getMaximalTime(Index currentIndex) {
        long timeMax = getLength();
        for (Index index : indexes) {
            long time = index.getInitialTime();
            if (time < timeMax && time >= currentIndex.getFinalTime()) {
                timeMax = time;
            }
        }
        return timeMax;
    }

    /**
     * Retourne le temps minimum pour le temps de départ de l'index.
     *
     * @param currentIndex l'index dont on veut redimmensionner.
     *
     * @return le temps minimum pour le début de l'index.
     */
    public Index previousIndex(Index currentIndex) {
        Index previousIndex = null;
        long timeMin = 0;
        for (Index index : indexes) {
            long time = index.getFinalTime();
            if (time > timeMin && time <= currentIndex.getInitialTime()) {
                timeMin = time;
                previousIndex = index;
            }
        }
        return previousIndex;
    }

    /**
     * Retourne le temps maximum pour le temps de fin de l'index.
     *
     * @param currentIndex l'index dont on veut redimmensionner.
     *
     * @return le temps maximum pour le temps de fin de l'index.
     */
    public Index nextIndex(Index currentIndex) {
        Index nextIndex = null;
        long timeMax = getLength();
        for (Index index : indexes) {
            long time = index.getInitialTime();
            if (time < timeMax && time >= currentIndex.getFinalTime()) {
                timeMax = time;
                nextIndex = index;
            }
        }
        return nextIndex;
    }

    /**
     * Trie par ordre croissant dans le temps les index.
     */
    public void sortIndexes() {
        List<Index> newIndexes = new ArrayList<>(indexes.size());
        while (indexes.size() > 0) {
            Index index = getFirstIndex();
            newIndexes.add(index);
            indexes.remove(index);
        }
        indexes = newIndexes;
    }

    /**
     * Retourne un iterateur sur les Index.
     *
     * @return un iterateur sur les Index.
     */
    public Iterator<Index> iterator() {
        return indexes.iterator();
    }

    /**
     * Actualise les index avec les modifictions.
     *
     * @param newIndex le nouveau index.
     * @param oldIndex l'ancien index.
     */
    private void updateIndexes(Index newIndex, Index oldIndex) {
        if ((newIndex == null && oldIndex == null)
                || (newIndex != null && oldIndex != null)) {
            return;
        }

        //Mise à jour des index pour le redimensionnement de l'index
        long timeMin = 0;
        long timeOffset = 0;

        //Ajout d'un Index modifiant la durée
        if (oldIndex == null && newIndex.isTimeLineModifier()) {
            timeMin = newIndex.getInitialTime();
            timeOffset = newIndex.getLength();
            if (newIndex.getRate() != Index.NORMAL_RATE) {
                timeOffset = (long) (newIndex.getLength()
                        * (1 - newIndex.getRate()) / newIndex.getRate());
            }
        }
        //supp d'un Index modifiant la durée
        else if (newIndex == null && oldIndex.isTimeLineModifier()) {
            timeMin = oldIndex.getInitialTime();
            timeOffset = -oldIndex.getLength();
            if (oldIndex.getRate() != Index.NORMAL_RATE) {
                timeOffset = (long) (oldIndex.getLength() * (oldIndex.getRate() - 1));
            }
        }

        if (timeOffset != 0) {
            for (Index index : indexes) {
                if (index != newIndex && index.getInitialTime() >= timeMin) {
                    index.move(timeOffset);
                }
            }
        }
    }

    /**
     * Indique si la liste contient un index où l'élève doit s'enregistrer.
     *
     * @return si la liste contient un index où l'élève doit s'enregistrer.
     *
     * @since version 1.01
     */
    public boolean hasStudentRecordIndex() {
        for (Index index : indexes) {
            if (index.isStudentRecord()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indique si l'index à une durée nulle.
     *
     * @param currentIndex l'index.
     *
     * @return <code>true</code>si l'index à une durée nulle.
     */
    private boolean isNullSize(Index currentIndex) {
        long begin = currentIndex.getInitialTime();
        long end = currentIndex.getFinalTime();
        return (begin >= end);
    }

    /**
     * Indique si l'index est compris en partie dans un autre index.
     *
     * @param currentIndex l'index.
     *
     * @return <code>true</code>si l'index est à cheval avec un autre index.
     */
    private boolean isOverlapped(Index currentIndex) {
        long begin = currentIndex.getInitialTime();
        long end = currentIndex.getFinalTime();
        for (Index index : indexes) {
            if (index == currentIndex) {
                continue;
            }
            if ((begin > index.getInitialTime() && begin < index.getFinalTime())
                    || (end > index.getInitialTime() && end < index.getFinalTime())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indique si un index est identique à un autre index.
     *
     * @param currentIndex l'index.
     *
     * @return <code>true</code> si l'index est identique à un autre index.
     */
    private boolean isIdentical(Index currentIndex) {
        long begin = currentIndex.getInitialTime();
        long end = currentIndex.getFinalTime();

        for (Index index : indexes) {
            if (index == currentIndex) {
                continue;
            }
            if (begin == index.getInitialTime() && end == index.getFinalTime()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Retourne la validité de l'index.
     *
     * @param currentIndex l'index.
     *
     * @return la validité de l'index.
     *
     * @since version 0.99
     */
    private boolean checkValidity(Index currentIndex) {
        if (isNullSize(currentIndex)) {
            return false;
        }
        if (isOverlapped(currentIndex)) {
            return false;
        }
        if (isIdentical(currentIndex)) {
            return false;
        }
        return true;
    }

    /**
     * Retourne la validité globale.
     *
     * @return la validité globale.
     *
     * @since version 0.99
     */
    public boolean checkValidity() {
        for (Index index : indexes) {
            if (!checkValidity(index)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retourne la validité de l'index.
     *
     * @param currentIndex l'index.
     *
     * @return la validité de l'index (NORMAL, NULL_SIZE, OVERLAP, IDENTICAL).
     *
     * @deprecated
     */
    @Deprecated
    private int getValidity(Index currentIndex) {
        int normality = NORMAL;

        if (isNullSize(currentIndex)) {
            normality |= NULL_SIZE;
        }
        if (isOverlapped(currentIndex)) {
            normality |= OVERLAP;
        }
        if (isIdentical(currentIndex)) {
            normality |= IDENTICAL;
        }

        return normality;
    }

    /**
     * Retourne la validité globale.
     *
     * @return la validité globale (NORMAL, NULL_SIZE, OVERLAP, IDENTICAL).
     */
    public int getGlobalValidity() {
        int normality = NORMAL;

        for (Index index : indexes) {
            normality |= getValidity(index);
        }

        return normality;
    }
}
