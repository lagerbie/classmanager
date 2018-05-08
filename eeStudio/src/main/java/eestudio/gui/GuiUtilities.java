package eestudio.gui;

import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utilitaires graphiques.
 *
 * @author Fabrice Alleau
 */
@Deprecated
public class GuiUtilities {
    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiUtilities.class);

    /**
     * Option "oui" sélectionnée
     */
    public static final int YES_OPTION = JOptionPane.YES_OPTION;
    /**
     * Option "non" sélectionnée
     */
    public static final int NO_OPTION = JOptionPane.NO_OPTION;

    /**
     * Chemin des images
     */
    private static final String imagesPath = "eestudio/resources/images/";
    /**
     * Réfrérences sur les images selon leur type
     */
    private static final ResourceBundle images = ResourceBundle.getBundle("eestudio.resources.images");
    /**
     * Liste d'icones de l'application
     */
    public static final List<Image> icones;

    /**
     * Taille des images pour affecter une couronne
     */
    private static final int imagesDim = 48;

    /**
     * Initilisations des icones.
     *
     */
    static {
        Image icone = getImage("icone");
        Image iconeVide = getImage("iconeVide");
        icones = new ArrayList<>(2);
        icones.add(icone);
        icones.add(iconeVide);
    }

    /**
     * Modifie de façon générale les propriétés graphiques de certains éléments.
     *
     * @param custom été de personnalisation.
     */
    public static void manageUI(boolean custom) {
        Color labelColor = null;
        Color inactiveColor = null;
        Color transparentColor = null;
        Color fieldBackground = null;

        if (custom) {
            labelColor = Color.WHITE;
            fieldBackground = Color.WHITE;
            inactiveColor = new Color(180, 180, 180);
            transparentColor = new Color(0, 0, 0, 0);
        }

        UIManager.put("CheckBox.background", transparentColor);
        UIManager.put("CheckBox.disabledText", inactiveColor);
        UIManager.put("CheckBox.foreground", labelColor);

        UIManager.put("ComboBox.background", fieldBackground);

        UIManager.put("Label.foreground", labelColor);

        UIManager.put("Panel.background", transparentColor);

        UIManager.put("RadioButton.background", transparentColor);
        UIManager.put("RadioButton.foreground", labelColor);

        UIManager.put("ScrollPane.background", transparentColor);
    }

    /**
     * Affiche une boîte de dialogue avec une liste de choix ou un champ texte.
     *
     * @param parent la fenêtre parente.
     * @param message le message à afficher.
     * @param options les valeurs que l'on peut sélectionnées (null si texte).
     * @param initValue la valeur initiale ({@code null} si pas de valeur).
     *
     * @return l'Object sélectionné ou {@code null} si pas de sélection.
     */
    public static Object showInputDialog(Window parent, String message, Object[] options, Object initValue) {
        return JOptionPane.showInputDialog(parent, message, UIManager.getString("OptionPane.inputDialogTitle"),
                JOptionPane.QUESTION_MESSAGE, new ImageIcon(), options, initValue);
    }

