package thot;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serveur pour lancer des applications.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class ApplicationLauncherServer implements Runnable {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLauncherServer.class);

    /**
     * Numéro de port part défaut.
     */
    private static final int DEFAULT_PORT = 7207;
    /**
     * Numéro de port du serveur.
     */
    private int port;
    /**
     * ServerSocket pour la réception des ordres.
     */
    private ServerSocket serverSocket;
    /**
     * Flux pour renvoyer la sortie de l'application.
     */
    private DataOutputStream outputStream;
    /**
     * Etat de la thread.
     */
    private boolean run = false;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOGGER.info("version: 1.8.4");
        ApplicationLauncherServer launchServer = new ApplicationLauncherServer(DEFAULT_PORT);
        launchServer.start();
    }

    /**
     * Initialisation.
     *
     * @param port le numéro de port.
     */
    private ApplicationLauncherServer(int port) {
        this.port = port;
    }

    /**
     * Démarage du serveur.
     */
    private void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            LOGGER.error("Impossible d'ouvrir une connexion réseau sur le port {}", e, port);
            return;
        }

        run = true;
        Thread thread = new Thread(this, this.getClass().getName());
        thread.start();
    }

    /**
     * Traitement des ordres.
     */
    @Override
    public void run() {
        String xml = null;
        DataInputStream inputStream;
        while (run) {
            //attente de la connection
            try (Socket socket = serverSocket.accept()) {
                inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());

                xml = inputStream.readUTF();

                LOGGER.info("Lancement de l'application {}", xml);

                int exit = executeCommand("/bin/sh", "-c", xml);
                outputStream.flush();

                LOGGER.info("L'application {} a été exécutée (code de sortie {})", xml, exit);
            } catch (IOException e) {
                LOGGER.error("Impossible de traiter la requête {}", e, xml);
            }
        }
    }

    /**
     * Exécute la commande reçue.
     *
     * @param command la commande.
     *
     * @return la valeur de fin du processus.
     */
    private int executeCommand(String... command) {
        int end = -1;
        Runtime runtime = Runtime.getRuntime();
        Process process = null;

        try {
            process = runtime.exec(command);
            Thread outputThread = createReadThread(process.getInputStream(), command[0]);
            Thread errorThread = createReadThread(process.getErrorStream(), command[0]);
            outputThread.start();
            errorThread.start();

            try {
                end = process.waitFor();
            } catch (InterruptedException e) {
                LOGGER.error("Le processus {} a été interrompu", e, command[0]);
            }
        } catch (IOException e) {
            LOGGER.error("Erreur lors du traitement du processus {}", e, command[0]);
        }

        if (process != null) {
            process.destroy();
        }

        return end;
    }

    /**
     * Créer une thread de traitement d'un flux.
     *
     * @param inputStream le flux à gérer.
     *
     * @return la thread de gestion du flux.
     */
    private Thread createReadThread(final InputStream inputStream, final String name) {
        return new Thread(name) {
            @Override
            public void run() {
                byte[] data = new byte[1024];
                try {
                    int cnt = inputStream.read(data);
                    while (cnt > 0) {
                        fireNewData(new String(data, 0, cnt));
                        cnt = inputStream.read(data);
                    }
                } catch (IOException e) {
                    LOGGER.error("Erreur de lecture sur le processus {}", e, name);
                }
            }
        };
    }

    /**
     * Evènement de nouvelles données lors de l'exécution du procesus.
     *
     * @param data les données.
     */
    private void fireNewData(String data) {
        try {
            if (data.length() > 32000) {
                outputStream.writeUTF(data.substring(0, 32000));
            } else {
                outputStream.writeUTF(data);
            }
            outputStream.flush();
        } catch (Exception e) {
            LOGGER.error("Erreur d'écriture", e);
        }
    }

}
