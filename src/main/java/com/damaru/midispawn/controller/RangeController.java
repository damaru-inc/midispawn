/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.damaru.midispawn.controller;

import com.damaru.midispawn.midi.InstrumentValue;
import com.damaru.midispawn.midi.MidiUtil;
import com.damaru.midispawn.model.Generator;
import com.damaru.midispawn.model.RangeInterval;
import com.damaru.midispawn.model.Score;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.controlsfx.control.RangeSlider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import java.net.URL;
import java.util.ResourceBundle;

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
        SpinnerValueFactory.IntegerSpinnerValueFactory programFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory.IntegerSpinnerValueFactory(0, 127);
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
        Generator generator = new Generator();
        InstrumentValue instrument = mainController.getInstrument();
        int prog = instrument.getProgram();
        log.debug("program: " + prog);
        generator.setProgram(prog);
        RangeInterval noteInterval = new RangeInterval(pitchStart.getLowValue(), pitchStart.getHighValue(), pitchEnd.getLowValue(), pitchEnd.getHighValue());
        RangeInterval durInterval = new RangeInterval(durationStart.getLowValue(), durationStart.getHighValue(), durationEnd.getLowValue(), durationEnd.getHighValue());
        RangeInterval velInterval = new RangeInterval(velocityStart.getLowValue(), velocityStart.getHighValue(), velocityEnd.getLowValue(), velocityEnd.getHighValue());
        Score score = new Score();
        log.debug("seconds: " + seconds.getValue());
        score.doSection(generator, (int) seconds.getValue(), noteInterval, durInterval, velInterval);
        Sequence seq = generator.getSequence();
        MidiUtil.playSequence(seq);
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

    @Override
    public void play() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
