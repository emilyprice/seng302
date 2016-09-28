package seng302.gui;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.LoadException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import seng302.Environment;
import seng302.Users.Student;
import seng302.Users.TutorHandler;
import seng302.data.Badge;
import seng302.managers.BadgeManager;
import seng302.utility.ImageCache;
import seng302.utility.LevelCalculator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

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
    private VBox badgesContainer;

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
    private VBox badges;


    @FXML
    StackPane stageMap;


    private ColorAdjust blackout;
    @FXML
    private GridPane badgeGrid;
    private int gridX = 0;
    private int gridY = 0;

    public static final ImageCache imageCache = new ImageCache();

    /**
     * Initializes the user summary controller and draws its graphs
     *
     * @param env The environment in which the controller is being created
     */
    public void create(Environment env) {
        this.env = env;
        this.user = env.getUserHandler().getCurrentUser();

        updateProgressBar();
        updateGraphs();

    }

    public void updateGraphs() {
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
        overallIncorrect.setFill(Color.LIGHTGRAY);
        overallIncorrect.setStyle("-fx-border-radius: 10 10 0 0;\n" +
                "  -fx-background-radius: 10 10 0 0;");
        overallCorrectLabel.setText(correctIncorrectOverall.getKey() + " \ncorrect");
        overallIncorrectLabel.setText(correctIncorrectOverall.getValue() + " \nincorrect");

        updateBadgesDisplay();

        displayClassAverage(env.getUserPageController().getTimePeriod());

        // TutorStatsController statsController = statsLoader.getController();

    }

    public void createStudent(Environment env, String userName, String timePeriod, String project) {

        this.env = env;

        updateProgressBarStudent(userName, project);

        Pair<Integer, Integer> correctIncorrectOverall = new TutorHandler(env).getTotalsForStudent(userName, project, timePeriod);

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
        loader.setLocation(getClass().getResource("/Views/StageMapPane.fxml"));

        try {
            noteMap = loader.load();
            stageMap.getChildren().clear();
            stageMap.getChildren().add(noteMap);
        } catch (LoadException e) {
        } catch (Exception e) {
            System.err.println("Failed to load stage map");
            e.printStackTrace();
        }


        StageMapController controller = loader.getController();
        env.setStageMapController(controller);
        env.setStagePane(noteMap);
        env.getStageMapController().setEnvironment(env);
        env.getStageMapController().create();
        env.getStageMapController().setDescription();
        env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().loadStageMapData();
        env.getStageMapController().visualiseLockedTutors();

    }


    public void showStudentStagemap(String userName, String userProject) {

        loader.setLocation(getClass().getResource("/Views/StageMapPane.fxml"));

        try {
            noteMap = loader.load();
            stageMap.getChildren().clear();
            stageMap.getChildren().add(noteMap);
        } catch (LoadException e) {
            // It's all fine.
        } catch (Exception e) {
            System.err.println("Failed to load stage map");
            e.printStackTrace();
        }


        StageMapController controller = loader.getController();
        controller.setEnvOnly(env);
        controller.generateTutorAndButtonNames();

        String unlockData = env.getFirebase().getClassroomsSnapshot().child(env.getUserHandler().getClassRoom() + "/users/" + userName + "/projects/" + userProject + "/unlockMap" ).getValue().toString();
        Type mapType = new TypeToken<HashMap<String, Boolean>>() {}.getType();
        HashMap<String, Boolean> unlockStuff = new Gson().fromJson(unlockData, mapType);

        if (unlockStuff != null) {
            controller.loadStudentMap(unlockStuff);
        } else {
            //create the standard unlock map
            controller.loadStudentMap();
        }


    }

    /**
     * Used to create the badgeGrid and display the badges in stackpanes with the correct effect
     */
    private void updateBadgesDisplay() {

        ArrayList<Badge> tutorBadges = new ArrayList();

        HashMap tutorBadgeMap = BadgeManager.getTutorBadges();
        ArrayList<Badge> generalBadges = BadgeManager.getOverallBadges();

        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(-1.0);
        this.blackout = blackout;

        try {
            for (Object tutor : tutorBadgeMap.keySet()) {
                for (Object b : (ArrayList) tutorBadgeMap.get(tutor)) {
                    tutorBadges.add((Badge) b);
                }
            }
            Collections.sort(generalBadges, new badgeComparator());
            generalBadges.forEach(this::addBadgeToGrid);
            Collections.sort(tutorBadges, new badgeComparator());
            tutorBadges.forEach(this::addTutorBadgeToGrid);
        } catch (NullPointerException e) {
            // Retrieving badges from json file
        }


        for (Object tutor : tutorBadgeMap.keySet()) {
            for (Object b : (ArrayList) tutorBadgeMap.get(tutor)) {
                tutorBadges.add((Badge) b);
            }
        }

    }

    /**
     * Helper function used to order badges for the badgeGrid
     */
    public static class badgeComparator implements Comparator<Badge> {
        @Override
        public int compare(Badge b1, Badge b2) {
            return (b1.currentBadgeType > b2.currentBadgeType) ? -1 : (b1.currentBadgeType < b2.currentBadgeType) ? 1 : 0;
        }
    }

    private void displayClassAverage(String timePeriod) {
        DataSnapshot users = env.getFirebase().getClassroomsSnapshot().child(env.getUserHandler().getClassRoom()).child("users");
        ArrayList<Pair<Integer, Integer>> data = new ArrayList<>();
        users.getChildren().forEach(user -> {
            user.child("projects").getChildren().forEach(project -> {
                try {
                    Pair<Integer, Integer> totals = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getTutorHandler().getTotalsForAllTutorsInProject(project, timePeriod);
                    data.add(totals);
                } catch (Exception e) {
                    Pair<Integer, Integer> totals = new TutorHandler(env).getTotalsForAllTutorsInProject(project, timePeriod);
                    data.add(totals);
                }
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

    /**
     * Used to add a non-tutor badge to the badgeGrid
     *
     * @param b the Badge to be added
     */
    public void addBadgeToGrid(Badge b) {

        String badgeImagePath = "/images/" + b.imageName + ".png";

        Image retrieve = imageCache.retrieve(badgeImagePath, 70);
        ImageView bView = new ImageView(retrieve);
        bView.fitHeightProperty().setValue(70);
        bView.fitWidthProperty().setValue(70);
        StackPane badgeStack;

        ImageView lockView = new ImageView(imageCache.retrieve("/images/lock.png", 40));
        lockView.fitHeightProperty().setValue(40);
        lockView.fitWidthProperty().setValue(40);

        VBox badgeBox = new VBox();
        Label badgeName = new Label(b.name);
        badgeName.setFont(javafx.scene.text.Font.font(16));
        Label description = new Label(b.description);
        ProgressBar progressBar = new ProgressBar();

        try {
            progressBar.setProgress(b.badgeProgress / b.badgeLevels.get(b.currentBadgeType));
        } catch (NullPointerException n) {
            progressBar.setProgress(b.badgeProgress);
        }

        ColorAdjust badgeEffect = new ColorAdjust();
        if (b.isLocked()) {
            badgeEffect = this.blackout;
            badgeStack = new StackPane(bView, lockView);
        } else {
            badgeStack = new StackPane(bView);
        }
        bView.setEffect(badgeEffect);


        badgeBox.getChildren().addAll(badgeStack, badgeName, progressBar, description);
        badgeBox.setAlignment(Pos.CENTER);
        badgeBox.setSpacing(4);
        badgeGrid.add(badgeBox, gridX, gridY);
        if (gridX < 4) {
            gridX++;
        } else {
            gridX = 0;
            gridY++;
        }
    }

    /**
     * Used to add a tutor badge to the badgeGrid
     *
     * @param b the Badge to be added
     */
    public void addTutorBadgeToGrid(Badge b) {
        String ribbonPath = "/images/ribbonAward.png";
        String lockImagePath = "/images/lock.png";
        String badgeImagePath = "/images/" + b.imageName + ".png";

        ImageView rView = new ImageView(imageCache.retrieve(ribbonPath, 70));
        rView.fitHeightProperty().setValue(70);
        rView.fitWidthProperty().setValue(70);


        ImageView bView = new ImageView(imageCache.retrieve(badgeImagePath, 26));
        bView.fitHeightProperty().setValue(26);
        bView.fitWidthProperty().setValue(26);

        ImageView lockView = new ImageView(imageCache.retrieve(lockImagePath, 40));

        ColorAdjust badgeEffect = new ColorAdjust();
        if (b.isLocked()) {
            badgeEffect = this.blackout;
            bView = lockView;
        } else if (b.currentBadgeType == 1) {
            badgeEffect.setHue(-0.863);
            badgeEffect.setSaturation(0.8);
            badgeEffect.setBrightness(0.4);
        } else if (b.currentBadgeType == 2) {
            badgeEffect.setHue(0);
            badgeEffect.setSaturation(-1);
            badgeEffect.setBrightness(0.32);
        } else if (b.currentBadgeType == 3) {
            badgeEffect.setHue(-0.687);
            badgeEffect.setSaturation(1);
            badgeEffect.setBrightness(0.1);
        }
        rView.setEffect(badgeEffect);
        StackPane badgeStack = new StackPane(rView, bView);
        badgeStack.getChildren().get(1).setTranslateY(-13);
        badgeStack.getChildren().get(1).setTranslateX(-0.6);

        VBox badgeBox = new VBox();
        Label badgeName = new Label(b.name);
        badgeName.setFont(javafx.scene.text.Font.font(16));
        Label tutorName = new Label(b.tutorName);
        Label description = new Label(b.description);
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(b.badgeProgress / b.badgeLevels.get(b.currentBadgeType));
        badgeBox.getChildren().addAll(badgeStack, tutorName, badgeName, progressBar, description);
        badgeBox.setAlignment(Pos.CENTER);
        badgeBox.setSpacing(4);

        badgeGrid.add(badgeBox, gridX, gridY);
        if (gridX < 4) {
            gridX++;
        } else {
            gridX = 0;
            gridY++;
        }
    }

    /**
     * Used for displaying the user summary in teacher mode. The teacher does not care about a student's badges
     */
    public void hideBadges() {
        badgesContainer.setVisible(false);
        badgesContainer.setManaged(false);
    }
}
