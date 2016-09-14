package seng302.gui;

import org.controlsfx.control.CheckComboBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import seng302.Environment;
import seng302.data.Note;
import seng302.utility.TutorRecord;
import seng302.utility.musicNotation.ChordUtil;
import seng302.utility.musicNotation.OctaveUtil;

/**
 * This class is responsible for the GUI and logic of the Chord Spelling Tutor. It works similarly
 * to all other tutors, and is based off the abstract TutorController class.
 */
public class ChordSpellingTutorController extends TutorController {
    @FXML
    HBox settings;

    @FXML
    AnchorPane chordSpellingAnchor;

    @FXML
    ComboBox numEnharmonics;

    @FXML
    Button btnGo;

    @FXML
    Text chordError;

    @FXML
    CheckBox allowFalseChords;

    private Random rand;

    private final String typeOneText = "Spell the chord with the name %s";

    private final String typeTwoText = "Name the chord with the notes %s";

    private String[] validChordNames = {"major", "minor", "minor 7th",
            "major 7th", "seventh", "diminished", "half diminished 7th", "diminished 7th"};

    @FXML
    CheckComboBox<String> chordTypes;

    /**
     * What type the generated chords are, i.e. major, minor
     */
//    private ArrayList<String> validChords = new ArrayList<String>();
    ObservableList<String> validChords;

    private String enharmonicsRequired;

    /**
     * Sets up the tutoring environment
     *
     * @param env The environment the tutor is being run in
     */
    public void create(Environment env) {
        super.create(env);
        initialiseQuestionSelector();
        initialiseChordTypeSelector();

        numEnharmonics.getItems().addAll("only one", "all");
        numEnharmonics.getSelectionModel().selectFirst();
        rand = new Random();
    }

    /**
     * Set up the chord type combo check box. The listener on this ComboCheckBox keeps track of
     * which chord types are to be included in question generation.
     */
    private void initialiseChordTypeSelector() {
        chordTypes.setMaxWidth(100);

        for (String validChordName : validChordNames) {
            chordTypes.getItems().add(validChordName);
        }

        chordTypes.getCheckModel().clearChecks();
        chordTypes.getCheckModel().check(0);
        chordTypes.getCheckModel().check(1);

    }

    /**
     * A simple method for checking all available chord types in the selector.
     */
    private void selectAllChordTypes() {
        for (int i = 0; i < chordTypes.getItems().size(); i++) {
            chordTypes.getCheckModel().checkIndices(i);
        }
    }

    @FXML
    /**
     * When the go button is pressed, a new tutoring session is launched
     */
    private void goAction(ActionEvent event) {
        if (chordTypes.getCheckModel().getCheckedIndices().size() != 0) {
            record = new TutorRecord();
            paneInit.setVisible(false);
            paneQuestions.setVisible(true);
            manager.resetEverything();
            manager.questions = selectedQuestions;
            enharmonicsRequired = (String) numEnharmonics.getValue();
            qPanes = new ArrayList<>();

            this.validChords = chordTypes.getCheckModel().getCheckedItems();

            questionRows.getChildren().clear();
            for (int i = 0; i < manager.questions; i++) {
                HBox questionRow = setUpQuestion();
                TitledPane qPane = new TitledPane("Question " + (i + 1), questionRow);
                qPane.setPadding(new Insets(2, 2, 2, 2));
                qPanes.add(qPane);
                questionRows.setMargin(questionRow, new Insets(10, 10, 10, 10));
            }
            qAccordion.getPanes().addAll(qPanes);
            qAccordion.setExpandedPane(qAccordion.getPanes().get(0));
            questionRows.getChildren().add(qAccordion);
        } else {
            chordError.setVisible(true);
        }
    }

    /**
     * Prepares a new question
     *
     * @return a question pane containing the question information
     */
    public HBox setUpQuestion() {
        //Both questions just need a chord
        Pair<String, ArrayList<Note>> randomChord = generateValidChord();

        //Generates either 1 or 2: The type of question
        int questionType = rand.nextInt(2) + 1;

        Pair question = new Pair(randomChord, questionType);

        return generateQuestionPane(question);
    }

