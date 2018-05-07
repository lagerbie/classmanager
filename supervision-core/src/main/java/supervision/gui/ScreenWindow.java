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
package supervision.gui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import supervision.CommonLogger;
import supervision.Constants;
import supervision.ProgressThread;
import supervision.Utilities;

/**
 * Classe permettant d'afficher un écran d'un autre poste.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class ScreenWindow extends ProgressThread implements Runnable {

    /**
     * Adresse IP de l'envoyeur.
     */
    private String addressIP;
    /**
     * Nom de l'envoyeur.
     */
    private String name;
    /**
     * Port sur lequel l'écran est envoyé.
     */
    private int receptionPort;
    /**
     * Socket pour la réception de l'écran.
     */
    private Socket socketReception;
    /**
     * InputStream pour la réception de l'écran.
     */
    private InputStream inputStream;
    /**
     * Time out pour la réception des données.
     */
    private int timeout = 100;
    /**
     * Mode pour la prise en main.
     */
    private boolean control;
    /**
     * Port sur lequel on envoi le clavier et la souris.
     */
    private int envoiPort;
    /**
     * Socket pour l'envoi des contrôles de la souris et du clavier en prise en
     * main.
     */
    private Socket socketEnvoi;
    /**
     * OutputStream pour l'envoi des contrôles de la souris et du clavier en
     * prise en main.
     */
    private OutputStream outputStream;

    /**
     * Thread de transfert des données.
     */
    private Thread thread;
    /**
     * Etat de sortie.
     */
    private boolean run = false;

    /**
     * Permision de fermer le remote screen.
     */
    private boolean closeWindowEnabled;
    /**
     * Thread pour donner le focus à cette fenêtre.
     */
    private Runnable focusFonction;

    /**
     * Fenêtre principale pour le remote screen.
     */
    private Window window;
    /**
     * Position horizontale de la fenêtre.
     */
    private int x;
    /**
     * Position verticale de la fenêtre.
     */
    private int y;
    /**
     * Largeur de la fenêtre.
     */
    private int width;
    /**
     * Hauteur de la fenêtre.
     */
    private int height;

    /**
     * Buffer pour la reconstruction de l'image.
     */
    private BufferedImage remoteImage;
    /**
     * Buffer pour l'image redimensionnée.
     */
    private BufferedImage scaleImage;
    /**
     * Numéro de ligne maximun de l'écran envoyé.
     */
    private int indexMax = -1;
    /**
     * Ratio entre cet écran et l'écran envoyé.
     */
    private double ratio = 0;
    /**
     * Hauteur d'une ligne de l'image reçue.
     */
    private int remoteLineHeight;
    /**
     * Largeur de l'image reçue.
     */
    private int remoteWidth;
    /**
     * offset pour centrer l'image.
     */
    private int xOffset = 0;
    /**
     * offset pour centrer l'image.
     */
    private int yOffset = 0;
    /**
     * Nombre de lignes maximum.
     */
    private int ligneMax = 64;
    /**
     * Sauvegarde des différentes lignes.
     */
    private Image[] remoteTemp = new Image[ligneMax];

    private MediaTracker tracker;
    /**
     * Robot pour consummer la touche ALT sous Linux.
     */
    private Robot robot = null;
    /**
     * Pour cacher le curseur.
     */
    private boolean hideCursor = true;
    /**
     * Panel pour dessiner l'image.
     */
    private JPanel backgroundPanel;
    /**
     * Frame pour intercepter les évènements clavier sous Linux.
     */
    private JDialog dialogLinux;

    /**
     * Initialisation.
     *
     * @param closeWindowEnabled
     * @param envoiPort
     */
    public ScreenWindow(boolean closeWindowEnabled, int envoiPort) {
        this.closeWindowEnabled = closeWindowEnabled;
        this.envoiPort = envoiPort;

        try {
            robot = new Robot();
        } catch (AWTException e) {
            CommonLogger.error(e);
            System.exit(-1);
        }

        focusFonction = new Runnable() {
            @Override
            public void run() {
                Utilities.waitInMillisecond(2000);
                performClick(1000, 500);
            }
        };

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                sendKeyPressed(e.getKeyCode());

                e.consume();//consomme l'évènement
                //System.out.println("Key Pressed: " + e.getKeyChar());
                int code = e.getKeyCode();
                //System.out.println("Key Pressed: " + KeyEvent.getKeyText(code));

                //la touche ALT n'est pas consommé sur Linux
                if (code == KeyEvent.VK_ALT) {
                    robot.keyRelease(code);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                sendKeyReleased(e.getKeyCode());
            }
        };

        backgroundPanel = new JPanel() {
            private static final long serialVersionUID = 91L;

            @Override
            public void paintComponent(Graphics g) {
                paintImage(g);
            }
        };
        backgroundPanel.setBackground(Color.BLACK);

        if (Utilities.LINUX_PLATFORM) {
            //utilisation d'une JWindow pour masquer les 2 barres d'Ubuntu
            //implique l'utilisation d'une JFrame pour la capture des évènements
            //clavier.
            window = new JWindow();
            ((JWindow) window).getContentPane().add(backgroundPanel);

            dialogLinux = new JDialog();
            dialogLinux.setUndecorated(true);
            dialogLinux.pack();
            dialogLinux.addKeyListener(keyAdapter);
        } else {
            //utilisation d'une JDialog pour récupérer les évènements clavier
            //pas besoin d'une frame secondaire
            //la méthode pour LINUX ne permet pas d'avoir simplement le focus
            //clavier sous Windows
            window = new JDialog();
            ((JDialog) window).setUndecorated(true);
            ((JDialog) window).getContentPane().add(backgroundPanel);
        }

        window.setAlwaysOnTop(true);
        window.setFocusable(true);
        window.setBackground(Color.BLACK);

        tracker = new MediaTracker(window);

        window.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                sendMouseMoved(e.getX(), e.getY());
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                sendMouseDragged(e.getX(), e.getY());
            }
        });

        window.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1
                        && SwingUtilities.isLeftMouseButton(e)) {
                    fireMouseClicked(e.getXOnScreen(), e.getYOnScreen());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                sendMousePressed(e.getModifiers());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                sendMouseReleased(e.getModifiers());
            }
        });

        window.addKeyListener(keyAdapter);
    }

    /**
     * Effectue automatiquement un click droit de la souris.
     * Permet d'avoir le focus clavier sur la JDialog sous Windows.
     *
     * @param x position x de la souris.
     * @param y position y de la souris.
     */
    private void performClick(int x, int y) {
        Point point = MouseInfo.getPointerInfo().getLocation();
        robot.mouseMove(x, y);
        robot.mousePress(MouseEvent.BUTTON1_MASK);
        robot.mouseRelease(MouseEvent.BUTTON1_MASK);
        //retour à la position précédente
        robot.mouseMove(point.x, point.y);
    }

    /**
     * Démarre la réception de l'écran.
     * Equivalent à <code>start(addressIP, port, priseenmain, null, 0)</code>.
     *
     * @param addressIP l'adresse IP de celui qui envoi l'écran.
     * @param port le port de communication utilisé pour les données de l'écran.
     * @param control <code>true</code> pour une prise en main,
     * sinon <code>false</code>.
     * @param nbLines le nombre de lignes.
     */
    public void start(String addressIP, int port, boolean control, int nbLines) {
        start(addressIP, port, control, nbLines, null);
    }

    /**
     * Démarre la réception de l'écran.
     * Equivalent à <code>start(addressIP, port, priseenmain, nom, numero, 0, 0,
     * dim.width, dim.height)</code>.
     *
     * @param addressIP l'adresse IP de celui qui envoi l'écran.
     * @param port le port de communication utilisé pour les données de l'écran.
     * @param control <code>true</code> pour une prise en main,
     * sinon <code>false</code>.
     * @param nbLines le nombre de lignes.
     * @param nom le nom du diffuseur.
     */
    public void start(String addressIP, int port, boolean control, int nbLines,
            String nom) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        start(addressIP, port, control, nbLines, nom, 0, 0, dim.width, dim.height);
    }

    /**
     * Démarre la réception de l'écran.
     *
     * @param addressIP l'adresse IP de celui qui envoi l'écran.
     * @param port le port de communication utilisé pour les données de l'écran.
     * @param control <code>true</code> pour une prise en main,
     * sinon <code>false</code>.
     * @param nbLines le nombre de lignes.
     * @param x la position horizontale de la fenêtre.
     * @param y la position verticale de la fenêtre.
     * @param width la largeur de la fenêtre.
     * @param height la hauteur de la fenêtre.
     * @param nom le nom du diffuseur.
     */
    private void start(String addressIP, int port, boolean control, int nbLines,
            String nom, int x, int y, int width, int height) {
        this.addressIP = addressIP;
        this.receptionPort = port;
        this.control = control;
        this.name = nom;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        remoteLineHeight = -1;
        ratio = 0;

        indexMax = nbLines - 1;
        if (nbLines > 8) {
            indexMax = nbLines - 3;
        }

        scaleImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        remoteImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        window.setSize(this.width, this.height);
        window.setLocation(this.x, this.y);

        if (!control && hideCursor) {
            hideCursor();
        } else {
            window.setCursor(null);
        }

        if (control) {
            connectEnvoi();
        }

        // On affiche la fenetre
        run = true;
        thread = new Thread(this, this.getClass().getName());
        thread.start();
        showWindow(true);

        Thread focusThread = new Thread(focusFonction, "focus");
        focusThread.start();

        fireProcessBegin(false);
    }

    /**
     * Arrête le processus.
     *
     * @return si le traitement est en cours.
     */
    public boolean isRun() {
        return run;
    }

    /**
     * Cache le curseur.
     */
    private void hideCursor() {
        int[] pixels = new int[16 * 16];
        Image image = Toolkit.getDefaultToolkit()
                .createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
        Cursor transparentCursor = Toolkit.getDefaultToolkit()
                .createCustomCursor(image, new Point(0, 0), "invisibleCursor");
        window.setCursor(transparentCursor);
    }

    /**
     * Modifie le timeout.
     *
     * @param timeout le temps d'attente maximun en milliseconde.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Retourne le timeout.
     *
     * @return le temps d'attente maximun en milliseconde.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Montre ou cache la fenêtre. Remplace la fonction
     * <code>setVisible(visible)</code>.
     *
     * @param visible la visibilité de la fenêtre.
     */
    public void showWindow(boolean visible) {
        if (visible) {
            if (Utilities.LINUX_PLATFORM) {
                dialogLinux.setVisible(true);
                dialogLinux.requestFocus();
//                dialogLinux.toFront();//add v1.70 à tester
            }
            window.setVisible(true);
            window.requestFocus();
            window.toFront();
            window.repaint();
        } else {
            if (Utilities.LINUX_PLATFORM) {
                //initialise l'affichage pour que la frame est le focus du clavier.
                if (!window.isVisible()) {
                    window.setVisible(true);
                }
                dialogLinux.setVisible(false);
            }
            window.setVisible(false);
        }
    }

    /**
     * Dispose des ressources graphiques.
     */
    private void close() {
        if (socketEnvoi != null) {
            disconnectEnvoi();
        }
        if (socketReception != null) {
            disconnectReception();
        }

        if (remoteImage != null) {
            remoteImage.flush();
            remoteImage = null;
        }

        if (scaleImage != null) {
            scaleImage.flush();
            scaleImage = null;
        }

        for (int i = 0; i < ligneMax; i++) {
            if (remoteTemp[i] != null) {
                remoteTemp[i].flush();
                remoteTemp[i] = null;
            }
        }
    }

    /**
     * Arrête la réception de l'écran avec une commande spécifique.
     */
    private void closeWithCommand() {
        run = false;
//        showWindow(false);

        closeCommand();

        if (control) {
            disconnectEnvoi();
        }
    }

    /**
     * Commande spécifique lors de l'arrêt de la réception.
     */
    protected void closeCommand() {
    }

    /**
     * Arrête la réception de l'écran.
     */
    public void stop() {
        if (!run) {
            return;
        }

        run = false;
//        showWindow(false);

        if (control) {
            sendClose();
            disconnectEnvoi();
        }
    }

    /**
     * Met à jour l'image quand des paramètres ont changés.
     */
    private void updateImage() {
        double remoteScreenHeight = remoteLineHeight * indexMax + remoteLineHeight;
        double remoteScreenWidth = remoteWidth;

        int w = window.getWidth();
        int h = window.getHeight();

        double gdRatioX = w / remoteScreenWidth;
        double gdRatioY = h / remoteScreenHeight;

        ratio = Math.min(gdRatioX, gdRatioY);

        xOffset = (int) ((w - remoteScreenWidth * ratio) / 2);
        yOffset = (int) ((h - remoteScreenHeight * ratio) / 2);

        if (remoteScreenHeight != remoteImage.getHeight()) {
            remoteImage.flush();
            remoteImage = new BufferedImage((int) remoteScreenWidth,
                    (int) remoteScreenHeight, BufferedImage.TYPE_INT_RGB);
        }

        int scaleWidth = (int) (remoteScreenWidth * ratio);
        int scaleHeight = (int) (remoteScreenHeight * ratio);
        if (scaleHeight != scaleImage.getHeight()
                || scaleWidth != scaleImage.getWidth()) {
            scaleImage.flush();
            scaleImage = new BufferedImage(scaleWidth, scaleHeight,
                    BufferedImage.TYPE_INT_RGB);
        }

        window.repaint();
    }

    /**
     * Modifie l'index maximum pour les lignes utilisées par le découpage de
     * l'écran envoyé.
     *
     * @param index l'index maximun des lignes utilisées.
     */
    private void setPosMax(int index) {
        indexMax = index;
        remoteLineHeight = remoteTemp[index].getHeight(null);
        remoteWidth = remoteTemp[index].getWidth(null);

        updateImage();
    }

    /**
     * Connection de la socket pour la réception de l'écran.
     */
    private boolean connectReception() {
        boolean connected = true;
        InetSocketAddress socketAddress = new InetSocketAddress(addressIP, receptionPort);
        socketReception = new Socket();

        CommonLogger.debug("ScreenWindow.connectReception at ip: "
                + addressIP + " port: " + receptionPort + " ...");
        try {
            socketReception.setTcpNoDelay(true);
            socketReception.connect(socketAddress, timeout);
            CommonLogger.debug("...ScreenWindow.connectReception success");
            inputStream = new BufferedInputStream(socketReception.getInputStream());
        } catch (IOException e) {
            CommonLogger.warning("Error in ScreenWindow.connectReception: " + e);
            connected = false;
        }
        return connected;
    }

    /**
     * Déconnection de la socket pour la réception de l'écran.
     */
    private void disconnectReception() {
        if (socketReception != null) {
            try {
                socketReception.close();
                socketReception = null;
                inputStream = null;
            } catch (Exception e) {
                CommonLogger.error("Error in ScreenWindow.disconnectReception: " + e);
            }
        }
    }

    /**
     * Retourne si la socket de réception de l'écran est connectée.
     *
     * @return si la socket est connectée.
     */
    private boolean isReceptionConnected() {
        if (socketReception == null || !socketReception.isConnected()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Connection de la socket pour l'envoi des contrôle lors de la prise en
     * main.
     */
    private void connectEnvoi() {
        InetSocketAddress socketAddress = new InetSocketAddress(addressIP, envoiPort);
        socketEnvoi = new Socket();

        try {
            socketEnvoi.connect(socketAddress, Constants.TIME_MAX_FOR_ORDER);
            outputStream = socketEnvoi.getOutputStream();
        } catch (IOException e) {
            CommonLogger.error("Error in ScreenWindow.connectEnvoi: " + e);
        }
    }

    /**
     * Déconnection de la socket pour l'envoi des contrôles de la prise en main.
     */
    private void disconnectEnvoi() {
        if (socketEnvoi != null) {
            try {
                socketEnvoi.close();
                socketEnvoi = null;
                outputStream = null;
            } catch (Exception e) {
                CommonLogger.error("Error in ScreenWindow.disconnectEnvoi: " + e);
            }
        }
    }

    /**
     * Fonction pour le double clic de la souris.
     *
     * @param x position horizontale de la souris.
     * @param y position verticale de la souris.
     */
    protected void fireMouseClicked(int x, int y) {
    }

    /**
     * Envoi le contrôle spécifique au diffuseur d'écran.
     *
     * @param buffer la commande de contrôle.
     */
    private void send(byte[] buffer) {
        if (control && run) {
            if (socketEnvoi == null || !socketEnvoi.isConnected()) {
                connectEnvoi();
                if (socketEnvoi == null) {
                    return;
                }
            }

            try {
                outputStream.write(buffer, 0, buffer.length);
                outputStream.flush();
            } catch (Exception e) {
                CommonLogger.error("Error in ScreenWindow.send: " + e);
            }
        }
    }

    /**
     * Envoi l'évènement de déplacement de la souris.
     *
     * @param x la position horizontale de la souris.
     * @param y la position verticale de la souris.
     */
    private void sendMouseMoved(int x, int y) {
        int X = x;
        int Y = y;
        if (ratio != 0) {
            X = (int) ((x - xOffset) / ratio);
            Y = (int) ((y - yOffset) / ratio);
        }

        byte[] buffer = new byte[5];
        buffer[0] = Constants.MOUSE_MOVED;
        int hX = X / 16;
        buffer[1] = (byte) (hX & 0xff);
        int lX = X - (hX * 16);
        buffer[2] = (byte) (lX & 0xff);
        int hY = Y / 16;
        buffer[3] = (byte) (hY & 0xff);
        int lY = Y - (hY * 16);
        buffer[4] = (byte) (lY & 0xff);
        send(buffer);
    }

    /**
     * Envoi l'évènement de drag de la souris.
     *
     * @param x la position horizontale de la souris.
     * @param y la position verticale de la souris.
     */
    private void sendMouseDragged(int x, int y) {
        int X = x;
        int Y = y;
        if (ratio != 0) {
            X = (int) ((x - xOffset) / ratio);
            Y = (int) ((y - yOffset) / ratio);
        }

        byte[] buffer = new byte[5];
        buffer[0] = Constants.MOUSE_MOVED;
        int hX = X / 16;
        buffer[1] = (byte) (hX & 0xff);
        int lX = X - (hX * 16);
        buffer[2] = (byte) (lX & 0xff);
        int hY = Y / 16;
        buffer[3] = (byte) (hY & 0xff);
        int lY = Y - (hY * 16);
        buffer[4] = (byte) (lY & 0xff);
        send(buffer);
    }

    /**
     * Envoi l'évènement de l'appui d'un bouton de la souris.
     *
     * @param modifiers la valeur du bouton.
     */
    private void sendMousePressed(int modifiers) {
        byte[] buffer = new byte[2];
        buffer[0] = Constants.MOUSE_PRESSED;
        buffer[1] = (byte) (modifiers & 0xff);
        send(buffer);
    }

    /**
     * Envoi l'évènement du relachement d'un bouton de la souris.
     *
     * @param modifiers la valeur du bouton.
     */
    private void sendMouseReleased(int modifiers) {
        byte[] buffer = new byte[2];
        buffer[0] = Constants.MOUSE_RELEASED;
        buffer[1] = (byte) (modifiers & 0xff);
        send(buffer);
    }

    /**
     * Envoi l'évènement de l'appui d'une touche du clavier.
     *
     * @param key la valeur de la touche.
     */
    private void sendKeyPressed(int key) {
        if (key != KeyEvent.VK_ESCAPE) {
            byte[] buffer = new byte[3];
            buffer[0] = Constants.KEY_PRESSED;
            int high = key / 16;
            buffer[1] = (byte) (high & 0xff);
            int low = key - (high * 16);
            buffer[2] = (byte) (low & 0xff);
            send(buffer);
        } else if (closeWindowEnabled) {
            closeWithCommand();
        }
    }

    /**
     * Envoi l'évènement du relachement d'une touche du clavier.
     *
     * @param key la valeur de la touche.
     */
    private void sendKeyReleased(int key) {
        byte[] buffer = new byte[3];
        buffer[0] = Constants.KEY_RELEASED;
        int high = key / 16;
        buffer[1] = (byte) (high & 0xff);
        int low = key - (high * 16);
        buffer[2] = (byte) (low & 0xff);
        send(buffer);
    }

    /**
     * Envoi la commande d'arrêter l'envoi d'écran au diffuseur.
     */
    protected void sendClose() {
        byte[] buffer = new byte[2];
        buffer[0] = Constants.CLOSE;
        buffer[1] = 0;

//        if(priseEnMain == 0) {
//            priseEnMain = 1;
//            connectEnvoi();
//            send(buffer);
//        } else
        send(buffer);
    }

    /**
     * Transforme les données disponibles sur la socket en image suivant le
     * découpage des lignes.
     *
     * @return la manière de sortir (<code> <0 </code> si erreur de connexion).
     */
    private long analyseJpeg() {
        byte[] readBuffer = new byte[1024 * 8];
        byte[] buffer;
        byte[] tempBuffer;
        byte[] imageBuffer;

        long end;

        int header = -1;
        int read = 0;
        int position;
        int eof;

        long initTime;
        long timeTransfert = 0;

        try {
            //recherche du premier index du JPEG
            while (header < 0) {
                initTime = System.nanoTime();
                read = inputStream.read(readBuffer);
                timeTransfert += (System.nanoTime() - initTime);
                if (read < 0) {
                    return timeTransfert;
                }

                header = getSOI(readBuffer, 0);
            }

            buffer = new byte[read - header];
            System.arraycopy(readBuffer, header, buffer, 0, read - header);

            while (header >= 0) {
                eof = getEOI(buffer, header);

                while (eof < 0) {
                    initTime = System.nanoTime();
                    read = inputStream.read(readBuffer);
                    timeTransfert += (System.nanoTime() - initTime);

                    int length = buffer.length;
                    tempBuffer = buffer;
                    buffer = new byte[length + read];
                    System.arraycopy(tempBuffer, 0, buffer, 0, length);
                    System.arraycopy(readBuffer, 0, buffer, length, read);
                    eof = getEOI(buffer, header);
                }

                imageBuffer = new byte[eof + 1];
                System.arraycopy(buffer, 0, imageBuffer, 0, eof + 1);

                if (eof + 1 < buffer.length) {
                    position = buffer[eof + 1];
                } else {
                    initTime = System.nanoTime();
                    position = inputStream.read();
                    timeTransfert += (System.nanoTime() - initTime);
                }

                if (position < 0) {
                    return timeTransfert;//2
                }
                if (remoteTemp[position] != null) {
                    remoteTemp[position].flush();
                }
                remoteTemp[position] = Toolkit.getDefaultToolkit().createImage(imageBuffer);
                tracker.addImage(remoteTemp[position], 0);

                try {
                    tracker.waitForID(0);
                } catch (InterruptedException e) {
                    CommonLogger.error(e);
                }

                if (tracker.getErrorsID(0) == null) {
                    if (position > indexMax) {
                        setPosMax(position);
                    } else {
                        int lineHeight = remoteTemp[position].getHeight(null);
                        if (lineHeight != remoteLineHeight) {
                            remoteLineHeight = lineHeight;
                            remoteWidth = remoteTemp[position].getWidth(null);
                            updateImage();
                        }
                    }
                } else {
                    CommonLogger.warning("err in track=" + tracker.getErrorsID(0));
                    CommonLogger.warning("remoteTemp[position]:" + remoteTemp[position]);
                    Object[] errors = tracker.getErrorsAny();
                    for (Object object : errors) {
                        CommonLogger.warning("err object in track: " + object);
                    }
                }
                tracker.removeImage(remoteTemp[position]);

                tempBuffer = new byte[buffer.length - eof - 1];//+1 pour byte bPos
                System.arraycopy(buffer, eof + 1, tempBuffer, 0, tempBuffer.length);
                buffer = tempBuffer;

                header = getSOI(buffer, 0);

                window.repaint();

                if (header >= 0) {
                    tempBuffer = new byte[buffer.length - header];//+1 pour byte bPos
                    System.arraycopy(buffer, header, tempBuffer, 0, tempBuffer.length);
                    buffer = tempBuffer;
                }
            }

            end = timeTransfert;
        } catch (SocketTimeoutException e) {
            CommonLogger.debug("ScreenWindow.analyseJpeg timeout: " + timeout);
            end = -1;
        } catch (IOException e) {
            CommonLogger.error("IO error in ScreenWindow.analyseJpeg: " + e.getMessage());
            end = -2;
        } catch (Exception e) {
            //erreur non standard comme ArrayIndexOutOfBoundsException; au cas où
            CommonLogger.error("Error in ScreenWindow.analyseJpeg: " + e.getMessage());
            end = -3;
        }

        return end;
    }

    /**
     * Retourne l'index où se trouve le début du JPEG. L'image JPEG commence par
     * le caractère 0xFFD8 (SOI : Start Of Image).
     *
     * @param buffer les données d'image.
     * @param offset l'indice de départ de recherche dans les données.
     * @return l'index de début du JPEG, ou -1 si pas de marque SOI.
     */
    private int getSOI(byte[] buffer, int offset) {
        byte one = (byte) 0xff;
        byte two = (byte) 0xD8;

        for (int i = offset; i < buffer.length - 1; i++) {
            if (buffer[i] == one && buffer[i + 1] == two) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Retourne l'index où se trouve la fin du JPEG. L'image JPEG termine par le
     * caractère 0xFFD9 (EOI : End Of Image).
     *
     * @param buffer les données d'image.
     * @param offset l'indice de départ de recherche dans les données.
     * @return l'index de fin du JPEG, ou -1 si pas de marque EOI.
     */
    private int getEOI(byte[] buffer, int offset) {
        byte one = (byte) 0xff;
        byte two = (byte) 0xD9;

        for (int i = offset; i < buffer.length - 1; i++) {
            if (buffer[i] == one && buffer[i + 1] == two) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Traitement pour la réception de l'écran.
     */
    @Override
    public void run() {
        int end = 0;
        long initTime = System.nanoTime();
        long currentTime;
        double transfertTime = 0;
        double analyseTime = 0;

        long analyse;
        int passe = 0;
        int error = 0;
        int errorConnected = 0;

        while (run) {
            if (!isReceptionConnected()) {
                try {
                    if (connectReception()) {
                        errorConnected = 0;
                    } else {
                        //gestion timeout
                        errorConnected++;
                        if (errorConnected > 32) {
                            run = false;
                            end = -1;
                            CommonLogger.error("ScreenWindow error limit in connect");
                        } else {
                            Utilities.waitInMillisecond(100);
                        }
                    }
                } catch (Exception e) {
                    CommonLogger.error("ScreenWindow error in connect");
                    CommonLogger.error(e);
                    run = false;
                }
            } else {
                currentTime = System.nanoTime();
                analyse = analyseJpeg();
                analyseTime += (System.nanoTime() - currentTime);
                if (analyse < -1) {
                    error++;
                    if (error > 32) {
                        run = false;
                        end = -2;
                        CommonLogger.error("ScreenWindow error limit in analyseJpeg");
                    }
                } else {
                    if (analyse < 0) {
                        // timeout atteint
                        analyse = timeout;
                    }

                    transfertTime += analyse;
                    passe++;
                    error = 0;
                    errorConnected = 0;
                }
            }
        }

        double global = System.nanoTime() - initTime;
        long secondToNano = 1000000;
        CommonLogger.info("ScreenWindow global: " + global / secondToNano
                + " analyse: " + analyseTime / secondToNano
                + " transfert: " + transfertTime / secondToNano
                + " passe: " + passe);
        if (passe > 0) {
            CommonLogger.info("ScreenWindow average global: "
                    + global / secondToNano / passe
                    + " analyse: " + analyseTime / secondToNano / passe
                    + " transfert: " + transfertTime / secondToNano / passe);
        }

//        if(isReceptionConnected()) {
        disconnectReception();
//        }

        showWindow(false);
        close();
        fireProcessEnded(end);
    }

    /**
     * Dessine l'image redimensionnée sur un Graphics.
     *
     * @param g le Graphics ou sera dessiner l'image.
     */
    private void paintImage(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

//        if(indexMax >= 0) {
        createScaledImage();
        graphics2D.drawImage(scaleImage, xOffset, yOffset, null);
//        }
    }

    /**
     * Crée l'image distante redimensionnée à la taille de la fenêtre en gardant
     * les proportions et la découpe en ligne.
     */
    private void createScaledImage() {
        if (indexMax < 0 || ratio == 0) {
            return;
        }

        Graphics2D graphics2D = remoteImage.createGraphics();
//        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        for (int i = 0; i <= indexMax; i++) {
            if (remoteTemp[i] != null) {
                int dyPos = remoteLineHeight * i;
                graphics2D.drawImage(remoteTemp[i],
                        0, dyPos, remoteWidth, remoteLineHeight, null);
            }
        }
        graphics2D.dispose();

        AffineTransform transform = AffineTransform.getScaleInstance(ratio, ratio);
        AffineTransformOp transformOp = new AffineTransformOp(transform,
                AffineTransformOp.TYPE_BILINEAR);
        transformOp.filter(remoteImage, scaleImage);

        if (name != null) {
            graphics2D = scaleImage.createGraphics();
            graphics2D.setFont(new Font("Arial", Font.BOLD, 13));
            graphics2D.setColor(Color.ORANGE);
            graphics2D.fillRect(0, 0, 185, 18);//185 = 26 lettres alphabet
            graphics2D.setColor(Color.DARK_GRAY);
            graphics2D.drawString(name, 2, 14);
            graphics2D.dispose();
        }
    }
}
