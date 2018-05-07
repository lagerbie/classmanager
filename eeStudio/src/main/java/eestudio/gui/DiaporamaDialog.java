package eestudio.gui;

//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.GridBagConstraints;
//import java.awt.GridBagLayout;
//import java.awt.Insets;
//import java.awt.Toolkit;
//import java.awt.Window;
//
//import javax.swing.BoxLayout;
//import javax.swing.JButton;
//import javax.swing.JCheckBox;
//import javax.swing.JComboBox;

import javax.swing.*;
//import javax.swing.JFormattedTextField;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JTextField;
//
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.FocusAdapter;
//import java.awt.event.FocusEvent;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import javax.swing.event.PopupMenuEvent;
//import javax.swing.event.PopupMenuListener;
//
//import java.text.DecimalFormatSymbols;
//import java.text.ParseException;
//import javax.swing.text.MaskFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//import eestudio.Core;
//import eestudio.Index;
//import eestudio.IndexFile;
//import eestudio.utils.Edu4Logger;
//import eestudio.utils.Utilities;
//import java.io.File;


/**
 * Boite de dialogue pour l'édition de diaporama.
 *
 * @author Fabrice Alleau
 * @since version 1.02
 */
public class DiaporamaDialog extends JDialog {
    private static final long serialVersionUID = 10200L;
//
//    /** Couleur sans problème */
//    private final Color normalColor = Color.WHITE;
//    /** Couleur pour un problème */
//    private final Color errorColor = Color.RED;
//
//    /** Position par défaut dans les champs de temps */
//    private static final int defautCaretPosition = 3;
//
//    /** Resources pour les textes */
//    private Resources resources;
//    /** Noyau pour la création des index */
//    private Core core;
//
//    /** Séparateur pour les chiffres */
//    private char decimalSeparator;
//
//    /** Message explicatif */
//    private JLabel messageLabel;
//    /** Titre pour le numéro de passage */
//    private JLabel orderLabel;
//    /** Titre pour l'affichage de la mignature de l'image */
//    private JLabel imageLabel;
//    /** Titre pour la durée de l'index */
//    private JLabel lengthLabel;
//    /** Titre pour le temps de départ de l'index */
//    private JLabel beginLabel;
//    /** Titre pour le temps de fin de l'index */
//    private JLabel endLabel;
//    /** Titre pour le soustitre de l'index */
//    private JLabel subtitleLabel;
//    /** Titre pour le fichier source de l'image */
//    private JLabel fileLabel;
//    /** Titre pour le fichier sonore associé */
//    private JLabel audioLabel;
//    /** Titre pour le fichier sonore associé */
//    private JLabel audioBeginLabel;
//
//    /** Liste des composants graphique d'un index */
//    private List<IndexFields> indexesFields;
//
//    /** Bouton pour tout sélectionner */
//    private JButton selectButton;
//    /** Bouton pour supprimer les index de la sélection */
//    private JButton eraseSelection;
//
//    /** Bouton pour ajouter des index */
//    private JButton addButton;
//
//    /** Bouton pour valider les changements sur l'index */
//    private JButton validButton;
//
//    private int orderWidth = 40;
//    private int thumbWidth = 40;
//    /** Largeur des champs des temps */
//    private int timeWidth = 85;
//    /** Largeur des champs de sous-titre */
//    private int textWidth = 200;
//    /** Hauteur des champs */
//    private int textHeight = 30;
//
//    /** Panneau principal des index */
//    private JPanel mainPanel;
//
//    /**
//     * Initialisation de la boite de dialogue.
//     *
//     * @param core le coeur de l'application.
//     * @param resources les resources pour les textes.
//     * @param parent la fenêtre parente (peut être null).
//     * @since version 1.02
//     */
//    public DiaporamaDialog(Window parent, Core core, Resources resources) {
//        super(parent, resources.getString("indexesTitle"), DEFAULT_MODALITY_TYPE);
//        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//
//        this.core = core;
//        this.resources = resources;
////        decimalSeparator = resources.getString("decimalSeparator");
//        decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
//
//        initComponents();
//        initListeners();
//    }
//
//    /**
//     * Initialisation des composants.
//     * 
//     * @since version 1.02
//     */
//    private void initComponents() {
//        int width = 800;
//        int height = 500;
//        int margin = 20;
//        Dimension dim;
//
//        int capacity = 32;
//        indexesFields = new ArrayList<IndexFields>(capacity);
//
//        messageLabel = new JLabel(resources.getString("indexesMessage"));
//        orderLabel = new JLabel("ordre");
//        imageLabel = new JLabel("thumb");
//        lengthLabel = new JLabel(resources.getString("lengthLabel"));
//        beginLabel = new JLabel(resources.getString("beginLabel"));
//        endLabel = new JLabel(resources.getString("endLabel"));
//        subtitleLabel = new JLabel(resources.getString("subtitleLabel"));
//        fileLabel = new JLabel("fichier");
//        audioLabel = new JLabel("audio");
//        audioBeginLabel = new JLabel("debut");
//
//        messageLabel.setHorizontalAlignment(JLabel.CENTER);
//        orderLabel.setHorizontalAlignment(JLabel.CENTER);
//        imageLabel.setHorizontalAlignment(JLabel.CENTER);
//        lengthLabel.setHorizontalAlignment(JLabel.CENTER);
//        beginLabel.setHorizontalAlignment(JLabel.CENTER);
//        endLabel.setHorizontalAlignment(JLabel.CENTER);
//        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
//        fileLabel.setHorizontalAlignment(JLabel.CENTER);
//        audioLabel.setHorizontalAlignment(JLabel.CENTER);
//        audioBeginLabel.setHorizontalAlignment(JLabel.CENTER);
//
//        dim = new Dimension(orderWidth, textHeight);
//        orderLabel.setMinimumSize(dim);
//        orderLabel.setPreferredSize(dim);
//        dim = new Dimension(thumbWidth, textHeight);
//        imageLabel.setMinimumSize(dim);
//        imageLabel.setPreferredSize(dim);
//        dim = new Dimension(timeWidth, textHeight);
//        lengthLabel.setMinimumSize(dim);
//        lengthLabel.setPreferredSize(dim);
//        beginLabel.setMinimumSize(dim);
//        beginLabel.setPreferredSize(dim);
//        endLabel.setMinimumSize(dim);
//        endLabel.setPreferredSize(dim);
//        audioBeginLabel.setMinimumSize(dim);
//        audioBeginLabel.setPreferredSize(dim);
//        dim = new Dimension(textWidth, textHeight);
//        subtitleLabel.setMinimumSize(dim);
//        subtitleLabel.setPreferredSize(dim);
//        fileLabel.setMinimumSize(dim);
//        fileLabel.setPreferredSize(dim);
//        audioLabel.setMinimumSize(dim);
//        audioLabel.setPreferredSize(dim);
//
//        mainPanel = new JPanel();
//        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//        dim = new Dimension(width - 20, textHeight);
//        mainPanel.setPreferredSize(dim);
//
//        JScrollPane scrollPane = new JScrollPane(mainPanel,
//                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//        scrollPane.setWheelScrollingEnabled(true);
//        scrollPane.setBorder(null);
//        dim = new Dimension(width, height - 2*margin - 100);
//        scrollPane.setPreferredSize(dim);
//
//        selectButton = GuiUtilities.getSelectableButton(this,
//                GuiUtilities.getImageIcon("all"),
//                GuiUtilities.getImageIcon("none"),
//                resources.getString("selectAll"));
//        selectButton.setAlignmentX(CENTER_ALIGNMENT);
//        addButton = GuiUtilities.getActionButton(this,
//                GuiUtilities.getImageIcon("add"),
//                GuiUtilities.getImageIcon("addOff"),
//                resources.getString("addIndexes"));
//        eraseSelection = GuiUtilities.getActionButton(this,
//                GuiUtilities.getImageIcon("eraseSelection"),
//                GuiUtilities.getImageIcon("eraseSelectionOff"),
//                resources.getString("eraseSelection"));
//        validButton = GuiUtilities.getActionButton(this,
//                GuiUtilities.getImageIcon("valid"),
//                GuiUtilities.getImageIcon("validOff"),
//                resources.getString("valid"));
//
//        GridBagLayout labelLayout = new GridBagLayout();
//        GridBagConstraints labelConstraints = new GridBagConstraints();
//        JPanel labelPanel = new JPanel();
//        labelPanel.setLayout(labelLayout);
//        dim = new Dimension(width - 2*margin - 20, textHeight);
//        labelPanel.setPreferredSize(dim);
//        scrollPane.setColumnHeaderView(labelPanel);
//
//        labelConstraints.weightx = 1.0;
//        labelLayout.setConstraints(selectButton, labelConstraints);
//        labelPanel.add(selectButton);
//        labelLayout.setConstraints(orderLabel, labelConstraints);
//        labelPanel.add(orderLabel);
//        labelLayout.setConstraints(imageLabel, labelConstraints);
//        labelPanel.add(imageLabel);
//        labelLayout.setConstraints(lengthLabel, labelConstraints);
//        labelPanel.add(lengthLabel);
//        labelLayout.setConstraints(beginLabel, labelConstraints);
//        labelPanel.add(beginLabel);
//        labelLayout.setConstraints(endLabel, labelConstraints);
//        labelPanel.add(endLabel);
//        labelLayout.setConstraints(subtitleLabel, labelConstraints);
//        labelPanel.add(subtitleLabel);
//        labelLayout.setConstraints(fileLabel, labelConstraints);
//        labelPanel.add(fileLabel);
//        labelLayout.setConstraints(audioLabel, labelConstraints);
//        labelPanel.add(audioLabel);
//        labelConstraints.gridwidth = GridBagConstraints.REMAINDER;
//        labelLayout.setConstraints(audioBeginLabel, labelConstraints);
//        labelPanel.add(audioBeginLabel);
//
//        JPanel panel = new BackgroundPanel(width, height, 15);
//        GridBagLayout layout = new GridBagLayout();
//        GridBagConstraints constraints = new GridBagConstraints();
//        panel.setLayout(layout);
//
//        constraints.weightx = 1.0;
//        constraints.weighty = 0.0;
//
//        constraints.gridwidth = GridBagConstraints.REMAINDER;
//        constraints.anchor = GridBagConstraints.BASELINE;
//        constraints.insets = new Insets(margin, margin, margin, margin);
//        layout.setConstraints(messageLabel, constraints);
//        panel.add(messageLabel);
//
//        constraints.weighty = 1.0;
//        constraints.anchor = GridBagConstraints.ABOVE_BASELINE;
//        constraints.fill = GridBagConstraints.BOTH;
//        constraints.insets = new Insets(0, margin, 0, margin);
//        layout.setConstraints(scrollPane, constraints);
//        panel.add(scrollPane);
//
//        constraints.gridwidth = 1;
//        constraints.weighty = 0.0;
//        constraints.anchor = GridBagConstraints.BASELINE;
//        constraints.fill = GridBagConstraints.NONE;
//        constraints.insets = new Insets(margin, margin, margin, margin);
//        layout.setConstraints(addButton, constraints);
//        panel.add(addButton);
//        layout.setConstraints(eraseSelection, constraints);
//        panel.add(eraseSelection);
//        constraints.gridwidth = GridBagConstraints.REMAINDER;
//        layout.setConstraints(validButton, constraints);
//        panel.add(validButton);
//
//        this.getContentPane().add(panel);
//        this.pack();
//    }
//
//    /**
//     * Initialisation des actions.
//     * 
//     * @since version 1.02
//     */
//    private void initListeners() {
//        validButton.addActionListener(new ActionListener() {
//            /**
//             * Action "Valider".
//             * @since version 1.02
//             */
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                validButton.setSelected(true);
//                validAction();
//                validButton.setSelected(false);
//            }
//        });//end validButton
//
//        selectButton.addActionListener(new ActionListener() {
//            /**
//             * Action "Sélectionner".
//             * @since version 1.02
//             */
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                boolean select = !selectButton.isSelected();
//                for(IndexFields indexFields : indexesFields) {
//                    indexFields.checkBox.setSelected(select);
//                }
//
//                selectButton.setSelected(select);
//                selectButton.setToolTipText(resources.getString(
//                        select ? "selectNone" : "selectAll"));
//            }
//        });//end selectButton
//
//        eraseSelection.addActionListener(new ActionListener() {
//            /**
//             * Action "Supprimer".
//             * @since version 1.02
//             */
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                eraseSelection.setSelected(true);
//                int option = GuiUtilities.showOptionDialog(getOwner(),
//                        resources.getString("deleteIndexes"), null, null);
//                if(option == GuiUtilities.YES_OPTION)
//                    removeSelectedIndexes();
//                eraseSelection.setSelected(false);
//            }
//        });//end eraseSelection
//
//        addButton.addActionListener(new ActionListener() {
//            /**
//             * Action "Ajouter".
//             * @since version 1.02
//             */
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                addButton.setSelected(true);
//                Object input = GuiUtilities.showInputDialog(getOwner(),
//                        resources.getString("howIndexesInput"),
//                        null, null);
//
//                if(input != null) {
//                    int nb = Utilities.parseStringAsInt((String)input);
//
//                    if(nb > 0) {
//                        addIndexes(nb);
//                        validate();
//                        updateSizeAndLocation();
//                    }
//                }
//                addButton.setSelected(false);
//            }
//        });//end addButton
//
//        this.addWindowListener(new WindowAdapter() {
//            /**
//             * Action "Fermer".
//             * @since version 1.02
//             */
//            @Override
//            public void windowClosing(WindowEvent e) {
//                validButton.requestFocus();
//                if(hasIndex()) {
//                    int option = GuiUtilities.showOptionDialogWithCancel(getOwner(),
//                            resources.getString("modifIndexes"), null, null);
//                    if(option == GuiUtilities.YES_OPTION) {
//                        validButton.setSelected(true);
//                        validAction();
//                        validButton.setSelected(false);
//                    }
//                    else if(option == GuiUtilities.NO_OPTION) {
//                        core.removeNullIndex();
//                        setVisible(false);
//                    }
//                }
//                else {
//                    setVisible(false);
//                }
//            }
//        });
//    }//end IndexesDialog(..)
//
//    /**
//     * Affiche la boite de dialogue.
//     *
//     * @param nbCreate le nombre d'index à créer.
//     * @since version 1.02
//     */
//    public void showDialog(int nbCreate) {
//        removeIndexes();
//        addIndexes(nbCreate);
//        updateSizeAndLocation();
//        this.setVisible(true);
//    }
//
//    /**
//     * Actualiser la taille et la position de la fenêtre.
//     * 
//     * @since version 1.02
//     */
//    private void updateSizeAndLocation() {
//        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
//
//        this.pack();
//        int width = getWidth();
//        int height = getHeight();
//
//        if(width < 600)
//            width = 740;
//        if(height > screenDim.height-50)
//            height = screenDim.height-50;
//
//        this.setSize(new Dimension(width, height));
//        this.setLocation((screenDim.width - getWidth()) / 4,
//                (screenDim.height - getHeight()) / 4);
//    }
//
//    /**
//     * Modifie les textes
//     * 
//     * @since version 1.02
//     */
//    public void updateLanguage() {
//        setTitle(resources.getString("indexTitle"));
//
//        messageLabel.setText(resources.getString("indexMessage"));
////        orderLabel.setText(resources.getString("indexMessage"));
////        imageLabel.setText(resources.getString("indexMessage"));
//        lengthLabel.setText(resources.getString("lengthLabel"));
//        beginLabel.setText(resources.getString("beginLabel"));
//        endLabel.setText(resources.getString("endLabel"));
//        subtitleLabel.setText(resources.getString("subtitleLabel"));
////        fileLabel.setText(resources.getString("indexMessage"));
////        audioLabel.setText(resources.getString("indexMessage"));
////        audioBeginLabel.setText(resources.getString("indexMessage"));
//
//        selectButton.setToolTipText(resources.getString("selectAll"));
//        eraseSelection.setToolTipText(resources.getString("eraseSelection"));
//        addButton.setToolTipText(resources.getString("addIndexes"));
//        validButton.setToolTipText(resources.getString("valid"));
//
////        decimalSeparator = resources.getString("decimalSeparator");
//        decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
//    }
//
//    /**
//     * Convertie la chaîne de caractères représentant le temps en millisecondes.
//     *
//     * @param field le champ texte contenat le temps formaté.
//     * @return le temps en millisecondes.
//     * @since version 1.02
//     */
//    private long getValue(JTextField field) {
//        String value = field.getText();
//        String[] split = value.split("[:,.]");
//        long minute = Utilities.parseStringAsLong(split[0].trim());
//        long seconde = Utilities.parseStringAsLong(split[1].trim());
//        long milliseconde = Utilities.parseStringAsLong(split[2].trim());
//        return (minute * 60 + seconde) * 1000 + milliseconde;
//    }
//
//    /**
//     * Convertie la chaîne de caractères représentant le temps en millisecondes.
//     *
//     * @param textField le champ texte contenat le temps formaté.
//     * @param value le temps en millisecondes.
//     * @since version 1.02
//     */
//    private void setValue(JTextField textField, long value) {
//        textField.setText(
//                String.format("%1$tM:%1$tS" + decimalSeparator + "%1$tL", value));
//    }
//
//    private int getOrder(JComboBox orderList) {
//        Object item = orderList.getSelectedItem();
//        if(item == null)
//            return -1;
//        else
//            return (Integer) item;
//    }
//
//    /**
//     * Mise à jour de la liste des types possibles avec un type initial.
//     * 
//     * @param type le type initial.
//     * @param typeList le composant graphique de la liste.
//     * @return si il faut autoriser la modification des champs de temps.
//     * @since version 1.02
//     */
//    private void updateOrderList(JComboBox orderList, int order, int cnt) {
//        int oldOrder = getOrder(orderList);
//        int oldNb = indexesFields.size();
//        if(oldOrder == order && oldNb == cnt)
//            return;
//
//        orderList.removeAllItems();
//        for(int i = 1; i <= cnt; i++) {
//            orderList.addItem(i);
//        }
//        orderList.setSelectedItem(order);
//    }
//
//    /**
//     * Mise à jour des champs avec vérifications des valeurs.
//     *
//     * @since version 1.02
//     */
//    private void updateFields() {
//        sortIndexFieldsList();
//        long begin = 0;
//        for(IndexFields indexFields : indexesFields) {
//            setValue(indexFields.beginField, begin);
//            begin += getValue(indexFields.lengthField);
//            setValue(indexFields.endField, begin);
//        }
//
//        if(begin > Core.TIME_MAX) {
//            GuiUtilities.showMessageDialog(
//                    this, resources.getString("beginError"));
//        }//end if
//    }
//
//    /**
//     * Trie par ordre croissant du temps de départ des index de la table.
//     *
//     * @since version 1.02
//     */
//    private void sortIndexFieldsList() {
//        int capacity = indexesFields.size();
//        List<IndexFields> sortList = new ArrayList<IndexFields>(capacity);
//
//        for(int i=1; i<=capacity; i++) {
//            IndexFields indexFields = getIndexFields(i);
//            sortList.add(indexFields);
//        }//end for
//
//        indexesFields.clear();
//        indexesFields = sortList;
//    }
//    
//    /**
//     * Retourne l'indice de l'index de la table qui a le temps de début le plus
//     * petit.
//     *
//     * @return l'indice.
//     * @since version 1.02
//     */
//    private IndexFields getIndexFields(int order) {
//        for(IndexFields indexFields : indexesFields) {
//            if(order == getOrder(indexFields.orderBox)) {
//                return indexFields;
//            }//end if
//        }//end for
//
//        return null;
//    }
//
//    /**
//     * Test la validité des changements et modifie les index en conséquence.
//     * 
//     * @since version 1.02
//     */
//    private void validAction() {
//        long duration = 0;
//        int size = indexesFields.size();
//        for(IndexFields indexFields : indexesFields) {
//            duration += getValue(indexFields.lengthField);
//        }//end for
//
//        if(duration > Core.TIME_MAX) {
//            GuiUtilities.showMessageDialog(this,
//                    resources.getString("fileDurationMessage"),
//                    duration/1000+1,
//                    Core.TIME_MAX/1000);
//            return;
//        }
//
//        List<Index> indexList = new ArrayList<Index>(size);
//        for(int i=0; i<size; i++) {
//            IndexFields indexFields = indexesFields.get(i);
//            long begin = getValue(indexFields.beginField);
//            long end = getValue(indexFields.endField);
//            String subtitle = indexFields.subtitleField.getText();
//            if(subtitle != null && subtitle.isEmpty())
//                subtitle = null;
//            String image = indexFields.fileField.getText();
//            if(image != null && image.isEmpty())
//                image = null;
//            if(image != null && !new File(image).exists())
//                image = null;
//            String audio = indexFields.audioField.getText();
//            if(audio != null && audio.isEmpty())
//                audio = null;
//            if(audio != null && !new File(audio).exists())
//                audio = null;
//
//            Index index = new IndexFile(subtitle, duration);
//
////            if(core.getIndexWithId(indexFields.id).isTimeLineModifier()) {
////                long initLength = core.getIndexWithId(indexFields.id).getLength();
////                long length = end - begin;
////                if(initLength - length > core.getRemainingTime()) {
////                    GuiUtilities.showMessageDialog(this,
////                            resources.getString("insertionDurationMessage"),
////                            (initLength-length)/1000+1,
////                            core.getRemainingTime()/1000,
////                            core.getDurationMax()/1000);
////                    return;
////                }
////            }
//
////            if(isChanged(indexFields))
////                core.setMediaIndex(indexFields.id, begin, end, Index.IMAGE, subtitle, -1);
//        }//end for
//
//        core.removeNullIndex();
//        core.sortIndexes();
//
//        setVisible(false);
//    }//end validAction()
//
//    /**
//     * Indique si il y a des index non null.
//     * 
//     * @return <code>true</code> si il y a des index non null.
//     * @since version 1.02
//     */
//    private boolean hasIndex() {
//        for(IndexFields indexFields : indexesFields) {
//            if(getValue(indexFields.lengthField) > 0)
//                return true;
//        }
//        return false;
//    }
//
//    /**
//     * Ajoute un index dans la table d'affichage.
//     *
//     * @since version 1.02
//     */
//    private void addIndex(int cnt) {
//        IndexFields fields = new IndexFields();
//        Dimension dim = new Dimension(
//                2*mainPanel.getPreferredSize().width, textHeight);
//        fields.setMaximumSize(dim);
//
//        setValue(fields.beginField, 0);
//        setValue(fields.endField, 0);
//        setValue(fields.lengthField, 0);
//        setValue(fields.audioBeginField, 0);
//
//        indexesFields.add(fields);
//        mainPanel.add(fields);
//        updateOrderList(fields.orderBox, indexesFields.size(), cnt);
//    }
//
//    /**
//     * Ajoute des indes dans la table.
//     *
//     * @param cnt le nombre d'index à ajouter.
//     * @since version 1.02
//     */
//    private void addIndexes(int cnt) {
//        int nb = indexesFields.size() + cnt;
//        for(int i=0; i<cnt; i++) {
//            addIndex(nb);
//        }
//
//        nb = mainPanel.getComponentCount();
//        Dimension dim = new Dimension(
//                mainPanel.getPreferredSize().width, nb*textHeight);
//        mainPanel.setPreferredSize(dim);
//    }
//
//    /**
//     * Supprime tous les index de la fenêtre.
//     *
//     * @since version 1.02
//     */
//    private void removeIndexes() {
//        indexesFields.clear();
//        mainPanel.removeAll();
//    }
//
//    /**
//     * Supprime les index sélectionné.
//     *
//     * @since version 1.02
//     */
//    private void removeSelectedIndexes() {
//        int capacity = indexesFields.size();
//        List<IndexFields> newIndexesFields = new ArrayList<IndexFields>(capacity);
//        List<Long> removeIndexIds = new ArrayList<Long>(capacity);
//
//        mainPanel.removeAll();
//
//        for(IndexFields indexFields : indexesFields) {
//            if(!indexFields.checkBox.isSelected()) {
//                newIndexesFields.add(indexFields);
//
//                mainPanel.add(indexFields);
//            }
//        }
//
//        removeIndexIds.clear();
//
//        indexesFields.clear();
//        indexesFields = newIndexesFields;
//        updateSizeAndLocation();
//    }
//
//    /**
//     * Classe recensant tous les composants graphiques nécessaires pour un index.
//     * 
//     * @since version 1.02
//     */
//    private class IndexFields extends JPanel {
//        private static final long serialVersionUID = 10200L;
//
//        /** Champs de sélection des index */
//        protected JCheckBox checkBox;
//        protected JComboBox orderBox;
//        protected JPanel imagePane;
//        /** Champ pour la durée de l'index */
//        protected JFormattedTextField lengthField;
//        /** Champs formatés pour les temps de départ des index */
//        protected JTextField beginField;
//        /** Champ formaté pour le temps de fin de l'index */
//        protected JTextField endField;
//        /** Zone de texte pour le soustitre de l'index */
//        protected JTextField subtitleField;
//        /** Zone de texte pour le soustitre de l'index */
//        protected JTextField fileField;
//        /** Zone de texte pour le soustitre de l'index */
//        protected JTextField audioField;
//        /** Champs formatés pour les temps de départ des index */
//        protected JFormattedTextField audioBeginField;
//
//        private IndexFields() {
//            MaskFormatter lengthFormatter = null;
//            MaskFormatter audioBeginFormatter = null;
//            try {
//                lengthFormatter = new MaskFormatter("##:##"+ decimalSeparator +"###");
//                lengthFormatter.setPlaceholderCharacter('0');
//                audioBeginFormatter = new MaskFormatter("##:##"+ decimalSeparator +"###");
//                audioBeginFormatter.setPlaceholderCharacter('0');
//            } catch(ParseException e) {
//                Edu4Logger.error(e);
//            }//end try
//
//            checkBox = new JCheckBox();
//            orderBox = new JComboBox();
//            imagePane = new JPanel();
//            lengthField = new JFormattedTextField(lengthFormatter);
//            beginField = new JTextField();
//            endField = new JTextField();
//            subtitleField = new JTextField();
//            fileField = new JTextField();
//            audioField = new JTextField();
//            audioBeginField = new JFormattedTextField(audioBeginFormatter);
//
//            Dimension dim = new Dimension(orderWidth, textHeight);
//            checkBox.setMinimumSize(dim);
//            checkBox.setMaximumSize(dim);
//            checkBox.setPreferredSize(dim);
//            orderBox.setMinimumSize(dim);
//            orderBox.setMaximumSize(dim);
//            orderBox.setPreferredSize(dim);
//            dim = new Dimension(thumbWidth, textHeight);
//            imagePane.setMinimumSize(dim);
//            imagePane.setMaximumSize(dim);
//            imagePane.setPreferredSize(dim);
//            dim = new Dimension(timeWidth, textHeight);
//            lengthField.setMinimumSize(dim);
//            lengthField.setMaximumSize(dim);
//            lengthField.setPreferredSize(dim);
//            beginField.setMinimumSize(dim);
//            beginField.setMaximumSize(dim);
//            beginField.setPreferredSize(dim);
//            endField.setMinimumSize(dim);
//            endField.setMaximumSize(dim);
//            endField.setPreferredSize(dim);
//            audioBeginField.setMinimumSize(dim);
//            audioBeginField.setMaximumSize(dim);
//            audioBeginField.setPreferredSize(dim);
//            dim = new Dimension(textWidth, textHeight);
//            subtitleField.setMinimumSize(dim);
//            subtitleField.setMaximumSize(dim);
//            subtitleField.setPreferredSize(dim);
//            fileField.setMinimumSize(dim);
//            fileField.setMaximumSize(dim);
//            fileField.setPreferredSize(dim);
//            audioField.setMinimumSize(dim);
//            audioField.setMaximumSize(dim);
//            audioField.setPreferredSize(dim);
//
//            checkBox.setHorizontalAlignment(JCheckBox.CENTER);
//            lengthField.setHorizontalAlignment(JTextField.CENTER);
//            beginField.setHorizontalAlignment(JTextField.CENTER);
//            endField.setHorizontalAlignment(JTextField.CENTER);
//            subtitleField.setHorizontalAlignment(JTextField.LEADING);
//
//            beginField.setEditable(false);
//            endField.setEditable(false);
//
//            GridBagLayout layout = new GridBagLayout();
//            GridBagConstraints constraints = new GridBagConstraints();
//            this.setLayout(layout);
//
//            constraints.weightx = 1.0;
//
//            layout.setConstraints(checkBox, constraints);
//            this.add(checkBox);
//            layout.setConstraints(orderBox, constraints);
//            this.add(orderBox);
//            layout.setConstraints(imagePane, constraints);
//            this.add(imagePane);
//            layout.setConstraints(lengthField, constraints);
//            this.add(lengthField);
//            layout.setConstraints(beginField, constraints);
//            this.add(beginField);
//            layout.setConstraints(endField, constraints);
//            this.add(endField);
//            layout.setConstraints(subtitleField, constraints);
//            this.add(subtitleField);
//            layout.setConstraints(fileField, constraints);
//            this.add(fileField);
//            layout.setConstraints(audioField, constraints);
//            this.add(audioField);
//            constraints.gridwidth = GridBagConstraints.REMAINDER;
//            layout.setConstraints(audioBeginField, constraints);
//            this.add(audioBeginField);
//            
//            lengthField.addFocusListener(new FocusAdapter() {
//                @Override
//                public void focusGained(FocusEvent e) {
//                    lengthField.setCaretPosition(defautCaretPosition);
//                }
//
//                @Override
//                public void focusLost(FocusEvent e) {
////                    updateFields(beginField, lengthField);
//                }
//            });
//            
//            //pour éviter d'avoir un reste d'image quand le programme affiche ou
//            //masque la liste
//            orderBox.addPopupMenuListener(new PopupMenuListener() {
//                @Override
//                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
//                    getContentPane().repaint();
//                }
//                @Override
//                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
//                    getContentPane().repaint();
//                }
//                @Override
//                public void popupMenuCanceled(PopupMenuEvent e) {
//                }
//            });
//        }
//    }//end class IndexFields
//
//    public static void main(String[] args) {
//        GuiUtilities.manageUI(true);
//        javax.swing.JFrame frame = new javax.swing.JFrame("test");
//        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//
//        try {
//            Core core = new Core(null);
//            DiaporamaDialog dialog = new DiaporamaDialog(frame, core, new Resources());
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