    @Override
    /**
     * Generates a GUI containing question data.
     * Needs to be broken up into parts - currently 160 lines long
     */
    HBox generateQuestionPane(Pair data) {
        final HBox questionRow = new HBox();
        formatQuestionRow(questionRow);
        Label correctAnswer = new Label();
        Label question = new Label();

        final HBox inputs = new HBox();

        int questionType = (int) data.getValue();

        if (questionType == 2) {
            //Use 'fake chords' with a ~0.25 probability for type 2 questions
            if (allowFalseChords.isSelected() && rand.nextInt(4) == 0) {
                data = new Pair(generateFalseChord(), questionType);
            }
        }

        final Pair finalData = (Pair) data.getKey();
        final String chordName = (String) finalData.getKey();
        final ArrayList<Note> chordNotes = (ArrayList<Note>) finalData.getValue();

        Button skip = new Button("Skip");
        styleSkipButton(skip);

        if (questionType == 1) {
            boolean fourNotes = false;
            //Type one questions
            String[] selectedNotes = new String[4];
            String[] correctNotes = new String[4];

            for (int i = 0; i < 3; i++) {
                correctNotes[i] = OctaveUtil.removeOctaveSpecifier(chordNotes.get(i).getNote());
            }

            try {
                correctNotes[3] = OctaveUtil.removeOctaveSpecifier(chordNotes.get(3).getNote());
                fourNotes = true;
            } catch (Exception e) {
                //there is no fourth note
                correctNotes[3] = "";
                selectedNotes[3] = "";
            }


            skip.setOnAction(event -> {
                String[] questionInfo = new String[]{
                        String.format(typeOneText, chordName),
                        chordAsString(chordNotes),
                        "2"

                };

                handleSkippedQuestion(questionInfo, questionRow, finalData, questionType);
            });


            //Type A question
            correctAnswer = correctAnswer(chordAsString(chordNotes));
            question.setText(chordName);

            ComboBox<String> note1 = new ComboBox<String>();
            note1.getItems().addAll(generateOptions(chordNotes.get(0)));
            note1.setOnAction(event -> {
                //Store the answer
                String selectedNote = note1.getValue();
                selectedNotes[0] = selectedNote;

                //Check if it's correct and style appropriately
                boolean answeredCorrectly = isNoteCorrect(correctNotes[0], selectedNote);
                styleNoteInput(note1, answeredCorrectly);

                //check entire question
                if (isQuestionCompletelyAnswered(inputs)) {
                    //Style whole question as done
                    String selection = String.join(" ", selectedNotes);
                    handleCompletedQuestion(questionRow, 1, finalData, selection);
                }
            });
            ComboBox<String> note2 = new ComboBox<String>();
            note2.getItems().addAll(generateOptions(chordNotes.get(1)));
            note2.setOnAction(event -> {
                //Store the answer
                String selectedNote = note2.getValue();
                selectedNotes[1] = selectedNote;

                //Check if it's correct and style appropriately
                boolean answeredCorrectly = isNoteCorrect(correctNotes[1], selectedNote);
                styleNoteInput(note2, answeredCorrectly);

                //check entire question
                if (isQuestionCompletelyAnswered(inputs)) {
                    //Style whole question as done
                    String selection = String.join(" ", selectedNotes);
                    handleCompletedQuestion(questionRow, 1, finalData, selection);
                }
            });
            ComboBox<String> note3 = new ComboBox<String>();
            note3.getItems().addAll(generateOptions(chordNotes.get(2)));
            note3.setOnAction(event -> {
                //Store the answer
                String selectedNote = note3.getValue();
                selectedNotes[2] = selectedNote;

                //Check if it's correct and style appropriately
                boolean answeredCorrectly = isNoteCorrect(correctNotes[2], selectedNote);
                styleNoteInput(note3, answeredCorrectly);

                //check entire question
                if (isQuestionCompletelyAnswered(inputs)) {
                    //Style whole question as done
                    String selection = String.join(" ", selectedNotes);
                    handleCompletedQuestion(questionRow, questionType, finalData, selection);
                }
            });
            ComboBox<String> note4 = new ComboBox<String>();
            if (fourNotes) {
                note4.getItems().addAll(generateOptions(chordNotes.get(3)));
                note4.setOnAction(event -> {
                    //Store the answer
                    String selectedNote = note4.getValue();
                    selectedNotes[3] = selectedNote;

                    //Check if it's correct and style appropriately
                    boolean answeredCorrectly = isNoteCorrect(correctNotes[3], selectedNote);
                    styleNoteInput(note4, answeredCorrectly);

                    //check entire question
                    if (isQuestionCompletelyAnswered(inputs)) {
                        //Style whole question as done
                        String selection = String.join(" ", selectedNotes);
                        handleCompletedQuestion(questionRow, 1, finalData, selection);
                    }
                });
            }

            inputs.getChildren().add(note1);
            inputs.getChildren().add(note2);
            inputs.getChildren().add(note3);
            if (fourNotes) {
                inputs.getChildren().add(note4);
            }


        } else {
            //Type 2 questions
            skip.setOnAction(event -> {
                String[] questionInfo = new String[]{
                        String.format(typeTwoText, chordAsString(chordNotes)),
                        chordName,
                        "2"

                };
                handleSkippedQuestion(questionInfo, questionRow, finalData, questionType);
            });

            correctAnswer = correctAnswer(chordName);
            question.setText(chordAsString(chordNotes));


            if (enharmonicsRequired.equals("all")) {
                CheckComboBox<String> possibleNames = new CheckComboBox<>();

                //Stores the enharmonics of the current chord
                ArrayList<String> allEnharmonics = new ArrayList<>();
                allEnharmonics.add(chordName);

                //Generate the list of options
                ArrayList<String> options;
                if (allEnharmonics.size() == 1) {
                    options = generateTypeTwoOptions(chordName);
                } else {
                    options = generateTypeTwoOptionsEnharmonics(allEnharmonics);
                }

                for (String chord : options) {
                    possibleNames.getItems().add(chord);
                }

                Button checkInputs = new Button("Check");
                checkInputs.setOnAction(event -> {
                    //Disallow any more inputs
                    checkInputs.setDisable(true);
                    possibleNames.setDisable(true);

                    //Check if the answer is correct
                    ObservableList<Integer> selectedItems = possibleNames.getCheckModel().getCheckedIndices();
                    ArrayList<String> selectedNames = selectedItems.stream().map(selectedIndex -> possibleNames.getCheckModel().getItem(selectedIndex)).collect(Collectors.toCollection(ArrayList::new));
                    String selection = String.join(", ", selectedNames);

                    //Checks that the user has selected all the enharmonics
                    boolean answeredCorrectly = true;
                    for (String enharmonic : allEnharmonics) {
                        if (!selectedNames.contains(enharmonic)) {
                            answeredCorrectly = false;
                        }
                    }

                    if (answeredCorrectly) {
                        checkInputs.setStyle("-fx-background-color: green");
                    } else {
                        checkInputs.setStyle("-fx-background-color: red");
                    }
                    handleCompletedQuestion(questionRow, questionType, finalData, selection);
                });

                inputs.getChildren().add(possibleNames);
                inputs.getChildren().add(checkInputs);
            } else {
                //you only need to select one correct answer
                ComboBox<String> possibleNames = new ComboBox<>();
                for (String chord : generateTypeTwoOptions(chordName)) {
                    possibleNames.getItems().add(chord);
                }

                possibleNames.setOnAction(event -> {
                    //Check if the answer is correct
                    String selection = possibleNames.getValue();
                    boolean answeredCorrectly = selection.equals(chordName);
                    styleNoteInput(possibleNames, answeredCorrectly);
                    handleCompletedQuestion(questionRow, questionType, finalData, selection);
                });
                inputs.getChildren().add(possibleNames);
            }

        }

        questionRow.getChildren().add(0, question);
        questionRow.getChildren().add(1, inputs);
        questionRow.getChildren().add(2, skip);
        questionRow.getChildren().add(3, correctAnswer);

        questionRow.prefWidthProperty().bind(paneQuestions.prefWidthProperty());
        return questionRow;
    }

