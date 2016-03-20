package seng302.gui;


import java.io.File;
import java.io.FileFilter;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng302.Environment;
import seng302.utility.TranscriptManager;

public class RootController {
    Environment env;
    TranscriptManager tm;
    Stage stage;

    String path;
    File fileDir;



    @FXML
    private TextField txtCommand;

    @FXML
    private Button btnGo;

    @FXML
    private MenuItem menuQuit;

    @FXML
    private TextArea txtTranscript;

    @FXML
    private MenuItem menuOpen;

    @FXML
    private MenuItem menuSave;

    @FXML
    private void initialize(){

    }

    @FXML
    private void goAction(){

        String text = txtCommand.getText();
        txtCommand.setText("");
        if (text.length() > 0) {
            env.getTranscriptManager().setCommand(text);
            env.getExecutor().executeCommand(text);
            txtTranscript.appendText(env.getTranscriptManager().getLastCommand());
        } else {
            txtTranscript.appendText("[ERROR] Cannot submit an empty command.\n");
        }
    }

    @FXML
    private void closeApplication(){
        if(tm.unsavedChanges == true) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Unsaved changes");
            alert.setContentText("Are you sure that you would like to quit?");

            ButtonType saveBtn = new ButtonType("Save");
            ButtonType quitBtn = new ButtonType("Quit");
            ButtonType cancelBtn = new ButtonType("Cancel");

            alert.getButtonTypes().setAll(saveBtn, quitBtn, cancelBtn);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == saveBtn) {
                saveTranscript();
                if(tm.unsavedChanges == false) {
                    System.exit(0);
                }
            } else if (result.get() == quitBtn) {
                System.exit(0);

            } else{
                //do nothing
            }


        }else{
            System.exit(0);
        }
    }


    @FXML
    private void saveTranscript(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter textFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(textFilter);
        fileChooser.setInitialDirectory(fileDir);
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            fileDir = file.getParentFile();
            path = file.getAbsolutePath();
            tm.Save(path);
        }
    }


    @FXML
    private void openTranscript(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter textFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(textFilter);
        fileChooser.setInitialDirectory(fileDir);
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            fileDir = file.getParentFile();
            path = file.getAbsolutePath();
            try {
                tm.Open(path);
                txtTranscript.setText(tm.convertToText());
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("This file is not valid");
                alert.showAndWait();
                System.err.println("Not a valid file");
            }

        }
    }



    @FXML
    public void handleEnterPressed(KeyEvent event){
    if (event.getCode() == KeyCode.ENTER) {
        goAction();
    }
}

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void setEnvironment(Environment env){
        this.env = env;
        tm = env.getTranscriptManager();


    }

}