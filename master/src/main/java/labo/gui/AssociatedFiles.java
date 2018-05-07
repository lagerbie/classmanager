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
package labo.gui;

import thot.gui.WaitDialog;
import thot.gui.Resources;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import labo.LaboModule;
import thot.model.ProjectFiles;
import thot.gui.GuiUtilities;
import thot.utils.Utilities;

/**
 * Gestion de l'ouveture ou l'enregistrement d'un fichier avec un projet
 * associé.
 *
 * @author Fabrice alleau
 * @version 1.82
 */
public class AssociatedFiles {

    private static File audioFile;
    private static File multimediaFile;
    private static File indexesFile;
    private static File subtitleFile;
    private static File textFile;

    private static JRadioButton otherButton;
    private static JRadioButton allButton;

    private static JCheckBox mediaButton;
    private static JCheckBox audioButton;
    private static JCheckBox textButton;

    /**
     * Initialisation de la boîte de dialogue pour sauvegarder ou charger un
     * fichier.
     *
     * @param owner la frame parente.
     * @param core les ressources pour les images.
     * @param resources
     * @param file le nom du fichier.
     * @version 1.82
     */
    public static void showDialog(final JDialog owner, final LaboModule core,
            final Resources resources, final File file) {

        ProjectFiles projectFiles = null;
        if (file.exists()) {
            projectFiles = Utilities.getProject(file);
        }

        if (projectFiles == null) {
            projectFiles = new ProjectFiles();
        }

        audioFile = null;
        multimediaFile = null;
        indexesFile = null;
        subtitleFile = null;
        textFile = null;

        if (projectFiles.getAudioFile() != null) {
            audioFile = new File(projectFiles.getAudioFile());
        }
        if (projectFiles.getVideoFile() != null) {
            multimediaFile = new File(projectFiles.getVideoFile());
        }
        if (projectFiles.getIndexesFile() != null) {
            indexesFile = new File(projectFiles.getIndexesFile());
        }
        if (projectFiles.getSubtitleFile() != null) {
            subtitleFile = new File(projectFiles.getSubtitleFile());
        }
        if (projectFiles.getTextFile() != null) {
            textFile = new File(projectFiles.getTextFile());
        }

        //vérification que les fichiers en chargement sont disponibles
        //Test si chemin relatif
        if ((audioFile != null && !audioFile.exists())
                || (textFile != null && !textFile.exists())
                || (indexesFile != null && !indexesFile.exists())) {
            File path = file.getParentFile();

            if (audioFile != null) {
                audioFile = new File(path, audioFile.getName());
            }
            if (textFile != null) {
                textFile = new File(path, textFile.getName());
            }
            if (multimediaFile != null) {
                multimediaFile = new File(path, multimediaFile.getName());
            }
            if (indexesFile != null) {
                indexesFile = new File(path, indexesFile.getName());
            }
            if (subtitleFile != null) {
                subtitleFile = new File(path, subtitleFile.getName());
            }
        }//end relatif

        if (multimediaFile != null && !multimediaFile.exists()) {
            GuiUtilities.showMessageDialog(owner,
                    String.format(resources.getString("noFile"), multimediaFile));
            multimediaFile = null;
        }//end multimediaFile

        if (indexesFile != null && !indexesFile.exists()) {
            GuiUtilities.showMessageDialog(owner,
                    String.format(resources.getString("noFile"), indexesFile));
            indexesFile = null;
        }//end indexesFile

        if (subtitleFile != null && !subtitleFile.exists()) {
            GuiUtilities.showMessageDialog(owner,
                    String.format(resources.getString("noFile"), subtitleFile));
            subtitleFile = null;
        }//end subtitleFile

        if (audioFile != null && !audioFile.exists()) {
            GuiUtilities.showMessageDialog(owner,
                    String.format(resources.getString("noFile"), audioFile));
            audioFile = null;
        }//end audioFile

        if (textFile != null && !textFile.exists()) {
            GuiUtilities.showMessageDialog(owner,
                    String.format(resources.getString("noFile"), textFile));
            textFile = null;
        }//end textFile


        //si on est en chargement index et qu'il n'y a pas de fichiers
        //associés, on charge directement le fichier.
        if (multimediaFile == null && audioFile == null && textFile == null) {
            if (!Utilities.isIndexFile(file)) {
                GuiUtilities.showMessageDialog(owner,
                        String.format(resources.getString("fileFormatNotSupported"), file));
                return;
            }

//            if (!core.loadIndexes(file)) {
//                GuiUtilities.showMessageDialog(owner,
//                        String.format(resources.getString("loadError"), file));
//            }
            return;
        }//end chargement simple index


        String title = resources.getString("loadTitle");
        String messageLigne1 = resources.getString("indexProject");
        String messageLigne2 = resources.getString("loadChoice");

        final ButtonGroup group = new ButtonGroup();
        otherButton = new JRadioButton(resources.getString("filesChoice"));
        allButton = null;
        mediaButton = null;
        audioButton = null;
        textButton = null;
        final JRadioButton noneButton = new JRadioButton(resources.getString("noneFile"));

        if (multimediaFile != null) {
            mediaButton = new JCheckBox(
                    String.format(resources.getString("multimediaFile"),
                    multimediaFile));
            mediaButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    otherButton.setSelected(true);
                }
            });
        }//end multimediaFile

        if (audioFile != null) {
            audioButton = new JCheckBox(
                    String.format(resources.getString("audioFile"), audioFile));
            audioButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    otherButton.setSelected(true);
                }
            });
        }//end audioFile

        if (textFile != null) {
            textButton = new JCheckBox(
                    String.format(resources.getString("textFile"), textFile));
            textButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    otherButton.setSelected(true);
                }
            });
        }//end textFile

        group.add(otherButton);

        if (multimediaFile != null || audioFile != null || textFile != null) {
            allButton = new JRadioButton(resources.getString("allFiles"));
            group.add(allButton);
        }//end all

        group.add(noneButton);

        if (allButton != null) {
            group.setSelected(allButton.getModel(), true);
        } else if (otherButton != null) {
            group.setSelected(otherButton.getModel(), true);
        }

        JLabel label1 = new JLabel(messageLigne1);
        JLabel label2 = new JLabel(messageLigne2);

        JButton validButton = new JButton(resources.getString("valid"));
        JButton cancelButton = new JButton(resources.getString("cancel"));
        JPanel menu = new JPanel();
        menu.add(validButton);
        menu.add(cancelButton);

        final JDialog dialog = new JDialog(owner, title, true);
        dialog.getContentPane().setLayout(
                new BoxLayout(dialog.getContentPane(), BoxLayout.X_AXIS));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(label1);
        mainPanel.add(label2);
        mainPanel.add(Box.createVerticalStrut(20));

        if (otherButton != null) {
            mainPanel.add(otherButton);
        }

        JPanel checkBoxPanel = new JPanel();
        checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
        if (mediaButton != null) {
            checkBoxPanel.add(mediaButton);
        }
        if (audioButton != null) {
            checkBoxPanel.add(audioButton);
        }
        if (textButton != null) {
            checkBoxPanel.add(textButton);
        }

        JPanel offsetPanel = new JPanel();
        offsetPanel.setLayout(new BoxLayout(offsetPanel, BoxLayout.X_AXIS));
        offsetPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
        offsetPanel.add(Box.createHorizontalStrut(20));
        offsetPanel.add(checkBoxPanel);
        mainPanel.add(offsetPanel);

        if (allButton != null) {
            mainPanel.add(allButton);
        }
        if (otherButton != null || audioButton != null || textButton != null) {
            mainPanel.add(noneButton);
        }

        mainPanel.add(menu);
        dialog.getContentPane().add(mainPanel);
        dialog.getContentPane().add(Box.createHorizontalStrut(100));

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                dialog.setVisible(false);
                dialog.dispose();
            }//end actionPerformed(ActionEvent event)
        });//end addActionListener

        validButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                WaitDialog waitDialog = new WaitDialog(dialog,
                        resources.getString("loadTitle"),
                        resources.getString("loadMessage"));
                waitDialog.setVisible(true);

