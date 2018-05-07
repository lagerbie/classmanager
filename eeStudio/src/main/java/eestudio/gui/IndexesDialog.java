package eestudio.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
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
import eestudio.Index;
import eestudio.utils.Edu4Logger;
import eestudio.utils.Utilities;

/*
 * v0.95: ajout de private ArrayList<Long> ids;
 * v0.95: ajout de private final Color normalColor = Color.WHITE;
 * v0.95: ajout de private final Color errorColor = Color.RED;
 * v0.95: ajout de private long insertInitialTime;
 * v0.95: supp de private Window parent;
 * v0.95: supp de private JTextPane textPane;
 * v0.95: ajout de private Window getMainWindow()
 * v0.95: ajout de private void closeDialog()
 * 
 * v0.95.10: Liste de types différente pour le type d'index initial.
 * v0.95.10: Utilisation de IndexFields pour la représentation d'un index
 * v0.95.10: ajout de private JLabel checkLabel;
 * v0.95.10: ajout de private JPanel checkPanel;
 * v0.95.10: ajout de private JButton selectAll;
 * v0.95.10: ajout de private JButton selectNone;
 * v0.95.10: ajout de private JButton invertSelection;
 * v0.95.10: ajout de private JButton eraseSelection;
 * v0.95.10: ajout de private class IndexFields
 * v0.95.10: ajout de private List<IndexFields> indexesFields;
 * v0.95.10: supp de private ArrayList<Long> ids;
 * v0.95.10: supp de private ArrayList<JFormattedTextField> beginFields;
 * v0.95.10: supp de private ArrayList<JFormattedTextField> endFields;
 * v0.95.10: supp de private ArrayList<JTextField> lengthFields;
 * v0.95.10: supp de private ArrayList<JComboBox> typeLists;
 * v0.95.10: supp de private ArrayList<JTextField> subtitleFields;
 * v0.95.10: modif de private ArrayList<Component> order; en List<Component>
 * v0.95.10: ajout de private boolean updateTypeList(String type, JComboBox typeList)
 * v0.95.10: ajout de private void removeSelectedIndexes()
 * v0.95.10: ajout de private void updateSizeAndLocation()
 * v0.95.10: ajout de private void initListeners()
 * v0.95.10: modif de addButton Listener [updateSizeAndLocation()]
 * v0.95.10: modif de showDialog(int nbCreate) [updateSizeAndLocation()]
 * v0.95.10: modif de initComponents() [liste de types, checkLabel, checkPanel, add JB, supp AL]
 * v0.95.10: modif de updateLanguage() [liste de types, checkLabel, checkPanel, add JB, supp AL]
 * v0.95.10: modif de addIndex(long id, long begin, long end, String type,
 *           String subtitile) [liste de types, check boxes, supp AL]
 * v0.95.10: modif de removeIndexes() [check boxes, supp AL]
 * v0.95.10: modif de sortIndexesFields() [liste de types, check boxes, supp AL]
 * v0.95.10: modif de isBeginOverlapped(JTextField beginField) [supp AL]
 * v0.95.10: modif de isEndOverlapped(JTextField endField) [supp AL]
 * v0.95.10: modif de isIdentical(int index) [supp AL]
 * v0.95.10: modif de getFirstIndex() [supp AL]
 * v0.95.10: modif de validButton Listener [vérification longueur insertion]
 * v0.95.10: modif de private JTextPane textPane; [-> variable locale]
 * 
 * v0.95.11: utilisation d'images pour les JButton
 * v0.95.11: ajout de private final Color changeColor = Color.CYAN;
 * v0.95.11: supp de private JButton invertSelection;
 * v0.95.11: supp de private JButton closeButton;
 * v0.95.11: modif de Color identicalColor [Color.BLUE -> Color.MAGENTA]
 * v0.95.11: modif de JButton selectAll et JButton selectNone en
 *           JButton selectButton [fusion]
 * v0.95.11: ajout de private void validAction()
 * v0.95.11: ajout de private boolean isChanged(IndexFields indexFields)
 * v0.95.11: supp de private Window getMainWindow()
 * v0.95.11: modif de IndexesDialog(..., Image icone, ...) en
 *           IndexesDialog(..., List<Image> icones, ...)
 * v0.95.11: modif de initComponents() [JB=image, JB]
 * v0.95.11: modif de initListeners() [validAction();, confirmation cancel,
 *           erase sélection]
 * v0.95.11: modif de updateLanguage() [JB=image, JB]
 * v0.95.11: modif de initBlocText(...) [ajout WindowListener]
 * 
 * v0.95.12: modif de IndexesDialog(Core, Map<String, String>, Resources, Window,
 *           List<Image>, StyledEditorKit, StyledDocument) en IndexesDialog(
 *           Window, List<Image>, Core, Resources,  Map<String, String>,
 *           StyledEditorKit, StyledDocument)
 * 
 * v0.95.13: supp de private JButton cancelButton;
 * v0.95.13: supp de private void closeDialog()
 * v0.95.13: ajout de private boolean hasChanges()
 * v0.95.13: modif de IndexesDialog(...) [defaultCloseOperation: hide -> nothing,
 *           initListeners(); decimalSeparator]
 * v0.95.13: modif de initComponents() [supp cancel, supp initListeners();]
 * v0.95.13: modif de private void initListeners() [supp cancel, test changement
 *           sur fermeture fenêtre et choix de confirmation]
  v0.95.13: modif de updateLanguage() [supp cancel]
 * 
 * v0.96: modif de initComponents() [fixe la largeur des champs texte à 100 (plus
 *        de calcul de longueur de chaîne de caractères)]
 * v0.96: modif de updateLanguage() [largeur des champs texte fixe]
 * v0.96: modif de removeSelectedIndexes() [supp par liste et non individuel]
 * v0.96: modif de isBeginOverlapped(JTextField beginField) en
 *        isBeginOverlapped(JTextField beginField)
 * v0.96: modif de isEndOverlapped(JTextField endField) en 
 *        private boolean isEndOverlapped(JTextField endField)
 * v0.96: modif de isIdentical(int index) en isIdentical(IndexFields fields)
 * v0.96: modif de int getFirstIndex() en IndexFields getFirstIndex()
 * v0.96: modif de initListeners() [mise bulle bouton sélection]
 * v0.96: modif de validAction() [plus d'actualisation état bouton]
 * 
 * v0.98: modif de getValue(String value) [use Utilities.parse*()]
 * 
 * v0.99: ajout de private void setValue(JTextField textField, long value)
 * v0.99: modif de IndexesDialog(Window parent, List<Image> icones, ...) en
 *        IndexesDialog(Window parent, ...)
 * v0.99: modif de initComponents() [GuiUtilities.getImageIcon]
 * v0.99: modif de getValue(String value) en getValue(JTextField field)
 * v0.99: modif de updateFields(...) [getValue, setValue]
 * v0.99: modif de validAction() [getValue]
 * v0.99: modif de isChanged(IndexFields indexFields) [getValue]
 * v0.99: modif de isBeginOverlapped(JTextField beginField) [getValue]
 * v0.99: modif de isEndOverlapped(JTextField endField) [getValue]
 * v0.99: modif de getFirstIndex() [getValue
 * v0.99: modif de addIndex(...) [setValue]
 * v0.99: modif de sortIndexesFields() [setValue]
 * 
 * v1.01: ajout de private JPanel mainPanel;
 * v1.01: modif de IndexesDialog(..) [ajout de setFocusTraversalPolicy(..)]
 * v1.01: modif de initComponents() [modif de checkLabel de " " en "\n", use
 *        de GridBagLayout et de BackgroundPanel]
 * v1.01: modif de updateLanguage() [changement des noms des ressources]
 * v1.01: modif de updateTypeList(..) [supp enable pour SPEED]
 * v1.01: modif de addIndex(long id, ..) [add ComboBox listener]
 * v1.01: modif de addIndexes(int cnt) [redimensionnement de mainPanel suivant
 *        le nombre de composants pour le scrollpane]
 * 
 * v1.02: supp de private JLabel checkLabel;
 * v1.02: supp de private JPanel checkPanel;
 * v1.02: supp de private JPanel beginPanel;
 * v1.02: supp de private JPanel lengthPanel;
 * v1.02: supp de private JPanel endPanel;
 * v1.02: supp de private JPanel typePanel;
 * v1.02: supp de private JPanel subtitlePanel;
 * v1.02: modif de initComponents() [label dans column header du scrollpane]
 * v1.02: modif de addIndex(long id, ...) [supp panel]
 * v1.02: modif de removeIndexes() [supp panel]
 * v1.02: modif de removeSelectedIndexes() [supp panel]
 * v1.02: modif de IndexFields [ajout de extends JPanel]
 * v1.02: supp de IndexFields(long id, JCheckBox checkBox,
 *        JFormattedTextField beginField, JFormattedTextField endField,
 *        JTextField lengthField, JComboBox typeList, JTextField subtitleField)
 * v1.02: ajout de private IndexFields(long id) [ajout de mouse et focus listener sur checkBox]
 */

