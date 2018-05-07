package eestudio.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.MaskFormatter;

import eestudio.Core;
import eestudio.Index;
import eestudio.utils.Edu4Logger;
import eestudio.utils.Utilities;

/*
 * v0.95.10: Listes de types différentes suivant le type d'index initial.
 * v0.95.10: ajout de private void updateTypeList(String type) [liste de types]
 * v0.95.10: ajout de private JButton beginPlus;
 * v0.95.10: ajout de private JButton beginMinus;
 * v0.95.10: ajout de private JButton endPlus;
 * v0.95.10: ajout de private JButton endMinus;
 * v0.95.10: modif de int currentIndex en Index currentIndex
 * v0.95.10: modif de initComponents() [liste de types, JButton, decimalSeparator]
 * v0.95.10: modif de updateLanguage() [liste de types, decimalSeparator]
 * v0.95.10: modif de initValues(int indice) en initValues(Index index) [liste de types]
 * v0.95.10: modif de saveIndex(long begin, long end, String type, String subtitle)
 *        [vérification longueur index pour la taille de la bande]
 * 
 * v0.95.11: ajout de private boolean isChanged()
 * v0.95.11: ajout de private int showCancelableMessage(String message)
 * v0.95.11: supp de private JButton stopButton;
 * v0.95.11: supp de private JButton closeButton;
 * v0.95.11: modif de tous les boutons (réprésentation sous forme d'images)
 * v0.95.11: modif de IndexDialog(.., Image icone) en IndexDialog(.., List<Image> icones)
 * v0.95.11: modif de initComponents() [supp JB, images JB, action listeners test:
 *           enabled -> selected, test changement sur next, previous, close,
 *           default close operation: hide -> nothing]
 * v0.95.11: modif de updateLanguage() [supp JB, images JB]
 * v0.95.11: modif de updateButtons(boolean playing) [playButton=stopButton,
 *           test: enabled -> selected]
 * v0.95.11: modif de saveIndex(long begin, long end) [test soutitre isEmpty()]
 * 
 * v0.95.12: modif de IndexDialog(Core core, Map<String, String> indexTypes,
 *           Resources resources, Window parent, List<Image> icones) en
 *           IndexDialog(Window parent, List<Image> icones, Core core,
 *           Resources resources, Map<String, String> indexTypes)
 * 
 * v0.96: modif de initComponents() [fixe la largeur des champs texte à 100
 *        (plus de calcul de longueur de chaînes de caractères)]
 * v0.96: modif de updateLanguage() [supp redimensionnement des champs]
 * 
 * v0.97: ajout de private MaskFormatter beginFormatter;
 * v0.97: ajout de private MaskFormatter endFormatter;
 * v0.97: ajout de private void action(final Object source)
 * v0.97: ajout de private void initListener() [même listener validButton,
 *        cancelButton, previousButton, nextButton et windowClosing ;
 *        supp core.audioPause() ; utilisation de dispose()]
 * v0.97: modif de IndexDialog(...) [defaultCloseOperation = DO_NOTHING_ON_CLOSE]
 * v0.97: modif de initComponents() [listeners à part et MaskFormatter]
 * v0.97: modif de updateLanguage() [modif MaskFormatter]
 * v0.97: modif de updateButtons(boolean playing) [mise à jour des boutons
 *        validButton, cancelButton, previousButton, nextButton]
 * v0.97: modif de saveIndex(long begin, long end, String type, String subtitle)
 *        [récupération de l'index modifié]
 * 
 * v0.98: modif de getValue(String value) [use Utilities.parseStringAsLong]
 * 
 * v0.99: ajout de private void setValue(JTextField textField, long value)
 * v0.99: modif de IndexDialog(Window parent, List<Image> icones, ...) en
 *        IndexDialog(Window parent, ...)
 * v0.99: modif de initComponents() [GuiUtilities.getImageIcon]
 * v0.99: modif de getValue(String value) en getValue(JTextField field)
 * v0.99: modif de fieldUpdateDialog() [getValue]
 * v0.99: modif de updateDialog() [setValue]
 * 
 * v1.01: ajout de private float initialSpeed;
 * v1.01: ajout de private JLabel speedLabel;
 * v1.01: ajout de private HorizontalSlider speedSlider;
 * v1.01: ajout de private float getSpeed()
 * v1.01: ajout de private void setSpeed(float speed)
 * v1.01: modif de initComponents() [GridBagLayout, BackgroundPanel, speed]
 * v1.01: modif de initListener() [ajout de ComboBox listener]
 * v1.01: modif de updateLanguage() [add speedLabel et modif noms ressources]
 * v1.01: modif de updateTypeList(String type) [supp test SPEED]
 * v1.01: modif de initValues(Index index) [speed]
 * v1.01: modif de saveIndex(long, long, ..) en saveIndex(long, long, .., float speed)
 * v1.01: modif de saveIndex(long, long) [speed]
 * v1.01: modif de action(final Object source) [speed]
 * v1.01: modif de isChanged() [speed]
 * 
 * v1.02: modif de initComponents() [gestion des poids pour agrrandir la zone de
 *        texte]
 */

