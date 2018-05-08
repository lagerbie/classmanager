package thot.supervision.gui;

import java.awt.*;

import javax.swing.*;

import thot.gui.GuiUtilities;
import thot.gui.ImagePanel;

/**
 * Composant pour afficher l'état de la batterie.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class Battery extends JPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Composant pour afficher le pourcentage.
     */
    private JLabel percent;

    /**
     * Initialisation avec l'image de la batterie.
     *
     * @param batteryImage l'image de la batterie.
     */
    public Battery(Image batteryImage) {
        super();
        ImagePanel battery = new ImagePanel(batteryImage);

        percent = new JLabel(String.format("%1$d %%", 100));
        percent.setHorizontalAlignment(JLabel.LEFT);
        percent.setBackground(GuiUtilities.TRANSPARENT_COLOR);
        percent.setForeground(Color.WHITE);

        LayoutManager layout = new FlowLayout(FlowLayout.LEFT, 5, 5);
        this.setLayout(layout);

        this.add(battery);
        this.add(percent);
    }

    /**
     * Modifie le pourcentage affiché.
     *
     * @param percent le nouveau pourcentage.
     */
    public void setPercent(int percent) {
        this.percent.setText(String.format("%1$d %%", percent));
    }
}
