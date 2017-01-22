package com.damaru.midispawn.midi;

import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import org.apache.log4j.Logger;

/**
 *
 * @author mdavis
 */
public class MidiUtil {
    
    private static final Logger log = Logger.getLogger(MidiUtil.class);
    private static Sequencer sequencer;
    private static Synthesizer synthesizer;
    
    public static ObservableList<MidiDevice.Info> getMidiDevices() {
        ObservableList<MidiDevice.Info> ret = FXCollections.observableArrayList();
        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            ret.add(info);
            log.debug("" + info + " " + info.getDescription());
        }
        return ret;
    }
    
    public static ObservableList<InstrumentValue> getInstruments() throws MidiUnavailableException {
        ObservableList<InstrumentValue> ret = FXCollections.observableArrayList();
        synthesizer = MidiSystem.getSynthesizer();
        Soundbank sb = synthesizer.getDefaultSoundbank();
        for (Instrument i : sb.getInstruments()) {
            ret.add(new InstrumentValue(i));
            log.debug("instrument: " + i.getName() + " " + i.getPatch().getBank() + " " + i.getPatch().getProgram());
        }
        return ret;
    }
    
    public static void playSequence(Sequence sequence) {
        try {
            if (sequencer == null) {
                sequencer = MidiSystem.getSequencer();
                MidiDevice.Info mi = sequencer.getDeviceInfo();
                log.debug("sequencer: " + mi);
                sequencer.open();
            }
            sequencer.setSequence(sequence);
            log.debug("start");
            sequencer.start();
            log.debug("end");
        } catch (MidiUnavailableException | InvalidMidiDataException ex) {
            log.error("Error", ex);
        }
    }

    public static void close() {
        if (sequencer != null &&sequencer.isOpen()) {
            sequencer.close();
        }
        
        if (synthesizer != null && synthesizer.isOpen()) {
            synthesizer.close();
        }
    }
}
