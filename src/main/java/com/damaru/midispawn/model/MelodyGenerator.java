package com.damaru.midispawn.model;

import com.damaru.midispawn.midi.MidiUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MelodyGenerator extends SequenceGenerator {

    private static Logger log = LogManager.getLogger(MelodyGenerator.class);

    private int minNote = MidiUtil.MIDDLE_C - 24;
    private int maxNote = MidiUtil.MIDDLE_C + 24;

    // index from -7 to +7 - given an interval, looks up the likelyhood of being chosen.
    private int[] intervalProbabilities =
            { 3, 1, 2, 2, 3, 3, 1, 2, 1, 3, 3, 2, 2, 1, 2};
    private int[] intervalLookupTable;
    int currentNote;

    public MelodyGenerator(int numSteps) throws Exception {
        super(numSteps);
        init();
    }

    private void init() {

        int count = 0;
        for (int i = 0; i < intervalProbabilities.length; i++) {
            count += intervalProbabilities[i];
        }

        intervalLookupTable =  new int[count];

        int interval = - (intervalProbabilities.length / 2); // from, say, -7 to +7
        int intervalLookupTableIndex = 0;
        for (int i = 0; i < intervalProbabilities.length; i++) {
            count = intervalProbabilities[i];
            for (int j = 0; j < count; j++) {
                intervalLookupTable[intervalLookupTableIndex++] = interval;
            }
            interval++;
        }

        currentNote = MidiUtil.MIDDLE_C;

        //for (int i = 0; i < intervalLookupTable.length; i++) {
        //    log.debug(String.format("interval: %2d %2d", i, intervalLookupTable[i]));
        //}
    }

    public int next() {
        int intervalIndex = Rand.getInt(intervalLookupTable.length);
        int interval = intervalLookupTable[intervalIndex];
        int prospectiveNextNote = currentNote + interval;
        if (prospectiveNextNote < minNote || prospectiveNextNote > maxNote) {
            interval = -interval;
        }

        currentNote += interval;
        return currentNote;
    }

    public void setMinNote(int minNote) {
        this.minNote = minNote;
    }

    public void setMaxNote(int maxNote) {
        this.maxNote = maxNote;
    }

    public void setIntervalProbabilities(int[] intervalProbabilities) {
        this.intervalProbabilities = intervalProbabilities;
        init();
    }
    
    
}
