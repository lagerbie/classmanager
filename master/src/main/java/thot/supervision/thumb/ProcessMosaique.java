package thot.supervision.thumb;

import java.awt.*;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.exception.ThotException;
import thot.supervision.Command;
import thot.supervision.CommandAction;
import thot.supervision.CommandParamater;
import thot.supervision.CommandType;
import thot.supervision.CommandXMLUtilities;
import thot.utils.ProgressThread;
import thot.utils.Server;
import thot.utils.Utilities;

/**
 * Gestion de la mosaique avec des processus.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class ProcessMosaique extends ProgressThread {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessMosaique.class);

    /**
     * Port de base pour la communication mosaïque -> thumb.
     */
    private int mosaiqueToThumbPortBase;
    /**
     * Port pour la communication thumb -> mosaïque.
     */
    private int thumbToMosaiquePort;
    /**
     * Chemin du processus.
     */
    private File processus;
    /**
     * Thread pour la réception des requêtes client.
     */
    private ListenOrder listenOrder;
    /**
     * Process de chaque client.
     */
    private List<Process> screenWindows;
    /**
     * Numéros de chaque client.
     */
    private List<Integer> screenNumber;
    /**
     * Fenêtre de fond pour empêcher de revenir sur les commandes et cacher les fenêtres java de chaque élève.
     */
    private JDialog window;
    /**
     * timeout.
     */
    private int timeout = 100;

    /**
     * Initialisation.
     *
     * @param thumbPortBase le port de base pour la comm mosaïque -> thumb
     * @param thumbToMosaiquePort le port pour la comm thumb -> mosaïque
     * @param processus le chemin du processus.
     */
    public ProcessMosaique(int thumbPortBase, int thumbToMosaiquePort, File processus) {
        this.mosaiqueToThumbPortBase = thumbPortBase - 1;
        this.thumbToMosaiquePort = thumbToMosaiquePort;
        this.processus = processus;

        screenWindows = new ArrayList<>(32);
        screenNumber = new ArrayList<>(32);

        window = new JDialog();
        window.setUndecorated(true);
        window.setAlwaysOnTop(true);
        window.setFocusable(true);
        window.setSize(Toolkit.getDefaultToolkit().getScreenSize());

        window.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    close();
                }
            }
        });

        startListenOrder();
    }

    /**
     * Modifie le timeout.
     *
     * @param timeout le timeout.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Ajoute un client.
     *
     * @param numero le numero du diffuseur.
     * @param addressIP l'adresse IP de celui qui envoi l'écran.
     * @param port le port de communication utilisé pour les données de l'écran.
     * @param name le nom du diffuseur.
     * @param x la position horizontale de la fenêtre.
     * @param y la position verticale de la fenêtre.
     * @param width la largeur de la fenêtre.
     * @param height la hauteur de la fenêtre.
     * @param nbLines le nombre de lignes.
     * @param timeout timeout pour l'affichage d'écran.
     */
    public void addClient(int numero, String addressIP, int port, String name, int x, int y, int width, int height,
            int nbLines, int timeout) throws ThotException {
        if (!window.isVisible()) {
            window.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            window.setVisible(true);
        }

        String args = getArgs(numero, addressIP, port, name, x, y, width, height, nbLines, timeout,
                screenWindows.isEmpty());

        Process screenWindow = startProcess(args);
        screenWindows.add(screenWindow);
        screenNumber.add(numero);

        window.toFront();
    }

    /**
     * Créer les arguments pour le processus.
     *
     * @param numero le numreo du client.
     * @param addressIP l'adresse IP de celui qui envoi l'écran.
     * @param port le port de communication utilisé pour les données de l'écran.
     * @param name le nom du diffuseur.
     * @param x la position horizontale de la fenêtre.
     * @param y la position verticale de la fenêtre.
     * @param width la largeur de la fenêtre.
     * @param height la hauteur de la fenêtre.
     * @param nbLines le nombre de lignes.
     * @param timeout le temps maximum d'attende.
     * @param initKeyboardFocus l'initialisation du focus clavier.
     *
     * @return la liste d'arguments.
     */
    private String getArgs(int numero, String addressIP, int port, String name, int x, int y, int width, int height,
            int nbLines, int timeout, boolean initKeyboardFocus) {

        StringBuilder builder = new StringBuilder(256);
        builder.append(" --numero ");
        builder.append(String.valueOf(numero));
        builder.append(" --mosaiqueToThumbPort ");
        builder.append(String.valueOf(mosaiqueToThumbPortBase + numero));
        builder.append(" --thumbToMosaiquePort ");
        builder.append(String.valueOf(thumbToMosaiquePort));
        builder.append(" --addressIP ");
        builder.append(addressIP);
        builder.append(" --port ");
        builder.append(String.valueOf(port));
        builder.append(" --name ");
        builder.append(name);
        builder.append(" --x ");
        builder.append(String.valueOf(x));
        builder.append(" --y ");
        builder.append(String.valueOf(y));
        builder.append(" --width ");
        builder.append(String.valueOf(width));
        builder.append(" --height ");
        builder.append(String.valueOf(height));
        builder.append(" --nbLines ");
        builder.append(String.valueOf(nbLines));
        builder.append(" --timeout ");
        builder.append(String.valueOf(timeout));
        builder.append(" --initKeyboardFocus ");
        builder.append(String.valueOf(initKeyboardFocus));

        return builder.toString();
    }

    /**
     * Démarre le processus pour la réception d'écran.
     *
     * @param args les arguments de lancement du processus.
     *
     * @return le processus démarré.
     */
    private Process startProcess(String args) throws ThotException {
        StringBuilder output = new StringBuilder(1024);
        StringBuilder error = new StringBuilder(1024);

        String command = processus.getAbsolutePath() + args;
        if (Utilities.isJarFile(processus)) {
            command = "java -jar \"" + processus.getAbsolutePath() + "\"" + args;
        }
        LOGGER.info("execute mosaique: " + command);

        return Utilities.startProcess("thumb", output, error, command);
    }

    /**
     * Envoi un ordre une vignette.
     *
     * @param action l'identifiant de laction.
     * @param parameter le paramètre de l'action.
     * @param numero le numéro du client
     */
    private void sendToThumb(String action, int parameter, int numero) {
        Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.getCommandAction(action));
        command.putParameter(CommandParamater.PARAMETER, parameter);
        InetSocketAddress socketAddress = new InetSocketAddress("127.0.0.1", mosaiqueToThumbPortBase + numero);

        try (Socket socket = new Socket()) {
            socket.connect(socketAddress, timeout);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(CommandXMLUtilities.getXML(command));
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.error("", e);
        }
    }

    /**
     * Envoi un ordre à toutes les vignettes.
     *
     * @param action l'identifiant de laction.
     * @param parameter le paramètre de l'action.
     */
    private void sendToAllThumb(String action, int parameter) {
        for (int number : screenNumber) {
            sendToThumb(action, parameter, number);
        }
    }

    /**
     * Exécute une requête reçue.
     *
     * @param xml la requête.
     */
    private void execute(String xml) {
        List<Command> commands = CommandXMLUtilities.parseCommand(xml);
        for (Command command : commands) {
            if (command.getAction() == CommandAction.CLOSE) {
                close();
            }
        }
    }

    /**
     * Quitte la mosaique et ferme les différentes miniatures.
     */
    public void close() {
        if (!window.isVisible()) {
            return;
        }

        sendToAllThumb(CommandAction.CLOSE.getAction(), 1);

        for (Process screenWindow : screenWindows) {
            screenWindow.destroy();
        }

        window.setVisible(false);

        screenWindows.clear();
        screenNumber.clear();
        fireProcessEnded(0);
    }

    /**
     * Ajoute un WindowListener.
     *
     * @param listener
     */
    public void addWindowListener(WindowListener listener) {
        window.addWindowListener(listener);
    }

    /**
     * Ajoute un ComponentListener.
     *
     * @param listener
     */
    public void addComponentListener(ComponentListener listener) {
        window.addComponentListener(listener);
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
     *
     * @version 1.90
     */
    private class ListenOrder extends Server {

        private ListenOrder() {
            super(thumbToMosaiquePort);
        }

        @Override
        protected void process(Socket socket) throws IOException {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());

            String xml = inputStream.readUTF();
            LOGGER.info("ProcessMosaique commande {}", xml);
            execute(xml);
        }
    }
}
