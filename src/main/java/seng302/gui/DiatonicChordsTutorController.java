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
import javafx.scene.control.TitledPane;
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
    private Integer type = 1;
    private ArrayList<String> type2answers;


    public void create(Environment env) {
        super.create(env);
        initialiseQuestionSelector();
        rand = new Random();
        type2answers = new ArrayList<>();
        for (int i = 1; i < 8; i++) {
            type2answers.add(ChordUtil.integerToRomanNumeral(i));
        }
        type2answers.add("Non Functional");
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
        List<String> scaleNames = Scale.scaleNameList(randomNoteName, scale, true, "");
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
        if (getTypeOfQuestion(questionAnswer) == 1) {
            question = new Label(String.format(typeOneText, data.getKey(), data.getValue()));
        } else {
            Pair chord = (Pair) data.getValue();
            question = new Label(String.format(typeTwoText, data.getKey(), chord.getKey() + " " + chord.getValue()));
        }
        formatQuestionRow(questionRow);

        final Label correctAnswer = correctAnswer(answer);
        final ComboBox<String> options = generateChoices(questionAnswer, answer);
        options.setOnAction(event ->
                handleQuestionAnswer(options.getValue().toLowerCase(), questionAnswer, questionRow)
        );

        Button skip = new Button("Skip");
        styleSkipButton(skip);

        skip.setOnAction(event -> {
            // Disables only input buttons
            disableButtons(questionRow, 1, 3);
            formatSkippedQuestion(questionRow);
            manager.add(data, 2);
            String questionString;
            if (getTypeOfQuestion(questionAnswer) == 1) {
                questionString = String.format(typeOneText, data.getKey(), data.getValue());
            } else {
                Pair chord = (Pair) data.getValue();
                questionString = String.format(typeTwoText, data.getKey(), chord.getKey() + " " + chord.getValue());
            }
            String[] questionList = new String[]{
                    questionString,
                    answer,
                    "2"
            };
            record.addQuestionAnswer(questionList);
            handleAccordion();
            if (manager.answered == manager.questions) {
                finished();
            }
        });

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
    private ComboBox<String> generateChoices(Pair questionAnswer, String answer) {
        Pair question = (Pair) questionAnswer.getKey();
        ComboBox<String> options = new ComboBox<>();
        options.setPrefHeight(30);

        if (getTypeOfQuestion(questionAnswer) == 2) {
            options.getItems().addAll(type2answers);
        } else {
            Integer numInScale = ChordUtil.romanNumeralToInteger((String) question.getKey());
            String noteName = (String) question.getValue();

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
     * Reacts accordingly to a user's input. Formats the question row as to whether the question was
     * answered correctly or not. Also saves the tutor record.
     *
     * @param userAnswer        The user's selection, as text
     * @param questionAndAnswer A pair containing the starting note and scale type
     * @param questionRow       The HBox containing GUI question data
     */
    public void handleQuestionAnswer(String userAnswer, Pair questionAndAnswer, HBox questionRow) {
        Integer correct;
        disableButtons(questionRow, 1, 2);
        String correctAnswer = (String) questionAndAnswer.getValue();
        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            correct = 1;
            manager.add(questionAndAnswer, 1);
            formatCorrectQuestion(questionRow);
        } else {
            correct = 0;
            manager.add(questionAndAnswer, 0);
            formatIncorrectQuestion(questionRow);
            //Shows the correct answer
            questionRow.getChildren().get(3).setVisible(true);
        }
        Pair questionPair = (Pair) questionAndAnswer.getKey();
        String[] question;
        if (getTypeOfQuestion(questionAndAnswer) == 1) {
            question = new String[]{
                    String.format(typeOneText,
                            questionPair.getKey(),
                            questionPair.getValue()),
                    userAnswer,
                    String.valueOf(correct)
            };
        } else {
            Pair chord = (Pair) questionPair.getValue();
            question = new String[]{
                    String.format(typeTwoText,
                            questionPair.getKey(),
                            chord.getKey() + " " + chord.getValue()),
                    userAnswer,
                    String.valueOf(correct)
            };
        }
        record.addQuestionAnswer(question);

        handleAccordion();
        if (manager.answered == manager.questions) {
            finished();
        }

    }


    @Override
    void resetInputs() {
        //Diatonic tutor does not have any inputs to reset.
    }

    /**
     * Figures out what type the questions is based on the answer. If the answer is a type 2 answer
     * then the question is type 2. Otherwise it is type 1.
     *
     * @param questionAndAnswer The pair data for the question.
     * @return an Integer indicating question type 1 or 2
     */
    private Integer getTypeOfQuestion(Pair questionAndAnswer) {
        String answer = (String) questionAndAnswer.getValue();
        if (type2answers.contains(answer)) {
            return 2;
        } else {
            return 1;
        }
    }

    @FXML
    /**
     * When the go button is pressed, a new tutoring session is launched
     */
    private void goAction(ActionEvent event) {
        record = new TutorRecord();
        paneInit.setVisible(false);
        paneQuestions.setVisible(true);
        manager.resetEverything();
        manager.questions = selectedQuestions;
        rand = new Random();
        qPanes = new ArrayList<>();

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
            TitledPane qPane = new TitledPane("Question " + (i + 1), questionRow);
            qPane.setPadding(new Insets(2, 2, 2, 2));
            qPanes.add(qPane);
            questionRows.setMargin(questionRow, new Insets(10, 10, 10, 10));
        }
        qAccordion.getPanes().addAll(qPanes);
        qAccordion.setExpandedPane(qAccordion.getPanes().get(0));
        questionRows.getChildren().add(qAccordion);

    }

}
