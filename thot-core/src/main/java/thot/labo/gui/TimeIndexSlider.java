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
package thot.labo.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.*;

import thot.gui.GuiUtilities;
import thot.gui.Resources;
import thot.labo.index.Index;
import thot.labo.index.IndexProcessing;
import thot.utils.Constants;

/*
 * resources:
 *  playIndex
 *  recordIndex
 *  beginIndex
 *  endIndex
 *  eraseIndex
 *  eraseRecord
 *  eraseIndex&Record
 */

/**
 * Panel pour le conrole de la vitesse de défilement du texte.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public abstract class TimeIndexSlider extends JPanel {
    private static final long serialVersionUID = 19000L;

    /**
     * Noyau de l'application.
     */
    private IndexProcessing core;
    /**
     * Fenêtre parente.
     */
    private Window owner;
    /**
     * Resources textuelles de l'application.
     */
    private Resources resources;

    /**
     * Position minimale.
     */
    private int min;
    /**
     * Position maximale.
     */
    private int max;
    /**
     * Position du curseur.
     */
    private int position;
    /**
     * Position relative du curseur.
     */
    private double relativePosition;

    /**
     * Image pour de fond pour la piste élève.
     */
    private Image backgroundIndex;
    /**
     * Image pour de fond pour la piste élève.
     */
    private Image backgroundTime;
    /**
     * Image du curseur.
     */
    private Image cursor;
