package seng302.gui;

import com.jfoenix.controls.JFXButton;

import org.controlsfx.control.RangeSlider;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.*;
import javafx.scene.text.Font;
import javafx.util.Pair;
import seng302.Environment;
import seng302.MicrophoneInput;
import seng302.data.Note;
import seng302.utility.NoteRangeSlider;
import seng302.utility.TutorRecord;
import seng302.utility.musicNotation.MidiNotePair;

/**
 * Created by Elliot on 26/09/2016.
 */
public class MicrophoneInputTutorController extends TutorController {

    @FXML
    ComboBox<MidiNotePair> cbxLower;

    @FXML
    AnchorPane microphoneTutorAnchor;

    @FXML
    VBox paneInit;

    @FXML
    ComboBox<MidiNotePair> cbxUpper;

    @FXML
    Button btnGo;

    @FXML
    HBox settings;

    @FXML
    RangeSlider rangeSlider;

    private MicrophoneInput microphoneInput;

    @FXML
    Label notes;

    Random rand;

    Boolean lowerSet = false;
    Boolean upperSet = false;

    @FXML
    private void initialize() {

        rand = new Random();

    }

    /**
     * The command which is bound to the Go button, or the enter key when the command prompt is
     * active. Checks that both lower and upper notes selected, alerts user if not.
     */
    @FXML
    private void goAction() {
        paneInit.setVisible(false);
        paneQuestions.setVisible(true);
        record = new TutorRecord();
        manager.answered = 0;
        qPanes = new ArrayList<>();

        if (lowerSet && upperSet) {
            questionRows.getChildren().clear();
            manager.resetEverything();
            manager.questions = selectedQuestions;
            for (int i = 0; i < manager.questions; i++) {

                int lowerPitchBound = ((Double) rangeSlider.getLowValue()).intValue();
                int upperPitchBound = ((Double) rangeSlider.getHighValue()).intValue();
                int pitchRange = upperPitchBound - lowerPitchBound;
                String midiOne = String.valueOf(lowerPitchBound + rand.nextInt(pitchRange + 1));

//                Pair<String, String> midis = new Pair<>(midiOne, midiTwo);
                HBox rowPane = generateQuestionPane(midiOne);
                TitledPane qPane = new TitledPane((i + 1) + ". Can you sing or play the note displayed?", rowPane);
                qPane.setPadding(new Insets(2, 2, 2, 2));
                qPanes.add(qPane);

                VBox.setMargin(rowPane, new Insets(10, 10, 10, 10));
            }
            qAccordion.getPanes().addAll(qPanes);
            qAccordion.setExpandedPane(qAccordion.getPanes().get(0));
            questionRows.getChildren().add(qAccordion);

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Unselected range");
            alert.setContentText("Please select valid upper and lower ranges");
            alert.setResizable(false);
            alert.showAndWait();
        }
        paneQuestions.prefWidthProperty().bind(microphoneTutorAnchor.prefWidthProperty());

    }


    /**
     * Generates default rangeslider value for competitive mode
     * @return
     */
    private int generateRangesliderDefault(){
        int num = rand.nextInt(95);
        if (num + 24 > 95) {
            return 84;
        } else {
            return num + 16;
        }
    }

    /**
     * Fills the pitch combo boxes and sets them to default values. Also sets the env and manager.
     *
     * @param env The environment of the app.
     */
    public void create(Environment env) {
        super.create(env);
        initialiseQuestionSelector();

        if (currentProject.getIsCompetitiveMode()) {
            int lowValue = generateRangesliderDefault();
            rangeSlider = new NoteRangeSlider(notes, 12, lowValue, lowValue+24);
            rangeSlider.setDisable(true);
        }else{
            rangeSlider = new NoteRangeSlider(notes, 12, 60, 72);
        }
        paneInit.getChildren().add(1, rangeSlider);
        lowerSet = true;
        upperSet = true;
    }

    /**
     * Changes the questionPane after the user has answered the question (with response to their
     * answer).
     *
     * @param row Which questionPane the question is from.
     * @param m1  The first note being compared.
     * @param m2  the second note being compared.
     * @return correctChoice What the answer is.
     */
    private int questionResponse(HBox row, String m1, String m2) {

        Note note1 = Note.lookup(m1);
        Note note2 = Note.lookup(m2);

        disableButtons(row, 1, row.getChildren().size() - 1);

        Integer correctChoice = 0;

        if (note1.getNote().equals(note2.getNote())) {
            correctChoice = 1;
        }

        if (correctChoice == 1) {
            formatCorrectQuestion(row);
        } else if (correctChoice == 2) formatSkippedQuestion(row);
        else {
            formatIncorrectQuestion(row);
        }


        manager.add(new Pair<>(note1.getNote(), note2.getNote()), correctChoice);

        handleAccordion();
        if (manager.answered == manager.questions) {
            finished();
        }

        return correctChoice;
    }

    /**
     * Constructs the question panels.
     */
    public HBox generateQuestionPane(String noteMidi) {

        Label noteLabel = new Label(Note.lookup(noteMidi).getNote());
        noteLabel.setFont(Font.font("System Regular", 17));
        Label answerLabel = new Label();
        final HBox rowPane = new HBox();
        formatQuestionRow(rowPane);
        Note correctAnswer = Note.lookup(noteMidi);
        Button recordButton = new Button("Record");


        microphoneInput = env.getMicrophoneInput();
        recordButton.setOnAction(event -> {
            // TODO microphone input
            if (recordButton.getText().equals("Record")) {
                env.getMicrophoneInput().recordSingleNoteTutorInput();
                recordButton.setText("Stop");
            } else {
                try {
                    String recordedNote = env.getMicrophoneInput().stopRecording().get(0);
                    answerLabel.setText(recordedNote);
                    int responseValue = questionResponse(rowPane, noteMidi, recordedNote);
                    recordButton.setDisable(true);
                } catch (Exception e) {
                    recordButton.setText("Record");
                    answerLabel.setText("Note not recognised, try again.");
                }
//                correctAnswer = Note.lookup(recordedNote);

//                if (responseValue == 0) {
//                    correctAnswer.setVisible(true);
//                }
            }
        });


        JFXButton skip = new JFXButton("Skip");

        skip.setOnAction(event -> {
            int responseValue = 0;
            if (responseValue == 0) {
//                correctAnswer.setVisible(true);
            }
        });

        if (isCompMode) {
            skip.setVisible(false);
            skip.setManaged(false);
        }

        rowPane.getChildren().add(noteLabel);
        rowPane.getChildren().add(recordButton);
//        rowPane.getChildren().add(stopButton);
        rowPane.getChildren().add(answerLabel);
        rowPane.getChildren().add(skip);

        rowPane.prefWidthProperty().bind(paneQuestions.prefWidthProperty());

        return rowPane;
    }



    /**
     * Note comparison
     *
//     * @param isHigher whether the user thinks the second note is higher or lower.
     * @param note1    the first note being compared.
     * @param note2    the second note being compared.
     * @return boolean if the user was correct or incorrect.
     */
    private boolean noteComparison(boolean isHigher, Note note1, Note note2) {
        if (isHigher) {
            return note1.getMidi() < note2.getMidi();
        } else {
            return note1.getMidi() > note2.getMidi();
        }
    }

    public void resetInputs() {
        rangeSlider.setLowValue(60);
        rangeSlider.setHighValue(72);
    }

}
