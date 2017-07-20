/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.damaru.midispawn.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;
import org.springframework.stereotype.Component;

import com.damaru.midispawn.midi.MidiDeviceValue;
import com.damaru.midispawn.model.RangeInterval;
import com.damaru.midispawn.model.Score;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;

/**
 * FXML Controller class
 *
 * @author mike
 */
@Component
public class RangeController extends MidiController {

    Logger log = LogManager.getLogger(RangeController.class);

    @FXML
    RangeSlider pitchStart;
    @FXML
    RangeSlider pitchEnd;
    @FXML
    RangeSlider durationStart;
    @FXML
    RangeSlider durationEnd;
    @FXML
    RangeSlider velocityStart;
    @FXML
    RangeSlider velocityEnd;
    @FXML
    Slider seconds;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        log.debug("url: " + url);
        pitchStart.setHighValue(60);
        pitchStart.setLowValue(40);
        pitchEnd.setHighValue(90);
        pitchEnd.setLowValue(70);
        velocityStart.setHighValue(60);
        velocityStart.setLowValue(40);
        velocityEnd.setHighValue(90);
        velocityEnd.setLowValue(70);
        durationStart.setHighValue(600);
        durationStart.setLowValue(400);
        durationEnd.setHighValue(600);
        durationEnd.setLowValue(400);
    }

    @Override
    public void generate() throws Exception {
        super.generate();
        RangeInterval noteInterval = new RangeInterval(pitchStart.getLowValue(), pitchStart.getHighValue(), pitchEnd.getLowValue(), pitchEnd.getHighValue());
        RangeInterval durInterval = new RangeInterval(durationStart.getLowValue(), durationStart.getHighValue(), durationEnd.getLowValue(), durationEnd.getHighValue());
        RangeInterval velInterval = new RangeInterval(velocityStart.getLowValue(), velocityStart.getHighValue(), velocityEnd.getLowValue(), velocityEnd.getHighValue());
        Score score = new Score();
        log.debug("seconds: " + seconds.getValue());
        score.doSection(generator, (int) seconds.getValue(), noteInterval, durInterval, velInterval);
    }

    @Override
    public void playDirect() throws Exception {
        RangeInterval noteInterval = new RangeInterval(pitchStart.getLowValue(), pitchStart.getHighValue(), pitchEnd.getLowValue(), pitchEnd.getHighValue());
        RangeInterval durInterval = new RangeInterval(durationStart.getLowValue(), durationStart.getHighValue(), durationEnd.getLowValue(), durationEnd.getHighValue());
        RangeInterval velInterval = new RangeInterval(velocityStart.getLowValue(), velocityStart.getHighValue(), velocityEnd.getLowValue(), velocityEnd.getHighValue());
        log.debug("seconds: " + seconds.getValue());
        MidiDeviceValue val = mainController.getMidiDevice();
        future = midiPlayer.play(val.getMidiDevice(), (int) seconds.getValue(), noteInterval, durInterval, velInterval);
    }


//    public void playSong(ActionEvent event) throws MidiUnavailableException, Exception {
//        log.debug("event: " + event);
//        //File midiFile = new File("duet.mid");
//        //Sequence sequence = MidiSystem.getSequence(midiFile);
//        Sequence sequence = null;
//        RadioButton selected = (RadioButton) group1.getSelectedToggle();
//        Score score = new Score();
//
//        switch (selected.getText()) {
//            case "song1":
//                sequence = score.song1();
//                break;
//            case "song2":
//                sequence = score.song2();
//                break;
//            case "song3":
//                sequence = score.song3();
//                break;
//            default:
//                log.error("unexpected radio button text: " + selected.getText());
//                return;
//        }
//
//        MidiUtil.playSequence(sequence);
//    }


}
