package thot.supervision.thumb;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.supervision.Command;
import thot.supervision.CommandAction;
import thot.supervision.CommandModule;
import thot.supervision.CommandParamater;
import thot.supervision.CommandXMLUtilities;

/**
 * Fenêtre pour la visualisation d'un élève en mode mosaique et plein écran.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class ProcessScreenWindow extends ScreenWindow {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessScreenWindow.class);

    /**
     * Temps d'attente maximum pour l'envoi des ordres (=2s).
     */
    private static final int TIME_MAX_FOR_ORDER = 2000;
    /**
     * Numéro du client.
     */
    private int numero;
    /**
     * Port pour la communication thumb -> mosaïque.
     */
    private int thumbToMosaiquePort;
    /**
     * Port pour la communication thumb -> mosaïque.
     */
    private int mosaiqueToThumbPort;
    /**
     * Thread pour réceptionner les requêtes du noyau.
     */
    private ListenOrder listenOrder;

    public static void main(String[] args) {
        int numero = 0;
        int mosaiqueToThumbPort = -1;
        int thumbToMosaiquePort = -1;

        String addressIP = null;
        int port = -1;
        String name = null;

        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;

        int nbLines = -1;
        int timeout = 100;
        boolean initKeyboardFocus = false;

        LOGGER.info("version 1.8.4");

        try {
            for (int i = 1; i < args.length; i += 2) {
                String option = args[i - 1];
                String value = args[i];
                LOGGER.info("parameter: " + option + " " + value);

                switch (option) {
                    case "--numero":
                        numero = Integer.parseInt(value);
                        break;
                    case "--mosaiqueToThumbPort":
                        mosaiqueToThumbPort = Integer.parseInt(value);
                        break;
                    case "--thumbToMosaiquePort":
                        thumbToMosaiquePort = Integer.parseInt(value);
                        break;
                    case "--addressIP":
                        addressIP = value;
                        break;
                    case "--port":
                        port = Integer.parseInt(value);
                        break;
                    case "--name":
                        name = value;
                        break;
                    case "--x":
                        x = Integer.parseInt(value);
                        break;
                    case "--y":
                        y = Integer.parseInt(value);
                        break;
                    case "--width":
                        width = Integer.parseInt(value);
                        break;
                    case "--height":
                        height = Integer.parseInt(value);
                        break;
                    case "--nbLines":
                        nbLines = Integer.parseInt(value);
                        break;
                    case "--timeout":
                        timeout = Integer.parseInt(value);
                        break;
                    case "--initKeyboardFocus":
                        initKeyboardFocus = Boolean.parseBoolean(value);
                        break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Erreur lors du traitement des paramètres", e);
            return;
        }

        ProcessScreenWindow screenWindow = new ProcessScreenWindow(numero, mosaiqueToThumbPort, thumbToMosaiquePort);
        screenWindow.setTimeout(timeout);

        if (initKeyboardFocus) {
            screenWindow.setInitFocus(initKeyboardFocus);
        }

        try {
            screenWindow.startListenOrder();
            screenWindow.start(addressIP, port, nbLines, name, x, y, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Initialisation.
     *
     * @param numero le numero du client.
     * @param mosaiqueToThumbPort le port de communication mosaïque -> thumb.
     * @param thumbToMosaiquePort le port de communication thumb -> mosaïque.
     */
    private ProcessScreenWindow(int numero, int mosaiqueToThumbPort, int thumbToMosaiquePort) {
        super();
        this.numero = numero;
        this.mosaiqueToThumbPort = mosaiqueToThumbPort;
        this.thumbToMosaiquePort = thumbToMosaiquePort;
    }

    /**
     * Envoi une reqête au gestionnaire.
     *
     * @param action le type de l'action.
     */
    private void sendToMosaique(CommandAction action) {
        Command command = new Command(CommandModule.THUMB, action);
        command.putParameter(CommandParamater.PARAMETER, numero);
        LOGGER.info("ProcessScreenWindow sendToMosaique {}", command);

        DataOutputStream outputStream;
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", thumbToMosaiquePort);

        try (Socket socket = new Socket()) {
            socket.connect(socketAddress, TIME_MAX_FOR_ORDER);
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(CommandXMLUtilities.getXML(command));
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.error("Error in sendToMosaique", e);
        }
    }

    /**
     * Quitte avec appel de la fonction quitter() de la mosaique.
     */
    @Override
    protected void closeCommand() {
        sendToMosaique(CommandAction.CLOSE);
    }

    /**
     * Exécute un ordre.
     *
     * @param xml l'ordre.
     */
    private void execute(String xml) {
        Command command = CommandXMLUtilities.parseCommand(xml);
        if (command != null && command.getAction() == CommandAction.CLOSE) {
            listenOrder.stop();
            close();
            System.exit(0);
        }
    }

    /**
     * Démarre le serveur pour la gestion des requêtes des processus déportés.
     */
    private void startListenOrder() throws IOException {
        listenOrder = new ListenOrder();
        listenOrder.start();
    }

    /**
     * Thread pour la gestion des requêtes des processus.
     */
    private class ListenOrder implements Runnable {

        /**
         * Serveur pour la réception des requêtes du gestionnaire mosaïque.
         */
        private ServerSocket serverSocket;
        /**
         * Etat pour l'arrêt de la thread.
         */
        private boolean run = false;

        /**
         * Démarre le serveur.
         */
        public void start() throws IOException {
            serverSocket = new ServerSocket(mosaiqueToThumbPort);

            run = true;
            Thread thread = new Thread(this, this.getClass().getName());
            thread.start();
        }

        /**
         * Arrête le serveur.
         */
        void stop() {
            run = false;
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    LOGGER.error("Impossible de fermer le serveur", e);
                }
            }
        }

        /**
         * Traitement des requêtes.
         */
        @Override
        public void run() {
            DataInputStream inputStream;
            while (run) {
                //attente de la connection
                try (Socket socket = serverSocket.accept()) {
                    inputStream = new DataInputStream(socket.getInputStream());

                    String xml = inputStream.readUTF();
                    LOGGER.info("mosaique commande {}", xml);
                    execute(xml);
                } catch (IOException e) {
                    LOGGER.error("ProcessScreenWindow error on run port {}", e, mosaiqueToThumbPort);
                }
            }

            stop();
        }
    }
}
