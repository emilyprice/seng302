package seng302.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import seng302.Environment;
import seng302.data.Badge;
import seng302.managers.BadgeManager;
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

    private ColorAdjust blackout;
    private ImageView lockView;
    @FXML
    private GridPane badgeGrid;
    private int gridX = 0;
    private int gridY = 0;


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
        updateBadgesDisplay();

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

    private void updateBadgesDisplay() {

        ArrayList levels = new ArrayList<Integer>();
        Badge grad = new Badge("Some Badge", "Yadda yadda", levels, 0.25, 1, "gradHat");
        Badge pitch = new Badge("Pitch Tutor", "Some ribbon thing", levels, 0.75, 0, "tuning-fork");
        Badge terms = new Badge("Terms Tutor", "Some ribbon thing", levels, 0.15, 2, "open-book");
//        ArrayList<Badge> generalBadges = new ArrayList();
        ArrayList<Badge> tutorBadges = new ArrayList();

        HashMap tutorBadgeMap = BadgeManager.getTutorBadges();
//        ArrayList<Badge> tutorBadges = new ArrayList<>(tutorBadgeMap.values());
        ArrayList<Badge> generalBadges = BadgeManager.getOverallBadges();
//        System.out.println("Badges pls: " + tutorBadgeMap);

        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(-1.0);
        this.blackout = blackout;
        Image lockImg = new Image("/images/lock.png");
        ImageView lockView = new ImageView(lockImg);
        lockView.fitHeightProperty().setValue(45);
        lockView.fitWidthProperty().setValue(45);
        this.lockView = lockView;

        for (Object tutor : tutorBadgeMap.keySet()) {
            for (Object b : (ArrayList) tutorBadgeMap.get(tutor)) {
                tutorBadges.add((Badge) b);
            }
        }

//        generalBadges.add(grad);
//        tutorBadges.add(pitch);
//        tutorBadges.add(terms);
        Collections.sort(generalBadges, new badgeComparator());
        generalBadges.forEach(this::addBadgeToGrid);
        Collections.sort(tutorBadges, new badgeComparator());
        tutorBadges.forEach(this::addTutorBadgeToGrid);
    }

    public static class badgeComparator implements Comparator<Badge> {
        @Override
        public int compare(Badge b1, Badge b2) {
            return (b1.currentBadgeType > b2.currentBadgeType) ? -1: (b1.currentBadgeType < b2.currentBadgeType) ? 1:0;
        }
    }

    public void addBadgeToGrid(Badge b) {
        Image bImage = new Image("/images/"+b.imageName+".png");
        ImageView bView = new ImageView(bImage);
        bView.fitHeightProperty().setValue(70);
        bView.fitWidthProperty().setValue(70);

        StackPane badgeStack = new StackPane(bView);
        ColorAdjust badgeEffect = new ColorAdjust();
        if (b.currentBadgeType == 0) {
            badgeEffect = this.blackout;
            badgeStack.getChildren().add(this.lockView);
        }
        bView.setEffect(badgeEffect);

        VBox badgeBox = new VBox();
        Label badgeName = new Label(b.name);
        badgeName.setFont(javafx.scene.text.Font.font(16));
        Label description = new Label(b.description);
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(b.badgeProgress);
        badgeBox.getChildren().addAll(badgeStack, badgeName, progressBar, description);
        badgeBox.setAlignment(Pos.CENTER);
        badgeBox.setSpacing(4);
        badgeGrid.add(badgeBox, gridX, gridY);
        if (gridX < 5) {
            gridX++;
        } else {
            gridX = 0;
            gridY++;
        }
    }

    public void addTutorBadgeToGrid(Badge b) {
        Image ribbonImage = new Image("/images/ribbonAward.png");
        ImageView rView = new ImageView(ribbonImage);
        rView.fitHeightProperty().setValue(70);
        rView.fitWidthProperty().setValue(70);
        Image bImage = new Image("/images/"+b.imageName+".png");
        ImageView bView = new ImageView(bImage);
        bView.fitHeightProperty().setValue(26);
        bView.fitWidthProperty().setValue(26);

        StackPane badgeStack = new StackPane(rView, bView);
        badgeStack.getChildren().get(1).setTranslateY(-13);
        badgeStack.getChildren().get(1).setTranslateX(-0.6);
        ColorAdjust badgeEffect = new ColorAdjust();
        if (b.currentBadgeType == 0) {
            badgeEffect = this.blackout;
            badgeStack.getChildren().remove(1);
            badgeStack.getChildren().add(this.lockView);
        } else if (b.currentBadgeType == 1) {
            badgeEffect.setHue(-0.88);
            badgeEffect.setSaturation(0.94);
            badgeEffect.setBrightness(-0.25);
        } else if (b.currentBadgeType == 2) {
            badgeEffect.setHue(0);
            badgeEffect.setSaturation(-1);
            badgeEffect.setBrightness(0.32);
        } else if ( b.currentBadgeType == 3) {
            badgeEffect.setHue(-0.888);
            badgeEffect.setSaturation(1);
            badgeEffect.setBrightness(0.4);
        }
        rView.setEffect(badgeEffect);

        VBox badgeBox = new VBox();
        Label badgeName = new Label(b.name);
        badgeName.setFont(javafx.scene.text.Font.font(16));
        Label description = new Label(b.description);
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(b.badgeProgress);
        badgeBox.getChildren().addAll(badgeStack, badgeName, progressBar, description);
        badgeBox.setAlignment(Pos.CENTER);
        badgeBox.setSpacing(4);
//        System.out.println(b.name + ", " + badgeStack.getChildren());
        badgeGrid.add(badgeBox, gridX, gridY);
        if (gridX < 5) {
            gridX++;
        } else {
            gridX = 0;
            gridY++;
        }    }
}
