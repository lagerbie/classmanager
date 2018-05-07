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
package thot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gestion d'un serveur socket.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public abstract class Server implements Runnable {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    /**
     * Numéro de port du serveur.
     */
    private int port;
    /**
     * ServerSocket pour la réception des ordres.
     */
    private ServerSocket serverSocket;
    /**
     * Temps d'attente maximum (0 = indéfiniment).
     */
    private int timeout = 0;

    /**
     * Thread principale.
     */
    private Thread thread;
    /**
     * Priorité de la thread.
     */
    private int priority = Thread.NORM_PRIORITY;
    /**
     * Etat de la thread.
     */
    private boolean run = false;

    /**
     * Initialisation.
     *
     * @param port le numéro de port.
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Modifie le temps d'attente de connection.
     *
     * @param timeout le temps d'attente.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
        if (serverSocket != null) {
            try {
                serverSocket.setSoTimeout(timeout);
            } catch (SocketException e) {
                LOGGER.error("Impossible de modifier le timeout avec la valeur {}", e, timeout);
            }
        }
    }

    /**
     * Modifie la priorité de la thread.
     *
     * @param priority la priorité de la thread.
     */
    public void setPriority(int priority) {
        this.priority = priority;
        if (thread != null) {
            thread.setPriority(priority);
        }
    }

    /**
     * Démarage du serveur.
     *
     * @return si le SeverSocket est initialisé.
     */
    public boolean start() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(timeout);
        } catch (IOException e) {
            LOGGER.error("Impossible de démarrer le serveur sur le port {}", e, port);
            return false;
        }

        run = true;
        thread = new Thread(this, this.getClass().getName());
        thread.setPriority(priority);
        thread.start();
        return true;
    }

    /**
     * Arrête le serveur.
     */
    public void stop() {
        run = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
                LOGGER.error("Impossible de d'arrêter le serveur sur le port {}", e, port);
            }
        }
    }

    /**
     * Retourne si la thread est en tratement.
     *
     * @return si la thread est en tratement.
     */
    public boolean isRun() {
        return run;
    }

    /**
     * Traitement spécifique des données sur la socket connecté.
     *
     * @param socket la connexion établie.
     *
     * @throws IOException
     */
    protected abstract void process(Socket socket) throws IOException;

    @Override
    public void run() {


        while (run) {
            //attente de la connection
            LOGGER.debug("wait connection on {}", this.getClass());
            try (Socket socket = serverSocket.accept()) {
                LOGGER.debug("connection established on {}", this.getClass());

                process(socket);

            } catch (IOException e) {
                LOGGER.error("Erreur lors du traitement de la requête", e);
            } finally {
                if (run) {
                    stop();
                    LOGGER.warn("redémarrage de Server: " + this.getClass());
                    start();
                }
            }
        }

    }
}
