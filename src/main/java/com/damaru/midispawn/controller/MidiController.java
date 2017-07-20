package com.damaru.midispawn.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.damaru.midispawn.midi.InstrumentValue;
import com.damaru.midispawn.midi.MidiDeviceValue;
import com.damaru.midispawn.midi.MidiPlayer;
import com.damaru.midispawn.midi.MidiUtil;
import com.damaru.midispawn.model.Generator;

import javafx.fxml.Initializable;

public abstract class MidiController implements Initializable {

    private static final Logger log = LogManager.getLogger(MidiController.class);
    protected Generator generator;
    @Autowired
    protected MainController mainController;
    @Autowired 
    protected MidiPlayer midiPlayer;
    protected Future<String> future;

    public MidiController() {
        try {
            generator = new Generator();
            log.debug("generator initialized: " + this.getClass());
        } catch (Exception e) {
            log.error("Can't initialize the Generator", e);
        }
    }

    public void play() throws Exception {
        Sequence seq = generator.getSequence();
        Track[] tracks = seq.getTracks();
        Track track = null;
        int notes = 0;
        if (tracks.length > 0) {
            track = tracks[0];
            notes = track.size();
        }
        log.debug("play: " + notes);

        // Change the program
        if (notes > 0) {
            InstrumentValue instrument = mainController.getInstrument();
            int program = instrument.getProgram();

            for (int i = 0; i < notes-1; i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message.getStatus() == ShortMessage.PROGRAM_CHANGE) {
                    log.debug("Found the program change.");
                    track.remove(event);
                    break;
                }
            }

            ShortMessage message = new ShortMessage();
            message.setMessage(ShortMessage.PROGRAM_CHANGE, program, program);
            MidiEvent event = new MidiEvent(message, 0);
            track.add(event);
            
            MidiDeviceValue val = mainController.getMidiDevice();
            MidiUtil.playSequence(seq, val);
        }
    }

    public void stop() {
        MidiUtil.stopSequence();
    }
    
    public abstract void playDirect() throws Exception;

    public void stopDirect() {
        if (future != null) {
            future.cancel(true);
        }
    }

    public void generate() throws Exception {
        generator.clear();
        InstrumentValue instrument = mainController.getInstrument();
        int prog = instrument.getProgram();
        log.debug("program: " + prog);
        generator.setProgram(prog);
    }
    
    public void saveMidiFile() {
        try {
            generator.writeFile("midispawn.mid");
        } catch (Exception e) {
            log.error(e);
        }
    }
    
    public Generator getGenerator() {
    	return generator;
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
}
