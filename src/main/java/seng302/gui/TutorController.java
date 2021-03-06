package seng302.gui;

import com.google.firebase.database.DataSnapshot;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import seng302.Environment;
import seng302.Users.Project;
import seng302.Users.TutorHandler;
import seng302.data.Note;
import seng302.managers.TutorManager;
import seng302.utility.ExperienceCalculator;
import seng302.utility.TutorRecord;
import seng302.utility.musicNotation.OctaveUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class TutorController {

    public Environment env;

    public TutorManager manager;

    public TutorRecord record;

    public float userScore;

    public String outputText;

    public int selectedQuestions;

    public Project currentProject;

    public TutorHandler tutorHandler;

    public List qPanes;

    public boolean isCompMode;


    Stage stage;

    File fileDir;

    String path;

    @FXML
    VBox questionRows;

    @FXML
    ScrollPane paneQuestions;

    @FXML
    Accordion qAccordion;

    @FXML
    VBox paneInit;

    @FXML
    private JFXButton back;

    @FXML
    JFXSlider numQuestions;

    @FXML
    Label questions;


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
        currentProject = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject();
        tutorHandler = currentProject.getTutorHandler();
        isCompMode = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode();
    }

    @FXML
    public void backAction() {
        try {
            String tutorName = env.getRootController().getHeader();
            env.getUserPageController().showPage(tutorName);
            env.getRootController().showUserPage(); //have to go back to summary page to get list of tutors
            env.getUserPageController().listView.getSelectionModel().select(tutorName); //select tutor summary page


        } catch (Exception e) {

        }
    }

    /**
     * Implements the settings of a slider used to select number of questions.
     */
    public void initialiseQuestionSelector() {
        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode()) {
            numQuestions.setValue(10);
            numQuestions.setDisable(true);
            selectedQuestions = 10;

        } else {
            selectedQuestions = (int) numQuestions.getValue();
            numQuestions.setValue(selectedQuestions);
            questions.setText(Integer.toString(selectedQuestions));

        }
        questions.setText(Integer.toString(selectedQuestions));

        // The listener for the number of questions selected
        numQuestions.valueProperty().addListener((observable, oldValue, newValue) -> {
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

//    /**
//     * If the user chooses to re-test their self on their failed questions, this function sets up
//     * the tutoring environment for that.
//     */
//    public void retest() {
//        record = new TutorRecord();
//        ArrayList<Pair> tempIncorrectResponses = new ArrayList<>(manager.getTempIncorrectResponses());
//        manager.clearTempIncorrect();
//        Collections.shuffle(tempIncorrectResponses);
//        manager.questions = tempIncorrectResponses.size();
//        List retestPanes = new ArrayList<>();
//
//        for (Pair pair : tempIncorrectResponses) {
//            HBox questionRow = generateQuestionPane(pair);
//            TitledPane qPane = new TitledPane("Question " + (tempIncorrectResponses.indexOf(pair) + 1), questionRow);
//            qPane.setPadding(new Insets(2, 2, 2, 2));
//            retestPanes.add(qPane);
//            VBox.setMargin(questionRow, new Insets(10, 10, 10, 10));
//        }
//        qAccordion.getPanes().remove(0, qAccordion.getPanes().size());
//        qAccordion.getPanes().addAll(retestPanes);
//        questionRows.getChildren().add(qAccordion);
//    }

    /**
     * Run whenever a tutoring session ends. Saves information about that session
     */
    protected void finished() {
        env.getPlayer().stop();
        String tutorName = env.getRootController().getHeader();

        //Calculates and gives a user their experience.
        //Note: I've ignored "skipped questions" here, as you won't be able to "skip" a
        //question in competition mode.
        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode()) {
            int expGained = ExperienceCalculator.calculateExperience(manager.correct, manager.questions);
            env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().addExperience(expGained);
            env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getBadgeManager().updateTutorBadges(tutorName, manager.correct, manager.answered);
            if (manager.correct == manager.questions) {
                env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getBadgeManager().updateTutorMaster(tutorName);
            }
        }

        userScore = getScore(manager.correct, manager.answered);

        record.setStats(manager.correct, manager.getTempIncorrectResponses().size(), userScore);
        record.setFinished();
        record.updateDate();


        if (currentProject != null) {
            if (isCompMode) {
                String tutorNameNoSpaces = tutorName.replaceAll("\\s", "");
                tutorHandler.saveTutorRecordsToFile(tutorNameNoSpaces, record);
            } else {
                currentProject.addRecentPracticeTutorRecordMap(tutorName, record);
            }


        }

        questionRows.getChildren().clear();
        try {
            env.getRootController().showUserPage();
            env.getUserPageController().showPage(tutorName);
        } catch (Exception e) {
            paneQuestions.setVisible(false);
            paneInit.setVisible(true);
        }

        env.getStageMapController().fetchTutorFile(tutorName);

        // Clear the current session
        manager.resetStats();
    }


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


    /**
     * Formats a GUI question to indicate it was skipped
     *
     * @param question The HBox containing info about a question
     */
    public void formatSkippedQuestion(HBox question) {
        question.getParent().getParent().setStyle("-fx-border-color: grey; -fx-border-width: 0 0 0 10px;");
    }

    /**
     * Formats a GUI question to indicate it was answered correctly
     *
     * @param question The HBox containing info about a question
     */
    public void formatCorrectQuestion(HBox question) {
        question.getParent().getParent().setStyle("-fx-border-color: green; -fx-border-width: 0 0 0 10px;");
    }

    /**
     * Formats a GUI question to indicate it was answered incorrectly
     *
     * @param question The HBox containing info about a question
     */
    public void formatIncorrectQuestion(HBox question) {
        question.getParent().getParent().setStyle("-fx-border-color: red; -fx-border-width: 0 0 0 10px;");
    }

    /**
     * Formats a GUI question to indicate it was answered partially correctly
     *
     * @param question The HBox containing info about a question
     */
    public void formatPartiallyCorrectQuestion(HBox question) {
        question.getParent().getParent().setStyle("-fx-border-color: yellow; -fx-border-width: 0 0 0 10px;");
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


    /**
     * Called whenever a question is answered or skipped. This sets the next unanswered question to
     * be the one that is expanded.
     */
    public void handleAccordion() {
        int currentPaneIndex = qPanes.indexOf(qAccordion.getExpandedPane());

        // Start by looking at the next question
        boolean found = false;
        int i = currentPaneIndex + 1;

        while (i < qPanes.size() && !found) {
            // Go forward to the end
            TitledPane currentQuestionPane = (TitledPane) qPanes.get(i);
            if (!currentQuestionPane.getStyle().contains("-fx-border-color")) {
                // this question has not been styled, and therefore not answered
                found = true;
                qAccordion.setExpandedPane((TitledPane) qPanes.get(i));
            }
            i++;
        }


        if (!found) {
            // start again from 0 if not found
            for (int j = 0; j < qPanes.size() - 1; j++) {
                TitledPane currentQuestionPane = (TitledPane) qPanes.get(j);
                if (currentQuestionPane.getStyle().contains("-fx-border-color")) {
                    // this question has been styled, and therefore answered
                } else {
                    qAccordion.setExpandedPane((TitledPane) qPanes.get(j));
                    break;
                }
            }
        }
    }
}