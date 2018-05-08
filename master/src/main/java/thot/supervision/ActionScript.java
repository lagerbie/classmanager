package thot.supervision;

/**
 *
 */
@Deprecated
public class ActionScript {
    private static final double alphaActif = 1;
    private static final double alphaNonActif = 0.3;
    private static final String PAUSE = "pause";

//    /** Sauvegarde de la commande à envoyer */
//    var currentCommand:Command = new Command(Command.UNKNOWN, Command.UNKNOWN);

    /**
     * Etat du lecteur/enregistreur
     */
    private String runningState = PAUSE;
    private String mediatype = "unload";
    private String precState = PAUSE;
    /**
     * Sauvegarde du temps courant en millisecondes
     */
    private long currentTime = 0;
    /**
     * Sauvegarde du temps maximum d'enregistrement en millisecondes
     */
    private long timeMax = 2 * 60 * 1000;
//    /** Listes des indexes multimédia */
//    var indexList:Array = new Array();

//    var vignetteList:Array = new Array();

    public void fireButtonClicked(Object button) {
//        var mc:MovieClip;
//        var buttonOn:Boolean = true;
//
//        if(button is MovieClip) {
//            mc = MovieClip(button);
//            buttonOn = (mc.currentFrame == 1);
//        }
//
//        var ifsend:Boolean = true;
//        var buttonName:String = button.name;
//
//        var action:String = button.name;
//        var parameter:String = buttonOn.toString();
//        var window:MovieClip;
//
//        switch(button) {
//            case mcBar.mcClose:
//                ifsend = false;
//                showMessage(confirmClose);
//                break;
//            case mcBar.mcMini:
//                ifsend = false;
//                minimize();
//                break;
//            case mcBar.mcHelp:
//                ifsend = false;
//                showAbout(aboutMessage);
//                break;
//            case mcLanguage:
//                ifsend = false;
//                mcLanguageChoices.visible = !mcLanguageChoices.visible;
//                break;
//            case mcSupervision.mcScreenAndVoice:
//                ifsend = false;
//                window = mcSupervision.mcSendChoices;
//                if(!buttonOn && !window.visible) {
//                        ifsend = true;
//                        action = mcSupervision.mcSendChoices.rdScreenAndVoice.name;
//                }
//                break;
//            case mcSupervision.mcSendChoices.button:
//                mc = mcSupervision.mcScreenAndVoice;
//                if(mcSupervision.mcSendChoices.rdScreenAndVoice.selected) {
//                        action = mcSupervision.mcSendChoices.rdScreenAndVoice.name;
//                }
//                else if(mcSupervision.mcSendChoices.rdVoice.selected) {
//                        action = mcSupervision.mcSendChoices.rdVoice.name;
//                }
//                mcSupervision.mcSendChoices.visible = false;
//                break;
//            case mcSupervision.mcSendText:
//                ifsend = false;
//                window = mcSupervision.mcMessageDialog;
//                break;
//            case mcSupervision.mcMessageDialog.button:
//                mc = mcSupervision.mcSendText;
//                action = mc.name;
//                parameter = mcSupervision.mcMessageDialog.textArea.text;//htmlText
//                mcSupervision.mcMessageDialog.visible = false;
//                break;
//            case mcSupervision.mcPairing:
//                window = mcSupervision.mcValidePairing;
//                break;
//            case mcSupervision.mcStudentClose:
//                ifsend = false;
//                window = mcSupervision.mcCloseChoices;
//                break;
//            case mcSupervision.mcCloseChoices.button:
//                mc = mcSupervision.mcStudentClose;
//                if(mcSupervision.mcCloseChoices.rdPower.selected) {
//                        action = mcSupervision.mcCloseChoices.rdPower.name;
//                }
//                else if(mcSupervision.mcCloseChoices.rdSession.selected) {
//                        action = mcSupervision.mcCloseChoices.rdSession.name;
//                }
//                else if(mcSupervision.mcCloseChoices.rdClassManager.selected) {
//                        action = mcSupervision.mcCloseChoices.rdClassManager.name;
//                }
//                mcSupervision.mcCloseChoices.visible = false;
//                break;
//            case mcGroups.mcArrow:
//                ifsend = false;
//                window = mcGroups.mcSuppGroups;
//                break;
//            case mcSupervision.mcGroupCreation:
//                ifsend = false;
//                showGroupSelection(buttonOn);
//                break;
//            case mcAutorisations.mcDeleteDocument:
//                ifsend = false;
//                window = mcAutorisations.mcConfirmDelete;
//                break;
//            case mcAutorisations.mcConfirmDelete.no:
//                ifsend = false;
//                mc = mcAutorisations.mcDeleteDocument;
//                mcAutorisations.mcConfirmDelete.visible = false;
//                break;
//            case mcAutorisations.mcConfirmDelete.yes:
//                mc = mcAutorisations.mcDeleteDocument;
//                mcAutorisations.mcConfirmDelete.visible = false;
//                action = mc.name;
//                break;
//            case mcLaboratory.mcPlay:
//            case mcLaboratory.mcRecord:
//                parameter = int(this.currentTime).toString();
//                break;
//            case mcLaboratory.mcBlock:
//            case mcLaboratory.mcFullScreen:
//                    parameter = (mc.currentFrame==1) ? "true" : "false";
//                    break;
//        }
//
//        if(button.parent == mcLanguageChoices) {
//            var language:String = "french";
//            var frame:Number = 1;
//            switch(mc) {
//                case mcLanguageChoices.mcFrench:
//                    language = "french";
//                    frame = 1;
//                    break;
//                case mcLanguageChoices.mcEnglish:
//                    language = "english";
//                    frame = 2;
//                    break;
//                case mcLanguageChoices.mcSpanish:
//                    language = "spanish";
//                    frame = 3;
//                    break;
//                case mcLanguageChoices.mcGerman:
//                    language = "german";
//                    frame = 4;
//                    break;
//                case mcLanguageChoices.mcItalian:
//                    language = "italian";
//                    frame = 5;
//                    break;
//            }
//            mcLanguageChoices.visible = false;
//            action = mcLanguage.name;
//            parameter = button.name;
//            setLanguage(language);
//            mcLanguage.gotoAndStop(frame);
//        }
//
//        if(mc != null && mc != mcLanguage) {
//            if(buttonOn) {
//                mc.gotoAndStop(2);
//                if(window != null) {
//                    //mise au premier plan
//                    window.parent.setChildIndex(window, window.parent.numChildren-1);
//                    window.visible = true;
//                    if(window == mcSupervision.mcMessageDialog)
//                            mcSupervision.mcMessageDialog.textArea.setFocus();
//                }
//            } else {
//                mc.gotoAndStop(1);
//                if(window != null) {
//                    window.visible = false;
//                }
//            }
//
//            if(mc.parent == mcSupervision) {
//                updateButtons(mc, buttonOn);
//            }
//        }
//
//        if(ifsend) {
//            secureButtons(true);
//            if(button.parent == mcLaboratory)
//                sendLaboCommand(action, parameter);
//            else
//                sendCommand(action, parameter);
//        }
    }

