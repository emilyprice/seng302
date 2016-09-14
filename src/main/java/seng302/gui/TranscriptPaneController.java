package seng302.gui;

import com.jfoenix.controls.JFXButton;

import java.io.File;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import seng302.Environment;
import seng302.command.Command;
import seng302.command.NullCommand;

/**
 * Created by jat157 on 20/03/16.
 */
public class TranscriptPaneController {

    Environment env;

    Stage stage;


    String path;
    File fileDir;

    @FXML
    public TextField txtCommand;

    @FXML
    private JFXButton btnGo;

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
    Button playall;

    @FXML
    Button playnext;

    @FXML
    Button stop;

    @FXML
    Label commandvalue;

    @FXML
    ToolBar playbackToolbar;

    @FXML
    AnchorPane transcriptAnchor;

    private boolean isExpanded;


    @FXML
    private void initialize() {
        hideTranscript();
        // Text field can only request focus once everything has been loaded.
        Platform.runLater(() -> txtCommand.requestFocus());
    }

    public void hideTranscript() {
        isExpanded = false;
        playbackToolbar.setMaxWidth(0);
        txtTranscript.setMaxWidth(0);
        txtCommand.setMaxWidth(0);
        btnGo.setMaxWidth(0);
        transcriptAnchor.setVisible(false);
        transcriptAnchor.setMinWidth(0);
        transcriptAnchor.setMaxWidth(0);
    }

    /**
     * Getter method for the input text field
     */
    public TextField getTxtCommand() {
        return txtCommand;
    }

    private String enteredCommand;

    private int historyLevel;


    /**
     * The command which is binded to the Go button, or the enter key when the command prompt is
     * active.
     */
    @FXML
    private void goAction() {
        String text = txtCommand.getText();
        txtCommand.setText("");
        executeAndPrintToTranscript(text);
    }

    public void executeAndPrintToTranscript(String text) {
        if (text.length() > 0) {
            env.getTranscriptManager().setCommand(text);
            Command command = env.getExecutor().parseCommandString(text);

            env.getExecutor().executeCommand(command);
            txtTranscript.appendText(env.getTranscriptManager().getLastCommand());

        } else {
            txtTranscript.appendText("[ERROR] Cannot submit an empty command.\n");
        }
    }

    private Command execute(String text) {
        if (text.length() > 0) {
            env.getTranscriptManager().setCommand(text);
            Command command = env.getExecutor().parseCommandString(text);
            env.getExecutor().executeCommand(command);
            return command;

        } else {
            return new NullCommand();
        }
    }

    private void printToTranscript() {
        txtTranscript.appendText(env.getTranscriptManager().getLastCommand());
    }


    /**
     *
     * @param event
     */

    @FXML
    public void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            goAction();
            env.getTranscriptManager().resetHistoryLevel();
        } else if (event.getCode() == KeyCode.UP) {
            txtCommand.setText(env.getTranscriptManager().cycleInputUp(txtCommand.getText()));
            Platform.runLater(() -> txtCommand.positionCaret(9999));
        } else if (event.getCode() == KeyCode.DOWN) {
            txtCommand.setText(env.getTranscriptManager().cycleInputDown(txtCommand.getText()));
            Platform.runLater(() -> txtCommand.positionCaret(9999));
        } else if (event.getCode() == KeyCode.ALPHANUMERIC) {
            env.getTranscriptManager().resetHistoryLevel();
        }

    }

    public void setTranscriptPane(String text) {
        txtTranscript.setText(text);
    }


    public void setStage(Stage stage) {
        this.stage = stage;

    }

    public void setEnv(Environment env) {
        this.env = env;


    }

    public void beginPlaybackMode(final ArrayList<String> commands) {
        showPlaybackGui(commands.get(0));
        final Task playAllTask = new Task<Void>() {
            @Override
            public Void call() {
                for (final String command : commands) {
                    if (isCancelled()) {
                        updateMessage("Cancelled");
                        break;
                    }
                    Command cmd = execute(command);
                    printToTranscript();
                    try {
                        Thread.sleep(cmd.getLength(env) + 1000);
                    } catch (InterruptedException e) {
                        updateMessage("Cancelled");
                        break;
                    }
                }
                Platform.runLater(() -> playbackFinished());
                return null;
            }
        };
        final Thread th = new Thread(playAllTask);
        th.setDaemon(true);

        playall.setOnAction(event -> {
            //Plays commands one by one, with a second's pause in between
            playnext.setDisable(true);
            th.start();
        });


        playnext.setOnAction(event -> {
            env.getPlayer().stop();

            try {
                Task playNextTask = new Task<Void>() {
                    @Override
                    public Void call() {
                        Command cmd = execute(commands.get(0));
                        printToTranscript();
                        try {
                            commands.remove(0);

                            Thread.sleep(cmd.getLength(env) + 100);
                        } catch (InterruptedException e) {
                            updateMessage("Cancelled");
                        }
                        return null;
                    }
                };
                Thread nextThread = new Thread(playNextTask);
                nextThread.setDaemon(true);
                nextThread.start();
                if (commands.get(1) != null) {
                    commandvalue.setText(commands.get(1));
                } else {
                    commandvalue.setText("-");
                }

            } catch (Exception e) {
                playbackFinished();
            }
        });

        stop.setOnAction(event -> {
            playAllTask.cancel();
            env.getPlayer().stop();
            hidePlaybackGui();
        });

    }

    public void playbackFinished() {
        playall.setDisable(true);
        playnext.setDisable(true);

    }

    private void showPlaybackGui(String firstCommand) {
        AnchorPane.setTopAnchor(txtTranscript, 40.0);
        txtCommand.setDisable(true);
        txtTranscript.setPromptText("");
        btnGo.setDisable(true);
        playall.setDisable(false);
        playnext.setDisable(false);
        commandvalue.setText(firstCommand);
    }

    public void hidePlaybackGui() {
        AnchorPane.setTopAnchor(txtTranscript, 0.0);
        txtTranscript.setPromptText("");
        txtCommand.setDisable(false);
        btnGo.setDisable(false);
        commandvalue.setText("");
    }

    public void giveFocus() {
        txtCommand.requestFocus();
        txtCommand.positionCaret(txtCommand.getText().length());
    }

    public void showTranscript() {
        if (!isExpanded) {
            playbackToolbar.setMaxWidth(Region.USE_COMPUTED_SIZE);
            txtTranscript.setMaxWidth(Region.USE_COMPUTED_SIZE);
            txtCommand.setMaxWidth(Region.USE_COMPUTED_SIZE);
            btnGo.setMaxWidth(Region.USE_COMPUTED_SIZE);
            transcriptAnchor.setVisible(true);
            transcriptAnchor.setMaxWidth(Region.USE_COMPUTED_SIZE);
            isExpanded = true;
            env.getRootController().resizeSplitPane(0.75);
        } else {
            hideTranscript();
            env.getRootController().resizeSplitPane(1.0);
        }

    }

}
