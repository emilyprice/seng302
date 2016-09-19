package seng302.gui;

import org.controlsfx.control.CheckComboBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import seng302.Environment;
import seng302.command.Scale;
import seng302.data.Note;
import seng302.utility.TutorRecord;
import seng302.utility.musicNotation.OctaveUtil;

public class ScaleSpellingTutorController extends TutorController {

    @FXML
    private HBox settings;


    @FXML
    private HBox settingsBox;


    @FXML
    private AnchorPane scaleSpellingAnchor;


    @FXML
    private Text scaleError;


    @FXML
    private Button btnGo;

    @FXML
    private ComboBox<String> inputMode;

    CheckComboBox<String> scaleTypes = new CheckComboBox<String>();

    private String[] validScaleNames = {"Major", "Minor", "Melodic Minor", "Blues",
            "Major Pentatonic", "Minor Pentatonic", "Harmonic Minor"};

    private ArrayList<String> selectedScaleTypes = new ArrayList<String>();

    Random rand;

    private final String typeOneText = "What are the notes of the %s scale?";

    private final String typeTwoText = "Which scale contains the notes %s";

    /**
     * Creates the basic functionality for the tutor, initialising the random variable, and the
     * selection tools for the main screen - Scale type, amount of questions and input mode
     *
     * @param env Environment for the application
     */
    public void create(Environment env) {
        super.create(env);
        rand = new Random();
        initialiseQuestionSelector();
        initialiseScaleTypeSelector();
        inputMode.getItems().addAll("Dropdown", "Keyboard");
        inputMode.getSelectionModel().selectFirst();
    }

    /**
     * Sets up the scale type CheckComboBox for selecting which scale types to be tested on.
     */
    private void initialiseScaleTypeSelector() {
        scaleTypes.setMaxWidth(100);

        for (String validChordName : validScaleNames) {
            scaleTypes.getItems().add(validChordName);
        }

        // Listener to keep track of which scale types are selected
        scaleTypes.getCheckModel().getCheckedIndices().addListener((ListChangeListener<Integer>) c -> {
            selectedScaleTypes.clear();
            selectedScaleTypes.addAll(scaleTypes.getCheckModel().getCheckedIndices().stream().map(index -> validScaleNames[index]).collect(Collectors.toList()));
        });

        //Select all scale types by default
        for (int i = 0; i < scaleTypes.getItems().size(); i++) {
            scaleTypes.getCheckModel().checkIndices(i);
        }

        //Adds to the settings, after its label
        settingsBox.getChildren().add(1, scaleTypes);
    }

    /**
     * Uses the selected scale types to generate a random scale. Creates a hash map with a start
     * note and a scale type.
     *
     * @return A hash map of start note and type of a random scale.
     */
    private Map generateRandomScale() {
        Map scaleInfo = new HashMap<String, Object>();

        scaleInfo.put("startNote", Note.getRandomNote());
        scaleInfo.put("scaleType", selectedScaleTypes.get(rand.nextInt(selectedScaleTypes.size())));
        scaleInfo.put("startNoteName", randomiseNoteName((Note) scaleInfo.get("startNote")));

        return scaleInfo;
    }

