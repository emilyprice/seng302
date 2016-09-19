package seng302.gui;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import seng302.data.KeySignature;
import seng302.utility.TutorRecord;

public class KeySignaturesTutorController extends TutorController {

    @FXML
    AnchorPane KeySignaturesTab;

    @FXML
    Button btnGo;

    @FXML
    ComboBox<String> scaleBox;

    @FXML
    ComboBox<String> formBox;

    @FXML
    ComboBox<String> answerBox;

    @FXML
    Label formLabel;


    /**
     * ArrayLists containing the major and minor notes thats are used to populate the answer
     * comboboxs used for question type 2
     */
    private ArrayList<String> majorSharps = new ArrayList<>(Arrays.asList("C", "G", "D", "A", "E", "B", "F#", "C#"));
    private ArrayList<String> majorFlats = new ArrayList<>(Arrays.asList("Cb", "Gb", "Db", "Ab", "Eb", "Bb", "F", "C"));
    private ArrayList<String> minorSharps = new ArrayList<>(Arrays.asList("A", "E", "B", "F#", "C#", "G#", "D#", "A#"));
    private ArrayList<String> minorFlats = new ArrayList<>(Arrays.asList("Ab", "Eb", "Bb", "F", "C", "G", "D", "A"));


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
        qPanes = new ArrayList<>();

        questionRows.getChildren().clear();
        for (int i = 0; i < manager.questions; i++) {
            setUpQuestion(i);
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
        scaleBox.getItems().addAll("Major", "Minor", "Both");
        formBox.getItems().addAll("Listing sharps/flats", "Number of sharps/flats");
        answerBox.getItems().addAll("Show Key Signature", "Show Name");

        if (currentProject.getIsCompetitiveMode()) {
            scaleBox.setDisable(true);
            scaleBox.setValue("Both");
            formBox.setDisable(true);
            formBox.setValue("Both");
            answerBox.setDisable(true);
            answerBox.setValue("Both");

        } else {

            scaleBox.getSelectionModel().selectFirst();
            formBox.getSelectionModel().selectFirst();
            answerBox.getSelectionModel().selectFirst();

        }

    }


    /**
     * Updates the form label depending on the question type
     */
    public void updateQuestionBox() {
        if (answerBox.getValue().equals("Show Key Signature")) {
            formLabel.setText("Question form:");
        } else {
            formLabel.setText("Answer form:");
        }

    }

    /**
     * Prepares a new question, gets the values from the drop down options for the questions
     */
    public void setUpQuestion(int questionNo) {
        String scaletype;
        int questionType;
        boolean answerType;

        if (currentProject.getIsCompetitiveMode()) {
            Random rand = new Random();
            scaletype = "both";
            questionType = rand.nextInt(2);
            answerType = rand.nextBoolean();
        } else {


            //figure out the scale is wanted to be tested
            if (scaleBox.getValue().equals("Major")) {
                scaletype = "major";
            } else if (scaleBox.getValue().equals("Minor")) {
                scaletype = "minor";
            } else {
                scaletype = "both";
            }

            //figure out the type of question wanted
            if (answerBox.getValue().equals("Show Key Signature")) {
                questionType = 0;
            } else {
                questionType = 1;
            }

            if (formBox.getValue().equals("Listing sharps/flats")) {
                answerType = true;

            } else {
                answerType = false;
            }
        }
        HBox questionRow = generateQuestionPane(new Pair<String, Pair>(scaletype, new Pair<>(questionType, answerType)), questionNo);


        questionRows.setMargin(questionRow, new Insets(10, 10, 10, 10));
    }


    /**
     * Creates a GUI pane for a single question
     *
     * @param pair - a pair containing the scale type and another pair that contains the question
     *             type and answer type
     * @return a HBox that contains a single question
     */
    public HBox generateQuestionPane(Pair pair, int questionNo) {

        // true if question type is 0 - show key signature
        if ((Integer) ((Pair) pair.getValue()).getKey() == 0) {
            return generateQuestionType1Pane(pair, questionNo);
        } else { // false is question type is 1 - show name
            return generateQuestionType2Pane(pair, questionNo);
        }
    }


