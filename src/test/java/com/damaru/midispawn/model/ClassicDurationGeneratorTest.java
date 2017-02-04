package com.damaru.midispawn.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class ClassicDurationGeneratorTest {

    private static Logger log = LogManager.getLogger(ClassicDurationGeneratorTest.class);

    @Test
    public void testDuration() {
        int beatsPerBar = 16;
        int pulsesPerBeat = 4;
        int max = 16;

        ClassicDurationGenerator gen = new ClassicDurationGenerator(beatsPerBar, pulsesPerBeat, max);
        for (int i = 0; i < 64; i++) {
            int dur = gen.next();
        }
    }
}
