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
package supervision;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Liste des élèves actifs durant la session.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class StudentClass {

    /**
     * Nombre de groupes.
     */
    public final static int GROUP_NUMBER = 8;
    /**
     * Nombre maximum d'élèves.
     */
    private int studentMax;
    /**
     * Stockage des élèves.
     */
    private List<Student> students;
    /**
     * Sauvegarde les groupes sélectionnés.
     */
    private boolean[] activatedGroups;

    /**
     * Initialise la liste des élèves.
     *
     * @param studentMax nombre d'élèves maximum autorisé.
     */
    public StudentClass(int studentMax) {
        this.studentMax = studentMax;
        this.students = new ArrayList<>(studentMax);

        activatedGroups = new boolean[GROUP_NUMBER];
        for (int i = 0; i < GROUP_NUMBER; i++) {
            activatedGroups[i] = false;
        }
    }

    /**
     * Ajoute un élève avec son adresse IP.
     * Ajoute un élève connecté (onLine). Prévient la duplication d'adresses IP.
     *
     * @param addressIP l'adresse IP de l'élève.
     * @param group le groupe affecté à l'élève.
     * @return {@code true} si l'élève à été ajouté ou si l'adresse IP est
     * déjà référencée.
     */
    public boolean addStudent(String addressIP, int group) {
        if (students.size() <= studentMax) {
            Student student = new Student(addressIP, group);
            student.setOnLine(true);
            return students.add(student);
        } else {
            return false;
        }
    }

    /**
     * Retourne le nombre d'élèves enregistrés dans la liste.
     *
     * @return le nombre d'élèves.
     */
    public int getStudentCount() {
        return students.size();
    }

    /**
     * Retourne le nombre maximum d'élèves autorisé pour la liste.
     *
     * @return le nombre maximum d'élèves.
     */
    public int getStudentCountMax() {
        return studentMax;
    }

    /**
     * Retourne l'élève possédant l'adresse IP indiquée.
     *
     * @param addressIP l'adresse IP.
     * @return l'élève avec l'adresse IP s'il existe ou {@code null}.
     */
    public Student getStudent(String addressIP) {
        if (addressIP == null) {
            return null;
        }

        for (Student student : students) {
            if (addressIP.contentEquals(student.getAddressIP())) {
                return student;
            }
        }
        return null;
    }

    /**
     * Retourne l'élève avec le login spécifié.
     *
     * @param login le login de l'élève à cherché.
     * @return l'élève trouvé ou {@code null}.
     */
    public Student getStudentWhithLogin(String login) {
        if (login == null) {
            return null;
        }

        for (Student student : students) {
            String current = student.getLogin();
            if (current != null && login.contentEquals(current)) {
                return student;
            }
        }
        return null;
    }

    /**
     * Retourne le premier élève de la liste.
     *
     * @return le premier élève ou {@code null} si la liste est vide.
     */
    public Student getFirstStudent() {
        if (students.isEmpty()) {
            return null;
        } else {
            return students.get(0);
        }
    }

    /**
     * Retourne un iterateur sur la liste d'élève.
     *
     * @return un iterateur sur la liste d'élève.
     */
    public Iterator<Student> iterator() {
        return students.iterator();
    }

    /**
     * Retourne si un des groupes est actif.
     *
     * @return si un des groupes est actif.
     */
    public boolean hasActivatedGroup() {
        return activatedGroups[0] || activatedGroups[1] || activatedGroups[2]
                || activatedGroups[3] || activatedGroups[4] || activatedGroups[5]
                || activatedGroups[6] || activatedGroups[7];
    }

    /**
     * Indique si le groupe a des élèves.
     *
     * @param group l'indice du groupe.
     * @return {@code true} si au moins un élève est affecté à ce groupe.
     */
    public boolean hasMemberInGroup(int group) {
        boolean hasMember = false;
        for (Student student : students) {
            if (student.getGroup() == group) {
                hasMember = true;
                break;
            }
        }
        return hasMember;
    }

    /**
     * Retourne si l'élève est sélectionné pour la diffusion.
     *
     * @param eleve le numéro de l'élève.
     * @return si l'élève est sélectionné pour la diffusion.
     */
    public boolean isSelectionnedForDiffuse(Student eleve) {
        return eleve.isOnLine() && activatedGroups[eleve.getGroup()];
    }

    /**
     * Modifie l'état de sélection du groupe.
     *
     * @param group le numéro du groupe.
     * @param active le nouvel état.
     * @return l'état réel de sélection du groupe.
     */
    public boolean setGroupActivated(int group, boolean active) {
        boolean hasMember = hasMemberInGroup(group);
        activatedGroups[group] = active && hasMember;
        return activatedGroups[group];
    }

    /**
     * Donne le nombre d'élèves sélectionnés.
     *
     * @return le nombre d'élèves sélectionnés.
     */
    public int getSelectedCount() {
        int nbSelected = 0;
        for (Student student : students) {
            if (isSelectionnedForDiffuse(student)) {
                nbSelected++;
            }
        }
        return nbSelected;
    }

    /**
     * Retourne l'élève suivant un autre élève dans la liste.
     *
     * @param student l'élève à partir duquel commence la recherche.
     * @return l'élève suivant dans la liste ou {@code null} si il n'y pas
     * d'élève suivant.
     */
    public Student next(Student student) {
        if (student != null) {
            int index = students.indexOf(student) + 1;
            if (index < students.size()) {
                return students.get(index);
            }
        }
        return null;
    }

    /**
     * Retourne l'élève sélectionné pour la diffusion suivant un autre élève.
     * Si l'élève à partir duquel commence la recherche est le dernier sélectionné
     * pour la diffusion dans liste, on reboucle à partir du début de la liste.
     *
     * @param student l'élève à partir duquel commence la recherche.
     * @return l'élève sélectionné pour la diffusion suivant.
     */
    public Student nextForScanning(Student student) {
        Student currentStudent;
        int index = students.indexOf(student);
        for (int i = index + 1; i < students.size(); i++) {
            currentStudent = students.get(i);
            if (isSelectionnedForDiffuse(currentStudent)) {
                return currentStudent;
            }
        }

        if (student == null) {
            return null;
        }
        // on reboucle au premier eleve
        return nextForScanning(null);
    }
}
