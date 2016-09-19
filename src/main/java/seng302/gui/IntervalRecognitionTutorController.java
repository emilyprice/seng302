package seng302.gui;

import org.controlsfx.control.RangeSlider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import seng302.Environment;
import seng302.data.Interval;
import seng302.data.Note;
import seng302.utility.NoteRangeSlider;
import seng302.utility.TutorRecord;

public class IntervalRecognitionTutorController extends TutorController {

    @FXML
    AnchorPane IntervalRecognitionTab;

    @FXML
    Button btnGo;

    @FXML
    VBox paneInit;

    @FXML
    Label notes;

    RangeSlider rangeSlider;

    /**
     * A constructor required for superclass to work
     */
    public IntervalRecognitionTutorController() {
        super();
    }

    public void create(Environment env) {
        super.create(env);
        initialiseQuestionSelector();
//        initaliseRangeSelector();

        if (currentProject.getIsCompetitiveMode()) {
            int lowValue = generateRangesliderDefault();
            rangeSlider = new NoteRangeSlider(notes, 12, lowValue, lowValue + 24);
            rangeSlider.setDisable(true);
        } else {
            rangeSlider = new NoteRangeSlider(notes, 24, 48, 72);

        }
        paneInit.getChildren().add(1, rangeSlider);
    }


    /**
     * Generates default rangeslider value for competitive mode
     */
    private int generateRangesliderDefault() {
        Random rand = new Random();
        int num = rand.nextInt(127);
        if (num + 24 > 127) {
            return 103;
        } else {
            return num;
        }

    }

