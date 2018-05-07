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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.awt.Desktop;

import javax.swing.event.EventListenerList;

/**
 * Classe pour le transfert de fichiers.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class FileTransfert {

    /**
     * Taille du buffer de lecture.
     */
    private static final int BUFFER_SIZE = 1024 * 64;
    /**
     * Taille du buffer interne des sockets.
     */
    private static final int SOCKET_BUFFER_SIZE = 1024 * 64;
    /**
     * Taille du buffer interne des flux de fichiers.
     */
    private static final int FILE_BUFFER_SIZE = 1024 * 64;
    /**
     * Gestion du mode buffered des sockets.
     */
    private static final boolean bufferedSocket = false;
    /**
     * Sauvegarde des écoutes sur différents éléments.
     */
    private EventListenerList listeners;
    /**
     * Option d'annulation.
     */
    private boolean running;

    /**
     * Initalise les fenêtres graphiques pour la progression.
     */
    public FileTransfert() {
        listeners = new EventListenerList();
    }

    /**
     * Ajoute d'une écoute de type ProgressListener.
     *
     * @param listener l'écoute à ajouter.
     */
    public void addListener(ProgressListener listener) {
        listeners.add(ProgressListener.class, listener);
    }

    /**
     * Enlève une écoute de type MasterCoreListener.
     *
     * @param listener l'écoute à enlever.
     */
    public void removeListener(ProgressListener listener) {
        listeners.remove(ProgressListener.class, listener);
    }

    /**
     * Notification du changement du titre.
     *
     * @param title le nouveau titre.
     */
    private void fireTitleChanged(String title) {
        for (ProgressPercentListener listener : listeners.getListeners(ProgressPercentListener.class)) {
            listener.processTitleChanged(this, title);
        }
    }

    /**
     * Notification du changement du message.
     *
     * @param message le nouveau message.
     */
    private void fireMessageChanged(String message) {
        for (ProgressPercentListener listener : listeners.getListeners(ProgressPercentListener.class)) {
            listener.processMessageChanged(this, message);
        }
    }

    /**
     * Notification du changement du mode double de la progression.
     *
     * @param doubleStatus le mode double progression (<code>true</code> si deux
     * pourcentages de progression peuvent être affichés.
     */
    private void fireDoubleStatusChanged(boolean doubleStatus) {
        for (ProgressPercentListener listener : listeners.getListeners(ProgressPercentListener.class)) {
            listener.processDoubleStatusChanged(this, doubleStatus);
        }
    }

    /**
     * Notification du début du traitement.
     *
     * @param determinated indique si le processus peut afficher un poucentage
     * de progression.
     */
    private void fireProcessBegin(boolean determinated) {
        for (ProgressListener listener : listeners.getListeners(ProgressListener.class)) {
            listener.processBegin(this, determinated);
        }
    }

    /**
     * Notification de fin du traitement.
     *
     * @param exit la valeur de sortie (par convention 0 équvaut à une sortie
     * normale).
     */
    private void fireProcessEnded(int exit) {
        for (ProgressListener listener : listeners.getListeners(ProgressListener.class)) {
            listener.processEnded(this, exit);
        }
    }

    /**
     * Notification du début du traitement.
     *
     * @param percent le nouveau pourcentage de progression.
     */
    private void firePercentChanged(int percent) {
        for (ProgressPercentListener listener : listeners.getListeners(ProgressPercentListener.class)) {
            listener.percentChanged(this, percent);
        }
    }

    /**
     * Notification du début du traitement.
     *
     * @param total la nouvelle valeur de progression totale en pourcentage.
     * @param subTotal la nouvelle valeur de progression intermédiaire en
     * pourcentage.
     */
    private void firePercentChanged(int total, int subTotal) {
        for (ProgressPercentListener listener : listeners.getListeners(ProgressPercentListener.class)) {
            listener.percentChanged(this, total, subTotal);
        }
    }

    /**
     * Détermine si il est nécessaire de télécharger un fichier.
     *
     * @param file le fichier en local.
     * @param size la taille du fichier à télécharger.
     * @return si il nécessaire de le télécharger.
     */
    public static boolean isDownloadFile(File file, long size) {
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            CommonLogger.error(e);
            return false;
        }

        return (file.canWrite() && file.length() != size);
    }

    /**
     * Ouvre un fichier en lançant l'application par défaut associée.
     *
     * @param file le fichier à ouvrir.
     */
    public static void launchFile(File file) {
        if (file == null || !file.exists()) {
            StringBuilder message = new StringBuilder("fichier inexistant: ");
            if (file == null) {
                message.append("null file");
            } else {
                message.append(file.getAbsolutePath());
            }
            CommonLogger.error(message.toString());
            return;
        }
        //Lancement du fichier par l'application par défaut
        try {
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            CommonLogger.error(e);
        }
    }

    /**
     * Annule le transfert.
     */
    public void cancel() {
        running = false;
    }

    /**
     * Indique si on est en transfert de fichier.
     *
     * @return <code>true</code> si on est en transfert de fichier.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Démarrage de la réception des données.
     *
     * @param file le nom du fichier.
     * @param size la taille du fichier.
     * @param addressIP l'adresse IP du serveur.
     * @param port le port du serveur.
     */
    public void loadFile(File file, long size, String addressIP, int port) {
        boolean download = isDownloadFile(file, size);

        running = true;

        long initTime = System.nanoTime();
        double transfertTime = 0;
        double fileTime = 0;
        double currentTime;

        fireMessageChanged(file.getName());
        fireDoubleStatusChanged(false);

        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        double receved = 0.0;

        OutputStream fileOutputStream = null;
        InetSocketAddress socketAddress = new InetSocketAddress(addressIP, port);
        Socket socket = new Socket();
        InputStream inputstream;
        try {
            //connexion de la socket à distance
            socket.connect(socketAddress, Constants.TIME_MAX_FOR_LOAD);

            if (bufferedSocket) {
                inputstream = new BufferedInputStream(
                        socket.getInputStream(), SOCKET_BUFFER_SIZE);
            } else {
                inputstream = socket.getInputStream();
            }

            socket.getOutputStream().write(download ? 1 : 0);
            socket.getOutputStream().flush();

            if (download) {
                fireProcessBegin(true);
                fileOutputStream = new BufferedOutputStream(
                        new FileOutputStream(file), FILE_BUFFER_SIZE);

                currentTime = System.nanoTime();
                read = inputstream.read(buffer);
                transfertTime += (System.nanoTime() - currentTime);

                while (read > 0) {
                    currentTime = System.nanoTime();
                    fileOutputStream.write(buffer, 0, read);
                    fileTime += (System.nanoTime() - currentTime);
                    receved += read;
                    double dt = receved / size * 100.0;
                    firePercentChanged((int) (dt));
                    if (!running) {
                        break;
                    }
                    currentTime = System.nanoTime();
                    read = inputstream.read(buffer);
                    transfertTime += (System.nanoTime() - currentTime);
                }
            }
        } catch (IOException e) {
            CommonLogger.error("FileTransfert.loadFile IOException for ip "
                    + addressIP + " : " + e.getMessage());
            CommonLogger.error(e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    CommonLogger.error(e);
                }
            }

            try {
                socket.close();
            } catch (IOException e) {
                CommonLogger.error(e);
            }
        }

