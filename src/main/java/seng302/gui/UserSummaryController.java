package seng302.gui;

import com.google.firebase.database.DataSnapshot;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import seng302.Environment;
import seng302.Users.Student;
import seng302.utility.LevelCalculator;

import java.util.ArrayList;

/**
 * Controller for the GUI page which displays a user's summary information.
 */
public class UserSummaryController {

    @FXML
    private VBox levelVBox;

    @FXML
    private Label highXp;

    @FXML
    ProgressBar pbLevel;

    @FXML
    private Label overallStats;

    @FXML
    private Rectangle overallCorrect;

    @FXML
    private Rectangle overallIncorrect;

    @FXML
    private Label overallIncorrectLabel;

    @FXML
    private Label overallCorrectLabel;

    @FXML
    private Line classAverage;

    private Environment env;

    private Student user;

    FXMLLoader loader = new FXMLLoader();

    AnchorPane noteMap;

    @FXML
    private Label summaryAverageLabel;

    @FXML
    private Label summaryAverageNumber;


    @FXML
    StackPane stageMap;




    /**
     * Initializes the user summary controller and draws its graphs
     *
     * @param env The environment in which the controller is being created
     */
    public void create(Environment env) {
        this.env = env;
        this.user = env.getUserHandler().getCurrentUser();

        updateProgressBar();

        Pair<Integer, Integer> correctIncorrectOverall = user.getProjectHandler().getCurrentProject().tutorHandler.getTotalsForAllTutors(env.getUserPageController().getTimePeriod());

        // Set up Overall graph and labels.

        double overallTotal = correctIncorrectOverall.getKey() + correctIncorrectOverall.getValue();
        double overallWidthCorrect = 500 * (correctIncorrectOverall.getKey() / overallTotal);
        Timeline overallCorrectAnim = new Timeline(
                new KeyFrame(Duration.millis(800), new KeyValue(overallCorrect.widthProperty(), overallWidthCorrect, Interpolator.EASE_OUT)));
        overallCorrectAnim.play();
        overallCorrect.setWidth(overallWidthCorrect);
        overallCorrect.setFill(Color.web("00b004"));
        double overallWidthIncorrect = 500 * (correctIncorrectOverall.getValue() / overallTotal);
        Timeline overallIncorrectAnim = new Timeline(
                new KeyFrame(Duration.millis(800), new KeyValue(overallIncorrect.widthProperty(), overallWidthIncorrect, Interpolator.EASE_OUT)));
        overallIncorrectAnim.play();
        overallIncorrect.setWidth(overallWidthIncorrect);
        overallIncorrect.setFill(Color.GRAY);
        overallCorrectLabel.setText(correctIncorrectOverall.getKey() + " \ncorrect");
        overallIncorrectLabel.setText(correctIncorrectOverall.getValue() + " \nincorrect");


        displayClassAverage(env.getUserPageController().getTimePeriod());

        // TutorStatsController statsController = statsLoader.getController();

    }

    public void createStudent(Environment env, String userName, String timePeriod, String project) {

        this.env = env;

        updateProgressBarStudent(userName, project);

        Pair<Integer, Integer> correctIncorrectOverall = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getTotalsForStudent(userName, project, timePeriod);

        // Set up Overall graph and labels.

        double overallTotal = correctIncorrectOverall.getKey() + correctIncorrectOverall.getValue();
        double overallWidthCorrect = 500 * (correctIncorrectOverall.getKey() / overallTotal);
        Timeline overallCorrectAnim = new Timeline(
                new KeyFrame(Duration.millis(800), new KeyValue(overallCorrect.widthProperty(), overallWidthCorrect, Interpolator.EASE_OUT)));
        overallCorrectAnim.play();
        overallCorrect.setWidth(overallWidthCorrect);
        overallCorrect.setFill(Color.web("00b004"));
        double overallWidthIncorrect = 500 * (correctIncorrectOverall.getValue() / overallTotal);
        Timeline overallIncorrectAnim = new Timeline(
                new KeyFrame(Duration.millis(800), new KeyValue(overallIncorrect.widthProperty(), overallWidthIncorrect, Interpolator.EASE_OUT)));
        overallIncorrectAnim.play();
        overallIncorrect.setWidth(overallWidthIncorrect);
        overallIncorrect.setFill(Color.GRAY);
        overallCorrectLabel.setText(correctIncorrectOverall.getKey() + " \ncorrect");
        overallIncorrectLabel.setText(correctIncorrectOverall.getValue() + " \nincorrect");


        displayClassAverage(timePeriod);

        // TutorStatsController statsController = statsLoader.getController();

    }

