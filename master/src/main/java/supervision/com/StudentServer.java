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
package supervision.com;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import supervision.MasterCore;
import supervision.Student;
import thot.supervision.Command;
import thot.supervision.CommandAction;
import thot.supervision.CommandParamater;
import thot.supervision.CommandXMLUtilities;
import thot.utils.Constants;
import thot.utils.Server;
import thot.utils.Utilities;

/**
 * Serveur pour la gestion des demandes des élèves.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class StudentServer extends Server {

    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(StudentServer.class);

    /**
     * Répertoire de l'utilisateur.
     */
    private final File userPath;
    /**
     * Répertoire des logins des élèves.
     */
    private final File loginsPath;
    /**
     * Fichier pour la sonnerie.
     */
    private File wavFile;
    /**
     * Référence pour la gestion des élèves.
     */
    private MasterCore core;
    /**
     * Allocation de données pour éviter de l'allouer dans la boucle.
     */
    private byte[] data = new byte[1024];

    /**
     * Initialisation.
     *
     * @param core la référence sur le noyau.
     * @param port le port pour la gestion des requêtes élève.
     * @param wavFile fichier pour la sonnerie.
     */
    public StudentServer(MasterCore core, int port, File wavFile) {
        super(port);
        this.core = core;
        this.wavFile = wavFile;

        setPriority(Thread.MAX_PRIORITY);

        userPath = new File(System.getProperty("user.home"), Constants.softNamePath);
        loginsPath = new File(userPath, "logins");
        loginsPath.mkdirs();
    }

    /**
     * Vérification que le couple (login, mot de passe) est indentique à celui déjà enregistrer. Si le login n'existe
     * pas, on crée une nouvelle entrée.
     *
     * @param login le login de l'élève.
     * @param password le mot de passe associé.
     *
     * @return {@code true} si l'entrée existe.
     */
    private boolean verifieLogin(String login, String password) {
        File loginFile = new File(loginsPath, login + ".xml");

        //Si pas d'entrée au nom du login
        if (!loginFile.exists()) {
            String xml = CommandXMLUtilities.getLoginXML(password);
            return Utilities.saveText(xml, loginFile);
        }

        String passwordInFile = CommandXMLUtilities.getPassword(loginFile);
        if (password == null || password.isEmpty()) {
            return (passwordInFile == null || passwordInFile.isEmpty());
        } else {
            return password.contentEquals(passwordInFile);
        }
    }

    /**
     * Exécute le traitement de l'action.
     *
     * @param command la commande.
     * @param addressIP l'adresse ip du demandeur.
     */
    private void execute(Command command, final String addressIP) {
        CommandAction action = command.getAction();
        Student student;

        switch (action) {
            case PONG:
                student = core.getStudentAtIP(addressIP);
                String battery = command.getParameter(CommandParamater.BATTERY);

                if (student == null) {
                    return;
                }

                if (battery != null) {
                    int level = Utilities.parseStringAsInt(battery);
                    student.setBatteryLevel(level);
                }

                String login = command.getParameter(CommandParamater.NAME);
                if (login != null) {
                    String password = command.getParameter(CommandParamater.PASSWORD);
                    Student loginStudent = core.getStudentAtLogin(login);
                    //si login est utilisé par un autre
                    if (loginStudent != null && loginStudent != student) {
                        student.setLogin(null);
                    } //si déja validé
                    else if (student.isChecked() && student.getLogin() == null) {
                        student.setLogin(login);
                    } //login non validé
                    else {
                        if (verifieLogin(login, password)) {
                            student.setLogin(login);
                        } else {
                            student.setLogin(null);
                        }
                    }
                } else {
                    //pas de login fourni
                    student.setLogin(null);
                }
                core.fireStudentChanged(student);
                break;
            case FIND:
                core.addStudent(addressIP);
                break;
            case HELP_CALL:
                student = core.getStudentAtIP(addressIP);
                if (student != null && wavFile.exists()) {
                    try {
                        //on force le format du clip pour correspondre à celui du
                        //fichier: sans cela, sous Linux, AudioSystem, n'arrive
                        //pas à ouvrir le flux du fichier avec les paramètres
                        //par défaut de AudioSystem.getClip()
                        AudioFormat format = AudioSystem.getAudioInputStream(wavFile).getFormat();
                        DataLine.Info info = new DataLine.Info(Clip.class, format);
                        Clip clip = (Clip) AudioSystem.getLine(info);
                        clip.open(AudioSystem.getAudioInputStream(wavFile));
                        clip.start();
                    } catch (IOException | LineUnavailableException | UnsupportedAudioFileException | IllegalArgumentException e) {
                        LOGGER.error("", e);
                    }
                    core.fireHelpDemanded(student);
                }
                break;
            case RECEIVE_FILE:
                int portFile = command.getParameterAsInt(CommandParamater.PORT);
                String fileName = command.getParameter(CommandParamater.FILE);
                int size = command.getParameterAsInt(CommandParamater.SIZE);

                student = core.getStudentAtIP(addressIP);
                String studentName = student.getName();
                File path = new File(userPath, studentName);

                File file = new File(path, fileName);
                core.receiveFile(file, size, addressIP, portFile);
                break;
            case END_ERROR:
                core.closeRemoteScreen();
                break;
        }
    }

    /**
     * Traitement spécifique des données sur la socket connecté.
     *
     * @param socket la connexion établie.
     *
     * @throws IOException
     */
    @Override
    protected void process(Socket socket) throws IOException {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

        String addressIP = socket.getInetAddress().getHostAddress();

        StringBuilder builder = new StringBuilder(64);
        int cnt = inputStream.read(data, 0, data.length);
        while (cnt > 0) {
            builder.append(new String(data, 0, cnt, "UTF-8"));
            cnt = inputStream.read(data, 0, data.length);
        }

        String xml = builder.toString();
        LOGGER.debug("receive student command : " + xml + " from " + addressIP);

        List<Command> commands = CommandXMLUtilities.parseCommand(xml);
        for (Command command : commands) {
            execute(command, addressIP);
        }
        commands.clear();
    }
}
