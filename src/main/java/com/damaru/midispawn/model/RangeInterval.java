
package com.damaru.midispawn.model;

/**
 *
 * @author mike
 */
public class RangeInterval {
    public double lowStart;
    public double highStart;
    public double lowEnd;
    public double highEnd;

    public RangeInterval(double startLow, double highStart, double lowEnd, double highEnd) {
        this.lowStart = startLow;
        this.highStart = highStart;
        this.lowEnd = lowEnd;
        this.highEnd = highEnd;
    }

    public RangeInterval(double low, double high) {
        this(low, high, low, high);
    }

}
