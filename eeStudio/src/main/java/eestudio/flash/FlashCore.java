package eestudio.flash;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.Locale;

import eestudio.Core;
import eestudio.Listener;
import eestudio.gui.GuiFlashResource;
import eestudio.utils.XMLUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thot.model.Constants;
import thot.model.Index;
import thot.utils.LaboXMLUtilities;
import thot.utils.Utilities;

/**
 * Fenêtre principale du poste élève.
 *
 * @author fabrice
 */
public class FlashCore {
    /**
     * Instance de log.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FlashCore.class);

    /**
     * Référence sur le noyau de l'application
     */
    private final Core core;

    /**
     * Serveur pour la communication flash -> core
     */
    private FlashServer flashToCoreServer;
    /**
     * Serveur pour la communication core -> flash
     */
    private FlashClient coreToFlashClient;

    /**
     * Resources graphiques
     */
    private GuiFlashResource guiResources;

    /**
     * Initialisation de l'interface graphique.
     *
     * @param core le noyau de l'application.
     * @param flashToCorePort
     * @param coreToFlashPort
     */
    public FlashCore(Core core, int flashToCorePort, int coreToFlashPort) {
        this.core = core;

        flashToCoreServer = new FlashServer(this, flashToCorePort);
        coreToFlashClient = new FlashClient(coreToFlashPort);

        addCoreListener();

        coreToFlashClient.start();
        flashToCoreServer.start();
    }

