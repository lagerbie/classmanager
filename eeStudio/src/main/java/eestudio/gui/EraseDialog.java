package eestudio.gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

import eestudio.Core;

/**
 * Fenêtre de dialoque pour l'effacement de données.
 *
 * @author Fabrice Alleau
 * @version 1.02
 * @since version 0.95
 */
@Deprecated
public class EraseDialog extends JDialog {
    private static final long serialVersionUID = 9701L;

    /**
     * Resources textuelles
     */
    private Resources resources;
    /**
     * Noyau pour la création des index
     */
    private Core core;

    /**
     * Barre de progression
     */
    private ProcessingBar processingBar;

    /**
     * Message pour la sélection des éléments
     */
    private JLabel messageLabel;

    /**
     * Boutton pour valider
     */
    private JButton validButton;
    /**
     * Boutton pour annuler
     */
    private JButton cancelButton;

    /**
     * Boutton pour tout sélectionner
     */
    private JRadioButton allButton;
    /**
     * Boutton pour le choix des index
     */
    private JCheckBox indexesButton;
    /**
     * Boutton pour le choix de la piste vidéo
     */
    private JCheckBox videoButton;
    /**
     * Boutton pour le choix de la piste audio
     */
    private JCheckBox audioButton;
    /**
     * Boutton pour le choix du texte associé
     */
    private JCheckBox textButton;

    /**
     * Initialisation de la fenêtre.
     *
     * @param parent la fenêtre parente.
     * @param core le noyau de l'application.
     * @param resources les resources textuelles.
     * @param processingBar barre de progression pour les traitements.
     *
     * @since version 0.95 - version 0.99
     */
    public EraseDialog(Window parent, Core core, Resources resources, ProcessingBar processingBar) {
        super(parent, resources.getString("eraseTitle"), DEFAULT_MODALITY_TYPE);

        this.resources = resources;
        this.core = core;
        this.processingBar = processingBar;

        initComponents();
    }

    /**
     * Initialisation des composants graphiques.
     *
     * @since version 0.95 - version 1.01
     */
    private void initComponents() {
        int margin = 20;
        int panelWidth = 360;
        int width = panelWidth + 2 * margin;
        int height = 500;
        int offset = 40;

        messageLabel = new JLabel(resources.getString("eraseMessage"));

        validButton = new JButton(resources.getString("valid"));
        cancelButton = new JButton(resources.getString("cancel"));

        allButton = new JRadioButton(resources.getString("allChoice"));
        indexesButton = new JCheckBox(resources.getString("indexesChoice"));
        audioButton = new JCheckBox(resources.getString("audioChoice"));
        videoButton = new JCheckBox(resources.getString("videoChoice"));
        textButton = new JCheckBox(resources.getString("textChoice"));

        BackgroundPanel panel = new BackgroundPanel(width, height, 15);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setLayout(layout);

        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(2 * margin, margin, margin, margin);
        constraints.anchor = GridBagConstraints.BASELINE;
        layout.setConstraints(messageLabel, constraints);
        panel.add(messageLabel);

        constraints.insets = new Insets(0, margin + offset, 0, margin);
        constraints.anchor = GridBagConstraints.BASELINE_LEADING;

        layout.setConstraints(allButton, constraints);
        panel.add(allButton);
        layout.setConstraints(indexesButton, constraints);
        panel.add(indexesButton);
        layout.setConstraints(videoButton, constraints);
        panel.add(videoButton);
        layout.setConstraints(audioButton, constraints);
        panel.add(audioButton);
        layout.setConstraints(textButton, constraints);
        panel.add(textButton);

        constraints.gridwidth = 1;
        constraints.insets = new Insets(2 * margin, margin, 2 * margin, margin);
        constraints.anchor = GridBagConstraints.BASELINE;
        layout.setConstraints(validButton, constraints);
        panel.add(validButton);

        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(cancelButton, constraints);
        panel.add(cancelButton);

        this.getContentPane().add(panel);
        this.pack();

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

        allButton.addMouseListener(mouseListener);
        indexesButton.addMouseListener(mouseListener);
        videoButton.addMouseListener(mouseListener);
        audioButton.addMouseListener(mouseListener);
        textButton.addMouseListener(mouseListener);
        allButton.addFocusListener(focusListener);
        indexesButton.addFocusListener(focusListener);
        videoButton.addFocusListener(focusListener);
        audioButton.addFocusListener(focusListener);
        textButton.addFocusListener(focusListener);

        ActionListener selectionAction = e -> {
            getContentPane().repaint();
            AbstractButton source = (AbstractButton) e.getSource();
            if (source == allButton) {
                indexesButton.setSelected(source.isSelected());
                videoButton.setSelected(source.isSelected());
                audioButton.setSelected(source.isSelected());
                textButton.setSelected(source.isSelected());
            } else if (!source.isSelected()) {
                allButton.setSelected(false);
            }
        };

        allButton.addActionListener(selectionAction);
        indexesButton.addActionListener(selectionAction);
        videoButton.addActionListener(selectionAction);
        audioButton.addActionListener(selectionAction);
        textButton.addActionListener(selectionAction);

        cancelButton.addActionListener(event -> {
            getContentPane().repaint();
            close();
        });

        validButton.addActionListener(event -> {
            getContentPane().repaint();
            processingBar.processBegin(false,
                    resources.getString("processingTitle"),
                    resources.getString("processingMessage"));

            Thread thread = new Thread(() -> {
                if (textButton.isSelected()) {
                    core.eraseText();
                }
                if (videoButton.isSelected()) {
                    core.eraseVideo();
                }
                if (audioButton.isSelected()) {
                    core.eraseAudio();
                }
                if (indexesButton.isSelected()) {
                    core.eraseIndexes();
                }

                close();
            });
            thread.start();
        });
    }