//    /**
//     * Affiche une boîte de dialogue avec une liste de choix ou un champ texte.
//     *
//     * @param parent la fenêtre parente.
//     * @param message le message à afficher.
//     * @param title le titre de la fenêtre.
//     * @param options les valeurs que l'on peut sélectionnées (null si texte).
//     * @param initValue la valeur initiale ({@code null} si pas de valeur).
//     * @return l'Object sélectionné ou {@code null} si pas de sélection.
//     */
//    public static Object showInputDialog(Window parent, String message,
//            String title, Object[] options, Object initValue) {
//        return JOptionPane.showInputDialog(parent, message,
//                UIManager.getString("OptionPane.inputDialogTitle"),
//                JOptionPane.QUESTION_MESSAGE, new ImageIcon(),
//                options, initValue);
//    }

    /**
     * Affiche une boîte de dialogue posant une question.
     *
     * @param parent la fenêtre parente.
     * @param message le message à afficher.
     * @param options les valeurs que l'on peut sélectionnées (null si texte).
     * @param initValue la valeur initiale ({@code null} si pas de valeur).
     *
     * @return {@code YES_OPTION} si le bouton oui a été cliqué
     *         ou {@code NO_OPTION} si c'est le bouton non.
     */
    public static int showOptionDialog(Window parent, String message, Object[] options, Object initValue) {
        return JOptionPane.showOptionDialog(parent, message, UIManager.getString("OptionPane.messageDialogTitle"),
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(), options, initValue);
    }

    /**
     * Affiche une boîte de dialogue posant une question.
     *
     * @param parent la fenêtre parente.
     * @param message le message à afficher.
     * @param options les valeurs que l'on peut sélectionnées (null si texte).
     * @param initValue la valeur initiale ({@code null} si pas de valeur).
     *
     * @return {@code YES_OPTION} si le bouton oui a été cliqué
     *         ou {@code NO_OPTION} si c'est le bouton non.
     */
    public static int showOptionDialogWithCancel(Window parent, String message,
            Object[] options, Object initValue) {
        return JOptionPane.showOptionDialog(parent, message, UIManager.getString("OptionPane.messageDialogTitle"),
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, new ImageIcon(), options, initValue);
    }

    /**
     * Affiche un message à l'écran.
     *
     * @param parent la fenêtre parente.
     * @param message le message à afficher.
     */
    public static void showMessageDialog(Window parent, String message) {
        JOptionPane.showMessageDialog(parent, message, UIManager.getString("OptionPane.messageDialogTitle"),
                JOptionPane.INFORMATION_MESSAGE, new ImageIcon());
    }

    /**
     * Affiche un message formaté à l'écran.
     *
     * @param parent la fenêtre parente.
     * @param typeFormat le type pour le message formaté à afficher.
     * @param args les différnts objet du message.
     */
    public static void showMessageDialog(Window parent, String typeFormat, Object... args) {
        showMessageDialog(parent, String.format(typeFormat, args));
    }

//    /**
//     * Affiche un message sans bloquer l'élève.
//     *
//     * @param message le message.
//     * @param icone l'icone.
//     * @return la fenêtre ainsi créée.
//     */
//    public static JDialog showModelessMessage(String message, ImageIcon icone) {
//        JOptionPane optionPane = new JOptionPane(message, JOptionPane.PLAIN_MESSAGE);
//
//        JDialog dialog = optionPane.createDialog(UIManager.getString("OptionPane.messageDialogTitle"));
//        dialog.setModalityType(JDialog.ModalityType.MODELESS);
//        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//
//        if(icone == null)
//            dialog.setIconImage(new ImageIcon().getImage());
//        else
//            dialog.setIconImage(icone.getImage());
//
//        dialog.setVisible(true);
//        return dialog;
//    }

    /**
     * Change la langue de l'interface.
     *
     * @param locale la nouvelle langue de l'interface.
     */
    public static void setDefaultLocale(Locale locale) {
        Locale.setDefault(locale);
        UIManager.getDefaults().setDefaultLocale(locale);
        UIManager.getLookAndFeelDefaults().setDefaultLocale(locale);
        JComponent.setDefaultLocale(locale);
    }

