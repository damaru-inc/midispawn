/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.damaru.midispawn.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.RangeSlider;
import org.springframework.stereotype.Component;

import javax.sound.midi.MidiUnavailableException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author mike
 */
@Component
public class MelodyController extends MidiController {

    private Logger log = LogManager.getLogger(MelodyController.class);

    @FXML
    RangeSlider noteRange;
    @FXML
    Slider bars;
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
    }

    @Override
    public void play() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