    public void fireSliderClicked(Object mcSlider) {//:MovieClip
//        var action:String = mcSlider.name;
//        var position:Number = (mcSlider.mcCursor.x-mcSlider.min)/(mcSlider.max-mcSlider.min);
//        var parameter:String = position.toString();
//
//        switch(mcSlider) {
//            case mcLaboratory.mcTime:
//                position *= this.timeMax;
//                parameter = int(position).toString();
//                break;
//            case mcLaboratory.mcMediaVolume:
//            case mcLaboratory.mcAudioVolume:
//            case mcLaboratory.mcTextSlider:
//                position *= 100;
//                parameter = int(position).toString();
//                break;
//        }
//
//        sendLaboCommand(action, parameter);
    }

    public void fireVignetteClicked(Object vignette, Object target) {//:Vignette
//        switch(target) {
//            case vignette.mcNote:
//            case vignette.mcNote.mcMessage:
//                vignette.mcNote.visible = false;
//                break;
//            case vignette.mcGroupSelection.mcChoiceA:
//            case vignette.mcGroupSelection.mcChoiceB:
//            case vignette.mcGroupSelection.mcChoiceC:
//            case vignette.mcGroupSelection.mcChoiceD:
//            case vignette.mcGroupSelection.mcChoiceE:
//            case vignette.mcGroupSelection.mcChoiceF:
//            case vignette.mcGroupSelection.mcChoiceG:
//            case vignette.mcGroupSelection.mcChoiceH:
//                sendCommand(target.name, vignette.iIndex);
//                break;
//            default:
//                sendCommand("mcEleve", vignette.iIndex);
//        }
    }

    public void setSliderPercent(Object mcSlider, double percent) {
        setSliderPercent(mcSlider, percent, false);
    }

    public void setSliderPercent(Object mcSlider, double percent, boolean onSlider) {//MovieClip
//        if(!onSlider){
//            var position:Number = percent / 100;
//            setSliderPosition(mcSlider, position);
//        }
//        else {
//            var action:String = mcSlider.name;
//            var parameter:String = int(percent).toString();
//            sendLaboCommand(action, parameter);
//        }
    }

    public void setTime(long time) {
        setTime(time, false);
    }

    public void setTime(long time, boolean onSlider) {
//        this.currentTime = time;
//
//        if(!onSlider){
//            var position:Number = 0;
//            if(this.timeMax > 0)
//                position = time / this.timeMax;
//
//            setSliderPosition(mcLaboratory.mcTime, position);
//        }
//        updateTimeCounterText(time, this.timeMax);
    }

    public void setTimeMax(long time) {
//        this.timeMax = time;
//        if(time == 0)
//            setTime(0);
//        else
//            setTime(this.currentTime);
    }

    private void updateIndexes(String xmlIndexes) {
//        var xml:XML = new XML(xmlIndexes);
//        removeAllIndex();
//        var indexes:Indexes = XMLUtilities.parseNodeAsIndexes(xml);
//        var nbIndexes:Number = indexes.getIndexesCount();
//        var index:Index;
//        for(var i:Number=0; i<nbIndexes; i++) {
//            index = indexes.getIndex(i);
//            addIndex(index);
//        }
    }

