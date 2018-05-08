package eestudio.gui;

import java.awt.*;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import thot.gui.GuiUtilities;

/**
 * Composant pour chager et sauvegarder des fichiers. Il utilise un compsoant Swing pour Windows ou un composant AWT
 * pour linux.
 *
 * @author Fabrice Alleau
 * @version 1.02
 * @since version 0.94
 */
@Deprecated
public class FileChooser {
    /**
     * Option pour le chargement de fichier
     */
    public static final int LOAD = JFileChooser.OPEN_DIALOG;
    /**
     * Option pour la sauvegarde de fichier
     */
    public static final int SAVE = JFileChooser.SAVE_DIALOG;

    /**
     * Composant graphique de l'explorateur de fichier
     */
    private JFileChooser chooserSwing;

    /**
     * Pattern pour les caractères interdits dans les noms de fichiers
     */
    private static final Pattern pattern = Pattern.compile("[:\"<>]");

    /**
     * Initilise le composant de choix du fichier avec son type et le répertoire par défaut.
     *
     * @param currentPath le répertoire courrant.
     *
     * @since version 0.94 - version 0.93.13
     */
    public FileChooser(File currentPath) {
        chooserSwing = new JFileChooser(currentPath);
    }

    /**
     * Ajuste la langue des éléments graphiques par celle par défaut.
     *
     * @since version 0.96
     */
    public void updateLanguage() {
        chooserSwing = new JFileChooser(chooserSwing.getCurrentDirectory());
    }

    /**
     * Reourne le fichier sélectionné.
     *
     * @param parent la fenêtre parente.
     * @param type le type soit {@code FileChooser.LOAD} ou soit {@code FileChooser.SAVE}.
     *
     * @return le fichier sélectionné ou {@code null} si l'opération a été annulée.
     *
     * @since version 0.94 - version 1.02
     */
    public File getSelectedFile(Window parent, int type) {
        File file = null;

        if (type == LOAD && chooserSwing.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            file = chooserSwing.getSelectedFile();
        } else if (type == SAVE && chooserSwing.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            file = chooserSwing.getSelectedFile();

            // remplacement des caractères spéciaux uniquement dans le nom du
            // fichier relatif au répertoire courant
            File directory = chooserSwing.getCurrentDirectory();
            String relativePath = file.getAbsolutePath().substring(directory.getAbsolutePath().length() + 1);

            file = new File(directory, pattern.matcher(relativePath).replaceAll("_"));
            // création des répertoires parent (cas où l'utilisateur est tapé \ ou /
            file.getParentFile().mkdirs();
        }

        GuiUtilities.manageUI(false);
        chooserSwing = new JFileChooser(chooserSwing.getCurrentDirectory());
        GuiUtilities.manageUI(true);
        return file;
    }

    /**
     * Met un filtre pour les fichiers.
     *
     * @param description le nom du filtre.
     * @param extensions les diverses extensions possible.
     *
     * @since version 0.94
     */
    public void setFileFilter(String description, String... extensions) {
        chooserSwing.setFileFilter(getFileFilter(description, extensions));
    }

    /**
     * Ajoute un filtre sélectionables pour les fichiers.
     *
     * @param description le nom du filtre.
     * @param extensions les diverses extensions possible.
     *
     * @since version 0.94
     */
    public void addChoosableFileFilter(String description, String... extensions) {
        chooserSwing.addChoosableFileFilter(getFileFilter(description, extensions));
    }

    /**
     * Détermine si le filtre "tous les fichiers" est disponible. Si {@code true} le filtre "tous les fichiers" devient
     * le filtre actif.
     *
     * @param accept si le filtre "tous les fichiers" est actif.
     *
     * @since version 0.94
     */
    public void setAcceptAllFileFilterUsed(boolean accept) {
        chooserSwing.setAcceptAllFileFilterUsed(accept);
    }

    /**
     * Renvoie un FileNameExtensionFilter pour les JFileChooser.
     *
     * @param filterName le nom du filtre.
     * @param extensions les diverses extensions possible.
     *
     * @return le filtre pour Swing.
     *
     * @since version 0.94
     */
    private FileFilter getFileFilter(String filterName, String... extensions) {
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].startsWith(".")) {
                extensions[i] = extensions[i].substring(1);
            }
        }

        return new FileNameExtensionFilter(filterName, extensions);
    }

}
