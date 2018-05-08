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
package thot.supervision.application;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.*;

import thot.gui.GuiUtilities;
import thot.gui.Resources;
import thot.utils.Constants;
import thot.utils.Utilities;

/**
 * Boite de dialogue pour l'édition des index.
 *
 * @author fabrice
 * @version 1.8.4
 */
public abstract class ApplicationsDialog extends JDialog {
    private static final long serialVersionUID = 19000L;
    /*
     * Resources textes :
     *  applicationsTitle, editApplicationTitle, applicationsMessage, editMessage,
     *  nameMessage, launchMessage, pathMessage, apply, browse,
     *  addApplication, launchApplication, editApplication, sortApplications,
     *  nameIsEmpty, nameHasBadCharacters, pathNoFile
     */

    /**
     * Liste des applications.
     */
    private ApplicationsList applicationsList;
    /**
     * Resources pour les textes.
     */
    private Resources resources;
    /**
     * Message explicatif.
     */
    private JLabel messageLabel;
    /**
     * Titre pour la sélection de l'application.
     */
    private JLabel checkLabel;
    /**
     * Titre pour le label de l'application.
     */
    private JLabel nameLabel;
    /**
     * Titre pour le lancement de l'application.
     */
    private JLabel launchLabel;
    /**
     * Titre pour l'édition de l'application.
     */
    private JLabel editLabel;
    /**
     * Liste des composants graphique d'une application.
     */
    private List<ApplicationFields> applicationFields;
    /**
     * Bouton pour appliquer les autorisations.
     */
    private JButton applyButton;
    /**
     * Bouton pour ajouter une application.
     */
    private JButton addButton;
    /**
     * Bouton pour trier les application.
     */
    private JButton sortButton;

    /**
     * Largeur des champs du label de l'application.
     */
    private int nameWidth = 100;
    /**
     * Hauteur des champs.
     */
    private int textHeight = 25;
    /**
     * Panneau pour les sélections d'index.
     */
    private JPanel checkPanel;
    /**
     * Panneau pour les temps de départ des index.
     */
    private JPanel namePanel;
    /**
     * Panneau pour les temps de fin des index.
     */
    private JPanel launchPanel;
    /**
     * Panneau pour les types des index.
     */
    private JPanel editPanel;
    private EditDialog editDialog;

    /**
     * Initialisation de la boite de dialogue.
     *
     * @param resources les resources pour les textes.
     * @param applicationsList
     */
    public ApplicationsDialog(Resources resources, ApplicationsList applicationsList) {
        super(null, resources.getString("applicationsTitle"), DEFAULT_MODALITY_TYPE);

        this.resources = resources;
        this.applicationsList = applicationsList;

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setIconImages(GuiUtilities.getIcons());

        editDialog = new EditDialog(this);

        initComponents();
        initListeners();
        applicationsList.sortApplications();
        for (Iterator<Application> it = applicationsList.iterator(); it.hasNext(); ) {
            addApplication(it.next());
        }
    }

