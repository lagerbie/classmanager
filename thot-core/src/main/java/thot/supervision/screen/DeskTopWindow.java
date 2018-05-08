package thot.supervision.screen;

import java.awt.*;

/**
 * Classe bloquant le clavier et la souris.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class DeskTopWindow extends BlockWindow {
    private static final long serialVersionUID = 19000L;

    @Override
    public void paint(Graphics g) {
        g.drawImage(null, 0, 0, null);
    }
}