    /**
     * Affecte les resources graphiques.
     *
     * @param guiResourcesWithFlash les resources graphiques.
     */
    public void setMainFrame(GuiFlashResource guiResourcesWithFlash) {
        this.guiResources = guiResourcesWithFlash;
        this.guiResources.addEditorWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                sendCommandToFlash(FlashConstants.text, "off");
                sendCommandToFlash(Command.TEXT, Boolean.toString(core.hasText()));
            }
        });

        this.guiResources.addProcessingBarWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                core.cancelConversion();
            }
        });
    }

    /**
     * @param list la liste
     */
    public void sendVersionToFlash(List<String> list) {
        sendCommandToFlash(Command.VERSION, XMLUtilities.getXMLDescription(list));
    }

    /**
     * Envoie la commande de changement de langue.
     *
     * @param locale la nouvelle langue.
     */
    public void sendLanguageToFlash(Locale locale) {
        sendCommandToFlash(Command.LANGUAGE, locale.getLanguage());
    }

    /**
     * Envoie une commande au Flash.
     *
     * @param action le nom de l'action.
     * @param parameter les parmètres.
     */
    private void sendCommandToFlash(String action, String parameter) {
        Command command = new Command(action, parameter);
        String xml = XMLUtilities.getXML(command);
        coreToFlashClient.sendCommand(xml);
    }

    /**
     * Ajout des méthodes pour le listener. Utilisation de SwingUtilities.invokeLater(new Runnable()) pour que les
     * modifications touchant l'interface graphique soit appelé par l'EDT.
     */
    private void addCoreListener() {
        core.addListener(new Listener() {

            @Override
            public void runningStateChanged(int state) {
                String runningState;
                if (state == Constants.PLAYING) {
                    runningState = Command.playing;
                } else if (state == Constants.RECORDING) {
                    runningState = Command.recording;
                } else if (state == Constants.RECORDING_INSERT) {
                    runningState = Command.insert;
                } else {
                    runningState = Command.pause;
                }

                guiResources.old_runningStateChanged(state);
                sendCommandToFlash(Command.RUNNING_STATE, runningState);
            }

            @Override
            public void recordTimeMaxChanged(long recordTimeMax) {
                sendCommandToFlash(Command.TIME_MAX, Long.toString(recordTimeMax));
            }

            @Override
            public void textLoaded(String text, boolean styled) {
                if (!styled) {
                    guiResources.old_textLoaded(text);
                }
                sendCommandToFlash(Command.TEXT, Boolean.toString(core.hasText()));
            }

            @Override
            public void timeChanged(long time) {
                sendCommandToFlash(Command.TIME, Long.toString(time));
            }

            @Override
            public void insertVoiceTimeChanged(long time) {
                sendCommandToFlash(Command.TIME_INSERT_VOICE, Long.toString(time));
            }

            @Override
            public void indexesChanged(String xmlIndexesDescription) {
                sendCommandToFlash(Command.INDEXES, xmlIndexesDescription);
            }

            @Override
            public void imageChanged(Image image) {
            }

            @Override
            public void audioWaveFileChanged(File leftChannelFile, File rigthChannelFile) {
                String filePath = null;
                if (leftChannelFile != null && leftChannelFile.exists()) {
                    filePath = leftChannelFile.getAbsolutePath();
                }
                sendCommandToFlash(Command.AUDIO_LEFT_CHANNEl_FILE, filePath);

                filePath = null;
                if (rigthChannelFile != null && rigthChannelFile.exists()) {
                    filePath = rigthChannelFile.getAbsolutePath();
                }
                sendCommandToFlash(Command.AUDIO_RIGHT_CHANNEl_FILE, filePath);
            }

            @Override
            public void videoFileChanged(File file) {
                String filePath = null;
                if (file != null && file.exists()) {
                    filePath = file.getAbsolutePath();
                }
                sendCommandToFlash(Command.PLAYER_FILE, filePath);
            }

//            @Override
//            public void processDeterminatedChanged(Object source, boolean determinated) {
//                guiResources.processDeterminatedChanged(determinated);
//            }

            @Override
            public void processBegin(Object source, boolean determinated) {
                guiResources.processBegin(determinated);
            }

            @Override
            public void processEnded(Object source, int exit) {
                guiResources.processEnded();
            }

            @Override
            public void percentChanged(Object source, int percent) {
                guiResources.percentChanged(percent);
            }
        });
    }

    /**
     * Exécute la commande.
     *
     * @param action le type de la commande.
     * @param parameter le paramètre de la commande.
     */
    public void executeCommand(String action, String parameter) {
        LOGGER.info("command: " + action + " para: " + parameter);

        //par défaut = false
        boolean on = Utilities.parseStringAsBoolean(parameter);

        Index index = null;
        if (parameter != null && parameter.startsWith("<" + LaboXMLUtilities.element_index)) {
            index = LaboXMLUtilities.parseIndex(parameter);
        }

        boolean sendOff = on;

        if (action.contentEquals(FlashConstants.load) && on) {
            guiResources.flashLoad();
            sendCommandToFlash(Command.TEXT, Boolean.toString(core.hasText()));
        } else if (action.contentEquals(FlashConstants.save) && on) {
            guiResources.flashSave();
        } else if (action.contentEquals(FlashConstants.edit) && on) {
            int nbIndexes = core.getIndexesCount();
            guiResources.flashEditAll(nbIndexes);
        } else if (action.contentEquals(FlashConstants.insertBlank) && on) {
            guiResources.flashInsertBlank(0);
        } else if (action.contentEquals(FlashConstants.erase) && on) {
            guiResources.flashErase();
        } else if (action.contentEquals(FlashConstants.tag) && on) {
            guiResources.flashEditTags(core.getTags());
        } else if (action.contentEquals(FlashConstants.play)) {
            sendOff = false;
            if (on) {
                core.audioPlay();
            } else {
                core.audioPause();
            }
        } else if (action.contentEquals(FlashConstants.back) && on) {
            core.timeToZero();
        } else if (action.contentEquals(FlashConstants.indexSubtitle) && index != null) {
            guiResources.flashIndexSubtitle(index.getInitialTime(), index.getFinalTime());
            index = null;
        } else if (action.contentEquals(FlashConstants.indexBlank) && index != null) {
            guiResources.flashIndexBlank(index.getInitialTime(), index.getFinalTime());
        } else if (action.contentEquals(FlashConstants.indexVoice) && index != null) {
            guiResources.flashIndexVoice(index.getInitialTime());
        } else if (action.contentEquals(FlashConstants.indexVoice) && on) {
            guiResources.flashIndexVoice(core.getCurrentTime());
        } else if (action.contentEquals(FlashConstants.indexFile) && index != null) {
            guiResources.flashIndexFile(index.getInitialTime());
        }
//        else if(action.contentEquals(FlashConstants.indexSelect) && index != null) {
//            //TODO -v2 selection de bande
//        }
        else if (action.contentEquals(FlashConstants.indexSpeed) && index != null) {
            guiResources.flashIndexSubtitle(
                    index.getInitialTime(), index.getFinalTime());
            index = null;
        }
//        else if(action.contentEquals(FlashConstants.detect) && on) {
//            //TODO -v2 détection de blanc
//        }
        else if (action.contentEquals(FlashConstants.text)) {
            sendOff = false;
            guiResources.flashText(on);
            if (!on) {
                sendCommandToFlash(Command.TEXT, Boolean.toString(core.hasText()));
            }
        } else if (action.contentEquals(FlashConstants.volume)) {
            int volume = Utilities.parseStringAsInt(parameter);
            if (volume >= 0) {
                core.setAudioVolume(volume);
            }
        } else if (action.contentEquals(FlashConstants.mute)) {
            core.toggleAudioMute();
        } else if (action.contentEquals(FlashConstants.time)) {
            long time = Utilities.parseStringAsLong(parameter);
            if (time >= 0) {
                core.setProtectedTime(time);
            }
        } else if (action.contentEquals(FlashConstants.indexEdit) && index != null) {
            core.sortIndexes();
            guiResources.flashIndexEdit((index.getInitialTime() + index.getFinalTime()) / 2);
            index = null;
        } else if (action.contentEquals(FlashConstants.indexRepeat) && index != null) {
            guiResources.flashIndexRepeat(index.getInitialTime(), index.getFinalTime(), index.getSubtitle());
        } else if (action.contentEquals(FlashConstants.indexBlankAfter) && index != null) {
            guiResources.flashIndexBlank(index.getFinalTime(), index.getFinalTime());
        } else if (action.contentEquals(FlashConstants.indexAfter) && index != null) {
            guiResources.flashIndexAfter(index.getInitialTime(), index.getFinalTime());
        } else if (action.contentEquals(FlashConstants.indexBegin) && index != null) {
            core.setProtectedTime(index.getInitialTime());
        } else if (action.contentEquals(FlashConstants.indexEnd) && index != null) {
            core.setProtectedTime(index.getFinalTime());
        } else if (action.contentEquals(FlashConstants.indexPlay) && index != null) {
            core.playOnRange(index.getInitialTime(), index.getFinalTime());
        } else if (action.contentEquals(FlashConstants.indexRecord) && index != null) {
            core.recordOnRange(index.getInitialTime(), index.getFinalTime());
        } else if (action.contentEquals(FlashConstants.indexErase) && index != null) {
            guiResources.flashIndexErase((index.getInitialTime() + index.getFinalTime()) / 2);
        } else if (action.contentEquals(FlashConstants.indexDelete) && index != null) {
            guiResources.flashIndexDelete((index.getInitialTime() + index.getFinalTime()) / 2);
        }
//        else if(action.contentEquals(FlashConstants.textLoad) && on){
//            guiResources.flashTextLoad();
//            sendCommandToFlash(Command.TEXT, Boolean.toString(core.hasText()));
//        }
//        else if(action.contentEquals(FlashConstants.textErase) && on){
//            guiResources.flashTextErase();
//        }
//        else if(action.contentEquals(FlashConstants.textSave) && on){
//            guiResources.flashTextSave();
//        }
        else if (action.contentEquals(FlashConstants.language)) {
            sendOff = false;
            guiResources.flashChangeLanguage(parameter);
        } else if (action.contentEquals(FlashConstants.close)) {
            sendOff = false;
            core.closeApplication();
        }

        if (sendOff || index != null) {
            sendCommandToFlash(action, "off");
        }
    }

}
