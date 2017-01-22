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
public class Sequencer {

    private int numSteps;
    private int currentStep;

    public Sequencer(int numSteps) {
        this.numSteps = numSteps;
        currentStep = 0;
    }

    public int next() {
        int ret = 0;
        if (currentStep < numSteps) {
            ret = currentStep;
            currentStep++;
        }
        return ret;
    }
    
    public double nextDouble() {
        return (double) next();
    }
    
    public Object nextObject() {
        return null;
    }

    public double getPercentDone() {
        return currentStep / (double) numSteps;
    }

    public boolean isDone() {
        return currentStep == numSteps;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public int getNumSteps() {
        return numSteps;
    }
}
