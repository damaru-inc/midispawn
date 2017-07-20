package com.damaru.midispawn.midi;

import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mdavis
 */
public class MidiUtil {

    private static final Logger log = LogManager.getLogger(MidiUtil.class);
    private static Sequencer sequencer;
    private static Synthesizer synthesizer;
    private static final ObservableList<InstrumentValue> instruments = FXCollections.observableArrayList();
    public final static int PPQ = 480; // pulses per quarter note
    public final static int MIDDLE_C = 60; // midi note number.

    public static ObservableList<MidiDevice.Info> getMidiDevices() {
        ObservableList<MidiDevice.Info> ret = FXCollections.observableArrayList();
        try {
            for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
                ret.add(info);
                MidiDevice device = MidiSystem.getMidiDevice(info);
                log.debug("" + info + " " + info.getDescription() + " rec: " + device.getMaxReceivers() 
                    + " tra: " + device.getMaxTransmitters());
            }
        } catch (MidiUnavailableException e) {
            log.error(e);
        }
        return ret;
    }

    public static ObservableList<InstrumentValue> getInstruments() throws MidiUnavailableException {
        if (instruments.isEmpty()) {
            synthesizer = MidiSystem.getSynthesizer();
            Soundbank sb = synthesizer.getDefaultSoundbank();
            for (Instrument i : sb.getInstruments()) {
                instruments.add(new InstrumentValue(i));
                // log.debug("instrument: " + i.getName() + " " +
                // i.getPatch().getBank() + " " + i.getPatch().getProgram());
            }
        }
        return instruments;
    }

    public static void playSequence(Sequence sequence) {
        try {
            if (sequencer == null) {
                sequencer = MidiSystem.getSequencer();
                MidiDevice.Info mi = sequencer.getDeviceInfo();
                float bpm = sequencer.getTempoInBPM();
                log.debug("sequencer: " + mi + " bpm: " + bpm);
                sequencer.open();
            }
            if (sequencer.isRunning()) {
                sequencer.stop();
            }
            if (sequencer.isOpen()) {
                sequencer.close();
                sequencer.open();
            }
            sequencer.setSequence(sequence);
            log.debug("start");
            sequencer.start();
            Transmitter transmitter = sequencer.getTransmitters().get(0);
            Receiver receiver = transmitter.getReceiver();
            log.debug("transmitter: " + transmitter + " receiver: " + receiver);
        } catch (MidiUnavailableException | InvalidMidiDataException ex) {
            log.error("Error", ex);
        }
    }

    public static void close() {
        if (sequencer != null && sequencer.isOpen()) {
            sequencer.close();
        }

        if (synthesizer != null && synthesizer.isOpen()) {
            synthesizer.close();
        }
    }
}