//        if(file.length() != size){
//            file.delete();
//        }

        running = false;
        fireProcessEnded(0);

        currentTime = System.nanoTime();
        double global = (currentTime - initTime);
        long secondToNano = 1000000;
        CommonLogger.info("receive file global: " + global / secondToNano
                + " writeFileTime: " + fileTime / secondToNano
                + " transfertTime: " + transfertTime / secondToNano
                + " file bytes: " + size
                + " taux global (o/ms): " + size / (global / secondToNano)
                + " taux transfert (o/ms): " + size / (transfertTime / secondToNano)
                + " isdownload: " + download);
    }

    /**
     * Démarrage d'envoi d'un fichier.
     *
     * @param file le fichier.
     * @param port le port du serveur.
     * @param nbClient le nombre de client.
     */
    public void sendFile(File file, int port, int nbClient) {
        if (!file.exists()) {
            return;
        }

        running = true;

        long initTime = System.nanoTime();
        double transfertTime = 0;
        double fileTime = 0;
        double currentTime;
        int sendedClient = 0;

        ServerSocket serverSocket;
        Socket socket = null;
        OutputStream send;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(Constants.TIME_MAX_FOR_ORDER);
        } catch (IOException e) {
            CommonLogger.error(e);
            return;
        }

        int read;
        long taille = file.length();
        byte[] buffer = new byte[BUFFER_SIZE];
        InputStream fileInputStream = null;

        fireDoubleStatusChanged((nbClient > 1));
        fireMessageChanged(file.getName());
        fireProcessBegin(true);

        for (int i = 0; i < nbClient; i++) {
            try {
                CommonLogger.debug("FileTransfert.sendFile try send for client " + i);
                initTime = System.currentTimeMillis();
                socket = serverSocket.accept();
                currentTime = System.currentTimeMillis();
                CommonLogger.debug("FileTransfert.sendFile file attente de "
                        + (currentTime - initTime));

                if (bufferedSocket) {
                    send = new BufferedOutputStream(socket.getOutputStream(),
                            SOCKET_BUFFER_SIZE);
                } else {
                    send = socket.getOutputStream();
                }

                int ch = socket.getInputStream().read();
                boolean transfert = (ch > 0);

                if (!transfert) {
                    socket.close();
                    socket = null;
                    CommonLogger.debug("FileTransfert.sendFile client " + i + " has file");
                    continue;
                }

                initTime = System.nanoTime();
                fileInputStream = new BufferedInputStream(
                        new FileInputStream(file), FILE_BUFFER_SIZE);

                double sended = 0.0;
                currentTime = System.nanoTime();
                read = fileInputStream.read(buffer);
                fileTime += (System.nanoTime() - currentTime);
                while (read > 0) {
                    currentTime = System.nanoTime();
                    send.write(buffer, 0, read);
                    transfertTime += (System.nanoTime() - currentTime);
                    sended += read;
                    double dt = sended / taille * 100.0;
                    double total = (dt + i * 100) / nbClient;
                    firePercentChanged((int) (total), (int) (dt));
                    if (!running) {
                        break;
                    }
                    currentTime = System.nanoTime();
                    read = fileInputStream.read(buffer);
                    fileTime += (System.nanoTime() - currentTime);
                }

                currentTime = System.nanoTime();
                send.flush();
                transfertTime += (System.nanoTime() - currentTime);

                sendedClient++;
            } catch (IOException e) {
                CommonLogger.error("FileTransfert.sendFile IOException for client "
                        + i + " : " + e.getMessage());
                CommonLogger.error(e);
            } finally {
                if (fileInputStream != null) {
                    try {
                        // Fermeture fichier
                        fileInputStream.close();
                        fileInputStream = null;
                    } catch (IOException e) {
                        CommonLogger.error(e);
                    }
                }

                if (socket != null) {
                    try {
                        socket.close();
                        socket = null;
                    } catch (IOException e) {
                        CommonLogger.error(e);
                    }
                }
            }

            if (!running) {
                break;
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            CommonLogger.error(e);
        }

        running = false;
        fireProcessEnded(0);

        currentTime = System.nanoTime();
        double global = (currentTime - initTime);
        long secondToNano = 1000000;
        CommonLogger.info("Transfert file global: " + global / secondToNano
                + " readFileTime: " + fileTime / secondToNano
                + " transfertTime: " + transfertTime / secondToNano
                + " nb: " + nbClient + " nb send: " + sendedClient
                + " average global: " + (global / secondToNano / sendedClient)
                + " average fileTime: " + (fileTime / secondToNano / sendedClient)
                + " average transfert: " + (transfertTime / secondToNano / sendedClient)
                + " file bytes: " + taille
                + " taux global (o/ms): " + taille / (global / secondToNano / sendedClient)
                + " taux transfert (o/ms): " + taille / (transfertTime / secondToNano / sendedClient));
    }
}
