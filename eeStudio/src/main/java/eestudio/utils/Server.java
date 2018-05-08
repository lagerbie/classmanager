package eestudio.utils;

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
 */
@Deprecated
public abstract class Server implements Runnable {
    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    /**
     * Numéro de port du serveur
     */
    private int port;
    /**
     * ServerSocket pour la réception des ordres
     */
    private ServerSocket serverSocket;
    /**
     * Temps d'attente maximum (0 = indéfiniment)
     */
    private int timeout = 0;

    /**
     * Thread principale
     */
    private Thread thread;
    /**
     * Priorité de la thread
     */
    private int priority = Thread.NORM_PRIORITY;
    /**
     * Etat de la thread
     */
    private boolean run = false;

    /**
     * Initialisation.
     *
     * @param port le numéro de port.
     *
     * @since version 0.95
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Modifie le temps d'attente de connection.
     *
     * @param timeout le temps d'attente.
     *
     * @since version 0.95
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
        if (serverSocket != null) {
            try {
                serverSocket.setSoTimeout(timeout);
            } catch (SocketException e) {
                LOGGER.error("", e);
            }
        }
    }

    /**
     * Modifie la priorité de la thread.
     *
     * @param priority la priorité de la thread.
     *
     * @since version 0.95
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
     *
     * @since version 0.95
     */
    public boolean start() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(timeout);
        } catch (IOException e) {
            LOGGER.error("", e);
            return false;
        }

        run = true;
        thread = new Thread(this);
        thread.setPriority(priority);
        thread.start();
        return true;
    }

    /**
     * Arrête le serveur.
     *
     * @since version 0.95 - version 0.99
     */
    public void stop() {
        run = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
        serverSocket = null;
    }

    /**
     * Retourne si la thread est en tratement.
     *
     * @return si la thread est en tratement.
     *
     * @since version 0.95
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
     * @since version 0.95
     */
    protected abstract void process(Socket socket) throws IOException;

    /**
     * Traitement des ordres.
     *
     * @since version 0.95 - version 0.98
     */
    @Override
    public void run() {
        Socket socket = null;

        try {
            while (run) {
                //attente de la connection
                LOGGER.debug("wait connection on " + this.getClass());
                socket = serverSocket.accept();
                LOGGER.debug("connection established on " + this.getClass());

                process(socket);
                //fermeture de la connection et reboucle sur une écoute du
                //port (si la connection n'est pas fermée, utilisation
                //inutile du cpu).
                socket.close();
                socket = null;
            }//end while
        } catch (IOException e) {
            LOGGER.error("", e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }//end catch
            }

            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    serverSocket = null;
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }

            if (run) {
                LOGGER.warn("redémarrage de Server: " + this.getClass());
                start();
            }
        }
    }

}
