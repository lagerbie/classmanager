package thot.supervision.gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import thot.gui.FileChooser;
import thot.gui.FilterPanel;
import thot.gui.GuiUtilities;
import thot.gui.ImagePanel;
import thot.gui.ProcessingBar;
import thot.gui.Resources;
import thot.supervision.CommandXMLUtilities;
import thot.supervision.MasterCore;
import thot.supervision.MasterCoreListener;
import thot.supervision.Student;
import thot.utils.Constants;
import thot.utils.Utilities;

/**
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class MainFrame extends JFrame implements MasterCoreListener {
    private static final long serialVersionUID = 19000L;

    /**
     * Label pour l'affichage de la supervision.
     */
    private static final String SUPERVISION = "supervision";
    /**
     * Label pour l'affichage du laboratoire.
     */
    private static final String LABORATORY = "laboratory";

    /**
     * Référence du noyau.
     */
    private MasterCore core;
    /**
     * Liste des vignettes eleve addressées par l'adresse ip des élèves.
     */
    private Map<String, Thumbnail> thumbnails;
    /**
     * Resources pour les textes.
     */
    private Resources resources;
    /**
     * Répertoire de l'utilisateur.
     */
    private final File userHome;

    /**
     * Largeur de la fenêtre principale.
     */
    private int width = 1205;
    /**
     * Hauteur de la fenêtre principale.
     */
    private int height = 766;
    /**
     * Hauteur du logo.
     */
    private int logoHeight = 40;
    /**
     * Largeur du menu principal.
     */
    private int menuWidth = 140;
    /**
     * Hauteur des vignettes élève.
     */
    private int thumbHeight = 170;
    /**
     * Dimension des boutons.
     */
    private int buttonSize = 64;
    /**
     * Marge courante.
     */
    private int margin = 5;

    /**
     * Bouton de changement de langue.
     */
    private JButton helpButton;
    /**
     * Bouton de minimisation.
     */
    private JButton miniButton;
    /**
     * Bouton de fermeture.
     */
    private JButton closeButton;
    /**
     * Boite de séléction de la langue.
     */
    private JComboBox<String> languageButton;

    /**
     * Bouton pour le groupe A.
     */
    private StateButton groupAbutton;
    /**
     * Bouton pour le groupe B.
     */
    private StateButton groupBbutton;
    /**
     * Bouton pour le groupe C.
     */
    private StateButton groupCbutton;
    /**
     * Bouton pour le groupe D.
     */
    private StateButton groupDbutton;
    /**
     * Bouton pour le groupe E.
     */
    private StateButton groupEbutton;
    /**
     * Bouton pour le groupe F.
     */
    private StateButton groupFbutton;
    /**
     * Bouton pour le groupe G.
     */
    private StateButton groupGbutton;
    /**
     * Bouton pour le groupe H.
     */
    private StateButton groupHbutton;
    /**
     * Bouton pour la flèche des groupes supplémentaires.
     */
    private StateButton arrowButton;
    /**
     * Panneau pour les groupes supplémentaires.
     */
    private JPanel groupSuppPanel;
    /**
     * Panneau pour la sélection de groupes.
     */
    private FilterPanel groupMenu;

    /**
     * Panneau pour afficher les différents menus.
     */
    private JPanel menusPanel;
    /**
     * Layout pour le panneau des menus. Pour intervetir les différents menus.
     */
    private CardLayout menusLayout;
    /**
     * Panneau pour les différentes vues.
     */
    private JPanel viewsPanel;
    /**
     * Layout pour le panneau des vues. Pour intervetir les différentes vues.
     */
    private CardLayout viewsLayout;
    /**
     * Panneau pour le menu de supervision.
     */
    private SupervisionPanel supervisionPanel;
    /**
     * Panneau pour le laboratoire.
     */
    private JPanel laboratoryPanel;
    /**
     * Panneau pour les vignettes élèves.
     */
    private ThumbsPanel thumbPanel;

    /**
     * Explorateur de fichiers.
     */
    protected final FileChooser chooser;
    /**
     * Fenêtre affichant une barre de progression.
     */
    private ProcessingBar processingBar;
    /**
     * Fenêtre pour l'affichage de messages.
     */
    private JDialog messageDialog;

    /**
     * Initialisation.
     *
     * @param core le noyau professeur.
     * @param resources les resources textuelle.
     * @param userHome le répertoire utilisateur.
     */
    public MainFrame(MasterCore core, Resources resources, File userHome) {
        super(Constants.softName);
        this.core = core;
        this.resources = resources;
        this.userHome = userHome;

        thumbnails = new HashMap<>(32);

        initFrame();
        initButtonActions();

        processingBar = new ProcessingBar(this, null);
        chooser = new FileChooser(this, userHome);

        setGroupFunctionsEnabled(false);
    }

    /**
     * Initialise les composants graphiques.
     */
    private void initFrame() {
        Dimension dim;
        //Boutons généraux
        helpButton = getButton(GuiConstants.help);
        miniButton = getButton(GuiConstants.mini);
        closeButton = getButton(GuiConstants.close);

        languageButton = new JComboBox<>();
//        languageButton.addItem("Català");
        languageButton.addItem("Deutsch");
        languageButton.addItem("English");
//        languageButton.addItem("Euskara");
        languageButton.addItem("Español");
        languageButton.addItem("Français");
        languageButton.addItem("Italiano");
        dim = new Dimension(100, logoHeight - 2 * margin);
        languageButton.setPreferredSize(dim);
        languageButton.setMaximumSize(dim);
        languageButton.setFocusable(false);
        languageButton.setToolTipText(getToolTipText(GuiConstants.language));
        Locale locale = Locale.getDefault();
        String language = locale.getDisplayLanguage(locale);
        language = language.substring(0, 1).toUpperCase()
                + language.substring(1, language.length()).toLowerCase();
        languageButton.setSelectedItem(language);

        //Boutons pour les groupes
        groupAbutton = getButton(GuiConstants.groupA);
        groupBbutton = getButton(GuiConstants.groupB);
        groupCbutton = getButton(GuiConstants.groupC);
        groupDbutton = getButton(GuiConstants.groupD);
        groupEbutton = getButton(GuiConstants.groupE);
        groupFbutton = getButton(GuiConstants.groupF);
        groupGbutton = getButton(GuiConstants.groupG);
        groupHbutton = getButton(GuiConstants.groupH);
        arrowButton = getButton(GuiConstants.arrow);

        Insets insets;
        GridBagLayout gridbag;
        GridBagConstraints c;

        //constitution du menu principal
        JPanel logo = new ImagePanel(GuiUtilities.getImage("logoImage", GuiConstants.imagesPath, GuiConstants.images),
                -1, logoHeight);

        JPanel tabPanel = new JPanel();
        tabPanel.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        dim = new Dimension(width - 2 * menuWidth, logoHeight);
        tabPanel.setMaximumSize(dim);
        tabPanel.setPreferredSize(dim);

        JLabel softLabel = new JLabel(Constants.softName, JLabel.LEFT);
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
        softLabel.setFont(font);

        JPanel standardMenu = new JPanel();
        standardMenu.setLayout(new BoxLayout(standardMenu, BoxLayout.X_AXIS));
        standardMenu.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        insets = new Insets(0, -3, 0, -3);
        helpButton.setMargin(insets);
        miniButton.setMargin(insets);
        closeButton.setMargin(insets);
        standardMenu.add(helpButton);
        standardMenu.add(miniButton);
        standardMenu.add(closeButton);

        JPanel mainMenu = new JPanel();
        mainMenu.setLayout(new BoxLayout(mainMenu, BoxLayout.X_AXIS));
        mainMenu.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        mainMenu.add(Box.createHorizontalStrut(margin));
        mainMenu.add(logo);
        mainMenu.add(Box.createHorizontalStrut(menuWidth - logo.getMaximumSize().width + margin));
        mainMenu.add(tabPanel);
        mainMenu.add(Box.createHorizontalStrut(2 * margin));
        mainMenu.add(softLabel);
        mainMenu.add(Box.createHorizontalStrut(10 * margin));
        mainMenu.add(languageButton);
        mainMenu.add(Box.createHorizontalStrut(5 * margin));
        mainMenu.add(standardMenu);
        mainMenu.add(Box.createHorizontalStrut(2 * margin));
        dim = new Dimension(width, logoHeight);
        mainMenu.setMaximumSize(dim);
        mainMenu.setPreferredSize(dim);


        dim = new Dimension(menuWidth, 2 * (buttonSize + margin) + 2 * margin);
        groupMenu = new FilterPanel(resources.getString("groupLabel"), dim);
        gridbag = new GridBagLayout();
        c = new GridBagConstraints();
        groupMenu.setLayout(gridbag);

        c.gridwidth = 1;
        gridbag.setConstraints(groupAbutton, c);
        groupMenu.add(groupAbutton);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(groupBbutton, c);
        groupMenu.add(groupBbutton);
        c.gridwidth = 1;
        gridbag.setConstraints(groupCbutton, c);
        groupMenu.add(groupCbutton);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(groupDbutton, c);
        groupMenu.add(groupDbutton);

        dim = new Dimension(310, buttonSize);
        groupSuppPanel = new FilterPanel("", dim);
        groupSuppPanel.setMaximumSize(dim);
        groupSuppPanel.setPreferredSize(dim);
        groupSuppPanel.add(groupEbutton);
        groupSuppPanel.add(groupFbutton);
        groupSuppPanel.add(groupGbutton);
        groupSuppPanel.add(groupHbutton);

        //menuHeight = height-logoHeight-margin
        int menuHeight = height - logoHeight - 2 * (buttonSize + margin) - 5 * margin;//-2*margin-groupMenu.height
        int panelWidth = width - menuWidth - 2 * margin;
        int panelHeight = height - logoHeight - margin;
        thumbPanel = new ThumbsPanel(panelWidth, panelHeight, thumbHeight);
        AdjustmentListener adjustmentListener = e -> getMainFrame().repaint();
        thumbPanel.getVerticalScrollBar().addAdjustmentListener(adjustmentListener);

        supervisionPanel = new SupervisionPanel(this, panelWidth, panelHeight, menuWidth, menuHeight, buttonSize,
                margin, resources);
        supervisionPanel.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        supervisionPanel.add(thumbPanel);

        menusPanel = new JPanel();
        menusLayout = new CardLayout();
        menusPanel.setLayout(menusLayout);
        menusPanel.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        menusPanel.add(supervisionPanel.getTabMenu(), SUPERVISION);
        menusLayout.show(menusPanel, SUPERVISION);

        JPanel menus = new JPanel();
        menus.setLayout(new BoxLayout(menus, BoxLayout.Y_AXIS));
        menus.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        menus.add(menusPanel);
        menus.add(groupMenu);
        menus.add(Box.createVerticalStrut(2 * margin));

        viewsPanel = new JPanel();
        viewsLayout = new CardLayout();
        viewsPanel.setLayout(viewsLayout);
        viewsPanel.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        viewsPanel.add(supervisionPanel, SUPERVISION);
        viewsLayout.show(viewsPanel, SUPERVISION);

        //Constitution du panel principal
        JPanel panel = new ImagePanel(
                GuiUtilities.getImage("backgroundImage", GuiConstants.imagesPath, GuiConstants.images));
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        layout.putConstraint(SpringLayout.SOUTH, arrowButton, -70, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.WEST, arrowButton, menuWidth - 20, SpringLayout.WEST, panel);
        panel.add(arrowButton);

        layout.putConstraint(SpringLayout.SOUTH, groupSuppPanel, -11, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.WEST, groupSuppPanel, menuWidth + 10, SpringLayout.WEST, panel);
        panel.add(groupSuppPanel);

        layout.putConstraint(SpringLayout.NORTH, mainMenu, margin, SpringLayout.NORTH, panel);
        panel.add(mainMenu);

        layout.putConstraint(SpringLayout.NORTH, menus, logoHeight + margin, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, menus, margin, SpringLayout.WEST, panel);
        panel.add(menus);

        layout.putConstraint(SpringLayout.NORTH, viewsPanel, logoHeight + margin, SpringLayout.NORTH, panel);
        layout.putConstraint(SpringLayout.WEST, viewsPanel, menuWidth + 2 * margin, SpringLayout.WEST, panel);
        layout.putConstraint(SpringLayout.SOUTH, viewsPanel, -2 * margin, SpringLayout.SOUTH, panel);
        layout.putConstraint(SpringLayout.EAST, viewsPanel, -margin, SpringLayout.EAST, panel);
        panel.add(viewsPanel);

        //Initialisation de l'état des boutons
        arrowButton.setVisible(false);
        groupSuppPanel.setVisible(false);

        this.setSize(panel.getPreferredSize());
        this.setUndecorated(true);
        this.getContentPane().add(panel);
        this.setResizable(false);
        this.setIconImages(GuiUtilities.getIcons());
    }

    /**
     * Ajoute les actions sur les différents éléments.
     */
    private void initButtonActions() {
        ActionListener buttonListener = event -> {
            StateButton button = (StateButton) event.getSource();
            if (button.getType().contentEquals(GuiConstants.sendMessage)) {
                String message = (String) GuiUtilities
                        .showInputDialog(button, null, resources.getString("sendText"), null, null);
                if (message != null) {
                    core.executeCommand(button.getType(), message);
                }
            } else {
                fireButtonClicked(button);
            }
        };

        languageButton.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                String language = (String) languageButton.getSelectedItem();

                if (language != null) {
                    GuiUtilities.setDefaultLocale(Locale.getDefault());
                    changeLanguage(Locale.getDefault());
                    core.executeCommand(GuiConstants.language, language);
                }
            }
        });

        groupAbutton.addActionListener(buttonListener);
        groupBbutton.addActionListener(buttonListener);
        groupCbutton.addActionListener(buttonListener);
        groupDbutton.addActionListener(buttonListener);
        groupEbutton.addActionListener(buttonListener);
        groupFbutton.addActionListener(buttonListener);
        groupGbutton.addActionListener(buttonListener);
        groupHbutton.addActionListener(buttonListener);

        arrowButton.addActionListener(e -> {
            arrowButton.toggle();
            groupSuppPanel.setVisible(!arrowButton.isOn());
        });

        //anonymous listeners pour le bouton de changement de langue
        helpButton.addActionListener(event -> {
            JDialog aboutDialog = new AboutDialog(getMainFrame(),
                    resources.getString(GuiConstants.help),
                    GuiUtilities.getImage("splashscreen", GuiConstants.imagesPath, GuiConstants.images));
            aboutDialog.setVisible(true);
        });

        //anonymous listeners pour le bouton minimiser
        miniButton.addActionListener(event -> setExtendedState(JFrame.ICONIFIED));

        //anonymous listeners pour le bouton de fermeture
        closeButton.addActionListener(event -> {
            int choix = showOptionDialog(resources.getString("confirmClose"));
            if (choix == GuiUtilities.YES_OPTION) {
                core.closeApplication();
            }
        });

        MouseAdapter menuButtonListener = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                if (event.getSource() instanceof MenuButton) {
                    fireMenuButtonClicked((MenuButton) event.getSource());
                }
            }
        };

        MouseAdapter menuMouseListener = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                getMainFrame().repaint();
            }

            @Override
            public void mouseExited(MouseEvent event) {
                fireMenuExit((JPopupMenu) event.getSource(), event.getPoint());
            }
        };

        PopupMenuListener popupMenuListener = new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent event) {
                fireMenuExit((JPopupMenu) event.getSource(), null);
            }
        };
        supervisionPanel.setButtonActions(buttonListener, menuButtonListener,
                menuMouseListener, popupMenuListener);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                core.closeApplication();
            }
        });

        MouseAdapter mouseAdapter = new MouseAdapter() {
            private int mouseX = 0;
            private int mouseY = 0;

            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getXOnScreen();
                mouseY = e.getYOnScreen();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int x = getX() + e.getXOnScreen() - mouseX;
                int y = getY() + e.getYOnScreen() - mouseY;
                setLocation(x, y);
                mouseX = e.getXOnScreen();
                mouseY = e.getYOnScreen();
            }
        };

        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);

        this.validate();
        this.setVisible(true);
    }

    @Override
    public void studentAdded(Object source, Student student) {
        initThumb(student);
        getMainFrame().repaint();
    }

    @Override
    public void studentChanged(Object source, Student student) {
        String id = student.getAddressIP();
        Thumbnail thumbnail = getThumbnail(id);
        if (thumbnail != null) {
            String name = student.getName();

            if (!supervisionPanel.getButton(GuiConstants.pairing).isOn()) {
                Student pairing = student.getPairing();
                if (pairing != null) {
                    name = name.concat(" & ").concat(pairing.getName());
                } else if (thumbnail.isThumbnailSelected()) {
                    name = name.concat(" & ");
                }
            }
            thumbnail.setSudentName(name);

            thumbnail.setGroup(student.getGroup());
            int percent = student.getBatteryLevel();
            if (percent < 0) {
                percent = 100;
            }
            thumbnail.setBatteryPercent(percent);
            thumbnail.setVisible(student.isOnLine());
        }
        this.repaint();
    }

    @Override
    public void buttonStateChanged(Object source, String name, boolean state) {
        updateButtons(name, state);
        this.repaint();
    }

    @Override
    public void groupSelectionChanged(Object source, int group, boolean selected) {
        selectGroup(group, selected);
        this.repaint();
    }

    @Override
    public void studentSelectedChanged(Object source, Student newStudent, Student oldStudent) {
        if (oldStudent != null) {
            setThumbSelected(oldStudent.getAddressIP(), false);
        }
        if (newStudent != null) {
            setThumbSelected(newStudent.getAddressIP(), true);
        }
        this.repaint();
    }

    @Override
    public void visuationStateChanged(Object source, int newState, int oldState) {
        String type = null;
        if (oldState == MasterCore.MOSAIQUE_MODE && newState == MasterCore.NORMAL_MODE) {
            type = GuiConstants.mosaique;
        } else if (oldState == MasterCore.SCANNING_MODE && newState == MasterCore.NORMAL_MODE) {
            type = GuiConstants.scanning;
        }
        if (type != null) {
            StateButton button = supervisionPanel.getButton(type);
            if (button != null) {
                button.setOn(true);
                updateButtonsForAction(button);
                getMainFrame().repaint();
            }
        }
    }

    @Override
    public void pairingStudentAssociatedChanged(Object source, Student student, Student associated) {
        if (student != null) {
            studentChanged(source, student);
        }
        if (associated != null) {
            studentChanged(source, associated);
        }
    }

    @Override
    public void newMessage(Object source, Student student, String type, Object... args) {
        String id = student.getAddressIP();
        String message;
        if (args == null || args.length == 0) {
            message = resources.getString(type);
        } else {
            if (type.contentEquals(GuiConstants.receiveFile)) {
                File file = (File) args[0];
//                String fileMessage = "<a href=\""+ file.getAbsolutePath()+"\">"+file.getName()+"</a> ";
                message = String.format(resources.getString(type), file.getName());
            } else {
                message = String.format(resources.getString(type), args);
            }
        }

        showPostIt(id, message);
        this.repaint();
    }

    @Override
    public void newMessage(Object source, String messageType, Object... args) {
        if (messageDialog != null && messageDialog.isVisible()) {
            return;
        }
        if (args == null) {
            messageDialog = GuiUtilities.showModelessMessage(resources.getString(messageType));
        } else {
            messageDialog = GuiUtilities.showModelessMessage(String.format(resources.getString(messageType), args));
        }
    }

    @Override
    public void processTitleChanged(Object source, String title) {
        processingBar.setTitle(title);
    }

    @Override
    public void processMessageChanged(Object source, String message) {
        processingBar.setMessage(message);
    }

    @Override
    public void processDeterminatedChanged(Object source, boolean determinated) {
        processingBar.setDeterminate(determinated);
    }

    @Override
    public void processDoubleStatusChanged(Object source, boolean doubleStatus) {
        processingBar.setDoubleProgress(doubleStatus);
    }

    @Override
    public void processBegin(Object source, boolean determinated) {
        processingBar.processBegin(determinated);
    }

    @Override
    public void processEnded(Object source, int exit) {
        processingBar.close();
    }

    @Override
    public void percentChanged(Object source, int percent) {
        processingBar.setValue(percent);
    }

    @Override
    public void percentChanged(Object source, int total, int subTotal) {
        processingBar.setValue(total, subTotal);
    }

    /**
     * Retourne la vignette élève désigné par l'identifiant (= adresse IP).
     *
     * @param id l'identifiant de l'élève (= adresse IP).
     *
     * @return la vignette élève, ou {@code null} si il n'y a pas d'élève avec l'identifiant.
     */
    private Thumbnail getThumbnail(String id) {
        return thumbnails.get(id);
    }

    /**
     * Action standard pour les boutons.
     *
     * @param button le bouton initialisateur de l'action.
     */
    private void fireButtonClicked(final StateButton button) {
        button.toggle();
        updateButtonsForAction(button);
//        secureButtons(true);
        switch (button.getType()) {
            case GuiConstants.masterScreen:
                if (!button.isOn()) {
                    button.getPopupMenu().show(button, 0, 0);
                } else {
                    core.executeCommand(GuiConstants.masterVoice, String.valueOf(false));
                }
                break;
            case GuiConstants.studentClose:
                if (!button.isOn()) {
                    button.getPopupMenu().show(button, 0, 0);
                }
                break;
            case GuiConstants.groupCreation:
                showGroupsCreation(!button.isOn());
                this.repaint();
                break;
            case GuiConstants.sendFile:
                Thread thread = new Thread(() -> {
                    sendFileAction();
                    button.toggle();
                    updateButtonsForAction(button);
                    getMainFrame().repaint();
                }, "sendFileButton");
                thread.start();
                break;
            default:
                core.executeCommand(button.getType(), String.valueOf(!button.isOn()));
        }
    }

    /**
     * Action standard pour les boutons de menu.
     *
     * @param menuButton le bouton initialisateur de l'action.
     */
    private void fireMenuButtonClicked(MenuButton menuButton) {
        core.executeCommand(menuButton.getType(), String.valueOf(true));
        StateButton button = menuButton.getParentButton();
        boolean toggle = (button.getType().contentEquals(GuiConstants.studentClose));
        if (toggle) {
            button.toggle();
            updateButtonsForAction(button);
            getMainFrame().repaint();
        }
    }

    /**
     * Action pour la sortie d'un menu sans avoir valider une action.
     *
     * @param menu le menu d'où l'on est sorti.
     * @param point la location sur l'écran ou est le pointeur.
     */
    private void fireMenuExit(JPopupMenu menu, Point point) {
        StateButton button = supervisionPanel.getButton(menu);

        if (button != null && (point == null || !menu.contains(point))) {
            menu.setVisible(false);
            button.toggle();
            updateButtonsForAction(button);
            getMainFrame().repaint();
        }
    }

    /**
     * Action pour envoyer un fichier. Ouvre une fenêtre d'explorateur de fichier.
     */
    private void sendFileAction() {
        File file = chooser.getSelectedFile(FileChooser.LOAD);
        if (file != null) {
            core.executeCommand(GuiConstants.sendFile, file.getAbsolutePath());
        }
    }

    /**
     * Initialise une vignette élève.
     *
     * @param student l'évéve correspondant à la nouvelle vignette.
     */
    private void initThumb(Student student) {
        final String id = student.getAddressIP();
        final Thumbnail thumbnail = new Thumbnail(
                GuiUtilities.getImageIcon("thumbnailImage", GuiConstants.imagesPath, GuiConstants.images).getImage(),
                GuiUtilities.getImageIcon("checkImage", GuiConstants.imagesPath, GuiConstants.images).getImage(),
                GuiUtilities.getImageIcon("batteryImage", GuiConstants.imagesPath, GuiConstants.images).getImage());

        thumbnail.setSudentName(student.getName());
        thumbnail.setGroup(student.getGroup());
        thumbnail.setBatteryPercent(student.getBatteryLevel());

        thumbnails.put(id, thumbnail);
        thumbPanel.addThumb(thumbnail);

        MouseAdapter groupCreationListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() instanceof Pastille) {
                    String group = ((Pastille) e.getSource()).getGroupLabel();
                    core.executeCommand(GuiConstants.creationGroup + group, id);
                }
            }
        };
        thumbnail.addCreationListener(groupCreationListener);

        thumbnail.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (thumbnail.isPostItVisible()) {
                    thumbnail.showPostIt(null, false);
                    getMainFrame().repaint();
                } else {
                    core.executeCommand(GuiConstants.student, id);
                }
            }
        });
    }

    /**
     * Modifie l'état de sélection d'un groupe.
     *
     * @param group l'identifiant du groupe.
     * @param select {@code true} pour la sélection de groupe, ou {@code false} pour la désélection de groupe.
     */
    private void selectGroup(int group, boolean select) {
        for (Thumbnail thumbnail : thumbnails.values()) {
            if (thumbnail.getGroup() == group) {
                thumbnail.setGroupSelected(select);
            }
        }
        setGroupFunctionsEnabled(hasGroupSelected());
    }

    /**
     * Affiche un message sur le post-it d'une vignette.
     *
     * @param id l'indentifiant de la vignette.
     * @param message le message à afficher.
     */
    private void showPostIt(String id, String message) {
        Thumbnail thumbnail = getThumbnail(id);
        if (thumbnail != null) {
            thumbnail.showPostIt(message, true);
        }
    }

    /**
     * Affiche ou cache le menu de création de groupes.
     *
     * @param visible {@code true} pour l'afficher, ou {@code false} pour revenir à l'affichage standard.
     */
    private void showGroupsCreation(boolean visible) {
        for (Thumbnail thumbnail : thumbnails.values()) {
            thumbnail.showGroupCreation(visible);
        }

        boolean hasGroupSupp = false;
        if (!visible) {
            for (Thumbnail thumbnail : thumbnails.values()) {
                if (thumbnail.getGroup() > Constants.GROUP_D) {
                    hasGroupSupp = true;
                    break;
                }
            }
        }

        setGroupSuppButtonsVisible(!visible && hasGroupSupp);
    }

    /**
     * Affiche ou cache la sélection potentiel de groupes supplémentaires.
     *
     * @param visible {@code true} pour l'afficher, ou {@code false} pour revenir à l'affichage standard.
     */
    private void setGroupSuppButtonsVisible(boolean visible) {
        arrowButton.setVisible(visible);
        if (visible) {
            arrowButton.setOn(true);
        } else {
            groupSuppPanel.setVisible(false);
        }
    }

    /**
     * Retourne si un des groupes est actif.
     *
     * @return si un des groupes est actif.
     */
    private boolean hasGroupSelected() {
        boolean hasGroupSelected = false;
        for (Thumbnail thumbnail : thumbnails.values()) {
            if (thumbnail.isGroupSelected()) {
                hasGroupSelected = true;
                break;
            }
        }
        return hasGroupSelected;
    }

    /**
     * Sélectionne la vignette élève indiquée par l'identifiant.
     *
     * @param id l'indentifiant de la vignette.
     * @param selected {@code true} pour la sélectionner, ou {@code false} pour la désélectionner.
     */
    private void setThumbSelected(String id, boolean selected) {
        Thumbnail thumbnail = getThumbnail(id);
        if (thumbnail != null) {
            thumbnail.setThumbnailSelected(selected);
        }
    }

    /**
     * Indique si le bouton est un bouton de sélection de groupe.
     *
     * @param button le bouton à tester
     *
     * @return {@code true} si le bouton est un bouton de sélection de groupe.
     */
    private boolean isGroupButton(JButton button) {
        return button == groupAbutton || button == groupBbutton
                || button == groupCbutton || button == groupDbutton
                || button == groupEbutton || button == groupFbutton
                || button == groupGbutton || button == groupHbutton;
    }

    /**
     * Modifie l'état d'un bouton et les actions possibles.
     *
     * @param type le type du bouton.
     * @param state le nouvel état du bouton.
     *
     * @deprecated
     */
    @Deprecated
    private void updateButtons(String type, boolean state) {
        StateButton button = null;
//        if(deleteDoucumentButton.getType().contentEquals(type)) {
//            button = deleteDoucumentButton;
//        }
//        else if(applicationButton.getType().contentEquals(type)) {
//            button = applicationButton;
//        }

        if (button != null) {
            button.setOn(state);
            updateButtonsForAction(button);
        }
    }

    /**
     * Modifie les actions possibles suivant l'état du bouton indiqué.
     *
     * @param button le bouton initialisateur de l'action.
     */
    private void updateButtonsForAction(StateButton button) {
        boolean enable = button.isOn();
        setGroupButtonsEnabled(enable || isGroupButton(button));
        updateSupervisionButtons(button);
        this.repaint();
    }

    /**
     * Modifie la possibilité de sélectionner des groupes.
     *
     * @param enable {@code true} pour pouvoir sélectioner des groupes, ou {@code false} pour l'impossibilité de
     *         sélection.
     */
    private void setGroupButtonsEnabled(boolean enable) {
        groupAbutton.setEnabled(enable);
        groupBbutton.setEnabled(enable);
        groupCbutton.setEnabled(enable);
        groupDbutton.setEnabled(enable);
        groupEbutton.setEnabled(enable);
        groupFbutton.setEnabled(enable);
        groupGbutton.setEnabled(enable);
        groupHbutton.setEnabled(enable);
        arrowButton.setEnabled(enable);
    }

    /**
     * Modifie les actions de supervisions possibles suivant l'état du bouton indiqué.
     *
     * @param button le bouton initialisateur de l'action.
     */
    private void updateSupervisionButtons(StateButton button) {
        boolean hasGroup = hasGroupSelected();
        boolean isGroupButton = isGroupButton(button);
        supervisionPanel.updateButtonsFor(button, hasGroup, isGroupButton);
    }

    /**
     * Modifie la possibilité de sélectionner des fonctions de groupes.
     *
     * @param enable {@code true} pour pouvoir sélectioner des fonctions de groupes, ou {@code false} pour
     *         l'impossibilité de sélection.
     */
    private void setGroupFunctionsEnabled(boolean enable) {
        supervisionPanel.setGroupFonctionsEnabled(enable);
//	updateLaboratoryGroupFunctions(enable);
//	updateQCMGroupFunctions(enable);
//	updateAutorisationsGroupFunctions(enable);
    }

    /**
     * Change la langue de l'interface.
     *
     * @param locale la nouvelle langue de l'interface.
     */
    private void changeLanguage(Locale locale) {
        this.setLocale(locale);

        File languageFile = new File(userHome, "language.xml");
        Utilities.saveText(CommandXMLUtilities.getLanguageXML(locale.getLanguage()), languageFile);

        //internalisation des différents textes
        resources.updateLocale(locale);

        //Boutons généraux
        helpButton.setToolTipText(getToolTipText(GuiConstants.language));
        miniButton.setToolTipText(getToolTipText(GuiConstants.mini));
        closeButton.setToolTipText(getToolTipText(GuiConstants.close));
        languageButton.setToolTipText(getToolTipText(GuiConstants.language));

        //Boutons pour les groupes
        groupMenu.changeTitle(resources.getString("groupLabel"));
        groupAbutton.setToolTipText(getToolTipText(GuiConstants.groupA));
        groupBbutton.setToolTipText(getToolTipText(GuiConstants.groupB));
        groupCbutton.setToolTipText(getToolTipText(GuiConstants.groupC));
        groupDbutton.setToolTipText(getToolTipText(GuiConstants.groupD));
        groupEbutton.setToolTipText(getToolTipText(GuiConstants.groupE));
        groupFbutton.setToolTipText(getToolTipText(GuiConstants.groupF));
        groupGbutton.setToolTipText(getToolTipText(GuiConstants.groupG));
        groupHbutton.setToolTipText(getToolTipText(GuiConstants.groupH));

        supervisionPanel.updateLanguage(resources);
        chooser.updateLanguage();

        this.repaint();
    }

    private String getToolTipText(String type) {
        String text;
        switch (type) {
            case GuiConstants.scanning:
            case GuiConstants.mosaique:
            case GuiConstants.sendFile:
            case GuiConstants.blackScreen:
            case GuiConstants.masterScreen:
            case GuiConstants.studentScreen:
            case GuiConstants.sendMessage:
            case GuiConstants.studentClose:
                text = "<html><center>" + resources.getString(type) + "<br />"
                        + resources.getString("groupFunction") + "</center></html>";
                break;
            case GuiConstants.groupCreation:
                text = "<html><center>" + resources.getString(type) + "<br />"
                        + resources.getString("noGroupFunction") + "</center></html>";
                break;
            default:
                text = resources.getString(type);
        }
        return text;
    }

    /**
     * Création d'un bouton avec un état avec son type.
     *
     * @param type le type de bouton
     *
     * @return le bouton créé.
     */
    private StateButton getButton(String type) {
        StateButton button = new StateButton(this, type);

        if (!type.contentEquals(GuiConstants.arrow)) {
            button.setToolTipText(getToolTipText(type));
        }

        return button;
    }

    /**
     * Affiche une boîte de dialogue avec une entrée texte.
     *
     * @param message le message à afficher.
     * @param initValue la valeur initiale ({@code null} si pas de valeur).
     *
     * @return le texte qui a été validé ou {@code null} si l'opération a été annulée.
     */
    protected String showInputDialog(String message, String initValue) {
        return (String) GuiUtilities.showInputDialog(this, message, null, initValue);
    }

    /**
     * Afficge une boîte de dialogue posant une question.
     *
     * @param message le message à afficher.
     *
     * @return {@code JOptionPane.YES_OPTION} si le bouton oui a été cliqué ou {@code JOptionPane.NO_OPTION} si c'est le
     *         bouton non.
     */
    protected int showOptionDialog(String message) {
        return GuiUtilities.showOptionDialog(this, message, null, null);
    }

    /**
     * Affiche un message à l'écran.
     *
     * @param message le message à afficher.
     */
    protected void showMessageDialog(String message) {
        GuiUtilities.showMessageDialog(this, message);
    }

    /**
     * Affiche une boîte de dialogue avec une liste de choix.
     *
     * @param message le message à afficher.
     * @param title le titre de la fenêtre.
     * @param values les valeurs que l'on peut sélectionnées.
     * @param initialValue la valeur sélectionnée au départ.
     *
     * @return l'Object sélectionnée ou {@code null} si pas de sélection.
     */
    private Object showInputDialog(String message, String title,
            Object[] values, Object initialValue) {
        return GuiUtilities.showInputDialog(this, message, title, values, initialValue);
    }

    /**
     * Retourne la fenêtre principale. Utilisée dans les actionPerformed.
     *
     * @return la fenêtre principale.
     */
    private JFrame getMainFrame() {
        return this;
    }
}
