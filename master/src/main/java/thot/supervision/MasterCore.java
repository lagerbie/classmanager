package thot.supervision;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Iterator;

import javax.swing.event.EventListenerList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.exception.ThotException;
import thot.gui.Resources;
import thot.supervision.application.Application;
import thot.supervision.application.ApplicationUtilities;
import thot.supervision.application.ApplicationsDialog;
import thot.supervision.application.ApplicationsList;
import thot.supervision.gui.GuiConstants;
import thot.supervision.screen.CaptureScreen;
import thot.supervision.screen.ScreenWindow;
import thot.supervision.thumb.ProcessMosaique;
import thot.supervision.voip.Voip;
import thot.utils.Constants;
import thot.utils.FileTransfert;
import thot.utils.ProgressListener;
import thot.utils.ThotPort;
import thot.utils.Utilities;
import thot.utils.XMLUtilities;

/**
 * Coeur de la supervision.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
public class MasterCore implements Runnable {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterCore.class);

    /**
     * Mode normal.
     */
    public static final int NORMAL_MODE = 0;
    /**
     * Mode en écoute discrete.
     */
    private static final int LISTENING_MODE = 1;
    /**
     * En prise en main.
     */
    private static final int CONTROL_MODE = 2;
    /**
     * En en envoi d'écran élève.
     */
    private static final int STUDENT_MODE = 3;
    /**
     * En en scrutation automatique.
     */
    public static final int SCANNING_MODE = 4;
    /**
     * En en scrutation automatique.
     */
    public static final int MOSAIQUE_MODE = 5;
    /**
     * Mode Programmation du Pairing.
     */
    private static final int PAIRING_PROGRAMMATION = 1;
    /**
     * Mode Pairing actif.
     */
    private static final int PAIRING_ACTIF = 2;

    /**
     * Liste des apprenants.
     */
    private StudentClass studentClass;

    /**
     * Sauvegarde des écoutes sur différents éléments.
     */
    private EventListenerList listeners;
    /**
     * Adresse du professeur.
     */
    private String tuteurIP;

    /**
     * Etat du Pairing.
     */
    private int pairingMode = NORMAL_MODE;
    /**
     * Etat de visualisation.
     */
    private int visualisationMode = NORMAL_MODE;
    /**
     * Elève sélectionné.
     */
    private Student selectedStudent = null;
    /**
     * Sauvegarde du premier élève sélectionné pour le pairing.
     */
    private Student pairingFirstSelected = null;

    /**
     * Indice pour la scrutation automatique.
     */
    private Student scanningStudent = null;
    /**
     * Temps d'affichage d'un élève lors de la scrutation automatique.
     */
    private int scanningTime = 10000;
    /**
     * Thread pour la scrutation automatique.
     */
    private Thread scanningThread;

    /**
     * Serveur d'écoute et client d'envoi du microphone principal.
     */
    private Voip chatVoip;
    /**
     * Fenêtre pour afficher un écran distant.
     */
    private ScreenWindow screenWindow;
    /**
     * Capture et envoi l'écran professeur.
     */
    private CaptureScreen captureScreen;
    /**
     * Affichage de la mosaïque avec des processus séparés.
     */
    private ProcessMosaique mosaiqueProcess;
    /**
     * Gestionnaire de transfert de fichiers.
     */
    private FileTransfert fileTransfert;

    /**
     * Liste des applications autorisées et interdites.
     */
    private ApplicationsList applicationsList;
    /**
     * Fichier de sauvegarde de la liste des applications.
     */
    private File applicationsFile;
    /**
     * Fenêtre de gestion des applications.
     */
    private ApplicationsDialog applicationsDialog;

    /**
     * Quality de l'image JPEG envoyée.
     */
    private int quality = 80;
    /**
     * Nombre d'images par seconde.
     */
    private double fps = 20;
    /**
     * Nombre de lignes de la capture d'écran.
     */
    private int nbLines = 32;
    /**
     * Nombre d'images par seconde pour la mosaïque.
     */
    private double mosaiqueFps = 20;
    /**
     * Timeout pour la mosaïque.
     */
    private int mosaiqueTimeout = 100;
    /**
     * Delai entre les envoi d'ordre pour la mosaïque.
     */
    private long mosaiqueDelay = 50;
    /**
     * Timeout pour l'envoi d'écran élève.
     */
    private int studentTimeout = 100;

    /**
     * Initialisation du noyau en détection automatique des élèves.
     *
     * @param studentCountMax le nombre maximum d'élèves.
     * @param resources les resources textuelles.
     * @param thumbPath chemin de l'exécutable pour la gestion de la mosaique.
     * @param voip gestionnaire pour l'audio.
     */
    public MasterCore(int studentCountMax, Resources resources, File thumbPath, Voip voip) {
        this.chatVoip = voip;
        tuteurIP = Utilities.getAddress();

        listeners = new EventListenerList();
        studentClass = new StudentClass(studentCountMax);

        mosaiqueProcess = new ProcessMosaique(ThotPort.mosaiqueToThumbPortBase, ThotPort.thumbToMosaiquePort,
                thumbPath);

        screenWindow = new StudentScreenWindow(this, ThotPort.keyboardAndMousePort);
        captureScreen = new CaptureScreen(-1);

        fileTransfert = new FileTransfert();

        applicationsFile = new File(System.getProperty("user.home"), Constants.softNamePath + "/applicationsList.xml");
        if (applicationsFile.exists()) {
            applicationsList = ApplicationUtilities.loadApplicationsList(applicationsFile);
        } else {
            applicationsList = new ApplicationsList();
        }

        applicationsDialog = new ApplicationsDialog(resources, applicationsList) {
            private static final long serialVersionUID = 15000L;

            @Override
            protected void launchApplication(String path) throws ThotException {
                if (path != null) {
                    File file = new File(path);
                    String filePath = Utilities.pathWithoutWindowsProgramFiles(file);
                    if (filePath == null) {
                        filePath = path;
                    } else {
                        filePath = Constants.PROGAM_FILES + filePath;
                    }
                    Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.EXECUTE);
                    command.putParameter(CommandParamater.FILE, filePath);
                    sendXmlToSelected(command);
                }
            }

            @Override
            protected void applyModifications() throws ThotException {
                ApplicationUtilities.saveObject(applicationsList, applicationsFile);

                StringBuilder appList = new StringBuilder(1024);
                for (Iterator<Application> it = applicationsList.iterator(); it.hasNext(); ) {
                    Application application = it.next();

                    if (!application.isAllowed() && application.getPath() != null) {
                        File file = new File(application.getPath());
                        appList.append(XMLUtilities
                                .createElement(CommandParamater.APPLICATION.getParameter(), file.getName()));
                    }
                }

                Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.RECEIVE_INTREDICTION);
                if (appList.length() > 0) {
                    command.putParameter(CommandParamater.LIST, appList);
                }
                sendXmlToSelected(command);

                fireButtonStateChanged(GuiConstants.interdireApplication, true);
            }
        };

        applicationsDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                fireButtonStateChanged(GuiConstants.interdireApplication, true);
            }
        });

        ProgressListener listener = new ProgressListener() {
            @Override
            public void processBegin(Object source, boolean determinated) {
                LOGGER.info("begin: " + source + " -> " + determinated);
            }

            @Override
            public void processEnded(Object source, int exit) {
                processEndReached(source, exit);
            }

            @Override
            public void percentChanged(Object source, int percent) {

            }
        };

        screenWindow.addListener(listener);
        captureScreen.addListener(listener);

        WindowAdapter windowAdapter = new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                closeRemoteScreen();
            }
        };

        ComponentAdapter componentAdapter = new ComponentAdapter() {
            @Override
            public void componentHidden(ComponentEvent e) {
                closeRemoteScreen();
            }
        };

        mosaiqueProcess.addWindowListener(windowAdapter);
        mosaiqueProcess.addComponentListener(componentAdapter);
        mosaiqueProcess.addListener(listener);
    }

    /**
     * Ajoute d'une écoute de type MasterCoreListener.
     *
     * @param listener l'écoute à ajouter.
     */
    public void addListener(MasterCoreListener listener) {
        listeners.add(MasterCoreListener.class, listener);
        fileTransfert.addListener(listener);
    }

    /**
     * Modifie les paramètres du transfert d'écran.
     *
     * @param quality la qualité pour la compression JPEG.
     * @param fps le nombre d'images par seconde.
     * @param nbLines le nombre de lignes pour le découpage de l'écran.
     */
    public void setParameters(int quality, double fps, int nbLines) {
        this.quality = quality;
        this.fps = fps;
        this.nbLines = nbLines;

        captureScreen.setQuality(quality);
        captureScreen.setFPS(fps);
        captureScreen.setNbLines(nbLines);
    }

    /**
     * Modifie le temps d'attente maximum pour regarder si il y a de nouvelles données lors des transferts d'écran.
     *
     * @param timeout le temps d'attente maximum.
     */
    public void setTimeout(int timeout) {
        screenWindow.setTimeout(timeout);
        mosaiqueProcess.setTimeout(timeout);
    }

    /**
     * Retourne la qualité des images lors du transfert d'image en pourcentage.
     *
     * @return la qualité des images transmises.
     */
    public int getQuality() {
        return quality;
    }

    /**
     * Retourne le nombre d'images par seconde voulu lors de transfert d'écran.
     *
     * @return le nombre d'images par seconde voulu lors de transfert d'écran.
     */
    public double getFps() {
        return fps;
    }

    /**
     * Retourne le nombre de lignes formant les images lors de transfert d'écran.
     *
     * @return le nombre de lignes formant les images lors de transfert d'écran.
     */
    public int getLines() {
        return nbLines;
    }

    /**
     * Retourne le temps d'attente maximum pour regarder si il y a de nouvelles données lors des transferts d'écran.
     *
     * @return le temps d'attente maximum.
     */
    public int getTimeout() {
        return screenWindow.getTimeout();
    }

    /**
     * Modifie les paramètres de la mosaïque.
     *
     * @param timeout le temps d'attente maximum.
     * @param delay le délai entre l'envoi de 2 ordres.
     * @param fps le nombre d'images par seconde.
     */
    public void setMosaiqueParameters(int timeout, long delay, double fps) {
        this.mosaiqueTimeout = timeout;
        this.mosaiqueDelay = delay;
        this.mosaiqueFps = fps;
    }

    /**
     * Retourne le timeout de la mosaïque.
     *
     * @return le timeout en millisecondes.
     */
    public int getMosaiqueTimeout() {
        return mosaiqueTimeout;
    }

    /**
     * Retourne le timeout de la mosaïque.
     *
     * @return le timeout en millisecondes.
     */
    public long getMosaiqueDelay() {
        return mosaiqueDelay;
    }

    /**
     * Retourne le nombre d'images par seconde de la mosaïque.
     *
     * @return le nombre d'images par seconde.
     */
    public double getMosaiqueFps() {
        return mosaiqueFps;
    }

    /**
     * Modifie le timeout de l'envoi écran élève.
     *
     * @param studentTimeout le timeout en millisecondes.
     */
    public void setSendStudentTimeout(int studentTimeout) {
        this.studentTimeout = studentTimeout;
    }

    /**
     * Retourne le timeout de l'envoi écran élève.
     *
     * @return le timeout en millisecondes.
     */
    public int getStudentTimeout() {
        return studentTimeout;
    }

    /**
     * Retourene la liste des élèves.
     *
     * @return la liste des élèves.
     */
    public StudentClass getStudentClass() {
        return studentClass;
    }

    /**
     * Notification de l'ajout d'un élève.
     *
     * @param student l'élève ajouté.
     */
    private void fireStudentAdded(Student student) {
        for (MasterCoreListener listener : listeners.getListeners(MasterCoreListener.class)) {
            listener.studentAdded(this, student);
        }
    }

    /**
     * Notification de modification des données d'un élève.
     *
     * @param student l'élève modifié.
     */
    public void fireStudentChanged(Student student) {
        for (MasterCoreListener listener : listeners.getListeners(MasterCoreListener.class)) {
            listener.studentChanged(this, student);
        }
    }

    /**
     * Notification pour le changement d'état de bouton.
     *
     * @param name le nom du bouton.
     * @param state le nouvel état.
     *
     * @deprecated
     */
    @Deprecated
    private void fireButtonStateChanged(String name, boolean state) {
        for (MasterCoreListener listener : listeners.getListeners(MasterCoreListener.class)) {
            listener.buttonStateChanged(this, name, state);
        }
    }

    /**
     * Notification de la sélection d'un groupe.
     *
     * @param group le numéro du groupe.
     * @param selected l'état sélectionné.
     */
    private void fireGroupSelectionChanged(int group, boolean selected) {
        for (MasterCoreListener listener : listeners.getListeners(MasterCoreListener.class)) {
            listener.groupSelectionChanged(this, group, selected);
        }
    }

    /**
     * Notification du changement de l'élève sélectionné.
     *
     * @param newStudent le nouvel élève sélectionné.
     * @param oldStudent l'ancien élève sélectionné.
     */
    private void fireStudentSelectedChanged(Student newStudent, Student oldStudent) {
        for (MasterCoreListener listener : listeners.getListeners(MasterCoreListener.class)) {
            listener.studentSelectedChanged(this, newStudent, oldStudent);
        }
    }

    /**
     * Notification du changement d'état de la visualisation.
     *
     * @param source la source de l'évènement.
     * @param newState le nouvel état de la visualisation.
     * @param oldState l'ancien état de la visualisation.
     */
    private void fireVisuationStateChanged(Object source, int newState, int oldState) {
        for (MasterCoreListener listener : listeners.getListeners(MasterCoreListener.class)) {
            listener.visuationStateChanged(source, newState, oldState);
        }
    }

    /**
     * Notification du changement d'association d'élèves.
     *
     * @param student un élève.
     * @param associated l'élève associé en pairing.
     */
    private void firePairingStudentAssociatedChanged(Student student, Student associated) {
        for (MasterCoreListener listener : listeners.getListeners(MasterCoreListener.class)) {
            listener.pairingStudentAssociatedChanged(this, student, associated);
        }
    }

    /**
     * Notification d'une demande d'aide d'un élève.
     *
     * @param student l'élève demandant de l'aide.
     */
    public void fireHelpDemanded(Student student) {
        fireNewMessage(student, GuiConstants.callMessage);
    }

    /**
     * Notification d'une réception de fichier envoyé par un élève.
     *
     * @param student l'élève demandant de l'envoi de fichier.
     * @param file le vichier envoyé.
     */
    private void fireFileReceived(Student student, File file) {
        fireNewMessage(student, GuiConstants.receiveFile, file);
    }

    /**
     * Notification d'un message.
     *
     * @param student l'élève demandant de l'aide.
     * @param messageType le type message a afficher.
     * @param args les arguments pour une chaîne de caractères formatée.
     */
    private void fireNewMessage(Student student, String messageType, Object... args) {
        for (MasterCoreListener listener : listeners.getListeners(MasterCoreListener.class)) {
            listener.newMessage(this, student, messageType, args);
        }
    }

    /**
     * Notification d'un message.
     *
     * @param messageType le type message a afficher.
     * @param args les arguments pour une chaîne de caractères formatée.
     */
    private void fireNewMessage(String messageType, Object... args) {
        for (MasterCoreListener listener : listeners.getListeners(MasterCoreListener.class)) {
            listener.newMessage(this, messageType, args);
        }
    }

    /**
     * Ferme correctement les processus de transfert de données.
     *
     * @param source la source de l'évènement.
     * @param exit la valeur de sortie du processus.
     */
    private void processEndReached(Object source, int exit) {
        LOGGER.info("ended: " + source + " -> " + exit);

        if (visualisationMode == SCANNING_MODE) {
//            stopScanning();
        } else if (visualisationMode != NORMAL_MODE) {
            closeRemoteScreen();
        } else {
            if (captureScreen.isRun()) {
                voiceAndScreenStop();
            } else if (chatVoip.speekerhasLine()) {
                blackScreenStop();
            }
        }
    }

    /**
     * Quitte l'application.
     */
    public void closeApplication() {
        close();
        System.exit(0);
    }

    /**
     * Ferme correctement le prof pour que les élèves soit en mode normal.
     */
    private void close() {
        screenWindow.stop();
        captureScreen.stop();
        chatVoip.disconnectAll();

        Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.MASTER_CLOSED);
        sendXmlToOnLan(command);
    }

    /**
     * Ajoute un élève avec son adresse IP. Ajoute un élève connecté (onLine). Prévient la duplication d'adresses IP.
     *
     * @param addressIP l'adresse IP de l'élève.
     */
    public void addStudent(String addressIP) {
        Student student = getStudentAtIP(addressIP);
        if (student == null) {
            boolean added = studentClass.addStudent(addressIP, Constants.GROUP_A);
            if (added) {
                student = getStudentAtIP(addressIP);
                fireStudentAdded(student);
                //selection automaitque pour le premier élève
                if (studentClass.getStudentCount() == 1) {
                    selectStudent(studentClass.getFirstStudent());
                }
            } else {
                int max = studentClass.getStudentCountMax();
                fireNewMessage("moreStudent", max);
            }
        } else {
            student.setOnLine(true);
            fireStudentChanged(student);
        }
    }

    /**
     * Modifie l'état de sélection du groupe.
     *
     * @param group le numéro du groupe.
     * @param activated le nouvel état.
     */
    private boolean setGroupActivated(int group, boolean activated) {
        boolean active = studentClass.setGroupActivated(group, activated);
        selectGroup(group, active);
        return active;
    }

    /**
     * Retourne le numéro de groupe suivant la lettre du groupe.
     *
     * @param idGroup la lettre de groupe.
     *
     * @return le numéro du groupe.
     */
    private int getGroup(char idGroup) {
        int group;
        if (Character.isUpperCase(idGroup)) {
            group = (idGroup - 'A');
        } else {
            group = (idGroup - 'a');
        }

        if (group < 0 || group >= StudentClass.GROUP_NUMBER) {
            group = 0;
        }

        return group;
    }

    /**
     * Retourne si l'élève est sélectionné pour la diffusion.
     *
     * @param student le numéro de l'élève.
     *
     * @return si l'élève est sélectionné pour la diffusion.
     */
    private boolean isSelectionnedForDiffuse(Student student) {
        return studentClass.isSelectionnedForDiffuse(student);
    }

    /**
     * Modifie l'élève associé lors du Pairing.
     *
     * @param student l'index de l'élève.
     * @param pairingStudent l'index de l'élève associé.
     */
    private void setPairing(Student student, Student pairingStudent) {
        student.setPairing(pairingStudent);
    }

    /**
     * Remise à zéro du pairing de l'élève.
     *
     * @param student le numéro de l'élève.
     */
    private void resetPairing(Student student) {
        student.setPairing(null);
    }

    /**
     * Retourne l'élève sélectionné. Si pas d'élève sélectionné, sélectionne le premier de la liste.
     *
     * @return l'élève sélectionné.
     */
    private Student getSelectedClient() {
        if (selectedStudent == null) {
            selectStudent(studentClass.getFirstStudent());
        }
        return selectedStudent;
    }

    /**
     * Retourne l'élève avec l'adresse IP spécifiée.
     *
     * @param addressIP l'adresse IP de l'élève à cherché.
     *
     * @return l'élève trouvé ou {@code null}.
     */
    public Student getStudentAtIP(String addressIP) {
        return studentClass.getStudent(addressIP);
    }

    /**
     * Retourne l'élève avec le login spécifié.
     *
     * @param login le login de l'élève à cherché.
     *
     * @return l'élève trouvé ou {@code null}.
     */
    public Student getStudentAtLogin(String login) {
        return studentClass.getStudentWhithLogin(login);
    }

    /**
     * Sélectionne un groupe.
     *
     * @param group le numéro du groupe.
     */
    private void selectGroup(int group, boolean selected) {
        fireGroupSelectionChanged(group, selected);
    }

    /**
     * Modifie l'état du pairing. Valeurs possibles : NORMAL_MODE, PAIRING_PROGRAMMATION, PAIRING_ACTIF
     *
     * @param mode l'état du pairing.
     */
    private void setPairingMode(int mode) {
        this.pairingMode = mode;
        if (mode == PAIRING_PROGRAMMATION) {
            selectStudent(null);
        }
    }

    /**
     * Sélectionne un élève.
     *
     * @param student l'indice de l'élève.
     */
    private void selectStudent(Student student) {
        if (selectedStudent != student) {
            fireStudentSelectedChanged(student, selectedStudent);
        }
        selectedStudent = student;
    }

    /**
     * Modifie le mode de visualisation. Valeurs possibles : NORMAL_MODE, LISTENING_MODE, CONTROL_MODE, STUDENT_MODE,
     * SCANNING_MODE, MOSAIQUE_MODE.
     *
     * @param mode le nouveau mode de visualisation.
     */
    private void setViualisationMode(int mode) {
        int oldMode = visualisationMode;
        visualisationMode = mode;
        fireVisuationStateChanged(this, visualisationMode, oldMode);
    }

    /**
     * Dévalide la réception d'un écran élève.
     */
    public void closeRemoteScreen() {
        screenWindow.stop();
        chatVoip.disconnectAll();

        Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.SEND_SCREEN_STOP);
        String xml = CommandXMLUtilities.getXML(command);
        //Un seul élévé émetteur d'écran 
        if (visualisationMode != NORMAL_MODE && visualisationMode != MOSAIQUE_MODE) {
            Student student = getSelectedClient();
            sendXmlToStudent(xml, student.getAddressIP());

            Student pairingStudent = student.getPairing();
            if (pairingStudent != null) {
                String addressIP = pairingStudent.getAddressIP();
                sendXmlToStudent(xml, addressIP);
            }
        }

        if (visualisationMode == MOSAIQUE_MODE) {
            setViualisationMode(NORMAL_MODE);
            mosaiqueProcess.close();

            sendXmlToOnLan(command);
        }

        if (visualisationMode == STUDENT_MODE) {
            command.setAction(CommandAction.RECEIVE_SCREEN_STOP);
            sendXmlToOnLan(command);
        }
    }

    /**
     * Déclenche l'écoute discrète d'un élève.
     *
     * @param student l'indice de l'élève.
     */
    private void startListening(Student student) {
        if (visualisationMode == NORMAL_MODE) {
            setViualisationMode(LISTENING_MODE);
        }

        selectStudent(student);
        String addressIP = student.getAddressIP();
        Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.SEND_SCREEN);
        command.putParameter(CommandParamater.CLIENT_NUMBER, 1);
        command.putParameter(CommandParamater.IP_ADDRESS, tuteurIP);
        command.putParameter(CommandParamater.SCREEN_PORT, ThotPort.screenRemotePortBase);
        command.putParameter(CommandParamater.AUDIO_PORT, chatVoip.getPort());
        command.putParameter(CommandParamater.FPS, (int) fps);
        command.putParameter(CommandParamater.QUALITY, quality);
        command.putParameter(CommandParamater.LINES, nbLines);
        sendXmlToStudent(CommandXMLUtilities.getXML(command), addressIP);

        screenWindow.start(addressIP, ThotPort.screenRemotePortBase, false, nbLines, student.getName());

        Student pairingStudent = student.getPairing();
        if (pairingStudent != null) {
            addressIP = pairingStudent.getAddressIP();
            command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.SEND_VOICE);
            command.putParameter(CommandParamater.IP_ADDRESS, tuteurIP);
            command.putParameter(CommandParamater.AUDIO_PORT, chatVoip.getPairingPort());
            sendXmlToStudent(CommandXMLUtilities.getXML(command), addressIP);
        }
    }

    /**
     * Déclenche la prise en main sur un élève.
     *
     * @param student l'indice de l'élève.
     */
    private void startStudentControl(Student student) {
        if (visualisationMode == NORMAL_MODE) {
            setViualisationMode(CONTROL_MODE);
        }

        selectStudent(student);
        String addressIP = student.getAddressIP();
        chatVoip.connect(addressIP, ThotPort.audioPort);

        Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.SEND_SCREEN);
        command.putParameter(CommandParamater.REMOTE_HANDLING, true);
        command.putParameter(CommandParamater.CLIENT_NUMBER, 1);
        command.putParameter(CommandParamater.IP_ADDRESS, tuteurIP);
        command.putParameter(CommandParamater.SCREEN_PORT, ThotPort.screenRemotePortBase);
        command.putParameter(CommandParamater.AUDIO_PORT, chatVoip.getPort());
        command.putParameter(CommandParamater.FPS, (int) fps);
        command.putParameter(CommandParamater.QUALITY, quality);
        command.putParameter(CommandParamater.LINES, nbLines);
        sendXmlToStudent(CommandXMLUtilities.getXML(command), addressIP);

        screenWindow.start(addressIP, ThotPort.screenRemotePortBase, true,
                nbLines, student.getName());

        Student pairingStudent = student.getPairing();
        if (pairingStudent != null) {
            addressIP = pairingStudent.getAddressIP();
            chatVoip.connect(addressIP, ThotPort.audioPort);
            command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.SEND_VOICE);
            command.putParameter(CommandParamater.IP_ADDRESS, tuteurIP);
            command.putParameter(CommandParamater.AUDIO_PORT, chatVoip.getPairingPort());
            sendXmlToStudent(CommandXMLUtilities.getXML(command), addressIP);
        }
    }

    /**
     * Déclenche la mosaïque.
     */
    private void startMosaique() {
        setViualisationMode(MOSAIQUE_MODE);
        int nbSelected = studentClass.getSelectedCount();

        if (nbSelected == 0) {
            return;
        }

        if (nbSelected <= 4) {
            nbSelected = 2;
        } else if (nbSelected <= 9) {
            nbSelected = 3;
        } else if (nbSelected <= 16) {
            nbSelected = 4;
        } else if (nbSelected <= 25) {
            nbSelected = 5;
        } else {
            nbSelected = 6;
        }

        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenDim.height / nbSelected;
        int width = screenDim.width / nbSelected;

        int colonne = 0;
        int ligne = 0;

        int currentPort = ThotPort.screenRemotePortBase;

        Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.SEND_SCREEN);
        command.putParameter(CommandParamater.CLIENT_NUMBER, 1);
        command.putParameter(CommandParamater.FPS, mosaiqueFps);
        command.putParameter(CommandParamater.QUALITY, quality);
        command.putParameter(CommandParamater.LINES, nbLines);
        command.putParameter(CommandParamater.SCREEN_PORT, currentPort);

        String xml = CommandXMLUtilities.getXML(command);

        int i = 1;
        for (Iterator<Student> it = studentClass.iterator(); it.hasNext(); ) {
            Student currentStudent = it.next();
            if (isSelectionnedForDiffuse(currentStudent)) {
                sendXmlToStudent(xml, currentStudent.getAddressIP());

                int x = colonne * width;
                int y = (ligne) * height;

                try {
                    mosaiqueProcess
                            .addClient(i, currentStudent.getAddressIP(), currentPort, currentStudent.getName(), x, y,
                                    width, height, nbLines, mosaiqueTimeout);
                } catch (ThotException e) {
                    LOGGER.error("Impossible de lancer une mosaique pour l'élève {}", e, currentStudent);
                }

                Utilities.waitInMillisecond(mosaiqueDelay);

                colonne++;
                if (colonne == nbSelected) {
                    ligne++;
                    colonne = 0;
                }
            }
        }
    }

    /**
     * Déclenche la scrutation automatique.
     */
    private void scanningStart() {
        if (visualisationMode == NORMAL_MODE) {
            setViualisationMode(SCANNING_MODE);
            scanningThread = new Thread(new Scanning(), "scanning");
            scanningThread.start();
        }
    }

    /**
     * Arrête la scrutation automatique.
     */
    private void scanningStop() {
        closeRemoteScreen();
        scanningStudent = null;
        setViualisationMode(NORMAL_MODE);
        scanningThread.interrupt();
    }

    /**
     * Demarre l'envoi d'écran prof.
     */
    private void voiceAndScreen() {
        int eleve = 0;

        for (Iterator<Student> it = studentClass.iterator(); it.hasNext(); ) {
            Student currentStudent = it.next();
            if (isSelectionnedForDiffuse(currentStudent)) {
                int currentPort = ThotPort.screenRemotePortBase + eleve;
                Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.RECEIVE_SCREEN);
                command.putParameter(CommandParamater.IP_ADDRESS, tuteurIP);
                command.putParameter(CommandParamater.SCREEN_PORT, currentPort);
                command.putParameter(CommandParamater.LINES, nbLines);
                command.putParameter(CommandParamater.TIMEOUT, screenWindow.getTimeout());
                sendXmlToStudent(CommandXMLUtilities.getXML(command), currentStudent.getAddressIP());

//                addresses.add(new InetSocketAddress(getAddressIP(i), currentPort));
                eleve++;
                chatVoip.connect(currentStudent.getAddressIP(), ThotPort.audioPort);
            }
        }

        if (eleve > 0) {
            captureScreen.start(ThotPort.screenRemotePortBase, eleve, false);
//            captureScreen.start(addresses, 0);
        }
    }

    /**
     * Arrête l'envoi d'écran prof.
     */
    private void voiceAndScreenStop() {
        chatVoip.disconnectAll();
        captureScreen.stop();
        Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.RECEIVE_SCREEN_STOP);
        sendXmlToOnLan(command);
    }

    /**
     * Démarre l'envoi d'un écran noir.
     */
    private void blackScreen() {
        Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.RECEIVE_BLACK_SCREEN);
        String xml = CommandXMLUtilities.getXML(command);
        for (Iterator<Student> it = studentClass.iterator(); it.hasNext(); ) {
            Student currentStudent = it.next();
            if (isSelectionnedForDiffuse(currentStudent)) {
                sendXmlToStudent(xml, currentStudent.getAddressIP());
                chatVoip.connect(currentStudent.getAddressIP(), ThotPort.audioPort);
            }
        }
    }

    /**
     * Arrête l'envoi d'un écran noir.
     */
    private void blackScreenStop() {
        Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.RECEIVE_SCREEN_STOP);
        sendXmlToOnLan(command);
        chatVoip.disconnectAll();
    }

    /**
     * Déclenche l'envoi d'un écran élève vers un groupes d'élèves.
     *
     * @param student l'indice de l'élève à diffuser.
     */
    private void studentScreen(Student student) {
        setViualisationMode(STUDENT_MODE);
        int nbDiffusion = 0;

        selectStudent(student);
        Student pairing = student.getPairing();

        StringBuilder studentList = new StringBuilder(1024);
        for (Iterator<Student> it = studentClass.iterator(); it.hasNext(); ) {
            Student currentStudent = it.next();
            if (student != currentStudent && isSelectionnedForDiffuse(currentStudent)) {
                nbDiffusion++;
                if (currentStudent != pairing) {
                    studentList.append(XMLUtilities.createElement(CommandParamater.CLIENT_IP_ADDRESS.getParameter(),
                            currentStudent.getAddressIP()));
                }
            }
        }

        if (nbDiffusion > 0) {
            nbDiffusion++; //on ajoute le poste professeur
            String ipDiffuseur = student.getAddressIP();

            Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.RECEIVE_SCREEN);
            command.putParameter(CommandParamater.IP_ADDRESS, ipDiffuseur);
            command.putParameter(CommandParamater.LINES, nbLines);
            command.putParameter(CommandParamater.TIMEOUT, studentTimeout);

            int currentPort = ThotPort.screenRemotePortBase;
            for (Iterator<Student> it = studentClass.iterator(); it.hasNext(); ) {
                Student currentStudent = it.next();
                if (student != currentStudent && isSelectionnedForDiffuse(currentStudent)) {
                    command.putParameter(CommandParamater.SCREEN_PORT, currentPort);
                    sendXmlToStudent(CommandXMLUtilities.getXML(command), currentStudent.getAddressIP());
                    currentPort++;
                }
            }

            command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.SEND_SCREEN);
            command.putParameter(CommandParamater.REMOTE_HANDLING, true);
            command.putParameter(CommandParamater.CLIENT_NUMBER, nbDiffusion);
            command.putParameter(CommandParamater.SCREEN_PORT, ThotPort.screenRemotePortBase);
            command.putParameter(CommandParamater.AUDIO_PORT, chatVoip.getPort());
            command.putParameter(CommandParamater.FPS, (int) fps);
            command.putParameter(CommandParamater.QUALITY, quality);
            command.putParameter(CommandParamater.LINES, nbLines);
            command.putParameter(CommandParamater.IP_ADDRESS, tuteurIP);
            if (studentList.length() > 0) {
                command.putParameter(CommandParamater.LIST, studentList.toString());
            }
            sendXmlToStudent(CommandXMLUtilities.getXML(command), ipDiffuseur);

            screenWindow.start(ipDiffuseur, currentPort, true, nbLines, student.getName());
            chatVoip.connect(ipDiffuseur, ThotPort.audioPort);
        }
    }

    /**
     * Arrête le pairing.
     */
    private void pairingStop() {
        setPairingMode(NORMAL_MODE);
        Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.PAIRING_STOP);
        String xml = CommandXMLUtilities.getXML(command);
        for (Iterator<Student> it = studentClass.iterator(); it.hasNext(); ) {
            Student currentStudent = it.next();
            resetPairing(currentStudent);
            if (currentStudent.isOnLine()) {
                sendXmlToStudent(xml, currentStudent.getAddressIP());
                fireStudentChanged(currentStudent);
            }
        }
    }

    /**
     * Modifie le pairing de l'élève. Mémorisation de l'élève précédement sélectionné.
     *
     * @param student l'indice de l'élève.
     */
    private void pairing(Student student) {
        //Anulation du précédent pairing
        Student pairing = student.getPairing();
        Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.PAIRING_STOP);
        String xml = CommandXMLUtilities.getXML(command);
        if (pairing != null) {
            sendXmlToStudent(xml, student.getAddressIP());
            sendXmlToStudent(xml, pairing.getAddressIP());
            firePairingStudentAssociatedChanged(selectedStudent, null);
            resetPairing(pairing);
            resetPairing(student);
        }

        if (pairingFirstSelected == null) {
            firePairingStudentAssociatedChanged(student, null);
            pairingFirstSelected = student;
        } else {
            if (pairingFirstSelected == student) {
                return;
            }

            setPairing(pairingFirstSelected, student);
            setPairing(student, pairingFirstSelected);

            firePairingStudentAssociatedChanged(pairingFirstSelected, student);

            //Interconnecter les élèves
            command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.PAIRING);
            command.putParameter(CommandParamater.IP_ADDRESS, student.getAddressIP());
            command.putParameter(CommandParamater.AUDIO_PORT, ThotPort.audioPairingPort);
            sendXmlToStudent(CommandXMLUtilities.getXML(command), pairingFirstSelected.getAddressIP());

            command.putParameter(CommandParamater.IP_ADDRESS, pairingFirstSelected.getAddressIP());
            sendXmlToStudent(CommandXMLUtilities.getXML(command), student.getAddressIP());

            pairingFirstSelected = null;
        }
    }

    /**
     * Appele d'un boite de dialogue de choix de fichier et lance le transfert.
     */
    private void sendFile(final File file) {
        final int nbEleve = studentClass.getSelectedCount();

        if (nbEleve > 0) {
            Thread thread = new Thread(() -> {
                fileTransfert.sendFile(file, ThotPort.fileTransfertPortBase, nbEleve);
                Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.LAUNCH_FILE);
                command.putParameter(CommandParamater.FILE, file.getName());
                sendXmlToSelected(command);
            }, "sendFile");

            thread.start();

            Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.RECEIVE_FILE);
            command.putParameter(CommandParamater.IP_ADDRESS, tuteurIP);
            command.putParameter(CommandParamater.PORT, ThotPort.fileTransfertPortBase);
            command.putParameter(CommandParamater.FILE, file.getName());
            command.putParameter(CommandParamater.SIZE, file.length());
            sendXmlToSelected(command);

            while (thread.isAlive()) {
                Utilities.waitInMillisecond(100);
            }
        }
    }

    /**
     * Lance une thread de réception de fichier.
     *
     * @param file le fichier destinataire des données.
     * @param size la taille du fichier.
     * @param addressIP l'adresse IP de l'envoyeur.
     * @param portFile le port de communication.
     */
    public void receiveFile(final File file, final int size,
            final String addressIP, final int portFile) {
        new Thread(() -> {
            while (fileTransfert.isRunning()) {
                Utilities.waitInMillisecond(100);
            }
            fileTransfert.loadFile(file, size, addressIP, portFile);
            fireFileReceived(getStudentAtIP(addressIP), file);
        }, "receiveFile from " + addressIP).start();
    }

    /**
     * Démarre du noyau.
     */
    public void start() {
        new Thread(this, this.getClass().getName()).start();
    }

    /**
     * Envoie les PING.
     */
    @Override
    public void run() {
        boolean isSend;
        String addressIP;

        Command command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.PING);
        command.putParameter(CommandParamater.IP_ADDRESS, tuteurIP);

        Student student = studentClass.getFirstStudent();
        while (true) {
            if (studentClass.getStudentCount() == 0 || student == null) {
                Utilities.waitInMillisecond(3000);
                student = studentClass.getFirstStudent();
                continue;
            }

            addressIP = student.getAddressIP();
            command.putParameter(CommandParamater.PASSWORD_CHECKED, student.isChecked());
            isSend = sendXmlToStudent(CommandXMLUtilities.getXML(command), addressIP);

            student.setOnLine(isSend);
            fireStudentChanged(student);

            student = studentClass.next(student);
            if (student == null) {
                student = studentClass.getFirstStudent();
                Utilities.waitInMillisecond(3000);
            }

            Utilities.waitInMillisecond(500);
        }
    }

    /**
     * Traite les commande de l'interface.
     *
     * @param action le type d'action.
     * @param parameter les paramètres de l'action.
     */
    public void executeCommand(String action, String parameter) {
//        if(laboModule != null && command.getType().contentEquals(Command.TYPE_LABO)) {
//            laboModule.executeLaboCommand(action, parameter);
//            return;
//        }

        boolean on = Utilities.parseStringAsBoolean(parameter);

        Command command;

        if (action.startsWith(GuiConstants.group)) {
            setGroupActivated(getGroup(action.charAt(action.length() - 1)), on);
        } else if (action.startsWith(GuiConstants.creationGroup)) {
            Student student = getStudentAtIP(parameter);
            student.setGroup(getGroup(action.charAt(action.length() - 1)));
            fireStudentChanged(student);
        }

        switch (action) {
            case GuiConstants.student://Selection d'un élève
                Student eleve = getStudentAtIP(parameter);
                if (eleve == null) {
                    return;
                }

                //Quitter avant de sélectionner un élève
                if (visualisationMode != NORMAL_MODE) {
                    closeRemoteScreen();
                }

                selectStudent(eleve);

                if (pairingMode == PAIRING_PROGRAMMATION) {
                    pairing(eleve);
                    return;
                }

                switch (visualisationMode) {
                    case CONTROL_MODE:
                        startStudentControl(eleve);
                        break;
                    case LISTENING_MODE:
                        startListening(eleve);
                        break;
                    case STUDENT_MODE:
                        studentScreen(eleve);
                        break;
                }
                break;
            case GuiConstants.listening:
                if (on) {
                    if (studentClass.getStudentCount() > 0) {
                        startListening(getSelectedClient());
                    }
                } else {
                    closeRemoteScreen();
                    setViualisationMode(NORMAL_MODE);
                }
                break;
            case GuiConstants.studentControl:
                if (on) {
                    if (studentClass.getStudentCount() > 0) {
                        startStudentControl(getSelectedClient());
                    }
                } else {
                    closeRemoteScreen();
                    setViualisationMode(NORMAL_MODE);
                }
                break;
            case GuiConstants.mosaique:
                if (on) {
                    startMosaique();
                } else {
                    closeRemoteScreen();
                }
                break;
            case GuiConstants.scanning:
                if (on) {
                    scanningStart();
                } else {
                    scanningStop();
                }
                break;
            case GuiConstants.masterScreenVoice:
                if (on) {
                    voiceAndScreen();
                } else {
                    voiceAndScreenStop();
                }
                break;
            case GuiConstants.masterVoice:
                for (Iterator<Student> it = studentClass.iterator(); it.hasNext(); ) {
                    Student currentStudent = it.next();
                    if (isSelectionnedForDiffuse(currentStudent)) {
                        chatVoip.connect(currentStudent.getAddressIP(), ThotPort.audioPort);
                    }
                }
                break;
            case GuiConstants.blackScreen:
                if (on) {
                    blackScreen();
                } else {
                    blackScreenStop();
                }
                break;
            case GuiConstants.studentScreen:
                if (on) {
                    studentScreen(getSelectedClient());
                } else {
                    closeRemoteScreen();
                    setViualisationMode(NORMAL_MODE);
                }
                break;
            case GuiConstants.pairingValid:
                if (on) {
                    setPairingMode(PAIRING_PROGRAMMATION);
                } else {
                    setPairingMode(PAIRING_ACTIF);
                }
                break;
            case GuiConstants.pairing:
                if (on) {
                    setPairingMode(PAIRING_PROGRAMMATION);
                } else {
                    pairingStop();
                }
                break;
            case GuiConstants.sendMessage:
                command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.RECEIVE_MESSAGE);
                command.putParameter(CommandParamater.MESSAGE, parameter);
                sendXmlToSelected(command);
                break;
            case GuiConstants.sendFile:
                if (parameter != null) {
                    File file = new File(parameter);
                    sendFile(file);
                }
                break;
            case GuiConstants.language:
                applicationsDialog.updateLanguage();
                break;
            case GuiConstants.loginSession:
                command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.RESET_LOGIN);
                String xml = CommandXMLUtilities.getXML(command);

                for (Iterator<Student> it = studentClass.iterator(); it.hasNext(); ) {
                    Student currentStudent = it.next();
                    if (isSelectionnedForDiffuse(currentStudent)) {
                        sendXmlToStudent(xml, currentStudent.getAddressIP());
                        currentStudent.setLogin(null);
                    }
                }
                break;
            case GuiConstants.computerPower:
                command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.SHUTDOWN);
                sendXmlToSelected(command);

                for (Iterator<Student> it = studentClass.iterator(); it.hasNext(); ) {
                    Student currentStudent = it.next();
                    if (isSelectionnedForDiffuse(currentStudent)) {
                        currentStudent.setOnLine(false);
                        currentStudent.setOnLine(false);
                        if (currentStudent.getPairing() != null) {
                            resetPairing(currentStudent.getPairing());
                            fireStudentChanged(currentStudent.getPairing());
                            resetPairing(currentStudent);
                        }

                        fireStudentChanged(currentStudent);
                    }
                }
                break;
            case GuiConstants.osSession:
                command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.SHUTDOWN_SESSION);
                sendXmlToSelected(command);

                for (Iterator<Student> it = studentClass.iterator(); it.hasNext(); ) {
                    Student currentStudent = it.next();
                    if (isSelectionnedForDiffuse(currentStudent)) {
                        currentStudent.setOnLine(false);
                        currentStudent.setOnLine(false);
                        if (currentStudent.getPairing() != null) {
                            resetPairing(currentStudent.getPairing());
                            fireStudentChanged(currentStudent.getPairing());
                            resetPairing(currentStudent);
                        }
                        fireStudentChanged(currentStudent);
                    }
                }
                break;
            case GuiConstants.jclicReports:
                String jclicReports = null;
                try {
                    if (Utilities.WINDOWS_PLATFORM) {
                        jclicReports = Utilities.getJClicReportsCommand();
                    } else {
                        jclicReports = "jclicreports";
                    }

                    StringBuilder out = new StringBuilder(1024);
                    StringBuilder err = new StringBuilder(1024);

                    Utilities.startProcess("jclicReports", out, err, jclicReports);
                } catch (ThotException e) {
                    LOGGER.error("Impossible de lancer Jclic Reports ({})", e, jclicReports);
                }
                break;
            case GuiConstants.jclic:
                String file = "jclic";
                command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.EXECUTE);
                command.putParameter(CommandParamater.FILE, file);
                sendXmlToSelected(command);
                break;
            case GuiConstants.bloqueClavier:
                command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.BLOCK_KEYBOARD);
                command.putParameter(CommandParamater.BLOCK, on);
                sendXmlToSelected(command);
                break;
            case GuiConstants.bloqueInternet:
                command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.BLOCK_INTERNET);
                command.putParameter(CommandParamater.BLOCK, on);
                sendXmlToSelected(command);
                break;
            case GuiConstants.interdireApplication:
                if (on) {
                    applicationsDialog.showDialog();
                }
                break;
            case GuiConstants.deleteDocument:
                command = new Command(CommandType.TYPE_SUPERVISION, CommandAction.DELETE_DOCUMENT);
                sendXmlToSelected(command);
                fireButtonStateChanged(GuiConstants.deleteDocument, true);
                break;
        }
    }

    /**
     * Envoie une commande xml à un élève.
     *
     * @param xml la commande xml.
     * @param address l'adresse IP de l'élève.
     *
     * @return la réussite de l'envoi.
     */
    private boolean sendXmlToStudent(String xml, String address) {
        try {
            Utilities.sendMessage(xml, address, ThotPort.masterToStudentPort);
            return true;
        } catch (ThotException e) {
            LOGGER.error("Echec de l'envoi de la commande {} à l'élève {}:{}", xml, address,
                    ThotPort.masterToStudentPort);
            return false;
        }
    }

    /**
     * Envoie une commande xml à tous les élèves sélectionnés.
     *
     * @param command la commande.
     */
    private void sendXmlToSelected(Command command) {
        String xml = CommandXMLUtilities.getXML(command);
        for (Iterator<Student> it = studentClass.iterator(); it.hasNext(); ) {
            Student currentStudent = it.next();
            if (isSelectionnedForDiffuse(currentStudent)) {
                sendXmlToStudent(xml, currentStudent.getAddressIP());
            }
        }
    }

    /**
     * Envoie une commande xml à tous les élèves en ligne.
     *
     * @param command la commande.
     */
    private void sendXmlToOnLan(Command command) {
        String xml = CommandXMLUtilities.getXML(command);
        for (Iterator<Student> it = studentClass.iterator(); it.hasNext(); ) {
            Student currentStudent = it.next();
            if (currentStudent.isOnLine()) {
                sendXmlToStudent(xml, currentStudent.getAddressIP());
            }
        }
    }

    /**
     * Envoie la commande d'ouverture du labo à tous les élèves sélectionnés.
     */
    public void sendOpenLabo() {
        String file = "labo";
        Command command = new Command(CommandType.TYPE_LABORATORY, CommandAction.EXECUTE);
        command.putParameter(CommandParamater.FILE, file);
        sendXmlToSelected(command);
    }

    /**
     * Thread pour la gestion de la scrutation automatique.
     */
    private class Scanning implements Runnable {

        /**
         * Traitement de passage.
         */
        @Override
        public void run() {
            long time;
            //sélection du premier élève pour la scrutation automatique
            scanningStudent = studentClass.nextForScanning(null);

            while (scanningStudent != null && visualisationMode == SCANNING_MODE) {

                startListening(scanningStudent);
                time = 0;
                //attente de la fin du temps de scrutation en ne bloquant pas une annulation.
                while (time < scanningTime) {
                    Utilities.waitInMillisecond(100);
                    time += 100;
                    if (visualisationMode != SCANNING_MODE || !screenWindow.isRun()) {
                        break;
                    }
                }

                //si il y a une annulation, on arrête la scrutation automatique
                if (visualisationMode != SCANNING_MODE || !screenWindow.isRun()) {
                    scanningStop();
                    break;
                }

                closeRemoteScreen();

                scanningStudent = studentClass.nextForScanning(scanningStudent);

                //attente entre l'affichage de deux élève
                if (visualisationMode == SCANNING_MODE && scanningStudent != null) {
                    Utilities.waitInMillisecond(500);
                } else {
                    scanningStop();
                    break;
                }
            }
        }
    }
}