    /**
     * Helper function for generateQuestionType1Pane that generates a question pane if the question
     * is in the form of a number of sharps or flats.
     *
     * @param pair - a pair containing the scale type and another pair that contains the question
     *             type and answer type
     * @return - returns a HBox containing the question pane
     */
    private HBox generateNumQuestionType1Pane(final Pair pair, int questionNo) {
        final HBox questionRow = new HBox();
        formatQuestionRow(questionRow);
        final ComboBox<String> options;
        final ComboBox<String> majorOptions;
        final ComboBox<String> minorOptions;

        Button skip = new Button("Skip");
        styleSkipButton(skip);
        Label correctAnswerLabel = new Label();
        final String question;


        Random rand1 = new Random();
        Random rand2 = new Random();

        int numberOfSharps = rand1.nextInt(8);
        String sharpOrFlat = "#";

        if (rand2.nextBoolean()) {
            sharpOrFlat = "b";
        }

        question = numberOfSharps + sharpOrFlat;
        TitledPane qPane = new TitledPane((questionNo + 1) + ". Which key contains " + question + "?", questionRow);
        qPane.setPadding(new Insets(2, 2, 2, 2));
        qPanes.add(qPane);

        options = generateType1ComboBox(question, (pair.getKey().equals("major")));
        options.setPrefHeight(30);

        majorOptions = generateType1ComboBox(question, true);
        minorOptions = generateType1ComboBox(question, false);

        skip.setOnAction(event -> {
            // Disables only input buttons
            formatSkippedQuestion(questionRow);
            manager.add(pair, 2);


            String correctAnswer = findCorrectAnswerNumSharpFlat(pair, question);
            if (pair.getKey().equals("both")) {
                disableButtons(questionRow, 0, 3);
            } else {
                disableButtons(questionRow, 0, 2);
            }

            String[] recordQuestion = new String[]{
                    String.format("Keys signature of %s %s", question, pair.getKey()),
                    correctAnswer,
                    "2"
            };
            record.addQuestionAnswer(recordQuestion);
            handleAccordion();
            if (manager.answered == manager.questions) {
                finished();
            }
        });


        options.setOnAction(event -> {
            disableButtons(questionRow, 0, 2);
            Integer isCorrect = 0;
            String correctAnswer = findCorrectAnswerNumSharpFlat(pair, question.toString());


            if (type1QuestionCorrectCheck(pair.getKey().toString(), question, true, options.getValue(), null)) {
                isCorrect = 1;
                formatCorrectQuestion(questionRow);
                manager.add(pair, 1);

            } else {
                correctAnswerLabel.setText(correctAnswer);
                correctAnswerLabel.setVisible(true);
                formatIncorrectQuestion(questionRow);
                manager.add(pair, 0);
            }

            // Sets up the question to be saved to the record
            String[] recordQuestion = new String[]{
                    String.format("Key signature of %s %s", question, pair.getKey()),
                    options.getValue(),
                    String.valueOf(isCorrect)
            };
            record.addQuestionAnswer(recordQuestion);
            // Shows the correct answer
            handleAccordion();
            if (manager.answered == manager.questions) {
                finished();
            }
        });

        majorOptions.setOnAction(event -> {
            disableButtons(questionRow, 0, 1);
            if (!(minorOptions.getValue() == (null))) {
                disableButtons(questionRow, 0, 3);
                disableButtons(questionRow, 0, 2);
                Integer isCorrect = 0;
                String correctAnswer = findCorrectAnswerNumSharpFlat(pair, question.toString());


                if (type1QuestionCorrectCheck(pair.getKey().toString(), question, true, majorOptions.getValue(), minorOptions.getValue())) {
                    isCorrect = 1;
                    formatCorrectQuestion(questionRow);
                    manager.add(pair, 1);

                } else {
                    correctAnswerLabel.setText(correctAnswer);
                    correctAnswerLabel.setVisible(true);
                    formatIncorrectQuestion(questionRow);
                    manager.add(pair, 0);
                }

                // Sets up the question to be saved to the record
                String[] recordQuestion = new String[]{
                        String.format("Key signature of %s %s", question, pair.getKey()),
                        options.getValue(),
                        String.valueOf(isCorrect)
                };
                record.addQuestionAnswer(recordQuestion);
                // Shows the correct answer
                handleAccordion();
                if (manager.answered == manager.questions) {
                    finished();
                }
            }


        });
        minorOptions.setOnAction(event -> {
            // This handler colors the GUI depending on the user's input
            disableButtons(questionRow, 1, 2);
            if (!(majorOptions.getValue() == null)) {
                disableButtons(questionRow, 0, 3);


                disableButtons(questionRow, 0, 2);
                Integer isCorrect = 0;
                String correctAnswer = findCorrectAnswerNumSharpFlat(pair, question.toString());


                if (type1QuestionCorrectCheck(pair.getKey().toString(), question, true, majorOptions.getValue(), minorOptions.getValue())) {
                    isCorrect = 1;
                    formatCorrectQuestion(questionRow);
                    manager.add(pair, 1);

                } else {
                    correctAnswerLabel.setText(correctAnswer);
                    correctAnswerLabel.setVisible(true);
                    formatIncorrectQuestion(questionRow);
                    manager.add(pair, 0);
                }

                // Sets up the question to be saved to the record
                String[] recordQuestion = new String[]{
                        String.format("Key signature of %s %s", question, pair.getKey()),
                        options.getValue(),
                        String.valueOf(isCorrect)
                };
                record.addQuestionAnswer(recordQuestion);
                // Shows the correct answer
                handleAccordion();
                if (manager.answered == manager.questions) {
                    finished();
                }
            }


        });

        if (isCompMode) {
            skip.setVisible(false);
            skip.setManaged(false);
        }

        if (pair.getKey().equals("both")) {
            questionRow.getChildren().add(0, majorOptions);
            questionRow.getChildren().add(1, minorOptions);
            questionRow.getChildren().add(2, skip);
            questionRow.getChildren().add(3, correctAnswerLabel);
        } else {
            questionRow.getChildren().add(0, options);
            questionRow.getChildren().add(1, skip);
            questionRow.getChildren().add(2, correctAnswerLabel);
        }

        questionRow.prefWidthProperty().bind(paneQuestions.prefWidthProperty());
        return questionRow;


    }