//    /** Image pour l'index d'enregistrement */
//    private ImageIcon recordIndex;
//    /** Image pour l'index de lecture */
//    private ImageIcon playIndex;

    private JMenu studentMenu;
    private JMenuItem playIndexMenuItem;
    private JMenuItem recordIndexMenuItem;
    private JMenuItem beginIndexMenuItem;
    private JMenuItem endIndexMenuItem;
    private JMenuItem eraseIndexMenuItem;
    private JMenuItem eraseRecordMenuItem;
    private JMenuItem eraseIndexRecordMenuItem;

    /**
     * Initialisation du slider pour le temps.
     *
     * @param core une référence du coeur de l'application.
     * @param owner la frame parente.
     * @param resources
     * @param width la largeur du slider.
     */
    public TimeIndexSlider(final IndexProcessing core, final Window owner, Resources resources, int width) {
        this.owner = owner;
        this.core = core;
        this.resources = resources;

        backgroundTime = GuiUtilities.getImage("timeImage");
        backgroundIndex = GuiUtilities.getImage("indexImage");
        cursor = GuiUtilities.getImage("timeCursorImage");
//        recordIndex = resources.getImageIcon("recordIndexImage");
//        playIndex = resources.getImageIcon("playIndexImage");

        Dimension dim = new Dimension(width, cursor.getHeight(null));
        setPreferredSize(dim);
        setMinimumSize(dim);
        setMaximumSize(dim);

        min = -cursor.getWidth(null) / 2;
        max = width - cursor.getWidth(null) / 2;

        setPosition(min);

        initPopupMenu();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (isEnabled() && SwingUtilities.isLeftMouseButton(event) && event.getClickCount() > 1) {
                    relativePosition = (double) event.getX() / getWidth();
                    if (core.getRunningState() == Constants.PAUSE && core.onIndex(relativePosition)) {

                        boolean studentIndex = core.onStudentIndex(relativePosition);
                        eraseIndexMenuItem.setEnabled(studentIndex);
                        eraseRecordMenuItem.setEnabled(studentIndex);
                        eraseIndexRecordMenuItem.setEnabled(studentIndex);

                        studentMenu.getPopupMenu().show(event.getComponent(), event.getX(), event.getY());
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent event) {
                if (!isEnabled()) {
                    return;
                }

                setPosition(event.getX());
                double relativePosition = (double) (position - min) / (max - min);
                releasedPosition(relativePosition);
            }

            @Override
            public void mouseExited(MouseEvent event) {
                owner.repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent event) {
                if (!isEnabled()) {
                    return;
                }

                setPosition(event.getX());
                double relativePosition = (double) (position - min) / (max - min);
                draggedPosition(relativePosition);
            }
        });
    }

    /**
     * Modifie la position relative.
     *
     * @param relativePosition
     */
    protected void setRelativePosition(double relativePosition) {
        this.relativePosition = relativePosition;
    }

    /**
     * Change la position du curseur.
     *
     * @param position la position relative entre 0 et 1.
     */
    public void setPosition(double position) {
        double positionX = position * (max - min) + min;
        setPosition((int) positionX);
    }

    /**
     * Change la postion du curseur.
     *
     * @param mousePosition la position du curseur.
     */
    private void setPosition(int mousePosition) {
        position = mousePosition;
        if (position < min) {
            position = min;
        }
        if (position > max) {
            position = max;
        }
        repaint();
        owner.repaint();
    }

    /**
     * Appeler quand la souris est relachée.
     *
     * @param relativePosition la position relative.
     */
    abstract public void releasedPosition(double relativePosition);

    /**
     * Appeler quand la souris est glissée.
     *
     * @param relativePosition la position relative.
     */
    abstract public void draggedPosition(double relativePosition);

    /**
     * Initialise le popup menu de la bande d'index.
     */
    private void initPopupMenu() {
        studentMenu = new JMenu();

        //Action pour lire l'index
        playIndexMenuItem = new JMenuItem(resources.getString("playIndex"));
        playIndexMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                core.playOnIndex(relativePosition);
            }
        });
        studentMenu.add(playIndexMenuItem);

        //Action pour enregistrer sur l'index
        recordIndexMenuItem = new JMenuItem(resources.getString("recordIndex"));
        recordIndexMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                core.recordOnIndex(relativePosition);
            }
        });
        studentMenu.add(recordIndexMenuItem);

        studentMenu.addSeparator();

        //Action pour aller au début de l'index
        beginIndexMenuItem = new JMenuItem(resources.getString("beginIndex"));
        beginIndexMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                core.setTimeBeginIndex(relativePosition);
            }
        });
        studentMenu.add(beginIndexMenuItem);

        //Action pour aller à la fin de l'index
        endIndexMenuItem = new JMenuItem(resources.getString("endIndex"));
        endIndexMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                core.setTimeEndIndex(relativePosition);
            }
        });
        studentMenu.add(endIndexMenuItem);

        studentMenu.addSeparator();

        //Action pour effacer l'index
        eraseIndexMenuItem = new JMenuItem(resources.getString("eraseIndex"));
        eraseIndexMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                core.eraseIndex(relativePosition);
            }
        });
        studentMenu.add(eraseIndexMenuItem);

        //Action pour effacer l'enregistrement
        eraseRecordMenuItem = new JMenuItem(resources.getString("eraseRecord"));
        eraseRecordMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                core.eraseIndexRecord(relativePosition);
            }
        });
        studentMenu.add(eraseRecordMenuItem);

        //Action pour effacer l'enregistrement
        eraseIndexRecordMenuItem = new JMenuItem(resources.getString("eraseIndex&Record"));
        eraseIndexRecordMenuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent event) {
                core.eraseIndexRecord(relativePosition);
                core.eraseIndex(relativePosition);
            }
        });
        studentMenu.add(eraseIndexRecordMenuItem);

        studentMenu.getPopupMenu().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent event) {
                owner.repaint();
            }

            @Override
            public void mouseExited(MouseEvent event) {
                if (!studentMenu.getPopupMenu().contains(event.getPoint())) {
                    studentMenu.getPopupMenu().setVisible(false);
                }
                owner.repaint();
            }
        });
    }

    /**
     * Modifie les différents texte suivant la ressource.
     */
    public void changePopupMenuText() {
        //Action pour lire l'index
        playIndexMenuItem.setText(resources.getString("playIndex"));
        //Action pour enregistrer sur l'index
        recordIndexMenuItem.setText(resources.getString("recordIndex"));
        //Action pour aller au début de l'index
        beginIndexMenuItem.setText(resources.getString("beginIndex"));
        //Action pour aller à la fin de l'index
        endIndexMenuItem.setText(resources.getString("endIndex"));
        //Action pour effacer l'index
        eraseIndexMenuItem.setText(resources.getString("eraseIndex"));
        //Action pour effacer l'enregistrement
        eraseRecordMenuItem.setText(resources.getString("eraseRecord"));
        //Action pour effacer l'enregistrement
        eraseIndexRecordMenuItem.setText(resources.getString("eraseIndex&Record"));
    }

    /**
     * Reourne le menu.
     *
     * @return
     */
    public JMenu getStudentMenu() {
        return studentMenu;
    }

    @Override
    public void paintComponent(Graphics g) {
        int indexOffset = 8;
        int timeOffset = 30;
        int lenght = 8;

        int width = this.getWidth();
        int height = this.getHeight();
        double recordMax = core.getRecordTimeMax();

        Color recordColor = new Color(255, 0, 0, 150);// = Color.RED;
        Color playColor = new Color(0, 255, 0, 150);// = Color.GREEN;
//        Color blankColor = new Color(0, 0, 255, 150);
//        Color masterColor = new Color(0, 255, 255, 150);

        g.drawImage(backgroundIndex, 0, indexOffset - 1, null);
        g.drawImage(backgroundTime, 0, timeOffset - 1, null);

        //Index d'enregistrement
        for (Iterator<Index> it = core.recordIndexIterator(); it.hasNext(); ) {
            Index index = it.next();

            int x1 = (int) (width / recordMax * index.getInitialTime());
            int x2 = (int) (width / recordMax * index.getFinalTime());

            g.setColor(recordColor);
            g.drawLine(x1, timeOffset - lenght, x1, height);

            if (x2 > 0) {
                g.fillRect(x1, timeOffset - lenght, x2 - x1, 2 * lenght + 1);

                g.setColor(Color.BLACK);
                g.drawLine(x1, timeOffset - lenght, x1, height);
                g.drawLine(x2, timeOffset - lenght, x2, height);
//                g.drawImage(recordIndex.getImage(), x1, timeOffset-lenght, x2-x1, 2*lenght+1, null);
            }
        }

        //Index du fichier multimédia
        for (Iterator<Index> it = core.mediaIndexIterator(); it.hasNext(); ) {
            Index index = it.next();
//            ImageIcon imageIcon = null;
            if (index.isStudentRecord()) {
//                imageIcon = recordIndex;
                g.setColor(recordColor);
            } else {
//                imageIcon = playIndex;
                g.setColor(playColor);
            }

            int x1 = (int) (width / recordMax * index.getInitialTime());
            int x2 = (int) (width / recordMax * index.getFinalTime());

            g.drawLine(x1, 0, x1, indexOffset + lenght);

            if (x2 > 0) {
                g.fillRect(x1, indexOffset - lenght, x2 - x1, 2 * lenght + 1);

                g.setColor(Color.BLACK);
                g.drawLine(x1, 0, x1, indexOffset + lenght);
                g.drawLine(x2, 0, x2, indexOffset + lenght);
//                g.drawImage(imageIcon.getImage(), x1, indexOffset-lenght, x2-x1, 2*lenght+1, null);
            }
        }

        //curseur
        g.drawImage(cursor, position, 0, null);
    }
}