/**
 * Boite de dialogue pour l'édition des index.
 *
 * @author Fabrice Alleau
 * @since version 0.94
 * @version 1.02
 */
public class IndexesDialog extends JDialog {
    private static final long serialVersionUID = 10200L;

    /** Couleur sans problème */
    private final Color normalColor = Color.WHITE;
    /** Couleur pour un problème */
    private final Color errorColor = Color.RED;
    /** Couleur pour des index identiques */
    private final Color identicalColor = Color.MAGENTA;
    /** Couleur pour des index identiques */
    private final Color changeColor = Color.CYAN;

    /** Position par défaut dans les champs de temps */
    private static final int defautCaretPosition = 3;

    /** Resources pour les textes */
    private Resources resources;
    /** Noyau pour la création des index */
    private Core core;

    /** Map pour donner la référence de texte selon le type de l'index */
    private Map<String, String> indexTypes;
    /** Map pour donner le type d'index selon le texte de la langue */
    private Map<String, String> indexTypesRevert;

    /** Séparateur pour les chiffres */
    private char decimalSeparator;

    /** Message explicatif */
    private JLabel messageLabel;
    /** Titre pour le temps de départ de l'index */
    private JLabel beginLabel;
    /** Titre pour le temps de fin de l'index */
    private JLabel endLabel;
    /** Titre pour la durée de l'index */
    private JLabel lengthLabel;
    /** Titre pour le type de l'index */
    private JLabel typeLabel;
    /** Titre pour le soustitre de l'index */
    private JLabel subtitleLabel;

    /** Liste des composants graphique d'un index */
    private List<IndexFields> indexesFields;
    /** Liste pour la gestion du cycle de focus */
    private List<Component> order;

    /** Bouton pour tout sélectionner */
    private JButton selectButton;
    /** Bouton pour supprimer les index de la sélection */
    private JButton eraseSelection;

    /** Bouton pour ajouter des index */
    private JButton addButton;
    /** Bouton pour trier les index */
    private JButton sortButton;

    /** Bouton pour valider les changements sur l'index */
    private JButton validButton;

