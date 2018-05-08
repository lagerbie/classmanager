package thot;

import thot.labo.LaboListener;

/**
 * Listener pour écouter les changement d'état du coeur du laboratoire de langue.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public interface LaboratoryListener extends LaboListener {

    /**
     * Appelé quand la langue a été changée.
     *
     * @param language la nouvelle langue pour l'interface.
     */
    void languageChanged(String language);

    /**
     * Appelé quand le volume du module multimédia a changé.
     *
     * @param volume le nouveau volume en poucentage (de 0 à 100).
     */
    void mediaVolumeChanged(int volume);

    /**
     * Appelé quand le volume du module audio a changé.
     *
     * @param volume le nouveau volume en poucentage (de 0 à 100).
     */
    void audioVolumeChanged(int volume);

    /**
     * Appelé quand l'état des commandes de l'élève ont changé.
     *
     * @param freeze le nouvel état.
     */
    void studentControlChanged(boolean freeze);

    /**
     * Appelé pour afficher un message à l'écran.
     *
     * @param message le message à afficher.
     */
    void newMessage(String message);

    /**
     * Appelé quand une demande d'aide à été envoyée.
     *
     * @param success le succes de la commande.
     */
    void helpDemandSuccess(boolean success);
}
