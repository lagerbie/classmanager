package eevision;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Cette thread permet la réception et l'écoute du son à travers le réseaux en mode TCP.
 *
 * @author fabrice
 * @version 0.66
 */
public class TCPListener extends Listener {
    private long duration;

    public static void main(String[] args) {
        int port = 9000;
        long duration = -1;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        } else if (args.length == 2) {
            port = Integer.parseInt(args[0]);
            duration = Long.parseLong(args[1]);
        }

        TCPListener listener = new TCPListener(port, duration);

        listener.start();
    }

    /**
     * Initialisation avec un numéro de port.
     *
     * @param port le port du receveur.
     * @param duration le temps de réception (-1 si infini)
     */
    public TCPListener(int port, long duration) {
        super(port);
        this.duration = duration;
    }

    /**
     * Démarrage de la réception des données et de leur lecture.
     */
    @Override
    public void run() {
        long initTime = System.currentTimeMillis();

        int cnt;

        try {
            //attente de la connection
            ServerSocket service = new ServerSocket(port);
            Socket speeker = service.accept();
            InputStream inputStream = speeker.getInputStream();

            //tant qu'il faut lire les données
            while (receive) {
                cnt = inputStream.read(data);
                if (cnt > 0) {
                    sourceDataLine.write(data, 0, cnt);
                }
                if (duration > 0 && System.currentTimeMillis() - initTime > duration) {
                    receive = false;
                }
            }

            //fermeture de la connection
            speeker.close();
            service.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
