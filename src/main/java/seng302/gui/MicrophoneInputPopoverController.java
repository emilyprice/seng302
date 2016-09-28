package seng302.gui;

import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import seng302.Environment;
import seng302.MicrophoneInput;

/**
 * Created by djj36 on 26-09-16
 */
public class MicrophoneInputPopoverController {

    @FXML
    private Button recordButton;

    @FXML
    private Text currentNoteText;

    @FXML
    private TextArea allNotesTextArea;

    @FXML
    private Button transcriptButton;

    private Environment env;
    private MicrophoneInput microphoneInput;
    private boolean recording = false;
    private ArrayList<String> foundNotes;

    /**
     * Initialises some of the basic settings for the popover - this includes the environment, which
     * provides access to the microphone input functionality.
     *
     * @param env Environment.
     */
    public void create(Environment env) {
        this.env = env;
        microphoneInput = env.getMicrophoneInput();
        currentNoteText.setText("");
        microphoneInput.addPopover(this);
        allNotesTextArea.setFocusTraversable(false);
        allNotesTextArea.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (allNotesTextArea.isFocused()) {
                    env.getRootController().getTranscriptController().giveFocus();
                }
            }
        });
    }

    /**
     * OnAction command for the recordButton. Stops or starts recording as per current state.
     */
    @FXML
    private void startStopRecording() {
        if (recording) {
            recordButton.setText("Start Recording");
            microphoneInput.stopRecording();
            recording = false;
            if (allNotesTextArea.getText().length() != 0) {
                transcriptButton.setDisable(false);
            }
        } else {
            try {
                microphoneInput.startRecording(false);
                recordButton.setText("Stop Recording");
                recording = true;
                transcriptButton.setDisable(true);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Implementation of the observable pattern. Called by the microphone input - updates the
     * fields in the popover.
     * @param foundNotes - A list of all the notes recognised to date.
     * @param currentNote - The most recently recognised note.
     */
    public void update(ArrayList<String> foundNotes, String currentNote) {
        if (currentNote != null) {
            currentNoteText.setText(currentNote);
            allNotesTextArea.setText("");
            for (String note : foundNotes) {
                allNotesTextArea.appendText(note + " ");
            }
        } else {
            currentNoteText.setText("");
            allNotesTextArea.setText("");
            for (String note : foundNotes) {
                allNotesTextArea.appendText(note + " ");
            }
        }
        this.foundNotes = foundNotes;
    }

    @FXML
    private void sendToTranscript() {
        transcriptButton.setDisable(true);
        String prevTxt = env.getRootController().getTranscriptController().txtCommand.getText();
        String newTxt = prevTxt + " " + allNotesTextArea.getText();
        env.getRootController().getTranscriptController().txtCommand.setText(newTxt);
        allNotesTextArea.clear();
    }

}
