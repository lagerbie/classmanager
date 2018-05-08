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
package thot.supervision;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.swing.*;

/**
 * Classe permettant d'afficher un écran d'un autre poste.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class ScreenWindow implements Runnable {

    /**
     * Vérification si la plateforme est Linux.
     */
    private static final boolean LINUX_PLATFORM = System.getProperty("os.name").toLowerCase().contains("linux");
    /**
     * Nom de l'envoyeur.
     */
    private String nom;
    /**
     * Adresse IP de l'envoyeur.
     */
    private String addressIP;
    /**
     * Port sur lequel l'écran est envoyé.
     */
    private int receptionPort;

    /**
     * Thread principale.
     */
    private Thread thread;
    /**
     * Etat de sortie.
     */
    private boolean run = false;
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
     * Notification pour initialiser le focus.
     */
    private boolean initFocus = false;

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
     * Offset pour centrer l'image.
     */
    private int xOffset = 0;
    /**
     * Offset pour centrer l'image.
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

    /**
     * Robot pour consummer la touche ALT sous Linux.
     */
    private Robot robot = null;
    private MediaTracker tracker;

    /**
     * Panel pour dessiner l'image.
     */
    private JPanel backgroundPanel;
    /**
     * Frame pour intercepter les évènements clavier sous Linux.
     */
    private JDialog dialogLinux;
    /**
     * Sauvegarde de l'état plein écran.
     */
    private boolean fullscreen = false;

    /**
     * Initialisation.
     */
    public ScreenWindow() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            CommonLogger.error(e);
            System.exit(-1);
        }

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                fireKeyPressed(e.getKeyCode());

                e.consume();//consomme l'évènement
                //la touche ALT n'est pas consommé sur Linux
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_ALT) {
                    robot.keyRelease(code);
                }
            }
        };

        backgroundPanel = new JPanel() {
            private static final long serialVersionUID = 19000L;

            @Override
            public void paintComponent(Graphics g) {
                paintImage(g);
            }
        };
        backgroundPanel.setBackground(Color.BLACK);

        if (LINUX_PLATFORM) {
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
        window.setCursor(null);

        tracker = new MediaTracker(window);

        window.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1 && SwingUtilities.isLeftMouseButton(e)) {
                    fireMouseClicked();
                }
            }
        });

        window.addKeyListener(keyAdapter);
    }

    /**
     * Modifie s'il faut initialiser le focus quand l'on démarre la thread. Par défaut {@code initKeyboardFocus} est à
     * {@code false}.
     *
     * @param initFocus l'état pour initialiser le focus.
     */
    public void setInitFocus(boolean initFocus) {
        this.initFocus = initFocus;
    }

    /**
     * Effectue automatiquement un click droit de la souris. Permet d'avoir le focus clavier sur la JDialog sous
     * Windows.
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
     *
     * @param addressIP l'adresse IP de celui qui envoi l'écran.
     * @param port le port de communication utilisé pour les données de l'écran.
     * @param nbLines le nombre de lignes.
     * @param x la position horizontale de la fenêtre.
     * @param y la position verticale de la fenêtre.
     * @param width la largeur de la fenêtre.
     * @param height la hauteur de la fenêtre.
     * @param nom le nom du diffuseur.
     */
    public void start(String addressIP, int port, int nbLines, String nom, final int x, final int y, final int width,
            final int height) {
        this.addressIP = addressIP;
        this.receptionPort = port;
        this.nom = nom;
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

        run = true;
        thread = new Thread(this, this.getClass().getName());
        thread.start();
        showWindow(true);

        if (initFocus) {
            Thread focusThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        CommonLogger.error(e);
                    }
                    performClick(x + width / 2, y + height / 2);
                }
            }, "focus");
            focusThread.start();
        }
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
     * Montre ou cache la fenêtre. Remplace la fonction {@code setVisible(visible)}.
     *
     * @param visible la visibilité de la fenêtre.
     */
    private void showWindow(boolean visible) {
        if (visible) {
            if (LINUX_PLATFORM) {
                dialogLinux.setVisible(true);
                dialogLinux.requestFocus();
                dialogLinux.toFront();//add v1.70 à tester
            }

            window.setVisible(true);
            window.requestFocus();
            window.toFront();
            window.repaint();
        } else {
            if (LINUX_PLATFORM) {
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
    private void dispose() {
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

        if (LINUX_PLATFORM) {
            dialogLinux.dispose();
        }

        window.dispose();
    }

    /**
     * Bascule la fenêtre en plein écran ou en taille normale. Permet de mettre la fenêtre en plein écran ou de la
     * redimensionner à la taille précisée dans le {@code start()}.
     *
     * @param fullscreen {@code true} pour passer en mode plein écran.
     */
    private void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        if (fullscreen) {
            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            window.setLocation(0, 0);
            window.setSize(screen);
            window.toFront();
        } else {
            if (LINUX_PLATFORM) {
                window.dispose();
            }
            window.setLocation(x, y);
            window.setSize(width, height);
//            window.toFront();
            if (LINUX_PLATFORM) {
                showWindow(true);
            }
        }

        updateImage();
    }

    /**
     * Arrête la réception de l'écran avec une commande spécifique.
     */
    private void closeWithCommand() {
        run = false;
        showWindow(false);
        closeCommand();
    }

    /**
     * Commande spécifique lors de l'arrêt de la réception.
     */
    protected void closeCommand() {
    }

    /**
     * Arrête la réception de l'écran.
     */
    public void close() {
        run = false;
        showWindow(false);
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
            remoteImage = new BufferedImage((int) remoteScreenWidth, (int) remoteScreenHeight,
                    BufferedImage.TYPE_INT_RGB);
        }

        int scaleWidth = (int) (remoteScreenWidth * ratio);
        int scaleHeight = (int) (remoteScreenHeight * ratio);
        if (scaleHeight != scaleImage.getHeight() || scaleWidth != scaleImage.getWidth()) {
            scaleImage.flush();
            scaleImage = new BufferedImage(scaleWidth, scaleHeight, BufferedImage.TYPE_INT_RGB);
        }

        window.repaint(0, 0, window.getWidth(), window.getHeight());
    }

    /**
     * Modifie l'index maximum pour les lignes utilisées par le découpage de l'écran envoyé.
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
    private boolean connectReception() throws IOException {
        boolean connected = false;
        CommonLogger.info("connection reception to addressIP:" + addressIP + " port:" + receptionPort);
        InetSocketAddress socketAddress = new InetSocketAddress(addressIP, receptionPort);
        socketReception = new Socket();
        socketReception.setTcpNoDelay(true);

        try {
            socketReception.connect(socketAddress, timeout);
            inputStream = socketReception.getInputStream();
            CommonLogger.info("...reception etablished");
            connected = true;
        } catch (SocketTimeoutException e) {
            CommonLogger.error("Err connectReception dans ScreenWindow: " + e);
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
            } catch (IOException e) {
                CommonLogger.error("Err disconnectReception dans ScreenWindow: " + e);
                CommonLogger.error(e);
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
     * Fonction pour le double clic de la souris.
     */
    private void fireMouseClicked() {
        setFullscreen(!fullscreen);
    }

    /**
     * Envoi l'évènement de l'appui d'une touche du clavier.
     *
     * @param key la valeur de la touche.
     */
    private void fireKeyPressed(int key) {
        if (key == KeyEvent.VK_ESCAPE) {
            closeWithCommand();
        }
    }

    /**
     * Transforme les données disponibles sur la socket en image suivant le découpage des lignes.
     *
     * @return le temps de lecture des données sur le réseau ou l'erreur si < 0.
     */
    private long analyseJpeg() {
        byte[] readBuffer = new byte[1024 * 8];//old 8190
        byte[] buffer;
        byte[] tempBuffer;
        byte[] imageBuffer;

        long end;

        int header = -1;
        int read = 0;
        int position;

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
                int eof = getEOI(buffer, header);

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
                    return timeTransfert;
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
                    CommonLogger.error("err in track=" + tracker.getErrorsID(0));
                    Object[] erors = tracker.getErrorsAny();
                    for (Object object : erors) {
                        CommonLogger.error("err in track :" + object);
                    }
                }

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
            CommonLogger.error("ScreenWindow timeout: " + timeout);
            end = -1;
        } catch (IOException e) {
            CommonLogger.error("ScreenWindow IO error in AnalyseJpeg: " + e.getMessage());
            end = -2;
        } catch (Exception e) {
            //erreur non standard comme ArrayIndexOutOfBoundsException au cas où
            CommonLogger.error("ScreenWindow error in AnalyseJpeg: " + e.getMessage());
            end = -3;
        }

        return end;
    }

    /**
     * Retourne l'index où se trouve le début du JPEG. L'image JPEG commence par le caractère 0xFFD8 (SOI : Start Of
     * Image).
     *
     * @param buffer les données d'image.
     * @param offset l'indice de départ de recherche dans les données.
     *
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
     * Retourne l'index où se trouve la fin du JPEG. L'image JPEG termine par le caractère 0xFFD9 (EOI : End Of Image).
     *
     * @param buffer les données d'image.
     * @param offset l'indice de départ de recherche dans les données.
     *
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
        long initTime = System.nanoTime();
        long analyse;
        double timeTransfert = 0;

        int passe = 0;
        int error = 0;
        int errorConnected = 0;

        while (run) {
            if (!isReceptionConnected()) {
                try {
                    if (connectReception()) {
                        errorConnected = 0;
                    } else {
                        try {
                            Thread.sleep(timeout);
                        } catch (InterruptedException e) {
                            CommonLogger.error(e);
                        }
                        errorConnected++;
                        if (errorConnected > 32) {
                            run = false;
                        }
                    }
                } catch (IOException e) {
                    CommonLogger.error(e);
                    run = false;
                }
            } else {
                analyse = analyseJpeg();
                if (analyse < -1) {
                    error++;
                    if (error > 32)//4
                    {
                        run = false;
                    }
                } else {
                    if (analyse < 0) {
                        // timeout atteint
                        analyse = timeout;
                    }
                    error = 0;
                    errorConnected = 0;

                    timeTransfert += analyse;
                    passe++;
                }
            }
        }

        long secondToNano = 1000000;
        double timeGlobal = System.nanoTime() - initTime;
        CommonLogger.info("global: " + timeGlobal / secondToNano + " transfert: " + timeTransfert / secondToNano
                + " passe: " + passe);
        if (passe > 0) {
            CommonLogger.info("global: " + timeGlobal / secondToNano / passe + " transfert: "
                    + timeTransfert / secondToNano / passe);
        }

        if (isReceptionConnected()) {
            disconnectReception();
        }

        showWindow(false);
        dispose();
    }

    /**
     * Méthode paint.
     *
     * @param g
     */
    private void paintImage(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (indexMax >= 0) {
            try {
                createScaledImage();
            } catch (Exception e) {
                CommonLogger.error(e);
            }
            graphics2D.drawImage(scaleImage, xOffset, yOffset, null);
        }
    }

    /**
     * Crée l'image distante redimensionnée à la taille de la fenêtre en gardant les proportions et la découpe en
     * ligne.
     */
    private void createScaledImage() {
        if (indexMax < 0 || ratio == 0) {
            return;
        }

        Graphics2D graphics2D = remoteImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        for (int i = 0; i <= indexMax; i++) {
            if (remoteTemp[i] != null) {
                int dyPos = remoteLineHeight * i;
                graphics2D.drawImage(remoteTemp[i], 0, dyPos, remoteWidth, remoteLineHeight, null);
            }
        }
        graphics2D.dispose();

        AffineTransform transform = AffineTransform.getScaleInstance(ratio, ratio);
        AffineTransformOp transformOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        transformOp.filter(remoteImage, scaleImage);

        if (nom != null) {
            graphics2D = scaleImage.createGraphics();
            graphics2D.setFont(new Font("Arial", Font.BOLD, 13));
            graphics2D.setColor(Color.ORANGE);
            graphics2D.fillRect(0, 0, 185, 18);//185 = 26 lettres alphabet
            graphics2D.setColor(Color.DARK_GRAY);
            graphics2D.drawString(nom, 2, 14);
            graphics2D.dispose();
        }
    }
}