    /**
     * Modification des textes pour un changement de langue.
     *
     * @since version 0.95 - version 1.02
     */
    public void updateLanguage() {
        this.setTitle(resources.getString("eraseTitle"));
        messageLabel.setText(resources.getString("eraseMessage"));

        validButton.setText(resources.getString("valid"));
        cancelButton.setText(resources.getString("cancel"));

        allButton.setText(resources.getString("allChoice"));
        indexesButton.setText(resources.getString("indexesChoice"));
        audioButton.setText(resources.getString("audioChoice"));
        videoButton.setText(resources.getString("videoChoice"));
        textButton.setText(resources.getString("textChoice"));
    }

    /**
     * Ferme la fenêtre et les resources associées.
     *
     * @since version 0.95.12
     */
    public void close() {
        processingBar.close();
        this.setVisible(false);
        this.dispose();
    }

    /**
     * Affiche la fenêtre et initialise les différents éléments de choix.
     *
     * @since version 0.95.12
     */
    public void showDialog() {
        boolean hasIndex = (core.getIndexesCount() > 0);
        indexesButton.setEnabled(hasIndex);
        indexesButton.setSelected(false);

        boolean hasData = (core.getRecordTimeMax() > 0);
        audioButton.setEnabled(hasData);
        audioButton.setSelected(false);

        boolean hasVideo = core.hasVideo();
        videoButton.setEnabled(hasVideo);
        videoButton.setSelected(false);

        boolean hasText = core.hasText();
        textButton.setEnabled(hasText);
        textButton.setSelected(false);

        this.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width - getWidth()) / 2, (dim.height - getHeight()) / 2);
        this.setVisible(true);
    }

//    public static void main(String[] args){
//        GuiUtilities.manageUI();
//        javax.swing.JFrame frame = new javax.swing.JFrame("test");
//        frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
//        ProcessingBar processingBar = new ProcessingBar();
//
//        try {
//            Core core = new Core(null);
//            EraseDialog dialog = new EraseDialog(frame, core,
//                    new Resources(), processingBar);
//
//            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                @Override
//                public void windowClosing(java.awt.event.WindowEvent e) {
//                    System.exit(0);
//                }
//            });
//
//            dialog.showDialog();
//        } catch(Exception e) {
//            eestudio.utils.Edu4Logger.error(e);
//        }
//    }

}
