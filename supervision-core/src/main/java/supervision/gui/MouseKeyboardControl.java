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
import java.awt.Robot;

import java.io.InputStream;
import java.io.IOException;
import java.net.Socket;

import supervision.CommonLogger;
import supervision.Server;
import supervision.Constants;

/**
 * Classe recevant les évènement de la souris et du clavier d'une prise en main.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class MouseKeyboardControl extends Server {

    /**
     * Robot pour appliquer les commandes de la souris et du clavier.
     */
    private Robot robot;

    /**
     * Initilisation par défaut.
     *
     * @param port le port pour le control du clavier et de la souris.
     */
    public MouseKeyboardControl(int port) {
        super(port);
        setTimeout(Constants.TIME_MAX_FOR_CONNEXION);

        try {
            robot = new Robot();
        } catch (AWTException e) {
            CommonLogger.error(e);
        }
    }

    /**
     * Déplace la souris à la position donnée.
     *
     * @param x la position horizontale.
     * @param y la position verticale.
     */
    private void mouseMoved(int x, int y) {
        robot.mouseMove(x, y);
    }

    /**
     * Enfonce un bouton de la souris.
     *
     * @param button le bouton de la souris.
     */
    private void mousePressed(int button) {
        robot.mousePress(button);
    }

    /**
     * Relache un bouton de la souris.
     *
     * @param button le bouton de la souris.
     */
    private void mouseReleased(int button) {
        robot.mouseRelease(button);
    }

    /**
     * Enfonce une touche du clavier.
     *
     * @param key la touche du clavier.
     */
    private void keyPressed(int key) {
        robot.keyPress(key);
    }

    /**
     * relache une touche du clavier.
     *
     * @param key la touche du clavier.
     */
    private void keyReleased(int key) {
        robot.keyRelease(key);
    }

    /**
     * Traitement des commandes.
     *
     * @param socket la socket de communication.
     * @throws IOException
     */
    @Override
    protected void process(Socket socket) throws IOException {
        socket.setTcpNoDelay(true);

        byte[] buffer = new byte[2048];
        InputStream inputStream = socket.getInputStream();

        int key;
        int read;
        int offset;
        int function;

        while (isRun()) {
            read = inputStream.read(buffer, 0, buffer.length);
            offset = 0;

            while (offset < read) {
                function = buffer[offset];
                switch (function) {
                    case Constants.MOUSE_MOVED:
                        int x = buffer[offset + 1] * 16 + buffer[offset + 2];
                        int y = buffer[offset + 3] * 16 + buffer[offset + 4];
                        mouseMoved(x, y);
                        offset += 5;
                        break;
                    case Constants.MOUSE_PRESSED:
                        mousePressed(buffer[offset + 1]);
                        offset += 2;
                        break;
                    case Constants.MOUSE_RELEASED:
                        mouseReleased(buffer[offset + 1]);
                        offset += 2;
                        break;
                    case Constants.KEY_PRESSED:
                        key = buffer[offset + 1] * 16 + buffer[offset + 2];
                        if (key > 0) {
                            keyPressed(key);
                        }
                        offset += 3;
                        break;
                    case Constants.KEY_RELEASED:
                        key = buffer[offset + 1] * 16 + buffer[offset + 2];
                        if (key > 0) {
                            keyReleased(key);
                        }
                        offset += 3;
                        break;
                    case Constants.CLOSE:
                        //fermer la socket
                        stop();
                        offset = read;
                        break;
                }
            }
        }
    }
}
