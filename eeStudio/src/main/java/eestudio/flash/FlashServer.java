package eestudio.flash;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

import eestudio.utils.XMLUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.Server;

/**
 * Serveur pour les requête du Flash.
 *
 * @author Fabrice Alleau
 */
public class FlashServer extends Server {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FlashServer.class);

    /**
     * référence pour exécuter une commande
     */
    private FlashCore flashCore;

    /**
     * Initialisation.
     *
     * @param core le noyau de supervision.
     * @param port le port d'écoute des commandes.
     */
    public FlashServer(FlashCore core, int port) {
        super(port);
        this.flashCore = core;
    }

    /**
     * Traitement de la connexion.
     *
     * @param socket la connexion.
     *
     * @throws IOException
     */
    @Override
    protected void process(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();

        StringBuilder xml = new StringBuilder(64);

        byte[] data = new byte[1024];
        int cnt = inputStream.read(data, 0, data.length);
        while (cnt > 0) {
            xml.append(new String(data, 0, cnt, "UTF-8"));
            // scan for the zero-byte EOM delimiter
            if (data[cnt - 1] == (byte) 0) {
                break;
            }
            cnt = inputStream.read(data, 0, data.length);
        }

        if (xml.length() > 0) {
            String request = xml.substring(0, xml.length() - 1);
            LOGGER.info("Flash request: {}", request);

            try {
                List<Command> commands = XMLUtilities.parseCommand(request);
                for (Command command : commands) {
                    flashCore.executeCommand(command.getAction(), command.getParameter());
                    command.clean();
                }
                commands.clear();
            } catch (Exception e) {
                //en cas d'erreur de traitement
                LOGGER.error("Erreur lors du traitement de la commande", e);
            }
        }
    }

}
