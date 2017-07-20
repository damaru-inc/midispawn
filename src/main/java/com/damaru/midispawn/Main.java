package com.damaru.midispawn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import com.damaru.midispawn.controller.MainController;
import com.damaru.midispawn.midi.MidiUtil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


@Configuration
@ComponentScan("com.damaru.midispawn")
@EnableAsync
public class Main extends Application {

    private Logger log = LogManager.getLogger(Main.class);
    private AnnotationConfigApplicationContext springContext;
    private Parent rootNode;
    @Autowired
    MainController mainController;

    public static void main(final String[] args) {
        Application.launch(args);
    }

    @Override
    public void init() throws Exception {
        log.debug("init start");
        springContext = new AnnotationConfigApplicationContext();
        springContext.register(Main.class);
        springContext.refresh();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        rootNode = fxmlLoader.load();
        log.debug("init end");
    }

    @Override
    public void start(Stage stage) throws Exception {
        log.debug("start start");
        stage.setScene(new Scene(rootNode));
        MainController controller = (MainController) springContext.getBean("mainController");
        log.debug("controller: " + controller + " mainController: " + mainController);
        if (controller != null) {
            controller.setRootNode(springContext, rootNode, stage);
        }
        stage.show();
        log.debug("start end");
    }

    @Override
    public void stop() throws Exception {
        log.debug("Called stop.");
        MidiUtil.close();
        Platform.exit();
    }

}




        
//Options ...   
//gets called for each Controller that you want DI. If you put a println
//in before the return, you can see which Controller gets DI by spring

//via good old anonymous class
//        loader.setControllerFactory(new Callback<Class<?>, Object>() {
//            @Override
//            public Object call(Class<?> clazz) {
//                return springContext.getBean(clazz);
//            }
//        });

// via lambda
//        loader.setControllerFactory((clazz) -> springContext.getBean(clazz));

//  via method reference       
// loader.setControllerFactory(springContext::getBean);
