package thot.utils;

import javax.swing.event.EventListenerList;

/**
 * Gestion de listeners.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public abstract class ProgressThread {

    /**
     * Liste d'écouteur pour répercuter les évènements du convertisseur.
     */
    private final EventListenerList listeners;

    /**
     * Initialisation.
     */
    protected ProgressThread() {
        listeners = new EventListenerList();
    }

    /**
     * Ajoute d'une écoute de type ProgessListener.
     *
     * @param listener l'écoute à ajouter.
     */
    public void addListener(ProgressListener listener) {
        listeners.add(ProgressListener.class, listener);
    }

    /**
     * Notification du début du traitement.
     *
     * @param determinated le status déterminé du process.
     */
    protected void fireProcessBegin(boolean determinated) {
        for (ProgressListener listener : listeners.getListeners(ProgressListener.class)) {
            listener.processBegin(this, determinated);
        }
    }

    /**
     * Notification de fin du traitement.
     *
     * @param exit la façon dont il est sorti.
     */
    protected void fireProcessEnded(int exit) {
        for (ProgressListener listener : listeners.getListeners(ProgressListener.class)) {
            listener.processEnded(this, exit);
        }
    }
}
