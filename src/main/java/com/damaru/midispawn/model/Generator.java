/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.damaru.midispawn.model;

import java.io.File;
import java.util.Random;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import org.apache.log4j.Logger;

/**
 *
 * @author mike
 */
public class Generator {
    private static Logger log = Logger.getLogger(Generator.class);
    public static final int PPQ = 480;
    public static final int PPS = PPQ * 2; // pulses per second
    public static final double LEGATO = 0.9;
    private Sequence sequence;
    private Track track;
    private Random random = new Random();
    private long currentTick = 0;

    public Generator() throws Exception {
        sequence = new Sequence(Sequence.PPQ, PPQ);
        track = sequence.createTrack();
    }

    public void createTrack() {
        track = sequence.createTrack();
    }

    public void setProgram(int program) throws Exception {
        ShortMessage message = new ShortMessage();
        message.setMessage(ShortMessage.PROGRAM_CHANGE, program, program);
        MidiEvent event = new MidiEvent(message, currentTick);
        track.add(event);
    }

    public void addNote() throws Exception {
        addNote(PPQ);
    }

    public void addNote(int length) throws Exception {
        int key = Rand.getInt64(30);
        addNote(length, key);
    }

    public void addNote(int key, int length) throws Exception {
        int velocity = Rand.getInt64(40);
        addNote(key, length, velocity);
    }

    public void addNote(int key, int length, int velocity) throws Exception {
        int duration = (int) (length * LEGATO);
        log.debug("key: " + key + " length: " + length + " vel: " + velocity + " currentTick: " + currentTick + " end: " + (currentTick + duration) + " duration: " + duration);
        track.add(createNoteOnEvent(key, velocity, currentTick));
        track.add(createNoteOffEvent(key, currentTick + duration));
        currentTick += length;
    }

    private static MidiEvent createNoteOnEvent(int key, int velocity, long tick) throws Exception {
        //log.debug("key: " + key + " vel: " + velocity + " tick: " + tick);
        return createNoteEvent(ShortMessage.NOTE_ON, key, velocity, tick);
    }

    private static MidiEvent createNoteOffEvent(int key, long tick) throws Exception {
        return createNoteEvent(ShortMessage.NOTE_OFF, key, 0, tick);
    }

    private static MidiEvent createNoteEvent(int command, int key, int velocity, long tick) throws Exception {
        //log.debug("command: " + command + " key: " + key + " vel: " + velocity + " tick: " + tick);
        ShortMessage message = new ShortMessage();
        message.setMessage(command,
                0, // always on channel 1
                key,
                velocity);
        MidiEvent event = new MidiEvent(message, tick);
        return event;
    }

    public static int transpose(int source, int sourceMin, int sourceMax, int targetMin, int targetMax, boolean reverse) {
        int ret = 0;
        int sourceRange = sourceMax - sourceMin;
        int targetRange = targetMax - targetMin;
        double sourceOffset = source - sourceMin;
        double sourceProportion = sourceOffset / sourceRange;
        double targetProportion = reverse ? 1.0 - sourceProportion : sourceProportion;
        int targetOffset = (int) (targetRange * targetProportion);
        ret = targetMin + targetOffset;
//        log.debug("sr: " + sourceRange + " tr: " + targetRange + " so: " + sourceOffset + " sp: " + sourceProportion
//                + " tp: " + targetProportion + " to: " + targetOffset + " ret: " + ret);
        return ret;
    }

    public void writeFile(String fileName) throws Exception {
        File outputFile = new File(fileName);
        MidiSystem.write(sequence, 1, outputFile);
    }

    public static int method1(Generator generator) throws Exception {
        log.debug("method1 start =================================================================");
        int ret = 0;
        int numNotes = 200;
        for (int i = 0; i < numNotes; i++) {
            int length = transpose(i, 0, numNotes, 20, 300, true);
            int keyCentre = transpose(i, 0, numNotes, 40, 100, false);
            int keyRange = transpose(i, 0, numNotes, 5, 40, true);
            int key = Rand.getIntCentred(keyCentre, keyRange);
            log.debug("key: " + key + " length: " + length);
            generator.addNote(key, length);
            ret += length;
        }
        log.debug("--------------------------------------------------------------------------------");
        numNotes /= 1.5;
        for (int i = 0; i < numNotes; i++) {
            int length = transpose(i, 0, numNotes, 20, 300, false);
            int keyCentre = transpose(numNotes - i, 0, numNotes, 40, 100, false);
            int keyRange = transpose(numNotes - i, 0, numNotes, 5, 40, true);
            int key = Rand.getIntCentred(keyCentre, keyRange);
            log.debug("key: " + key + " length: " + length);
            generator.addNote(key, length);
            ret += length;
        }
        log.debug("method1 end ===================================================================");
        return ret;
    }

