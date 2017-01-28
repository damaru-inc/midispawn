package com.damaru.midispawn.controller;

import javafx.fxml.Initializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class TabPaneManager implements Initializable {

    private RangeController rangeController;
    private MelodyController melodyController;

    public TabPaneManager() {

    }

    @Autowired
    public TabPaneManager(RangeController rangeController, MelodyController melodyController) {
        this.rangeController = rangeController;
        this.melodyController = melodyController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
