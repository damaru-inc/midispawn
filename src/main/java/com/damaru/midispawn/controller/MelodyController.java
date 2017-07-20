/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.damaru.midispawn.controller;

import com.damaru.midispawn.midi.MidiUtil;
import com.damaru.midispawn.model.ClassicDurationGenerator;
import com.damaru.midispawn.model.Generator;
import com.damaru.midispawn.model.MelodyGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;
import org.springframework.stereotype.Component;

import javax.sound.midi.MidiUnavailableException;
import java.net.URL;
import java.util.ResourceBundle;
import javax.sound.midi.Sequence;

/**
 * FXML Controller class
 *
 * @author mike
 */
@Component
public class MelodyController extends MidiController {

    private Logger log = LogManager.getLogger(MelodyController.class);
    int pulsesPerSixteenthNote = MidiUtil.PPQ / 4;

    @FXML
    RangeSlider noteRange;
    @FXML
    Slider notes;
    @FXML
    Slider int_m_7;
    @FXML
    Slider int_m_6;
    @FXML
    Slider int_m_5;
    @FXML
    Slider int_m_4;
    @FXML
    Slider int_m_3;
    @FXML
    Slider int_m_2;
    @FXML
    Slider int_m_1;
    @FXML
    Slider int_p_0;
    @FXML
    Slider int_p_1;
    @FXML
    Slider int_p_2;
    @FXML
    Slider int_p_3;
    @FXML
    Slider int_p_4;
    @FXML
    Slider int_p_5;
    @FXML
    Slider int_p_6;
    @FXML
    Slider int_p_7;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void generate() throws MidiUnavailableException, Exception {
        super.generate();
        int numNotes = notes.valueProperty().intValue();
        MelodyGenerator mg = new MelodyGenerator(numNotes);
        int[] probs = {
            int_m_7.valueProperty().intValue(),
            int_m_6.valueProperty().intValue(),
            int_m_5.valueProperty().intValue(),
            int_m_4.valueProperty().intValue(),
            int_m_3.valueProperty().intValue(),
            int_m_2.valueProperty().intValue(),
            int_m_1.valueProperty().intValue(),
            int_p_0.valueProperty().intValue(),
            int_p_1.valueProperty().intValue(),
            int_p_2.valueProperty().intValue(),
            int_p_3.valueProperty().intValue(),
            int_p_4.valueProperty().intValue(),
            int_p_5.valueProperty().intValue(),
            int_p_6.valueProperty().intValue(),
            int_p_7.valueProperty().intValue()
        };
        mg.setIntervalProbabilities(probs);
        ClassicDurationGenerator durationGenerator = new ClassicDurationGenerator(4, 4, 8);
        
        for (int i = 0; i < numNotes; i++) {
            int note = mg.next();
            int sixteenthNotes = durationGenerator.next();
            int duration = sixteenthNotes * pulsesPerSixteenthNote;
            generator.addNote(note, duration, 100);
        }
                
    }

}