    /** Bouton d'appel du texte associé */
    private JButton blocTextButton;
    /** Fenêtre du texte associé */
    private JDialog blocTextDialog;

    /** Largeur des champs des temps */
    private int timeWidth = 100;
    /** Largeur des champs du type */
    private int typeWidth;
    /** Largeur des champs de sous-titre */
    private int subtitleWidth = 300;
    /** Hauteur des champs */
    private int textHeight = 30;

    /** Panneau principal des index */
    private JPanel mainPanel;

    /** Durée des insertions initiales */
    private long insertInitialTime;

    /**
     * Initialisation de la boite de dialogue.
     *
     * @param core le coeur de l'application.
     * @param indexTypes map pour les resources textuelles suivant le type d'index.
     * @param resources les resources pour les textes.
     * @param parent la fenêtre parente (peut être null).
     * @param editorKit l'éditeur pour le texte.
     * @param styledDocument le document qui sauvegarde le texte.
     * @since version 0.94 - version 1.01
     */
    public IndexesDialog(Window parent, Core core, Resources resources,
            Map<String, String> indexTypes,
            StyledEditorKit editorKit, StyledDocument styledDocument) {
        super(parent, resources.getString("indexesTitle"), DEFAULT_MODALITY_TYPE);

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.core = core;
        this.indexTypes = indexTypes;
        this.resources = resources;

//        decimalSeparator = resources.getString("decimalSeparator");
        decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();

        indexTypesRevert = new HashMap<String, String>(indexTypes.size());
        for(String integer : indexTypes.keySet()) {
            String typeInLanguage = resources.getString(indexTypes.get(integer));
            indexTypesRevert.put(typeInLanguage, integer);
        }

        initComponents();
        initBlocText(editorKit, styledDocument);
        this.setFocusTraversalPolicy(new LineFocusTraversalPolicy());
        initListeners();
    }

    /**
     * Initialisation des composants.
     * 
     * @since version 0.94 - version 1.02
     */
    private void initComponents() {
        int width = 740;
        int height = 500;
        int margin = 20;
        Dimension dim;

        int capacity = 32;
        indexesFields = new ArrayList<IndexFields>(capacity);
        order = new ArrayList<Component>(capacity*4);

        messageLabel = new JLabel(resources.getString("indexesMessage"));
        beginLabel = new JLabel(resources.getString("beginLabel"));
        endLabel = new JLabel(resources.getString("endLabel"));
        lengthLabel = new JLabel(resources.getString("lengthLabel"));
        typeLabel = new JLabel(resources.getString("typeLabel"));
        subtitleLabel = new JLabel(resources.getString("subtitleLabel"));

        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        beginLabel.setHorizontalAlignment(JLabel.CENTER);
        endLabel.setHorizontalAlignment(JLabel.CENTER);
        lengthLabel.setHorizontalAlignment(JLabel.CENTER);
        typeLabel.setHorizontalAlignment(JLabel.CENTER);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);

        FontMetrics metrics = getFontMetrics(messageLabel.getFont());
//        timeWidth = Math.max(metrics.stringWidth(lengthLabel.getText()),
//                Math.max(metrics.stringWidth(beginLabel.getText()),
//                metrics.stringWidth(endLabel.getText())));

        int max = 0;
        for(String typeInLanguage : indexTypesRevert.keySet()) {
            max = Math.max(max, metrics.stringWidth(typeInLanguage));
        }
        typeWidth = max + 30;

        dim = new Dimension(timeWidth, textHeight);
        beginLabel.setMinimumSize(dim);
        beginLabel.setPreferredSize(dim);
        endLabel.setMinimumSize(dim);
        endLabel.setPreferredSize(dim);
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

