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
package supervision;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Fenêtre pour la visualisation d'un élève en mode mosaique et plein écran.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class ProcessScreenWindow extends ScreenWindow {
    private static final long serialVersionUID = 19000L;

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

        File logFile = new File(System.getProperty("java.io.tmpdir"),
                "Siclic/thumb%u.xml");
        CommonLogger.setLogFile(logFile);
        CommonLogger.info("version 1.90.00");

        try {
            for (int i = 1; i < args.length; i += 2) {
                String option = args[i - 1];
                String value = args[i];
                CommonLogger.info("parameter: " + option + " " + value);

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
                    case "--verbose":
                        CommonLogger.setLevel(Integer.parseInt(value));
                        break;
                }
            }
        } catch (Exception e) {
            CommonLogger.error(e);
            return;
        }

        ProcessScreenWindow screenWindow = new ProcessScreenWindow(
                numero, mosaiqueToThumbPort, thumbToMosaiquePort);
        screenWindow.setTimeout(timeout);

        if (initKeyboardFocus) {
            screenWindow.setInitFocus(initKeyboardFocus);
        }

        screenWindow.startListenOrder();
        screenWindow.start(addressIP, port, nbLines, name, x, y, width, height);
    }

    /**
     * Initialisation.
     *
     * @param numero le numero du client.
     * @param mosaiqueToThumbPort le port de communication mosaïque -> thumb.
     * @param thumbToMosaiquePort le port de communication thumb -> mosaïque.
     */
    public ProcessScreenWindow(int numero,
            int mosaiqueToThumbPort, int thumbToMosaiquePort) {
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
    private void sendToMosaique(String action) {
        ProcessCommand command = new ProcessCommand(action, numero);
        CommonLogger.info("ProcessScreenWindow sendToMosaique: "
                + command.createXMLCommand());

        Socket socket = new Socket();
        DataOutputStream outputStream;
        InetSocketAddress socketAddress
                = new InetSocketAddress("127.0.0.1", thumbToMosaiquePort);

        try {
            socket.connect(socketAddress, TIME_MAX_FOR_ORDER);
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(command.createXMLCommand());
            outputStream.flush();
        } catch (IOException e) {
            CommonLogger.error("IOerror in sendToMosaique: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                CommonLogger.error(e);
            }
        }
    }

    /**
     * Quitte avec appel de la fonction quitter() de la mosaique.
     */
    @Override
    protected void closeCommand() {
        CommonLogger.debug("Thumb closeCommand()");
        sendToMosaique(ProcessCommand.CLOSE);
    }

    /**
     * Exécute un ordre.
     *
     * @param xml l'ordre.
     */
    private void execute(String xml) {
        ProcessCommand command = ProcessCommand.createCommand(xml);
        if (command.getAction().contentEquals(ProcessCommand.CLOSE)) {
            listenOrder.stop();
            close();
            System.exit(0);
        }
    }

    /**
     * Démarre le serveur pour la gestion des requêtes des processus déportés.
     */
    private void startListenOrder() {
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
        private Thread thread;

        /**
         * Démarre le serveur.
         */
        public boolean start() {
            try {
                serverSocket = new ServerSocket(mosaiqueToThumbPort);
            } catch (IOException e) {
                CommonLogger.error("IOError in ListenOrder.start: " + e.getMessage());
                return false;
            }

            run = true;
            thread = new Thread(this, this.getClass().getName());
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
                } catch (IOException e) {
                    CommonLogger.error(e);
                }
            }
        }

        /**
         * Traitement des requêtes.
         */
        @Override
        public void run() {
            Socket socket = null;
            DataInputStream inputStream;

            try {
                while (run) {
                    //attente de la connection
                    socket = serverSocket.accept();

                    inputStream = new DataInputStream(socket.getInputStream());

                    String xml = inputStream.readUTF();
                    CommonLogger.info("mosaique commande : " + xml);
                    execute(xml);

                    //fermeture de la connection et reboucle sur une écoute du
                    //port (si la connection n'est pas fermée, utilisation
                    //inutile du cpu).
                    socket.close();
                }
            } catch (IOException e) {
                CommonLogger.error("ProcessScreenWindow error on run port : "
                        + mosaiqueToThumbPort);
                CommonLogger.error(e);
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        CommonLogger.error(e);
                    }
                }

                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        CommonLogger.error(e);
                    }
                }

                if (run) {
                    CommonLogger.info("redémarrage de Processus");
                    start();
                } else {
                    System.exit(0);
                }
            }
        }
    }
}