    /**
     * Initialisation des composants.
     */
    private void initComponents() {
        messageLabel = new JLabel(resources.getString("applicationsMessage"));
        checkLabel = new JLabel(" ");
        nameLabel = new JLabel(resources.getString("nameMessage"));
        launchLabel = new JLabel(resources.getString("launchMessage"));
        editLabel = new JLabel(resources.getString("editMessage"));

        messageLabel.setAlignmentX(CENTER_ALIGNMENT);
        checkLabel.setAlignmentX(CENTER_ALIGNMENT);
        nameLabel.setAlignmentX(CENTER_ALIGNMENT);
        launchLabel.setAlignmentX(CENTER_ALIGNMENT);
        editLabel.setAlignmentX(CENTER_ALIGNMENT);

        checkPanel = new JPanel();
        checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));
        checkPanel.add(checkLabel);

        namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.add(nameLabel);

        launchPanel = new JPanel();
        launchPanel.setLayout(new BoxLayout(launchPanel, BoxLayout.Y_AXIS));
        launchPanel.add(launchLabel);

        editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
        editPanel.add(editLabel);

        int capacity = applicationsList.getApplicationsCount() + 4;
        applicationFields = new ArrayList<>(capacity);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.add(Box.createHorizontalGlue());
        mainPanel.add(checkPanel);
        mainPanel.add(Box.createHorizontalStrut(5));
        mainPanel.add(namePanel);
        mainPanel.add(Box.createHorizontalStrut(20));
        mainPanel.add(Box.createHorizontalGlue());
        mainPanel.add(launchPanel);
        mainPanel.add(Box.createHorizontalStrut(20));
        mainPanel.add(Box.createHorizontalGlue());
        mainPanel.add(editPanel);
        mainPanel.add(Box.createHorizontalGlue());
        mainPanel.add(Box.createHorizontalStrut(20));

        JScrollPane scrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        applyButton = new JButton(resources.getString("apply"));
        sortButton = new JButton(resources.getString("sortApplications"));
        addButton = new JButton(resources.getString("addApplication"));

        JPanel optionMenu = new JPanel();
        optionMenu.setLayout(new BoxLayout(optionMenu, BoxLayout.X_AXIS));
        optionMenu.add(Box.createHorizontalGlue());
        optionMenu.add(applyButton);
        optionMenu.add(Box.createHorizontalGlue());
        optionMenu.add(sortButton);
        optionMenu.add(Box.createHorizontalGlue());
        optionMenu.add(addButton);
        optionMenu.add(Box.createHorizontalGlue());

        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.getContentPane().add(Box.createVerticalStrut(20));
        this.getContentPane().add(messageLabel);
        this.getContentPane().add(Box.createVerticalStrut(20));
        this.getContentPane().add(scrollPane);
        this.getContentPane().add(Box.createVerticalStrut(20));
        this.getContentPane().add(optionMenu);
        this.getContentPane().add(Box.createVerticalStrut(20));
        this.pack();
    }

    /**
     * Initialisation des actions.
     */
    private void initListeners() {
        applyButton.addActionListener(e -> validAction());

        addButton.addActionListener(e -> {
            Application newApplication = new Application(null, null);
            applicationsList.addApplication(newApplication);
            addApplication(newApplication);
            updateSizeAndLocation();
            editDialog.showDialog(newApplication);
        });

        sortButton.addActionListener(e -> sortApplicationsFields());
    }

    /**
     * Affiche la boite de dialogue.
     */
    public void showDialog() {
        sortApplicationsFields();
        updateSizeAndLocation();
        this.setVisible(true);
    }

    /**
     * Actualiser la taille et la position de la fenêtre.
     */
    private void updateSizeAndLocation() {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

        this.pack();
        int width = getWidth();
        int height = getHeight();

//        if(width < 600)
//            width = 740;
        if (height > screenDim.height - 50) {
            height = screenDim.height - 50;
        }

        this.setSize(new Dimension(width, height));

        this.setLocation((screenDim.width - this.getWidth()) / 4, (screenDim.height - this.getHeight()) / 4);
    }

    /**
     * Met à jour les composants graphiques de l'application concernée.
     *
     * @param application l'application.
     */
    private void updateApplicationFields(Application application) {
        long id = application.getId();
        for (ApplicationFields fields : applicationFields) {
            if (fields.id == id) {
                fields.nameField.setText(application.getName());

                boolean exist = false;
                if (application.getPath() != null) {
                    File file = new File(application.getPath());
                    exist = file.exists();
                }
                if (!exist) {
                    fields.checkBox.setSelected(false);
                }
                fields.checkBox.setEnabled(exist);
                fields.launchButton.setEnabled(exist);

                break;
            }
        }
    }

    /**
     * Enlève les composants graphiques d'une application concernée.
     *
     * @param application l'application.
     */
    private void removeApplicationFields(Application application) {
        long id = application.getId();
        for (ApplicationFields fields : applicationFields) {
            if (fields.id == id) {
                checkPanel.remove(fields.checkBox);
                namePanel.remove(fields.nameField);
                launchPanel.remove(fields.launchButton);
                editPanel.remove(fields.editButton);
                applicationFields.remove(fields);
                applicationsList.removeApplication(application);

                ActionListener[] actions = fields.editButton.getActionListeners();
                for (ActionListener actionListener : actions) {
                    fields.launchButton.removeActionListener(actionListener);
                }

                actions = fields.editButton.getActionListeners();
                for (ActionListener actionListener : actions) {
                    fields.editButton.removeActionListener(actionListener);
                }

                fields.checkBox.removeAll();
                fields.checkBox = null;
                fields.nameField.removeAll();
                fields.nameField = null;
                fields.launchButton.removeAll();
                fields.launchButton = null;
                fields.editButton.removeAll();
                fields.editButton = null;

                updateSizeAndLocation();
                break;
            }
        }
    }

    /**
     * Modifie les textes.
     */
    public void updateLanguage() {
        setTitle(resources.getString("applicationsTitle"));

        messageLabel.setText(resources.getString("applicationsMessage"));
        nameLabel.setText(resources.getString("nameMessage"));
        launchLabel.setText(resources.getString("launchMessage"));
        editLabel.setText(resources.getString("editMessage"));

        applyButton.setText(resources.getString("apply"));
        sortButton.setText(resources.getString("sortApplications"));
        addButton.setText(resources.getString("addApplication"));

        int size = applicationFields.size();

        for (int i = 0; i < size; i++) {
            ApplicationFields fields = applicationFields.get(i);

            fields.launchButton.setText(resources.getString("launchApplication"));
            fields.editButton.setText(resources.getString("editApplication"));
        }

        updateSizeAndLocation();

        editDialog.updateLanguage();
    }

    /**
     * Test la validité des changements et modifie les index en conséquence.
     */
    private void validAction() {
        int size = applicationFields.size();

        for (int i = 0; i < size; i++) {
            ApplicationFields fields = applicationFields.get(i);
            Application application = applicationsList.getApplicationWithId(fields.id);
            application.setAllowed(!fields.checkBox.isSelected());
        }

        setVisible(false);

        applyModifications();
    }

    /**
     * Ajoute les composants graphiques pour une application.
     *
     * @param application l'application.
     */
    private void addApplication(Application application) {
        JCheckBox checkBox = new JCheckBox();
        JTextField nameField = new JTextField(application.getName());
        JButton launchButton = new JButton(resources.getString("launchApplication"));
        JButton editButton = new JButton(resources.getString("editApplication"));

        checkBox.setPreferredSize(new Dimension(textHeight, textHeight));
        checkBox.setMaximumSize(new Dimension(textHeight, textHeight));
        nameField.setPreferredSize(new Dimension(nameWidth, textHeight));
        nameField.setMaximumSize(new Dimension(nameWidth, textHeight));
        launchButton.setAlignmentX(CENTER_ALIGNMENT);
        editButton.setAlignmentX(CENTER_ALIGNMENT);

        checkPanel.add(checkBox);
        namePanel.add(nameField);
        launchPanel.add(launchButton);
        editPanel.add(editButton);

        boolean exist = false;
        if (application.getPath() != null) {
            if (application.getPath().startsWith(Constants.PROGAM_FILES)) {
                String path = application.getPath().substring(Constants.PROGAM_FILES.length());
                File file = Utilities.pathOnWindowsProgramFiles(path);
                exist = file.exists();
                if (exist) {
                    application.setPath(file.getAbsolutePath());
                }
            } else {
                File file = new File(application.getPath());
                exist = file.exists();
            }
        }

        checkBox.setEnabled(exist);
        launchButton.setEnabled(exist);
        nameField.setEditable(false);

        final ApplicationFields fields = new ApplicationFields(
                application.getId(), checkBox, nameField, launchButton, editButton);
        applicationFields.add(fields);

        launchButton.addActionListener(e -> {
            String path = applicationsList.getApplicationWithId(fields.id).getPath();
            launchApplication(path);
        });

        editButton.addActionListener(e -> editDialog.showDialog(applicationsList.getApplicationWithId(fields.id)));
    }

    /**
     * Trie par ordre croissant du temps de départ des index de la table.
     */
    private void sortApplicationsFields() {
        applicationsList.sortApplications();

        int i = 0;
        for (Iterator<Application> it = applicationsList.iterator(); it.hasNext(); ) {
            Application application = it.next();
            ApplicationFields fields = applicationFields.get(i);
            fields.id = application.getId();
            fields.checkBox.setSelected(!application.isAllowed());
            fields.nameField.setText(application.getName());

            boolean exist = false;
            if (application.getPath() != null) {
                File file = new File(application.getPath());
                exist = file.exists();
            }
            fields.checkBox.setEnabled(exist);
            fields.launchButton.setEnabled(exist);

            i++;
        }
    }

    protected abstract void launchApplication(String path);

    protected abstract void applyModifications();

    /**
     * Classe recensant tous les composants graphiques nécessaires pour un index.
     *
     * @version 1.90
     */
    private class ApplicationFields {

        /**
         * id de l'index.
         */
        protected long id;
        /**
         * Champs de sélection des index.
         */
        protected JCheckBox checkBox;
        /**
         * Champs formatés pour les temps de départ des index.
         */
        protected JTextField nameField;
        /**
         * Champ formaté pour le temps de fin de l'index.
         */
        protected JButton launchButton;
        /**
         * Champ non modifiable pour la durée de l'index.
         */
        protected JButton editButton;

        private ApplicationFields(long id, JCheckBox checkBox, JTextField nameField,
                JButton launchButton, JButton editButton) {
            this.id = id;
            this.checkBox = checkBox;
            this.nameField = nameField;
            this.launchButton = launchButton;
            this.editButton = editButton;
        }
    }

    /**
     * Fenêtre principale pour la gestion des propiétés d'une Application.
     *
     * @version 1.90
     */
    private class EditDialog extends JDialog {

        private static final long serialVersionUID = 19000L;
        private Application application;
        private JLabel nameLabel;
        private JLabel pathLabel;
        private JTextField nameTextField;
        private JTextField pathTextField;
        private JButton pathButton;
        private JButton applyButton;

        private EditDialog(JDialog owner) {
            super(owner, resources.getString("editApplicationTitle"), true);
            this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            nameLabel = new JLabel(resources.getString("nameMessage"));
            pathLabel = new JLabel(resources.getString("pathMessage"));
            nameLabel.setAlignmentX(RIGHT_ALIGNMENT);

            nameTextField = new JTextField();
            pathTextField = new JTextField();

            pathButton = new JButton(resources.getString("browse"));
            applyButton = new JButton(resources.getString("apply"));
            applyButton.setAlignmentX(CENTER_ALIGNMENT);

            JPanel pathPanel = new JPanel();
            pathPanel.setLayout(new BoxLayout(pathPanel, BoxLayout.X_AXIS));
            pathPanel.add(pathLabel);
            pathPanel.add(Box.createHorizontalGlue());
            pathPanel.add(pathButton);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.add(Box.createVerticalStrut(20));
            mainPanel.add(nameLabel);
            mainPanel.add(Box.createVerticalStrut(5));
            mainPanel.add(nameTextField);
            mainPanel.add(Box.createVerticalStrut(20));
            mainPanel.add(pathPanel);
            mainPanel.add(Box.createVerticalStrut(5));
            mainPanel.add(pathTextField);
            mainPanel.add(Box.createVerticalStrut(20));
            mainPanel.add(applyButton);
            this.getContentPane().add(mainPanel);
            this.pack();
            this.setSize(300, this.getHeight());

            pathButton.addActionListener(e -> {
                File file = null;
                if (pathTextField.getText() != null) {
                    file = new File(pathTextField.getText());
                }
                JFileChooser fileChooser = new JFileChooser(file);
                fileChooser.setSelectedFile(file);
                int option = fileChooser.showOpenDialog(getContentPane());
                if (option == JFileChooser.APPROVE_OPTION) {
                    file = fileChooser.getSelectedFile();
                    pathTextField.setText(file.getAbsolutePath());
                }
            });

            applyButton.addActionListener(e -> {
                String name = nameTextField.getText();
                String path = pathTextField.getText();

                if (name == null || name.isEmpty()) {
                    showMessage(resources.getString("nameIsEmpty"));
                    return;
                }

                //vérification des caractères spéciaux
                for (int i = 0; i < name.length(); i++) {
                    if (!Character.isLetterOrDigit(name.charAt(i))) {
                        showMessage(resources.getString("nameHasBadCharacters"));
                        return;
                    }
                }

                File file = null;
                if (path != null) {
                    file = new File(path);
                }

                if (file == null || !file.exists()) {
                    int choix = showOptionDialog(resources.getString("pathNoFile"));
                    if (choix != GuiUtilities.YES_OPTION) {
                        return;
                    }
                }

                application.setName(name);

                if (file == null) {
                    application.setPath(null);
                } else {
                    application.setPath(file.getAbsolutePath());
                }

                updateApplicationFields(application);

                setVisible(false);
            });

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (application.getName() == null
                            || application.getName().trim().isEmpty()) {
                        removeApplicationFields(application);
                    }
                }
            });

        }

        private void showDialog(Application application) {
            this.application = application;
            nameTextField.setText(application.getName());
            pathTextField.setText(application.getPath());
            int x = getOwner().getX();
            int y = getOwner().getY();
            this.setLocation(x + 100, y + 100);
            this.setVisible(true);
        }

        private void updateLanguage() {
            setTitle(resources.getString("editApplicationTitle"));

            nameLabel.setText(resources.getString("nameMessage"));
            pathLabel.setText(resources.getString("pathMessage"));

            pathButton.setText(resources.getString("browse"));
            applyButton.setText(resources.getString("apply"));
        }

        private void showMessage(String message) {
            GuiUtilities.showMessageDialog(this, message);
        }

        private int showOptionDialog(String message) {
            return GuiUtilities.showOptionDialog(this, message, null, null);
        }
    }
}