//                //Effacement des éléments précédement chargés
//                core.eraseProject();
//                core.mediaUnload();
//                core.audioErase();
//                core.textErase();
//
//                if (indexesFile != null) {
//                    core.loadIndexes(indexesFile);
//                }
//
//                //chargement des fichiers associés
//                if (otherButton != null && group.isSelected(otherButton.getModel())) {
//                    if (mediaButton != null && mediaButton.isSelected() && multimediaFile != null) {
//                        core.loadMedia(multimediaFile, indexesFile);
//                    }//end mediaButton
//                    if (audioButton != null && audioButton.isSelected() && audioFile != null) {
////                        core.audioLoad(audioFile);
//                    }//end audioButton
//                    if (textButton != null && textButton.isSelected() && textFile != null) {
//                        core.loadText(textFile);
//                    }//end textButton
//                } else if (allButton != null && group.isSelected(allButton.getModel())) {
//                    core.loadMedia(multimediaFile);
//                    if (multimediaFile != null) {
//                        core.loadMedia(multimediaFile, indexesFile);
//                    }//end multimediaFile
//
//                    if (audioFile != null) {
////                        core.audioLoad(audioFile);
//                    }//end audioFile
//
//                    if (textFile != null) {
//                        core.loadText(textFile);
//                    }//end textFile
//                }

                waitDialog.setVisible(false);
                waitDialog.dispose();

                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setLocation(owner.getX(), owner.getY());
        dialog.setVisible(true);
    }
}