    /**
     * Generates a 'false chord' - that is, a set of notes that do not correspond to a chord. Paired
     * with the name "No Chord"
     */
    private Pair generateFalseChord() {
        ArrayList<Note> randomNotes = new ArrayList<>();
        ArrayList<Integer> noteMidis = new ArrayList<>();
        boolean generatedFalseChord = false;

        while (!generatedFalseChord) {
            randomNotes.clear();
            noteMidis.clear();

            //Makes it generate either 3 or 4 notes
            int numberOfNotes = rand.nextInt(2) + 3;

            for (int i = 0; i < numberOfNotes; i++) {
                Note randomNote = Note.getRandomNote();
                randomNotes.add(randomNote);
                noteMidis.add(randomNote.getMidi());
            }

            //Check if it's a real chord
            try {
                //check this works correctly once chord util has been updated
                ChordUtil.getChordName(noteMidis, true);
            } catch (IllegalArgumentException notAChord) {
                //Only use it if it's not a valid chord
                generatedFalseChord = true;

            }
        }

        return new Pair("No Chord", randomNotes);
    }

    /**
     * Turns a chord (array list of notes) into a pretty string.
     *
     * @param chord An Array List of Notes
     * @return the provided notes in a readable format
     */
    private String chordAsString(ArrayList<Note> chord) {
        String chordAsText = "";

        for (Note note : chord) {
            chordAsText += OctaveUtil.removeOctaveSpecifier(note.getNote()) + " ";
        }
        return chordAsText;
    }

