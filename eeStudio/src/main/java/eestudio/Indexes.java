package eestudio;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classes regroupant une suite d'index.
 *
 * @author Fabrice Alleau
 * @version 1.01
 * @since version 0.94
 */
@Deprecated
public class Indexes {
    /**
     * Mode d'examin (une seule lecture)
     */
    public static final String EXAMEN_MODE = "examen";
    /**
     * Mode libre (plusieurs lectures possibles)
     */
    public static final String OPEN_MODE = "libre";

    /**
     * Mode d'utilisation
     */
    private String mode;
    /**
     * Durée totale du média
     */
    private long mediaLength;

    /**
     * Sauvegarde la liste des index
     */
    private List<Index> indexes;

    /**
     * Initialise la liste des index.
     *
     * @since version 0.94
     */
    public Indexes() {
        mode = OPEN_MODE;
        mediaLength = 0;
        indexes = new ArrayList<Index>(32);
    }

    /**
     * Retourne le mode d'utilisation.
     *
     * @return le mode d'utilisation.
     *
     * @since version 0.94
     */
    public String getMode() {
        return mode;
    }

    /**
     * Modifie le mode d'utilisation.
     *
     * @param mode le mode d'utilisation.
     *
     * @since version 0.94
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * Retourne la durée totale du média sans les insertions.
     *
     * @return la durée totale en millisecondes.
     *
     * @since version 0.94
     */
    public long getMediaLength() {
        return mediaLength;
    }

    /**
     * Modifie la durée totale.
     *
     * @param length la durée totale en millisecondes.
     *
     * @since version 0.94
     */
    public void setMediaLength(long length) {
        this.mediaLength = length;
    }

    /**
     * Retourne la durée totale du média avec les insertions.
     *
     * @return la durée totale en millisecondes.
     *
     * @since version 0.94 - version 1.01
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
     *
     * @since version 0.94
     */
    public int getIndexesCount() {
        return indexes.size();
    }

    /**
     * Ajoute un index. WARNING: pas de changements pour les index modifiant la durée.
     *
     * @param index l'index à ajouter.
     *
     * @return <code>true</true> si l'index a été ajouté.
     *
     * @see #add(eestudio.Index)
     * @since version 0.94
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
     *
     * @since version 0.94
     */
    public boolean addIndex(Index index) {
        updateIndexes(index, null);
        return indexes.add(index);
    }

    /**
     * Ajoute un index sans valeur de type lecture.
     *
     * @return l'id de l'index crée.
     *
     * @since version 0.94 - version 0.95
     */
    public long addNullIndex() {
        Index index = new Index(Index.PLAY);
        indexes.add(index);
        return index.getId();
    }

    /**
     * Ajoute un demi-index de soustitre au temps voulu.
     *
     * @param time le temps voulu.
     *
     * @return si le demi index a été ajouté.
     *
     * @since version 0.94
     */
    public boolean addHalfSubtitleIndexAtTime(long time) {
        boolean added = true;
        Index index = getHalfIndex();
        if (index == null) {
            added = indexes.add(new Index(Index.PLAY, time));
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
     *
     * @since version 0.94
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
     *
     * @since version 0.94
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
        }//end if

        if (!remove) {
            index = null;
        }

        updateIndexes(null, index);

        return index;
    }

    /**
     * Supprime les index dont le temps final est inférieur ou égal au temps initial.
     *
     * @since version 0.94 - version 0.96
     */
    public void removeNullIndex() {
        List<Index> removeIndexes = new ArrayList<Index>(indexes.size());

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
     *
     * @since version 0.94 - version 0.96
     */
    public void removeAll() {
        for (Index index : indexes) {
            index.clean();
        }
        indexes.clear();
    }

    /**
     * Retourne l'index avec l'id.
     *
     * @param id l'id de l'index.
     *
     * @return l'index.
     *
     * @since version 0.95
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
     *
     * @since version 0.94
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
     *
     * @since version 0.94
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
     * Retourne l'index le plus proche du temps demandé.
     *
     * @param time time le temps demandé.
     *
     * @return l'index.
     *
     * @since version 0.94
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
     *
     * @since version 0.94
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
     *
     * @since version 0.94
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
     *
     * @since version 0.95
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
        }//end for
        return previousIndex;
    }

    /**
     * Retourne le temps maximum pour le temps de fin de l'index.
     *
     * @param currentIndex l'index dont on veut redimmensionner.
     *
     * @return le temps maximum pour le temps de fin de l'index.
     *
     * @since version 0.95
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
        }//end for
        return nextIndex;
    }

    /**
     * Trie par ordre croissant dans le temps les index.
     *
     * @since version 0.94
     */
    public void sortIndexes() {
        List<Index> newIndexes = new ArrayList<Index>(indexes.size());
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
     *
     * @since version 0.95
     */
    public Iterator<Index> iterator() {
        return indexes.iterator();
    }

    /**
     * Actualise les index avec les modifictions.
     *
     * @param newIndex le nouveau index.
     * @param oldIndex l'ancien index.
     *
     * @since version 0.94 - version 1.01
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
     *
     * @since version 0.94
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
     *
     * @since version 0.94
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
     *
     * @since version 0.94
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

}//end
