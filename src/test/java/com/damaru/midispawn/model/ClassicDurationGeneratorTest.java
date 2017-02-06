package com.damaru.midispawn.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class ClassicDurationGeneratorTest {

    private static Logger log = LogManager.getLogger(ClassicDurationGeneratorTest.class);

    @Test
    public void testDuration() {
	// foo
        int beatsPerBar = 4;
        int pulsesPerBeat = 4;
        int max = 8;

        ClassicDurationGenerator gen = new ClassicDurationGenerator(beatsPerBar, pulsesPerBeat, max);
        for (int i = 0; i < 32; i++) {
            int dur = gen.next();
            log.info(String.format("foo i: %3d dur: %2d", i, dur));
            System.out.println(String.format("foo i: %3d dur: %2d", i, dur));
        }
    }
}