    /**
     * Helper function for finding the correct answer that can be displayed if a question is
     * answered incorrectly. For Type 1 questions that have the number of flats or sharps as the
     * question
     *
     * @param pair     - a pair containing the scale type and another pair that contains the
     *                 question type and answer type
     * @param question - the question that is being answered
     * @return a string representing the correct answer to the given question
     */
    private String findCorrectAnswerNumSharpFlat(Pair pair, String question) {
        Boolean isBoth = false;
        String correctAnswer = "";
        if (pair.getKey().equals("both")) {
            isBoth = true;
        }

        if ((pair.getKey().equals("major") || isBoth)) {
            if (question.contains("0")) {
                correctAnswer = "C major ";
            } else {
                for (Map.Entry<String, KeySignature> entry : KeySignature.getMajorKeySignatures().entrySet()) {

                    if (question.contains("#")) {
                        if (question.equals(entry.getValue().getNumberOfSharps() + "#")) {
                            correctAnswer = correctAnswer + entry.getKey() + " major ";
                        }
                    } else {
                        if (question.equals(entry.getValue().getNumberOfFlats() + "b")) {
                            correctAnswer = correctAnswer + entry.getKey() + " major ";
                        }
                    }
                }
            }
        }

        if ((pair.getKey().equals("minor")) || isBoth) {
            if (question.contains("0")) {
                correctAnswer += " A minor";
            } else {
                for (Map.Entry<String, KeySignature> entry : KeySignature.getMinorKeySignatures().entrySet()) {
                    if (question.contains("#")) {
                        if (question.equals(entry.getValue().getNumberOfSharps() + "#")) {
                            correctAnswer = correctAnswer + entry.getKey() + " minor";
                        }
                    } else {
                        if (question.equals(entry.getValue().getNumberOfFlats() + "b")) {
                            correctAnswer = correctAnswer + entry.getKey() + " minor";
                        }
                    }
                }
            }
        }
        return correctAnswer;
    }


