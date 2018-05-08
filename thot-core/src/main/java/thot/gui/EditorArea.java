package thot.gui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Utilities;
import javax.swing.undo.UndoManager;

/*
 * resources:
 *  bold, italic, underLine, strikeThrough, colorTitle
 */

/**
 * Zone de texte dans un format stylé.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class EditorArea extends JPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Hauteur de la barre de menu.
     */
    private final int menuHeight = 24;
    /**
     * Bouton pour le Gras.
     */
    private JButton boldButton;
    /**
     * Bouton pour l'Italique.
     */
    private JButton italicButton;
    /**
     * Bouton pour le Souligné.
     */
    private JButton underLineButton;
    /**
     * Bouton pour le Barré.
     */
    private JButton strikeThroughButton;
//    /** Bouton pour l'alignement à gauche (pas présent sur l'interface) */
//    private JButton leftAlignButton;
//    /**
//     * Bouton pour l'alignement à droite (pas présent sur l'interface)
//     */
//    private JButton rigthAlignButton;
//    /**
//     * Bouton pour l'alignement au centre (pas présent sur l'interface)
//     */
//    private JButton centerAlignButton;
//    /**
//     * Bouton pour la sélection de taille (pas présent sur l'interface)
//     */
//    private JComboBox sizeComboBox;
//    /**
//     * Bouton pour la sélection de Font (pas présent sur l'interface)
//     */
//    private JComboBox fontComboBox;

    /**
     * Titre pour la sélection de couleur.
     */
    private String colorTitle;
    /**
     * Bouton dans la barre affichant la couleur.
     */
    private JPanel colorPanel;
    /**
     * Zone de texte.
     */
    private JTextPane textPane;
    /**
     * Fenêtre avec une barre de défilement.
     */
    private JScrollPane textScrollPane;
    /**
     * Gestion du Undo/Redo.
     */
    private UndoManager undoManager;

    /**
     * Initialisation avec le l'éditeur et le document.
     *
     * @param editorKit l'éditeur pour le texte.
     * @param styledDocument le document qui sauvegarde le texte.
     * @param resources référence pour les textes des boutons.
     */
    public EditorArea(StyledEditorKit editorKit, StyledDocument styledDocument, Resources resources) {
        super();

        colorTitle = resources.getString("colorTitle");

        boldButton = getButton(resources, "bold", false);
        italicButton = getButton(resources, "italic", false);
        underLineButton = getButton(resources, "underLine", false);
        strikeThroughButton = getButton(resources, "strikeThrough", false);

//        leftAlignButton = new JButton("Left Align");
//        centerAlignButton = new JButton("center Align");
//        rigthAlignButton = new JButton("Rigth Align");
//
//        fontComboBox = new JComboBox();
//        fontComboBox.setPreferredSize(new Dimension(200, menuHeight));
//        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment()
//                .getAvailableFontFamilyNames();
//        for (int i = 0; i < fonts.length; i++) {
//            fontComboBox.addItem(fonts[i]);
//        }
//
//        sizeComboBox = new JComboBox();
//        for (int i = 6; i <= 40; i += 2) {
//            sizeComboBox.addItem(new Integer(i).toString());
//        }
//        sizeComboBox.setPreferredSize(new Dimension(64, menuHeight));

        colorPanel = new JPanel();
        colorPanel.setPreferredSize(new Dimension(menuHeight, menuHeight));
        colorPanel.setMaximumSize(new Dimension(menuHeight, menuHeight));
        //colorPanel.setLayout(new BoxLayout(colorPanel, BoxLayout.X_AXIS));
        colorPanel.setBackground(Color.BLACK);

        textPane = new JTextPane();
        textPane.setPreferredSize(new Dimension(200, 400));
        textPane.setEditorKit(editorKit);
        textPane.setDocument(styledDocument);

        undoManager = new UndoManager();
        styledDocument.addUndoableEditListener(undoManager);

        JToolBar tools = new JToolBar();
//        tools.add(fontComboBox);
//        tools.addSeparator();
        tools.add(boldButton);
        tools.add(italicButton);
        tools.add(underLineButton);
        tools.add(strikeThroughButton);
//        tools.addSeparator();
//        tools.add(leftAlignButton);
//        tools.add(centerAlignButton);
//        tools.add(rigthAlignButton);
//        tools.addSeparator();
//        tools.add(sizeComboBox);
        tools.addSeparator();
        tools.add(colorPanel);
        tools.addSeparator();
        tools.setFloatable(false);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // RTF barre de format
//        this.add(tools);
        textScrollPane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//        textScrollPane.setMinimumSize(new Dimension(335, 400));
//        textScrollPane.setPreferredSize(new Dimension(335, 480));//450
//        textScrollPane.setMaximumSize(new Dimension(335, 480));//450
        textScrollPane.setBorder(null);
        this.add(textScrollPane);

        addListeners();
    }

    /**
     * Modifie les dimensions de la fenêtre texte.
     *
     * @param width la largeur.
     * @param heigth la hauteur.
     */
    public void setSizeMax(int width, int heigth) {
        textPane.setPreferredSize(new Dimension(width - 20, heigth));
        textScrollPane.setMinimumSize(new Dimension(width, heigth));
        textScrollPane.setPreferredSize(new Dimension(width, heigth));
        textScrollPane.setMaximumSize(new Dimension(width, heigth));
    }

    /**
     * Créer un bouton selon le type avec les images et le texte voulus.
     *
     * @param resources référence pour les textes des boutons.
     * @param type le type du bouton : bolt, italic, underLine, strikeThrough.
     * @param animation pour savoir si il y a des effets sur le bouton (unused).
     *
     * @return le bouton créé.
     */
    private JButton getButton(Resources resources, String type, boolean animation) {
        JButton button = new JButton(resources.getString(type));
//        String imagesPath = images.getString("imagesPath");
//        JButton button = new JButton(
//                new ImageIcon(getClass().getClassLoader().getResource(imagesPath + images.getString(type + "Image"))));
//
//        button.setMargin(new Insets(0, 0, 0, 0));
//
//        if (animation) {
//            button.setDisabledIcon(new ImageIcon(
//                    getClass().getClassLoader().getResource(imagesPath + images.getString(type + "ImageOff"))));
//
//            button.setRolloverIcon(new ImageIcon(
//                    getClass().getClassLoader().getResource(imagesPath + images.getString(type + "ImageSurvol"))));
//        }
//
//        button.setBorderPainted(false);
//        button.setContentAreaFilled(false);
//        button.setFocusPainted(false);
//        button.setOpaque(true);
//        Color color = new Color(0, 0, 0, 0);
//        button.setBackground(color);
//        button.setForeground(color);

        button.setToolTipText(resources.getString(type));

        return button;
    }

    /**
     * Met à jour les textes suivant la resource indiquée.
     *
     * @param resources référence pour les textes des boutons.
     */
    public void updateTexts(Resources resources) {
        boldButton.setToolTipText(resources.getString("bold"));
        italicButton.setToolTipText(resources.getString("italic"));
        underLineButton.setToolTipText(resources.getString("underLine"));
        strikeThroughButton.setToolTipText(resources.getString("strikeThrough"));

        colorTitle = resources.getString("colorTitle");

        //pas d'images -> modifications du texte affiché sur les boutons
        boldButton.setText(resources.getString("bold"));
        italicButton.setText(resources.getString("italic"));
        underLineButton.setText(resources.getString("underLine"));
        strikeThroughButton.setText(resources.getString("strikeThrough"));
    }

    /**
     * Ajoute les listeners des boutons.
     */
    private void addListeners() {
        boldButton.addActionListener(e -> changeAttributes(null, !boldButton.isSelected(), null, null,
                null, null, null, null));

        italicButton.addActionListener(e -> changeAttributes(null, null, !italicButton.isSelected(), null,
                null, null, null, null));

        underLineButton.addActionListener(e -> changeAttributes(null, null, null, !underLineButton.isSelected(),
                null, null, null, null));

        strikeThroughButton.addActionListener(e -> changeAttributes(null, null, null, null,
                !strikeThroughButton.isSelected(), null, null, null));

//        leftAlignButton.addActionListener(
//                e -> changeAttributes(null, null, null, null, null, null, null, StyleConstants.ALIGN_LEFT));
//
//        centerAlignButton.addActionListener(
//                e -> changeAttributes(null, null, null, null, null, null, null, StyleConstants.ALIGN_CENTER));
//
//        rigthAlignButton.addActionListener(
//                e -> changeAttributes(null, null, null, null, null, null, null, StyleConstants.ALIGN_RIGHT));

        colorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(textPane, colorTitle,
                        colorPanel.getBackground());
//                if(System.getProperty("os.name").toLowerCase().contains("linux")) {
//                    color = AWTOptionPane.showColorChooser(null, colorTitle, colorPanel.getBackground());
//                } else {
//                    color = JColorChooser.showDialog(textPane, colorTitle, colorPanel.getBackground());
//                }

                if (color != null) {
                    colorPanel.setBackground(color);
                    changeAttributes(null, null, null, null, null, null, color,
                            null);
                }
            }
        });

