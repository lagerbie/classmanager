package eevision;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Cette thread permet l'envoi de son du micro à travers le réseaux en mode UDP.
 *
 * @author fabrice
 * @version 0.66
 */
public class UDPSpeeker extends Speeker {

    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 9000;
        if (args.length == 2) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }

        UDPSpeeker speeker = new UDPSpeeker(ip, port, -1);
        speeker.start();
    }

    /**
     * Initialisation avec une adresse IP et un numéro de port.
     *
     * @param ip l'adresse ip du receveur.
     * @param port le port du receveur.
     */
    public UDPSpeeker(String ip, int port, long duration) {
        super(ip, port, duration);
    }

    /**
     * Démarrage de l'écoute du microphone et de l'envoi des données.
     */
    @Override
    public void run() {
        DatagramPacket paquet = new DatagramPacket(data, data.length);

        try {
            //Ouverture de la connection
            DatagramSocket listener = new DatagramSocket(getPort(), InetAddress.getByName(getIp()));

            //Tant qu'il faut envoyer des données.
            while (isSend()) {
                int cnt = targetDataLine.read(data, 0, data.length);
                if (cnt > 0) {
                    paquet.setData(data, 0, cnt);
                    listener.send(paquet);
                }
            }

            //fermeture de la connection
            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