    /**
     * Creates a new question and adds it to the already generated questions.
     * Numbers the question according to the input given.
     * @param questionNumber Number of this question.
     */
    public void createNewQuestion(Integer questionNumber) {
        // Every question needs a random scale
        Map scaleInfo = generateRandomScale();

        // Randomly decides whether question will be type 1, 2, or 3
        int questionType = rand.nextInt(3) + 1;

        // Creates a pair with question type, and the scale info
        Pair question = new Pair(questionType, scaleInfo);

        // Delegates actual generation of question
        HBox questionRow = generateQuestionPane(question);
        questionRows.setMargin(questionRow, new Insets(10, 10, 10, 10));

//        TitledPane qPane = new TitledPane("Question " + questionNumber, questionRow);
        TitledPane qPane;
        if (questionType == 1) {
            qPane = new TitledPane(questionNumber + ". What are the notes of the " + (String) scaleInfo.get("startNoteName") + " " + (String) scaleInfo.get("scaleType") + " scale?", questionRow);
        } else if (questionType == 2) {
            String startNoteName = (String) scaleInfo.get("startNoteName");
            Note startNote = (Note) scaleInfo.get("startNote");
            String scaleType = (String) scaleInfo.get("scaleType");
            ArrayList<Note> correctNotes = startNote.getScale(scaleType, true);
            ArrayList<String> noteNames = Scale.scaleNameList(startNoteName, correctNotes, true, scaleType.toLowerCase());
            qPane = new TitledPane(questionNumber + ". Which scale contains the notes:  " + String.join(" ", noteNames) + "?", questionRow);
        } else {
            qPane = new TitledPane(questionNumber + ". Play the scale using the keyboard (set to tutor input).", questionRow);
        }
        qPane.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (qPane.isExpanded()) {
                    if (questionType != 3) {
                        if (((HBox) questionRow.getChildren().get(0)).getChildren().get(0) instanceof TextField) {
                            env.setCurrentFocussed((TextField) ((HBox) questionRow.getChildren().get(0)).getChildren().get(0), false, ((HBox) questionRow.getChildren().get(0)).getChildren().get(1));
                        }
                    } else {
                        if (((HBox) questionRow.getChildren().get(1)).getChildren().get(0) instanceof TextField) {
                            env.setCurrentFocussed((TextField) ((HBox) questionRow.getChildren().get(1)).getChildren().get(0), false, ((HBox) questionRow.getChildren().get(1)).getChildren().get(1));
                        }

                    }
                }
            }
        });
        qPane.setPadding(new Insets(2, 2, 2, 2));
        qPanes.add(qPane);

    }

    /**
     * Checks to see if a type one question is complete
     * @param question The question that needs to be checked
     * @return Whether or not the question is complete
     */
    private boolean isTypeOneTwoComplete(HBox question) {
        // Check if all inputs have been entered
        boolean isComplete = true;
        for (Node comboBox : ((HBox) question.lookup("#inputs")).getChildren()) {
            if (!comboBox.isDisabled()) {
                isComplete = false;
            }
        }
        return isComplete;
    }

    /**
     * Returns the correctness of the question
     * 0 - Incorrect
     * 1 - Correct
     * 2 - Partially Correct
     * @param question The question to be graded
     * @return Grade for the question
     */
    private int gradeTypeOneTwoQuestion(HBox question) {
        int correct = 0;
        int incorrect = 0;
        question.lookup("#skip").setDisable(true);

        // Calculate how many were correct and incorrect
        for (Node comboBox : ((HBox) question.lookup("#inputs")).getChildren()) {
            if (comboBox.getStyle().contains("green")) {
                correct += 1;
            } else {
                incorrect += 1;
            }
        }

        // Style accordingly
        if (incorrect == 0) {
            // Answer was correct
            formatCorrectQuestion(question);
            return 1;
        } else if (correct == 0) {
            // Answer was incorrect
            formatIncorrectQuestion(question);
            return 0;
        } else {
            // Answer was partially correct
            formatPartiallyCorrectQuestion(question);
            return 2;
        }


    }

    /**
     * Styles the question dependant on the correctness of the question
     * @param question Question to be styled
     * @param score Correctness of the question
     */
    private void styleTypeOneTwoQuestion(HBox question, int score) {
        if (score == 0) {
            formatIncorrectQuestion(question);
        } else if (score == 1) {
            formatCorrectQuestion(question);
        } else if (score == 2) {
            formatPartiallyCorrectQuestion(question);
        }


    }

    /**
     * Styles the input of a question according to correctness
     * @param input Input to be styled
     * @param answer Expected answer for the input
     */
    private void styleTypeOneTwoInput(ComboBox input, String answer) {
        input.setDisable(true);
        if (input.getValue().equals(answer)) {
            input.setStyle("-fx-background-color: green");
        } else {
            input.setStyle("-fx-background-color: red");
        }

    }

    /**
     * Handles the input of a question. Styles and checks for correctness and completeness.
     * @param input Most recent input of the question
     * @param scaleInfo Question parameter. Which scale is being tested.
     * @param questionRow Question to be styled
     * @param answer Expected answer for input
     * @param questionType Type of question
     */
    private void handleTypeOneTwoInput(ComboBox input, Map scaleInfo, HBox questionRow, String answer, int questionType) {
        styleTypeOneTwoInput(input, answer);
        if (isTypeOneTwoComplete(questionRow)) {
            gradeTypeOneTwoQuestion(questionRow);
            int score = gradeTypeOneTwoQuestion(questionRow);

            styleTypeOneTwoQuestion(questionRow, score);


            if (score == 0) {
                //show the answer
                questionRow.lookup("#answer").setVisible(true);
            }
            if (score == 2) {
                // if question was partially correct, add it to the manager as incorrect
                score = 0;
            }

            String questionText = "";
            if (questionType == 1) {
                questionText = String.format(typeOneText, (String) scaleInfo.get("startNoteName"));

            } else if (questionType == 2) {
                String startNoteName = (String) scaleInfo.get("startNoteName");
                Note startNote = (Note) scaleInfo.get("startNote");
                String scaleType = (String) scaleInfo.get("scaleType");
                ArrayList<Note> correctNotes = startNote.getScale(scaleType, true);
                ArrayList<String> noteNames = Scale.scaleNameList(startNoteName, correctNotes, true, scaleType.toLowerCase());
                questionText = String.format(typeTwoText, noteNames);
            }

            String usersAnswer = "";

            for (Node part : ((HBox) questionRow.lookup("#inputs")).getChildren()) {
                usersAnswer += ((ComboBox) part).getValue() + " ";
            }
            usersAnswer = usersAnswer.trim();

            String[] question = new String[]{
                    questionText,
                    usersAnswer,
                    Integer.toString(score)
            };

            record.addQuestionAnswer(question);
            handleAccordion();

            manager.add(new Pair(scaleInfo, 1), score);
            if (manager.answered == manager.questions) {
                finished();
            }
        }
    }

    /**
     * Deals with saving data and formatting skipped questions
     * @param scaleInfo Question parameter, expected results and scale being tested
     * @param questionType Question type being tested
     * @param questionRow Question to skip
     */
    private void handleSkippedQuestion(Map scaleInfo, int questionType, HBox questionRow) {
        manager.add(new Pair(scaleInfo, questionType), 2);
        formatSkippedQuestion(questionRow);
        disableButtons(questionRow, 0, questionRow.getChildren().size() - 1);

        String questionText = "";
        if (questionType == 1) {
            questionText = String.format(typeOneText, (String) scaleInfo.get("startNoteName"));

        } else if (questionType == 2) {
            String startNoteName = (String) scaleInfo.get("startNoteName");
            Note startNote = (Note) scaleInfo.get("startNote");
            String scaleType = (String) scaleInfo.get("scaleType");
            ArrayList<Note> correctNotes = startNote.getScale(scaleType, true);
            ArrayList<String> noteNames = Scale.scaleNameList(startNoteName, correctNotes, true, scaleType.toLowerCase());
            questionText = String.format(typeTwoText, noteNames);
        } else if (questionType == 3) {
            questionText = "Play the scale using the keyboard (set to tutor input).";
        }
        String[] questionInfo = new String[]{
                questionText,
                "",
                "2"

        };
        record.addQuestionAnswer(questionInfo);
        handleAccordion();

        if (manager.answered == manager.questions) {
            finished();
        }

    }

    /**
     * Creates a GUI HBox for type one questions. Type one questions display the scale name, and the
     * user must select all notes of the scale.
     *
     * @param scaleInfo A map containing the start note and scale type
     * @return An HBox, containing the entire question
     */
    private HBox generateQuestionTypeOne(Map scaleInfo) {
        final HBox questionRow = new HBox();
        formatQuestionRow(questionRow);

        // Set up textual representation of question
        Note startNote = (Note) scaleInfo.get("startNote");
        String startNoteName = (String) scaleInfo.get("startNoteName");

        String scaleType = (String) scaleInfo.get("scaleType");

        // Set up answer and textual representation of answer
        ArrayList<Note> correctNotes = startNote.getScale(scaleType, true);
        ArrayList<String> correctNoteNames = Scale.scaleNameList(startNoteName, correctNotes, true, scaleType.toLowerCase());
        Label correctAnswer = correctAnswer(String.join(" ", correctNoteNames));
        correctAnswer.setId("answer");

        // Creates a selection of ComboBoxes to pick notes from
        final HBox inputs = new HBox();
        inputs.setId("inputs");
        Button submit = new Button();
        submit.setOnAction(event -> {
            markKeyboardInput(questionRow, scaleInfo, inputs, correctNoteNames, 1);
        });


        for (int i = 0; i < correctNoteNames.size(); i++) {
            String note = correctNoteNames.get(i);
            if (inputMode.getValue().equals("Dropdown")) {
                ComboBox<String> noteOptions = new ComboBox<>();

                // Create a list of potential answers, include the real answer
                List<String> textualOptions = new ArrayList<>();
                textualOptions.add(note);

                // Create 7 fictional options
                for (int j = 0; j < 7; j++) {
                    // Randomly generate notes until we find one currently unused
                    String randomNote = randomiseNoteName(Note.getRandomNote());
                    while (textualOptions.contains(randomNote)) {
                        randomNote = randomiseNoteName(Note.getRandomNote());
                    }
                    textualOptions.add(randomNote);
                }

                // Randomise the order of options and add all to combobox
                Collections.shuffle(textualOptions);
                noteOptions.getItems().addAll(textualOptions);

                // Add handler to each ComboBox input
                noteOptions.setOnAction(event -> handleTypeOneTwoInput(noteOptions, scaleInfo, questionRow, note, 1));

                inputs.getChildren().add(noteOptions);
            } else {
                // Input mode = keyboard
                int index = i;
                TextField noteField = new TextField();
                noteField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                        if (newPropertyValue) {
                            if ((index + 1) < correctNoteNames.size()) {
                                env.setCurrentFocussed(noteField, false, inputs.getChildren().get(index + 1));
                            } else {
                                env.setCurrentFocussed(noteField, false, submit);
                            }
                        }
                    }
                });
                noteField.setEditable(false);

                inputs.getChildren().add(noteField);
            }
        }

        Button skip = new Button("Skip");
        skip.setId("skip");
        styleSkipButton(skip);
        skip.setOnAction(event -> handleSkippedQuestion(scaleInfo, 1, questionRow));
        if (isCompMode) {
            skip.setVisible(false);
            skip.setManaged(false);
        }


        questionRow.getChildren().add(0, inputs);
        questionRow.getChildren().add(1, skip);
        questionRow.getChildren().add(2, correctAnswer);

        questionRow.prefWidthProperty().bind(paneQuestions.prefWidthProperty());
        return questionRow;
    }

    /**
     * Generates a question of type 2. Given a series of notes, determine the scale.
     * @param scaleInfo Parameter, scale to be tested.
     * @return Returns a question row to be added to the questions
     */
    private HBox generateQuestionTypeTwo(Map scaleInfo) {
        final HBox questionRow = new HBox();
        formatQuestionRow(questionRow);

        // Get scale info
        Note startNote = (Note) scaleInfo.get("startNote");
        String scaleType = (String) scaleInfo.get("scaleType");

        String correctStartNote = randomiseNoteName(startNote);

        // Set up textual representation of question
        ArrayList<Note> correctNotes = startNote.getScale(scaleType, true);
        ArrayList<String> correctNoteNames = Scale.scaleNameList(correctStartNote, correctNotes, true, scaleType.toLowerCase());

        // Set up answer and textual representation of answer
        String answer = correctStartNote + " " + scaleType;
        Label correctAnswer = correctAnswer(answer);
        correctAnswer.setId("answer");

        // This question type only has two inputs
        final HBox inputs = new HBox();
        inputs.setId("inputs");
        ComboBox<String> noteOptions = new ComboBox<>();
        ComboBox<String> scaleTypeOptions = new ComboBox<>();

        List<String> textNoteOptions = new ArrayList<>();

        // Generate the note options
        textNoteOptions.add(correctStartNote);

        for (int i = 0; i < 7; i++) {
            String randomNote = randomiseNoteName(Note.getRandomNote());
            while (textNoteOptions.contains(randomNote)) {
                randomNote = randomiseNoteName(Note.getRandomNote());
            }
            textNoteOptions.add(randomNote);

        }
        Collections.shuffle(textNoteOptions);
        noteOptions.getItems().addAll(textNoteOptions);
        // Add handler to each ComboBox input

        noteOptions.setOnAction(event -> handleTypeOneTwoInput(noteOptions, scaleInfo, questionRow, correctStartNote, 2));

        // Generate the scale options - all the selected scale types.
        scaleTypeOptions.getItems().addAll(selectedScaleTypes);

        scaleTypeOptions.setOnAction(event -> handleTypeOneTwoInput(scaleTypeOptions, scaleInfo, questionRow, scaleType, 2));

        inputs.getChildren().add(noteOptions);
        inputs.getChildren().add(scaleTypeOptions);


        Button skip = new Button("Skip");
        skip.setId("skip");
        styleSkipButton(skip);
        skip.setOnAction(event -> handleSkippedQuestion(scaleInfo, 2, questionRow));
        if (isCompMode) {
            skip.setVisible(false);
            skip.setManaged(false);
        }


        questionRow.getChildren().add(0, inputs);
        questionRow.getChildren().add(1, skip);
        questionRow.getChildren().add(2, correctAnswer);

        questionRow.prefWidthProperty().bind(paneQuestions.prefWidthProperty());
        return questionRow;
    }

    /**
     * Generates a type 3 question playback mode
     * @param scaleInfo Scale to be tested
     * @return Returns a generated question of type 3
     */
    private HBox generateQuestionTypeThree(Map scaleInfo) {
        final HBox questionRow = new HBox();
        formatQuestionRow(questionRow);

        // Get scale info
        Note startNote = (Note) scaleInfo.get("startNote");
        String scaleType = (String) scaleInfo.get("scaleType");

        final HBox inputs = new HBox();

        Button play = new Button();
        stylePlayButton(play);
        play.setOnAction(event -> {
            if (scaleType.equalsIgnoreCase("blues")) {
                env.getPlayer().playBluesNotes(startNote.getScale(scaleType, true));
            } else {
                env.getPlayer().playNotes(startNote.getScale(scaleType, true));
            }
        });

        ArrayList<String> correctNoteNames = Scale.scaleNameList(
                OctaveUtil.removeOctaveSpecifier(startNote.getNote()),
                startNote.getScale(scaleType, true),
                true,
                scaleType.toLowerCase());

        Button skip = new Button("Skip");
        styleSkipButton(skip);
        skip.setOnAction(event -> handleSkippedQuestion(scaleInfo, 3, questionRow));
        if (isCompMode) {
            skip.setVisible(false);
            skip.setManaged(false);
        }

        Button submit = new Button();
        submit.setOnAction(event -> {
            markKeyboardInput(questionRow, scaleInfo, inputs, correctNoteNames, 3);

        });

        // Generate keyboard input fields
        for (int i = 0; i < correctNoteNames.size(); i++) {
            final int index = i;
            TextField noteField = new TextField();
            noteField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
                    if (newPropertyValue) {
                        if ((index + 1) < correctNoteNames.size()) {
                            env.setCurrentFocussed(noteField, false, inputs.getChildren().get(index + 1));
                        } else {
                            env.setCurrentFocussed(noteField, false, submit);
                        }

                    }
                }
            });
            noteField.setEditable(false);
            inputs.getChildren().add(i, noteField);
        }



        Label correctAnswer = correctAnswer(String.join(" ", correctNoteNames));
        correctAnswer.setId("answer");

        questionRow.getChildren().add(0, play);
        questionRow.getChildren().add(1, inputs);
        questionRow.getChildren().add(2, skip);
        questionRow.getChildren().add(3, correctAnswer);

        return questionRow;
    }

    /**
     * Determines what type of question is to be generated and generates the appropriate question.
     * @param data Pair containing question type and scaleMap - scale to be used.
     * @return The generated question
     */
    HBox generateQuestionPane(Pair data) {
        int questionType = (int) data.getKey();
        if (questionType == 1) {
            return generateQuestionTypeOne((Map) data.getValue());
        } else if (questionType == 2) {
            return generateQuestionTypeTwo((Map) data.getValue());
        } else {
            return generateQuestionTypeThree((Map) data.getValue());
        }
    }

    @Override
    void resetInputs() {

    }

    /**
     * Go action on the start screen, generates questions and gets tutor running.
     * @param event Click event for go button
     */
    @FXML
    void goAction(ActionEvent event) {
        if (scaleTypes.getCheckModel().getCheckedIndices().size() != 0) {
            scaleError.setVisible(false);
            record = new TutorRecord();
            paneInit.setVisible(false);
            paneQuestions.setVisible(true);

            manager.resetEverything();
            manager.questions = selectedQuestions;

            qPanes = new ArrayList<>();

            questionRows.getChildren().clear();
            for (int i = 0; i < manager.questions; i++) {
                createNewQuestion(i + 1);
            }
            qAccordion.getPanes().addAll(qPanes);
            qAccordion.setExpandedPane(qAccordion.getPanes().get(0));
            questionRows.getChildren().add(qAccordion);
        } else {
            scaleError.setVisible(true);
        }

    }


    /**
     * Used for marking answers that have been answered using keyboard input
     */
    private void markKeyboardInput(HBox questionRow, Map scaleInfo, HBox inputs, ArrayList<String> correctNoteNames, int questionNum) {
        int correct = 0;
        int incorrect = 0;
        String selectedAnswer = "";
        for (int i = 0; i < inputs.getChildren().size(); i++) {
            TextField answer = (TextField) inputs.getChildren().get(i);
            selectedAnswer += answer.getText() + " ";
            if (!noteEnharmonicComparison(Note.lookup(OctaveUtil.addDefaultOctave(answer.getText())), Note.lookup(OctaveUtil.addDefaultOctave(correctNoteNames.get(i))))) {
                answer.setStyle("-fx-background-color: green");
                correct++;
            } else {
                answer.setStyle("-fx-background-color: red");
                incorrect++;
            }
            answer.setDisable(true);
        }

        String correctness;
        if (incorrect == 0) {
            correctness = "1";
            formatCorrectQuestion(questionRow);
            manager.add(new Pair(scaleInfo, 3), 1);
        } else if (correct != 0) {
            correctness = "0";
            formatPartiallyCorrectQuestion(questionRow);
            manager.add(new Pair(scaleInfo, 3), 0);
        } else {
            correctness = "0";
            formatIncorrectQuestion(questionRow);
            manager.add(new Pair(scaleInfo, 3), 0);
        }

        String questionText = "";
        if (questionNum == 3) {
            questionText = "Play the scale using the keyboard (set to tutor input).";
        } else {
            questionText = String.format(typeOneText, (String) scaleInfo.get("startNoteName"));
        }
        String[] questionInfo = new String[]{
                questionText,
                selectedAnswer,
                correctness

        };
        record.addQuestionAnswer(questionInfo);

        handleAccordion();

        if (manager.answered == manager.questions) {
            finished();
        }
    }


    /**
     * Method used to compare the correct note and the note to be added to answer options.
     *
     * @param correctNote the right note answer
     * @param thisNote    the note to be added
     * @return comparison boolean that represents the comparison result
     */
    private boolean noteEnharmonicComparison(Note correctNote, Note thisNote) {
        char correctNoteLetter = correctNote.getNote().charAt(0);
        boolean comparison = true;

        if (thisNote.getEnharmonicWithLetter(correctNoteLetter) != null) {
            if (thisNote.getEnharmonicWithLetter(correctNoteLetter).equals(correctNote.getNote())) {
                comparison = false;
            }
        }
        return comparison;
    }

}
