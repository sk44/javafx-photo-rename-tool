/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk44.photorenametool.interfaces.javafx;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import sk44.photorenametool.domain.PhotoDirectoryWalker;
import sk44.photorenametool.util.Action;

/**
 *
 * @author sk
 */
public class MainWindowController implements Initializable {

    @FXML
    private TextField sourceDirPath;
    @FXML
    private TextField outputDirPath;
    @FXML
    private TextField prefix;
    @FXML
    private TextField timeDifference;
    @FXML
    private TextArea messageArea;

    @FXML
    protected void handleBrowseSourceDir(ActionEvent event) {
        openDirectoryChooser("Choose Source Dir...", new Action<File>() {
            @Override
            public void execute(File obj) {
                sourceDirPath.setText(obj.getAbsolutePath());
                if (outputDirPath.getText().isEmpty()) {
                    outputDirPath.setText(new File(obj, "output").getAbsolutePath());
                }
            }
        });
    }

    @FXML
    protected void handleBrowseOutputDir(ActionEvent event) {
        openDirectoryChooser("Choose Output Dir...", new Action<File>() {
            @Override
            public void execute(File obj) {
                outputDirPath.setText(obj.getAbsolutePath());
            }
        });
    }

    @FXML
    protected void handleExecute(ActionEvent event) {
        try {
            Task<String> task = createTask();
            // bind -> overwrite
//            messageArea.textProperty().bind(task.messageProperty());
            // append
            task.messageProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                    updateMessageArea(t1);
                }
            });
            new Thread(task).start();
        } catch (RuntimeException e) {
            updateMessageArea(e.getMessage());
            throw e;
        }
    }

    private Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                PhotoDirectoryWalker.create(
                    sourceDirPath.getText(),
                    outputDirPath.getText(),
                    prefix.getText(),
                    getTimeDifference(),
                    new Action<String>() {
                    @Override
                    public void execute(String obj) {
                        updateMessage(obj);
                    }
                }).execute();
                return "Done...";
            }
        };
    }

    private int getTimeDifference() {
        return Integer.valueOf(timeDifference.getText());
    }

    @FXML
    protected void handleClear(ActionEvent event) {
        clearMessage();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearMessage();
    }

    private void openDirectoryChooser(String title, Action<File> handler) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle(title);
        File dir = dc.showDialog(null);
        if (dir != null) {
            handler.execute(dir);
        }
    }

    private void updateMessageArea(String message) {
        messageArea.appendText("\n" + message);
    }

    private void clearMessage() {
        messageArea.clear();
        messageArea.setText("Ready.");
    }
}
