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
package thot.supervision.com;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.utils.Constants;
import thot.utils.Utilities;

/**
 * Thread pour la recherche des élèves.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class StudentSearch implements Runnable {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentSearch.class);

    /**
     * Adresse multicast pour la recherche des élèves.
     */
    private String multicastIP;
    /**
     * Port de l'adresse multicast pour la recherche des élèves.
     */
    private int multicastPort;
    /**
     * Temps d'attente entre 2 envois de trame multicast.
     */
    private final long WAIT_TIME = Constants.TIME_MAX_FOR_ORDER;

    /**
     * Initialisation avec remise à zéro du fichier de sauvegarde.
     *
     * @param multicastIP adresse multicast pour la recherche du professeur.
     * @param multicastPort port pour la recherche du professeur.
     */
    public StudentSearch(String multicastIP, int multicastPort) {
        this.multicastIP = multicastIP;
        this.multicastPort = multicastPort;
    }

    /**
     * Démarrage de la thread de la recherche de professeur.
     */
    public void start() {
        new Thread(this, this.getClass().getName()).start();
    }

    /**
     * Recherche des élèves.
     */
    @Override
    public void run() {
        InetAddress group;
        MulticastSocket multicastSocket;
        byte[] data;

        try {
            group = InetAddress.getByName(multicastIP);
            multicastSocket = new MulticastSocket(multicastPort);
            multicastSocket.setTimeToLive(3);
            multicastSocket.joinGroup(group);
            data = Constants.XML_STUDENT_SEARCH.getBytes("UTF-8");
        } catch (IOException e) {
            LOGGER.error("", e);
            return;
        }

        DatagramPacket packet = new DatagramPacket(data, data.length, group, multicastPort);

        while (true) {
            Utilities.waitInMillisecond(WAIT_TIME);
            try {
                multicastSocket.send(packet);
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
    }
}
