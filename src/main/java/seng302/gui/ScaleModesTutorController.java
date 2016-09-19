package seng302.gui;


import com.jfoenix.controls.JFXSlider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import seng302.command.Modes;
import seng302.data.ModeHelper;
import seng302.data.Note;
import seng302.utility.TutorRecord;
import seng302.utility.musicNotation.OctaveUtil;

/**
 * Created by Sarah on 28/08/16. Controller class for the Scale Modes Tutor. Based off the abstract
 * TutorController class.
 */
public class ScaleModesTutorController extends TutorController {
    @FXML
    JFXSlider numQuestions;

    @FXML
    ComboBox<String> ccbModes;

    private Random rand;
    private final String typeOneText = "What is the mode of %s if it is of degree %s?";
    private final String typeTwoText = "What is the parent scale of %s %s?";
    private Integer type = 1;
    private ArrayList<String> majorNotes = new ArrayList<>(Arrays.asList("C", "G", "D", "A", "E",
            "B", "F", "Bb", "Eb", "Ab", "Db", "Gb"));


    public void create(Environment env) {
        super.create(env);
        initialiseQuestionSelector();
        rand = new Random();
        ccbModes.getItems().addAll("Major Scales", "Melodic Minor Scales", "Both");

        if (currentProject.getIsCompetitiveMode()) {
            ccbModes.setValue("Both");
            ccbModes.setDisable(true);

        } else {
            ccbModes.getSelectionModel().selectFirst();
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

        // Generate Questions based on which of the options are checked.
        ArrayList<String> possibleTypes = new ArrayList<String>();
        Random questionChooser = new Random();
        String questionType;
        if (ccbModes.getValue().equals("Both")) {
            possibleTypes.add("Major Scales");
            possibleTypes.add("Melodic Minor Scales");
        } else {
            possibleTypes.add(ccbModes.getValue());
        }
        Integer numPossTypes = possibleTypes.size();

        rand = new Random();
        qPanes = new ArrayList<>();

        questionRows.getChildren().clear();
        for (int i = 0; i < manager.questions; i++) {
            HBox questionRow;
            Pair question;
            String qString;
            if (rand.nextBoolean()) {
                type = 1;
                questionType = possibleTypes.get(questionChooser.nextInt(numPossTypes));
                question = generateQuestionTypeOne(questionType);
                Pair questionPair = (Pair) question.getKey();
                qString = String.format(typeOneText, questionPair.getKey(), questionPair.getValue());
                questionRow = generateQuestionPane(question);
            } else {
                type = 2;
                questionType = possibleTypes.get(questionChooser.nextInt(numPossTypes));
                question = generateQuestionTypeTwo(questionType);
                Pair questionPair = (Pair) question.getKey();
                qString = String.format(typeTwoText, questionPair.getKey(), questionPair.getValue());
                questionRow = generateQuestionPane(question);
            }
            TitledPane qPane = new TitledPane((i + 1) + ". " + qString, questionRow);
            qPane.setPadding(new Insets(2, 2, 2, 2));
            qPanes.add(qPane);
            questionRows.setMargin(questionRow, new Insets(10, 10, 10, 10));
        }
        qAccordion.getPanes().addAll(qPanes);
        qAccordion.setExpandedPane(qAccordion.getPanes().get(0));
        questionRows.getChildren().add(qAccordion);

    }

    /**
     * Figures out what type the questions is based on the answer. If the answer is a type 2 answer
     * then the question is type 2. Otherwise it is type 1.
     *
     * @param questionAndAnswer The pair data for the question.
     * @return an Integer indicating question type 1 or 2
     */
    private Integer getTypeOfQuestion(Pair questionAndAnswer) {
        Pair question = (Pair) questionAndAnswer.getKey();
        try {
            Integer determineTypeOfQuestion = (Integer) question.getValue();
            return 1;
        } catch (Exception e) {
            return 2;
        }
    }

    /**
     * generates the questions for the tutor for question type 1 Type 1 question: What is the mode
     * of NOTE major of degree #? Type 1 answer: the answer options are the mode note and the mode
     * type (e.g. D Dorian)
     *
     * @param question pair that contains the question and the correct answer.
     * @return options potential answers for the user to pick the correct one from
     */
    private ComboBox generateChoices(Pair question) {
        ComboBox options = new ComboBox<>();
        //stores the options that appear in the tutor. One correct answer contained
        ArrayList answers = new ArrayList();

        // Scale type - for use with determining the correct random choice for the tutor
        String scaleType = (String) ((Pair) question.getValue()).getValue();

        //splitting up the answer. e.g D and dorian
        String answer = (String) ((Pair) question.getValue()).getKey();
        answers.add(answer); //adding correct answer to answers array
        Integer degree = (Integer) ((Pair) question.getKey()).getValue();

        //splits the mode note into a separate string
        String modeNoteString = answer.split(" ")[0];
        Note modeNote = Note.lookup(OctaveUtil.addDefaultOctave(modeNoteString));
        //splits the the mode type into a separate string
        String modeTypeString = answer.split(" ")[1];

        //generate random boolean to determine semitones from root note. It will either be one semitone up from
        //the correct mode note or a semitone down. This is to prevent the user from identifying patterns to
        // narrow down the correct answer
        Boolean random = rand.nextBoolean();
        String note;
        if (random) {
            note = OctaveUtil.removeOctaveSpecifier(modeNote.semitoneUp(1).getNote());
        } else {
            note = OctaveUtil.removeOctaveSpecifier(modeNote.semitoneDown(1).getNote());
        }

        //adds the correct answer to the answer options
        answers.add(note + " " + modeTypeString);
        ArrayList<Integer> usedNum = new ArrayList<>();

        //for loop for generate random wrong answers for the options
        for (int i = 0; i < 3; i++) {
            Integer randomValueMode = rand.nextInt(7) + 1; //generates random number for accessing types of modes in ModeHelper

            //if it has already been generated, reshuffle
            while (randomValueMode.equals(degree) || usedNum.contains(randomValueMode)) {
                randomValueMode = rand.nextInt(7) + 1;
            }
            usedNum.add(randomValueMode); //adds new value to options array
            String randomScaleType = null;
            if (scaleType.equals("major")) {
                randomScaleType = ModeHelper.getMajorValueModes().get(randomValueMode);
            } else {
                randomScaleType = ModeHelper.getMelodicMinorValueModes().get(randomValueMode);
            }

            answers.add(note + " " + randomScaleType);
            answers.add((modeNoteString + " " + randomScaleType));
        }

        //shuffles the answers to be put in a random order
        Collections.shuffle(answers);
        options.getItems().addAll(answers);
        return options;
    }


    /**
     * generates the questions for the tutor for question type 2 Type 2 question: What is the parent
     * scale of MODE MODE_TYPE? Type 2 answer: NOTE major
     *
     * @param question pair that contains the question and the correct answer.
     * @return options potential answers for the user to pick the correct one from
     */
    private ComboBox generateChoices2(Pair question) {

        ComboBox options = new ComboBox<>();
        ArrayList answers = new ArrayList(); //stores the options that appear in the tutor. One correct answer contain
        answers.add((String) (((Pair) question.getValue()).getKey()));

        // Scale type for making sure the scale type is correct
        String questionScaleType = (String) (((Pair) question.getValue()).getValue());

        //adds 8 answers to answers array
        while (answers.size() < 8) {
            Integer randomIndex = rand.nextInt(12);
            String scaleType = majorNotes.get(randomIndex); //selects random scaleType based on random index

            //if it has already been generated, reshuffle
            while (answers.contains(scaleType + " " + questionScaleType)) {
                randomIndex = rand.nextInt(12);
                scaleType = majorNotes.get(randomIndex);
            }
            answers.add(scaleType + " " + questionScaleType);

        }
        Collections.shuffle(answers);
        options.getItems().addAll(answers);
        return options;

    }


    /**
     * Reacts accordingly to a user's input. Formats the question row as to whether the question was
     * answered correctly or not. Also saves the tutor record
     *
     * @param userAnswer        User's selectoin
     * @param questionAndAnswer pair that contains the question and the correct answer as strings
     * @param questionRow       HBox containing GUI data
     */
    private void handleQuestionAnswer(String userAnswer, Pair questionAndAnswer, HBox questionRow, String scaleType) {
        Integer correct;
        disableButtons(questionRow, 1, 3);
        String correctAnswer = (String) ((Pair) questionAndAnswer.getValue()).getKey();
        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            correct = 1;
            manager.add(questionAndAnswer, 1);
            formatCorrectQuestion(questionRow);
        } else {
            correct = 0;
            manager.add(questionAndAnswer, 0);
            formatIncorrectQuestion(questionRow);
            //Shows the correct answer
            questionRow.getChildren().get(2).setVisible(true);
        }


        Pair questionPair = (Pair) questionAndAnswer.getKey();
        String[] question;


        if (getTypeOfQuestion(questionAndAnswer) == 1) {
            question = new String[]{
                    String.format(typeOneText,
                            ((String) questionPair.getKey()).concat(" ".concat(scaleType)),
                            questionPair.getValue()),
                    userAnswer,
                    String.valueOf(correct)
            };
        } else {
            question = new String[]{
                    String.format(typeTwoText,
                            questionPair.getKey(),
                            questionPair.getValue()),
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


    /**
     * GUI formatting for the tutor
     *
     * @param questionAnswer pair that contains the question and the correct answer as strings
     * @return questionRow the pane that contains the question and relevant buttons and combobox for
     * answering.
     */
    HBox generateQuestionPane(Pair questionAnswer) {

        Pair data = (Pair) questionAnswer.getKey();
        String answer = (String) ((Pair) questionAnswer.getValue()).getKey();
        String scaleType = (String) ((Pair) questionAnswer.getValue()).getValue();
        final HBox questionRow = new HBox();
        final ComboBox<String> options;

        if (getTypeOfQuestion(questionAnswer) == 1) {
            options = generateChoices(questionAnswer);

        } else {
            options = generateChoices2(questionAnswer);
        }
        formatQuestionRow(questionRow);

        final Label correctAnswer = correctAnswer(answer);


        options.setOnAction(event ->
                handleQuestionAnswer(options.getValue().toLowerCase(), questionAnswer, questionRow, scaleType)
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
                questionString = String.format(typeTwoText, data.getKey(), data.getValue());
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

        if (isCompMode) {
            skip.setVisible(false);
            skip.setManaged(false);
        }

        questionRow.getChildren().add(0, options);
        questionRow.getChildren().add(1, skip);
        questionRow.getChildren().add(2, correctAnswer);

        questionRow.prefWidthProperty().bind(paneQuestions.prefWidthProperty());
        return questionRow;
    }


    /**
     * Generate the questions that asks for the mode of a given scale and a given degree
     *
     * @return a pair with the question asked and the correct answer.
     */
    private Pair generateQuestionTypeOne(String scaleType) {

        //generate random note and random degree
        Integer degree = rand.nextInt(7) + 1; //generates random degree of mode
        Integer idx = rand.nextInt(majorNotes.size()); //random index to select random tonic note
        String randomScaleName = (majorNotes.get(idx)); //gets random scale name
        String finalScaleType = null;

        Pair question = new Pair(randomScaleName, degree);

        String answer = null;
        switch (scaleType) {
            case "Major Scales":
                answer = Modes.getCorrespondingScaleString(randomScaleName, degree, "major");
                finalScaleType = "major";
                break;
            case "Melodic Minor Scales":
                answer = Modes.getCorrespondingScaleString(randomScaleName, degree, "melodic minor");
                finalScaleType = "melodic minor";
                break;

        }
        Pair answerAndType = new Pair(answer, finalScaleType);
        return new Pair(question, answerAndType);


    }


    /**
     * Generate the questions that asks for the parent scale of a given mode and its type
     *
     * @return a Pair with the question asked and the correct answer
     */
    private Pair generateQuestionTypeTwo(String scaleType) {

        //generate random degree and random note
        Integer degree = rand.nextInt(7) + 1; //generates random degree of mode
        String randomNote = "";
        String randomType = "";
        Pair question = null;
        String answer = null;
        String finalScaleType = null;
        switch (scaleType) {
            case "Major Scales":
                randomNote = (String) ModeHelper.getMajorModeNoteMap().get(degree).get(rand.nextInt(12));
                randomType = ModeHelper.getMajorValueModes().get(degree);
                question = new Pair(randomNote, randomType);
                answer = Modes.getMajorParentScaleString(randomNote, randomType);
                finalScaleType = "major";
                break;
            case "Melodic Minor Scales":
                randomNote = (String) ModeHelper.getMelodicMinorModeNoteMap().get(degree).get(rand.nextInt(12));
                randomType = ModeHelper.getMelodicMinorValueModes().get(degree);
                question = new Pair(randomNote, randomType);
                answer = Modes.getMelodicMinorParentScaleString(randomNote, randomType);
                finalScaleType = "melodic minor";
                break;

        }
        Pair answerAndType = new Pair(answer, finalScaleType);
        return new Pair(question, answerAndType);


    }

    @Override
    void resetInputs() {
        //Scale Modes tutor does not have any inputs to reset.
    }


}
