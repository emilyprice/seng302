package seng302.gui;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import seng302.Environment;
import seng302.data.Note;
import seng302.utility.TutorRecord;
import seng302.utility.musicNotation.ChordUtil;


/**
 * Created by Elliot on 16/05/16 Controller class for the chord recognition tutor.
 */
public class ChordRecognitionTutorController extends TutorController {
    @FXML
    HBox settings;

    @FXML
    AnchorPane chordTutorAnchor;

    @FXML
    Button btnGo;

    @FXML
    ComboBox<String> playChords;

    @FXML
    ComboBox<Integer> octaves;

    @FXML
    ComboBox<String> chordTypeBox;

    private Random rand;


    @FXML
    /**
     * Run when the go button is pressed. Creates a new tutoring session.
     */
    private void goAction(ActionEvent event) {
        record = new TutorRecord();
        paneInit.setVisible(false);
        paneQuestions.setVisible(true);
        manager.resetEverything();
        manager.questions = selectedQuestions;
        isCompMode = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode();
        qPanes = new ArrayList<>();

        questionRows.getChildren().clear();
        for (int i = 0; i < manager.questions; i++) {
            HBox questionRow = setUpQuestion();
            TitledPane qPane = new TitledPane((i + 1) + ". What type of chord is this?", questionRow);
            qPane.setPadding(new Insets(2, 2, 2, 2));
            qPanes.add(qPane);
            questionRows.setMargin(questionRow, new Insets(10, 10, 10, 10));
        }

        qAccordion.getPanes().addAll(qPanes);
        qAccordion.setExpandedPane(qAccordion.getPanes().get(0));
        questionRows.getChildren().add(qAccordion);
    }


    /**
     * Initialises certain GUI elements
     */
    public void create(Environment env) {
        super.create(env);
        initialiseQuestionSelector();
        rand = new Random();
        playChords.getItems().addAll("Unison", "Arpeggio", "Both");
        octaves.getItems().addAll(1, 2, 3, 4);
        chordTypeBox.getItems().addAll("3 Notes", "4 Notes", "Both");

        if (currentProject.getIsCompetitiveMode()) {
            playChords.setValue("Both");
            playChords.setDisable(true);
            octaves.setValue(1);
            octaves.setDisable(true);
            chordTypeBox.setValue("Both");
            chordTypeBox.setDisable(true);

        }else{

            playChords.getSelectionModel().selectFirst();
            octaves.getSelectionModel().selectFirst();
            chordTypeBox.getSelectionModel().selectFirst();
        }
    }

    /**
     * Prepares a new question
     *
     * @return a question pane containing the question information
     */
    public HBox setUpQuestion() {

        String chordType;
        Integer type;

        if (chordTypeBox.getValue().equals("Both")) {
            type = rand.nextInt(8);
        } else if (chordTypeBox.getValue().equals("3 Notes")) {
            type = rand.nextInt(3);
        } else {
            type = rand.nextInt(5) + 3;
        }

        switch (type) {
            case 0:
                chordType = "major";
                break;
            case 1:
                chordType = "minor";
                break;
            case 2:
                chordType = "diminished";
                break;
            case 3:
                chordType = "major 7th";
                break;
            case 4:
                chordType = "7th";
                break;
            case 5:
                chordType = "minor 7th";
                break;
            case 6:
                chordType = "half diminished";
                break;
            case 7:
                chordType = "diminished 7th";
                break;
            default:
                chordType = "major";
        }
        Note randNote = Note.getRandomNote();
        return generateQuestionPane(new Pair<>(randNote, chordType));
    }


