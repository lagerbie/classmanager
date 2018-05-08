package eestudio.gui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Utilities;
import javax.swing.undo.UndoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Zone de texte dans le style de format RTF ou HTML.
 *
 * @author Fabrice Alleau
 */
public class EditorArea extends JPanel {
    private static final long serialVersionUID = 9512L;

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EditorArea.class);

    /**
     * Resources textuelles
     */
    private Resources resources;

    /**
     * Hauteur de la barre de menu
     */
    private final int menuHeight = 24;

    /**
     * Bouton pour le Gras
     */
    private JButton boldButton;
    /**
     * Bouton pour l'Italic
     */
    private JButton italicButton;
    /**
     * Bouton pour le Souligné
     */
    private JButton underLineButton;
    /**
     * Bouton pour le Barré
     */
    private JButton strikeThroughButton;

//    /** Bouton pour l'alignement à gauche (pas présent sur l'interface) */
//    private JButton leftAlignButton;
//    /** Bouton pour l'alignement à droite (pas présent sur l'interface) */
//    private JButton rigthAlignButton;
//    /** Bouton pour l'alignement au centre (pas présent sur l'interface) */
//    private JButton centerAlignButton;

    /**
     * Bouton pour la sélection de taille (pas présent sur l'interface)
     */
    private JComboBox sizeComboBox;