    private void executeCommand(Object command) {//:Command
//        var action:String = command.getAction();
//        var index:Number = command.getParameterAsInteger(Command.INDEX);
//        var stateOn:Boolean = command.getParameterAsBoolean(Command.PARAMETER);
//
//        switch(action) {
//            case Command.LOAD_VIGNETTE:
//                modifyVignette(index, command.getParameter(Command.NAME), command.getParameter(Command.BATTERY));
//                break;
//            case Command.BUTTON:
//                var button:String = command.getParameter(Command.NAME);
//                var buttonParent:MovieClip;
//                if(button in mcSupervision)
//                        buttonParent = mcSupervision;
//                else if(button in mcGroups)
//                        buttonParent = mcGroups;
//                else if(button in mcAutorisations)
//                        buttonParent = mcAutorisations;
//                else if(button in mcQCM)
//                        buttonParent = mcQCM;
//
//                if(buttonParent != null) {
//                        updateButtons(buttonParent[button], false);
//                        buttonParent[button].gotoAndStop(1);
//                }
//                break;
//            case Command.SELECT_VIGNETTE:
//                selectVignette(index, stateOn);
//                break;
//            case Command.SET_IN_GROUP:
//                setVignetteGroup(index, command.getParameter(Command.GROUP));
//                break;
//            case Command.SELECT_GROUP:
//                selectGroup(command.getParameter(Command.GROUP), stateOn);
//                break;
//            case Command.HIDE_VIGNETTE:
//                hideVignette(index);
//                break;
//            case Command.BLOCK:
//                this.validLicence = !stateOn;
//                break;
//            case Command.BLOCK_LABO:
//                this.validLaboLicence = !stateOn;
//                break;
//            case Command.MESSAGE:
//                showNoteMessage(index, command.getParameter(Command.PARAMETER));
//                break;
//            case Command.LANGUAGE:
//                var language:String = command.getParameter(Command.LANGUAGE);
//                this.setLanguage(language);
//                break;
//        }
//        secureButtons(false);
    }

    private void executeLaboCommand(Object command) {//:Command
//        var action:String = command.getAction();
//        var parameter:String = command.getParameter(Command.PARAMETER);
//
//        var time:Number;
//        var isActive:Boolean;
//        var frame:Number;
//
//        switch(action) {
//            case Command.timePosition:
//                time = new Number(parameter);
//                setTime(time);
//                break;
//            case Command.runningState:
//                this.runningState = parameter;
//                updateLaboratoryButtons(this.runningState, this.mediatype);
//                break;
//            case Command.mediaType:
//                this.mediatype = parameter;
//                updateLaboratoryButtons(this.runningState, this.mediatype);
//                break;
//            case mcLaboratory.mcFullScreen.name:
//                isActive = command.getParameterAsBoolean(Command.PARAMETER);
//                updateFullScreenButton(isActive);
//                break;
//            case mcLaboratory.mcBlock.name:
//                isActive = command.getParameterAsBoolean(Command.PARAMETER);
//                updateBlockButton(isActive);
//                break;
//            case Command.text:
//                if(parameter.charAt(0) == '?')
//                    mcLaboratory.mcText.text = parameter.substring(1);
//                else
//                    mcLaboratory.mcText.text = parameter;
//                break;
//            case Command.trackTimeMax:
//                time = new Number(parameter);
//                setTimeMax(time);
//                break;
//            case Command.removeIndexes:
//                removeAllIndex();
//                break;
//            case "index":
//                drawIndex(parameter);
//                break;
//            case Command.secure:
//                isActive = command.getParameterAsBoolean(Command.PARAMETER);
//                secureButtons(isActive);
//                break;
//            case mcLaboratory.mcDiffuse.name:
//                isActive = command.getParameterAsBoolean(Command.PARAMETER);
//                frame = (isActive) ? 2 : 1;
//                mcLaboratory.mcDiffuse.gotoAndStop(frame);
//
//                mcLaboratory.mcBlock.gotoAndStop(frame);
//                mcLaboratory.mcBlock.alpha = (isActive) ? alphaNonActif : alphaActif;
//                mcLaboratory.mcFullScreen.gotoAndStop(frame);
//                mcLaboratory.mcFullScreen.alpha = (isActive) ? alphaNonActif : alphaActif;
//                break;
//            default:
//                isActive = command.getParameterAsBoolean(Command.PARAMETER);
//                frame = (isActive) ? 2 : 1;
//
//                if(action in mcLaboratory) {
//                    mcLaboratory[action].gotoAndStop(frame);
//                }
//        }
    }


////tabs
//tab.addEventListener(MouseEvent.CLICK, onClickTabEvent);
//var tabSelected:MovieClip = mcSupervisionTab;
//updateTabs(mcSupervisionTab);

    void updateTabs(Object tab) {//:MovieClip
//	tabSelected.alpha = alphaNonActif;
//	tab.alpha = alphaActif;
//
//	tabSelected = tab;
//	
//	this.mcSupervision.visible = (tab==mcSupervisionTab);
//	this.mcLaboratory.visible = (tab==mcLaboTab);
//	this.mcQCM.visible = (tab==mcQCMTab);
//	this.mcAutorisations.visible = (tab==mcAdvancedTab);
//	
//	showAllVignettes(tab!=mcLaboTab);
    }

    void onClickTabEvent(Object event) {//:Event
//	var mc = event.currentTarget;
//	if(mc.alpha > 0.2) {
//		updateTabs(mc);
//	}
    }


    ////vignettes
    void selectGroup(String group, boolean select) {
//	var frame:Number = getFrame(group);
//	var hasSelected:Boolean = false;
//	for(var eleve:Number=1; eleve<=vignetteMax; eleve++) {
//		if(vignetteList[eleve].currentFrame == frame) {
//			vignetteList[eleve].mcCheck.visible = select;
//		}
//		if(vignetteList[eleve].mcCheck.visible)
//			hasSelected = true;
//	}
//	updateGroupFunctions(hasSelected);
    }

    int getFrame(String group) {
        return (group.charAt(0) - "A".charAt(0) + 1);
    }

    void setVignetteGroup(int eleve, String group) {
//	var frame:Number = getFrame(group);
//	var vignette:Vignette = vignetteList[eleve];
//	vignette.gotoAndStop(frame);
    }

