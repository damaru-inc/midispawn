/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.damaru.midispawn.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.damaru.midispawn.midi.MidiUtil;

import javax.sound.midi.*;
import java.io.File;

public class Generator {
    private static Logger log = LogManager.getLogger(Generator.class);
    private Sequence sequence;
    private Track track;
    private long currentTick = 0;

    public Generator() throws Exception {
        sequence = new Sequence(Sequence.PPQ, MidiUtil.PPQ);
        track = sequence.createTrack();
        int[] supported = MidiSystem.getMidiFileTypes();
        log.debug("Midi types supported: ");
        for (int i = 0; i < supported.length; i++) {
            log.debug("" + supported[i]);
        }
        log.debug("resolution: " + sequence.getResolution() + " tick length: " + sequence.getTickLength() + " micro: " + sequence.getMicrosecondLength());
    }

    public void clear() {
        sequence.deleteTrack(track);
        track = sequence.createTrack();
        currentTick = 0;
    }

    public void createTrack() {
        track = sequence.createTrack();
    }

    public void setProgram(int program) throws Exception {
        ShortMessage message = new ShortMessage();
        message.setMessage(ShortMessage.PROGRAM_CHANGE, program, program);
        MidiEvent event = new MidiEvent(message, currentTick++);
        track.add(event);
    }

    public void setTempo(int bpm) throws Exception {
        double beatsPerSecond = bpm / 60.0;
        double secondsPerBeat = 1 / beatsPerSecond;
        long microsecsPerBeat = (long) (secondsPerBeat * 1_000_000);
        long mask = 256 * 256;
        log.debug("mask: " + mask + " mics: " + microsecsPerBeat);
        int val1 = (int) (microsecsPerBeat / mask);
        microsecsPerBeat = microsecsPerBeat - (val1 * 256 * 256);
        long mask2 = 256;
        int val2 = (int) (microsecsPerBeat / mask2);
        int val3 = (int) (microsecsPerBeat - (val2 * 256));
        long val = (val1 * 256 * 256) + (val2 * 256) + val3;
        log.debug("val1: " + val1 + " val2: " + val2 + " val3: " + val3 + " val: " + val);
        MetaMessage message = new MetaMessage();
        byte[] data = new byte[5];
        data[0] = 0x51;
        data[1] = 0x03;
        data[2] = (byte) val1;
        data[3] = (byte) val2;
        data[4] = (byte) val3;
        message.setMessage(0x51, data, 5);
        MidiEvent event = new MidiEvent(message, currentTick);
        track.add(event);
    }

    public void addNote() throws Exception {
        addNote(MidiUtil.PPQ);
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
        int duration = (int) (length * MidiUtil.LEGATO);
        log.debug("key: " + key + " length: " + length + " vel: " + velocity + " currentTick: " + currentTick + " end: " + (currentTick + duration) + " duration: " + duration);
        track.add(MidiUtil.createNoteOnEvent(key, velocity, currentTick));
        track.add(MidiUtil.createNoteOffEvent(key, currentTick + duration));
        currentTick += length;
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
        writeFile(outputFile);
    }

    public void writeFile(File outputFile) throws Exception {
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
        InterpolatingSequenceGenerator lengthInterpolator = new InterpolatingSequenceGenerator(numNotes, 300, 20);
        RangeSequenceGenerator keySequencer = new RangeSequenceGenerator(numNotes, 20, 60, 97, 103);
        for (int i = 0; i < numNotes; i++) {
            int length = lengthInterpolator.next();
            int key = keySequencer.next();
            log.debug("key: " + key + " length: " + length);
            generator.addNote(key, length);
            ret += length;
        }
        double secs = ret / (double) MidiUtil.PPS;

        log.debug("-------------------------------------------------------------------------------- " + secs);
        numNotes /= 1.5;
        lengthInterpolator = new InterpolatingSequenceGenerator(numNotes, 20, 300);
        keySequencer = new RangeSequenceGenerator(numNotes, 97, 103, 20, 60);
        int ret2 = 0;
        for (int i = 0; i < numNotes; i++) {
            int length = lengthInterpolator.next();
            int key = keySequencer.next();
            log.debug("key: " + key + " length: " + length);
            generator.addNote(key, length);
            ret2 += length;
        }
        secs = ret2 / (double) MidiUtil.PPS;
        log.debug("method2 end =================================================================== " + secs);
        return ret + ret2;
    }

