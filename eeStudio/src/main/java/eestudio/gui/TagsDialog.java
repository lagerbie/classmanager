package eestudio.gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import eestudio.Core;
import thot.gui.GuiUtilities;
import thot.labo.TagList;

/**
 * Boite de dialogue pour redimensionner un index.
 *
 * @author Fabrice Alleau
 * @version 1.01
 * @since version 1.00
 */
public class TagsDialog extends JDialog {
    private static final long serialVersionUID = 10000L;

    /**
     * Resources textuelles
     */
    private Resources resources;
    /**
     * Noyau pour la création des index
     */
    private Core core;

    /**
     * Tags initiaux
     */
    private TagList initTags;

    /**
     * Message explicatif
     */
    private JLabel messageLabel;
    /**
     * Label pour le titre du document
     */
    private JLabel titleLabel;
    /**
     * Label pour le nom de l'artiste
     */
    private JLabel artistLabel;
    /**
     * Label pour le nom de l'album
     */
    private JLabel albumLabel;
    /**
     * Label pour les commentaires
     */
    private JLabel commentLabel;

    /**
     * Champ pour le titre du document
     */
    private JTextField titleField;
    /**
     * Champ pour le nom de l'artiste
     */
    private JTextField artistField;
    /**
     * Champ pour le nom de l'album
     */
    private JTextField albumField;
    /**
     * Champ pour les commentaires
     */
    private JTextField commentField;

    /**
     * Bouton pour valider les changements sur l'index
     */
    private JButton validButton;
    /**
     * Bouton pour annuler les changements sur l'index
     */
    private JButton cancelButton;

    /**
     * Initialisation de la boite de dialogue.
     *
     * @param core le coeur de l'application.
     * @param resources les resources pour les textes.
     * @param parent la fenêtre parente (peut être null).
     *
     * @since version 1.00
     */
    public TagsDialog(Window parent, Core core, Resources resources) {
        super(parent, resources.getString("tagsTitle"), DEFAULT_MODALITY_TYPE);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        this.resources = resources;
        this.core = core;

        initComponents();
        initListener();
    }

