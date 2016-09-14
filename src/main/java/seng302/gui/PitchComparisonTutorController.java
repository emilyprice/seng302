package seng302.gui;

import org.controlsfx.control.RangeSlider;

import java.util.ArrayList;
import java.util.Random;

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
import javafx.util.Pair;
import seng302.Environment;
import seng302.data.Note;
import seng302.utility.NoteRangeSlider;
import seng302.utility.TutorRecord;
import seng302.utility.musicNotation.MidiNotePair;

/**
 * Created by jat157 on 20/03/16.
 */
public class PitchComparisonTutorController extends TutorController {

    @FXML
    ComboBox<MidiNotePair> cbxLower;

    @FXML
    AnchorPane pitchTutorAnchor;

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
                String midiTwo = String.valueOf(lowerPitchBound + rand.nextInt(pitchRange + 1));

                Pair<String, String> midis = new Pair<String, String>(midiOne, midiTwo);
                HBox rowPane = generateQuestionPane(midis);
                TitledPane qPane = new TitledPane("Question " + (i + 1), rowPane);
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
        paneQuestions.prefWidthProperty().bind(pitchTutorAnchor.prefWidthProperty());

    }


    /**
     * Fills the pitch combo boxes and sets them to default values. Also sets the env and manager.
     *
     * @param env The environment of the app.
     */
    public void create(Environment env) {
        super.create(env);
        initialiseQuestionSelector();
        rangeSlider = new NoteRangeSlider(notes, 12, 60, 72);
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

        disableButtons(row, 1, 5);

        Integer correctChoice = 0;


        if (((ToggleButton) row.getChildren().get(1)).isSelected()) { //Higher\
            row.getChildren().get(1).setStyle("-fx-text-fill: white;-fx-background-color: black");
            if (noteComparison(true, note1, note2)) correctChoice = 1;
            String[] question = new String[]{
                    String.format("Is %s higher or lower than %s", note2.getNote(), note1.getNote()),
                    "Higher",
                    correctChoice.toString()
            };

            record.addQuestionAnswer(question);
        } else if (((ToggleButton) row.getChildren().get(2)).isSelected()) { //Same
            row.getChildren().get(2).setStyle("-fx-text-fill: white;-fx-background-color: black");
            if (note1 == note2) correctChoice = 1;
            String[] question = new String[]{
                    String.format("Is %s higher or lower than %s", note2.getNote(), note1.getNote()),
                    "Same",
                    correctChoice.toString()
            };

            record.addQuestionAnswer(question);
        } else if (((ToggleButton) row.getChildren().get(3)).isSelected()) { //Lower
            row.getChildren().get(3).setStyle("-fx-text-fill: white;-fx-background-color: black");
            if (noteComparison(false, note1, note2)) {
                correctChoice = 1;
            }
            String[] question = new String[]{
                    String.format("Is %s higher or lower than %s", note2.getNote(), note1.getNote()),
                    "Lower",
                    correctChoice.toString()
            };
            record.addQuestionAnswer(question);
        } else if (((ToggleButton) row.getChildren().get(4)).isSelected()) { //Skip
            row.getChildren().get(4).setStyle("-fx-text-fill: white;-fx-background-color: black");
            correctChoice = 2;

            String[] question = new String[]{
                    String.format("Is %s higher or lower than %s", note2.getNote(), note1.getNote()),
                    getAnswer(note1, note2),
                    correctChoice.toString()
            };
            record.addQuestionAnswer(question);
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
    public HBox generateQuestionPane(Pair midis) {

        final HBox rowPane = new HBox();
        formatQuestionRow(rowPane);
        final String midiOne = midis.getKey().toString();
        final String midiTwo = midis.getValue().toString();
        final Label correctAnswer = correctAnswer(getAnswer(Note.lookup(midiOne), Note.lookup(midiTwo)));

        ToggleGroup group = new ToggleGroup();
        ToggleButton higher = new ToggleButton("Higher");
        Image imageUp = new Image(getClass().getResourceAsStream("/images/up-arrow.png"), 20, 20, true, true);
        higher.setGraphic(new ImageView(imageUp));
        higher.setToggleGroup(group);
        ToggleButton same = new ToggleButton("Same");
        Image imageSame = new Image(getClass().getResourceAsStream("/images/minus-symbol.png"), 20, 20, true, true);
        same.setGraphic(new ImageView(imageSame));
        same.setToggleGroup(group);
        ToggleButton lower = new ToggleButton("Lower");
        Image imageLower = new Image(getClass().getResourceAsStream("/images/download-arrow-1.png"), 20, 20, true, true);
        lower.setGraphic(new ImageView(imageLower));
        lower.setToggleGroup(group);
        ToggleButton skip = new ToggleButton("Skip");
        styleSkipToggleButton(skip);
        skip.setToggleGroup(group);

        higher.setOnAction(event -> {
            int responseValue = questionResponse(rowPane, midiOne, midiTwo);
            if (responseValue == 0) {
                correctAnswer.setVisible(true);
            }


        });

        lower.setOnAction(event -> {
            int responseValue = questionResponse(rowPane, midiOne, midiTwo);
            if (responseValue == 0) {
                correctAnswer.setVisible(true);
            }
        });

        same.setOnAction(event -> {
            int responseValue = questionResponse(rowPane, midiOne, midiTwo);
            if (responseValue == 0) {
                correctAnswer.setVisible(true);
            }
        });


        skip.setOnAction(event -> {
            int responseValue = questionResponse(rowPane, midiOne, midiTwo);
            if (responseValue == 0) {
                correctAnswer.setVisible(true);
            }
        });


        Button playBtn = new Button();
        stylePlayButton(playBtn);

        playBtn.setOnAction(event -> {
            Note note1 = Note.lookup(midiOne);
            Note note2 = Note.lookup(midiTwo);
            ArrayList<Note> notes = new ArrayList<>();
            notes.add(note1);
            notes.add(note2);
            env.getPlayer().playNotes(notes, 48);
        });

        rowPane.getChildren().add(playBtn);
        rowPane.getChildren().add(higher);
        rowPane.getChildren().add(same);
        rowPane.getChildren().add(lower);
        rowPane.getChildren().add(skip);
        rowPane.getChildren().add(correctAnswer);

        rowPane.prefWidthProperty().bind(paneQuestions.prefWidthProperty());

        return rowPane;
    }


    /**
     * Note comparison
     *
     * @param isHigher whether the user thinks the second note is higher or lower.
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

    private String getAnswer(Note note1, Note note2) {
        if (note1.getMidi().equals(note2.getMidi())) {
            return "Same";
        } else if (note1.getMidi() < note2.getMidi()) {
            return "Higher";
        } else {
            return "Lower";
        }
    }

    public void resetInputs() {
        rangeSlider.setLowValue(60);
        rangeSlider.setHighValue(72);
    }


}
