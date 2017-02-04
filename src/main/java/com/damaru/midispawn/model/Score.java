package com.damaru.midispawn.model;

import javax.sound.midi.Sequence;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author mike
 */
public class Score {

    private static Logger log = LogManager.getLogger(Score.class);
    private int totalNotes;

    public void doSection(Generator generator, int seconds, RangeInterval keyInterval, RangeInterval durInterval, RangeInterval velInterval) throws Exception {
        int numNotes = InterpolatingSequenceGenerator.calculateNumSteps(durInterval, seconds * Generator.PPS);
        log.debug("seconds: " + seconds + " numNotes: " + numNotes);
        RangeSequenceGenerator keySequencer = new RangeSequenceGenerator(numNotes, keyInterval);
        RangeSequenceGenerator durSequencer = new RangeSequenceGenerator(numNotes, durInterval);
        RangeSequenceGenerator velSequencer = new RangeSequenceGenerator(numNotes, velInterval);
        int len = 0;
        for (int i = 0; i < numNotes; i++) {
            int key = keySequencer.next();
            int length = durSequencer.next();
            int velocity = velSequencer.next();
            generator.addNote(key, length, velocity);
            len += length;
        }
        double secs = len / (double) Generator.PPS;
        log.debug("length: " + len + " secs: " + secs);
        totalNotes += numNotes;
    }

    public void s1s1(Generator generator, int seconds) throws Exception {
        RangeInterval keyInterval = new RangeInterval(40, 60, 80, 86);
        RangeInterval durInterval = new RangeInterval(200, 300, 20, 50);
        RangeInterval velInterval = new RangeInterval(80, 110, 40, 80);
        doSection(generator, seconds, keyInterval, durInterval, velInterval);
    }

    public void s1s2(Generator generator, int seconds) throws Exception {
        RangeInterval keyInterval = new RangeInterval(80, 86, 80, 86);
        RangeInterval durInterval = new RangeInterval(20, 50, 220, 50);
        RangeInterval velInterval = new RangeInterval(40, 80, 40, 80);
        doSection(generator, seconds, keyInterval, durInterval, velInterval);
    }

    public void s1s3(Generator generator, int seconds) throws Exception {
        RangeInterval keyInterval = new RangeInterval(80, 86, 90, 120);
        RangeInterval durInterval = new RangeInterval(20, 50, 200, 400);
        RangeInterval velInterval = new RangeInterval(40, 80, 10, 40);
        doSection(generator, seconds, keyInterval, durInterval, velInterval);
    }

    public Sequence song1() throws Exception {
        Generator generator = new Generator();
        s1s1(generator, 7);
        s1s2(generator, 2);
        s1s3(generator, 10);
        return generator.getSequence();
    }

    public void s2s1(Generator generator, int seconds) throws Exception {
        RangeInterval keyInterval = new RangeInterval(40, 60, 80, 86);
        RangeInterval durInterval = new RangeInterval(200, 220, 50, 50);
        RangeInterval velInterval = new RangeInterval(80, 110, 40, 80);
        doSection(generator, seconds, keyInterval, durInterval, velInterval);
    }

    public void s2s2(Generator generator, int seconds) throws Exception {
        RangeInterval keyInterval = new RangeInterval(80, 86, 80, 96);
        RangeInterval durInterval = new RangeInterval(50, 50, 50, 70);
        RangeInterval velInterval = new RangeInterval(40, 80, 40, 80);
        doSection(generator, seconds, keyInterval, durInterval, velInterval);
    }

    public void s2s3(Generator generator, int seconds) throws Exception {
        RangeInterval keyInterval = new RangeInterval(80, 96, 90, 120);
        RangeInterval durInterval = new RangeInterval(50, 70, 200, 400);
        RangeInterval velInterval = new RangeInterval(40, 80, 10, 40);
        doSection(generator, seconds, keyInterval, durInterval, velInterval);
    }

    public Sequence song2() throws Exception {
        Generator generator = new Generator();
        s2s1(generator, 5);
        s2s2(generator, 6);
        s2s3(generator, 7);
        return generator.getSequence();
        //generator.writeFile("song2");
    }

    public void s3s1(Generator generator, int seconds) throws Exception {
        RangeInterval keyInterval = new RangeInterval(40, 50, 10, 20);
        RangeInterval durInterval = new RangeInterval(1000, 4000);
        RangeInterval velInterval = new RangeInterval(40, 100);
        doSection(generator, seconds, keyInterval, durInterval, velInterval);
    }

    public Sequence song3() throws Exception {
        Generator generator = new Generator();
        s3s1(generator, 40);
        return generator.getSequence();
        //generator.writeFile("song3");
    }

    public static void main(String[] args) throws Exception {
        Score score = new Score();
        //score.song1();
        //score.song2();
        score.song3();
        log.debug("total notes: " + score.totalNotes);
    }
}
