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
package thot.supervision.screen;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.CRC32;

import javax.imageio.ImageIO;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.gui.GuiUtilities;
import thot.utils.Constants;
import thot.utils.ProgressThread;
import thot.utils.Utilities;

/**
 * Classe d'envoi d'écran.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class CaptureScreen extends ProgressThread implements Runnable {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CaptureScreen.class);

    /**
     * Gestion de la prise en main.
     */
    private boolean control = false;
    /**
     * Gestionnaire de la prise en main.
     */
    private MouseKeyboardControl mouseKeyboardControl;
    /**
     * Image du curseur.
     */
    private Image cursorImage;

    /**
     * Quality de l'image JPEG envoyée.
     */
    private int quality = 80;
    /**
     * Nombre d'images par seconde.
     */
    private double fps = 20;
    /**
     * Nombre de lignes de la capture d'écran.
     */
    private int nbLines = 32;

    /**
     * Robot pour créer la capture d'écran et la gestion de la souris en mode annoter.
     */
    private Robot robot;

    /**
     * Serveurs pour l'envoi des données.
     */
    private ServerSocket[] serverSockets;
    /**
     * Sockets pour l'envoi des données.
     */
    private Socket[] sockets;
    /**
     * OuputStream pour l'envoi des données.
     */
    private OutputStream[] outputStreams;
    /**
     * Thread de transfert des données.
     */
    private Thread thread;
    /**
     * Etat de fonctionnement.
     */
    private boolean run = false;

    /**
     * Image de l'écran.
     */
    private BufferedImage screenImage;

    /**
     * Initialisation avec le controle du clavier et de la souris.
     *
     * @param portControl le port pour le control du clavier et de la souris.
     */
    public CaptureScreen(int portControl) {
        if (portControl > 0) {
            mouseKeyboardControl = new MouseKeyboardControl(portControl);
        }

        cursorImage = GuiUtilities.getImage("cursor");

        try {
            robot = new Robot();
        } catch (AWTException e) {
            LOGGER.error("", e);
            System.exit(-1);
        }
    }

    /**
     * Initialisation des sockets.
     *
     * @param portBase le port de base pour les différentes diffusions.
     * @param nbClient nombre de clients pour recevoir cet écran
     */
    private void initSockets(int portBase, int nbClient) {
        sockets = new Socket[nbClient];
        serverSockets = new ServerSocket[nbClient];
        outputStreams = new OutputStream[nbClient];

        for (int i = 0; i < nbClient; i++) {
            try {
                int port = portBase + i;
                LOGGER.debug("CaptureScreen ecoute port: {}", port);

                serverSockets[i] = new ServerSocket(port);
                serverSockets[i].setSoTimeout(Constants.TIME_MAX_FOR_CONNEXION);
                sockets[i] = serverSockets[i].accept();
                sockets[i].setTcpNoDelay(true);
                outputStreams[i] = new BufferedOutputStream(sockets[i].getOutputStream());
                LOGGER.info("CaptureScreen connecté à l'adresse {}:{} ", sockets[i].getRemoteSocketAddress(), port);
            } catch (Exception e) {
                LOGGER.error("CaptureScreen Exception au client: {}", e, i);
            }
        }
    }

    /**
     * Fermetures des sockets de communication.
     */
    private void closeSocket() {
        if (serverSockets == null) {
            return;
        }

        for (int i = 0; i < sockets.length; i++) {
            if (sockets[i] != null) {
                try {
                    sockets[i].close();
                } catch (IOException | NullPointerException e) {
                    LOGGER.error("", e);
                }
                outputStreams[i] = null;
                sockets[i] = null;
            }
            if (serverSockets[i] != null) {
                try {
                    serverSockets[i].close();
                } catch (IOException | NullPointerException e) {
                    LOGGER.error("", e);
                }
                serverSockets[i] = null;
            }
        }
        sockets = null;
        serverSockets = null;
        outputStreams = null;
    }

    /**
     * Modifie la qualité de l'image.
     *
     * @param quality la qualité de l'image.
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }

    /**
     * Modifie le nombre d'images par seconde.
     *
     * @param fps le nombre d'images par seconde.
     */
    public void setFPS(double fps) {
        this.fps = fps;
    }

    /**
     * Modifie le nombre de lignes du découpage.
     *
     * @param nbLines le nombre de lignes du découpage.
     */
    public void setNbLines(int nbLines) {
        this.nbLines = nbLines;
    }

    /**
     * Démarre l'envoi d'écran.
     *
     * @param portBase le premier numéro de port.
     * @param nbClient le nombre de clients.
     * @param control la prise en main ?
     */
    public void start(int portBase, int nbClient, boolean control) {
        closeSocket();//au cas ou
        this.control = control;
        initSockets(portBase, nbClient);
        if (this.control && mouseKeyboardControl != null) {
            mouseKeyboardControl.start();
        }

        //calcul du nombre de lignes pour avoir une hauteur constante
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        while (screenDimension.height % nbLines != 0) {
            nbLines--;
        }

        LOGGER.info("CaptureScreen start with fps: {} quality: {} nbLines: {}", fps, quality, nbLines);
        run = true;
        thread = new Thread(this, this.getClass().getName());
        thread.start();
    }

    /**
     * Arrête le processus.
     */
    public void stop() {
        run = false;
    }

    /**
     * Retourne si le transfert est en mode actif.
     *
     * @return si le transfert est en mode actif.
     */
    public boolean isRun() {
        return run;
    }

    @Override
    public void run() {
        int end = 0;
        long initTime = System.nanoTime();
        long secondToNano = 1000000;
        int currentQuality = quality;
        double currentFPS = fps;

        int nbClient = sockets.length;

        long[] checksumSave = new long[nbLines];
        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        int hauteurLigne = screenDimension.height / nbLines;
        long waitTime = (long) (1000 / fps * secondToNano);

        BufferedImage bufferedImage = null;
        PointerInfo pointer;
        Point location;

        Rectangle rectangle = new Rectangle(screenDimension);

        screenImage = robot.createScreenCapture(rectangle);
        Graphics graphics = screenImage.getGraphics();

        JPEGImageWriteParam param = new JPEGImageWriteParam(null);
        param.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(quality / 100.0f);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data;
        boolean converted;

        long checksum;
        CRC32 checksumEngine = new CRC32();

        long passTime;
        long duration;

        long currentTime;
        double capture = 0;
        double encode = 0;
        double calcul = 0;
        double transfert = 0;
        double imageBytes = 0;
        long passe = 0;

        fireProcessBegin(false);

        int line;
        int client;

        while (run) {
            passTime = System.nanoTime();
            screenImage = robot.createScreenCapture(rectangle);
            capture += (System.nanoTime() - passTime);

            currentTime = System.nanoTime();
            pointer = MouseInfo.getPointerInfo();
            location = pointer.getLocation();
            graphics = screenImage.getGraphics();
            graphics.drawImage(cursorImage, location.x, location.y, null);
            calcul += (System.nanoTime() - currentTime);

            line = 0;
            while (line < nbLines) {
                currentTime = System.nanoTime();
                bufferedImage = screenImage.getSubimage(
                        0, line * hauteurLigne, screenDimension.width, hauteurLigne);
                calcul += (System.nanoTime() - currentTime);

                if (currentQuality != quality) {
                    param.setCompressionQuality(quality / 100.0f);
                    currentQuality = quality;
                }

                if (currentFPS != fps) {
                    waitTime = (long) (1000 / fps * secondToNano);
                    currentFPS = fps;
                }

                try {
                    currentTime = System.nanoTime();
                    converted = ImageIO.write(bufferedImage, "jpeg", out);
                    encode += (System.nanoTime() - currentTime);
                    if (!converted) {
                        LOGGER.warn("CaptureScreen ImageIO.write return false");
                        continue;
                    }
//                    bufferedImage.flush();
                } catch (Exception e) {
                    LOGGER.error("", e);
                    continue;
                }

                currentTime = System.nanoTime();
                data = out.toByteArray();
                out.reset();

                checksumEngine.update(data, 0, data.length);
                checksum = checksumEngine.getValue();
                checksumEngine.reset();
                calcul += (System.nanoTime() - currentTime);

                imageBytes += data.length;

                if (checksumSave[line] != checksum) {
                    checksumSave[line] = checksum;

                    client = 0;
                    while (client < nbClient) {
                        if (outputStreams[client] == null) {
                            if (run) {
                                end = -2;
                            }
                            run = false;
                            line = nbLines + 1;
                            break;
                        }
                        try {
                            currentTime = System.nanoTime();
                            outputStreams[client].write(data);
                            outputStreams[client].write(line);
                            outputStreams[client].flush();
                            transfert += (System.nanoTime() - currentTime);
                        } catch (IOException e) {
                            LOGGER.error("CaptureScreen Exception for client: {} ip: {}", e, client,
                                    sockets[client].getInetAddress());
                            //arrêt boucle d'envoi pour clients
                            client = nbClient;
                            //arrêt boucle ligne
                            line = nbLines;

                            if (run) {
                                end = -1;
                            }
                            //arrêt envoi d'écran
                            run = false;
                        }
                        client++;
                    }//fin boucle client
                }//fin si envoi
                line++;
            }//fin boucle ligne

            duration = System.nanoTime() - passTime;
            Utilities.waitInNanosecond(Math.max(0, waitTime - duration));
            passe++;

            if (run && control && mouseKeyboardControl != null) {
                run = mouseKeyboardControl.isRun();
            }

//            if(screenImage != null)
//                screenImage.flush();
//            if(bufferedImage != null)
//                bufferedImage.flush();
//            if(graphics != null)
//                graphics.dispose();
        }

        double global = System.nanoTime() - initTime;
        LOGGER.info("global: " + global / secondToNano
                + " capture: " + capture / secondToNano
                + " encode: " + encode / secondToNano
                + " calcul: " + calcul / secondToNano
                + " transfert: " + transfert / secondToNano
                + " imageBytes: " + imageBytes
                + " passe: " + passe);
        if (passe > 0) {
            LOGGER.info("average global: " + global / secondToNano / passe
                    + " capture: " + capture / secondToNano / passe
                    + " encode: " + encode / secondToNano / passe
                    + " calcul: " + calcul / secondToNano / passe
                    + " transfert: " + transfert / secondToNano / passe
                    + " imageBytes per image: " + imageBytes / passe
                    + " imageBytes per line: " + imageBytes / passe / nbLines);
        }

        closeSocket();

        if (control && mouseKeyboardControl != null) {
            mouseKeyboardControl.stop();
        }

        if (screenImage != null) {
            screenImage.flush();
        }
        screenImage = null;
        if (bufferedImage != null) {
            bufferedImage.flush();
        }
        if (graphics != null) {
            graphics.dispose();
        }

        fireProcessEnded(end);
    }
}
