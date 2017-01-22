/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.damaru.midispawn.model;

import java.util.Random;

/**
 *
 * @author mike
 */
public class Rand {

    private static Random random = new Random();

    public static int getIntCentred(int centre, int range) {
        int ret = 0;
        if (range == 0) {
            ret = centre;
        } else {
            int base = centre - (range / 2);
            if (base < 0) {
                base = 0;
                //MidiGeneratorApp.debug("Rand.getInt: base was " + base + " centre: " + centre + " range: " + range);
            }
            ret = random.nextInt(range) + centre;
        }
        return ret;
    }

    public static int getInt64(int range) {
        return getIntCentred(64, range);
    }

    public static double getDoubleInRange(double low, double high) {
        double rand = random.nextDouble();
        double range = high - low;
        double ret = (rand * range) + low;
        return ret;
    }
}