        JScrollPane scrollPane = new JScrollPane(mainPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setBorder(null);
        dim = new Dimension(width, height - 2*margin - 100);
        scrollPane.setPreferredSize(dim);

        selectButton = GuiUtilities.getSelectableButton(this,
                GuiUtilities.getImageIcon("all"),
                GuiUtilities.getImageIcon("none"),
                resources.getString("selectAll"));
        selectButton.setAlignmentX(CENTER_ALIGNMENT);
        addButton = GuiUtilities.getActionButton(this,
                GuiUtilities.getImageIcon("add"),
                GuiUtilities.getImageIcon("addOff"),
                resources.getString("addIndexes"));
        sortButton = GuiUtilities.getActionButton(this,
                GuiUtilities.getImageIcon("sort"),
                GuiUtilities.getImageIcon("sortOff"),
                resources.getString("sortIndexes"));
        eraseSelection = GuiUtilities.getActionButton(this,
                GuiUtilities.getImageIcon("eraseSelection"),
                GuiUtilities.getImageIcon("eraseSelectionOff"),
                resources.getString("eraseSelection"));
        validButton = GuiUtilities.getActionButton(this,
                GuiUtilities.getImageIcon("valid"),
                GuiUtilities.getImageIcon("validOff"),
                resources.getString("valid"));
        blocTextButton = GuiUtilities.getSelectableButton(this,
                GuiUtilities.getImageIcon("text"),
                GuiUtilities.getImageIcon("textOff"),
                resources.getString("notepad"));

        GridBagLayout labelLayout = new GridBagLayout();
        GridBagConstraints labelConstraints = new GridBagConstraints();
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(labelLayout);
        dim = new Dimension(width - 2*margin - 20, textHeight);
        labelPanel.setPreferredSize(dim);
        scrollPane.setColumnHeaderView(labelPanel);
        AdjustmentListener adjustmentListener = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                getContentPane().repaint();
            }
        };
        scrollPane.getVerticalScrollBar().addAdjustmentListener(adjustmentListener);
        scrollPane.getHorizontalScrollBar().addAdjustmentListener(adjustmentListener);

        labelConstraints.weightx = 0.0;
        labelLayout.setConstraints(selectButton, labelConstraints);
        labelPanel.add(selectButton);
        labelLayout.setConstraints(beginLabel, labelConstraints);
        labelPanel.add(beginLabel);
        labelLayout.setConstraints(lengthLabel, labelConstraints);
        labelPanel.add(lengthLabel);
        labelLayout.setConstraints(endLabel, labelConstraints);
        labelPanel.add(endLabel);
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
        constraints.insets = new Insets(0, margin, 0, margin);
        layout.setConstraints(scrollPane, constraints);
        panel.add(scrollPane);

        constraints.gridwidth = 1;
        constraints.weighty = 0.0;
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
     * @since version 0.94 - version 0.95.11
     */
    private void initBlocText(StyledEditorKit editorKit, StyledDocument styledDocument) {
        JTextPane textPane = new JTextPane();
        textPane.setEditorKit(editorKit);
	textPane.setDocument(styledDocument);

        blocTextDialog = new JDialog(this);
        blocTextDialog.getContentPane().setLayout(
                new BoxLayout(blocTextDialog.getContentPane(), BoxLayout.Y_AXIS));
        JScrollPane textScrollPane = new JScrollPane(textPane,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textScrollPane.setMinimumSize(new Dimension(335, 450));
        textScrollPane.setPreferredSize(new Dimension(335, 450));
        textScrollPane.setMaximumSize(new Dimension(335, 550));

        blocTextDialog.getContentPane().add(textScrollPane);
        blocTextDialog.pack();

        blocTextDialog.addWindowListener(new WindowAdapter() {
            /**
             * @since version 0.95.11
             */
            @Override
            public void windowClosing(WindowEvent e) {
                blocTextButton.setSelected(false);
            }
        });
    }//end initBlocText()

    /**
     * Initialisation des actions.
     * 
     * @since version 0.94 - version 0.95.10
     */
    private void initListeners() {
        validButton.addActionListener(new ActionListener() {
            /**
             * Action "Valider".
             * @since version 0.94 - version 0.95.10
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                validButton.setSelected(true);
                validAction();
                validButton.setSelected(false);
            }
        });//end validButton

        selectButton.addActionListener(new ActionListener() {
            /**
             * Action "Sélectionner".
             * @since version 0.94
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean select = !selectButton.isSelected();
                for(IndexFields indexFields : indexesFields) {
                    indexFields.checkBox.setSelected(select);
                }

                selectButton.setSelected(select);
                selectButton.setToolTipText(resources.getString(
                        select ? "selectNone" : "selectAll"));
            }
        });//end selectButton

//        invertSelection.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                for(IndexFields indexFields : indexesFields) {
//                    indexFields.checkBox.setSelected(
//                            !indexFields.checkBox.isSelected());
//                }
//            }
//        });//end invertSelection

        eraseSelection.addActionListener(new ActionListener() {
            /**
             * Action "Supprimer".
             * @since version 0.94
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                eraseSelection.setSelected(true);
                int option = GuiUtilities.showOptionDialog(getOwner(),
                        resources.getString("deleteIndexes"), null, null);
                if(option == GuiUtilities.YES_OPTION)
                    removeSelectedIndexes();
                eraseSelection.setSelected(false);
            }
        });//end eraseSelection

        addButton.addActionListener(new ActionListener() {
            /**
             * Action "Ajouter".
             * @since version 0.94 - version 0.95.10
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                addButton.setSelected(true);
                Object input = GuiUtilities.showInputDialog(getOwner(),
                        resources.getString("howIndexesInput"),
                        null, null);

                if(input != null) {
                    int nb = Utilities.parseStringAsInt((String)input);

                    if(nb > 0) {
                        addIndexes(nb);
                        validate();
                        updateSizeAndLocation();
                    }
                }
                addButton.setSelected(false);
            }
        });//end addButton

        sortButton.addActionListener(new ActionListener() {
            /**
             * Action "Trier".
             * @since version 0.94
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                sortIndexesFields();
            }
        });//end sortButton

        blocTextButton.addActionListener(new ActionListener() {
            /**
             * Action "Texte associé".
             * @since version 0.94
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                blocTextDialog.setVisible(!blocTextDialog.isVisible());
                blocTextButton.setSelected(blocTextDialog.isVisible());
            }
        });//end blocTextButton

        this.addWindowListener(new WindowAdapter() {
            /**
             * Action "Fermer".
             * @since version 0.94 - version 0.95.13
             */
            @Override
            public void windowClosing(WindowEvent e) {
                validButton.requestFocus();
                boolean changed = hasChanges();
                if(changed) {
                    int option = GuiUtilities.showOptionDialogWithCancel(getOwner(),
                            resources.getString("modifIndexes"), null, null);
                    if(option == GuiUtilities.YES_OPTION) {
                        validButton.setSelected(true);
                        validAction();
                        validButton.setSelected(false);
                    }
                    else if(option == GuiUtilities.NO_OPTION) {
                        core.removeNullIndex();
                        setVisible(false);
                    }
                }
                else {
                    setVisible(false);
                }
            }
        });
    }//end IndexesDialog(..)

    /**
     * Affiche la boite de dialogue.
     *
     * @param nbCreate le nombre d'index à créer.
     * @since version 0.94 - version 0.95.10
     */
    public void showDialog(int nbCreate) {
        core.sortIndexes();

        removeIndexes();
        for(Iterator<Index> it = core.indexesIterator(); it.hasNext();) {
            addIndex(it.next());
        }

        addIndexes(nbCreate);

        updateSizeAndLocation();
        this.setVisible(true);
    }

    /**
     * Actualiser la taille et la position de la fenêtre.
     * 
     * @since version 0.95.10
     */
    private void updateSizeAndLocation() {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

        this.pack();
        int width = getWidth();
        int height = getHeight();

        if(width < 600)
            width = 740;
        if(height > screenDim.height-50)
            height = screenDim.height-50;

        this.setSize(new Dimension(width, height));

//        this.setLocation((owner.getWidth()-this.getWidth())/2,
//                (owner.getHeight()-this.getHeight())/2);

        this.setLocation((screenDim.width - getWidth()) / 4,
                (screenDim.height - getHeight()) / 4);
        blocTextDialog.setLocation(getWidth() + getX(),
                getY() + (getHeight() - blocTextDialog.getHeight()) / 2);
    }

    /**
     * Modifie les textes
     * 
     * @since version 0.94 - version 1.01
     */
    public void updateLanguage() {
        setTitle(resources.getString("indexTitle"));

        messageLabel.setText(resources.getString("indexMessage"));
        beginLabel.setText(resources.getString("beginLabel"));
        endLabel.setText(resources.getString("endLabel"));
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
        for(String integer : indexTypes.keySet()) {
            String typeInLanguage = resources.getString(indexTypes.get(integer));
            indexTypesRevert.put(typeInLanguage, integer);
        }

        FontMetrics metrics = getFontMetrics(messageLabel.getFont());
//        timeWidth = Math.max(metrics.stringWidth(lengthLabel.getText()),
//                Math.max(metrics.stringWidth(beginLabel.getText()),
//                metrics.stringWidth(endLabel.getText())));

        int max = 0;
        for(String typeInLanguage : indexTypesRevert.keySet()) {
            max = Math.max(max, metrics.stringWidth(typeInLanguage));
        }
        typeWidth = max + 30;
    }

    /**
     * Mise à jour de la liste des types possibles avec un type initial.
     * 
     * @param type le type initial.
     * @param typeList le composant graphique de la liste.
     * @return si il faut autoriser la modification des champs de temps.
     * @since version 0.95.10 - version 1.01
     */
    private boolean updateTypeList(String type, JComboBox typeList) {
        typeList.removeAllItems();
        if(type.contentEquals(Index.PLAY)|| type.contentEquals(Index.RECORD)) {
            typeList.addItem(resources.getString(indexTypes.get(Index.PLAY)));
            typeList.addItem(resources.getString(indexTypes.get(Index.RECORD)));
        }
        else if(type.contentEquals(Index.BLANK)||type.contentEquals(Index.BLANK_BEEP)) {
            typeList.addItem(resources.getString(indexTypes.get(Index.BLANK)));
            typeList.addItem(resources.getString(indexTypes.get(Index.BLANK_BEEP)));
        }
        else {
            typeList.addItem(resources.getString(indexTypes.get(type)));
        }

        typeList.setSelectedItem(resources.getString(indexTypes.get(type)));
        typeList.setEnabled(typeList.getItemCount() > 1);

        boolean enableTimeField = true;
        if(type.contentEquals(Index.FILE) || type.contentEquals(Index.REPEAT)) {
            //|| type.contentEquals(Index.SPEED)
            //pas de modification de la durée
            enableTimeField = false;
        }

        return enableTimeField;
    }

    /**
     * Convertie la chaîne de caractères représentant le temps en millisecondes.
     *
     * @param field le champ texte contenat le temps formaté.
     * @return le temps en millisecondes.
     * @since version 0.94 - version 0.99
     */
    private long getValue(JTextField field) {
        String value = field.getText();
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
     * @since version 0.99
     */
    private void setValue(JTextField textField, long value) {
        textField.setText(
                String.format("%1$tM:%1$tS" + decimalSeparator + "%1$tL", value));
    }

    /**
     * Mise à jour des champs avec vérifications des valeurs.
     *
     * @param beginField le champs pour le début d'index.
     * @param endField le champs pour la fin d'index.
     * @param lengthField le champs pour la longueur d'index.
     * @since version 0.94 - version 0.99
     */
    private void updateFields(JTextField beginField, JTextField endField,
            JTextField lengthField) {
        long begin = getValue(beginField);
        long end = getValue(endField);

        if(begin < 0 || begin > core.getRecordTimeMax()) {
            begin = 0;
            setValue(beginField, begin);
            beginField.setBackground(errorColor);
            GuiUtilities.showMessageDialog(
                    this, resources.getString("beginError"));
            beginField.setBackground(normalColor);
            beginField.requestFocusInWindow();
        }//end if

        if(end > core.getRecordTimeMax()) {
            end = core.getRecordTimeMax();
            setValue(endField, end);
            endField.setBackground(errorColor);
            GuiUtilities.showMessageDialog(
                    this, resources.getString("endError"));
            endField.setBackground(normalColor);
            endField.requestFocusInWindow();
        }//end if

        if(end < begin && end != 0) {
            end = begin;
            setValue(endField, end);
            endField.setBackground(errorColor);
            GuiUtilities.showMessageDialog(
                    this, resources.getString("beginEndError"));
            endField.setBackground(normalColor);
            endField.requestFocusInWindow();
        }

        int beginCaretPosition = beginField.getCaretPosition();
        int endCaretPosition = endField.getCaretPosition();

        setValue(beginField, begin);
        setValue(endField, end);
        setValue(lengthField, end-begin);

        beginField.setCaretPosition(beginCaretPosition);
        endField.setCaretPosition(endCaretPosition);
    }

    /**
     * Test la validité des changements et modifie les index en conséquence.
     * 
     * @since version 0.95.11 - version 0.99
     */
    private void validAction() {
        boolean overlapped = false;
        boolean identical = false;
        long insertTime = 0;
        int size = indexesFields.size();
        for(IndexFields indexFields : indexesFields) {
            //si les champs de début et de fin sont égaux, on passe au
            //suivant (index éliminé lors de la validation)
            if(indexFields.beginField.getText().equals(
                    indexFields.endField.getText()))
                continue;

            if(core.getIndexWithId(indexFields.id).isTimeLineModifier())
                insertTime += (getValue(indexFields.endField)
                        - getValue(indexFields.beginField));

            indexFields.beginField.setBackground(normalColor);
            indexFields.endField.setBackground(normalColor);

            if(isBeginOverlapped(indexFields.beginField)) {
                indexFields.beginField.setBackground(errorColor);
                overlapped = true;
            }

            if(isEndOverlapped(indexFields.endField)) {
                indexFields.endField.setBackground(errorColor);
                overlapped = true;
            }

            if(isIdentical(indexFields)) {
                indexFields.beginField.setBackground(identicalColor);
                indexFields.endField.setBackground(identicalColor);
                identical = true;
            }
        }//end for

        if(overlapped) {
            int choix = GuiUtilities.showOptionDialog(this,
                    resources.getString("overlappedIndexes"),
                    null, null);
            if(choix != GuiUtilities.YES_OPTION)
                return;
        }//end overlapped

        if(identical) {
            int choix = GuiUtilities.showOptionDialog(this,
                    resources.getString("identicalIndexes"),
                    null, null);
            if(choix != GuiUtilities.YES_OPTION)
                return;
        }//end identical

        if(insertInitialTime-insertTime > core.getRemainingTime()) {
            GuiUtilities.showMessageDialog(this,
                    resources.getString("insertionDurationMessage"),
                    (insertInitialTime-insertTime)/1000+1,
                    core.getRemainingTime()/1000,
                    core.getDurationMax()/1000);
            return;
        }

        for(int i=0; i<size; i++) {
            IndexFields indexFields = indexesFields.get(i);
            long begin = getValue(indexFields.beginField);
            long end = getValue(indexFields.endField);
            String type = indexTypesRevert.get(
                    (String)indexFields.typeList.getSelectedItem());
            String subtitle = indexFields.subtitleField.getText();
            if(subtitle != null && subtitle.isEmpty())
                subtitle = null;

            if(core.getIndexWithId(indexFields.id).isTimeLineModifier()) {
                long initLength = core.getIndexWithId(indexFields.id).getLength();
                long length = end - begin;
                if(initLength - length > core.getRemainingTime()) {
                    indexFields.beginField.setBackground(errorColor);
                    indexFields.endField.setBackground(errorColor);
                    GuiUtilities.showMessageDialog(this,
                            resources.getString("insertionDurationMessage"),
                            (initLength-length)/1000+1,
                            core.getRemainingTime()/1000,
                            core.getDurationMax()/1000);
                    return;
                }
            }

            if(isChanged(indexFields))
                core.setMediaIndex(indexFields.id, begin, end, type, subtitle, -1);
        }//end for

        core.removeNullIndex();
        core.sortIndexes();

        setVisible(false);
    }//end validAction()

    /**
     * Teste si des changements ont été effectués sur les index.
     * 
     * @return si des changements ont eu lieu.
     * @since version 0.95.13
     */
    private boolean hasChanges() {
        boolean changed = false;
        for(IndexFields indexFields : indexesFields) {
            indexFields.beginField.setBackground(normalColor);
            indexFields.endField.setBackground(normalColor);

            if(isChanged(indexFields)){
                indexFields.beginField.setBackground(changeColor);
                indexFields.endField.setBackground(changeColor);
                changed = true;
                break;
            }
        }
        return changed;
    }

    /**
     * Teste si des changements ont été effectués sur l'index.
     * 
     * @param indexFields les champs de l'index.
     * @return si des changements ont eu lieu.
     * @since version 0.95.11 - version 0.99
     */
    private boolean isChanged(IndexFields indexFields) {
        long begin = getValue(indexFields.beginField);
        long end = getValue(indexFields.endField);

        String type = indexTypesRevert.get(
                (String)indexFields.typeList.getSelectedItem());

        String subtitle = indexFields.subtitleField.getText();
        if(subtitle != null && subtitle.isEmpty())
            subtitle = null;

        Index index = core.getIndexWithId(indexFields.id);

        if(begin != index.getInitialTime())
            return true;
        if(end != index.getFinalTime())
            return true;
  
        if(!type.contentEquals(index.getType()))
            return true;

        if(subtitle == null)
            return (index.getSubtitle() != null);
        else if(index.getSubtitle() == null)
            return true;
        else
            return !subtitle.contentEquals(index.getSubtitle());
    }

    /**
     * Indique si le champ est compris dans un index.
     *
     * @param beginField le champs à tester.
     * @return <code>true</code>si le champ est compris dans un autre index.
     * @since version 0.94 - version 0.99
     */
    private boolean isBeginOverlapped(JTextField beginField) {
        long time = getValue(beginField);
        for(IndexFields indexFields : indexesFields) {
            if(beginField == indexFields.beginField) {
                continue;
            }

            if(time >= getValue(indexFields.beginField)
                    && time < getValue(indexFields.endField))
                return true;
        }//end for
        return false;
    }

    /**
     * Indique si le champ est compris dans un index.
     *
     * @param endField le champs à tester.
     * @return <code>true</code>si le champ est compris dans un autre index.
     * @since version 0.94 - version 0.99
     */
    private boolean isEndOverlapped(JTextField endField) {
        long time = getValue(endField);
        for(IndexFields indexFields : indexesFields) {
            if(endField == indexFields.endField)
                continue;

            if(time > getValue(indexFields.beginField)
                    && time <= getValue(indexFields.endField))
                return true;
        }//end for
        return false;
    }

    /**
     * Indique si un index est identique à un autre temps (temps de début et
     * temps de fin identiques).
     *
     * @param fields les champs graphiques de l'index.
     * @return <code>true</code> si l'index est identique à un autre index.
     * @since version 0.94 - version 0.96
     */
    private boolean isIdentical(IndexFields fields) {
        String begin = fields.beginField.getText();
        String end = fields.endField.getText();

        for(IndexFields indexFields : indexesFields) {
            if(indexFields == fields)
                continue;
            if(indexFields.beginField.getText().equals(begin)
                    && indexFields.endField.getText().equals(end))
                return true;
        }
        return false;
    }

    /**
     * Ajoute un index dans la table d'affichage.
     *
     * @param index l'index à ajouter.
     * @since version 0.94
     */
    private void addIndex(Index index) {
        addIndex(index.getId(), index.getInitialTime(), index.getFinalTime(),
                index.getType(), index.getSubtitle());
        if(index.isTimeLineModifier())
            insertInitialTime += index.getLength();
    }

    /**
     * Ajoute un index dans la table d'affichage.
     *
     * @param begin le temps de début de l'index.
     * @param end le temps de fin de l'index.
     * @param type le type de l'index.
     * @param subtitle le soustitre associé à l'index.
     * @since version 0.94 - version 1.02
     */
    private void addIndex(long id,
            long begin, long end, String type, String subtitle) {
        IndexFields fields = new IndexFields(id);
        boolean enableTimeField = updateTypeList(type, fields.typeList);
        fields.beginField.setEnabled(enableTimeField);
        fields.endField.setEnabled(enableTimeField);
        Dimension dim = new Dimension(
                2*mainPanel.getPreferredSize().width, textHeight);
        fields.setMaximumSize(dim);

        indexesFields.add(fields);

        mainPanel.add(fields);
        order.add(fields.beginField);
        order.add(fields.endField);
        order.add(fields.typeList);
        order.add(fields.subtitleField);

        setValue(fields.beginField, begin);
        setValue(fields.endField, end);
        setValue(fields.lengthField, end-begin);

        fields.subtitleField.setText(subtitle);
    }//end addIndex(long begin, long end, int type, String subtitile)

    /**
     * Ajoute des indes dans la table.
     *
     * @param cnt le nombre d'index à ajouter.
     * @since version 0.94 - version 1.02
     */
    private void addIndexes(int cnt) {
        for(int i=0; i<cnt; i++) {
            long id = core.addNullIndex();
            addIndex(id, 0, 0, Index.PLAY, null);
        }

        int nb = mainPanel.getComponentCount();
        Dimension dim = new Dimension(
                mainPanel.getPreferredSize().width, nb*textHeight);
        mainPanel.setPreferredSize(dim);
    }

    /**
     * Supprime tous les index de la fenêtre.
     *
     * @since version 0.94 - version 1.02
     */
    private void removeIndexes() {
        indexesFields.clear();
        order.clear();
        mainPanel.removeAll();
    }

    /**
     * Retourne l'indice de l'index de la table qui a le temps de début le plus
     * petit.
     *
     * @return l'indice.
     * @since version 0.94 - version 0.99
     */
    private IndexFields getFirstIndex() {
        IndexFields first = null;
        long time = Long.MAX_VALUE;
        for(IndexFields indexFields : indexesFields) {
            long currentTime = getValue(indexFields.beginField);
            if(currentTime < time) {
                first = indexFields;
                time = currentTime;
            }//end if
        }//end for

        return first;
    }

    /**
     * Trie par ordre croissant du temps de départ des index de la table.
     *
     * @since version 0.94 - version 0.99
     */
    private void sortIndexesFields() {
        int capacity = indexesFields.size();
        long[] id = new long[capacity];
        boolean[] check = new boolean[capacity];
        String[] begin = new String[capacity];
        String[] end = new String[capacity];
        String[] type = new String[capacity];
        String[] subtitle = new String[capacity];

        for(int i=0; i<capacity; i++) {
            IndexFields indexFields = getFirstIndex();
            id[i] = indexFields.id;
            check[i] = indexFields.checkBox.isSelected();
            begin[i] = indexFields.beginField.getText();
            end[i] = indexFields.endField.getText();
            type[i] = indexTypesRevert.get(
                    (String)indexFields.typeList.getSelectedItem());
            subtitle[i] = indexFields.subtitleField.getText();

            setValue(indexFields.beginField, Long.MAX_VALUE);
        }//end for

        order.clear();
        for(int i=0; i<capacity; i++) {
            IndexFields indexFields = indexesFields.get(i);
            indexFields.id = id[i];
            indexFields.checkBox.setSelected(check[i]);
            indexFields.beginField.setText(begin[i]);
            indexFields.endField.setText(end[i]);
            indexFields.subtitleField.setText(subtitle[i]);
            updateFields(indexFields.beginField, indexFields.endField,
                    indexFields.lengthField);
            boolean enableTimeField = updateTypeList(type[i], indexFields.typeList);
            indexFields.beginField.setEnabled(enableTimeField);
            indexFields.endField.setEnabled(enableTimeField);

            order.add(indexFields.beginField);
            order.add(indexFields.endField);
            order.add(indexFields.typeList);
            order.add(indexFields.subtitleField);
        }//end for
    }//sortIndexesFields()

    /**
     * Supprime les index sélectionné.
     *
     * @since version 0.95.10 - version 1.02
     */
    private void removeSelectedIndexes() {
        int capacity = indexesFields.size();
        List<IndexFields> newIndexesFields = new ArrayList<IndexFields>(capacity);
        List<Long> removeIndexIds = new ArrayList<Long>(capacity);

        order.clear();
        mainPanel.removeAll();

        for(IndexFields indexFields : indexesFields) {
            if(!indexFields.checkBox.isSelected()) {
                newIndexesFields.add(indexFields);

                mainPanel.add(indexFields);
                order.add(indexFields.beginField);
                order.add(indexFields.endField);
                order.add(indexFields.typeList);
                order.add(indexFields.subtitleField);
            }
            else {
                removeIndexIds.add(indexFields.id);
            }
        }

        core.removeIndex(removeIndexIds);
        removeIndexIds.clear();

        indexesFields.clear();
        indexesFields = newIndexesFields;
        updateSizeAndLocation();
    }

    /**
     * Classe pour la gestion de l'ordre de focus des composants.
     *
     * @since version 0.94
     */
    private class LineFocusTraversalPolicy extends FocusTraversalPolicy {

        @Override
        public Component getComponentAfter(Container container,
                Component aComponent) {
            if(order.isEmpty())
                return null;

            int index = order.indexOf(aComponent) + 1;
            if(index == order.size())
                index = 0;
            return order.get(index);
        }

        @Override
        public Component getComponentBefore(Container container,
                Component aComponent) {
            if(order.isEmpty())
                return null;

            int index = order.indexOf(aComponent) - 1;
            if(index < 0)
                index = order.size() - 1;
            return order.get(index);
        }

        @Override
        public Component getDefaultComponent(Container container) {
            if(order.isEmpty())
                return null;
            return order.get(0);
        }

        @Override
        public Component getLastComponent(Container container) {
            if(order.isEmpty())
                return null;
            return order.get(order.size()-1);
        }

        @Override
        public Component getFirstComponent(Container container) {
            if(order.isEmpty())
                return null;
            return order.get(0);
        }
    }//end class LineFocusTraversalPolicy

    /**
     * Classe recensant tous les composants graphiques nécessaires pour un index.
     * 
     * @since version 0.95.10
     * @version 1.02
     */
    private class IndexFields extends JPanel {
        private static final long serialVersionUID = 10200L;

        /** id de l'index */
        protected long id;
        /** Champs de sélection des index */
        protected JCheckBox checkBox;
        /** Champs formatés pour les temps de départ des index */
        protected JFormattedTextField beginField;
        /** Champ formaté pour le temps de fin de l'index */
        protected JFormattedTextField endField;
        /** Champ non modifiable pour la durée de l'index */
        protected JTextField lengthField;
        /** Liste déroulante pour le type de l'index */
        protected JComboBox typeList;
        /** Zone de texte pour le soustitre de l'index */
        protected JTextField subtitleField;

        private IndexFields(long id) {
            this.id = id;

            MaskFormatter beginFormatter = null;
            MaskFormatter endFormatter = null;
            try {
                beginFormatter = new MaskFormatter("##:##"+ decimalSeparator +"###");
                beginFormatter.setPlaceholderCharacter('0');
                endFormatter = new MaskFormatter("##:##"+ decimalSeparator +"###");
                endFormatter.setPlaceholderCharacter('0');
            } catch(ParseException e) {
                Edu4Logger.error(e);
            }//end try

            checkBox = new JCheckBox();
            beginField = new JFormattedTextField(beginFormatter);
            endField = new JFormattedTextField(endFormatter);
            lengthField = new JTextField();
            typeList = new JComboBox();
            subtitleField = new JTextField();

            Dimension dim = new Dimension(40, textHeight);
            checkBox.setMinimumSize(dim);
            checkBox.setMaximumSize(dim);
            checkBox.setPreferredSize(dim);
            dim = new Dimension(timeWidth, textHeight);
            beginField.setMinimumSize(dim);
            beginField.setMaximumSize(dim);
            beginField.setPreferredSize(dim);
            endField.setMinimumSize(dim);
            endField.setMaximumSize(dim);
            endField.setPreferredSize(dim);
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
            endField.setHorizontalAlignment(JTextField.RIGHT);
            lengthField.setHorizontalAlignment(JTextField.CENTER);
            subtitleField.setHorizontalAlignment(JTextField.LEADING);

            lengthField.setEditable(false);

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
            layout.setConstraints(endField, constraints);
            this.add(endField);
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
                    updateFields(beginField, endField, lengthField);
                }
            });

            endField.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    endField.setCaretPosition(defautCaretPosition);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    updateFields(beginField, endField, lengthField);
                }
            });

            //pour éviter d'avoir un reste d'image quand le programme affiche ou
            //masque la liste
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
    }//end class IndexFields

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
////        indexTypesMap.put(Index.IMAGE, "imageType");
//        indexTypesMap.put(Index.SELECTION, "selectionType");
//
//        Index index = new Index(Index.PLAY, 0);
//        index.setFinalTime(1000);
//
//        try {
//            Core core = new Core(null);
//            IndexesDialog dialog = new IndexesDialog(frame, core,
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

}//end