/**
 * Boite de dialogue pour redimensionner un index.
 *
 * @author Fabrice Alleau
 * @since version 0.94
 * @version 1.02
 */
public class IndexDialog extends JDialog {
    private static final long serialVersionUID = 10100L;

    /** Position par défaut dans les champs de temps */
    private final int defautCaretPosition = 3;

    /** Resources textuelles */
    private Resources resources;
    /** Noyau pour la création des index */
    private Core core;

    /** Map pour donner la référence de texte selon le type de l'index */
    private Map<String, String> indexTypes;
    /** Map pour donner le type d'index selon le texte de la langue */
    private Map<String, String> indexTypesRevert;

    /** Séparateur pour les chiffres */
    private char decimalSeparator;

    /** Pas pour les boutons plus et moins */
    private final long STEP = 500;//en millisecondes

    /** Numéro de l'index courant */
    private Index currentIndex;
    /** Temps de départ initial de l'index */
    private long initialBeginTime;
    /** Temps de fin initial de l'index */
    private long initialEndTime;
    /** Type initial de l'index */
    private String initialType;
    /** Soustitre initial de l'index */
    private String initialSubtitle;
    /** Vitesse initiale de l'index */
    private float initialSpeed;

    /** Temps minimal pour le temps de début de l'index */
    private long beginMin;
    /** Temps maximal pour le temps de fin de l'index */
    private long endMax;

    /** Temps de départ dans la fenêtre */
    private long begin;
    /** Temps de fin dans la fenêtre */
    private long end;

    /** Masque pour modifier le temps de départ */
    private MaskFormatter beginFormatter;
    /** Masque pour modifier le temps de fin */
    private MaskFormatter endFormatter;

    /** Message explicatif */
    private JLabel messageLabel;
    /** Label pour le temps de départ de l'index */
    private JLabel beginLabel;
    /** Label pour le temps de fin de l'index */
    private JLabel endLabel;
    /** Label pour la durée de l'index */
    private JLabel lengthLabel;
    /** Label pour le type de l'index */
    private JLabel typeLabel;
    /** Label pour la vitesse de l'index */
    private JLabel speedLabel;
    /** Label pour le soustitre de l'index */
    private JLabel subtitleLabel;

    /** Champ formaté pour le temps de départ de l'index */
    private JFormattedTextField beginField;
    /** Champ formaté pour le temps de fin de l'index */
    private JFormattedTextField endField;
    /** Champ non modifiable pour la durée de l'index */
    private JTextField lengthField;
    /** Liste déroulante pour le type de l'index */
    private JComboBox typeList;
    /** Slider pour la vitesse sur l'index */
    private HorizontalSlider speedSlider;
    /** Zone de texte pour le soustitre de l'index */
    private JTextArea subtitleField;

    /** Bouton pour aller à l'index précédent */
    private JButton previousButton;
    /** Bouton pour aller à l'index suivant */
    private JButton nextButton;

    /** Bouton pour lire l'index */
    private JButton playButton;
    /** Bouton pour aller au début de l'index */
    private JButton beginIndexButton;

    /** Bouton pour augmenter le temps de départ */
    private JButton beginPlus;
    /** Bouton pour diminuer le temps de départ */
    private JButton beginMinus;
    /** Bouton pour augmenter le temps de fin */
    private JButton endPlus;
    /** Bouton pour diminuer le temps de fin */
    private JButton endMinus;

