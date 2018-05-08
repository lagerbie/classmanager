/*
 * ClassManager - Supervision de classes et Laboratoire de langue
 * Copyright (C) 2013 Fabrice Alleau <fabrice.alleau@siclic.fr>
 *
 * This file is part of ClassManager.
 *
 * ClassManager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ClassManager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ClassManager.  If not, see <http://www.gnu.org/licenses/>.
 */
package thot.gui;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import thot.StudentCore;
import thot.utils.Constants;

/**
 * Fenêtre de login des élèves.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Login extends JFrame {
    private static final long serialVersionUID = 19000L;
    /*
     * Resources textes : login, password, connect, loginMessage, badLoginEmpty,
     *                    badLoginCharacters
     * 
     * Resources images : fondLogin, valid
     */

    /**
     * Référence du noyau.
     */
    private StudentCore noyau;
    /**
     * Resources textuelle.
     */
    private Resources resources;

    /**
     * Image de fond de la fenêtre de login.
     */
    private Image backgroundImage;
    /**
     * Label pour le message.
     */
    private JLabel messageLabel;
    /**
     * Label pour le login.
     */
    private JLabel loginLabel;
    /**
     * Label pour le mot de passe.
     */
    private JLabel passwordLabel;
    /**
     * Champ pour le login.
     */
    private JTextField loginTextField;
    /**
     * Champ pour le mot de passe.
     */
    private JPasswordField passwordField;
    /**
     * Bouton de validation.
     */
    private JButton loginButton;
    /**
     * Fenêtre de message.
     */
    private JDialog messageDialog;

//    public static void main(String[] args){
//        Resources resources = new Resources(null);
//        StudentCore core = new StudentCore(resources, "228.5.6.7", true);
//        Login login = new Login(core, resources);
//        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        login.setSize(1024, 768);
//        login.showLogin(true);
//    }

    /**
     * Initialisation.
     *
     * @param noyau référence sur le noyau élève.
     * @param resources les resources textuelles.
     */
    public Login(StudentCore noyau, Resources resources) {
        super(Constants.softName);

        this.noyau = noyau;
        this.resources = resources;

        this.setResizable(false);
        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.setIconImages(GuiUtilities.getIcons());

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        Image fond = GuiUtilities.getImage("fondLogin");
        backgroundImage = fond.getScaledInstance(
                dim.width, dim.height, Image.SCALE_AREA_AVERAGING);

        Color textColor = Color.GRAY;
        Font font = new Font("Arial", Font.BOLD, 16);

        messageLabel = new JLabel(resources.getString("loginMessage"));
        messageLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        messageLabel.setForeground(textColor);
        messageLabel.setFont(font);

        loginLabel = new JLabel(resources.getString("login"));
        loginLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        loginLabel.setForeground(textColor);
        loginLabel.setFont(font);

        passwordLabel = new JLabel(resources.getString("password"));
        passwordLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        passwordLabel.setForeground(textColor);
        passwordLabel.setFont(font);

        loginTextField = new JTextField(26);
        loginTextField.setMaximumSize(new Dimension(200, 20));
        loginTextField.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        passwordField = new JPasswordField(26);
        passwordField.setMaximumSize(new Dimension(200, 20));
        passwordField.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        loginButton = new JButton(GuiUtilities.getImageIcon("valid"));
        loginButton.setBorderPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setFocusPainted(false);
        loginButton.setToolTipText(resources.getString("connect"));

        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new BoxLayout(verticalPanel, BoxLayout.Y_AXIS));
        verticalPanel.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        verticalPanel.add(loginLabel);
        verticalPanel.add(Box.createVerticalStrut(10));
        verticalPanel.add(loginTextField);
        verticalPanel.add(Box.createVerticalStrut(20));
        verticalPanel.add(passwordLabel);
        verticalPanel.add(Box.createVerticalStrut(10));
        verticalPanel.add(passwordField);
        verticalPanel.add(Box.createVerticalStrut(20));
        verticalPanel.add(loginButton);

        JPanel horizontalPanel = new JPanel();
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.X_AXIS));
        horizontalPanel.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        horizontalPanel.add(Box.createHorizontalStrut(dim.width / 2));
        horizontalPanel.add(verticalPanel);
        horizontalPanel.add(Box.createHorizontalGlue());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        mainPanel.add(Box.createVerticalStrut(dim.height / 4));
        mainPanel.add(messageLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(horizontalPanel);
        mainPanel.add(Box.createVerticalGlue());

        JPanel backgroundPanel = new JPanel() {
            private static final long serialVersionUID = 19000L;

            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(backgroundImage, 0, 0, this);
            }
        };
        backgroundPanel.setPreferredSize(dim);
        backgroundPanel.setMinimumSize(dim);
        backgroundPanel.setMaximumSize(dim);

        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.add(Box.createVerticalStrut(5));
        backgroundPanel.add(mainPanel);

        this.getContentPane().add(backgroundPanel);
        this.setSize(dim);
        this.validate();

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    checkLogin();
                }
            }
        };

        FocusListener focusListener = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        };

        loginTextField.addKeyListener(keyAdapter);
        passwordField.addKeyListener(keyAdapter);
        loginButton.addKeyListener(keyAdapter);

        loginTextField.addFocusListener(focusListener);
        passwordField.addFocusListener(focusListener);
        loginButton.addFocusListener(focusListener);
        this.addFocusListener(focusListener);

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                checkLogin();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                repaint();
            }
        });
    }

    /**
     * Modifie la langue de la fenêtre.
     */
    public void updateLanguage() {
        loginLabel.setText(resources.getString("login"));
        passwordLabel.setText(resources.getString("password"));
        messageLabel.setText(resources.getString("loginMessage"));
        loginButton.setToolTipText(resources.getString("connect"));
        this.validate();
    }

    /**
     * Affiche la fenêtre de login.
     * Remplace la fonction {@code setVisible(true)}.
     *
     * @param visible l'état visible ou non.
     */
    public void showLogin(boolean visible) {
        if (visible) {
            this.setVisible(true);
            this.toFront();
            loginTextField.grabFocus();
        } else {
            if (messageDialog != null) {
                messageDialog.setModal(false);
                messageDialog.setVisible(false);
                messageDialog.dispose();
                messageDialog = null;
            }
            this.setVisible(false);
        }
    }

    /**
     * Réinitialise les champs par défaut.
     */
    public void reset() {
        loginTextField.setText("");
        passwordField.setText("");
    }

    /**
     * Appel la validation du login et mot de passe.
     */
    private void checkLogin() {
        String login = loginTextField.getText();
        char[] pwd = passwordField.getPassword();
        String password = new String(pwd);

        if (login.isEmpty()) {// || password.isEmpty()
            showMessage("badLoginEmpty");
            return;
        }

        //vérification des caractères spéciaux
        for (int i = 0; i < login.length(); i++) {
            if (!Character.isLetterOrDigit(login.charAt(i))) {
                showMessage("badLoginCharacters");
                reset();
                return;
            }
        }

        noyau.checkLogin(loginTextField.getText(), password);
        this.setVisible(false);
    }

    /**
     * Affiche un message.
     *
     * @param type le type du message.
     */
    public void showMessage(String type) {
        if (!this.isVisible()) {
            showLogin(true);
        }

        messageDialog = GuiUtilities.getMessageDialog(this, resources.getString(type));
        messageDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        messageDialog.setModalityType(ModalityType.DOCUMENT_MODAL);
        messageDialog.setVisible(true);
        messageDialog.toFront();
    }
}
