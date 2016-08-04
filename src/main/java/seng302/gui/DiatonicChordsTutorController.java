package seng302.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import seng302.Environment;
import seng302.command.Scale;
import seng302.data.Note;
import seng302.utility.TutorRecord;
import seng302.utility.musicNotation.ChordUtil;

/**
 * Created by isabelle on 30/07/16.
 */
public class DiatonicChordsTutorController extends TutorController {
    private Random rand;
    private final String typeOneText = "What is the %s chord of %s major?";
    private final String typeTwoText = "In %s major, what is %s?";
    Integer type = 1;


    public void create(Environment env) {
        super.create(env);
        initialiseQuestionSelector();
        rand = new Random();
    }

    /**
     * Generate the questions that ask the diatonic chord of a certain function.
     *
     * @return a Pair that includes the function and the Note name.
     */
    private Pair generateQuestionTypeOne() {
        Integer function = rand.nextInt(7) + 1;
        String functionInRomanNumeral = ChordUtil.integerToRomanNumeral(function);
        Note randomNote = Note.getRandomNote();
        String randomNoteName = randomiseNoteName(randomNote);
        Pair question = new Pair(functionInRomanNumeral, randomNoteName);
        String answer = ChordUtil.getChordFunction(functionInRomanNumeral, randomNoteName, "major");
        return new Pair(question, answer);
    }

    /**
     * Generate the questions that ask for the function of a chord and key?
     *
     * @return a Pair that includes the random note name and another Pair containing chord Note,
     * chord type.
     */
    private Pair generateQuestionTypeTwo() {
        Note randomNote = Note.getRandomNote();
        String randomNoteName = randomiseNoteName(randomNote);
        // Functional questions:
        ArrayList<Note> scale = randomNote.getOctaveScale("major", 1, true);
        Integer numInScale = rand.nextInt(7) + 1;
        List<String> scaleNames = Scale.scaleNameList(randomNoteName, scale, true);
        String chordNote = scaleNames.get(numInScale - 1);

        // About a quarter of questions will be non-functional.
        Integer funcOrNon = rand.nextInt(8);
        String chordType;
        if (funcOrNon <= 7) {
            chordType = ChordUtil.getDiatonicChordQuality(ChordUtil.integerToRomanNumeral(numInScale));
        } else {
            // Non Functional questions:
            Integer randomFunction = rand.nextInt(7) + 1;
            chordType = ChordUtil.getDiatonicChordQuality(ChordUtil.integerToRomanNumeral(randomFunction));
        }
        Pair<String, String> chord = new Pair(chordNote, chordType);
        Pair data = new Pair(randomNoteName, chord);
        String answer = ChordUtil.getFunctionOf(randomNoteName, chordNote, chordType);
        return new Pair(data, answer);
    }


    @Override
    HBox generateQuestionPane(Pair questionAnswer) {
        Pair data = (Pair) questionAnswer.getKey();
        String answer = (String) questionAnswer.getValue();
        final HBox questionRow = new HBox();
        Label question;
        ArrayList<String> type2answers = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            type2answers.add(ChordUtil.integerToRomanNumeral(i));
        }
        type2answers.add("Non Functional");
        if (type2answers.contains(answer)) {
            type = 2;
        } else {
            type = 1;
        }

        if (type == 1) {
            question = new Label(String.format(typeOneText, data.getKey(), data.getValue()));
        } else {
            Pair chord = (Pair) data.getValue();
            question = new Label(String.format(typeTwoText, data.getKey(), chord.getKey() + " " + chord.getValue()));
        }
        formatQuestionRow(questionRow);

        final Label correctAnswer = correctAnswer(answer);
        final ComboBox<String> options = generateChoices(data, answer);
        options.setOnAction(event ->
                handleQuestionAnswer(options.getValue().toLowerCase(), questionAnswer, questionRow)
        );

        Button skip = new Button("Skip");
        styleSkipButton(skip);

        questionRow.getChildren().add(0, question);
        questionRow.getChildren().add(1, options);
        questionRow.getChildren().add(2, skip);
        questionRow.getChildren().add(3, correctAnswer);