    /** Bouton pour valider les changements sur l'index */
    private JButton validButton;
    /** Bouton pour annuler les changements sur l'index */
    private JButton cancelButton;

    /**
     * Initialisation de la boite de dialogue.
     *
     * @param core le coeur de l'application.
     * @param indexTypes map pour les resources textuelles suivant le type d'index.
     * @param resources les resources pour les textes.
     * @param parent la fenêtre parente (peut être null).
     * @since version 0.94 - version 0.99
     */
    public IndexDialog(Window parent, Core core, Resources resources,
            Map<String, String> indexTypes) {
        super(parent, resources.getString("indexTitle"), DEFAULT_MODALITY_TYPE);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.resources = resources;
        this.core = core;
        this.indexTypes = indexTypes;
        indexTypesRevert = new HashMap<String, String>(indexTypes.size());

        initComponents();
        initListener();
    }

    /**
     * Initialise les composants.
     *
     * @since version 0.94 - version 1.02
     */
    private void initComponents() {
        int width = 352;
        int timeWidth = 84;
        int timeHeight = 20;
        int margin = 20;
        int cellWidth = 26;
        int supp = 4;

        Dimension dim;

        messageLabel = new JLabel(resources.getString("indexMessage"));
        beginLabel = new JLabel(resources.getString("beginLabel"));
        endLabel = new JLabel(resources.getString("endLabel"));
        lengthLabel = new JLabel(resources.getString("lengthLabel"));
        typeLabel = new JLabel(resources.getString("typeLabel"));
        speedLabel = new JLabel(resources.getString("speedLabel"));
        subtitleLabel = new JLabel(resources.getString("subtitleLabel"));

//        decimalSeparator = resources.getString("decimalSeparator");
        decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
        try {
            beginFormatter = new MaskFormatter("##:##"+ decimalSeparator +"###");
            beginFormatter.setPlaceholderCharacter('0');
            endFormatter = new MaskFormatter("##:##"+ decimalSeparator +"###");
            endFormatter.setPlaceholderCharacter('0');
        } catch(ParseException e) {
            Edu4Logger.error(e);
        }//end try

        beginField = new JFormattedTextField(beginFormatter);
        beginField.setHorizontalAlignment(JTextField.CENTER);
        endField = new JFormattedTextField(endFormatter);
        endField.setHorizontalAlignment(JTextField.CENTER);
        lengthField = new JTextField();
        lengthField.setEditable(false);
        lengthField.setHorizontalAlignment(JTextField.CENTER);

        dim = new Dimension(timeWidth, timeHeight);
        beginField.setPreferredSize(dim);
        beginField.setMinimumSize(dim);
        endField.setPreferredSize(dim);
        endField.setMinimumSize(dim);
        lengthField.setPreferredSize(dim);
        lengthField.setMinimumSize(dim);

        beginPlus = GuiUtilities.getActionButton(this,
                GuiUtilities.getImageIcon("plus"), null, resources.getString("plus"));
        beginMinus = GuiUtilities.getActionButton(this,
                GuiUtilities.getImageIcon("minus"), null, resources.getString("minus"));
        endPlus = GuiUtilities.getActionButton(this,
                GuiUtilities.getImageIcon("plus"), null, resources.getString("plus"));
        endMinus = GuiUtilities.getActionButton(this,
                GuiUtilities.getImageIcon("minus"), null, resources.getString("minus"));

        typeList = new JComboBox();
        dim = new Dimension(width-2*margin, timeHeight);
        typeList.setMinimumSize(dim);
        typeList.setPreferredSize(dim);
        for(String type : indexTypes.keySet()) {
            String typeInLanguage = resources.getString(indexTypes.get(type));
            indexTypesRevert.put(typeInLanguage, type);
        }

        dim = new Dimension(width-2*margin, 18);
        speedSlider = new HorizontalSlider(this, dim.width, dim.height);
        speedSlider.setTickBounds(Index.RATE_MIN, Index.RATE_MAX, true);
        speedSlider.setPreferredSize(dim);
        speedSlider.setMinimumSize(dim);

        subtitleField = new JTextArea();
        subtitleField.setLineWrap(true);//retour à la ligne autaumatique
        subtitleField.setWrapStyleWord(true);//ne découpe pas les mots en fin de ligne
        subtitleField.setRows(5);
        JScrollPane scrollPane = new JScrollPane(subtitleField, 
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setBorder(null);
        dim = new Dimension(width-2*margin, 150);
        scrollPane.setMinimumSize(dim);
        scrollPane.setPreferredSize(dim);

        playButton = GuiUtilities.getSelectableButton(this,
                GuiUtilities.getImageIcon("play"),
                GuiUtilities.getImageIcon("pause"),
                resources.getString("play"));
        beginIndexButton = GuiUtilities.getActionButton(this,
                GuiUtilities.getImageIcon("back"),
                GuiUtilities.getImageIcon("backOff"),
                resources.getString("beginIndex"));

        validButton = GuiUtilities.getActionButton(this,
                GuiUtilities.getImageIcon("valid"),
                GuiUtilities.getImageIcon("validOff"),
                resources.getString("valid"));
        cancelButton = GuiUtilities.getActionButton(this,
                GuiUtilities.getImageIcon("cancel"),
                GuiUtilities.getImageIcon("cancelOff"),
                resources.getString("cancel"));

        previousButton = GuiUtilities.getActionButton(this,
                GuiUtilities.getImageIcon("previous"),
                GuiUtilities.getImageIcon("previousOff"),
                resources.getString("previous"));
        nextButton = GuiUtilities.getActionButton(this,
                GuiUtilities.getImageIcon("next"),
                GuiUtilities.getImageIcon("nextOff"),
                resources.getString("next"));

        playButton.setEnabled(true);

        BackgroundPanel panel = new BackgroundPanel(width, 550, 15);//new JPanel();//
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setLayout(layout);

        constraints.weightx = 1.0;
        constraints.weighty = 0.0;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(margin, margin, margin, margin);
        constraints.anchor = GridBagConstraints.BASELINE;
        layout.setConstraints(messageLabel, constraints);
        panel.add(messageLabel);

        constraints.gridx = 0;
        constraints.gridy += 1;
        constraints.gridwidth = 4;
        constraints.insets = new Insets(0, margin, 0, margin);
        constraints.anchor = GridBagConstraints.BASELINE;
        layout.setConstraints(beginLabel, constraints);
        panel.add(beginLabel);

        constraints.gridx = 4;
        constraints.gridwidth = 4;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.BASELINE;
        layout.setConstraints(lengthLabel, constraints);
        panel.add(lengthLabel);

        constraints.gridx = 8;
        constraints.gridwidth = 4;
        constraints.insets = new Insets(0, margin, 0, margin);
        constraints.anchor = GridBagConstraints.BASELINE;
        layout.setConstraints(endLabel, constraints);
        panel.add(endLabel);

        constraints.gridx = 0;
        constraints.gridy += 1;
        constraints.gridwidth = 4;
        constraints.insets = new Insets(0, margin, 0, 0);
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        layout.setConstraints(beginField, constraints);
        panel.add(beginField);

        constraints.gridx = 4;
        constraints.gridwidth = 4;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.BASELINE;
        layout.setConstraints(lengthField, constraints);
        panel.add(lengthField);

        constraints.gridx = 8;
        constraints.gridwidth = 4;
        constraints.insets = new Insets(0, 0, 0, margin);
        constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        layout.setConstraints(endField, constraints);
        panel.add(endField);

        constraints.gridx = 1;
        constraints.gridy += 1;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        layout.setConstraints(beginMinus, constraints);
        panel.add(beginMinus);

        constraints.gridx = 2;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        layout.setConstraints(beginPlus, constraints);
        panel.add(beginPlus);

        constraints.gridx = 9;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        layout.setConstraints(endMinus, constraints);
        panel.add(endMinus);

        constraints.gridx = 10;
        constraints.gridwidth = 1;
        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        layout.setConstraints(endPlus, constraints);
        panel.add(endPlus);

        constraints.gridx = 0;
        constraints.gridy += 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(margin, margin, 0, margin);
        constraints.anchor = GridBagConstraints.BASELINE;
        layout.setConstraints(typeLabel, constraints);
        panel.add(typeLabel);

        constraints.gridx = 0;
        constraints.gridy += 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, margin, 0, margin);
        layout.setConstraints(typeList, constraints);
        panel.add(typeList);

        constraints.gridx = 0;
        constraints.gridy += 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(margin, margin, 0, margin);
        layout.setConstraints(subtitleLabel, constraints);
        panel.add(subtitleLabel);

        constraints.gridx = 0;
        constraints.gridy += 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(0, margin, margin, margin);
        layout.setConstraints(scrollPane, constraints);
        panel.add(scrollPane);

        constraints.gridx = 0;
        constraints.gridy += 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(0, margin, 5, margin);
        layout.setConstraints(speedLabel, constraints);
        panel.add(speedLabel);

        constraints.gridx = 0;
        constraints.gridy += 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(0, margin, margin, margin);
        layout.setConstraints(speedSlider, constraints);
        panel.add(speedSlider);

        constraints.gridx = 0;
        constraints.gridy += 1;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(0, margin, 0, supp);
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        layout.setConstraints(beginIndexButton, constraints);
        panel.add(beginIndexButton);

        constraints.gridx = 3;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(0, 19, 0, 0);
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        layout.setConstraints(playButton, constraints);
        panel.add(playButton);

        constraints.gridx = 6;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(0, 18, 0, 0);
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        layout.setConstraints(cancelButton, constraints);
        panel.add(cancelButton);

        constraints.gridx = 9;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(0, supp, 0, margin);
        constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        layout.setConstraints(validButton, constraints);
        panel.add(validButton);

        constraints.gridx = 0;
        constraints.gridy += 1;
        constraints.gridwidth = 3;
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;
        constraints.insets = new Insets(0, margin+1, margin, 0);
        layout.setConstraints(previousButton, constraints);
        panel.add(previousButton);

        constraints.gridx = 9;
        constraints.gridwidth = 3;
        constraints.insets = new Insets(0, 0, margin, margin+1);
        constraints.anchor = GridBagConstraints.BASELINE_TRAILING;
        layout.setConstraints(nextButton, constraints);
        panel.add(nextButton);

        constraints.insets = new Insets(0, 0, 0, 0);
        constraints.anchor = GridBagConstraints.BASELINE;
        constraints.gridy = 3;
        constraints.gridwidth = 1;

        Component box = Box.createHorizontalStrut(cellWidth);
        constraints.gridx = 0;
        layout.setConstraints(box, constraints);
        panel.add(box);

        box = Box.createHorizontalStrut(cellWidth);
        constraints.gridx = 3;
        layout.setConstraints(box, constraints);
        panel.add(box);

        box = Box.createHorizontalStrut(cellWidth);
        constraints.gridx = 4;
        layout.setConstraints(box, constraints);
        panel.add(box);

        box = Box.createHorizontalStrut(cellWidth);
        constraints.gridx = 5;
        layout.setConstraints(box, constraints);
        panel.add(box);

        box = Box.createHorizontalStrut(cellWidth);
        constraints.gridx = 6;
        layout.setConstraints(box, constraints);
        panel.add(box);

        box = Box.createHorizontalStrut(cellWidth);
        constraints.gridx = 7;
        layout.setConstraints(box, constraints);
        panel.add(box);

        box = Box.createHorizontalStrut(cellWidth);
        constraints.gridx = 8;
        layout.setConstraints(box, constraints);
        panel.add(box);

        box = Box.createHorizontalStrut(cellWidth);
        constraints.gridx = 11;
        layout.setConstraints(box, constraints);
        panel.add(box);

        this.getContentPane().add(panel);
        this.pack();
    }//end initComponents()