    /**
     * Generates a single question pane for type 1 questions
     *
     * @param pair - a pair containing the scale type and another pair that contains the question
     *             type and answer type
     * @return - returns a HBox containing the question pane
     */
    public HBox generateQuestionType1Pane(final Pair pair, int questionNo) {


        // number of sharps and flats
        if (!(Boolean) (((Pair) pair.getValue()).getValue())) {
            return (generateNumQuestionType1Pane(pair, questionNo));
        }

        final HBox questionRow = new HBox();
        formatQuestionRow(questionRow);
        final ComboBox<String> options;
        final ComboBox<String> majorOptions;
        final ComboBox<String> minorOptions;


        Random rand = new Random();

        Button skip = new Button("Skip");
        styleSkipButton(skip);
        Label correctAnswerLabel = new Label();
        final List<String> question;


        ArrayList<KeySignature> possibleQuestions = new ArrayList<>(KeySignature.getMajorKeySignatures().values());
        possibleQuestions.addAll(KeySignature.getMinorKeySignatures().values());

        question = possibleQuestions.get(rand.nextInt(possibleQuestions.size())).getNotes();
        TitledPane qPane = new TitledPane((questionNo + 1) + ". Which key contains " + question + "?", questionRow);
        qPane.setPadding(new Insets(2, 2, 2, 2));
        qPanes.add(qPane);


        options = generateType1ComboBox(question.toString(), (pair.getKey().equals("major")));
        options.setPrefHeight(30);

        majorOptions = generateType1ComboBox(question.toString(), true);
        minorOptions = generateType1ComboBox(question.toString(), false);


        skip.setOnAction(event -> {
            // Disables only input buttons
            formatSkippedQuestion(questionRow);

            manager.add(pair, 2);


            String correctAnswer = "";

            Boolean isBoth = false;

            if (pair.getKey().equals("both")) {
                isBoth = true;
                disableButtons(questionRow, 0, 3);
            } else {
                disableButtons(questionRow, 0, 2);
            }


            if ((pair.getKey().equals("major") || isBoth)) {
                for (Map.Entry<String, KeySignature> entry : KeySignature.getMajorKeySignatures().entrySet()) {
                    if (question.equals(entry.getValue().getNotes())) {
                        correctAnswer = correctAnswer + entry.getKey() + " major ";
                    }
                }
            }
            if ((pair.getKey().equals("minor")) || isBoth) {
                for (Map.Entry<String, KeySignature> entry : KeySignature.getMinorKeySignatures().entrySet()) {
                    if (question.equals(entry.getValue().getNotes())) {
                        correctAnswer = correctAnswer + entry.getKey() + " minor";
                    }
                }
            }


            String[] recordQuestion = new String[]{
                    String.format("Key signature of %s %s", question, pair.getKey()),
                    correctAnswer,
                    "2"
            };
            record.addQuestionAnswer(recordQuestion);
            handleAccordion();
            if (manager.answered == manager.questions) {
                finished();
            }
        });


        options.setOnAction(event -> {
            disableButtons(questionRow, 0, 2);
            Integer isCorrect = 0;
            String correctAnswer = findCorrectAnswer(pair, question.toString());

            if (type1QuestionCorrectCheck(pair.getKey().toString(), question.toString(), false, options.getValue(), null)) {
                isCorrect = 1;
                formatCorrectQuestion(questionRow);
                manager.add(pair, 1);

            } else {
                correctAnswerLabel.setText(correctAnswer);
                correctAnswerLabel.setVisible(true);
                formatIncorrectQuestion(questionRow);
                manager.add(pair, 0);
            }

            // Sets up the question to be saved to the record
            String[] recordQuestion = new String[]{
                    String.format("Key signature of %s %s", question, pair.getKey()),
                    options.getValue(),
                    String.valueOf(isCorrect)
            };
            record.addQuestionAnswer(recordQuestion);
            // Shows the correct answer
            handleAccordion();
            if (manager.answered == manager.questions) {
                finished();
            }
        });

        majorOptions.setOnAction(event -> {
            disableButtons(questionRow, 1, 2);
            if (!(minorOptions.getValue() == (null))) {
                disableButtons(questionRow, 0, 3);

                disableButtons(questionRow, 0, 2);
                Integer isCorrect = 0;

                String correctAnswer = findCorrectAnswer(pair, question.toString());

                if (type1QuestionCorrectCheck(pair.getKey().toString(), question.toString(), false, majorOptions.getValue(), minorOptions.getValue())) {
                    isCorrect = 1;
                    formatCorrectQuestion(questionRow);
                    manager.add(pair, 1);

                } else {
                    correctAnswerLabel.setText(correctAnswer);
                    correctAnswerLabel.setVisible(true);
                    formatIncorrectQuestion(questionRow);
                    manager.add(pair, 0);
                }

                // Sets up the question to be saved to the record
                String[] recordQuestion = new String[]{
                        String.format("Key signature of %s %s", question, pair.getKey()),
                        options.getValue(),
                        String.valueOf(isCorrect)
                };
                record.addQuestionAnswer(recordQuestion);
                // Shows the correct answer
                handleAccordion();
                if (manager.answered == manager.questions) {
                    finished();
                }
            }


        });

        minorOptions.setOnAction(event -> {
            disableButtons(questionRow, 1, 2);
            if (!(majorOptions.getValue() == null)) {
                disableButtons(questionRow, 0, 3);
                String correctAnswer = findCorrectAnswer(pair, question.toString());

                disableButtons(questionRow, 0, 2);
                Integer isCorrect = 0;

                if (type1QuestionCorrectCheck(pair.getKey().toString(), question.toString(), false, majorOptions.getValue(), minorOptions.getValue())) {
                    isCorrect = 1;
                    formatCorrectQuestion(questionRow);
                    manager.add(pair, 1);

                } else {
                    correctAnswerLabel.setText(correctAnswer);
                    correctAnswerLabel.setVisible(true);
                    formatIncorrectQuestion(questionRow);
                    manager.add(pair, 0);
                }

                // Sets up the question to be saved to the record
                String[] recordQuestion = new String[]{
                        String.format("Key signature of %s %s", question, pair.getKey()),
                        options.getValue(),
                        String.valueOf(isCorrect)
                };
                record.addQuestionAnswer(recordQuestion);
                // Shows the correct answer
                handleAccordion();
                if (manager.answered == manager.questions) {
                    finished();
                }
            }


        });

        if (isCompMode) {
            skip.setVisible(false);
            skip.setManaged(false);
        }


        if (pair.getKey().equals("both")) {
            questionRow.getChildren().add(0, majorOptions);
            questionRow.getChildren().add(1, minorOptions);
            questionRow.getChildren().add(2, skip);
            questionRow.getChildren().add(3, correctAnswerLabel);
        } else {
            questionRow.getChildren().add(0, options);
            questionRow.getChildren().add(1, skip);
            questionRow.getChildren().add(2, correctAnswerLabel);
        }

        questionRow.prefWidthProperty().bind(paneQuestions.prefWidthProperty());
        return questionRow;

    }


