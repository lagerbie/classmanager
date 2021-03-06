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
package thot.gui;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Composant pour chager et sauvegarder des fichiers.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class FileChooser {

    /**
     * Option pour le chargement de fichier.
     */
    public static final int LOAD = FileDialog.LOAD;
    /**
     * Option pour la sauvegarde de fichier.
     */
    public static final int SAVE = FileDialog.SAVE;
    /**
     * Composant graphique de l'explorateur de fichier en SWING.
     */
    private JFileChooser chooserSwing;
    /**
     * Composant graphique de l'explorateur de fichier en AWT.
     */
    private FileDialog chooserAWT;
    /**
     * Fenêtre parente.
     */
    private Window parent;

    /**
     * Initilise le composant de choix du fichier avec son type et le répertoire par défaut.
     *
     * @param parent la fenêtre parente.
     * @param currentPath le répertoire courrent.
     */
    public FileChooser(Window parent, File currentPath) {
        this.parent = parent;
        chooserSwing = new JFileChooser(currentPath);
    }

    /**
     * Initilise le composant de choix du fichier avec son type et le répertoire par défaut.
     *
     * @param parent la fenêtre parente.
     * @param currentPath le répertoire courrent.
     */
    public FileChooser(Frame parent, File currentPath, boolean awt) {
        this.parent = parent;
        if (awt) {
            chooserAWT = new FileDialog(parent);
        } else {
            chooserSwing = new JFileChooser(currentPath);
        }
    }

    /**
     * Ajuste la langue des éléments graphiques par celle par défaut.
     */
    public void updateLanguage() {
        if (chooserSwing != null) {
            chooserSwing = new JFileChooser(chooserSwing.getCurrentDirectory());
        }
        if (chooserAWT != null) {
            String directory = chooserAWT.getDirectory();
            chooserAWT = new FileDialog((Frame) chooserAWT.getParent());
            chooserAWT.setDirectory(directory);
        }
    }

    /**
     * Reourne le fichier sélectionné.
     *
     * @param type le type soit <code>FileChooser.LOAD</code> ou soit
     *         <code>FileChooser.SAVE</code>.
     *
     * @return le fichier sélectionné ou <code>null</code> si l'opération a été annulée.
     */
    public File getSelectedFile(int type) {
        if (chooserSwing == null) {
            return getSelectedFileAWT(type);
        }
        File file = null;

        if (type == LOAD && chooserSwing.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            file = chooserSwing.getSelectedFile();
        } else if (type == SAVE && chooserSwing.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            file = chooserSwing.getSelectedFile();
        }

        chooserSwing = new JFileChooser(chooserSwing.getCurrentDirectory());

        return file;
    }

    /**
     * Reourne le fichier sélectionné.
     *
     * @param type le type soit <code>FileChooser.LOAD</code> ou soit
     *         <code>FileChooser.SAVE</code>.
     *
     * @return le fichier sélectionné ou <code>null</code> si l'opération a été annulée.
     */
    private File getSelectedFileAWT(int type) {
        File file = null;
        chooserAWT.setMode(type);
        chooserAWT.setVisible(true);
        String mrl = chooserAWT.getFile();
        String directory = chooserAWT.getDirectory();

        if (mrl != null) {
            file = new File(directory, mrl);
        }
        chooserAWT.setDirectory(directory);
        return file;
    }

    /**
     * Met un filtre pour les fichiers.
     *
     * @param description le nom du filtre.
     * @param extensions les diverses extensions possible.
     */
    public void setFileFilter(String description, String... extensions) {
        if (chooserSwing != null) {
            chooserSwing.setFileFilter(getFileFilter(description, extensions));
        }
        if (chooserAWT != null) {
            chooserAWT.setFilenameFilter(getFileNameFilter(description, extensions));
        }
    }

    /**
     * Ajoute un filtre sélectionables pour les fichiers.
     *
     * @param description le nom du filtre.
     * @param extensions les diverses extensions possible.
     */
    public void addChoosableFileFilter(String description, String... extensions) {
        if (chooserSwing != null) {
            chooserSwing.addChoosableFileFilter(getFileFilter(description, extensions));
        }
        if (chooserAWT != null) {
            chooserAWT.setFilenameFilter(getFileNameFilter(description, extensions));
        }
    }

    /**
     * Détermine si le filtre "tous les fichiers" est disponible. Si
     * <code>true</code> le filtre "tous les fichiers" devient le filtre actif.
     *
     * @param accept si le filtre "tous les fichiers" est actif.
     */
    public void setAcceptAllFileFilterUsed(boolean accept) {
        if (chooserSwing != null) {
            chooserSwing.setAcceptAllFileFilterUsed(accept);
        }
    }

    /**
     * Renvoie un FileNameExtensionFilter pour les JFileChooser.
     *
     * @param filterName le nom du filtre.
     * @param extensions les diverses extensions possible.
     *
     * @return le filtre pour Swing.
     */
    private FileFilter getFileFilter(String filterName, String... extensions) {
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].startsWith(".")) {
                extensions[i] = extensions[i].substring(1);
            }
        }

        return new FileNameExtensionFilter(filterName, extensions);
    }

    /**
     * Renvoie un FileNameExtensionFilter pour les JFileChooser.
     *
     * @param filterName le nom du filtre.
     * @param extensions les diverses extensions possible.
     *
     * @return le filtre pour Swing.
     */
    private FilenameFilter getFileNameFilter(String filterName, String... extensions) {
        return new FileNameFilter(getFileFilter(filterName, extensions));
    }

    private static class FileNameFilter implements FilenameFilter {

        private FileFilter extensionFilter;

        FileNameFilter(FileFilter extensionFilter) {
            this.extensionFilter = extensionFilter;
        }

        @Override
        public boolean accept(File dir, String name) {
            return extensionFilter.accept(new File(dir, name));
        }
    }
}
