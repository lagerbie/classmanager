package supervision;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Serveur pour lancer des applications.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class LaunchServer implements Runnable {

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
        printMessage("version: 1.90.00");
        LaunchServer launchServer = new LaunchServer(DEFAULT_PORT);
        launchServer.start();
    }

    /**
     * Initialisation.
     *
     * @param port le numéro de port.
     */
    public LaunchServer(int port) {
        this.port = port;
    }

    /**
     * Démarage du serveur.
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            printError(e);
            return;
        }

        run = true;
        Thread thread = new Thread(this, this.getClass().getName());
        thread.start();
    }

    /**
     * Arrête le serveur.
     */
    public void stop() {
        run = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            printError(e);
        }
    }

    /**
     * Traitement des ordres.
     */
    @Override
    public void run() {
        Socket socket = null;

        try {
            while (run) {
                //attente de la connection
                socket = serverSocket.accept();
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                outputStream = new DataOutputStream(socket.getOutputStream());

                String xml = inputStream.readUTF();

                if (xml != null) {
                    printMessage("requête: " + xml);
                    executeCommand(new String[]{"/bin/sh", "-c", xml});
                    outputStream.flush();
                }
                socket.close();
            }
        } catch (IOException e) {
            printError(e);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                printError(e);
            }

            if (run) {
                printMessage("redémarrage de LaunchServer");
                start();
            }
        }
    }

    /**
     * Exécute la commande reçue.
     *
     * @param command la commande.
     * @param output buffer pour la sortie standard.
     * @param error buffer pour la sortie des erreurs
     *
     * @return la valeur de fin du processus.
     */
    private int executeCommand(String[] command) {
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
                printError(e);
            }
        } catch (IOException e) {
            printError(e);
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
     * @param output un StringBuilder initialisé pour afficher la sortie.
     *
     * @return la thread de gestion du flux.
     */
    private Thread createReadThread(final InputStream inputStream, String name) {
        Thread thread = new Thread(name) {
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
                    printError(e);
                }
            }
        };
        return thread;
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
                outputStream.writeUTF(data.toString());
            }
            outputStream.flush();
        } catch (Exception e) {
            printError(e);
        }
    }

    /**
     * Affiche un message.
     *
     * @param message le mesage.
     */
    private static void printMessage(String message) {
        System.out.println(message);
    }

    /**
     * Affiche une erreur.
     *
     * @param error l'erreur.
     */
    private static void printError(Throwable error) {
        error.printStackTrace();
    }
}
