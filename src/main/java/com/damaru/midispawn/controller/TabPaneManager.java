package com.damaru.midispawn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TabPaneManager {

    private final RangeController rangeController;
    private final MelodyController melodyController;

    @Autowired
    public TabPaneManager(RangeController rangeController, MelodyController melodyController) {
        this.rangeController = rangeController;
        this.melodyController = melodyController;
    }
}