    public static int method2(Generator generator) throws Exception {
        log.debug("method2 start =================================================================");
        int ret = 0;
        int numNotes = 200;
        Interpolator lengthInterpolator = new Interpolator(numNotes, 300, 20);
        RangeSequencer keySequencer = new RangeSequencer(numNotes, 20, 60, 97, 103);
        for (int i = 0; i < numNotes; i++) {
            int length = lengthInterpolator.next();
            int key = keySequencer.next();
            log.debug("key: " + key + " length: " + length);
            generator.addNote(key, length);
            ret += length;
        }
        double secs = ret / (double) PPS;

        log.debug("-------------------------------------------------------------------------------- " + secs);
        numNotes /= 1.5;
        lengthInterpolator = new Interpolator(numNotes, 20, 300);
        keySequencer = new RangeSequencer(numNotes, 97, 103, 20, 60);
        int ret2 = 0;
        for (int i = 0; i < numNotes; i++) {
            int length = lengthInterpolator.next();
            int key = keySequencer.next();
            log.debug("key: " + key + " length: " + length);
            generator.addNote(key, length);
            ret2 += length;
        }
        secs = ret2 / (double) PPS;
        log.debug("method2 end =================================================================== " + secs);
        return ret + ret2;
    }

    public static int method3(Generator generator) throws Exception {
        log.debug("method3 start =================================================================");
        int ret = 0;
        double targetSecs = 8.0;
        Interpolator lengthInterpolator = Interpolator.createInterpolator(300, 20, targetSecs * PPS);
        int numNotes = lengthInterpolator.getNumSteps();
        RangeSequencer keySequencer = new RangeSequencer(numNotes, 20, 60, 97, 103);
        for (int i = 0; i < numNotes; i++) {
            int length = lengthInterpolator.next();
            int key = keySequencer.next();
            log.debug("key: " + key + " length: " + length);
            generator.addNote(key, length);
            ret += length;
        }
        double secs = ret / (double) PPS;

        log.debug("-------------------------------------------------------------------------------- " + secs + " " + numNotes);
        targetSecs = 4.0;
        lengthInterpolator = Interpolator.createInterpolator(20, 300, targetSecs * PPS);
        keySequencer = new RangeSequencer(numNotes, 97, 103, 20, 60);
        int ret2 = 0;
        numNotes = lengthInterpolator.getNumSteps();
        for (int i = 0; i < numNotes; i++) {
            int length = lengthInterpolator.next();
            int key = keySequencer.next();
            log.debug("key: " + key + " length: " + length);
            generator.addNote(key, length);
            ret2 += length;
        }
        secs = ret2 / (double) PPS;
        log.debug("method3 end =================================================================== " + secs + " " + numNotes);
        return ret + ret2;
    }

    public static void make10() throws Exception {
        Generator generator = new Generator();

        int len = 0;
        int numNotes = 10;
        int length = PPS;
        RangeSequencer keySequencer = new RangeSequencer(numNotes, 20, 60, 97, 103);
        for (int i = 0; i < numNotes; i++) {
            int key = keySequencer.next();
            log.debug("key: " + key + " length: " + length);
            generator.addNote(key, length);
            len += length;
        }
        generator.writeFile("ten.mid");
        double secs = len / (double) PPS;
        String format = "len1: %d %2.4f";
        String msg = String.format(format, len, secs);
        log.debug(msg);
    }

    public static void doSection(Generator generator, int secs, int tempoStartLow, int tempoStartHigh, int tempoEndLow, int tempoEndHigh,
            int pitchStartLow, int pitchStartHigh, int pitchEndLow, int pitchEndHigh,
            int velStartLow, int velStartHigh, int velEndLow, int velEndHigh) throws Exception {
        double averageTempoStart = (tempoStartLow + tempoStartHigh) / 2.0;
        double averageTempoEnd = (tempoEndLow + tempoEndHigh) / 2.0;
        int numNotes = Interpolator.calculateNumSteps(averageTempoStart, averageTempoEnd, secs * PPS);
        RangeSequencer keySequencer = new RangeSequencer(numNotes, pitchStartLow, pitchStartHigh, pitchEndLow, pitchEndHigh);
        RangeSequencer tempoSequencer = new RangeSequencer(numNotes, tempoStartLow, tempoStartHigh, tempoEndLow, tempoEndHigh);
        RangeSequencer velocitySequencer = new RangeSequencer(numNotes, velStartLow, velStartHigh, velEndLow, velEndHigh);

        for (int i = 0; i < numNotes; i++) {
            int key = keySequencer.next();
            int length = tempoSequencer.next();
            int velocity = velocitySequencer.next();
            generator.addNote(key, length, velocity);
        }
    }
    
    public Sequence getSequence() {
        return sequence;
    }

    public static void main(String[] args) throws Exception {
        boolean do10 = false;
        if (do10) {
            make10();
        } else {
            Generator generator = new Generator();
            int len1 = method3(generator);
            //generator.writeFile("test1.mid");
            //generator = new Generator();
            //generator.createTrack();
            //generator.currentTick = 0L;
            //generator.setProgram(10);
            //int len2 = method3(generator);
            generator.writeFile("duet.mid");
//            generator = new Generator();
//            int len3 = method3(generator);
//            generator.writeFile("test3.mid");
//            double secs1 = len1 / (double) PPS;
//            double secs2 = len2 / (double) PPS;
//            double secs3 = len3 / (double) PPS;
//            String format = "len1: %d %2.4f   len2: %d %2.4f   len3: %d %2.4f";
//            String msg = String.format(format, len1, secs1, len2, secs2, len3, secs3);
//            log.debug(msg);
        }
    }
}