    /**
     * Initialisation des listeners.
     * 
     * @since version 0.97 - version 1.01
     */
    private void initListener() {
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

        beginPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                begin += STEP;
                if(begin > end)
                    begin = end;
                updateDialog();
            }
        });//end beginPlus

        beginMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                begin -= STEP;
                if(begin < beginMin)
                    begin = beginMin;
                updateDialog();
            }
        });//end beginMinus

        endPlus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                end += STEP;
                if(end > endMax)
                    end = endMax;
                updateDialog();
            }
        });//end endPlus

        endMinus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                end -= STEP;
                if(end < begin)
                    end = begin;
                updateDialog();
            }
        });//end endMinus

        beginField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                beginField.setCaretPosition(defautCaretPosition);
            }

            @Override
            public void focusLost(FocusEvent e) {
                int caretPosition = beginField.getCaretPosition();
                fieldUpdateDialog();
                beginField.setCaretPosition(caretPosition);
            }
        });//end beginField

        endField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                endField.setCaretPosition(defautCaretPosition);
            }

            @Override
            public void focusLost(FocusEvent e) {
                int caretPosition = endField.getCaretPosition();
                fieldUpdateDialog();
                endField.setCaretPosition(caretPosition);
            }
        });//end endField

        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action(e.getSource());
            }
        };

        validButton.addActionListener(listener);
        cancelButton.addActionListener(listener);
        previousButton.addActionListener(listener);
        nextButton.addActionListener(listener);

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!playButton.isSelected()) {
                    if(saveIndex(begin, end)) {
                        long beginTime = core.getCurrentTime();
                        if(beginTime < begin || beginTime >= end)
                            beginTime = begin;

                        core.playOnRange(beginTime, end);
                    }
                }
                else {
                    core.audioPause();
                }
            }
        });//end playButton

        beginIndexButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                core.setProtectedTime(begin);
            }
        });//end beginIndexButton

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {        
                action(e.getSource());
            }
        });
    }//end initListener()

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
     * Récupère la vitesse affichée sur le slider.
     * 
     * @return la vitesse.
     * @since version 1.01
     */
    private float getSpeed() {
//        double position = speedSlider.getPosition();
//        float speed = (float) ((Index.RATE_MAX - Index.RATE_MIN) * position
//                + Index.RATE_MIN);
//        if(position == 0.5)
//            speed = Index.NORMAL_RATE;
//        return speed;
        return (float) speedSlider.getValue();
    }

    /**
     * Modifie la vitesse affichée sur le slider.
     * 
     * @param speed la vitesse.
     * @since version 1.01
     */
    private void setSpeed(float speed) {
//        double position = (speed - Index.RATE_MIN) / (Index.RATE_MAX - Index.RATE_MIN);
//        if(speed == Index.NORMAL_RATE)
//            position = 0.5;
//        speedSlider.setPosition(position);
        speedSlider.setValue(speed);
    }

    /**
     * Mise à jour des champs avec vérifications des valeurs.
     *
     * @since version 0.94 - version 0.99
     */
    private void fieldUpdateDialog() {
        begin = getValue(beginField);
        end = getValue(endField);

        if(begin < beginMin || begin > endMax) {
            begin = beginMin;
            updateDialog();
            GuiUtilities.showMessageDialog(this, resources.getString("beginError"));
        }//end if

        if(end > endMax) {
            end = endMax;
            updateDialog();
            GuiUtilities.showMessageDialog(this, resources.getString("endError"));
        }//end if

        if(end < begin) {
            end = begin;
            updateDialog();
            GuiUtilities.showMessageDialog(this, resources.getString("beginEndError"));
        }

        updateDialog();
    }

    /**
     * Mise à jour des champs.
     *
     * @since version 0.94 - version 0.99
     */
    private void updateDialog() {
        int beginCaretPosition = beginField.getCaretPosition();
        int endCaretPosition = endField.getCaretPosition();
        setValue(beginField, begin);
        setValue(endField, end);
        setValue(lengthField, end-begin);
        beginField.setCaretPosition(beginCaretPosition);
        endField.setCaretPosition(endCaretPosition);
    }

    /**
     * Affiche la boite de dialogue.
     *
     * @param index le numero de l'index initial.
     * @since version 0.94
     */
    public void showDialog(Index index) {
        initValues(index);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(
                (dim.width - getWidth()) / 2, (dim.height - getHeight()) / 4);
        this.setVisible(true);
    }

    /**
     * Modifie les textes suivant
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
        speedLabel.setText(resources.getString("speedLabel"));
        subtitleLabel.setText(resources.getString("subtitleLabel"));

//        decimalSeparator = resources.getString("decimalSeparator");
        decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();

        try {
            beginFormatter.setMask("##:##"+ decimalSeparator +"###");
            endFormatter.setMask("##:##"+ decimalSeparator +"###");
        } catch(ParseException e) {
            Edu4Logger.error(e);
        }

        String currentType = indexTypesRevert.get((String)typeList.getSelectedItem());
        indexTypesRevert.clear();
        for(String type : indexTypes.keySet()) {
            String typeInLanguage = resources.getString(indexTypes.get(type));
            indexTypesRevert.put(typeInLanguage, type);
        }
        if(currentType != null) {
            updateTypeList(currentType);
            typeList.setSelectedItem(resources.getString(indexTypes.get(currentType)));
        }

        playButton.setToolTipText(resources.getString("play"));
        beginIndexButton.setToolTipText(resources.getString("beginIndex"));
        validButton.setToolTipText(resources.getString("valid"));
        cancelButton.setToolTipText(resources.getString("cancel"));
        previousButton.setToolTipText(resources.getString("previous"));
        nextButton.setToolTipText(resources.getString("next"));

        beginPlus.setToolTipText(resources.getString("plus"));
        beginMinus.setToolTipText(resources.getString("minus"));
        endPlus.setToolTipText(resources.getString("plus"));
        endMinus.setToolTipText(resources.getString("minus"));

        this.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width-this.getWidth())/2,
                (dim.height-this.getHeight())/2);
    }

    /**
     * Initialise les valeurs initiales et courantes.
     *
     * @param index l'index courant.
     * @since version 0.94 - version 1.01
     */
    private void initValues(Index index) {
        this.currentIndex = index;
        initialBeginTime = index.getInitialTime();
        initialEndTime = index.getFinalTime();
        initialType = index.getType();
        initialSubtitle = index.getSubtitle();
        initialSpeed = index.getRate();

        begin = initialBeginTime;
        end = initialEndTime;

        beginMin = core.getMinimalTimeBefore(index);
        endMax = core.getMaximalTimeAfter(index);

        updateTypeList(initialType);

        subtitleField.setText(initialSubtitle);
        setSpeed(initialSpeed);

        previousButton.setEnabled(beginMin > 0);
        nextButton.setEnabled(endMax < core.getRecordTimeMax());

        updateDialog();

        core.setProtectedTime(begin);
    }

    /**
     * Mise à jour de l'états des boutons suivant qu'il est en lecture ou non.
     *
     * @param playing <code>true</code> si le poste est en lecture.
     * @since version 0.94 - version 0.97
     */
    public void updateButtons(boolean playing) {
        playButton.setSelected(playing);
        validButton.setEnabled(!playing);
        cancelButton.setEnabled(!playing);

        previousButton.setEnabled(!playing && beginMin > 0);
        nextButton.setEnabled(!playing && endMax < core.getRecordTimeMax());
    }

    /**
     * Mise à jour de la liste des types possibles avec un type initial.
     * 
     * @param type le type initial.
     * @since version 0.95.10 - version 1.01
     */
    private void updateTypeList(String type) {
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

        boolean modifTime = true;
        if(type.contentEquals(Index.FILE) || type.contentEquals(Index.REPEAT)) {
            //|| type.contentEquals(Index.SPEED)
            //pas de modification de la durée
            modifTime = false;
        }

        beginPlus.setEnabled(modifTime);
        beginMinus.setEnabled(modifTime);
        endPlus.setEnabled(modifTime);
        endMinus.setEnabled(modifTime);
        beginField.setEnabled(modifTime);
        endField.setEnabled(modifTime);
    }

    /**
     * Sauvegarde l'index.
     *
     * @param begin le temps de début.
     * @param end le temps de fin.
     * @param type le type.
     * @param subtitle le soustitre.
     * @param speed la vitesse.
     * @return si la sauvegarde a été effectuée.
     * @since version 0.94 - version 1.01
     */
    private boolean saveIndex(long begin, long end, String type,
            String subtitle, float speed) {
        if(end < begin) {
            GuiUtilities.showMessageDialog(this, resources.getString("beginEndError"));
            return false;
        }
        else if(initialEndTime-initialBeginTime - (end-begin) > core.getRemainingTime()) {
            GuiUtilities.showMessageDialog(
                    this, resources.getString("insertionDurationMessage"),
                    (end-begin)/1000+1,
                    core.getRemainingTime()/1000, core.getDurationMax()/1000);
            return false;
        }

        this.currentIndex = core.setMediaIndex(currentIndex.getId(), begin, end,
                type, subtitle, speed);

        this.begin = currentIndex.getInitialTime();
        this.end = currentIndex.getFinalTime();
        typeList.setSelectedItem(
                resources.getString(indexTypes.get(currentIndex.getType())));
        subtitleField.setText(currentIndex.getSubtitle());
        setSpeed(currentIndex.getRate());
        updateDialog();
        getOwner().repaint();
        return true;
    }

    /**
     * Sauvegarde l'index.
     *
     * @param begin le temps de début.
     * @param end le temps de fin.
     * @since version 0.94 - version 1.01
     */
    private boolean saveIndex(long begin, long end) {
        String type = indexTypesRevert.get((String)typeList.getSelectedItem());
        String subtitle = subtitleField.getText();
        if(subtitle != null && subtitle.isEmpty())
            subtitle = null;
        float speed = getSpeed();
        return saveIndex(begin, end, type, subtitle, speed);
    }

    /**
     * Traitement des actions longues dans un thread séparée.
     * 
     * @param source la source de l'action.
     * @since version 0.97 _ version 1.01
     */
    private void action(final Object source) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(source == validButton) {
                    saveIndex(begin, end);
                }
                else if(source == cancelButton) {
                    saveIndex(initialBeginTime, initialEndTime, initialType,
                            initialSubtitle, initialSpeed);
                }
                else if(source == previousButton) {
                    if(isChanged()) {
                        int option = showCancelableMessage(resources.getString("modifIndex"));
                        if(option == GuiUtilities.YES_OPTION) {
                            saveIndex(begin, end);
                        }
                        else if(option != GuiUtilities.NO_OPTION) {
                            return;
                        }
                    }
                    initValues(core.previousIndex(currentIndex));
                }
                else if(source == nextButton) {
                    if(isChanged()) {
                        int option = showCancelableMessage(resources.getString("modifIndex"));
                        if(option == GuiUtilities.YES_OPTION) {
                            saveIndex(begin, end);
                        }
                        else if(option != GuiUtilities.NO_OPTION) {
                            return;
                        }
                    }
                    initValues(core.nextIndex(currentIndex));
                }
                else if(source instanceof IndexDialog) {
                    fieldUpdateDialog();
                    if(isChanged()) {
                        int option = showCancelableMessage(resources.getString("modifIndex"));
                        if(option == GuiUtilities.YES_OPTION) {
                            saveIndex(begin, end);
                        }
                        else if(option != GuiUtilities.NO_OPTION) {
                            return;
                        }
                    }
                    setVisible(false);
                    dispose();
                }
            }
        });
        thread.start();
    }

    /**
     * Retourne si l'index a changé depuis la dernière validation.
     * 
     * @return si l'index a changé.
     * @since version 0.95.11 - version 1.01
     */
    private boolean isChanged() {
        if(begin != currentIndex.getInitialTime())
            return true;
        if(end != currentIndex.getFinalTime())
            return true;

        String type = indexTypesRevert.get((String)typeList.getSelectedItem());
        if(!type.contentEquals(currentIndex.getType()))
            return true;

        float speed = getSpeed();
        if(speed != currentIndex.getRate())
            return true;

        String subtitle = subtitleField.getText();
        if(subtitle != null && subtitle.isEmpty())
            subtitle = null;

        if(subtitle == null)
            return (currentIndex.getSubtitle() != null);
        else if(currentIndex.getSubtitle() == null)
            return true;
        else
            return !subtitle.contentEquals(currentIndex.getSubtitle());
    }

    /**
     * Affiche un message avec les options oui, non, cancel.
     * 
     * @param message le message.
     * @return l'option choisie.
     * @since version 0.95.11
     */
    private int showCancelableMessage(String message) {
        return GuiUtilities.showOptionDialogWithCancel(this, message, null, null);
    }

//    public static void main(String[] args) {
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
//            GuiUtilities.manageUI(true);
//            IndexDialog dialog = new IndexDialog(frame, core,
//                    new Resources(), indexTypesMap);
//
//            dialog.addWindowListener(new WindowAdapter() {
//                @Override
//                public void windowClosing(WindowEvent e) {
//                    System.exit(0);
//                }
//            });
//
//            dialog.showDialog(index);
//        } catch(Exception e) {
//            Edu4Logger.error(e);
//        }
//    }

}//end
