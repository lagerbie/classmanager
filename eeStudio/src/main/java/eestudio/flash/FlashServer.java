package eestudio.flash;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

import eestudio.utils.Edu4Logger;
import eestudio.utils.Server;
import eestudio.utils.XMLUtilities;

/**
 * Serveur pour les requête du Flash.
 *
 * @author Fabrice Alleau
 * @version 0.96
 * @since version 0.94
 */
public class FlashServer extends Server {
    /**
     * référence pour exécuter une commande
     */
    private FlashCore flashCore;

    /**
     * Initialisation.
     *
     * @param core le noyau de supervision.
     * @param port le port d'écoute des commandes.
     *
     * @since version 0.94 - version 0.95
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
     * @since version 0.95 (équivalent au traitement de la version 0.94) - version 0.96
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
            Edu4Logger.info("Flash request: " + request);

            try {
                List<Command> commands = XMLUtilities.parseCommand(request);
                for (Command command : commands) {
                    flashCore.executeCommand(command.getAction(), command.getParameter());
                    command.clean();
                }
                commands.clear();
            } catch (Exception e) {
                //en cas d'erreur de traitement
                Edu4Logger.error(e);
            }
        }
    }

}
