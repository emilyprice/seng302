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


    public void create(Environment env) {
        this.env = env;
        microphoneInput = env.getMicrophoneInput();
        currentNoteText.setText("");
        microphoneInput.addPopover(this);
    }

    @FXML
    private void startStopRecording() {
        if (recording) {
            recordButton.setText("Start Recording");
            microphoneInput.stopRecording();
            recording = false;
        } else {
            try {
                microphoneInput.startRecording();
                recordButton.setText("Stop Recording");
                recording = true;
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
    }

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
