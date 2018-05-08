package thot.supervision;

import thot.supervision.screen.ScreenWindow;

/**
 * Fenêtre pour afficher l'écran d'un élève.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class StudentScreenWindow extends ScreenWindow {
    private static final long serialVersionUID = 19000L;

    /**
     * Référence sur le gestionnaire des élèves.
     */
    private MasterCore core;

    /**
     * Initialisation.
     *
     * @param core référence sur le gestionnaire des élèves.
     * @param keyboardAndMousePort le port de communication pour le control du clavier et de la souris.
     */
    public StudentScreenWindow(MasterCore core, int keyboardAndMousePort) {
        super(true, keyboardAndMousePort);
        this.core = core;
    }

    @Override
    protected void closeCommand() {
        sendClose();
        core.closeRemoteScreen();
    }
}
