package thot.supervision;

import java.text.ParseException;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.utils.Utilities;

/**
 * Fenêtre pour des réglages.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class ReglageDialog {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReglageDialog.class);

    /**
     * Référence du noyau de l'application.
     */
    private MasterCore noyau;
    /**
     * Fenêtre principale des réglages.
     */
    private JFrame dialog;
    /**
     * Champ pour la qualité de compression.
     */
    private JFormattedTextField qualityField;
    /**
     * Champ pour le nombre d'images par seconde.
     */
    private JFormattedTextField fpsField;
    /**
     * Champ pour le nombre de lignes lors d'envoi d'écran.
     */
    private JFormattedTextField linesField;
    /**
     * Champ pour le temps d'attente maximum général.
     */
    private JFormattedTextField timeoutField;
    /**
     * Champ pour le temps d'attente sur la mosaïque.
     */
    private JFormattedTextField mosaiqueTimeoutField;
    /**
     * Champ pour le temps d'attente entre 2 envois d'ordre sur la mosaïque.
     */
    private JFormattedTextField mosaiqueDelayField;
    /**
     * Champ pour le nombre d'images par seconde sur la mosaïque.
     */
    private JFormattedTextField mosaiqueFpsField;
    /**
     * Champ pour le temps d'attente sur l'envoi d'écran élève.
     */
    private JFormattedTextField studentTimeoutField;

    /**
     * Initiliastion.
     *
     * @param noyau la référence du noyau de l'application.
     */
    public ReglageDialog(MasterCore noyau) {
        this.noyau = noyau;

        dialog = new JFrame();
        dialog.setTitle("Tests Réglages");
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        try {
            MaskFormatter qualityFormat = new MaskFormatter("###");
            qualityFormat.setPlaceholderCharacter('0');
            MaskFormatter fpsFormat = new MaskFormatter("##");
            fpsFormat.setPlaceholderCharacter('0');
            MaskFormatter linesFormat = new MaskFormatter("##");
            linesFormat.setPlaceholderCharacter('0');
            MaskFormatter timeoutFormat = new MaskFormatter("####");
            timeoutFormat.setPlaceholderCharacter('0');
            MaskFormatter mosaiqueTimeoutFormat = new MaskFormatter("####");
            mosaiqueTimeoutFormat.setPlaceholderCharacter('0');
            MaskFormatter mosaiqueDelayFormat = new MaskFormatter("####");
            mosaiqueDelayFormat.setPlaceholderCharacter('0');
            MaskFormatter studentTimeoutFormat = new MaskFormatter("####");
            studentTimeoutFormat.setPlaceholderCharacter('0');

            qualityField = new JFormattedTextField(qualityFormat);
            fpsField = new JFormattedTextField(fpsFormat);
            linesField = new JFormattedTextField(linesFormat);
            timeoutField = new JFormattedTextField(timeoutFormat);

            mosaiqueTimeoutField = new JFormattedTextField(mosaiqueTimeoutFormat);
            mosaiqueDelayField = new JFormattedTextField(mosaiqueDelayFormat);
            mosaiqueFpsField = new JFormattedTextField();
            studentTimeoutField = new JFormattedTextField(studentTimeoutFormat);
        } catch (ParseException e) {
            LOGGER.error("", e);
        }

        JLabel messageLabel = new JLabel("<html>"
                + "quality: de 0 à 100 (defaut: 80)<br>"
                + "fps: de 1 à 40 (defaut: 20)<br>"
                + "lines: de 1 à 64 (defaut: 32)<br>"
                + "timeout: de 0 à 2000 (defaut: 100 ms)<br>"
                + "mosaique timeout: de 0 à 2000 (defaut: 100 ms)<br>"
                + "mosaique delay: de 0 à 2000 (defaut: 30 ms)<br>"
                + "mosaique fps: de 0.1 à 20 (defaut: 0.2 ms)<br>"
                + "ecran eleve timeout: de 0 à 2000 (defaut: 100 ms)<br>"
                + "</html>");

        JButton validButton = new JButton("Valider");
        validButton.addActionListener(e -> valide());

        dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        dialog.getContentPane().add(messageLabel);
        dialog.getContentPane().add(new JLabel(" "));
        dialog.getContentPane().add(new JLabel("Quality:"));
        dialog.getContentPane().add(qualityField);
        dialog.getContentPane().add(new JLabel("fps:"));
        dialog.getContentPane().add(fpsField);
        dialog.getContentPane().add(new JLabel("lines:"));
        dialog.getContentPane().add(linesField);
        dialog.getContentPane().add(new JLabel("timeout:"));
        dialog.getContentPane().add(timeoutField);
        dialog.getContentPane().add(new JLabel("mosaique timeout:"));
        dialog.getContentPane().add(mosaiqueTimeoutField);
        dialog.getContentPane().add(new JLabel("mosaique delay:"));
        dialog.getContentPane().add(mosaiqueDelayField);
        dialog.getContentPane().add(new JLabel("mosaique fps:"));
        dialog.getContentPane().add(mosaiqueFpsField);
        dialog.getContentPane().add(new JLabel("ecran eleve timeout:"));
        dialog.getContentPane().add(studentTimeoutField);
        dialog.getContentPane().add(new JLabel(" "));
        dialog.getContentPane().add(validButton);
        dialog.pack();
    }

    /**
     * Affiche la fenêtre de régalges.
     */
    public void showDialog() {
        updateFiels();
        dialog.setVisible(true);
    }

    /**
     * Mise à jour des différents champs.
     */
    private void updateFiels() {
        qualityField.setText(String.format("%1$03d", noyau.getQuality()));
        fpsField.setText(String.format("%1$02d", (int) noyau.getFps()));
        linesField.setText(String.format("%1$02d", noyau.getLines()));
        timeoutField.setText(String.format("%1$04d", noyau.getTimeout()));

        mosaiqueTimeoutField.setText(String.format("%1$04d", noyau.getMosaiqueTimeout()));
        mosaiqueDelayField.setText(String.format("%1$04d", noyau.getMosaiqueDelay()));
        mosaiqueFpsField.setText(String.format("%1$02.2f", noyau.getMosaiqueFps()));
        studentTimeoutField.setText(String.format("%1$04d", noyau.getStudentTimeout()));
    }

    /**
     * Validation des différents champs.
     */
    private void valide() {
        int quality = Utilities.parseStringAsInt(qualityField.getText());
        int fps = Utilities.parseStringAsInt(fpsField.getText());
        int lines = Utilities.parseStringAsInt(linesField.getText());
        int timeout = Utilities.parseStringAsInt(timeoutField.getText());

        int mosaiqueTimeout = Utilities.parseStringAsInt(mosaiqueTimeoutField.getText());
        int mosaiqueDelay = Utilities.parseStringAsInt(mosaiqueDelayField.getText());
        double mosaiqueFps = Utilities.parseStringAsDouble(fpsField.getText());
        int studentTimeout = Utilities.parseStringAsInt(studentTimeoutField.getText());

        if (quality < 0 || quality > 100) {
            quality = 80;
        }
        if (fps < 1 || fps > 40) {
            fps = 20;
        }
        if (lines < 1 || lines > 64) {
            lines = 32;
        }
        if (timeout <= 0 || timeout > 2000) {
            timeout = 100;
        }

        if (mosaiqueTimeout <= 0 || mosaiqueTimeout > 2000) {
            mosaiqueTimeout = 100;
        }
        if (mosaiqueDelay < 0 || mosaiqueDelay > 2000) {
            mosaiqueDelay = 30;
        }
        if (mosaiqueFps <= 0 || mosaiqueFps > 20) {
            mosaiqueFps = 0.2;
        }
        if (studentTimeout <= 0 || studentTimeout > 2000) {
            studentTimeout = 100;
        }

        noyau.setTimeout(timeout);
        noyau.setParameters(quality, fps, lines);
        noyau.setMosaiqueParameters(mosaiqueTimeout, mosaiqueDelay, mosaiqueFps);
        noyau.setSendStudentTimeout(studentTimeout);
        updateFiels();
    }
}
