package com.damaru.midispawn.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ClassicDurationGenerator extends SequenceGenerator {

    private static Logger log = LogManager.getLogger(ClassicDurationGenerator.class);

    private int beatsPerBar;
    private int pulsesPerBeat;
    private int maxDuration;
    private int pulsesPerBar;
    private double chanceOfFinishingBeat = 0.8;

    public ClassicDurationGenerator(int beatsPerBar, int pulsesPerBeat, int maxDuration) {
        super(-1);
        this.beatsPerBar = beatsPerBar;
        this.pulsesPerBeat = pulsesPerBeat;
        this.maxDuration = maxDuration;

        pulsesPerBar = beatsPerBar * pulsesPerBeat;
    }

    public int next() {
        int ret = 0;
        int offsetFromBeat = this.currentStep % pulsesPerBeat;
        int offsetFromBar = currentStep % pulsesPerBar;

        log.debug(String.format("currentStep: %3d offsetFromBar: %2d offsetFromBeat: %d", currentStep, offsetFromBar, offsetFromBeat));
        int pulsesLeftInBar = pulsesPerBar - offsetFromBar;

        if (offsetFromBeat == 0) {
            ret = getRandomAndTruncate(maxDuration, pulsesLeftInBar);
        } else {
            if (Rand.flipCoin(chanceOfFinishingBeat)) {
                List<Integer> possibleValues = new ArrayList<>();
                int pulsesToFinishBeat = pulsesPerBeat - offsetFromBeat;
                log.debug( String.format("pulsesLeftInBar: %2d pulsesToFinishBeat: %d", pulsesLeftInBar, pulsesToFinishBeat));

                while (pulsesToFinishBeat <= pulsesLeftInBar && pulsesToFinishBeat <= maxDuration) {
                    log.debug(String.format("Added %d", pulsesToFinishBeat));
                    possibleValues.add(pulsesToFinishBeat);
                    pulsesToFinishBeat += pulsesPerBeat;
                }

                int size = possibleValues.size();
                if (size == 0) {
                    throw new RuntimeException(String.format("This can't happen: no values to finish beat. pulsesLeftInBar: %2d pulsesToFinishBeat: %d",
                            pulsesLeftInBar, pulsesToFinishBeat));
                }
                int pick = Rand.getInt(size);
                ret = possibleValues.get(pick);
            } else {
                ret = getRandomAndTruncate(maxDuration, pulsesLeftInBar);
            }
        }

        currentStep += ret;
        log.debug(String.format("currentStep %3d returning %2d", currentStep, ret));
        return ret;
    }

    public int getRandomAndTruncate(int maxDuration, int pulsesLeftInBar) {
        int max = Math.min(maxDuration, pulsesLeftInBar);
        int val = Rand.getIntInRange(1, max);

        if (val > maxDuration) {
            val = maxDuration;
        }

        return val;
    }
}