    void hideVignette(int eleve) {
//	var vignette:Vignette = vignetteList[eleve];
//	vignette.visible = false;
//	vignette.onLine = false;
    }

    void modifyVignette(int eleve, String eleveName, String battery) {
//	if(this.mcLaboratory.visible) {
//		return;
// 	}
//	
//	var vignette:Vignette = vignetteList[eleve];
//	if(!vignette.visible)
//		vignette.visible = true;
//		
//	if(battery == "-1")
//		battery = "100";
//	
//	vignette.mcBattery.text = battery + " %";
//	vignette.mcName.text = eleveName;
//	vignette.onLine = true;
    }

    void showNoteMessage(int eleve, String messageText) {
//	var vignette:Vignette = vignetteList[eleve];
//	centerText(vignette.mcNote.mcMessage, messageText);
//	vignette.mcNote.visible = true;
    }

    void showGroupSelection(boolean visible) {
//	var hasGroupSupp:Boolean = false;
//	for(var eleve:Number=1; eleve<=vignetteMax; eleve++) {
//		vignetteList[eleve].mcGroupSelection.visible = visible;
//		if(vignetteList[eleve].currentFrame > 4)
//			hasGroupSupp = true;
// 	}
//	
//	showGroupSuppButtons(!visible && hasGroupSupp);
    }

    void showAllVignettes(boolean visible) {
//	for(var eleve:Number=1; eleve<=vignetteMax; eleve++) {
//		vignetteList[eleve].visible = visible && vignetteList[eleve].onLine;
// 	}
    }

    void selectVignette(int eleve, boolean select) {
//	vignetteList[eleve].mcSelection.visible = select;
    }

    void initVignette(int eleve) {
//	var vignette:Vignette = new Vignette();
//
//	vignette.iIndex = eleve;
//	vignette.onLine = false;
//	
//	vignette.visible = false;
//	vignette.mcSelection.visible = false;
//	vignette.mcCheck.visible = false;
//	vignette.mcGroupSelection.visible = false;
//	vignette.mcNote.visible = false;
//	
//	vignette.addEventListener(MouseEvent.CLICK, onClickVignetteEvent);
//
//	vignetteList.push(vignette);
//	this.addChild(vignette);
    }

    void onClickVignetteEvent(Object event) {//:Event
//	var vignette = event.currentTarget;
//	if(vignette.alpha > 0.5) {
//		fireVignetteClicked(vignette, event.target);
//	}
    }


////supervision
//mcSupervision.mcMessageDialog.visible = false;
//mcSupervision.mcSendChoices.visible = false;
//mcSupervision.mcCloseChoices.visible = false;
//mcSupervision.mcValidePairing.visible = false;
//
//mcSupervision.mcSendChoices.rdScreenAndVoice.selected = true;
//mcSupervision.mcCloseChoices.rdPower.selected = true;
//
//addButtonEvents(mcSupervision.mcListening);
//addButtonEvents(mcSupervision.mcStudentControl);
//addButtonEvents(mcSupervision.mcScanning);
//addButtonEvents(mcSupervision.mcMosaique);
//
//addButtonEvents(mcSupervision.mcScreenAndVoice);
//addButtonEvents(mcSupervision.mcStudentScreen);
//addButtonEvents(mcSupervision.mcSendFile);
//addButtonEvents(mcSupervision.mcBlackScreen);
//addButtonEvents(mcSupervision.mcSendText);
//
//addButtonEvents(mcSupervision.mcPairing);
//addButtonEvents(mcSupervision.mcValidePairing);
//addButtonEvents(mcSupervision.mcGroupCreation);
//addButtonEvents(mcSupervision.mcStudentClose);
//
//mcSupervision.mcMessageDialog.button.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//mcSupervision.mcSendChoices.button.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//mcSupervision.mcCloseChoices.button.addEventListener(MouseEvent.CLICK, onClickButtonEvent);

    void updateSupervisionGroupFunctions(boolean hasGroup) {
//	var alphaValue:Number = hasGroup ? alphaActif : alphaNonActif;
//	
//	mcSupervision.mcScanning.alpha = alphaValue;
//	mcSupervision.mcMosaique.alpha = alphaValue;
//	mcSupervision.mcScreenAndVoice.alpha = alphaValue;
//	mcSupervision.mcStudentScreen.alpha = alphaValue;
//	mcSupervision.mcSendFile.alpha = alphaValue;
//	mcSupervision.mcBlackScreen.alpha = alphaValue;
//	mcSupervision.mcSendText.alpha = alphaValue;
//	mcSupervision.mcStudentClose.alpha = alphaValue;
//	
//	mcSupervision.mcGroupCreation.alpha
//		= hasGroup ? alphaNonActif : alphaActif;
    }

    void updateSupervisionButtons(Object button, boolean block) {//:MovieClip
//	var alphaValue:Number = (block) ? alphaNonActif : alphaActif;
//
//	mcSupervision.mcListening.alpha = alphaValue;
//	mcSupervision.mcStudentControl.alpha = alphaValue;
//	mcSupervision.mcScanning.alpha = alphaValue;
//	mcSupervision.mcMosaique.alpha = alphaValue;
//	
//	mcSupervision.mcScreenAndVoice.alpha = alphaValue;
//	mcSupervision.mcStudentScreen.alpha = alphaValue;
//	mcSupervision.mcSendFile.alpha = alphaValue;
//	mcSupervision.mcBlackScreen.alpha = alphaValue;
//	mcSupervision.mcSendText.alpha = alphaValue;
//	
//	mcSupervision.mcPairing.alpha = alphaValue;
//	mcSupervision.mcValidePairing.alpha = alphaValue;
//	mcSupervision.mcGroupCreation.alpha = alphaValue;
//	mcSupervision.mcStudentClose.alpha = alphaValue;
//	
//	button.alpha = alphaActif;
//	
//	if(!block) {
//		mcSupervision.mcSendChoices.visible = false;
//		mcSupervision.mcMessageDialog.visible = false;
//		mcSupervision.mcCloseChoices.visible = false;
//	}
    }

////security
//securityMask.visible = false;

