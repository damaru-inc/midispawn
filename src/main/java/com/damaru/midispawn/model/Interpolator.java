/*
 *  Copyright (C) 2010 Michael Davis - Damaru Inc - michael@damaru.com
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.damaru.midispawn.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author Michael Davis
 */
public class Interpolator extends Sequencer {

    private static Logger log = LogManager.getLogger(Interpolator.class);
    private double start;
    private double end;
    private double range;

    public static int calculateNumSteps(RangeInterval rangeInterval, double targetTotal) {
        int ret = 0;
        double averageStart = (rangeInterval.lowStart + rangeInterval.highStart) / 2.0;
        double averageEnd = (rangeInterval.lowEnd + rangeInterval.highEnd) / 2.0;
        log.debug("averageStart: " + averageStart + " averageEnd: " + averageEnd);
        ret = calculateNumSteps(averageStart, averageEnd, targetTotal);
        return ret;
    }

    public static int calculateNumSteps(double start, double end, double targetTotal) {
        int ret = 0;
        double average = (start + end) / 2.0;
        ret = (int) (targetTotal / average);
        log.debug("average: " + average + " targetTotal: " + targetTotal);
        return ret;
    }

    public static Interpolator createInterpolator(double start, double end, double targetTotal) {
        Interpolator ret = null;
        int numSteps = calculateNumSteps(start, end, targetTotal);
        ret = new Interpolator(numSteps, start, end);
        return ret;
    }

    public Interpolator(int numSteps, double start, double end) {
        super(numSteps);
        this.start = start;
        this.end = end;
        range = end - start;
    }

    @Override
    public int next() {
        int ret = 0;
        ret = (int) Math.round(nextDouble());
        return ret;
    }

    @Override
    public double nextDouble() {
        double ret = 0.0;
        double percentDone = getPercentDone();
        double delta = percentDone * range;
        ret = start + delta;
        super.next();
        return ret;
    }
}
