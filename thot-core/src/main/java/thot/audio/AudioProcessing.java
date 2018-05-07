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
package thot.audio;

import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.swing.event.EventListenerList;

import lombok.Getter;

/**
 * Classe de traitement général.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public abstract class AudioProcessing implements Runnable {

    /**
     * Format Audio pour la capture du microphone.
     */
    @Getter
    private AudioFormat audioFormat;
    /**
     * Stockage des données audio à enregistrer.
     */
    private ByteBuffer recordBuffer;

    /**
     * Mode actif.
     */
    private boolean run;
    /**
     * Thread du procesus.
     */
    private Thread thread;
    /**
     * Gestionnaire des Listener.
     */
    private final EventListenerList listenerList = new EventListenerList();

    /**
     * Temps courrant.
     */
    private long currentTime;
    /**
     * Temps d'arrêt.
     */
    private long stopTime;

    /**
     * Taille des buffers utilisés. x8 pour XP 2 canaux à 44100Hz
     */
    public static final int BUFFER_SIZE = 1024 * 8;
    /**
     * Buffer d'échange.
     */
    private byte data[] = new byte[BUFFER_SIZE];

    /**
     * Initialisation avec un format audio et une référence sur le buffer où seront enregistrées les données pour un
     * mode indirect.
     *
     * @param recordBuffer le buffer de stockage.
     * @param audioFormat le format audio.
     */
    public AudioProcessing(ByteBuffer recordBuffer, AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
        this.recordBuffer = recordBuffer;
    }

    /**
     * Ajoute un listener.
     *
     * @param listener le listener à ajouter.
     */
    public void addListener(TimeProcessingListener listener) {
        listenerList.add(TimeProcessingListener.class, listener);
    }

    /**
     * Supprime un listener.
     *
     * @param listener le listener à supprimer.
     */
    public void removeListener(TimeProcessingListener listener) {
        listenerList.remove(TimeProcessingListener.class, listener);
    }

    /**
     * Notification que le temps à changé.
     *
     * @param time le nouveau temps.
     */
    protected void fireTimeChanged(long time) {
        for (TimeProcessingListener listener : listenerList.getListeners(TimeProcessingListener.class)) {
            listener.timeChanged(time);
        }
    }

    /**
     * Appel lors de la fin du traitement.
     *
     * @param running indique si le mode est toujours actif.
     */
    protected void fireEndProcess(boolean running) {
        for (TimeProcessingListener listener : listenerList.getListeners(TimeProcessingListener.class)) {
            listener.endProcess(running);
        }
    }

    /**
     * Démarre l'enregistrement des données.
     *
     * @param initTime le temps de départ.
     * @param stopTime le temps de fin.
     */
    public void start(long initTime, long stopTime) {
        this.currentTime = initTime;
        this.stopTime = stopTime;
        start();
    }

    /**
     * Démarre le processus.
     */
    private void start() {
        run = true;
        thread = new Thread(this, this.getClass().getName());
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        thread.setPriority(Thread.MAX_PRIORITY);
    }

    /**
     * Arrête le processus.
     */
    public void stop() {
        run = false;
    }

    /**
     * Etat normal de processus.
     *
     * @return si le processus n'a pas été arrêté.
     */
    public boolean isRun() {
        return run;
    }

    /**
     * Retourne si le processus est actif.
     *
     * @return si le processus est actif.
     */
    public boolean isAlive() {
        return thread != null && thread.isAlive();
    }

    /**
     * Fermeture de la ligne.
     */
    public abstract void close();

    /**
     * Traitement spécifique des données sur la socket connecté.
     *
     * @param recordBuffer
     * @param data le tableau où seront les données.
     * @param offset l'offset de départ.
     * @param length le nombre de bytes à lire.
     *
     * @return
     */
    protected abstract int process(ByteBuffer recordBuffer, byte[] data,
            int offset, int length);

    protected abstract void endProcess();

    @Override
    public void run() {
        //repositionnement de la tête d'enregistrement pour sauvegarder
        //les données avant la frame courante.
        int samplePosition = (int) (currentTime / 1000.0f * audioFormat.getSampleRate());
        int offset = samplePosition * audioFormat.getFrameSize();
        recordBuffer.position(offset);

        //nombres de bytes à lire
        int nbSamples = (int) ((stopTime - currentTime) / 1000.0f * audioFormat.getSampleRate());
        int nbBytes = nbSamples * audioFormat.getFrameSize();

        //nombres de bytes à lire
        int read = Math.min(nbBytes, BUFFER_SIZE);

        long initTime = currentTime;
        long time = 0;

        //on boucle tant que l'état du module audio n'a pas été arrêté.
        while (isRun()) {
            read = process(recordBuffer, data, 0, read);

            //mise à jour du temps courant
            time = (long) (recordBuffer.position()
                    / audioFormat.getFrameSize()
                    / audioFormat.getSampleRate() * 1000.0f);

            if ((time - initTime) > 500) {
                initTime = time;
                fireTimeChanged(time);
            }

            nbBytes -= read;
            if (nbBytes < audioFormat.getFrameSize()) {
                //si inférieur à la taille d'une frame on arrête
                break;
            } else {
                //mise à jour du nombres de bytes à lire
                read = Math.min(nbBytes, BUFFER_SIZE);
            }
        }

        endProcess();
        fireTimeChanged(time);
        fireEndProcess(isRun());
    }
}
