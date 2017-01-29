package com.damaru.midispawn.controller;

import com.damaru.midispawn.midi.MidiUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mike on 2017-01-29.
 */
@Component
public class MainController implements Initializable {

    Logger log = LogManager.getLogger(MainController.class);
    private VBox rootNode;
    @FXML
    ComboBox module;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        log.debug("init start");
        ObservableList<String> modules = FXCollections.observableArrayList();
        modules.add("Range");
        modules.add("Melody");
        module.setItems(modules);
        module.getSelectionModel().select(0);
        modules.addListener(new ModuleChangeListener());
        log.debug("init end");
    }

    public void setRootNode(ApplicationContext springContext, Parent rootNode) throws IOException {
        this.rootNode = (VBox) rootNode;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/tabs/Simple.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Node rangeModule = fxmlLoader.load();
        ObservableList<Node> children = this.rootNode.getChildren();
        children.set(1, rangeModule);
    }

    class ModuleChangeListener implements ListChangeListener {

        @Override
        public void onChanged(Change c) {
            log.debug("Changed module: " + c);
        }

    }

    public void quit(ActionEvent event) {
        MidiUtil.close();
        Platform.exit();
    }


}
