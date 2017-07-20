package com.damaru.midispawn.midi;

import java.util.List;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    private static Receiver currentReceiver;

    public static ObservableList<MidiDevice.Info> getMidiDevices() {
        ObservableList<MidiDevice.Info> ret = FXCollections.observableArrayList();
        try {
            for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            	String name = info.getName();
                MidiDevice device = MidiSystem.getMidiDevice(info);
                if (device.getMaxReceivers() != 0 && !name.contains("sequencer")) {
                	ret.add(info);
                    log.debug(name + " rec: " + device.getMaxReceivers() 
                    + " tra: " + device.getMaxTransmitters());
                }
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

    public static void playSequence(Sequence sequence, MidiDevice.Info deviceInfo) {
        try {
            if (sequencer == null) {
                sequencer = MidiSystem.getSequencer();
                MidiDevice.Info mi = sequencer.getDeviceInfo();
                float bpm = sequencer.getTempoInBPM();
                log.debug("sequencer: " + mi + " bpm: " + bpm);
                MetaEventListener listener = new MetaEventListener()
                {
    				@Override
                    public void meta(MetaMessage event)
                    {
                        if (event.getType() == 47)
                        {
                            sequencer.close();
                            currentReceiver.close();
                            log.debug("Got end-of-track.");
                        }
                    }

                };
                sequencer.addMetaEventListener(listener);
            }
            
            if (sequencer.isRunning()) {
                sequencer.stop();
            }
            
            if (sequencer.isOpen()) {
                sequencer.close();
            }

            if (currentReceiver != null) {
            	currentReceiver.close();
            }
            
            sequencer.open();

            if (deviceInfo != null) {
            	Transmitter transmitter = null;
            	List<Transmitter> transmitters = sequencer.getTransmitters();
            	log.debug("transmitters " + transmitters.size());
            	
            	if (transmitters != null && transmitters.size() > 0) {
            		transmitter = transmitters.get(0);
            	} else {
            		transmitter = sequencer.getTransmitter();
            	}

            	MidiDevice device = MidiSystem.getMidiDevice(deviceInfo);
	            log.debug("Using device " + deviceInfo);
	            device.open();
	            currentReceiver = device.getReceiver();
	            transmitter.setReceiver(currentReceiver);
            }
            
            sequencer.setSequence(sequence);
            log.debug("start");
            sequencer.start();
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
        
        if (currentReceiver != null) {
        	currentReceiver.close();
        }
    }
}
