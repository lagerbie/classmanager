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
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;

import javax.sound.sampled.AudioFormat;

/**
 * Serveur pour les demandes des données du microphones.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Server implements Runnable {
    /*
     * Demande de connection de la forme:
     * <?xml version=\"1.0\" encoding=\"UTF-8\"?>
     *     <connection>
     *         <address> addressIP</address>
     *         <port> port </port>
     *     </connection>
     * 
     * Demande de déconnection de la forme:
     * <?xml version=\"1.0\" encoding=\"UTF-8\"?>
     *     <disconnection>
     *         <address> addressIP </address>
     *         <port> port </port>
     *     </disconnection>
     */

    /**
     * Format de capture et de rendu audio: 11025 Hz, 16 bits, mono, signed,
     * little-endian.
     */
    private final AudioFormat audioFormat = new AudioFormat(11025.0f, 16, 1, true, false);

    /**
     * Port d'envoi audio principal.
     */
    private final int audioPort = 7220;
    /**
     * Port d'envoi audio secondaire.
     */
    private final int audioPairingPort = 7221;
    /**
     * Port d'écoute du service.
     */
    private final int port = 7206;
    /**
     * Server pour les connexions réseau.
     */
    private ServerSocket serverSocket;
    /**
     * Gestionnaire d'envoi du son.
     */
    private MicrophoneServer captureServer;
    /**
     * Serveur de réception de flux audio principal.
     */
    private ListenerServer listenerServer;
    /**
     * Serveur de réception de flux audio pour le pairing.
     */
    private ListenerServer listenerPairingServer;

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        String logName = String.format("Siclic/soundServer-%1$tF-%1$tH.%1$tM.%1$tS.xml",
                calendar);

        File logFile = new File(System.getProperty("java.io.tmpdir"), logName);
        CommonLogger.setLogFile(logFile);
        CommonLogger.info("version: 1.90.00");

        Server server = new Server();
        server.start();
    }

    /**
     * Initialisation.
     */
    public Server() {
        captureServer = new MicrophoneServer(audioFormat);

        listenerServer = new ListenerServer(audioFormat, audioPort);
        listenerServer.start();

        if (listenerServer == null || !listenerServer.isLineOpen()) {
            CommonLogger.error("listenerServer not open");
        }

        listenerPairingServer = new ListenerServer(audioFormat, audioPairingPort);
        listenerPairingServer.start();

        if (listenerPairingServer == null || !listenerPairingServer.isLineOpen()) {
            CommonLogger.error("listenerPairingServer not open");
        }
    }

    /**
     * Démmarage du service.
     */
    private void start() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            CommonLogger.error(e);
            System.exit(-1);
        }

        new Thread(this, this.getClass().getName()).start();
    }

    @Override
    public void run() {
        Socket socket = null;
        DataInputStream inputStream;
        String[] split;

        try {
            while (true) {
                //attente de la connection
                socket = serverSocket.accept();

                inputStream = new DataInputStream(socket.getInputStream());

                String xml = inputStream.readUTF();
                CommonLogger.info("sound server command: " + xml);

                split = xml.split("<address>|</address><port>|</port>");

                if (split.length == 4) {
                    String addressIP = split[1];
                    int portAudio = Integer.parseInt(split[2]);
                    if (xml.contains("<connection>")) {
                        captureServer.connect(addressIP, portAudio);
                    } else if (xml.contains("<disconnection>")) {
                        captureServer.disconnect(addressIP, portAudio);
                    }
                }

                //fermeture de la connection et reboucle sur une écoute du
                //port (si la connection n'est pas fermée, utilisation
                //inutile du cpu).
                socket.close();
                socket = null;
            }
        } catch (IOException | NumberFormatException e) {
            CommonLogger.error(e);
        }

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
            serverSocket = null;
        }

        start();
    }
}
