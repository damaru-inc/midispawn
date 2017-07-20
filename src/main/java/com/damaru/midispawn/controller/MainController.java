package com.damaru.midispawn.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.damaru.midispawn.midi.InstrumentValue;
import com.damaru.midispawn.midi.MidiDeviceValue;
import com.damaru.midispawn.midi.MidiUtil;
import com.damaru.midispawn.model.Generator;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Created by mike on 2017-01-29.
 */
@Component
@SuppressWarnings("rawtypes")
public class MainController implements Initializable {

    Logger log = LogManager.getLogger(MainController.class);
    private VBox rootNode;
    @FXML
    ComboBox<String> moduleCombo;
    @FXML
    ComboBox<InstrumentValue> instrumentCombo;
    @FXML
    ComboBox<MidiDeviceValue> deviceCombo;
    Generator generator = null;
    private MidiController controller;
    private ApplicationContext springContext;
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        log.debug("init start");
        try {
            ObservableList<String> modules = FXCollections.observableArrayList();
            modules.add("Melody");
            modules.add("Range");
            moduleCombo.setItems(modules);
            moduleCombo.getSelectionModel().select(0);
            moduleCombo.valueProperty().addListener(new ModuleChangeListener());
            ObservableList<InstrumentValue> instruments = MidiUtil.getInstruments();
            instrumentCombo.setItems(instruments);
            instrumentCombo.getSelectionModel().select(0);
            ObservableList<MidiDeviceValue> devices = MidiUtil.getMidiDevices();
            deviceCombo.setItems(devices);
            deviceCombo.getSelectionModel().select(0);

            generator = new Generator();
        } catch (Exception e) {
            log.error("Can't initialize MainController", e);
            quit(null);
        }
        log.debug("init end");
    }

    public void setRootNode(ApplicationContext springContext, Parent rootNode, Stage stage) throws IOException {
        this.rootNode = (VBox) rootNode;
        this.springContext = springContext;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Melody.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Node rangeModule = fxmlLoader.load();
        ObservableList<Node> children = this.rootNode.getChildren();
        children.set(2, rangeModule);
        controller = fxmlLoader.getController();
        this.stage = stage;
    }

    public void moduleSelected() {
        log.debug("module selected.");
    }

    class ModuleChangeListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue ov, String old, String newOne) {
            log.debug(String.format("changed: %s %s ", old, newOne));
            String resourceName = "/fxml/" + newOne + ".fxml";
            log.debug("Changing module to " + resourceName);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resourceName));
            fxmlLoader.setControllerFactory(springContext::getBean);
            Node module = null;
            try {
                module = fxmlLoader.load();
            } catch (IOException e) {
                log.error("Failed to load module resourceName", e);
                quit(null);
            }
            ObservableList<Node> children = rootNode.getChildren();
            children.set(2, module);
            controller = fxmlLoader.getController();
        }

    }

    public void quit(ActionEvent event) {
        MidiUtil.close();
        Platform.exit();
        System.exit(0);
    }

    public InstrumentValue getInstrument() {
        return (InstrumentValue) instrumentCombo.getValue();
    }
    
    public MidiDeviceValue getMidiDevice() {
    	return (MidiDeviceValue) deviceCombo.getValue();
    }
    
    public void generate(ActionEvent event) {
        try {
            controller.generate();
        } catch (Exception ex) {
            log.error("Error generating midi", ex);
        }
    }
    
    public void saveMidiFile(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Midi File");
            File file = fileChooser.showSaveDialog(stage);
            Generator generator = controller.getGenerator();
            generator.writeFile(file);
        } catch (Exception ex) {
            log.error("Error generating midi", ex);
        }
    }

    public void play(ActionEvent event) {
        try {
            controller.play();
        } catch (Exception ex) {
            log.error("Error playing midi", ex);
        }
    }
    
    public void stop(ActionEvent event) {
        try {
            controller.stop();
        } catch (Exception ex) {
            log.error("Error stopping midi", ex);
        }
    }

    public void playDirect(ActionEvent event) {
        try {
            controller.playDirect();
        } catch (Exception ex) {
            log.error("Error playing midi", ex);
        }
    }
    
    public void stopDirect(ActionEvent event) {
        try {
            controller.stopDirect();
        } catch (Exception ex) {
            log.error("Error stopping midi", ex);
        }
    }

}
