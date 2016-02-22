package dallastools.controllers.stages;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by dimmaryanto on 9/24/15.
 */
@Component
public class StageRunner {

    @Autowired
    private ResourceBundle resourceBundle;

    public Initializable getController(Class getclass, Stage stage, String fxml) {
        FXMLLoader loader = new FXMLLoader(getclass.getResource(fxml));
        loader.setResources(resourceBundle);
        try {
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.centerOnScreen();
            return loader.getController();
        } catch (IOException e) {
            System.err.println("can't load the scene" + e.getMessage());
            return null;
        }
    }
}