        questionRow.prefWidthProperty().bind(paneQuestions.prefWidthProperty());
        return questionRow;
    }

    /**
     * For type 2 questions: the answer options are the roman numerals from 1-7 and
     * 'Non-functional'. For type 1 questions: the answer options are 7 random choices that have the
     * correct letter + the correct answer.
     *
     * @return a combo box of scale options
     */
    private ComboBox<String> generateChoices(Pair functionAndData, String answer) {
        ComboBox<String> options = new ComboBox<>();
        options.setPrefHeight(30);

        if (type == 2) {
            for (int i = 1; i < 8; i++) {
                options.getItems().add(ChordUtil.integerToRomanNumeral(i));
            }
            options.getItems().add("Non Functional");
        } else {
            Integer numInScale = ChordUtil.romanNumeralToInteger((String) functionAndData.getKey());
            String noteName = (String) functionAndData.getValue();

            //Get the letter numInScale above the scale note
            char letter = ChordUtil.lettersUp(noteName, numInScale - 1);
            List<String> allOptions = new ArrayList<>();
            allOptions.add(letter + " major 7th");
            allOptions.add(letter + " minor 7th");
            allOptions.add(letter + " 7th");
            allOptions.add(letter + " half-diminished 7th");
            allOptions.add(letter + "b major 7th");
            allOptions.add(letter + "b minor 7th");
            allOptions.add(letter + "b 7th");
            allOptions.add(letter + "b half-diminished 7th");
            allOptions.add(letter + "# major 7th");
            allOptions.add(letter + "# minor 7th");
            allOptions.add(letter + "# 7th");
            allOptions.add(letter + "# half-diminished 7th");
            Collections.shuffle(allOptions);
            // We only want 8 of all the options.
            List<String> eight = allOptions.subList(0, 8);

            // Ensure the correct answer is included.
            if (!eight.contains(answer)) {
                eight.remove(rand.nextInt(7));
                eight.add(rand.nextInt(7), answer);
            }

            options.getItems().addAll(eight);
        }


        return options;
    }

    /**
     * Reacts accordingly to a user's input
     *
     * @param userAnswer  The user's selection, as text
     * @param data        A pair containing the starting note and scale type
     * @param questionRow The HBox containing GUI question data
     */
    public void handleQuestionAnswer(String userAnswer, Pair data, HBox questionRow) {
        manager.answered += 1;
        boolean correct;
        disableButtons(questionRow, 1, 2);
        String correctAnswer = (String) data.getValue();
        System.out.println(correctAnswer);
        System.out.println(userAnswer);
        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            correct = true;
            manager.add(data, 1);
            formatCorrectQuestion(questionRow);
        } else {
            correct = false;
            manager.add(data, 0);
            formatIncorrectQuestion(questionRow);
            //Shows the correct answer
            questionRow.getChildren().get(3).setVisible(true);
        }
        Pair questionPair = (Pair) data.getKey();
        String[] question;
        if (type == 1) {
            question = new String[]{
                    String.format(typeOneText,
                            questionPair.getKey(),
                            questionPair.getValue()),
                    userAnswer,
                    Boolean.toString(correct)
            };
        } else {
            Pair chord = (Pair) questionPair.getValue();
            question = new String[]{
                    String.format(typeTwoText,
                            questionPair.getKey(),
                            chord.getKey() + " " + chord.getValue()),
                    userAnswer,
                    Boolean.toString(correct)
            };
        }
        projectHandler.saveTutorRecords("diatonic", record.addQuestionAnswer(question));
        env.getRootController().setTabTitle("diatonicChordTutor", true);

        if (manager.answered == manager.questions) {
            finished();
        }

    }

    public void finished() {
        env.getPlayer().stop();
        userScore = getScore(manager.correct, manager.answered);
        outputText = String.format("You have finished the tutor.\n" +
                        "You answered %d questions, and skipped %d questions.\n" +
                        "You answered %d questions correctly, %d questions incorrectly.\n" +
                        "This gives a score of %.2f percent.",
                manager.questions, manager.skipped,
                manager.correct, manager.incorrect, userScore);
        if (projectHandler.currentProjectPath != null) {
            projectHandler.saveSessionStat("keySignature", record.setStats(manager.correct, manager.getTempIncorrectResponses().size(), userScore));
            projectHandler.saveCurrentProject();
            outputText += "\nSession auto saved.";
        }
        env.getRootController().setTabTitle("keySignatureTutor", false);
        // Sets the finished view
        resultsContent.setText(outputText);

        paneQuestions.setVisible(false);
        paneResults.setVisible(true);
        questionRows.getChildren().clear();

        Button retestBtn = new Button("Retest");
        Button clearBtn = new Button("Clear");
        Button saveBtn = new Button("Save");

        clearBtn.setOnAction(event -> {
            manager.saveTempIncorrect();
            paneResults.setVisible(false);
            paneQuestions.setVisible(true);
        });

        paneResults.setPadding(new Insets(10, 10, 10, 10));
        retestBtn.setOnAction(event -> {
            paneResults.setVisible(false);
            paneQuestions.setVisible(true);
            retest();

        });
        saveBtn.setOnAction(event -> saveRecord());

        if (manager.getTempIncorrectResponses().size() > 0) {
            //Can re-test
            buttons.getChildren().setAll(retestBtn, clearBtn, saveBtn);
        } else {
            //Perfect score
            buttons.getChildren().setAll(clearBtn, saveBtn);
        }

        buttons.setMargin(retestBtn, new Insets(10, 10, 10, 10));
        buttons.setMargin(clearBtn, new Insets(10, 10, 10, 10));
        buttons.setMargin(saveBtn, new Insets(10, 10, 10, 10));
        // Clear the current session
        manager.resetStats();
    }

    @Override
    void resetInputs() {

    }

    @FXML
    /**
     * When the go button is pressed, a new tutoring session is launched
     */
    private void goAction(ActionEvent event) {
        record = new TutorRecord();
        paneQuestions.setVisible(true);
        paneResults.setVisible(false);
        manager.resetEverything();
        manager.questions = selectedQuestions;

        rand = new Random();

        questionRows.getChildren().clear();
        for (int i = 0; i < manager.questions; i++) {
            HBox questionRow;
            if (rand.nextBoolean()) {
                type = 1;
                questionRow = generateQuestionPane(generateQuestionTypeOne());
            } else {
                type = 2;
                questionRow = generateQuestionPane(generateQuestionTypeTwo());
            }
            questionRows.getChildren().add(questionRow);
            questionRows.setMargin(questionRow, new Insets(10, 10, 10, 10));
        }

    }

}
