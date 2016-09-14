package seng302.gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import seng302.Environment;
import seng302.utility.LevelCalculator;

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

    /**
     * Initializes the user summary controller and draws its graphs
     *
     * @param env The environment in which the controller is being created
     */
    public void create(Environment env) {
        this.env = env;

        updateProgressBar();

        Pair<Integer, Integer> correctIncorrectOverall = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getTotalsForAllTutors(env.getUserPageController().getTimePeriod());

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
        classAverage.setVisible(false);


    }

    /**
     * Updates the GUI progress bar to display the user's current XP in relation to the XP required
     * to obtain the next level
     */
    public void updateProgressBar() {
        int userXp = env.getUserHandler().getCurrentUser().getUserExperience();
        int userLevel = env.getUserHandler().getCurrentUser().getUserLevel();
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


}