    void secureButtons(boolean secure) {
//	//mise au premier plan
//	if(secure) {
//		securityMask.parent.setChildIndex(
//				securityMask, securityMask.parent.numChildren-1);
//	}
//	
//	securityMask.visible = secure;
    }


////QCM
//addButtonEvents(mcQCM.mcJClic);
//addButtonEvents(mcQCM.mcJClicReports);

    void updateQCMGroupFunctions(boolean hasGroup) {
//	mcQCM.mcJClic.alpha = hasGroup ? alphaActif : alphaNonActif;
    }


////laboratory
//var currentSlider:MovieClip = null;
//
//mcLaboratory.mcPause.gotoAndStop(2);
//mcLaboratory.mcPause.mcBubble.visible = false;
//updateTimeCounterText(0, 0);
//
//addButtonEvents(mcLaboratory.mcRapatriate);
//addButtonEvents(mcLaboratory.mcDiffuse);
//addButtonEvents(mcLaboratory.mcBlock);
//addButtonEvents(mcLaboratory.mcMediaSend);
//addButtonEvents(mcLaboratory.mcMediaErase);
//addButtonEvents(mcLaboratory.mcFullScreen);
//
//addButtonEvents(mcLaboratory.mcMediaLoad);
//addButtonEvents(mcLaboratory.mcBack);
//addButtonEvents(mcLaboratory.mcPlay);
//addButtonEvents(mcLaboratory.mcPause);
//addButtonEvents(mcLaboratory.mcRecord);
//
//addButtonEvents(mcLaboratory.mcAudioSend);
//addButtonEvents(mcLaboratory.mcAudioSave);
//addButtonEvents(mcLaboratory.mcAudioErase);
//
//addButtonEvents(mcLaboratory.mcLanguage);
//addButtonEvents(mcLaboratory.mcTimeMax);
//addButtonEvents(mcLaboratory.mcMessage);
//
//addButtonEvents(mcLaboratory.mcTextSend);
//addButtonEvents(mcLaboratory.mcTextSave);
//addButtonEvents(mcLaboratory.mcTextErase);
//
//addButtonEvents(mcLaboratory.mcTextLoad);
//addButtonEvents(mcLaboratory.mcMasterTextSave);
//addButtonEvents(mcLaboratory.mcMasterTextErase);
//
//addButtonEvents(mcLaboratory.mcLanceLabo);
//addButtonEvents(mcLaboratory.mcCloseLabo);
//
//mcLaboratory.mcMediaVolume.addEventListener(MouseEvent.MOUSE_DOWN, onPressSliderEvent);
//mcLaboratory.mcAudioVolume.addEventListener(MouseEvent.MOUSE_DOWN, onPressSliderEvent);
//mcLaboratory.mcTextSlider.addEventListener(MouseEvent.MOUSE_DOWN, onPressSliderEvent);
//mcLaboratory.mcTime.addEventListener(MouseEvent.MOUSE_DOWN, onPressSliderEvent);

    void updateLaboratoryGroupFunctions(boolean hasGroup) {
//	var alphaValue:Number = hasGroup ? alphaActif: alphaNonActif;
//	
//	mcLaboratory.mcLanceLabo.alpha = alphaValue;
//	mcLaboratory.mcCloseLabo.alpha = alphaValue;
//	
//	mcLaboratory.mcRapatriate.alpha = alphaValue;
//	mcLaboratory.mcDiffuse.alpha = alphaValue;
//	mcLaboratory.mcBlock.alpha = alphaValue;
//	
//	mcLaboratory.mcMediaSend.alpha = (mediatype == "unload")
//			? alphaNonActif : alphaValue;
//	mcLaboratory.mcFullScreen.alpha = alphaValue;
//
//	mcLaboratory.mcRecord.alpha = alphaValue;
//		
//	mcLaboratory.mcAudioSend.alpha = alphaValue;
//	mcLaboratory.mcAudioSave.alpha = alphaValue;
//	mcLaboratory.mcAudioErase.alpha = alphaValue;
//	
//	mcLaboratory.mcLanguage.alpha = alphaValue;
//	mcLaboratory.mcTimeMax.alpha = alphaValue;
//	mcLaboratory.mcMessage.alpha = alphaValue;
//	
//	mcLaboratory.mcTextSend.alpha = alphaValue;
//	mcLaboratory.mcTextSave.alpha = alphaValue;
//	mcLaboratory.mcTextErase.alpha = alphaValue;
    }

