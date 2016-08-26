package seng302.gui;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import seng302.Environment;
import seng302.data.Note;

import seng302.Users.ProjectHandler;
import seng302.Users.TutorHandler;
import seng302.managers.TutorManager;
import seng302.utility.TutorRecord;
import seng302.utility.musicNotation.OctaveUtil;

public abstract class TutorController {

    public Environment env;

    public TutorManager manager;

    public TutorRecord record;

    public float userScore;

    public String outputText;

    public int selectedQuestions;

    public ProjectHandler projectHandler;

    public TutorHandler tutorHandler;

    Stage stage;

    File fileDir;

    String path;

    @FXML
    VBox questionRows;

    @FXML
    ScrollPane paneQuestions;

    @FXML
    ScrollPane paneResults;

    @FXML
    Text resultsTitle;

    @FXML
    Text resultsContent;

    @FXML
    VBox resultsBox;

    @FXML
    HBox buttons;

    @FXML
    Slider numQuestions;

    @FXML
    Label questions;

    private String tabID;

    /**
     * An empty constructor, required for sub-classes.
     */
    public TutorController() {
    }

    /**
     * The method called to initialise a tutor. Sets up the environment and tutor manager
     */
    public void create(Environment env) {
        this.env = env;
        manager = new TutorManager();
        projectHandler = env.getUserHandler().getCurrentUser().getProjectHandler();
        tutorHandler = projectHandler.getCurrentProject().getTutorHandler();
    }

    /**
     * Implements the settings of a slider used to select number of questions.
     */
    public void initialiseQuestionSelector() {
        selectedQuestions = (int) numQuestions.getValue();
        questions.setText(Integer.toString(selectedQuestions));

        // The listener for the number of questions selected
        numQuestions.valueProperty().addListener((observable, newValue, oldValue) -> {
            selectedQuestions = newValue.intValue();
            questions.setText(Integer.toString(selectedQuestions));
        });

        // The question number was not being updated when just a click (rather than a drag)
        // occurred. This handles that situation.
        numQuestions.setOnMouseReleased(event -> {
            Double val = numQuestions.getValue();
            selectedQuestions = val.intValue();
            questions.setText(Integer.toString(val.intValue()));
        });
    }

    /**
     * If the user chooses to re-test their self on their failed questions, this function sets up
     * the tutoring environment for that.
     */
    public void retest() {
        record = new TutorRecord();
        ArrayList<Pair> tempIncorrectResponses = new ArrayList<>(manager.getTempIncorrectResponses());
        manager.clearTempIncorrect();
        Collections.shuffle(tempIncorrectResponses);
        manager.questions = tempIncorrectResponses.size();
        for (Pair pair : tempIncorrectResponses) {
            HBox questionRow = generateQuestionPane(pair);
            questionRows.getChildren().add(questionRow);
            VBox.setMargin(questionRow, new Insets(10, 10, 10, 10));
        }
    }

    protected void finished(String tutorType) {
        env.getPlayer().stop();
        userScore = getScore(manager.correct, manager.answered);
        System.out.println(userScore);
        System.out.println(env.getUserHandler().getCurrentUser().getBadgeManager());
        System.out.println(Math.round(userScore)/10);
        env.getUserHandler().getCurrentUser().getBadgeManager().updateAllBadges(tutorType,Math.round(userScore)/10);
        outputText = String.format("You have finished the tutor.\n" +
                        "You answered %d questions, and skipped %d questions.\n" +
                        "You answered %d questions correctly, %d questions incorrectly.\n" +
                        "This gives a score of %.2f percent.",
                manager.questions, manager.skipped,
                manager.correct, manager.incorrect, userScore);

        record.setStats(manager.correct, manager.getTempIncorrectResponses().size(), userScore);
        record.setFinished();
        record.setDate();
        if (projectHandler.getCurrentProject() != null) {
            record.setStats(manager.correct, manager.getTempIncorrectResponses().size(), userScore);
            projectHandler.getCurrentProject().saveCurrentProject();
            outputText += "\nSession auto saved.";
        }
        env.getRootController().setTabTitle(tabID, false);

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

    /**
     * An empty function which is overridden by each tutor
     */
    abstract HBox generateQuestionPane(Pair data);

    /**
     * A function for disabling a selection of buttons. For example, disable all inputs but not the
     * play button.
     *
     * @param questionRow the HBox containing children to be disabled
     * @param firstChild  the index of the first object to disable
     * @param lastChild   the index at which to stop disabling items
     */
    public void disableButtons(HBox questionRow, int firstChild, int lastChild) {
        for (int i = firstChild; i < lastChild; i++) {
            questionRow.getChildren().get(i).setDisable(true);
        }
    }

    public TutorManager getManager() {
        return manager;
    }

    /**
     * Saves a record of the tutoring session to a file.
     */
    public void saveRecord() {

        //show a file picker
        FileChooser fileChooser = new FileChooser();
        if (projectHandler.getCurrentProject().isProject()) {
            env.getRootController().checkProjectDirectory();
            fileChooser.setInitialDirectory(Paths.get(projectHandler.getCurrentProject().getCurrentProjectPath()).toFile());
        }
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            fileDir = file.getParentFile();
            path = file.getAbsolutePath();
            env.setRecordLocation(path);
            projectHandler.getCurrentProject().getTutorHandler().saveTutorRecordsToFile(path, record);
        }
    }

