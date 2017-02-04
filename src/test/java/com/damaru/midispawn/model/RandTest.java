package com.damaru.midispawn.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class RandTest {

    private static Logger log = LogManager.getLogger(RandTest.class);

    @Test
    public void testRand() {

        int highest = Integer.MIN_VALUE;
        int lowest = Integer.MAX_VALUE;

        for (int i = 0; i < 1000; i++) {
            int val = Rand.getIntInRange(10, 20);
            if (val < lowest) {
                lowest = val;
            } else if (val > highest) {
                highest = val;
            }
        }

        log.info(String.format("Lowest: %2d  highest: %2d", lowest, highest));

        highest = Integer.MIN_VALUE;
        lowest = Integer.MAX_VALUE;

        for (int i = 0; i < 1000; i++) {
            int val = Rand.getIntCentred(100, 50);

            if (val < lowest) {
                lowest = val;
            } else if (val > highest) {
                highest = val;
            }
        }

        log.info(String.format("Lowest: %2d  highest: %2d", lowest, highest));
    }
}