    void updateLaboratoryButtons(String runningState, String mediatype) {
//	var isUnload:Boolean = (mediatype == "unload");
//        var isImage:Boolean = (mediatype == "image");
//        var isPlaying:Boolean = (runningState == "playing");
//        var isRecording:Boolean = (runningState == "recording");
//        var isStop:Boolean = (runningState == "pause");
//	var isDiffuse:Boolean = (mcLaboratory.mcDiffuse.currentFrame == 2);
//	
//	mcLaboratory.mcPlay.gotoAndStop(isPlaying ? 2 : 1);
//	mcLaboratory.mcRecord.gotoAndStop(isRecording ? 2 : 1);
//	mcLaboratory.mcPause.gotoAndStop(isStop ? 2 : 1);
//
//	mcLaboratory.mcRapatriate.alpha
//		= (hasGroupState && isStop && !isDiffuse) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcDiffuse.alpha
//		= (hasGroupState && isStop) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcBlock.alpha
//		= (hasGroupState) ? alphaActif : alphaNonActif;
//	
//	mcLaboratory.mcMediaSend.alpha
//		= (hasGroupState && isStop && !isUnload && !isDiffuse)
//			? alphaActif : alphaNonActif;
//	mcLaboratory.mcMediaErase.alpha
//		= (isStop && !isUnload && !isDiffuse) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcFullScreen.alpha
//		= (hasGroupState && !isUnload && !isDiffuse)
//			? alphaActif : alphaNonActif;
//
//	mcLaboratory.mcMediaLoad.alpha
//		= (isStop) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcBack.alpha
//		= (isStop) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcPlay.alpha
//		= (!isPlaying) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcPause.alpha
//		= (isPlaying || isRecording) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcRecord.alpha
//		= (hasGroupState && !isRecording) ? alphaActif : alphaNonActif;
//	
//	mcLaboratory.mcAudioSend.alpha
//		= (hasGroupState && isStop) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcAudioSave.alpha
//		= (hasGroupState && isStop) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcAudioErase.alpha
//		= (hasGroupState && isStop) ? alphaActif : alphaNonActif;
//	
//        mcLaboratory.mcLanguage.alpha
//		= (hasGroupState) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcTimeMax.alpha
//		= (hasGroupState && isStop && (isUnload || isImage)) 
//			? alphaActif : alphaNonActif;
//	mcLaboratory.mcMessage.alpha
//		= (hasGroupState) ? alphaActif : alphaNonActif;
//	
//	mcLaboratory.mcTextSend.alpha
//		= (hasGroupState) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcTextSave.alpha
//		= (hasGroupState) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcTextErase.alpha
//		= (hasGroupState) ? alphaActif : alphaNonActif;
//	
//	mcLaboratory.mcTextLoad.alpha = alphaActif;
//	mcLaboratory.mcMasterTextSave.alpha = alphaActif;
//	mcLaboratory.mcMasterTextErase.alpha = alphaActif;
//	
//	mcLaboratory.mcMediaVolume.mcCursor.alpha = alphaActif;
//	mcLaboratory.mcAudioVolume.mcCursor.alpha
//		= (hasGroupState) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcTextSlider.mcCursor.alpha
//		= (hasGroupState) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcTime.mcCursor.alpha = alphaActif;
//			
//	mcLaboratory.mcLanceLabo.alpha
//		= (hasGroupState && isStop) ? alphaActif : alphaNonActif;
//	mcLaboratory.mcCloseLabo.alpha
//		= (hasGroupState && isStop) ? alphaActif : alphaNonActif;
    }

    void updateFullScreenButton(boolean fullScreen) {
//	if(fullScreen) {
//		mcLaboratory.mcFullScreen.gotoAndStop(2);
//	} else {
//		mcLaboratory.mcFullScreen.gotoAndStop(1);
//	}
    }

    void updateBlockButton(boolean freeze) {
//	if(freeze) {
//		mcLaboratory.mcBlock.gotoAndStop(2);
//	} else {
//		mcLaboratory.mcBlock.gotoAndStop(1);
//	}
    }

    void setSliderPosition(Object mcSlider, double position) {//:MovieClip
//	if(mcSlider == this.currentSlider)
//		return;
//	
//	mcSlider.mcCursor.x
//		= position*(mcSlider.max-mcSlider.min) + mcSlider.min;
    }

    void updateTimeCounterText(long currentTime, long timeMax) {
//	var display:String;
//	var minute:int = int(currentTime/1000/60);
//	var second:int = int(currentTime/1000)%60;
//	var one:Boolean = (second < 10);
//			
//	display = minute.toString() + ":";
//	if(one) display += "0";
//	display += second.toString() + " / ";
//			
//	minute = int(timeMax/1000/60);
//	second = int(timeMax/1000)%60;
//	one = (second < 10);
//			
//	display += minute.toString() + ":";
//	if(one) display += "0";
//	display += second.toString();
//	
//	mcLaboratory.mcTimeCounter.label.text = display;
    }

    /**
     * Event pour la pression sur la souris sur un slider. Déclenche le drag du curseur du slider. Affecte le slider
     * courant (this.currentSlider). Affecte l'état précédent (this.precState). Affecte des listener sur la scène
     * globale (stage) pour le mouvement, et le relachement de la souris.
     *
     * @param event l'évènement déclencheur.
     */
    void onPressSliderEvent(Object event) {
//	var mcSlider = event.currentTarget;
//		
//	if(mcSlider.mcCursor.alpha > 0.5) {
////		_root.mcLaboProf.sendLaboCommand("mcPause", false);
//		
//		var bounds:Rectangle
//				= new Rectangle(mcSlider.min, mcSlider.mcCursor.y,
//				mcSlider.max-mcSlider.min, 0);
//		this.currentSlider = mcSlider;
//		this.precState = this.runningState;
////		if(this.runningState != PAUSE)
////			buttonClicked(mcPlay);
//		addStageSliderEvent();
//		mcSlider.mcCursor.startDrag(false, bounds);
//	}
    }