    /**
     * Updates the GUI progress bar to display the user's current XP in relation to the XP required
     * to obtain the next level
     */
    public void updateProgressBar() {
        int userXp = user.getUserExperience();
        int userLevel = user.getUserLevel();
        int minXp = LevelCalculator.getRequiredExp(userLevel);
        int maxXp = LevelCalculator.getRequiredExp(userLevel + 1);

        float percentage = 100 * (userXp - minXp) / (maxXp - minXp);

        if ((percentage / 100) <= 0) {
            pbLevel.setProgress(0);
        } else {
            pbLevel.setProgress(percentage / 100);
        }

        highXp.setText(Integer.toString(maxXp - userXp) + "XP to level " + Integer.toString(userLevel + 1));
    }

    public void updateProgressBarStudent(String studentName, String studentProject) {
        DataSnapshot currentProject = env.getFirebase().getClassroomsSnapshot().child(env.getUserHandler().getClassRoom() + "/users/" + studentName + "/projects/" + studentProject);
        int userXp = Integer.parseInt(currentProject.child("experience").getValue().toString());
        int userLevel = Integer.parseInt(currentProject.child("level").getValue().toString());
        int minXp = LevelCalculator.getRequiredExp(userLevel);
        int maxXp = LevelCalculator.getRequiredExp(userLevel + 1);

        float percentage = 100 * (userXp - minXp) / (maxXp - minXp);

        if ((percentage / 100) <= 0) {
            pbLevel.setProgress(0);
        } else {
            pbLevel.setProgress(percentage / 100);
        }

        highXp.setText(Integer.toString(maxXp - userXp) + "XP to level " + Integer.toString(userLevel + 1));


    }

    /**
     * Loads the stage map into the summary page
     */
    public void loadStageMap() {
        if (env.getStageMapController() == null) {
            loader.setLocation(getClass().getResource("/Views/StageMapPane.fxml"));

            try {
                noteMap = loader.load();
                stageMap.getChildren().add(noteMap);
            } catch (Exception e) {
                System.err.println("Failed to load stage map");
                System.out.println(e.getStackTrace());
                e.printStackTrace();
            }


            StageMapController controller = loader.getController();
            env.setStageMapController(controller);
            env.setStagePane(noteMap);
            env.getStageMapController().setEnvironment(env);
            env.getStageMapController().create();

            user.getProjectHandler().getCurrentProject().loadStageMapData();

            env.getStageMapController().visualiseLockedTutors();

        } else {

            try {
                stageMap.getChildren().add(env.getStagePane());
                env.getStageMapController().visualiseLockedTutors();

            } catch (Exception e) {
                System.err.println("Failed to load stage map");
                System.out.println(e.getStackTrace());
                e.printStackTrace();
            }
        }







    }


    private void displayClassAverage(String timePeriod) {
        DataSnapshot users = env.getFirebase().getClassroomsSnapshot().child(env.getUserHandler().getClassRoom()).child("users");
        ArrayList<Pair<Integer, Integer>> data = new ArrayList<>();
        users.getChildren().forEach(user -> {
            user.child("projects").getChildren().forEach(project -> {
                Pair<Integer, Integer> totals = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getTutorHandler().getTotalsForAllTutorsInProject(project, timePeriod);
                data.add(totals);
            });
        });

        Integer classTotalIncorrect = 0;
        Integer classTotalCorrect = 0;
        for (Pair<Integer, Integer> score : data) {
            classTotalCorrect += score.getKey();
            classTotalIncorrect += score.getValue();
        }

        float averageClassScore = 0;
        if (classTotalCorrect + classTotalIncorrect != 0) {
            averageClassScore = classTotalCorrect.floatValue() / (classTotalCorrect + classTotalIncorrect);
        }
        StackPane.setMargin(classAverage, new Insets(0, 0, 0, 500 * averageClassScore - 30));
        HBox.setMargin(summaryAverageLabel, new Insets(0, 0, 0, 500 * averageClassScore - 50));
        summaryAverageNumber.setText(String.format("%.2f", averageClassScore));


    }







}
