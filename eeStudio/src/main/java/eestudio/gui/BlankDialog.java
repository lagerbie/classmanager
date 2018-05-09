package eestudio.gui;

import java.awt.*;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.MaskFormatter;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

import eestudio.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.exception.ThotException;
import thot.gui.GuiUtilities;
import thot.gui.Resources;
import thot.labo.index.Index;
import thot.labo.index.IndexType;
import thot.utils.Utilities;

/**
 * Boite de dialogue pour l'édition des index.
 *
 * @author fabrice
 */
public class BlankDialog extends JDialog {
    private static final long serialVersionUID = 10200L;

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Core.class);

    /**
     * Couleur sans problème
     */
    private final Color normalColor = Color.WHITE;
    /**
     * Couleur pour un problème
     */
    private final Color errorColor = Color.RED;
    /**
     * Couleur pour des index identiques
     */
    private final Color identicalColor = Color.MAGENTA;

    /**
     * Position par défaut dans les champs de temps
     */
    private final int defautCaretPosition = 3;

    /**
     * Resources pour les textes
     */
    private Resources resources;
    /**
     * Noyau pour la création des index
     */
    private Core core;

    /**
     * Map pour donner la référence de texte selon le type de l'index
     */
    private Map<IndexType, String> indexTypes;
    /**
     * Map pour donner le type d'index selon le texte de la langue
     */
    private Map<String, IndexType> indexTypesRevert;

    /**
     * Séparateur pour les chiffres
     */
    private char decimalSeparator;

    /**
     * Message explicatif
     */
    private JLabel messageLabel;
    /**
     * Titre pour le temps de départ de l'index
     */
    private JLabel beginLabel;
    /**
     * Titre pour la durée de l'index
     */
    private JLabel lengthLabel;
    /**
     * Titre pour le type de l'index
     */
    private JLabel typeLabel;
    /**
     * Titre pour le soustitre de l'index
     */
    private JLabel subtitleLabel;

    /**
     * Liste des composants graphique d'un index
     */
    private List<IndexFields> indexesFields;
    /**
     * Liste pour la gestion du cycle de focus
     */
    private List<Component> order;

    /**
     * Bouton pour tout sélectionner
     */
    private JButton selectButton;
    /**
     * Bouton pour supprimer les index de la sélection
     */
    private JButton eraseSelection;
    /**
     * Bouton pour ajouter des index
     */
    private JButton addButton;
    /**
     * Bouton pour trier les index
     */
    private JButton sortButton;
    /**
     * Bouton pour valider les changements sur l'index
     */
    private JButton validButton;

    /**
     * Bouton d'appel du texte associé
     */
    private JButton blocTextButton;
    /**
     * Fenêtre du texte associé
     */
    private JDialog blocTextDialog;

    /**
     * Largeur des champs des temps
     */
    private int timeWidth = 100;
    /**
     * Largeur des champs du type
     */
    private int typeWidth;
    /**
     * Largeur des champs de sous-titre
     */
    private int subtitleWidth = 300;
    /**
     * Hauteur des champs
     */
    private int textHeight = 30;

    /**
     * Panneau principal des index
     */
    private JPanel mainPanel;

    /**
     * Initialisation de la boite de dialogue.
     *
     * @param core le coeur de l'application.
     * @param indexTypes map pour les resources textuelles suivant le type d'index.
     * @param resources les resources pour les textes.
     * @param parent la fenêtre parente (peut être null).
     * @param editorKit l'éditeur pour le texte.
     * @param styledDocument le document qui sauvegarde le texte.
     */
    public BlankDialog(Window parent, Core core, Resources resources, Map<IndexType, String> indexTypes,
            StyledEditorKit editorKit, StyledDocument styledDocument) {
        super(parent, resources.getString("blankTitle"), DEFAULT_MODALITY_TYPE);

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.core = core;
        this.indexTypes = indexTypes;
        this.resources = resources;

//        decimalSeparator = resources.getString("decimalSeparator");
        decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();

        indexTypesRevert = new HashMap<>(indexTypes.size());
        for (IndexType type : indexTypes.keySet()) {
            String typeInLanguage = resources.getString(indexTypes.get(type));
            indexTypesRevert.put(typeInLanguage, type);
        }

        initComponents();
        initBlocText(editorKit, styledDocument);
        this.setFocusTraversalPolicy(new LineFocusTraversalPolicy());
        initListeners();
    }

    /**
     * Initialisation des composants.
     */
    private void initComponents() {
        int width = 740;
        int height = 500;
        int margin = 20;
        Dimension dim;

        int capacity = 32;
        indexesFields = new ArrayList<>(capacity);
        order = new ArrayList<>(capacity * 4);

        messageLabel = new JLabel(resources.getString("blankMessage"));
        beginLabel = new JLabel(resources.getString("beginLabel"));
        lengthLabel = new JLabel(resources.getString("lengthLabel"));
        typeLabel = new JLabel(resources.getString("typeLabel"));
        subtitleLabel = new JLabel(resources.getString("subtitleLabel"));

        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        beginLabel.setHorizontalAlignment(JLabel.CENTER);
        lengthLabel.setHorizontalAlignment(JLabel.CENTER);
        typeLabel.setHorizontalAlignment(JLabel.CENTER);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);

        FontMetrics metrics = getFontMetrics(messageLabel.getFont());

        int max = 0;
        for (String typeInLanguage : indexTypesRevert.keySet()) {
            max = Math.max(max, metrics.stringWidth(typeInLanguage));
        }
        typeWidth = max + 30;

        dim = new Dimension(timeWidth, textHeight);
        beginLabel.setMinimumSize(dim);
        beginLabel.setPreferredSize(dim);
        lengthLabel.setMinimumSize(dim);
        lengthLabel.setPreferredSize(dim);
        dim = new Dimension(typeWidth, textHeight);
        typeLabel.setMinimumSize(dim);
        typeLabel.setPreferredSize(dim);
        dim = new Dimension(subtitleWidth, textHeight);
        subtitleLabel.setMinimumSize(dim);
        subtitleLabel.setPreferredSize(dim);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        dim = new Dimension(width - 20, textHeight);
        mainPanel.setPreferredSize(dim);

        JScrollPane scrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setBorder(null);
        dim = new Dimension(width, height - 2 * margin - 100);
        scrollPane.setPreferredSize(dim);
        AdjustmentListener adjustmentListener = e -> getContentPane().repaint();
        scrollPane.getVerticalScrollBar().addAdjustmentListener(adjustmentListener);
        scrollPane.getHorizontalScrollBar().addAdjustmentListener(adjustmentListener);

        selectButton = GuiUtilities
                .getSelectableButton(this, GuiUtilities.getImageIcon("all"), GuiUtilities.getImageIcon("none"),
                        resources.getString("selectAll"));
        selectButton.setAlignmentX(CENTER_ALIGNMENT);
        addButton = GuiUtilities
                .getActionButton(this, GuiUtilities.getImageIcon("add"), GuiUtilities.getImageIcon("addOff"),
                        resources.getString("addIndexes"));
        sortButton = GuiUtilities
                .getActionButton(this, GuiUtilities.getImageIcon("sort"), GuiUtilities.getImageIcon("sortOff"),
                        resources.getString("sortIndexes"));
        eraseSelection = GuiUtilities.getActionButton(this, GuiUtilities.getImageIcon("eraseSelection"),
                GuiUtilities.getImageIcon("eraseSelectionOff"), resources.getString("eraseSelection"));
        validButton = GuiUtilities
                .getActionButton(this, GuiUtilities.getImageIcon("valid"), GuiUtilities.getImageIcon("validOff"),
                        resources.getString("valid"));
        blocTextButton = GuiUtilities
                .getSelectableButton(this, GuiUtilities.getImageIcon("text"), GuiUtilities.getImageIcon("textOff"),
                        resources.getString("notepad"));

        GridBagLayout labelLayout = new GridBagLayout();
        GridBagConstraints labelConstraints = new GridBagConstraints();
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(labelLayout);
        dim = new Dimension(width - 2 * margin - 20, textHeight);
        labelPanel.setPreferredSize(dim);
        scrollPane.setColumnHeaderView(labelPanel);

        labelConstraints.weightx = 0.0;
        labelLayout.setConstraints(selectButton, labelConstraints);
        labelPanel.add(selectButton);
        labelLayout.setConstraints(beginLabel, labelConstraints);
        labelPanel.add(beginLabel);
        labelLayout.setConstraints(lengthLabel, labelConstraints);
        labelPanel.add(lengthLabel);
        labelLayout.setConstraints(typeLabel, labelConstraints);
        labelPanel.add(typeLabel);
        labelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        labelConstraints.weightx = 1.0;
        labelLayout.setConstraints(subtitleLabel, labelConstraints);
        labelPanel.add(subtitleLabel);

        JPanel panel = new BackgroundPanel(width, height, 15);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setLayout(layout);

        constraints.weightx = 1.0;
        constraints.weighty = 0.0;

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.BASELINE;
        constraints.insets = new Insets(margin, margin, margin, margin);
        layout.setConstraints(messageLabel, constraints);
        panel.add(messageLabel);

        constraints.weighty = 1.0;
        constraints.anchor = GridBagConstraints.ABOVE_BASELINE;
        constraints.fill = GridBagConstraints.BOTH;
        layout.setConstraints(scrollPane, constraints);
        panel.add(scrollPane);

        constraints.weighty = 0.0;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.BASELINE;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(margin, margin, margin, margin);
        layout.setConstraints(addButton, constraints);
        panel.add(addButton);
        layout.setConstraints(eraseSelection, constraints);
        panel.add(eraseSelection);
        layout.setConstraints(sortButton, constraints);
        panel.add(sortButton);
        layout.setConstraints(blocTextButton, constraints);
        panel.add(blocTextButton);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(validButton, constraints);
        panel.add(validButton);

        this.getContentPane().add(panel);
        this.pack();
    }

    /**
     * Initialise la fenêtre du bloc-notes.
     *
     * @param editorKit l'éditeur pour le texte.
     * @param styledDocument le document qui sauvegarde le texte.
     */
    private void initBlocText(StyledEditorKit editorKit, StyledDocument styledDocument) {
        JTextPane textPane = new JTextPane();
        textPane.setEditorKit(editorKit);
        textPane.setDocument(styledDocument);

        blocTextDialog = new JDialog(this);
        blocTextDialog.getContentPane().setLayout(new BoxLayout(blocTextDialog.getContentPane(), BoxLayout.Y_AXIS));
        JScrollPane textScrollPane = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Dimension dim = new Dimension(335, 450);
        textScrollPane.setMinimumSize(dim);
        textScrollPane.setPreferredSize(dim);
        textScrollPane.setMaximumSize(new Dimension(335, 550));

        blocTextDialog.getContentPane().add(textScrollPane);
        blocTextDialog.pack();

        blocTextDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                blocTextButton.setSelected(false);
            }
        });
    }

    /**
     * Actualiser la taille et la position de la fenêtre.
     */
    private void updateSizeAndLocation() {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

        this.pack();
        int width = getWidth();
        int height = getHeight();

        if (width < 600) {
            width = 740;
        }
        if (height > screenDim.height - 50) {
            height = screenDim.height - 50;
        }

        this.setSize(new Dimension(width, height));
        this.setLocation((screenDim.width - getWidth()) / 4, (screenDim.height - getHeight()) / 4);
        blocTextDialog.setLocation(getWidth() + getX(), getY() + (getHeight() - blocTextDialog.getHeight()) / 2);
    }

    /**
     * Modifie les textes
     */
    public void updateLanguage() {
        setTitle(resources.getString("blankTitle"));

        messageLabel.setText(resources.getString("blankMessage"));
        beginLabel.setText(resources.getString("beginLabel"));
        lengthLabel.setText(resources.getString("lengthLabel"));
        typeLabel.setText(resources.getString("typeLabel"));
        subtitleLabel.setText(resources.getString("subtitleLabel"));

        selectButton.setToolTipText(resources.getString("selectAll"));
        eraseSelection.setToolTipText(resources.getString("eraseSelection"));

        addButton.setToolTipText(resources.getString("addIndexes"));
        sortButton.setToolTipText(resources.getString("sortIndexes"));

        validButton.setToolTipText(resources.getString("valid"));
        blocTextButton.setToolTipText(resources.getString("notepad"));

//        decimalSeparator = resources.getString("decimalSeparator");
        decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();

        indexTypesRevert.clear();
        for (IndexType type : indexTypes.keySet()) {
            String typeInLanguage = resources.getString(indexTypes.get(type));
            indexTypesRevert.put(typeInLanguage, type);
        }

        FontMetrics metrics = getFontMetrics(messageLabel.getFont());

        int max = 0;
        for (String typeInLanguage : indexTypesRevert.keySet()) {
            max = Math.max(max, metrics.stringWidth(typeInLanguage));
        }
        typeWidth = max + 30;
    }

    /**
     * Initialisation des actions.
     */
    private void initListeners() {
        validButton.addActionListener(e -> {
            validButton.setSelected(true);
            validAction();
            validButton.setSelected(false);
        });

        selectButton.addActionListener(e -> {
            boolean select = !selectButton.isSelected();
            for (IndexFields indexFields : indexesFields) {
                indexFields.checkBox.setSelected(select);
            }

            selectButton.setSelected(select);
            selectButton.setToolTipText(resources.getString(select ? "selectNone" : "selectAll"));
        });

//        invertSelection.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                for(IndexFields indexFields : indexesFields) {
//                    indexFields.checkBox.setSelected(
//                            !indexFields.checkBox.isSelected());
//                }
//            }
//        });

        eraseSelection.addActionListener(e -> {
            eraseSelection.setSelected(true);
            int option = GuiUtilities.showOptionDialog(getOwner(), resources.getString("deleteIndexes"), null, null);
            if (option == GuiUtilities.YES_OPTION) {
                removeSelectedIndexes();
            }
            eraseSelection.setSelected(false);
        });

        addButton.addActionListener(e -> {
            addButton.setSelected(true);
            Object input = GuiUtilities.showInputDialog(getOwner(), resources.getString("howIndexesInput"), null, null);

            if (input != null) {
                int nb = Utilities.parseStringAsInt((String) input);

                if (nb > 0) {
                    addIndexes(nb);
                    validate();
                    updateSizeAndLocation();
                }
            }
            addButton.setSelected(false);
        });

        sortButton.addActionListener(e -> sortIndexesFields());

        blocTextButton.addActionListener(e -> {
            blocTextDialog.setVisible(!blocTextDialog.isVisible());
            blocTextButton.setSelected(blocTextDialog.isVisible());
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                validButton.requestFocus();
                if (hasIndex()) {
                    int option = GuiUtilities
                            .showOptionDialogWithCancel(getOwner(), resources.getString("modifIndexes"), null, null);
                    if (option == GuiUtilities.YES_OPTION) {
                        validButton.setSelected(true);
                        validAction();
                        validButton.setSelected(false);
                    } else if (option == GuiUtilities.NO_OPTION) {
                        setVisible(false);
                    }
                } else {
                    setVisible(false);
                }
            }
        });
    }

    /**
     * Affiche la boite de dialogue.
     *
     * @param nbCreate le nombre d'index à créer.
     */
    public void showDialog(int nbCreate) {
        removeIndexes();
        addIndexes(nbCreate);
        updateSizeAndLocation();
        this.setVisible(true);
    }

    /**
     * Ajoute des indes dans la table.
     *
     * @param cnt le nombre d'index à ajouter.
     */
    private void addIndexes(int cnt) {
        for (int i = 0; i < cnt; i++) {
            addIndex();
        }

        int nb = mainPanel.getComponentCount();
        mainPanel.setPreferredSize(new Dimension(mainPanel.getPreferredSize().width, nb * textHeight));
    }

    /**
     * Ajoute un index dans la table d'affichage.
     */
    private void addIndex() {
        IndexFields fields = new IndexFields();
        Dimension dim = new Dimension(2 * mainPanel.getPreferredSize().width, textHeight);
        fields.setMaximumSize(dim);

        indexesFields.add(fields);

        mainPanel.add(fields);
        order.add(fields.beginField);
        order.add(fields.typeList);
        order.add(fields.subtitleField);

        setValue(fields.beginField, 0);
        setValue(fields.lengthField, 0);
    }

    /**
     * Mise à jour de la liste des types possibles avec un type initial.
     *
     * @param type le type initial.
     * @param typeList le composant graphique de la liste.
     */
    private void updateTypeList(IndexType type, JComboBox typeList) {
        typeList.setSelectedItem(resources.getString(indexTypes.get(type)));
    }

    /**
     * Convertie la chaîne de caractères représentant le temps en millisecondes.
     *
     * @param textField le champ texte contenat le temps formaté.
     *
     * @return le temps en millisecondes.
     */
    private long getValue(JTextField textField) {
        String value = textField.getText();
        String[] split = value.split("[:,.]");
        long minute = Utilities.parseStringAsLong(split[0].trim());
        long seconde = Utilities.parseStringAsLong(split[1].trim());
        long milliseconde = Utilities.parseStringAsLong(split[2].trim());
        return (minute * 60 + seconde) * 1000 + milliseconde;
    }

    /**
     * Convertie la chaîne de caractères représentant le temps en millisecondes.
     *
     * @param textField le champ texte contenat le temps formaté.
     * @param value le temps en millisecondes.
     */
    private void setValue(JTextField textField, long value) {
        textField.setText(String.format("%1$tM:%1$tS" + decimalSeparator + "%1$tL", value));
    }

    /**
     * Mise à jour des champs avec vérifications des valeurs.
     *
     * @param beginField le champs pour le début d'index.
     * @param lengthField le champs pour la longueur d'index.
     */
    private void updateFields(JTextField beginField, JTextField lengthField) {
        long begin = getValue(beginField);
        long length = getValue(lengthField);

        if (begin < 0 || begin > core.getRecordTimeMax()) {
            begin = 0;
            setValue(beginField, begin);
            beginField.setBackground(errorColor);
            GuiUtilities.showMessageDialog(this, resources.getString("beginError"));
            beginField.setBackground(normalColor);
            beginField.requestFocusInWindow();
        }

        if (length > core.getRemainingTime()) {
            length = core.getRemainingTime();
            setValue(lengthField, length);
            lengthField.setBackground(errorColor);
            GuiUtilities.showMessageDialog(this, resources.getString("lengthError"));
            lengthField.setBackground(normalColor);
            lengthField.requestFocusInWindow();
        }

        int beginCaretPosition = beginField.getCaretPosition();
        int lengthCaretPosition = lengthField.getCaretPosition();

        setValue(beginField, begin);
        setValue(lengthField, length);

        beginField.setCaretPosition(beginCaretPosition);
        lengthField.setCaretPosition(lengthCaretPosition);
    }

    /**
     * Indique si le temps est compris dans un index.
     *
     * @param time le temps à tester
     *
     * @return {@code true}si le champ est compris dans un autre index.
     */
    private boolean onIndex(long time) {
        for (Iterator<Index> it = core.indexesIterator(); it.hasNext(); ) {
            Index index = it.next();
            if (time > index.getInitialTime() && time < index.getFinalTime()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indique si un index est identique à un autre temps (temps de début et temps de fin identiques).
     *
     * @param fields les champs de début et de longueur de l'index.
     *
     * @return {@code true} si l'index est identique à un autre index.
     */
    private boolean isIdentical(IndexFields fields) {
        String begin = fields.beginField.getText();
        String length = fields.lengthField.getText();

        for (IndexFields indexFields : indexesFields) {
            if (indexFields == fields) {
                continue;
            }
            if (indexFields.beginField.getText().equals(begin)
                    && indexFields.lengthField.getText().equals(length)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indique si il y a des index non null.
     *
     * @return {@code true} si il y a des index non null.
     */
    private boolean hasIndex() {
        for (IndexFields indexFields : indexesFields) {
            if (getValue(indexFields.lengthField) > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Supprime tous les index de la fenêtre.
     */
    private void removeIndexes() {
        indexesFields.clear();
        order.clear();
        mainPanel.removeAll();
    }

    /**
     * Supprime les index sélectionné.
     */
    private void removeSelectedIndexes() {
        int capacity = indexesFields.size();
        List<IndexFields> newIndexesFields = new ArrayList<>(capacity);

        order.clear();
        mainPanel.removeAll();

        for (IndexFields indexFields : indexesFields) {
            if (!indexFields.checkBox.isSelected()) {
                newIndexesFields.add(indexFields);

                mainPanel.add(indexFields);
                order.add(indexFields.beginField);
                order.add(indexFields.lengthField);
                order.add(indexFields.typeList);
                order.add(indexFields.subtitleField);
            }
        }

        indexesFields.clear();
        indexesFields = newIndexesFields;
        updateSizeAndLocation();
    }

    /**
     * Supprime les index sélectionné.
     */
    private void removeNullIndexes() {
        int capacity = indexesFields.size();
        List<IndexFields> newIndexesFields = new ArrayList<>(capacity);

        order.clear();
        mainPanel.removeAll();

        for (IndexFields indexFields : indexesFields) {
            if (getValue(indexFields.lengthField) > 0) {
                newIndexesFields.add(indexFields);

                mainPanel.add(indexFields);
                order.add(indexFields.beginField);
                order.add(indexFields.lengthField);
                order.add(indexFields.typeList);
                order.add(indexFields.subtitleField);
            }
        }

        indexesFields.clear();
        indexesFields = newIndexesFields;
        updateSizeAndLocation();
    }

    /**
     * Trie par ordre croissant du temps de départ des index de la table.
     */
    private void sortIndexesFields() {
        int capacity = indexesFields.size();
        boolean[] check = new boolean[capacity];
        String[] begin = new String[capacity];
        String[] length = new String[capacity];
        IndexType[] type = new IndexType[capacity];
        String[] subtitle = new String[capacity];

        for (int i = 0; i < capacity; i++) {
            IndexFields indexFields = getFirstIndex();
            check[i] = indexFields.checkBox.isSelected();
            begin[i] = indexFields.beginField.getText();
            length[i] = indexFields.lengthField.getText();
            type[i] = indexTypesRevert.get((String) indexFields.typeList.getSelectedItem());
            subtitle[i] = indexFields.subtitleField.getText();

            setValue(indexFields.beginField, Long.MAX_VALUE);
        }

        order.clear();
        for (int i = 0; i < capacity; i++) {
            IndexFields indexFields = indexesFields.get(i);
            indexFields.checkBox.setSelected(check[i]);
            indexFields.beginField.setText(begin[i]);
            indexFields.lengthField.setText(length[i]);
            indexFields.subtitleField.setText(subtitle[i]);
            updateFields(indexFields.beginField, indexFields.lengthField);
            updateTypeList(type[i], indexFields.typeList);

            order.add(indexFields.beginField);
            order.add(indexFields.lengthField);
            order.add(indexFields.typeList);
            order.add(indexFields.subtitleField);
        }
    }

    /**
     * Retourne l'indice de l'index de la table qui a le temps de début le plus petit.
     *
     * @return l'indice.
     */
    private IndexFields getFirstIndex() {
        IndexFields first = null;
        long time = Long.MAX_VALUE;
        for (IndexFields indexFields : indexesFields) {
            long currentTime = getValue(indexFields.beginField);
            if (currentTime < time) {
                first = indexFields;
                time = currentTime;
            }
        }

        return first;
    }

    /**
     * Test la validité des changements et modifie les index en conséquence.
     */
    private void validAction() {
        boolean overlapped = false;
        boolean identical = false;
        long insertTime = 0;
        int size = indexesFields.size();

        for (IndexFields indexFields : indexesFields) {
            insertTime += getValue(indexFields.lengthField);

            indexFields.beginField.setBackground(normalColor);

            if (onIndex(getValue(indexFields.beginField))) {
                indexFields.beginField.setBackground(errorColor);
                overlapped = true;
            }

            if (isIdentical(indexFields)) {
                indexFields.beginField.setBackground(identicalColor);
                identical = true;
            }
        }

        if (overlapped) {
            int choix = GuiUtilities.showOptionDialog(this, resources.getString("overlappedIndexes"), null, null);
            if (choix != GuiUtilities.YES_OPTION) {
                return;
            }
        }

        if (identical) {
            int choix = GuiUtilities.showOptionDialog(this, resources.getString("identicalIndexes"), null, null);
            if (choix != GuiUtilities.YES_OPTION) {
                return;
            }
        }

        if (insertTime > core.getRemainingTime()) {
            GuiUtilities.showMessageDialog(this, resources.getString("insertionDurationMessage"), insertTime / 1000 + 1,
                    core.getRemainingTime() / 1000, core.getDurationMax() / 1000);
            return;
        }

        sortIndexesFields();
        for (int i = size - 1; i >= 0; i--) {
            IndexFields indexFields = indexesFields.get(i);
            long begin = getValue(indexFields.beginField);
            long length = getValue(indexFields.lengthField);
            IndexType type = indexTypesRevert.get((String) indexFields.typeList.getSelectedItem());
            String subtitle = indexFields.subtitleField.getText();
            if (subtitle != null && subtitle.isEmpty()) {
                subtitle = null;
            }

            if (length > core.getRemainingTime()) {
                indexFields.beginField.setBackground(errorColor);
                GuiUtilities.showMessageDialog(this, resources.getString("insertionDurationMessage"), length / 1000 + 1,
                        core.getRemainingTime() / 1000, core.getDurationMax() / 1000);
                return;
            }
            try {
                core.addMediaIndexAt(begin, length, type, subtitle);
            } catch (ThotException e) {
                LOGGER.error("Erreur dans le traitement", e);
                GuiUtilities.showMessage("Erreur dans le traitement " + e);
                return;
            }
        }

        core.removeNullIndex();
        core.sortIndexes();

        setVisible(false);
    }


    /**
     * Classe pour la gestion de l'ordre de focus des composants.
     */
    private class LineFocusTraversalPolicy extends FocusTraversalPolicy {

        @Override
        public Component getComponentAfter(Container container, Component aComponent) {
            if (order.isEmpty()) {
                return null;
            }

            int index = order.indexOf(aComponent) + 1;
            if (index == order.size()) {
                index = 0;
            }
            return order.get(index);
        }

        @Override
        public Component getComponentBefore(Container container, Component aComponent) {
            if (order.isEmpty()) {
                return null;
            }

            int index = order.indexOf(aComponent) - 1;
            if (index < 0) {
                index = order.size() - 1;
            }
            return order.get(index);
        }

        @Override
        public Component getDefaultComponent(Container container) {
            if (order.isEmpty()) {
                return null;
            }
            return order.get(0);
        }

        @Override
        public Component getLastComponent(Container container) {
            if (order.isEmpty()) {
                return null;
            }
            return order.get(order.size() - 1);
        }

        @Override
        public Component getFirstComponent(Container container) {
            if (order.isEmpty()) {
                return null;
            }
            return order.get(0);
        }
    }

    /**
     * Classe recensant tous les composants graphiques nécessaires pour un index.
     */
    private class IndexFields extends JPanel {
        private static final long serialVersionUID = 10200L;

        /**
         * Champs de sélection des index
         */
        JCheckBox checkBox;
        /**
         * Champs formatés pour les temps de départ des index
         */
        JFormattedTextField beginField;
        /**
         * Champ non modifiable pour la durée de l'index
         */
        JFormattedTextField lengthField;
        /**
         * Liste déroulante pour le type de l'index
         */
        JComboBox typeList;
        /**
         * Zone de texte pour le soustitre de l'index
         */
        JTextField subtitleField;

        private IndexFields() {
            MaskFormatter beginFormatter = null;
            MaskFormatter lengthFormatter = null;
            try {
                beginFormatter = new MaskFormatter("##:##" + decimalSeparator + "###");
                beginFormatter.setPlaceholderCharacter('0');
                lengthFormatter = new MaskFormatter("##:##" + decimalSeparator + "###");
                lengthFormatter.setPlaceholderCharacter('0');
            } catch (ParseException e) {
                LOGGER.error("", e);
            }

            checkBox = new JCheckBox();
            beginField = new JFormattedTextField(beginFormatter);
            lengthField = new JFormattedTextField(lengthFormatter);
            typeList = new JComboBox();
            subtitleField = new JTextField();

            typeList.addItem(resources.getString(indexTypes.get(IndexType.BLANK)));
            typeList.addItem(resources.getString(indexTypes.get(IndexType.BLANK_BEEP)));
            updateTypeList(IndexType.BLANK, typeList);

            Dimension dim = new Dimension(40, textHeight);
            checkBox.setMinimumSize(dim);
            checkBox.setMaximumSize(dim);
            checkBox.setPreferredSize(dim);
            dim = new Dimension(timeWidth, textHeight);
            beginField.setMinimumSize(dim);
            beginField.setMaximumSize(dim);
            beginField.setPreferredSize(dim);
            lengthField.setMinimumSize(dim);
            lengthField.setMaximumSize(dim);
            lengthField.setPreferredSize(dim);
            dim = new Dimension(typeWidth, textHeight);
            typeList.setMinimumSize(dim);
            typeList.setMaximumSize(dim);
            typeList.setPreferredSize(dim);
            dim = new Dimension(subtitleWidth, textHeight);
            subtitleField.setMinimumSize(dim);
//            subtitleField.setMaximumSize(dim);
            subtitleField.setPreferredSize(dim);

            checkBox.setHorizontalAlignment(JCheckBox.CENTER);
            beginField.setHorizontalAlignment(JTextField.RIGHT);
            lengthField.setHorizontalAlignment(JTextField.CENTER);
            subtitleField.setHorizontalAlignment(JTextField.LEADING);

            GridBagLayout layout = new GridBagLayout();
            GridBagConstraints constraints = new GridBagConstraints();
            this.setLayout(layout);

            constraints.weightx = 0.0;

            layout.setConstraints(checkBox, constraints);
            this.add(checkBox);
            layout.setConstraints(beginField, constraints);
            this.add(beginField);
            layout.setConstraints(lengthField, constraints);
            this.add(lengthField);
            layout.setConstraints(typeList, constraints);
            this.add(typeList);
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.weightx = 1.0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            layout.setConstraints(subtitleField, constraints);
            this.add(subtitleField);

            beginField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    beginField.setCaretPosition(defautCaretPosition);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    updateFields(beginField, lengthField);
                }
            });

            lengthField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    lengthField.setCaretPosition(defautCaretPosition);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    updateFields(beginField, lengthField);
                }
            });

            //pour éviter d'avoir un reste d'image quand le programme affiche ou masque la liste
            typeList.addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    getContentPane().repaint();
                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    getContentPane().repaint();
                }

                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {
                }
            });

            MouseListener mouseListener = new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getContentPane().repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    getContentPane().repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    getContentPane().repaint();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    getContentPane().repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    getContentPane().repaint();
                }
            };

            FocusListener focusListener = new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    getContentPane().repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    getContentPane().repaint();
                }
            };

            checkBox.addMouseListener(mouseListener);
            checkBox.addFocusListener(focusListener);
        }
    }