    void onMoveSliderEvent(Object event) {
//	var mcSlider = this.currentSlider;
//	if(mcSlider != null) {
//		var position:Number
//			= (mcSlider.mcCursor.x-mcSlider.min)/(mcSlider.max-mcSlider.min);
//	
//		if(mcSlider == mcLaboratory.mcTime) {
//			var time:Number = int(position * this.timeMax);
//			setTime(time, true);
//		}
//		else {
//			var percent:Number = int(position * 100);
//			setSliderPercent(mcSlider, percent, true);
//		}
//	}
    }

    /**
     * Event pour le relechement de la souris sur un slider. Arrête le drag du curseur du slider. Gére l'envoi de
     * commande associée au slider courant. Réinitialise le slider courant (this.currentSlider). Supprime les listener
     * sur la scène globale (stage) pour le mouvement, et le relachement de la souris.
     *
     * @param event l'évènement déclencheur.
     */
    void onReleaseSliderEvent(Object event) {
//	var mcSlider = this.currentSlider;
//	if(mcSlider != null) {
//		mcSlider.mcCursor.stopDrag();
//		
//		//repositionnement pour un click
//		var x:Number = event.stageX-mcSlider.x-(mcSlider.mcCursor.width-1)/2;
//		if(x > mcSlider.max)
//			x = mcSlider.max;
//		else if(x < mcSlider.min)
//			x = mcSlider.min;
//		mcSlider.mcCursor.x = x;
//
//		onMoveSliderEvent(event);
//		
//		removeStageSliderEvent();
//		this.currentSlider = null;
//		
//		fireSliderClicked(mcSlider);
//
////		if(this.precState == PLAY){
////			buttonClicked(mcPlay);
////		}
//	}
    }

    /**
     * Affecte des listener sur la scène globale (stage) pour le mouvement, et le relachement de la souris.
     */
    void addStageSliderEvent() {
//	//evénèment pour les relachemenents en dehors des sliders
//	stage.addEventListener(MouseEvent.MOUSE_UP, onReleaseSliderEvent);
//	stage.addEventListener(MouseEvent.MOUSE_MOVE, onMoveSliderEvent);
    }

    /**
     * Supprime les listener sur la scène globale (stage) pour le mouvement, et le relachement de la souris.
     */
    void removeStageSliderEvent() {
//	//evénèment pour les relachemenents en dehors des sliders
//	stage.addEventListener(MouseEvent.MOUSE_UP, onReleaseSliderEvent);
//	stage.addEventListener(MouseEvent.MOUSE_MOVE, onMoveSliderEvent);
    }


    void addIndex(Object index) {//:Index
//	var beginTime:Number = index.getInitialTime();
//	var endTime:Number = index.getFinalTime();
//	var type:String = index.getType();
//
//	var mcIndex:MovieClip;
//	if(type == "record")
//		mcIndex = new RecordIndex();
//	else
//		mcIndex = new PlayIndex();
//
//	mcIndex.x = mcLaboratory.mcTimer.min
//		+ (beginTime/this.timeMax)*(mcLaboratory.mcTime.max-mcLaboratory.mcTime.min)
//		+ mcLaboratory.mcTime.mcCursor.width/2;
//	mcIndex.y = mcLaboratory.mcTime.mcCursor.y;
//	mcIndex.width = (endTime-beginTime)/this.timeMax * (mcLaboratory.mcTime.max-mcLaboratory.mcTime.min);
//	mcIndex.height = mcLaboratory.mcTime.mcCursor.height;
//	mcIndex.alpha = 0.75;
//	mcIndex.visible = true;
//
//	indexList.push(mcIndex);
//	//mcLaboratory.mcTime.swapDepths(6000);
//	mcLaboratory.mcTime.addChild(mcIndex);
    }

    void removeAllIndex() {
//	var nbIndexes:Number = indexList.length;
//	for(var i:Number=0; i<nbIndexes; i++) {
//		mcLaboratory.mcTime.removeChild(indexList[i]);
//	}
//	indexList = new Array();
    }


///autorisations
//mcAutorisations.visible = false;
//mcAutorisations.mcConfirmDelete.visible = false;
//
//addButtonEvents(mcAutorisations.mcApplication);
//addButtonEvents(mcAutorisations.mcKeyboard);
//addButtonEvents(mcAutorisations.mcInternet);
//addButtonEvents(mcAutorisations.mcDeleteDocument);
//
//mcAutorisations.mcConfirmDelete.yes.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//mcAutorisations.mcConfirmDelete.no.addEventListener(MouseEvent.CLICK, onClickButtonEvent);

    void updateAutorisationsGroupFunctions(boolean hasGroup) {
//	var alphaValue:Number = hasGroup ? alphaActif : alphaNonActif;
//	mcAutorisations.mcApplication.alpha = alphaValue;
//	mcAutorisations.mcKeyboard.alpha = alphaValue;
//	mcAutorisations.mcInternet.alpha = alphaValue;
//	mcAutorisations.mcDeleteDocument.alpha = alphaValue;
    }


////main buttons
//mcLanguageChoices.mcFrench.language.text = "Français";
//mcLanguageChoices.mcEnglish.language.text = "English";
//mcLanguageChoices.mcSpanish.language.text = "Español";
//mcLanguageChoices.mcGerman.language.text = "Deutsch";
//mcLanguageChoices.mcItalian.language.text = "Italiano";
//
//var hasGroupState:Boolean = false;
//updateGroupFunctions(false);
//
//mcLanguageChoices.visible = false;
//mcGroups.mcSuppGroups.visible = false;
//mcGroups.mcArrow.visible = false;
//
//mcBar.mcHelp.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//mcBar.mcMini.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//mcBar.mcClose.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//
//mcLanguage.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//mcLanguageChoices.mcFrench.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//mcLanguageChoices.mcEnglish.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//mcLanguageChoices.mcSpanish.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//mcLanguageChoices.mcGerman.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//mcLanguageChoices.mcItalian.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//
//mcGroups.mcArrow.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//addButtonEvents(mcGroups.mcGroupA);
//addButtonEvents(mcGroups.mcGroupB);
//addButtonEvents(mcGroups.mcGroupC);
//addButtonEvents(mcGroups.mcGroupD);
//addButtonEvents(mcGroups.mcSuppGroups.mcGroupE);
//addButtonEvents(mcGroups.mcSuppGroups.mcGroupF);
//addButtonEvents(mcGroups.mcSuppGroups.mcGroupG);
//addButtonEvents(mcGroups.mcSuppGroups.mcGroupH);