    /**
     * Helper function for finding the correct answer that can be displayed if a question is
     * answered incorrectly
     *
     * @param pair     - a pair containing the scale type and another pair that contains the
     *                 question type and answer type
     * @param question - the question that is being answered
     * @return a string representing the correct answer to the given question
     */
    private String findCorrectAnswer(Pair pair, String question) {
        Boolean isBoth = false;
        String correctAnswer = "";

        if (pair.getKey().equals("both")) {
            isBoth = true;
        }

        if ((pair.getKey().equals("major") || isBoth)) {
            for (Map.Entry<String, KeySignature> entry : KeySignature.getMajorKeySignatures().entrySet()) {
                if (question.equals(entry.getValue().getNotes().toString())) {
                    correctAnswer = correctAnswer + entry.getKey() + " major ";
                }
            }
        }
        if ((pair.getKey().equals("minor")) || isBoth) {
            for (Map.Entry<String, KeySignature> entry : KeySignature.getMinorKeySignatures().entrySet()) {
                if (question.equals(entry.getValue().getNotes().toString())) {
                    correctAnswer = correctAnswer + entry.getKey() + " minor";
                }
            }
        }

        return correctAnswer;
    }


    /**
     * Generates a combobox filled with potential answers to given question for type 1 questions
     *
     * @param question - the current question that the comboBox is being generated for
     * @param isMajor  - variable representing the scale type of the current question
     * @return - returns a filled comboBox
     */
    private ComboBox<String> generateType1ComboBox(String question, Boolean isMajor) {

        ComboBox<String> options = new ComboBox<String>();
        options.setPrefHeight(30);

        ArrayList<String> optionsList = new ArrayList<String>();

        if (isMajor) {
            if (question.contains("#")) {

                for (String keySig : majorSharps) {
                    optionsList.add(keySig + " major");
                }

            } else {
                for (String keySig : majorFlats) {
                    optionsList.add(keySig + " major");
                }
            }
        } else {
            if (question.contains("#")) {

                for (String keySig : minorSharps) {
                    optionsList.add(keySig + " minor");
                }

            } else {
                for (String keySig : minorFlats) {
                    optionsList.add(keySig + " minor");
                }
            }

        }

        Collections.shuffle(optionsList);
        for (String option : optionsList) {
            options.getItems().add(option);
        }


        return options;
    }