    /**
     * Run when the user clicks the "Go" button. Generates and displays a new set of questions.
     *
     * @param event The mouse click that initiated the method.
     */
    public void goAction(ActionEvent event) {
        paneInit.setVisible(false);
        paneQuestions.setVisible(true);
        record = new TutorRecord();
        manager.resetEverything();
        manager.questions = selectedQuestions;
        qPanes = new ArrayList<>();

        if (manager.questions >= 1) {
            // Run the tutor
            questionRows.getChildren().clear();
            for (int i = 0; i < manager.questions; i++) {
                HBox questionRow = setUpQuestion();
                TitledPane qPane = new TitledPane((i + 1) + ". What is this interval?", questionRow);
                qPane.setPadding(new Insets(2, 2, 2, 2));
                qPanes.add(qPane);
                VBox.setMargin(questionRow, new Insets(10, 10, 10, 10));
            }
            qAccordion.getPanes().addAll(qPanes);
            qAccordion.setExpandedPane(qAccordion.getPanes().get(0));
            questionRows.getChildren().add(qAccordion);

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
     *
     * @return an HBox object containing the GUI for one question
     */
    private HBox setUpQuestion() {
        Interval thisInterval = generateInterval();
        int lowerPitchBound = ((Double) rangeSlider.getLowValue()).intValue();
        int upperPitchBound = ((Double) rangeSlider.getHighValue()).intValue();
        Note firstNote = getStartingNote(thisInterval.getSemitones(), lowerPitchBound, upperPitchBound);
        Pair<Interval, Note> pair = new Pair<>(thisInterval, firstNote);
        return generateQuestionPane(pair);
    }


    /**
     * Creates a GUI section for one question.
     *
     * @return a JavaFX HBox containing controls and info about one question.
     */
    public HBox generateQuestionPane(Pair intervalAndNote) {
        final HBox questionRow = new HBox();
        formatQuestionRow(questionRow);

        //Add buttons for play and skip
        Button play = new Button();
        stylePlayButton(play);

        Button skip = new Button("Skip");
        styleSkipButton(skip);

        final ComboBox<String> options = generateChoices((Interval) intervalAndNote.getKey());
        options.setPrefHeight(30);

        final Pair pair = intervalAndNote;
        final Interval thisInterval = (Interval) pair.getKey();
        final Note firstNote = (Note) pair.getValue();
        final Note secondNote = getFinalNote(firstNote, thisInterval);
        final ArrayList<Note> playNotes = new ArrayList<>();

        final Label correctAnswer = correctAnswer(thisInterval.getName());

        playNotes.add(firstNote);
        playNotes.add(secondNote);

        play.setOnAction(event -> {
            env.getPlayer().playNotes(playNotes, 48);
        });

        skip.setOnAction(event -> {
            // Disables only input buttons
            disableButtons(questionRow, 1, 3);
            formatSkippedQuestion(questionRow);
            manager.add(pair, 2);


            String[] question = new String[]{
                    String.format("Interval between %s and %s", firstNote.getNote(), secondNote.getNote()),
                    thisInterval.getName(),
                    "2"
            };
            record.addQuestionAnswer(question);
            handleAccordion();
            if (manager.answered == manager.questions) {
                finished();
            }
        });

        options.setOnAction(event -> {
            // This handler colors the GUI depending on the user's input
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
            Integer correct = 0;
            if (options.getValue().equals(thisInterval.getName())) {
                correct = 1;
            }
            // Sets up the question to be saved to the record
            String[] question = new String[]{
                    String.format("Interval between %s and %s", firstNote.getNote(), secondNote.getNote()),
                    options.getValue(),
                    correct.toString()
            };
            record.addQuestionAnswer(question);
            // Shows the correct answer
            handleAccordion();
            if (manager.answered == manager.questions) {
                finished();
            }
        });

        if (isCompMode) {
            skip.setVisible(false);
            skip.setManaged(false);
        }

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
     *
     * @param numSemitones The generated interval, so the second note is not outside correct range
     * @return A Note object, for playing an interval.
     */
    private Note getStartingNote(int numSemitones, int lowerPitchRange, int upperPitchRange) {
        Random randNote = new Random();
        int note = randNote.nextInt(upperPitchRange - numSemitones - lowerPitchRange + 1) + lowerPitchRange;
        return Note.lookup(String.valueOf(note));
    }

    /**
     * Calculates the second note of an interval based on the first.
     *
     * @param startingNote The first note of an interval
     * @param interval     The number of semitones in an interval
     * @return The second note of the interval
     */
    private Note getFinalNote(Note startingNote, Interval interval) {
        return startingNote.semitoneUp(interval.getSemitones());
    }

    /**
     * Randomly selects an interval from the approved list
     *
     * @return the randomly selected interval
     */
    private Interval generateInterval() {
        Random rand = new Random();
        // There are 15 different intervals
        return Interval.intervals[rand.nextInt(27)];
    }

    /**
     * Creates a JavaFX combo box containing the lexical names of all intervals.
     *
     * @return a combo box of interval options
     */
    private ComboBox<String> generateChoices1() {
        ComboBox<String> options = new ComboBox<>();
        for (Interval interval : Interval.intervals) {
            options.getItems().add(interval.getName());
        }
        return options;
    }


    /**
     * Generates and populates The Origin combo box It generates the options in a range around the
     * correct answer
     */
    private ComboBox<String> generateChoices(Interval thisInterval) {
        Random rand = new Random();
        ComboBox<String> options = new ComboBox<>();

        int currentSemitones = thisInterval.getSemitones();
        int lowSemi = currentSemitones - 1;
        int highSemi = currentSemitones + 1;
        int higher;
        boolean tooHigh = highSemi > 24;
        boolean tooLow = lowSemi < 0;
        ArrayList<Interval> enharmonic;
        ArrayList<String> optionContent = new ArrayList<>();
        optionContent.add(thisInterval.getName());

        while (optionContent.size() < 8) {

            if (tooHigh) {
                higher = 0;
            } else if (tooLow) {
                higher = 1;
            } else {

                higher = rand.nextInt(1);
            }

            if (higher == 1) {
                if (highSemi <= 24) {
                    enharmonic = Interval.lookupBySemitones(highSemi);

                    optionContent.add(enharmonic.get(rand.nextInt(enharmonic.size())).getName());
                    highSemi += 1;
                } else {
                    tooHigh = true;
                }

            } else {
                if (lowSemi >= 0) {
                    enharmonic = Interval.lookupBySemitones(lowSemi);
                    optionContent.add(enharmonic.get(rand.nextInt(enharmonic.size())).getName());
                    lowSemi -= 1;
                } else {
                    tooLow = true;
                }
            }

        }

        Collections.shuffle(optionContent);
        for (String interval : optionContent) {
            options.getItems().add(interval);
        }
        return options;
    }


    public void resetInputs() {
        rangeSlider.setLowValue(60);
        rangeSlider.setHighValue(72);
    }


}