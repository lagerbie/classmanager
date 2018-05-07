package eestudio.flash;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayDeque;

import eestudio.utils.Edu4Logger;
import eestudio.utils.Server;
import eestudio.utils.Utilities;

/**
 * Client avec liste d'attente pour l'envoi des commandes au Flash.
 *
 * @author Fabrice Alleau
 * @version 0.95
 * @since version 0.94
 */
public class FlashClient extends Server {
    /**
     * Taille de la liste d'attente
     */
    private final int FIFO_MAX = 64;//16 trop petit (suppression de commandes possible)
    /**
     * Liste d'attente
     */
    private ArrayDeque<String> fifo = new ArrayDeque<>(FIFO_MAX);

    /**
     * Initialisation.
     *
     * @param port port de communication.
     *
     * @since version 0.94
     */
    public FlashClient(int port) {
        super(port);
    }

    /**
     * Traitement de la connexion.
     *
     * @param socket la connexion.
     *
     * @throws IOException problème de transmission
     * @since version 0.95 (équivalent au traitement de la 0.94)
     */
    @Override
    protected void process(Socket socket) throws IOException {
        socket.setTcpNoDelay(true);
        OutputStream outputStream = socket.getOutputStream();

        String xml;
        while (isRun()) {
            Utilities.waitInMillisecond(30);
            xml = fifo.poll();

            if (xml != null) {
                outputStream.write(xml.getBytes("UTF-8"));
                outputStream.write((byte) 0);
                outputStream.flush();
                Edu4Logger.info("send to Flash Command : " + xml);
            }
        }
    }

    /**
     * Envoi d'une commandes xml au Flash.
     *
     * @param xml la commande.
     *
     * @return toujours <code>true</code>.
     *
     * @since version 0.94
     */
    public boolean sendCommand(String xml) {
        return fifo.offer(xml);
    }

}
