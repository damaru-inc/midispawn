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

/**
 *
 * @author Michael Davis
 */
public class RangeSequenceGenerator extends SequenceGenerator {
    private InterpolatingSequenceGenerator highInterpolator;
    private InterpolatingSequenceGenerator lowInterpolator;

    public RangeSequenceGenerator(int numSteps, RangeInterval rangeInterval) {
        this(numSteps, rangeInterval.lowStart, rangeInterval.highStart, rangeInterval.lowEnd, rangeInterval.highEnd);
    }
    
    public RangeSequenceGenerator(int numSteps, double lowStart, double highStart, double lowEnd, double highEnd) {
        super(numSteps);
        highInterpolator = new InterpolatingSequenceGenerator(numSteps, highStart, highEnd);
        lowInterpolator = new InterpolatingSequenceGenerator(numSteps, lowStart, lowEnd);
    }

    @Override
    public int next() {
        return (int) nextDouble();
    }

    @Override
    public double nextDouble() {
        double ret = 0.0;
        double nextHigh = highInterpolator.nextDouble();
        double nextLow = lowInterpolator.nextDouble();
        ret = Rand.getDoubleInRange(nextLow, nextHigh);
        super.next();
        return ret;
    }

}