//    public static void main(String[] args) {
//        GuiUtilities.manageUI(true);
//        javax.swing.JFrame frame = new javax.swing.JFrame("test");
//        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//
//        Map<String,String> indexTypesMap = new HashMap<String, String>(5);
//        indexTypesMap.put(Index.PLAY, "playType");
//        indexTypesMap.put(Index.RECORD, "recordType");
//        indexTypesMap.put(Index.BLANK, "blankType");
//        indexTypesMap.put(Index.BLANK_BEEP, "blankBeepType");
//        indexTypesMap.put(Index.VOICE, "voiceType");
//        indexTypesMap.put(Index.REPEAT, "repeatType");
//        indexTypesMap.put(Index.FILE, "fileType");
//        indexTypesMap.put(Index.SELECTION, "selectionType");
//
//        Index index = new Index(Index.PLAY, 0);
//        index.setFinalTime(1000);
//
//        try {
//            Core core = new Core(null);
//            BlankDialog dialog = new BlankDialog(frame, core,
//                    new Resources(), indexTypesMap,
//                    core.getStyledEditorKit(), core.getStyledDocument());
//
//            dialog.addWindowListener(new WindowAdapter() {
//                @Override
//                public void windowClosing(WindowEvent e) {
//                    System.exit(0);
//                }
//            });
//
//            dialog.showDialog(5);
//        } catch(Exception e) {
//            Edu4Logger.error(e);
//        }
//    }

}