//    /** Bouton pour la sélection de Font (pas présent sur l'interface) */
//    private JComboBox fontComboBox;

    /**
     * Titre pour la sélection de couleur
     */
    private String colorTitle;
    /**
     * Bouton dans la barre affichant la couleur
     */
    private JPanel colorPanel;
    /**
     * Zone de texte
     */
    private JTextPane textPane;

    /**
     * Gestion du Undo/Redo
     */
    private UndoManager undoManager;

    /**
     * Pour indiquer que le programme met à jour les composants graphiques
     */
    private boolean update = false;

    /**
     * Initialisation avec le l'éditeur et le document.
     *
     * @param parent la fenêtre parente.
     * @param editorKit l'éditeur pour le texte.
     * @param styledDocument le document qui sauvegarde le texte.
     * @param resources les resources pour les textes.
     * @param width la largeur de la zone de texte.
     * @param height la hauteur de la zone de texte.
     */
    public EditorArea(Window parent, StyledEditorKit editorKit, StyledDocument styledDocument, Resources resources,
            int width, int height) {
        super();
        this.resources = resources;

        initComponents(parent, width, height);

        textPane.setEditorKit(editorKit);
        textPane.setDocument(styledDocument);
        undoManager = new UndoManager();
        styledDocument.addUndoableEditListener(undoManager);

        initListeners();
    }

    /**
     * Initialisation des composants graphiques.
     *
     * @param parent la fenêtre parente.
     * @param width la largeur de la zone de texte.
     * @param height la hauteur de la zone de texte.
     */
    private void initComponents(Window parent, int width, int height) {
        Dimension dim;

        colorTitle = resources.getString("colorTitle");

        boldButton = GuiUtilities
                .getSelectableButton(parent, GuiUtilities.getImageIcon("bold"), GuiUtilities.getImageIcon("boldOff"),
                        resources.getString("bold"));
        italicButton = GuiUtilities.getSelectableButton(parent, GuiUtilities.getImageIcon("italic"),
                GuiUtilities.getImageIcon("italicOff"), resources.getString("italic"));
        underLineButton = GuiUtilities.getSelectableButton(parent, GuiUtilities.getImageIcon("underLine"),
                GuiUtilities.getImageIcon("underLineOff"), resources.getString("underLine"));
        strikeThroughButton = GuiUtilities.getSelectableButton(parent, GuiUtilities.getImageIcon("strikeThrough"),
                GuiUtilities.getImageIcon("strikeThroughOff"), resources.getString("strikeThrough"));

//        leftAlignButton = new JButton("Left Align");
//        centerAlignButton = new JButton("center Align");
//        rigthAlignButton = new JButton("Rigth Align");

//        fontComboBox = new JComboBox();
//        fontComboBox.setPreferredSize(new Dimension(200, menuHeight));
//        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
//                .getAvailableFontFamilyNames();
//        for(int i=0; i<fonts.length; i++ ) {
//            fontComboBox.addItem(fonts[i]);
//        }

        sizeComboBox = new JComboBox();
        sizeComboBox.addItem(new Integer(8));
        sizeComboBox.addItem(new Integer(10));
        sizeComboBox.addItem(new Integer(12));
        sizeComboBox.addItem(new Integer(14));
        sizeComboBox.addItem(new Integer(18));
        sizeComboBox.addItem(new Integer(24));
        sizeComboBox.addItem(new Integer(36));

        dim = new Dimension(64, menuHeight);
        sizeComboBox.setPreferredSize(dim);

        colorPanel = new JPanel();
        colorPanel.setBackground(Color.BLACK);
        dim = new Dimension(menuHeight, menuHeight);
        colorPanel.setPreferredSize(dim);

        textPane = new JTextPane();
        JScrollPane textScrollPane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textScrollPane.setWheelScrollingEnabled(true);
        textScrollPane.setBorder(null);

        dim = new Dimension(width - 16, height - menuHeight - 8);
        textPane.setPreferredSize(dim);
        dim = new Dimension(width, height - menuHeight);
        textScrollPane.setPreferredSize(dim);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        this.setLayout(layout);

        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.anchor = GridBagConstraints.BASELINE;
        constraints.gridwidth = 1;

//        this.add(fontComboBox);
        layout.setConstraints(boldButton, constraints);
        this.add(boldButton);
        layout.setConstraints(italicButton, constraints);
        this.add(italicButton);
        layout.setConstraints(underLineButton, constraints);
        this.add(underLineButton);
        layout.setConstraints(strikeThroughButton, constraints);
        this.add(strikeThroughButton);
//        this.add(leftAlignButton);
//        this.add(centerAlignButton);
//        this.add(rigthAlignButton);
        constraints.anchor = GridBagConstraints.BELOW_BASELINE;
        layout.setConstraints(sizeComboBox, constraints);
        this.add(sizeComboBox);
        constraints.anchor = GridBagConstraints.BASELINE;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(colorPanel, constraints);
        this.add(colorPanel);

        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        layout.setConstraints(textScrollPane, constraints);
        this.add(textScrollPane);
    }

    /**
     * Met à jour les textes pour le changement de langues.
     */
    public void updateLanguage() {
        boldButton.setToolTipText(resources.getString("bold"));
        italicButton.setToolTipText(resources.getString("italic"));
        underLineButton.setToolTipText(resources.getString("underLine"));
        strikeThroughButton.setToolTipText(resources.getString("strikeThrough"));

        colorTitle = resources.getString("colorTitle");
    }

    /**
     * Ajoute les listeners des boutons.
     */
    private void initListeners() {
        //pour éviter d'avoir un reste d'image quand le programme affiche ou masque la liste
        sizeComboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                getParent().repaint();
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                getParent().repaint();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        boldButton.addActionListener(
                e -> changeAttributes(!boldButton.isSelected(), null, null, null, null, null, null, null));

        italicButton.addActionListener(
                e -> changeAttributes(null, !italicButton.isSelected(), null, null, null, null, null, null));

        underLineButton.addActionListener(
                e -> changeAttributes(null, null, !underLineButton.isSelected(), null, null, null, null, null));

        strikeThroughButton.addActionListener(
                e -> changeAttributes(null, null, null, !strikeThroughButton.isSelected(), null, null, null, null));

//        leftAlignButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                changeAttributes(null, null, null, null, null, null, null,
//                        StyleConstants.ALIGN_LEFT);
//            }
//        });
//
//        centerAlignButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                changeAttributes(null, null, null, null, null, null, null,
//                        StyleConstants.ALIGN_CENTER);
//            }
//        });
//
//        rigthAlignButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                changeAttributes(null, null, null, null, null, null, null,
//                        StyleConstants.ALIGN_RIGHT);
//            }
//        });

        colorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(colorPanel, colorTitle,
                        colorPanel.getBackground());

                if (color != null) {
                    colorPanel.setBackground(color);
                    changeAttributes(null, null, null, null, null, color, null, null);
                }
            }
        });

