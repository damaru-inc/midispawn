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

    public static boolean flipCoin(double chanceOfWinning) {
        double val = random.nextDouble();
        return val < chanceOfWinning;
    }

    public static int getIntCentred(int centre, int range) {
        int ret = 0;
        if (range == 0) {
            ret = centre;
        } else {
            int base = centre - (range / 2);
            ret = getIntInRange(base, base + range);
        }
        return ret;
    }

    public static int getIntInRange(int low, int high) {
        int range = high - low + 1;
        return random.nextInt(range) + low;
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

    public static int getInt(int max) {
        return random.nextInt(max);
    }

}
