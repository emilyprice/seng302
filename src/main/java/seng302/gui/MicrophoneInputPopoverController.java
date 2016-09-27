package seng302.gui;

import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

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
        } else {
            try {
                microphoneInput.startRecording(false);
                recordButton.setText("Stop Recording");
                recording = true;
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

}