    /**
     * Randomly decides whether the chord will be major or minor. Will be extended for further chord
     * types
     *
     * @return either "major" or "minor" as a string
     */
    private String generateRandomChordType() {
        this.validChords = chordTypes.getCheckModel().getCheckedItems();
        int chordType = rand.nextInt(validChords.size());
        return validChords.get(chordType);
    }

    /**
     * Generates a "valid chord". That is, its name is valid and its notes match its name.
     *
     * @return A Pair object of Chord Name, Notes in Chord
     */
    private Pair<String, ArrayList<Note>> generateValidChord() {
        String chordType = generateRandomChordType();

        boolean validChord = false;
        String chordName = "";
        ArrayList<Note> chordNotes = null;

        while (!validChord) {
            Note startNote = Note.getRandomNote();
            if (ChordUtil.getChord(startNote, chordType) != null) {
                validChord = true;
                chordNotes = ChordUtil.getChord(startNote, chordType);
                chordName = OctaveUtil.removeOctaveSpecifier(startNote.getNote()) + " " + chordType;
            }
        }

        return new Pair<String, ArrayList<Note>>(chordName, chordNotes);
    }

    /**
     * Given a note to include, generates a set of 8 notes around it.
     *
     * @param correctNote The note that must be included
     * @return A list of 8 notes, with the required note included somewhere.
     */
    private ArrayList<String> generateOptions(Note correctNote) {
        ArrayList<String> surroundingNotes = new ArrayList<String>();
        int correctNoteMidi = correctNote.getMidi();

        //Generates a starting point in the range -7 to +7
        int randomStartingDifference = rand.nextInt(15) - 7;

        String startingNoteMidi = Integer.toString(correctNoteMidi + randomStartingDifference);

        Note startingNote = Note.lookup(startingNoteMidi);

        if (randomStartingDifference > 0) {
            //goes to above the note
            //example, if you're +7 above the note, you take that one and go down 7 semitones?
            for (int i = 0; i < 8; i++) {
                Note thisNote = startingNote.semitoneDown(i);
                if (noteEnharmonicComparison(correctNote, thisNote)) {
                    surroundingNotes.add(randomiseNoteName(thisNote));
                } else {
                    surroundingNotes.add(OctaveUtil.removeOctaveSpecifier(correctNote.getNote()));
                }
            }
        } else {
            //goes to below the note
            //so, if you're -7 below the note, you take that and go up 7 semitones
            for (int i = 0; i < 8; i++) {
                Note thisNote = startingNote.semitoneUp(i);
                if (noteEnharmonicComparison(correctNote, thisNote)) {
                    surroundingNotes.add(randomiseNoteName(thisNote));
                } else {
                    surroundingNotes.add(OctaveUtil.removeOctaveSpecifier(correctNote.getNote()));
                }
            }
        }
        return surroundingNotes;

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

    /**
     * Randomly selects 7 chord names, and creates a list of them along with the real chord name.
     *
     * @param correctAnswer The correct chord name, to be included in the list
     * @return An arraylist of chord names
     */
    private ArrayList<String> generateTypeTwoOptions(String correctAnswer) {
        ArrayList<String> chordNames = new ArrayList<String>();
        chordNames.add(correctAnswer);

        //"No Chord" is always an option, if false chords is 'turned on'.
        if (allowFalseChords.isSelected() && !chordNames.contains("No Chord")) {
            chordNames.add("No Chord");
        }

        while (chordNames.size() < 8) {
            String chordName = generateValidChord().getKey();
            if (!chordNames.contains(chordName)) {
                chordNames.add(chordName);
            }
        }
        Collections.shuffle(chordNames);
        return chordNames;
    }

    /**
     * Creates a list of 8 chord names, that includes all the enharmonic names of the 'real' chord.
     *
     * @param enharmonicNames A list of correct options
     * @return An arraylist of chord names
     */
    private ArrayList<String> generateTypeTwoOptionsEnharmonics(ArrayList<String> enharmonicNames) {
        ArrayList<String> chordNames = new ArrayList<String>();

        for (String enharmonicName : enharmonicNames) {
            chordNames.add(enharmonicName);
        }

        //"No Chord" is always an option, if false chords is 'turned on'.
        if (allowFalseChords.isSelected() && !chordNames.contains("No Chord")) {
            chordNames.add("No Chord");
        }

        while (chordNames.size() < 8) {
            String chordName = generateValidChord().getKey();
            if (!chordNames.contains(chordName)) {
                chordNames.add(chordName);
            }
        }
        Collections.shuffle(chordNames);
        return chordNames;
    }


    /**
     * Checks if the note a user has selected is the correct note. Essentially just a nice
     * comparison function.
     *
     * @param correctNote  The correct value
     * @param selectedNote The value the user has selected
     * @return True if the user selected the right note, False otherwise
     */
    private Boolean isNoteCorrect(String correctNote, String selectedNote) {
        return correctNote.equals(selectedNote);
    }

    /**
     * Once a user has selected a note (type 1 questions), it is styled. The relevant combo box is
     * styled green if the answer was correct, red if it was incorrect.
     *
     * @param note      The combo box a note was selected from
     * @param isCorrect Whether or not the correct answer was selected
     */
    private void styleNoteInput(ComboBox<String> note, boolean isCorrect) {
        //check if the note is correct, style accordingly
        String selectedNote = note.getValue();
        if (isCorrect) {
            //style green
            note.setStyle("-fx-background-color: green");
        } else {
            //style red
            note.setStyle("-fx-background-color: red");
        }
        note.setDisable(true);
    }

    /**
     * Checks that all parts of a question have been answered.
     *
     * @param inputs The HBox containing the question parts
     * @return True if all parts are answered, false otherwise.
     */
    private boolean isQuestionCompletelyAnswered(HBox inputs) {
        boolean isAnswered = true;
        for (Object input : inputs.getChildren()) {
            ComboBox<String> inputCombo = (ComboBox<String>) input;
            if (inputCombo.getValue() == null) {
                isAnswered = false;
            }
        }
        return isAnswered;
    }

    /**
     * Checks if all inputs to a type 1 question were correct.
     *
     * @param inputs The HBox containing the combo box inputs
     * @return 0 if all inputs are wrong, 1 if all are correct, and 2 if some inputs are correct
     */
    private int typeOneQuestionCorrectness(HBox inputs) {
        int correctParts = 0;
        int numberOfParts = inputs.getChildren().size();
        for (Object input : inputs.getChildren()) {
            ComboBox<String> thisInput = (ComboBox<String>) input;
            if (thisInput.getStyle().contains("green")) {
                //this part was correct
                correctParts += 1;
            }

        }
        if (correctParts == numberOfParts) {
            //All correct
            return 1;
        } else if (correctParts == 0) {
            //All incorrect
            return 0;
        } else {
            //Some correct
            return 2;
        }

    }

    /**
     * Checks whether the user correctly answered a type 2 question or not
     *
     * @param inputs The HBox containing the user's input
     * @return true if the user was correct, false otherwise
     */
    private boolean isTypeTwoQuestionCorrect(HBox inputs) {
        for (Node input : inputs.getChildren()) {
            if (input.getStyle().contains("green")) {
                return true;
            }
        }
        return false;
    }


    /**
     * This method is called when a question is completed. It styles the GUI based on the
     * correctness of the question, and also saves the question's information.
     *
     * @param completedQuestion The HBox containing the GUI of the question
     * @param questionType      Whether it was a type 1 or type 2 question
     * @param data              The question data - chord name and notes
     * @param selectedAnswer    - The answer that the user input. For saving
     */
    private void handleCompletedQuestion(HBox completedQuestion, int questionType, Pair data, String selectedAnswer) {
        HBox inputs = (HBox) completedQuestion.getChildren().get(1);
        String questionText;
        Integer answeredCorrectly = 0;

        //0 for wrong, 1 for right, 2 for partially correct
        int correctnessValue;

        if (questionType == 1) {
            //check question of type 1
            correctnessValue = typeOneQuestionCorrectness(inputs);
            questionText = String.format(typeOneText, data.getKey());
        } else {
            //check question of type 2
            if (isTypeTwoQuestionCorrect(inputs)) {
                correctnessValue = 1;
            } else {
                correctnessValue = 0;
            }
            questionText = String.format(typeTwoText, chordAsString((ArrayList<Note>) data.getValue()));
        }

        applyFormatting(completedQuestion, correctnessValue);

        if (correctnessValue == 0 || correctnessValue == 2) {
            manager.add(new Pair<Pair, Integer>(data, questionType), 0);

            //Shows the correct answer
            completedQuestion.getChildren().get(3).setVisible(true);
        } else {
            answeredCorrectly = 1;
            manager.add(new Pair<Pair, Integer>(data, questionType), 1);
        }

        //Disables skip button
        completedQuestion.getChildren().get(2).setDisable(true);

        String[] question = new String[]{
                questionText,
                selectedAnswer,
                answeredCorrectly.toString()
        };
        record.addQuestionAnswer(question);
        handleAccordion();
        if (manager.answered == manager.questions) {
            finished();
        }
    }

    /**
     * @param question    The HBox to be styled
     * @param formatStyle 0 for wrong, 1 for right, 2 for partially correct
     */
    private void applyFormatting(HBox question, int formatStyle) {
        switch (formatStyle) {
            case 0:
                formatIncorrectQuestion(question);
                break;
            case 1:
                formatCorrectQuestion(question);
                break;
            case 2:
                formatPartiallyCorrectQuestion(question);
                break;
        }

    }


    /**
     * Run when a user elects to skip a question. Saves that question to the manager as skipped, and
     * writes it to the project.
     *
     * @param questionInfo The textual representation of the skipped question
     * @param questionRow  The GUI element of the question, to be styled
     * @param finalData    The information about the question, to be stored.
     * @param questionType Whether the question was of type 1 or type 2.
     */
    private void handleSkippedQuestion(String[] questionInfo, HBox questionRow, Pair finalData, int questionType) {
        // Disables only input buttons
        disableButtons(questionRow, 1, 3);
        formatSkippedQuestion(questionRow);
        manager.add(new Pair<>(finalData, questionType), 2);

        record.addQuestionAnswer(questionInfo);

        handleAccordion();
        if (manager.answered == manager.questions) {
            finished();
        }
    }


    @Override
    /**
     * Resets the settings inputs
     */
    void resetInputs() {
        chordTypes.getCheckModel().clearChecks();
        chordTypes.getCheckModel().checkIndices(0, 1);
        numEnharmonics.getSelectionModel().selectFirst();
        allowFalseChords.setSelected(false);
    }
}
