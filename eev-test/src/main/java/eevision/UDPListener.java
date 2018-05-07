package eevision;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Cette thread permet la réception et l'écoute du son à travers le réseaux en mode UDP.
 *
 * @author fabrice
 * @version 0.66
 */
public class UDPListener extends Listener {

    public static void main(String[] args) {
        int port = 9000;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        UDPListener listener = new UDPListener(port);

        listener.start();
    }

    /**
     * Initialisation avec un numéro de port.
     *
     * @param port le port du receveur.
     */
    public UDPListener(int port) {
        super(port);
    }

    /**
     * Démarrage de la réception des données et de leur lecture.
     */
    @Override
    public void run() {
        DatagramPacket paquet = new DatagramPacket(data, data.length);

        try {
            //attente de la connection
            DatagramSocket speeker = new DatagramSocket(port);

            //tant qu'il faut lire les données
            while (receive) {
                speeker.receive(paquet);
                sourceDataLine.write(paquet.getData(), 0, paquet.getLength());
            }

            //fermeture de la connection
            speeker.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
