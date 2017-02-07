package com.damaru.midispawn.model;

import com.damaru.midispawn.midi.MidiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import javax.sound.midi.Sequence;

public class MelodyGeneratorTest {

    Logger log = LogManager.getLogger(MelodyGeneratorTest.class);
    int numNotes = 20;
    int pulsesPerSixteenthNote = MidiUtil.PPQ / 4;

    @Test
    public void testMelodyGenerator() throws Exception {
        MelodyGenerator mg = new MelodyGenerator(numNotes);
        ClassicDurationGenerator durationGenerator = new ClassicDurationGenerator(4, 4, 8);
        Generator generator = new Generator();
        generator.setTempo(120);

        for (int i = 0; i < numNotes; i++) {
            int note = mg.next();
            int sixteenthNotes = durationGenerator.next();
            int duration = sixteenthNotes * pulsesPerSixteenthNote;
            generator.addNote(note, duration, 100);
        }

        generator.writeFile("melody.mid");
        Sequence seq = generator.getSequence();
        log.debug("resolution: " + seq.getResolution() + " tick length: " + seq.getTickLength() + " micro: " + seq.getMicrosecondLength());
        MidiUtil.playSequence(seq);
        MidiUtil.close();
    }
}