    /**
     * Creates a GUI pane for a single question
     *
     * @param noteAndChord pair containing first note and type of scale to play
     */
    public HBox generateQuestionPane(Pair noteAndChord) {
        final Pair<Note, String> noteAndChordType = noteAndChord;
        final HBox questionRow = new HBox();
        formatQuestionRow(questionRow);
        final Label correctAnswer = correctAnswer(noteAndChordType.getValue());
        final Note startNote = noteAndChordType.getKey();
        final String chordType = noteAndChordType.getValue();

        final Collection<Note> theChord = new ArrayList<Note>(ChordUtil.getChord(startNote, chordType));

        Button play = new Button();
        stylePlayButton(play);

        play.setOnAction(event -> {
            //Play the scale
            int currentTempo = env.getPlayer().getTempo();
            if (playChords.getValue().equals("Unison")) {
                env.getPlayer().playSimultaneousNotes(theChord);
            } else if (playChords.getValue().equals("Arpeggio")) {
                env.getPlayer().playNotes((ArrayList) theChord);
            } else {
                env.getPlayer().playNotes((ArrayList) theChord);
                try {
                    //Calculates how long three crotchets is at the current tempo
                    Integer wait;
                    if (chordTypeBox.getValue().equals("Both") || chordTypeBox.getValue().equals("4 Notes")) {
                        wait = 1000 * 240 / currentTempo + 40;
                    } else {
                        wait = 1000 * 180 / currentTempo + 40;
                    }
                    Thread.sleep(wait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                env.getPlayer().playSimultaneousNotes(theChord);
            }
        });


        final ComboBox<String> options = generateChoices();
        options.setOnAction(event -> {
            handleQuestionAnswer(options.getValue().toLowerCase(), noteAndChordType, questionRow);
        });


        Button skip = new Button("Skip");
        styleSkipButton(skip);

        skip.setOnAction(event -> {
            // Disables only input buttons

            disableButtons(questionRow, 1, 3);

            formatSkippedQuestion(questionRow);
            manager.add(noteAndChordType, 2);
            String[] question = new String[]{
                    String.format("%s scale from %s", chordType, startNote.getNote()),
                    chordType,
                    "2"
            };
            record.addQuestionAnswer(question);
            handleAccordion();
            if (manager.answered == manager.questions) {
                finished();
            }
        });


        questionRow.getChildren().add(0, play);
        questionRow.getChildren().add(1, options);
        if (isCompMode) {
            skip.setVisible(false);
            skip.setManaged(false);
        }
        questionRow.getChildren().add(2, skip);
        questionRow.getChildren().add(3, correctAnswer);

        questionRow.prefWidthProperty().bind(paneQuestions.prefWidthProperty());
        return questionRow;
    }

    /**
     * Creates a JavaFX combo box containing the lexical names of all scales.
     *
     * @return a combo box of scale options
     */
    private ComboBox<String> generateChoices() {
        ComboBox<String> options = new ComboBox<String>();
        options.setPrefHeight(30);
        if (chordTypeBox.getValue().equals("Both")) {
            options.getItems().add("major");
            options.getItems().add("minor");
            options.getItems().add("diminished");
            options.getItems().add("minor 7th");
            options.getItems().add("major 7th");
            options.getItems().add("7th");
            options.getItems().add("half diminished");
            options.getItems().add("diminished 7th");
        } else if (chordTypeBox.getValue().equals("3 Notes")) {
            options.getItems().add("major");
            options.getItems().add("minor");
            options.getItems().add("diminished");
        } else {
            options.getItems().add("minor 7th");
            options.getItems().add("major 7th");
            options.getItems().add("7th");
            options.getItems().add("half diminished");
            options.getItems().add("diminished 7th");
        }

        return options;
    }

    /**
     * Reacts accordingly to a user's input
     *
     * @param userAnswer    The user's selection, as text
     * @param correctAnswer The correct chord, as text
     * @param questionRow   The HBox containing GUI question data
     */
    public void handleQuestionAnswer(String userAnswer, Pair correctAnswer, HBox questionRow) {
        Integer correct;
        disableButtons(questionRow, 1, 3);
        if (userAnswer.equals(correctAnswer.getValue())) {
            correct = 1;
            manager.add(correctAnswer, 1);
            formatCorrectQuestion(questionRow);
        } else {
            correct = 0;
            manager.add(correctAnswer, 0);
            formatIncorrectQuestion(questionRow);
            //Shows the correct answer
            questionRow.getChildren().get(3).setVisible(true);
        }
        Note startingNote = (Note) correctAnswer.getKey();
        String[] question = new String[]{
                String.format("%s chord from %s",
                        correctAnswer.getValue(),
                        startingNote.getNote()),
                userAnswer,
                Integer.toString(correct)
        };
        record.addQuestionAnswer(question);

        handleAccordion();

        if (manager.answered == manager.questions) {
            finished();
        }
    }

    /**
     * Returns the option combo boxes to their default states.
     */
    public void resetInputs() {
        playChords.getSelectionModel().selectFirst();
        octaves.getSelectionModel().selectFirst();
    }

}
