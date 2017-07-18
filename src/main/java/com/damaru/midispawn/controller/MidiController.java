package com.damaru.midispawn.controller;

import javafx.fxml.Initializable;
import com.damaru.midispawn.model.Generator;
import javafx.event.ActionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class MidiController implements Initializable {

    private static final Logger log = LogManager.getLogger(MidiController.class);
    protected Generator generator;
    @Autowired
    protected MainController mainController;
    
    public MidiController() {
        try {
            generator = new Generator();
            log.debug("generator initialized: " + this.getClass());
        } catch (Exception e) {
            log.error("Can't initialize the Generator", e);
        }        
    }
    
    public abstract void generate() throws Exception;
    public abstract void play() throws Exception;
    public abstract void stop() throws Exception;
}
