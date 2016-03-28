package seng302.gui;

import java.io.File;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import seng302.Environment;

/**
 * Created by jat157 on 20/03/16.
 */
public class TranscriptPaneController {

    Environment env;

    Stage stage;


    String path;
    File fileDir;


//    @FXML
//    private Pane pane1;

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
    private SimpleIntegerProperty history;



    @FXML
    private void initialize(){

    }

    private String enteredCommand;

    private int historyLevel;


    /**
     * The command which is binded to the Go button, or the enter key when the command prompt is active.
     */
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


    /**
     *
     * @param event
     */

    @FXML
    public void handleKeyPressed(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER) {
            goAction();
        }

        else if(event.getCode() == KeyCode.UP){

           handleScrollUp();
            //env.getTranscriptManager().cycleInputUp(txtCommand.getText());

        }

        else if(event.getCode() == KeyCode.DOWN){

            handleScrollDown();
        }
        else if(event.getCode() == KeyCode.ALPHANUMERIC){
            historyLevel = -1;
            enteredCommand = "";
        }



    }

    /**
     * Called when the 'Up' arrow is pressed when the command prompt is active.
     * Cycles through command history.
     */
    private void handleScrollUp(){

        txtCommand.setText(env.getTranscriptManager().cycleInputUp(txtCommand.getText()));


//        int size = env.getTranscriptManager().getTranscriptTuples().size();
//        if(historyLevel == -1 && size > 0){
//            historyLevel = 1;
//
//            enteredCommand = txtCommand.getText();
//
//        }
//        else{
//            if(historyLevel < size){
//                historyLevel ++;
//            }
//
//
//        }
//
//        txtCommand.setText(env.getTranscriptManager().getTranscriptTuples().get(size-historyLevel).getCommand());
    }

    /**
     * Called when the 'Down' key is pressed when the command prompt is active.
     * If the command prompt is showing a previously used command from the history, then a newer command will be shown.
     */
    private void handleScrollDown(){
//        int size = env.getTranscriptManager().getTranscriptTuples().size();
//
//        if(historyLevel > 1){
//
//            historyLevel--;
//
//            txtCommand.setText(env.getTranscriptManager().getTranscriptTuples().get(size-historyLevel).getCommand());
//        }
//        else if(historyLevel > 0){
//            historyLevel --;
//
//            if(enteredCommand != null && enteredCommand.equals("")){
//                System.out.println("empty!");
//                historyLevel = -1;
//                txtCommand.setText(enteredCommand);
//
//            }else{
//                txtCommand.setText(enteredCommand);
//            }
//
//
//
//        }
//        else if(historyLevel == 0){
//            txtCommand.setText("");
//            historyLevel = -1;
//        }

        txtCommand.setText(env.getTranscriptManager().cycleInputDown(txtCommand.getText()));
    }






    public void setStage(Stage stage){
        this.stage = stage;

    }
    public void setEnv(Environment env){
        this.env = env;
    }





}