//        fontComboBox.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                changeAttributes(null, null, null, null,
//                        null, null, (String) fontComboBox.getSelectedItem(), null);
//            }
//        });

        sizeComboBox.addActionListener(
                e -> changeAttributes(null, null, null, null, (Integer) sizeComboBox.getSelectedItem(), null, null,
                        null));

        textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                updateToolsPanel();
            }
        });

        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_HOME:
                    case KeyEvent.VK_END:
                    case KeyEvent.VK_PAGE_UP:
                    case KeyEvent.VK_PAGE_DOWN:
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_BEGIN:
                    case KeyEvent.VK_KP_LEFT:
                    case KeyEvent.VK_KP_UP:
                    case KeyEvent.VK_KP_RIGHT:
                    case KeyEvent.VK_KP_DOWN:
                        updateToolsPanel();
                        break;
                    case KeyEvent.VK_Z:
                        if (e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && undoManager.canUndo()) {
                            undoManager.undo();
                        }
                        break;
                    case KeyEvent.VK_Y:
                        if (e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK && undoManager.canRedo()) {
                            undoManager.redo();
                        }
                        break;
                }
            }
        });
    }

    /**
     * Raffraichissement de la barre des composants.
     */
    private void updateToolsPanel() {
        update = true;
        AttributeSet attributeSet = textPane.getInputAttributes();

        boldButton.setSelected(StyleConstants.isBold(attributeSet));
        italicButton.setSelected(StyleConstants.isItalic(attributeSet));
        strikeThroughButton.setSelected(StyleConstants.isStrikeThrough(attributeSet));
        underLineButton.setSelected(StyleConstants.isUnderline(attributeSet));

        colorPanel.setBackground(StyleConstants.getForeground(attributeSet));

//        fontComboBox.setSelectedItem(StyleConstants.getFontFamily(attributeSet));
        sizeComboBox.setSelectedItem(new Integer(StyleConstants.getFontSize(attributeSet)));

//        attributeSet = textPane.getParagraphAttributes();
//        leftAlignButton.setSelected(StyleConstants.getAlignment(attributeSet)==StyleConstants.ALIGN_LEFT);
//        rigthAlignButton.setSelected(StyleConstants.getAlignment(attributeSet)==StyleConstants.ALIGN_RIGHT);
//        centerAlignButton.setSelected(StyleConstants.getAlignment(attributeSet)==StyleConstants.ALIGN_CENTER);
        getParent().repaint();
        update = false;
    }

    /**
     * Modifie les les attributs du texte sélectionné.
     *
     * @param fontName le nom de la nouvelle police ou <code>null</code>.
     * @param bold la définition du gras ou <code>null</code>.
     * @param italic la définition de l'italique ou <code>null</code>.
     * @param underline la définition de sous-lignement ou <code>null</code>.
     * @param strikeThrough la définition du barré ou <code>null</code>.
     * @param size la nouvelle taille ou <code>null</code>.
     * @param color la nouvelle couleur ou <code>null</code>.
     * @param alignment le nouvel alignement ou <code>null</code>.
     */
    private void changeAttributes(Boolean bold, Boolean italic, Boolean underline, Boolean strikeThrough, Integer size,
            Color color, String fontName, Integer alignment) {
        if (update) {
            return;
        }

        selectWords();
        MutableAttributeSet mutableAttributeSet = textPane.getInputAttributes();

        // on applique les différents styles
        if (fontName != null) {
            StyleConstants.setFontFamily(mutableAttributeSet, fontName);
        }

        if (bold != null) {
            StyleConstants.setBold(mutableAttributeSet, bold);
        }
        if (italic != null) {
            StyleConstants.setItalic(mutableAttributeSet, italic);
        }
        if (underline != null) {
            StyleConstants.setUnderline(mutableAttributeSet, underline);
        }
        if (strikeThrough != null) {
            StyleConstants.setStrikeThrough(mutableAttributeSet, strikeThrough);
        }

        if (size != null) {
            StyleConstants.setFontSize(mutableAttributeSet, size);
        }
        if (color != null) {
            StyleConstants.setForeground(mutableAttributeSet, color);
        }

        textPane.setCharacterAttributes(mutableAttributeSet, false);

        if (alignment != null) {
            StyleConstants.setAlignment(mutableAttributeSet, alignment);
            textPane.getStyledDocument().setParagraphAttributes(textPane.getSelectionStart(),
                    textPane.getSelectionEnd() - textPane.getSelectionStart(), mutableAttributeSet, false);
        }

        updateToolsPanel();
    }

    /**
     * Remplace le texte de la fenêtre.
     *
     * @param text le nouveau texte.
     */
    public void setText(String text) {
        textPane.selectAll();
        textPane.replaceSelection(text);
        //pour remettre le curseur au début et non à la fin.
        textPane.setCaretPosition(0);
    }

    /**
     * Selectionne le mot situé à l'index fourni et retourne l'index de fin.
     */
    private void selectWords() {
        int start = textPane.getSelectionStart();
        int end = textPane.getSelectionEnd();

        //met le focus sur la zone de texte
        textPane.grabFocus();

        try {
            start = Utilities.getWordStart(textPane, start);
            end = Utilities.getWordEnd(textPane, end);
            textPane.select(start, end);
        } catch (BadLocationException e) {
            LOGGER.error("", e);
        }
    }

}
