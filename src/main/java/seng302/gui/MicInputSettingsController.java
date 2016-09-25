package seng302.gui;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import seng302.Environment;
import seng302.MicrophoneInput;
import seng302.data.Note;

public class MicInputSettingsController {

    @FXML
    private ToggleButton recordButton;

    @FXML
    private ToggleGroup MIC;

    @FXML
    private ToggleButton stopButton;

    @FXML
    private TextArea textArea;

    @FXML
    private VBox inputDevices;

    @FXML
    private Slider thresholdSlider;

    @FXML
    private Text thresholdText;

    private MicrophoneInput microphoneInput;

    private ToggleGroup inputToggleGroup = new ToggleGroup();

    private Environment env;

    public void create(Environment env) {
        microphoneInput = env.getMicrophoneInput();
        recordButton.setOnAction(event -> {
            try {
                microphoneInput.startRecording();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        });
        stopButton.setOnAction(event -> microphoneInput.stopRecording());
        this.env = env;

        // Populate input fields
        for (Mixer.Info info : microphoneInput.getMixerInfo(false, true)) {
            RadioButton radio = new RadioButton(microphoneInput.toLocalString(info));
            radio.setToggleGroup(inputToggleGroup);
            radio.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        microphoneInput.setMixer(AudioSystem.getMixer(info));
                    } catch (Exception e) {
                        System.err.println("Input device not supported.");
                    }
                }
            });
            radio.setPadding(new Insets(5, 0, 5, 15));
            inputDevices.getChildren().add(radio);
        }
        thresholdSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                thresholdSlider.setValue(Math.round(thresholdSlider.getValue()));
                thresholdText.setText(String.valueOf(thresholdSlider.getValue()));
                microphoneInput.setThreshold(thresholdSlider.getValue());
            }
        });
    }
}
