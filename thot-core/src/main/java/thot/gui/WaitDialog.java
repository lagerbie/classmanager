package thot.gui;

import java.awt.*;

import javax.swing.*;

/**
 * Boîte de dialoque pour attendre.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class WaitDialog extends JDialog {
    private static final long serialVersionUID = 19000L;

    /**
     * Initialise et affiche la boite de dialogue.
     *
     * @param owner la fenêtre parente.
     * @param title le titre de la fenêtre.
     * @param message le message de la fenêtre.
     */
    public WaitDialog(Window owner, String title, String message) {
        super(owner, title);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setBackground(Color.LIGHT_GRAY);
        this.setResizable(false);

        JLabel label = new JLabel(message);
        this.getContentPane().setLayout(new FlowLayout());
        this.getContentPane().add(label);
        this.pack();

        //centrage de la fenêtre
        this.setLocation((owner.getWidth() - this.getWidth()) / 2, (owner.getHeight() - this.getHeight()) / 2);
    }
}