    void updateButtons(Object button, boolean block) {
//	blockGroupButtons(block);
//	updateSupervisionButtons(button, block);
//	if(!block)
//		updateGroupFunctions(hasGroupState);
    }

    void updateGroupFunctions(boolean hasGroup) {
//	this.hasGroupState = hasGroup;
//	updateSupervisionGroupFunctions(hasGroup);
//	updateLaboratoryGroupFunctions(hasGroup);
//	updateQCMGroupFunctions(hasGroup);
//	updateAutorisationsGroupFunctions(hasGroup);
    }

    void blockGroupButtons(boolean block) {
//	var alphaValue:Number = (block) ? alphaNonActif : alphaActif;
//	mcGroups.mcGroupA.alpha = alphaValue;
//	mcGroups.mcGroupB.alpha = alphaValue;
//	mcGroups.mcGroupC.alpha = alphaValue;
//	mcGroups.mcGroupD.alpha = alphaValue;
//	mcGroups.mcSuppGroups.mcGroupE.alpha = alphaValue;
//	mcGroups.mcSuppGroups.mcGroupF.alpha = alphaValue;
//	mcGroups.mcSuppGroups.mcGroupG.alpha = alphaValue;
//	mcGroups.mcSuppGroups.mcGroupH.alpha = alphaValue;
//	mcGroups.mcArrow.alpha = alphaValue;
    }

    void showGroupSuppButtons(boolean group_supp) {
//	if(group_supp == true) {
//		mcGroups.mcArrow.visible = true;
//		mcGroups.mcArrow.gotoAndStop(1);
//	} else {
//		mcGroups.mcArrow.visible = false;
//		mcGroups.mcSuppGroups.visible = false;
//	}
    }

    void addButtonEvents(Object mcButton) {
//	mcButton.addEventListener(MouseEvent.ROLL_OVER, onRollOverButtonEvent);
//	mcButton.addEventListener(MouseEvent.ROLL_OUT, onRollOutButtonEvent);
//	mcButton.addEventListener(MouseEvent.CLICK, onClickButtonEvent);
//	mcButton.mcBubble.mcHelp.addEventListener(MouseEvent.ROLL_OVER, onRollOverBubbleEvent);
    }

    void onClickButtonEvent(Object event) {
//	if(event.target is Bubble || event.target.parent is Bubble)
//		return;
//
//	var mc = event.currentTarget;
//	if(mc.alpha > 0.5) {
//		fireButtonClicked(mc);
//	}
    }

    void onRollOverButtonEvent(Object event) {
//	if(event.eventPhase == EventPhase.AT_TARGET) {
//		var mc = event.target;
//		if(mc.alpha > 0.5) {
//			var offset:int = 0;
////			if(mcTextEditor.visible)
////				offset = 1;
//			//mise au premier plan
//			mc.parent.setChildIndex(mc, mc.parent.numChildren-1-offset);
//			mc.mcBubble.visible = true;
//		}
//	}
    }

    void onRollOutButtonEvent(Object event) {
//	if(event.eventPhase == EventPhase.AT_TARGET) {
//		var mc = event.target;
//		mc.mcBubble.visible = false;
//	}
    }

    void onRollOverBubbleEvent(Object event) {
//	if(event.eventPhase == EventPhase.AT_TARGET) {
//		var mcBubble = event.target.parent;
//		mcBubble.visible = false;
//	}
    }

/////messages
//messageDialog.visible = false;
//aboutDialog.visible = false;
//
//messageDialog.yesButton.addEventListener(MouseEvent.CLICK, onClickMessageEvent);
//messageDialog.noButton.addEventListener(MouseEvent.CLICK, onClickMessageEvent);
//aboutDialog.yesButton.addEventListener(MouseEvent.CLICK, onClickMessageEvent);

    void showMessage(String message) {
//	messageDialog.yesButton.text.text = yes;
//	messageDialog.noButton.text.text = no;
//	
//	messageDialog.message.htmlText = message;
//	
//	secureButtons(true);
//	//mise au premier plan
//	this.setChildIndex(messageDialog, this.numChildren-1);
//	messageDialog.visible = true;
    }

    void showAbout(String message) {
//	aboutDialog.yesButton.text.text = "ok";
//	aboutDialog.message.htmlText = message;
//	
//	secureButtons(true);
//	//mise au premier plan
//	this.setChildIndex(aboutDialog, this.numChildren-1);
//	aboutDialog.visible = true;
    }

    void onClickMessageEvent(Object event) {
//	var mc = event.currentTarget;
//	if(mc == messageDialog.yesButton) {
//		closeWindow();
//	}
//	messageDialog.visible = false;
//	aboutDialog.visible = false;
//	secureButtons(false);
    }

}