    /**
     * Initialise les composants.
     *
     * @since version 1.00 - version 1.01
     */
    private void initComponents() {
        int margin = 20;
        int fieldWidth = 200;
        int fieldHeight = 20;
        int width = fieldWidth + 100 + 2 * margin;
        int height = 500;
        int offset = 60;
        Dimension dim;

        messageLabel = new JLabel(resources.getString("tagsMessage"));
        titleLabel = new JLabel(resources.getString("titleLabel"));
        artistLabel = new JLabel(resources.getString("artistLabel"));
        albumLabel = new JLabel(resources.getString("albumLabel"));
        commentLabel = new JLabel(resources.getString("commentLabel"));

        titleField = new JTextField();
        artistField = new JTextField();
        albumField = new JTextField();
        commentField = new JTextField();

        validButton = GuiUtilities
                .getActionButton(this, GuiUtilities.getImageIcon("valid"), GuiUtilities.getImageIcon("validOff"),
                        resources.getString("valid"));
        cancelButton = GuiUtilities
                .getActionButton(this, GuiUtilities.getImageIcon("cancel"), GuiUtilities.getImageIcon("cancelOff"),
                        resources.getString("cancel"));

        dim = new Dimension(fieldWidth, fieldHeight);
        titleField.setPreferredSize(dim);
        titleField.setMinimumSize(dim);
        titleField.setMaximumSize(dim);
        artistField.setPreferredSize(dim);
        artistField.setMinimumSize(dim);
        artistField.setMaximumSize(dim);
        albumField.setPreferredSize(dim);
        albumField.setMinimumSize(dim);
        albumField.setMaximumSize(dim);
        commentField.setPreferredSize(dim);
        commentField.setMinimumSize(dim);
        commentField.setMaximumSize(dim);

        BackgroundPanel panel = new BackgroundPanel(width, height, 15);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setLayout(layout);

        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(2 * margin, margin, 2 * margin, margin);
        constraints.anchor = GridBagConstraints.BASELINE;
        layout.setConstraints(messageLabel, constraints);
        panel.add(messageLabel);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, margin, 0, margin);
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        layout.setConstraints(titleLabel, constraints);
        panel.add(titleLabel);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0, 0, 0, margin);
        layout.setConstraints(titleField, constraints);
        panel.add(titleField);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, margin, 0, margin);
        layout.setConstraints(artistLabel, constraints);
        panel.add(artistLabel);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0, 0, 0, margin);
        layout.setConstraints(artistField, constraints);
        panel.add(artistField);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, margin, 0, margin);
        layout.setConstraints(albumLabel, constraints);
        panel.add(albumLabel);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0, 0, 0, margin);
        layout.setConstraints(albumField, constraints);
        panel.add(albumField);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, margin, 0, margin);
        layout.setConstraints(commentLabel, constraints);
        panel.add(commentLabel);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0, 0, 0, margin);
        layout.setConstraints(commentField, constraints);
        panel.add(commentField);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, margin, 0, margin);
        constraints.insets = new Insets(2 * margin, margin + offset, 2 * margin, 0);
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        panel.add(validButton);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(2 * margin, 0, 2 * margin, margin + offset);
        constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        layout.setConstraints(cancelButton, constraints);
        panel.add(cancelButton);

        this.getContentPane().add(panel);
        this.pack();
    }

    /**
     * Initialisation des listeners.
     *
     * @since version 1.00
     */
    private void initListener() {
        ActionListener listener = e -> action(e.getSource());

        validButton.addActionListener(listener);
        cancelButton.addActionListener(listener);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                action(e.getSource());
            }
        });
    }

    /**
     * Affiche la boite de dialogue.
     *
     * @param tags la liste des tags initiale.
     *
     * @since version 1.00
     */
    public void showDialog(TagList tags) {
        initValues(tags);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width - this.getWidth()) / 2, (dim.height - this.getHeight()) / 4);
        this.setVisible(true);
    }

    /**
     * Modifie les textes suivant
     *
     * @since version 1.00 - version 1.01
     */
    public void updateLanguage() {
        setTitle(resources.getString("tagsTitle"));

        messageLabel.setText(resources.getString("tagsMessage"));
        titleLabel.setText(resources.getString("titleLabel"));
        artistLabel.setText(resources.getString("artistLabel"));
        albumLabel.setText(resources.getString("albumLabel"));
        commentLabel.setText(resources.getString("commentLabel"));

        validButton.setToolTipText(resources.getString("valid"));
        cancelButton.setToolTipText(resources.getString("cancel"));

        this.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width - getWidth()) / 2, (dim.height - getHeight()) / 2);
    }

    /**
     * Initialise les valeurs initiales et courantes.
     *
     * @param tags la liste initiale des tags.
     *
     * @since version 1.00
     */
    private void initValues(TagList tags) {
        this.initTags = tags;
        titleField.setText(tags.getTag(TagList.TITLE));
        artistField.setText(tags.getTag(TagList.ARTIST));
        albumField.setText(tags.getTag(TagList.ALBUM));
        commentField.setText(tags.getTag(TagList.COMMENT));
    }

    /**
     * Récupère la liste des tags filtrés.
     *
     * @return la liste des tags filtrées (sans valeur null et sans chaîne de caractères vide).
     *
     * @since version 1.00
     */
    private TagList getTags() {
        TagList tags = new TagList();
        String value = titleField.getText();
        if (value != null && !value.trim().isEmpty()) {
            tags.putTag(TagList.TITLE, value);
        }

        value = artistField.getText();
        if (value != null && !value.trim().isEmpty()) {
            tags.putTag(TagList.ARTIST, value);
        }

        value = albumField.getText();
        if (value != null && !value.trim().isEmpty()) {
            tags.putTag(TagList.ALBUM, value);
        }

        value = commentField.getText();
        if (value != null && !value.trim().isEmpty()) {
            tags.putTag(TagList.COMMENT, value);
        }

        return tags;
    }

    /**
     * Sauvegarde l'index.
     *
     * @since version 1.00
     */
    private void saveTags() {
        TagList tags = getTags();
        core.setTags(tags);
    }

    /**
     * Traitement des actions longues dans un thread séparée.
     *
     * @param source la source de l'action.
     *
     * @since version 1.00
     */
    private void action(final Object source) {
        Thread thread = new Thread(() -> {
            if (source == validButton) {
                saveTags();
                setVisible(false);
                dispose();
            } else if (source instanceof TagsDialog) {
                if (hasChanges()) {
                    int option = showCancelableMessage(resources.getString("modifTags"));
                    if (option == GuiUtilities.YES_OPTION) {
                        saveTags();
                    } else if (option != GuiUtilities.NO_OPTION) {
                        return;
                    }
                }
                setVisible(false);
                dispose();
            }
        });
        thread.start();
    }

    /**
     * Retourne les tags ont changés depuis la dernière validation.
     *
     * @return si les tags ont changés.
     *
     * @since version 1.00
     */
    private boolean hasChanges() {
        TagList tags = getTags();
        return !tags.isIndenticTo(initTags);
    }

    /**
     * Affiche un message avec les options oui, non, cancel.
     *
     * @param message le message.
     *
     * @return l'option choisie.
     *
     * @since version 1.00
     */
    private int showCancelableMessage(String message) {
        return GuiUtilities.showOptionDialogWithCancel(this, message, null, null);
    }

//    public static void main(String[] args){
//        GuiUtilities.manageUI();
//        javax.swing.JFrame frame = new javax.swing.JFrame("test");
//        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//        try {
//            Core core = new Core(null);
//            TagsDialog dialog = new TagsDialog(frame, core,
//                    new Resources());
//
//            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                @Override
//                public void windowClosing(java.awt.event.WindowEvent e) {
//                    System.exit(0);
//                }
//            });
//
//            TagList list = new TagList();
//            dialog.showDialog(list);
//        } catch(Exception e) {
//            eestudio.utils.Edu4Logger.error(e);
//        }
//    }

}
