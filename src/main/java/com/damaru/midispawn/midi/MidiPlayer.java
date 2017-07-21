package com.damaru.midispawn.midi;

import java.util.concurrent.Future;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.damaru.midispawn.model.ClassicDurationGenerator;
import com.damaru.midispawn.model.InterpolatingSequenceGenerator;
import com.damaru.midispawn.model.MelodyGenerator;
import com.damaru.midispawn.model.RangeInterval;
import com.damaru.midispawn.model.RangeSequenceGenerator;

@Component
public class MidiPlayer {

    private Logger log = LogManager.getLogger(MidiPlayer.class);
    private static final int INTERVAL = 20; // milliseconds sleep time
    private static final int LOOPS_PER_SECOND = 1000 / INTERVAL;


    @Async
    public Future<String> play(MidiDevice midiDevice, MelodyGenerator mg, ClassicDurationGenerator dg) throws Exception {
        double tick = 0;
        double nextEvent = 0;
        double nextNoteOn = 0;
        double pulsesPerLoop = MidiUtil.PPS / LOOPS_PER_SECOND;
        midiDevice.open();
        Receiver receiver = midiDevice.getReceiver();
        log.info("playing - INTERVAL: " + INTERVAL + " pulsesPerLoop: " + pulsesPerLoop + " device: " + midiDevice);

        boolean notePlaying = false;
        int key = 0;

        try {

            while (true) {
                if (tick >= nextEvent) {
                    if (notePlaying) {
                        ShortMessage message = MidiUtil.createNoteOffMessage(key, 120);
                        receiver.send(message, (long) tick);
                        nextEvent = nextNoteOn;
                        notePlaying = false;
                    } else {
                        key = mg.next();
                        int sixteenthNotes = dg.next();
                        int duration = sixteenthNotes * MidiUtil.PULSES_PER_SIXTEENTH_NOTE;
                        int legatoLength = (int) (duration * MidiUtil.LEGATO);
                        nextEvent = tick + legatoLength;
                        nextNoteOn = tick + duration;
                        notePlaying = true;
                        ShortMessage message = MidiUtil.createNoteOnMessage(key, 120);
                        receiver.send(message,  (long) tick);
                    }
                }
                Thread.sleep(INTERVAL);
                tick += pulsesPerLoop;
            }
        } catch (InterruptedException e) {
            log.debug("interrupted....." + tick);
            if (notePlaying) {
                ShortMessage message = MidiUtil.createNoteOffMessage(key, 120);
                receiver.send(message, -1);            	
            }
            receiver.close();
        }

        return new AsyncResult<String>("finished " + tick);
    }


    @Async
    public Future<String> play(MidiDevice midiDevice, int seconds, RangeInterval keyInterval, RangeInterval durInterval,
            RangeInterval velInterval) throws Exception {
        double tick = 0;
        double nextEvent = 0;
        double nextNoteOn = 0;
        double pulsesPerLoop = MidiUtil.PPS / LOOPS_PER_SECOND;
        midiDevice.open();
        Receiver receiver = midiDevice.getReceiver();
        log.info("playing - INTERVAL: " + INTERVAL + " pulsesPerLoop: " + pulsesPerLoop + " device: " + midiDevice);

        boolean notePlaying = false;
        int key = 0;

        int numNotes = InterpolatingSequenceGenerator.calculateNumSteps(durInterval, seconds * MidiUtil.PPS);
        RangeSequenceGenerator keySequencer = new RangeSequenceGenerator(numNotes, keyInterval);
        RangeSequenceGenerator durSequencer = new RangeSequenceGenerator(numNotes, durInterval);
        RangeSequenceGenerator velSequencer = new RangeSequenceGenerator(numNotes, velInterval);

        try {

            while (true) {
                if (tick >= nextEvent) {
                    if (notePlaying) {
                        ShortMessage message = MidiUtil.createNoteOffMessage(key, 120);
                        receiver.send(message, -1);
                        nextEvent = nextNoteOn;
                        notePlaying = false;
                    } else {
                        key = keySequencer.next();
                        int duration = durSequencer.next();
                        int velocity = velSequencer.next();
                        int legatoLength = (int) (duration * MidiUtil.LEGATO);
                        nextEvent = tick + legatoLength;
                        nextNoteOn = tick + duration;
                        notePlaying = true;
                        ShortMessage message = MidiUtil.createNoteOnMessage(key, 120);
                        receiver.send(message, -1);
                    }
                }
                Thread.sleep(INTERVAL);
                tick += pulsesPerLoop;
            }
        } catch (InterruptedException e) {
            log.debug("interrupted....." + tick);
            if (notePlaying) {
                ShortMessage message = MidiUtil.createNoteOffMessage(key, 120);
                receiver.send(message, -1);            	
            }
            receiver.close();
        }

        return new AsyncResult<String>("finished " + tick);
    }    
}
