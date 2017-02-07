package com.damaru.midispawn.model;

import com.damaru.midispawn.midi.MidiUtil;
import org.junit.Test;

import javax.sound.midi.Sequence;

public class MelodyGeneratorTest {

    int numNotes = 20;
    int pulsesPerSixteenthNote = MidiUtil.PPQ / 4;

    @Test
    public void testMelodyGenerator() throws Exception {
        MelodyGenerator mg = new MelodyGenerator(numNotes);
        ClassicDurationGenerator durationGenerator = new ClassicDurationGenerator(4, 4, 8);
        Generator generator = new Generator();

        for (int i = 0; i < numNotes; i++) {
            int note = mg.next();
            int sixteenthNotes = durationGenerator.next();
            int duration = sixteenthNotes * pulsesPerSixteenthNote;
            generator.addNote(note, duration);
        }

        //generator.writeFile("melody.mid");
        Sequence seq = generator.getSequence();
        MidiUtil.playSequence(seq);
    }
}