    /**
     * Generates a single question pane for type 2 questions
     *
     * @param pair - a pair containing the scale type and another pair that contains the question
     *             type and answer type
     * @return - returns a HBox containing the question pane
     */
    public HBox generateQuestionType2Pane(final Pair pair, int questionNo) {
        Boolean isMajor = false;

        final HBox questionRow = new HBox();
        formatQuestionRow(questionRow);
        final ComboBox<String> options;
        Random rand = new Random();

        Button skip = new Button("Skip");
        styleSkipButton(skip);
        Label questionText = new Label();
        Label correctAnswerLabel = new Label();
        List<String> keysAsArray;
        final String question;

        Random bRand = new Random();

        int random = 2;
        if (pair.getKey().equals("both")) {
            random = bRand.nextInt(2);
        }

        if ((pair.getKey().equals("major")) || (random == 0)) {
            isMajor = true;
            keysAsArray = new ArrayList<>(KeySignature.getMajorKeySignatures().keySet());
            question = keysAsArray.get(rand.nextInt(keysAsArray.size()));
            options = generateMajorChoices(question, (Boolean) (((Pair) pair.getValue()).getValue()));

        } else if ((pair.getKey().equals("minor")) || (random == 1)) {
            keysAsArray = new ArrayList<>(KeySignature.getMinorKeySignatures().keySet());
            question = keysAsArray.get(rand.nextInt(keysAsArray.size()));
            options = generateMinorChoices(question, (Boolean) (((Pair) pair.getValue()).getValue()));

        } else {

            System.err.println("something is broken");
            options = null;
            question = null;
        }
        TitledPane qPane;
        if ((Boolean) ((Pair) pair.getValue()).getValue()) {
            qPane = new TitledPane((questionNo + 1) + ". What is the key signature of " + question + " " + pair.getKey() + "?", questionRow);
        } else {
            qPane = new TitledPane((questionNo + 1) + ". How many sharps or flats does " + question + " " + pair.getKey() + " contain?", questionRow);
        }
        qPane.setPadding(new Insets(2, 2, 2, 2));
        qPanes.add(qPane);
        options.setPrefHeight(30);


        final Boolean fIsMajor = isMajor;

        skip.setOnAction(event -> {
            // Disables only input buttons
            disableButtons(questionRow, 0, 2);
            formatSkippedQuestion(questionRow);

            manager.add(pair, 2);


            String correctAnswer;

            if (fIsMajor) {

                if (!(Boolean) ((Pair) pair.getValue()).getValue()) {

                    if ((KeySignature.getMajorKeySignatures().get(question)).getNotes().get(0).contains("#")) {
                        correctAnswer = (KeySignature.getMajorKeySignatures().get(question)).getNumberOfSharps() + "#";
                    } else {
                        correctAnswer = (KeySignature.getMajorKeySignatures().get(question)).getNumberOfFlats() + "b";
                    }
                } else {
                    correctAnswer = KeySignature.getMajorKeySignatures().get(question).getNotes().toString();
                }

            } else {
                if (!(Boolean) ((Pair) pair.getValue()).getValue()) {

                    if ((KeySignature.getMinorKeySignatures().get(question)).getNotes().get(0).contains("#")) {
                        correctAnswer = (KeySignature.getMinorKeySignatures().get(question)).getNumberOfSharps() + "#";
                    } else {
                        correctAnswer = (KeySignature.getMinorKeySignatures().get(question)).getNumberOfFlats() + "b";
                    }
                } else {
                    correctAnswer = KeySignature.getMinorKeySignatures().get(question).getNotes().toString();
                }
            }


            String[] recordQuestion = new String[]{
                    String.format("Keys signature of %s %s", question, pair.getKey()),
                    correctAnswer,
                    "2"
            };
            record.addQuestionAnswer(recordQuestion);
            handleAccordion();
            if (manager.answered == manager.questions) {
                finished();
            }
        });

        // This handler colors the GUI depending on the user's input
        options.setOnAction(event -> {
            disableButtons(questionRow, 0, 2);
            Integer isCorrect = 0;
            String correctAnswerStr;


            if (fIsMajor) {
                if (!(Boolean) ((Pair) pair.getValue()).getValue()) {

                    if ((KeySignature.getMajorKeySignatures().get(question)).getNotes().get(0).contains("#")) {
                        correctAnswerStr = (KeySignature.getMajorKeySignatures().get(question)).getNumberOfSharps() + "#";
                    } else {
                        correctAnswerStr = (KeySignature.getMajorKeySignatures().get(question)).getNumberOfFlats() + "b";
                    }
                } else {
                    correctAnswerStr = KeySignature.getMajorKeySignatures().get(question).getNotes().toString();
                }

            } else {
                if (!(Boolean) ((Pair) pair.getValue()).getValue()) {

                    if ((KeySignature.getMinorKeySignatures().get(question)).getNotes().get(0).contains("#")) {
                        correctAnswerStr = (KeySignature.getMinorKeySignatures().get(question)).getNumberOfSharps() + "#";
                    } else {
                        correctAnswerStr = (KeySignature.getMinorKeySignatures().get(question)).getNumberOfFlats() + "b";
                    }
                } else {
                    correctAnswerStr = KeySignature.getMinorKeySignatures().get(question).getNotes().toString();
                }
            }

            if (type2QuestionCorrectCheck(((Boolean) ((Pair) pair.getValue()).getValue()), fIsMajor, question, options.getValue())) {
                isCorrect = 1;
                formatCorrectQuestion(questionRow);
                manager.add(pair, 1);

            } else {
                correctAnswerLabel.setText(correctAnswerStr);
                correctAnswerLabel.setVisible(true);
                formatIncorrectQuestion(questionRow);
                manager.add(pair, 0);
            }

            // Sets up the question to be saved to the record
            String[] recordQuestion = new String[]{
                    String.format("Key signature of %s %s", question, pair.getKey()),
                    options.getValue(),
                    String.valueOf(isCorrect)
            };
            record.addQuestionAnswer(recordQuestion);
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

        questionRow.getChildren().add(0, options);
        questionRow.getChildren().add(1, skip);
        questionRow.getChildren().add(2, correctAnswerLabel);

        questionRow.prefWidthProperty().bind(paneQuestions.prefWidthProperty());
        return questionRow;
    }


    /**
     * Determines if a given question is correct
     *
     * @param showKeysignature - the type of question (if the answer is in the form of key
     *                         signatures or the number of key signatures)
     * @param isMajor          - if the scale in the question is major or minor
     * @param question         - the scale that is being tested
     * @param givenAnswer      - the answer in the combo box
     */
    public Boolean type2QuestionCorrectCheck(Boolean showKeysignature, Boolean isMajor, String question, String givenAnswer) {


        //if display keySignatures in answer
        if (showKeysignature) {
            if (isMajor) {
                if (givenAnswer.equals(KeySignature.getMajorKeySignatures().get(question).getNotes().toString())) {
                    return true;
                }

            } else {
                if (givenAnswer.equals(KeySignature.getMinorKeySignatures().get(question).getNotes().toString())) {
                    return true;
                }
            }
        }
        //if displaying the number of sharps/flats in answer
        else {
            if (isMajor) {
                if ((KeySignature.getMajorKeySignatures().get(question)).getNotes().get(0).contains("#")) {
                    if (givenAnswer.equals((KeySignature.getMajorKeySignatures().get(question)).getNumberOfSharps() + "#")) {
                        return true;
                    }
                } else if ((KeySignature.getMajorKeySignatures().get(question)).getNotes().get(0).contains("b")) {
                    if (givenAnswer.equals((KeySignature.getMajorKeySignatures().get(question)).getNumberOfFlats() + "b")) {
                        return true;
                    }
                } else {
                    if (givenAnswer.startsWith("0")) {
                        return true;
                    }
                }

            } else {
                if ((KeySignature.getMinorKeySignatures().get(question)).getNotes().get(0).contains("#")) {
                    if (givenAnswer.equals((KeySignature.getMinorKeySignatures().get(question)).getNumberOfSharps() + "#")) {
                        return true;
                    }
                } else if ((KeySignature.getMinorKeySignatures().get(question)).getNotes().get(0).contains("b")) {
                    if (givenAnswer.equals((KeySignature.getMinorKeySignatures().get(question)).getNumberOfFlats() + "b")) {
                        return true;
                    }
                } else {//if it is an A minor or C major
                    if (givenAnswer.startsWith("0")) {
                        return true;
                    }
                }
            }

        }
        return false;

    }


    /**
     * Is used to determine if a type 1 question is correct or incorrect
     *
     * @param scaleType           - the type of scale that the question is, eg minor, major or both
     * @param question            -  the question that is being determined if its correct or not
     * @param questionIsInNumForm - determines what type of type1 question it is, represents if the
     *                            question is in the form of number of sharps and flats
     * @param givenAnswer1        - the given answer to the question
     * @param givenAnswer2        - if the both option is selected then it will need the second
     *                            answer
     * @return - a boolean that represents if the given question is correct
     */
    public Boolean type1QuestionCorrectCheck(String scaleType, String question, Boolean questionIsInNumForm, String givenAnswer1, String givenAnswer2) {


        if (scaleType.equals("major")) {
            if (questionIsInNumForm) {
                if (question.contains("#")) {
                    return ((KeySignature.getMajorKeySignatures().get(givenAnswer1.substring(0, givenAnswer1.indexOf(" "))).getNumberOfSharps() + "#").equals(question));
                } else {
                    return ((KeySignature.getMajorKeySignatures().get(givenAnswer1.substring(0, givenAnswer1.indexOf(" "))).getNumberOfFlats() + "b").equals(question));
                }


            } else {
                return ((KeySignature.getMajorKeySignatures().get(givenAnswer1.substring(0, givenAnswer1.indexOf(" "))).getNotes().toString()).equals(question));
            }
        } else if (scaleType.equals("minor")) {

            if (questionIsInNumForm) {
                if (question.contains("#")) {
                    return ((KeySignature.getMinorKeySignatures().get(givenAnswer1.substring(0, givenAnswer1.indexOf(" "))).getNumberOfSharps() + "#").equals(question));
                } else {
                    return ((KeySignature.getMinorKeySignatures().get(givenAnswer1.substring(0, givenAnswer1.indexOf(" "))).getNumberOfFlats() + "b").equals(question));
                }


            } else {
                return ((KeySignature.getMinorKeySignatures().get(givenAnswer1.substring(0, givenAnswer1.indexOf(" "))).getNotes().toString()).equals(question));
            }

        } else {
            Boolean minorCorrect = false;
            Boolean majorCorrect;
            if (questionIsInNumForm) {
                if (questionIsInNumForm) {
                    if (question.contains("#")) {
                        majorCorrect = ((KeySignature.getMajorKeySignatures().get(givenAnswer1.substring(0, givenAnswer1.indexOf(" "))).getNumberOfSharps() + "#").equals(question));

                        if (givenAnswer2 != null) {
                            minorCorrect = ((KeySignature.getMinorKeySignatures().get(givenAnswer2.substring(0, givenAnswer2.indexOf(" "))).getNumberOfSharps() + "#").equals(question));
                        }
                    } else {
                        majorCorrect = ((KeySignature.getMajorKeySignatures().get(givenAnswer1.substring(0, givenAnswer1.indexOf(" "))).getNumberOfFlats() + "b").equals(question));

                        if (givenAnswer2 != null) {
                            minorCorrect = ((KeySignature.getMinorKeySignatures().get(givenAnswer2.substring(0, givenAnswer2.indexOf(" "))).getNumberOfFlats() + "b").equals(question));
                        }
                    }

                    if (minorCorrect && majorCorrect) {
                        return true;

                    } else {
                        return false;
                    }
                }


            } else {
                majorCorrect = ((KeySignature.getMajorKeySignatures().get(givenAnswer1.substring(0, givenAnswer1.indexOf(" "))).getNotes().toString()).equals(question));
                minorCorrect = (KeySignature.getMinorKeySignatures().get(givenAnswer2.substring(0, givenAnswer2.indexOf(" "))).getNotes().toString()).equals(question);
                if (majorCorrect && minorCorrect) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }


    /**
     * Generates the answers in the combo box when the given question is of type major
     *
     * @param scale        - the question that is being tested
     * @param keysignature - if the items in the combo box should be key signatures. if its false
     *                     then the combo box will be populated with the number of key signatures
     * @return a combo box full of potential answers
     */
    private ComboBox<String> generateMajorChoices(String scale, Boolean keysignature) {
        ComboBox<String> options = new ComboBox<>();
        options.setPrefHeight(30);

        ArrayList<List> optionsList = new ArrayList<>();
        ArrayList<String> optionsListNum = new ArrayList<>();
        if ((KeySignature.getMajorKeySignatures().get(scale)).getNotes().get(0).contains("#")) {

            for (String keySig : majorSharps) {
                if (keysignature == true) {
                    optionsList.add((KeySignature.getMajorKeySignatures().get(keySig)).getNotes());
                } else {
                    optionsListNum.add((KeySignature.getMajorKeySignatures().get(keySig)).getNumberOfSharps() + "#");
                }

            }
        } else {
            for (String keySig : majorFlats) {
                if (keysignature == true) {
                    optionsList.add((KeySignature.getMajorKeySignatures().get(keySig)).getNotes());
                } else {
                    optionsListNum.add((KeySignature.getMajorKeySignatures().get(keySig)).getNumberOfFlats() + "b");
                }
            }
        }


        if (keysignature == true) {
            Collections.shuffle(optionsList);
            for (List option : optionsList) {
                options.getItems().add(option.toString());
            }
        } else {
            Collections.shuffle(optionsListNum);
            for (String option : optionsListNum) {
                options.getItems().add(option);
            }
        }

        return options;
    }


    /**
     * Generates the answers in the combo box when the given question is of type minor
     *
     * @param scale        - the question that is being tested
     * @param keysignature - if the items in the combo box should be key signatures. if its false
     *                     then the combo box will be populated with the number of key signatures
     * @return a combo box full of potential answers
     */
    private ComboBox<String> generateMinorChoices(String scale, Boolean keysignature) {
        ComboBox<String> options = new ComboBox<>();
        options.setPrefHeight(30);

        ArrayList<List> optionsList = new ArrayList<>();
        ArrayList<String> optionsListNum = new ArrayList<>();
        if ((KeySignature.getMinorKeySignatures().get(scale)).getNotes().get(0).contains("#")) {

            for (String keySig : minorSharps) {
                if (keysignature == true) {
                    optionsList.add((KeySignature.getMinorKeySignatures().get(keySig)).getNotes());
                } else {
                    optionsListNum.add((KeySignature.getMinorKeySignatures().get(keySig)).getNumberOfSharps() + "#");
                }
            }
        } else {
            for (String keySig : minorFlats) {
                if (keysignature == true) {
                    optionsList.add((KeySignature.getMinorKeySignatures().get(keySig)).getNotes());
                } else {
                    optionsListNum.add((KeySignature.getMinorKeySignatures().get(keySig)).getNumberOfFlats() + "b");
                }
            }
        }

        if (keysignature == true) {
            Collections.shuffle(optionsList);
            for (List option : optionsList) {
                options.getItems().add(option.toString());
            }
        } else {
            Collections.shuffle(optionsListNum);
            for (String option : optionsListNum) {
                options.getItems().add(option);
            }
        }

        return options;
    }

    /**
     * Returns the option combo boxes to their default states.
     */
    public void resetInputs() {
        scaleBox.getSelectionModel().selectFirst();
        formBox.getSelectionModel().selectFirst();
        answerBox.getSelectionModel().selectFirst();
    }
}