//    /**
//     * Change la font par défaut.
//     * 
//     * @param fontName le nom la font par défaut.
//     */
//    public static void setDefaultFont(String fontName) {
//        Enumeration<Object> keys = UIManager.getDefaults().keys();
//        while(keys.hasMoreElements()) {
//            Object key = keys.nextElement();
//            Object value = UIManager.get(key);
//
//            if(value instanceof FontUIResource) {
//                int style = ((FontUIResource)value).getStyle();
//                int size = ((FontUIResource)value).getSize();
//                UIManager.put(key, new FontUIResource(fontName, style, size));
//            }
//        }
//    }

    /**
     * Retourne l'Image correspondante au type précisé. Ajoute une couronne blanche sur les images de taille imagesDim x
     * imagesDim.
     *
     * @param imageType le type de l'image.
     *
     * @return l'Image correspondante.
     */
    private static Image getImage(String imageType) {
        return getImageIcon(imageType).getImage();
    }

    /**
     * Retourne l'ImageIcon correspondant au type précisé. Ajoute une couronne blanche sur les images de taille
     * imagesDim x imagesDim.
     *
     * @param imageType le type de l'image.
     *
     * @return l'ImageIcon correspondant.
     */
    public static ImageIcon getImageIcon(String imageType) {
        BufferedImage image = null;
        String imagePath = imagesPath + images.getString(imageType);
        try {
            image = ImageIO.read(ClassLoader.getSystemResource(imagePath));

            int width = image.getWidth();
            int height = image.getHeight();

            double offsetExt = 1.5;
            double offsetInt = 3;

            Area area = new Area(
                    new Ellipse2D.Double(offsetExt, offsetExt, imagesDim - 2 * offsetExt, imagesDim - 2 * offsetExt));
            Area circle = new Area(
                    new Ellipse2D.Double(offsetInt, offsetInt, imagesDim - 2 * offsetInt, imagesDim - 2 * offsetInt));
            area.subtract(circle);
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            graphics.setColor(Color.WHITE);
            if (width == imagesDim && height == imagesDim) {
                graphics.fill(area);
            }

            area.reset();
            circle.reset();
            graphics.dispose();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return new ImageIcon(image);
    }

//    /**
//     * Retourne la liste d'icones de l'application.
//     * 
//     * @return la liste d'icones de l'application.
//     */
//    public static List<Image> getIcones() {
//        return icones;
//    }

//    /**
//     * Création d'un bouton avec des images.
//     * 
//     * @param parent la fenêtre parente.
//     * @param image l'image du bouton à l'état normal.
//     * @param imageDisabled l'image du bouton à l'état désactivé.
//     * @param text le texte de la bulle d'aide.
//     * @return le bouton créé.
//     */
//    public static JButton getDisableButton(Component parent,
//            String image, String imageDisabled, String text) {
//        return getButton(parent, image, imageDisabled, null, imageDisabled, null, text);
//    }

    /**
     * Création d'un bouton à deux états actifs avec des images.
     *
     * @param parent la fenêtre parente.
     * @param image l'image du bouton à l'état normal.
     * @param imageSelected l'image du bouton à l'état sélectionné.
     * @param text le texte de la bulle d'aide.
     *
     * @return le bouton créé.
     */
    public static JButton getSelectableButton(Component parent, ImageIcon image, ImageIcon imageSelected, String text) {
        return getButton(parent, image, null, imageSelected, null, null, text);
    }

    /**
     * Création d'un bouton avec des images avec un un effet d'appui.
     *
     * @param parent la fenêtre parente.
     * @param image l'image du bouton à l'état normal.
     * @param imageoff l'image du bouton à l'état désactivé et appuyé.
     * @param text le texte de la bulle d'aide.
     *
     * @return le bouton créé.
     */
    public static JButton getActionButton(Component parent, ImageIcon image, ImageIcon imageoff, String text) {
        return getButton(parent, image, imageoff, null, imageoff, null, text);
    }

    /**
     * Création d'un bouton avec des images.
     *
     * @param parent la fenêtre parente.
     * @param image l'image du bouton à l'état normal.
     * @param imageDisabled l'image du bouton à l'état désactivé.
     * @param imageSelected l'image du bouton à l'état sélectionné.
     * @param imagePressed l'image du bouton à l'état appuyé.
     * @param imageRollover l'image du bouton à l'état rollover.
     * @param text le texte de la bulle d'aide.
     *
     * @return le bouton créé.
     */
    private static JButton getButton(final Component parent, ImageIcon image, ImageIcon imageDisabled,
            ImageIcon imageSelected, ImageIcon imagePressed, ImageIcon imageRollover, String text) {

        JButton button = new JButton(image);
        if (imageDisabled != null) {
            button.setDisabledIcon(imageDisabled);
        }
        if (imageSelected != null) {
            button.setSelectedIcon(imageSelected);
        }
        if (imagePressed != null) {
            button.setPressedIcon(imagePressed);
        }
        if (imageRollover != null) {
            button.setRolloverIcon(imageRollover);
        }

        button.setMargin(new Insets(0, 0, 0, 0));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        button.setToolTipText(text);

        //anonymous listeners pour rafraichir la frame lors d'afichage des toolTip
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                parent.repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                parent.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                parent.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                parent.repaint();
            }
        });

        button.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                parent.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                parent.repaint();
            }
        });

        //pour éviter d'avoir un reste d'image quand le programme désactive le bouton
        button.addPropertyChangeListener(evt -> parent.repaint());

        return button;
    }

}