//        fontComboBox.addItemListener((ItemListener) e -> {
//            if (e.getStateChange() == ItemEvent.SELECTED) {
//                changeAttributes((String) fontComboBox.getSelectedItem(), null, null, null, null, null, null, null);
//            }
//        });
//
//        sizeComboBox.addItemListener((ItemListener) e -> {
//            if (e.getStateChange() == ItemEvent.SELECTED) {
//                changeAttributes(null, null, null, null, null,
//                        labo.utils.Utilities.parseStringAsInt((String) sizeComboBox.getSelectedItem()), null, null);
//            }
//        });
//
//        textPane.addCaretListener(e -> updateToolsPanel());

        textPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) {
                    if (e.getKeyCode() == KeyEvent.VK_Z && undoManager.canUndo()) {
                        undoManager.undo();
                    } else if (e.getKeyCode() == KeyEvent.VK_Y && undoManager.canRedo()) {
                        undoManager.redo();
                    }
                }
            }
        });
    }

    /**
     * Raffraichissement de la barre des composants.
     */
    private void updateToolsPanel() {
        AttributeSet attributeSet = textPane.getCharacterAttributes();

        boldButton.setSelected(StyleConstants.isBold(attributeSet));
        italicButton.setSelected(StyleConstants.isItalic(attributeSet));
        strikeThroughButton.setSelected(StyleConstants.isStrikeThrough(attributeSet));
        underLineButton.setSelected(StyleConstants.isUnderline(attributeSet));

        if (boldButton.isSelected()) {
            boldButton.setBackground(Color.CYAN);
        } else {
            boldButton.setBackground(Color.LIGHT_GRAY);
        }

        if (italicButton.isSelected()) {
            italicButton.setBackground(Color.CYAN);
        } else {
            italicButton.setBackground(Color.LIGHT_GRAY);
        }

        if (strikeThroughButton.isSelected()) {
            strikeThroughButton.setBackground(Color.CYAN);
        } else {
            strikeThroughButton.setBackground(Color.LIGHT_GRAY);
        }

        if (underLineButton.isSelected()) {
            underLineButton.setBackground(Color.CYAN);
        } else {
            underLineButton.setBackground(Color.LIGHT_GRAY);
        }

        colorPanel.setBackground(StyleConstants.getForeground(attributeSet));

//        fontComboBox.setSelectedItem(StyleConstants.getFontFamily(attributeSet));
//        sizeComboBox.setSelectedItem(new Integer(StyleConstants.getFontSize(attributeSet)).toString());
//
//        attributeSet = textPane.getParagraphAttributes();
//        leftAlignButton.setSelected(StyleConstants.getAlignment(attributeSet) == StyleConstants.ALIGN_LEFT);
//        rigthAlignButton.setSelected(StyleConstants.getAlignment(attributeSet) == StyleConstants.ALIGN_RIGHT);
//        centerAlignButton.setSelected(StyleConstants.getAlignment(attributeSet) == StyleConstants.ALIGN_CENTER);
    }

    /**
     * Modifie les les attributs du texte sélectionné.
     *
     * @param fontName le nom de la nouvelle police ou {@code null}.
     * @param bold la définition du gras ou {@code null}.
     * @param italic la définition de l'italique ou {@code null}.
     * @param underline la définition de sous-lignement ou {@code null}.
     * @param strikeThrough la définition du barré ou {@code null}.
     * @param size la nouvelle taille ou {@code null}.
     * @param color la nouvelle couleur ou {@code null}.
     * @param alignment le nouvel alignement ou {@code null}.
     */
    private void changeAttributes(String fontName, Boolean bold, Boolean italic, Boolean underline,
            Boolean strikeThrough, Integer size, Color color, Integer alignment) {

        boolean allText = (textPane.getSelectedText() != null);

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

        if (size != null) {
            StyleConstants.setFontSize(mutableAttributeSet, size);
        }

        if (color != null) {
            StyleConstants.setForeground(mutableAttributeSet, color);
        }

        if (underline != null) {
            StyleConstants.setUnderline(mutableAttributeSet, underline);
        }

        if (strikeThrough != null) {
            StyleConstants.setStrikeThrough(mutableAttributeSet, strikeThrough);
        }

        StyledDocument styledDocument = textPane.getStyledDocument();

        if (!allText) {
            int start = textPane.getSelectionStart();
            int end = textPane.getSelectionEnd();
            styledDocument.setCharacterAttributes(start, end - start, mutableAttributeSet, false);
        } else {
            textPane.setCharacterAttributes(mutableAttributeSet, false);
        }

        if (alignment != null) {
            StyleConstants.setAlignment(mutableAttributeSet, alignment);
            styledDocument.setParagraphAttributes(textPane.getSelectionStart(),
                    textPane.getSelectionEnd() - textPane.getSelectionStart(), mutableAttributeSet, false);
        }

        updateToolsPanel();
    }

    /**
     * Retourne le texte de la fenêtre.
     *
     * @return le texte de la fenêtre.
     */
    public String getText() {
        textPane.selectAll();
        return textPane.getSelectedText();
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
     * Modifie la position du curseur.
     *
     * @param position la nouvelle position.
     */
    public void setCaretPosition(int position) {
        textPane.setCaretPosition(position);
    }

    /**
     * Retourne la position du curseur.
     *
     * @return la position du curseur.
     */
    public int getCaretPosition() {
        return textPane.getCaretPosition();
    }

    /**
     * Modifie la couleur du texte sélectionné.
     *
     * @param color la couleur du texte sélectionné.
     */
    public void setSelectedTextColor(Color color) {
        textPane.setSelectedTextColor(color);
    }

    /**
     * Modifie la couleur du fond de la sélection.
     *
     * @param color la couleur du fond de la sélection.
     */
    public void setSelectionColor(Color color) {
        textPane.setSelectionColor(color);
    }

    @Override
    public void grabFocus() {
        //Override du grabFocus() pour donner le focus à la zone de texte.
        textPane.grabFocus();
    }

    @Override
    public void setFont(Font font) {
        if (textPane != null) {
            textPane.setFont(font);
        }
    }

    /**
     * Retourne le nombre de caractère du texte.
     *
     * @return le nombre de caractère du texte.
     */
    public int getLength() {
        //textArea.getLineEndOffset(textArea.getLineCount()-1);
        return textPane.getDocument().getLength();
    }

    /**
     * Selectionne le mot situé à l'index fourni et retourne l'index de fin.
     *
     * @param index l'index dans le texte.
     *
     * @return l'index de fin du mot.
     *
     * @throws BadLocationException
     */
    public int selectWord(int index) throws BadLocationException {
        int start = Utilities.getWordStart(textPane, index);
        int end = Utilities.getWordEnd(textPane, index);

        textPane.select(start, end);

        return end;
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        //Ajoute le MouseListener sur la zone de texte.
        textPane.addMouseListener(l);
    }
}
