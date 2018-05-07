package eestudio.gui;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

/*
 * v0.95.10: ajout de private JButton loadButton;
 * v0.95.10: ajout de private JButton eraseButton;
 * v0.95.10: ajout de private JLabel messageLabel;
 * v0.95.10: ajout de public void addLoadActionListener(ActionListener listener)
 * v0.95.10: ajout de public void addEraseActionListener(ActionListener listener)
 * v0.95.10: supp de public String getText()
 * v0.95.10: modif de public TextEditorWindow(...) [add bouton, message, titre,
 *           supp resizable, supp default close]
 * 
 * v0.95.11: modif de private int textEditorWidth de 335 -> 350
 * v0.95.11: modif de TextEditorWindow(...) [use GuiUtilities.getActionButton]
 * 
 * v0.95.12: ajout de private Resources resources;
 * v0.95.12: modif de TextEditorWindow(..., ResourceBundle images,
 *           ResourceBundle texts) en TextEditorWindow(..., Resources resources)
 * v0.95.12: modif de updateTexts(ResourceBundle texts) en updateLanguage()
 *           [ajouts de boutons, message, titre]
 * 
 * v0.96: modif de TextEditorWindow(...) [ajout de this.setSize(..) après this.pack();
 * 
 * v0.99: modif de TextEditorWindow(...) [GuiUtilities.getImageIcon]
 * 
 * v1.01: supp de private int textEditorWidth = 350;
 * v1.01: supp de private int textEditorHeight = 472;
 * v1.01: modif de TextEditorWindow(...) [GridBagLayout, BackgroundPanel]
 */

/**
 * Fenêtre d'édition du texte.
 * 
 * @author Fabrice Alleau
 * @since version 0.94
 * @version 1.01
 */
public class TextEditorWindow extends JFrame {
    private static final long serialVersionUID = 10100L;

    /** Resources textuelles */
    private Resources resources;

    /** Zone de texte */
    private EditorArea textArea;

    /** Bouton de chargement de fichiers */
    private JLabel messageLabel;
    /** Bouton de chargement de fichiers */
    private JButton loadButton;
    /** Bouton pour l'effacement du texte */
    private JButton eraseButton;

    /**
     * Initialisation de la fenêtre.
     * 
     * @param editorKit l'éditeur du texte.
     * @param styledDocument le document associé.
     * @param resources les resources pour les textes.
     * @since version 0.94 - version 1.01
     */
    public TextEditorWindow(StyledEditorKit editorKit, StyledDocument styledDocument,
            Resources resources) {
        super(resources.getString("editTextTitle"));

        this.resources = resources;

        int textEditorWidth = 350;
        int textEditorHeight = 472;
        int margin = 20;
        int width = textEditorWidth + 2*margin;
        int height = textEditorHeight + 100;

        textArea = new EditorArea(this, editorKit, styledDocument, resources,
                textEditorWidth, textEditorHeight);

        messageLabel = new JLabel(resources.getString("editTextMessage"));

        loadButton = GuiUtilities.getActionButton(getContentPane(),
                GuiUtilities.getImageIcon("open"),
                GuiUtilities.getImageIcon("openOff"),
                resources.getString("openText"));
        eraseButton = GuiUtilities.getActionButton(getContentPane(),
                GuiUtilities.getImageIcon("textErase"),
                GuiUtilities.getImageIcon("textEraseOff"),
                resources.getString("eraseText"));

        BackgroundPanel panel = new BackgroundPanel(width, height, 15);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setLayout(layout);

        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.anchor = GridBagConstraints.BASELINE;

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(margin, margin, 0, margin);
        layout.setConstraints(messageLabel, constraints);
        panel.add(messageLabel);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, margin, 0, margin);
        layout.setConstraints(loadButton, constraints);
        panel.add(loadButton);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(eraseButton, constraints);
        panel.add(eraseButton);

        constraints.weighty = 1.0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0, margin, margin, margin);
        constraints.fill = GridBagConstraints.BOTH;
        layout.setConstraints(textArea, constraints);
        panel.add(textArea);

        this.getContentPane().add(panel);
        this.pack();
    }

    /**
     * Mise à jour des textes.
     * 
     * @since version 0.94 - version 0.95.12
     */
    public void updateLanguage() {
        textArea.updateLanguage();
        this.setTitle(resources.getString("editTextTitle"));
        messageLabel.setText(resources.getString("editTextMessage"));
        loadButton.setToolTipText(resources.getString("openText"));
        eraseButton.setToolTipText(resources.getString("eraseText"));
    }

    /**
     * Affichage de la fenêtre.
     * 
     * @param visible l'état visible de la fenêtre.
     * @since version 0.94
     */
    public void showWindow(boolean visible) {
        if(this.isVisible() != visible)
            this.setVisible(visible);

        if(visible) {
            this.toFront();
        }
    }

    /**
     * Modification du texte au format brut.
     * 
     * @param text le texte brut.
     * @since version 0.94
     */
    public void setText(String text) {
        textArea.setText(text);
    }

    /**
     * Ajout un ActionListener sur le bouton de chargement de fichiers.
     * 
     * @param listener l'ActionListener.
     * @since version 0.95.10
     */
    public void addLoadActionListener(ActionListener listener) {
        loadButton.addActionListener(listener);
    }

    /**
     * Ajout un ActionListener sur le bouton d'effacement du texte.
     * 
     * @param listener l'ActionListener.
     * @since version 0.95.10
     */
    public void addEraseActionListener(ActionListener listener) {
        eraseButton.addActionListener(listener);
    }

//    public static void main(String[] args){
//        GuiUtilities.manageUI();
//        try {
//            eestudio.Core core = new eestudio.Core(null);
//            TextEditorWindow dialog = new TextEditorWindow(
//                    core.getStyledEditorKit(), core.getStyledDocument(),
//                    new Resources());
//
//            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                @Override
//                public void windowClosing(java.awt.event.WindowEvent e) {
//                    System.exit(0);
//                }
//            });
//
//            dialog.showWindow(true);
//        } catch(Exception e) {
//            eestudio.utils.Edu4Logger.error(e);
//        }
//    }

}//end
