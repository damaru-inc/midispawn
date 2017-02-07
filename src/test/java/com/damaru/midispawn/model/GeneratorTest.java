package com.damaru.midispawn.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

/**
 * Created by mike on 2017-02-06.
 */
public class GeneratorTest {

    private static Logger log = LogManager.getLogger(GeneratorTest.class);
    @Test
    public void testGenerator() {
        int bpm = 60;
        double bps = bpm / 60.0;
        double secondsPerBeat = 1 / bps;
        long mics = (long) (secondsPerBeat * 1_000_000);
        long mask = 256 * 256;
        log.debug("mask: " + mask + " mics: " + mics);
        int val1 = (int) (mics / mask);
        mics = mics - (val1 * 256 * 256);
        long mask2 = 256;
        int val2 = (int) (mics / mask2);
        int val3 = (int) (mics - (val2 * 256));
        long val = (val1 * 256 * 256) + (val2 * 256) + val3;
        log.debug("val1: " + val1 + " val2: " + val2 + " val3: " + val3 + " val: " + val);
    }
}