    public static int method3(Generator generator) throws Exception {
        log.debug("method3 start =================================================================");
        int ret = 0;
        double targetSecs = 8.0;
        InterpolatingSequenceGenerator lengthInterpolator = InterpolatingSequenceGenerator.createInterpolator(300, 20, targetSecs * MidiUtil.PPS);
        int numNotes = lengthInterpolator.getNumSteps();
        RangeSequenceGenerator keySequencer = new RangeSequenceGenerator(numNotes, 20, 60, 97, 103);
        for (int i = 0; i < numNotes; i++) {
            int length = lengthInterpolator.next();
            int key = keySequencer.next();
            log.debug("key: " + key + " length: " + length);
            generator.addNote(key, length);
            ret += length;
        }
        double secs = ret / (double) MidiUtil.PPS;

        log.debug("-------------------------------------------------------------------------------- " + secs + " " + numNotes);
        targetSecs = 4.0;
        lengthInterpolator = InterpolatingSequenceGenerator.createInterpolator(20, 300, targetSecs * MidiUtil.PPS);
        keySequencer = new RangeSequenceGenerator(numNotes, 97, 103, 20, 60);
        int ret2 = 0;
        numNotes = lengthInterpolator.getNumSteps();
        for (int i = 0; i < numNotes; i++) {
            int length = lengthInterpolator.next();
            int key = keySequencer.next();
            log.debug("key: " + key + " length: " + length);
            generator.addNote(key, length);
            ret2 += length;
        }
        secs = ret2 / (double) MidiUtil.PPS;
        log.debug("method3 end =================================================================== " + secs + " " + numNotes);
        return ret + ret2;
    }

    public static void make10() throws Exception {
        Generator generator = new Generator();

        int len = 0;
        int numNotes = 10;
        int length = MidiUtil.PPS;
        RangeSequenceGenerator keySequencer = new RangeSequenceGenerator(numNotes, 20, 60, 97, 103);
        for (int i = 0; i < numNotes; i++) {
            int key = keySequencer.next();
            log.debug("key: " + key + " length: " + length);
            generator.addNote(key, length);
            len += length;
        }
        generator.writeFile("ten.mid");
        double secs = len / (double) MidiUtil.PPS;
        String format = "len1: %d %2.4f";
        String msg = String.format(format, len, secs);
        log.debug(msg);
    }

    public static void doSection(Generator generator, int secs, int tempoStartLow, int tempoStartHigh, int tempoEndLow, int tempoEndHigh,
            int pitchStartLow, int pitchStartHigh, int pitchEndLow, int pitchEndHigh,
            int velStartLow, int velStartHigh, int velEndLow, int velEndHigh) throws Exception {
        double averageTempoStart = (tempoStartLow + tempoStartHigh) / 2.0;
        double averageTempoEnd = (tempoEndLow + tempoEndHigh) / 2.0;
        int numNotes = InterpolatingSequenceGenerator.calculateNumSteps(averageTempoStart, averageTempoEnd, secs * MidiUtil.PPS);
        RangeSequenceGenerator keySequencer = new RangeSequenceGenerator(numNotes, pitchStartLow, pitchStartHigh, pitchEndLow, pitchEndHigh);
        RangeSequenceGenerator tempoSequencer = new RangeSequenceGenerator(numNotes, tempoStartLow, tempoStartHigh, tempoEndLow, tempoEndHigh);
        RangeSequenceGenerator velocitySequencer = new RangeSequenceGenerator(numNotes, velStartLow, velStartHigh, velEndLow, velEndHigh);

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
