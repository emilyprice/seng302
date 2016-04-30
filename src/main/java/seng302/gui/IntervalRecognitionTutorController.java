package seng302.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import seng302.Environment;
import seng302.data.Interval;
import seng302.data.Note;
import seng302.utility.TutorRecord;

public class IntervalRecognitionTutorController extends TutorController {

    @FXML
    AnchorPane IntervalRecognitionTab;


    @FXML
    Button btnGo;

    /**
     * A constructor required for superclass to work
     */
    public IntervalRecognitionTutorController() {
        super();
    }

    public void create(Environment env) {
        super.create(env);
        initaliseQuestionSelector();
    }

    /**
     * Run when the user clicks the "Go" button.
     * Generates and displays a new set of questions.
     * @param event The mouse click that initiated the method.
     */
    public void goAction(ActionEvent event) {
        paneQuestions.setVisible(true);
        paneResults.setVisible(false);
        record = new TutorRecord(new Date(), "Interval Recognition");
        manager.questions = selectedQuestions;
        if (manager.questions >= 1){
            // Run the tutor
            questionRows.getChildren().clear();
            for (int i = 0; i < manager.questions; i++) {
                HBox questionRow = setUpQuestion();
                questionRows.getChildren().add(questionRow);
                questionRows.setMargin(questionRow, new Insets(10, 10, 10, 10));
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid Number of Intervals");
            alert.setContentText("Please select a positive number of intervals");
            alert.setResizable(false);
            alert.showAndWait();
        }

    }


    /**
     * This function generates information for a new question, and displays it in the GUI
     * @return an HBox object containing the GUI for one question
     */
    private HBox setUpQuestion() {
        Interval thisInterval = generateInterval();
        Note firstNote = getStartingNote(thisInterval.getSemitones());
        Pair<Interval, Note> pair = new Pair<Interval, Note>(thisInterval, firstNote);
        return generateQuestionPane(pair);
    }


    /**
     * Creates a GUI section for one question.
     * @return a JavaFX HBox containing controls and info about one question.
     */
    public HBox generateQuestionPane(Pair intervalAndNote) {
        final HBox questionRow = new HBox();
        formatQuestionRow(questionRow);

        //Add buttons for play and skip
        Button play = new Button();
        Image imagePlay = new Image(getClass().getResourceAsStream("/images/play-button.png"), 20, 20, true, true);
        play.setGraphic(new ImageView(imagePlay));
        play.setStyle("-fx-base: #40a927;");
        Button skip = new Button("Skip");
        Image imageSkip = new Image(getClass().getResourceAsStream("/images/right-arrow.png"), 20, 20, true, true);
        skip.setGraphic(new ImageView(imageSkip));
        final ComboBox<String> options = generateChoices();
        options.setPrefHeight(30);

        final Pair pair = intervalAndNote;
        final Interval thisInterval = (Interval) pair.getKey();
        final Note firstNote = (Note) pair.getValue();
        final Note secondNote = getFinalNote(firstNote, thisInterval);
        final ArrayList<Note> playNotes = new ArrayList<Note>();

        final Label correctAnswer = correctAnswer(thisInterval.getName());

        playNotes.add(firstNote);
        playNotes.add(secondNote);

        play.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                env.getPlayer().playNotes(playNotes, 48);
            }
        });

        skip.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                // Disables only input buttons
                disableButtons(questionRow, 1, 3);
                formatSkippedQuestion(questionRow);
                manager.questions -= 1;
                manager.add(pair, 2);
                String[] question = new String[]{
                        String.format("Interval between %s and %s", firstNote.getNote(), secondNote.getNote()),
                        thisInterval.getName()
                };
                record.addSkippedQuestion(question);
                if (manager.answered == manager.questions) {
                    finished();
                }
            }
        });

        options.setOnAction(new EventHandler<ActionEvent>() {
            // This handler colors the GUI depending on the user's input
            public void handle(ActionEvent event) {
                // Disables only input buttons
                disableButtons(questionRow, 1, 3);
                if (options.getValue().equals(thisInterval.getName())) {
                    formatCorrectQuestion(questionRow);
                    manager.add(pair, 1);
                } else {
                    correctAnswer.setVisible(true);
                    formatIncorrectQuestion(questionRow);
                    manager.add(pair, 0);
                }
                manager.answered += 1;
                // Sets up the question to be saved to the record
                String[] question = new String[] {
                        String.format("Interval between %s and %s", firstNote.getNote(), secondNote.getNote()),
                        options.getValue(),
                        Boolean.toString(options.getValue().equals(thisInterval.getName()))
                };
                record.addQuestionAnswer(question);
                // Shows the correct answer
                if (manager.answered == manager.questions) {
                    finished();
                }
            }
        });

        questionRow.getChildren().add(0, play);
        questionRow.getChildren().add(1, options);
        questionRow.getChildren().add(2, skip);
        questionRow.getChildren().add(3, correctAnswer);

        questionRow.prefWidthProperty().bind(paneQuestions.prefWidthProperty());
        return questionRow;
    }

    // The following methods are specific to this tutor

    /**
     * Randomly selects a note for the interval.
     * @param numSemitones The generated interval, so the second note is not outside correct range
     * @return A Note object, for playing an interval.
     */
    private Note getStartingNote(int numSemitones) {
        Random randNote = new Random();
        return Note.lookup(String.valueOf(randNote.nextInt(128 - numSemitones)));
    }

    /**
     * Calculates the second note of an interval based on the first.
     * @param startingNote The first note of an interval
     * @param interval The number of semitones in an interval
     * @return The second note of the interval
     */
    private Note getFinalNote(Note startingNote, Interval interval) {
        return startingNote.semitoneUp(interval.getSemitones());
    }

    /**
     * Randomly selects an interval from the approved list
     * @return the randomly selected interval
     */
    private Interval generateInterval() {
        Random rand = new Random();
        // There are 8 different intervals
        return Interval.intervals[rand.nextInt(8)];
    }

    /**
     * Creates a JavaFX combo box containing the lexical names of all intervals.
     * @return a combo box of interval options
     */
    private ComboBox<String> generateChoices() {
        ComboBox<String> options = new ComboBox<String>();
        for (Interval interval:Interval.intervals) {
            options.getItems().add(interval.getName());
        }
        return options;
    }

}