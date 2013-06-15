package sk44.photorenametool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhotoRenameToolApplication extends Application {

    private static final Logger logger = LoggerFactory.getLogger(PhotoRenameToolApplication.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Photo Rename Tool");
        Scene scene = new Scene((Pane) FXMLLoader
            .load(getClass().getResource("interfaces/javafx/MainWindow.fxml")));
        stage.setScene(scene);
        stage.show();
    }
}
