package eevision;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

/**
 * Cette thread permet l'envoi de son du micro à travers le réseaux en mode TCP.
 *
 * @author fabrice
 * @version 0.66
 */
public class TCPSpeeker extends Speeker {

    public static void main(String[] args) {
        String ip = "127.0.0.1";
        int port = 9000;
        long duration = -1;
        if (args.length == 2) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        } else if (args.length == 3) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
            duration = Long.parseLong(args[2]);
        }

        TCPSpeeker speeker = new TCPSpeeker(ip, port, duration);

        speeker.start();
    }

    /**
     * Initialisation avec une adresse IP et un numéro de port.
     *
     * @param ip l'adresse ip du receveur.
     * @param port le port du receveur.
     * @param duration le temps d'envoi (-1 si infini)
     */
    public TCPSpeeker(String ip, int port, long duration) {
        super(ip, port, duration);
    }

    /**
     * Démarrage de l'écoute du microphone et de l'envoi des données.
     */
    @Override
    public void run() {
        printInfo();

        long initTime = System.currentTimeMillis();

        int cnt;

        try {
            //Ouverture de la connection
            Socket listener = new Socket(getIp(), getPort());
            OutputStream outputStream = listener.getOutputStream();

            //Tant qu'il faut envoyer des données.
            while (isSend()) {
                cnt = targetDataLine.read(data, 0, data.length);
                if (cnt > 0) {
                    outputStream.write(data, 0, cnt);
                }
                if (getDuration() > 0 && System.currentTimeMillis() - initTime > getDuration()) {
                    setSend(false);
                }
            }

            //fermeture de la connection
            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printInfo() {
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            if (mixer.isLineSupported(targetDataLine.getLineInfo())) {
                if (mixer.isOpen()) {
                    System.out.println("Mixer used: " + mixer.getMixerInfo());
                }
            }
        }
    }

}