    /**
     * Calculates a user's score after a tutoring session
     *
     * @param correct  The number of questions the user answered correctly
     * @param answered The number of questions the user answered, correctly or incorrectly
     * @return the user's score as a percentage value
     */
    public float getScore(int correct, int answered) {
        float score = 0;
        if (answered > 0) {
            score = (float) correct / (float) answered * 100;
        }
        return score;

    }


    /**
     * Styles a question row consistently
     *
     * @param questionRow the row being styled
     */
    public void formatQuestionRow(HBox questionRow) {
        questionRow.setPadding(new Insets(10, 10, 10, 10));
        questionRow.setSpacing(10);
        questionRow.setStyle("-fx-border-color: #336699; -fx-border-width: 2px;");
    }

    /**
     * Using random numbers, "randomises" whether a note will display with a sharp or flat. Only
     * uses sharps/flats when applicable.
     *
     * @param noteToRandomise A Note which is being "randomised"
     * @return A 'randomised' string representation of a note.
     */
    protected String randomiseNoteName(Note noteToRandomise) {
        Random rand = new Random();
        String noteName = OctaveUtil.removeOctaveSpecifier(noteToRandomise.getNote());

        //As the default is sharp, we randomise to get some flats
        if (rand.nextInt(2) != 0) {
            if (!noteToRandomise.simpleEnharmonic().equals("")) {
                noteName = OctaveUtil.removeOctaveSpecifier(noteToRandomise.simpleEnharmonic());
            }
        }
        return noteName;
    }


//    /**
//     * Creates an alert to ask the user whether or not to save a record to file.
//     */
//    public void promptSaveRecord() {
//        Alert savePrompt = new Alert(Alert.AlertType.NONE);
//        savePrompt.setContentText("Would you like to save this tutoring session?");
//        savePrompt.setHeaderText("Save Record?");
//        ButtonType save = new ButtonType("Save");
//        ButtonType cancel = new ButtonType("Discard");
//        savePrompt.getButtonTypes().setAll(save, cancel);
//        ButtonType result = savePrompt.showAndWait().get();
//
//        if (result.equals(save)) {
//            saveRecord();
//        }
//    }

    /**
     * Formats a GUI question to indicate it was skipped
     *
     * @param question The HBox containing info about a question
     */
    public void formatSkippedQuestion(HBox question) {
        question.setStyle("-fx-border-color: grey; -fx-border-width: 2px;");
    }

    /**
     * Formats a GUI question to indicate it was answered correctly
     *
     * @param question The HBox containing info about a question
     */
    public void formatCorrectQuestion(HBox question) {
        question.setStyle("-fx-border-color: green; -fx-border-width: 2px;");
    }

    /**
     * Formats a GUI question to indicate it was answered incorrectly
     *
     * @param question The HBox containing info about a question
     */
    public void formatIncorrectQuestion(HBox question) {
        question.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
    }

    /**
     * Formats a GUI question to indicate it was answered partially correctly
     *
     * @param question The HBox containing info about a question
     */
    public void formatPartiallyCorrectQuestion(HBox question) {
        question.setStyle("-fx-border-color: yellow; -fx-border-width: 2px;");
    }

    /**
     * Returns a new, hidden label containing the correct answer to a question.
     *
     * @param answerToShow The correct answer to the question
     * @return A new hidden label
     */
    public Label correctAnswer(String answerToShow) {
        Label correctAnswerLabel = new Label(answerToShow);
        correctAnswerLabel.setVisible(false);
        return correctAnswerLabel;

    }

    /**
     * Consistently styles all play buttons
     *
     * @param play the button to be styled
     */
    public void stylePlayButton(Button play) {
        Image imagePlay = new Image(getClass().getResourceAsStream("/images/play-icon.png"), 20, 20, true, true);
        play.setGraphic(new ImageView(imagePlay));
    }

    /**
     * Consistently styles all skip buttons
     *
     * @param skip the button to be styled
     */
    public void styleSkipButton(Button skip) {
        Image imageSkip = new Image(getClass().getResourceAsStream("/images/right-arrow.png"), 20, 20, true, true);
        skip.setGraphic(new ImageView(imageSkip));
    }

    /**
     * Consistently styles all skip toggle buttons
     *
     * @param skip the toggle button to be styled
     */
    public void styleSkipToggleButton(ToggleButton skip) {
        Image imageSkip = new Image(getClass().getResourceAsStream("/images/right-arrow.png"), 20, 20, true, true);
        skip.setGraphic(new ImageView(imageSkip));
    }

    abstract void resetInputs();

    public void clearTutor() {
        numQuestions.setValue(5);
        questionRows.getChildren().clear();
        manager.resetEverything();
        resetInputs();
    }

    public String getTabID() {
        return tabID;
    }

    public void setTabID(String tabID) {
        this.tabID = tabID;
    }
}