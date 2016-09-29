package seng302.gui;

import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.util.StringConverter;
import seng302.Environment;
import seng302.Users.TutorHandler;
import seng302.data.Badge;
import seng302.managers.BadgeManager;
import seng302.utility.TutorRecord;

/**
 * Controller for the tutor stats pane,  used in the user page for all tutors.
 */
public class TutorStatsController {

    private Environment env;


    @FXML
    private LineChart lineChart;


    @FXML
    private Label latestAttempt;

    @FXML
    private Label overallStats;

    @FXML
    private Rectangle correct;

    @FXML
    private Rectangle incorrect;

    @FXML
    private Rectangle overallCorrect;

    @FXML
    private Rectangle overallIncorrect;


    @FXML
    private Label recentIncorrectLabel;

    @FXML
    private Label recentCorrectLabel;

    @FXML
    private Label overallIncorrectLabel;

    @FXML
    private Label overallCorrectLabel;

    @FXML
    private Line classAverage;


    @FXML
    private Label tutorName;


    @FXML
    private GridPane badgeGrid;

    @FXML
    private Label classAverageLabel;

    @FXML
    private Label classAverageNumber;

    String currentTutor;
    private ColorAdjust blackout;
    private int gridCount;

    public void create(Environment env) {
        this.env = env;


    }


    /**
     * Creates the most recent tutor record graph and the overall tutor record graph
     *
     * @param tutor the specific tutor that the graphs will getting data from
     */
    public void displayGraphs(String tutor, String timePeriod) {
        currentTutor = tutor;


        tutorName.setText(tutor);
        Pair<Integer, Integer> correctIncorrectRecent = new Pair<>(0, 0);
        Pair<Integer, Integer> correctIncorrectOverall = new Pair<>(0, 0);
        List<Pair<Date, Float>> dateAndTime = new ArrayList<>();
        dateAndTime.add(new Pair<>(new Date(0), 0f));

        TutorHandler handler = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler;


        try {

            switch (tutor) {

                case "Pitch Comparison Tutor":
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() == false) {

                        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().recentPracticeTutorRecordMap.get(tutor) != null) {
                            correctIncorrectRecent = new Pair(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsCorrect").intValue(), env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsIncorrect").intValue());
                        }
                    } else {
                        correctIncorrectRecent = handler.getRecentTutorTotals("pitchTutor");
                    }
                    correctIncorrectOverall = handler.getTutorTotals("pitchTutor", timePeriod);
                    dateAndTime = handler.getTimeAndScores("pitchTutor", timePeriod);
                    break;
                case "Interval Recognition Tutor":
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() == false) {

                        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().recentPracticeTutorRecordMap.get(tutor) != null) {
                            correctIncorrectRecent = new Pair(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsCorrect").intValue(), env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsIncorrect").intValue());
                        }
                    } else {
                        correctIncorrectRecent = handler.getRecentTutorTotals("intervalTutor");
                    }
                    correctIncorrectOverall = handler.getTutorTotals("intervalTutor", timePeriod);
                    dateAndTime = handler.getTimeAndScores("intervalTutor", timePeriod);
                    break;
                case "Scale Recognition Tutor":
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() == false) {

                        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().recentPracticeTutorRecordMap.get(tutor) != null) {
                            correctIncorrectRecent = new Pair(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsCorrect").intValue(), env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsIncorrect").intValue());
                        }
                    } else {
                        correctIncorrectRecent = handler.getRecentTutorTotals("scaleTutor");
                    }
                    correctIncorrectOverall = handler.getTutorTotals("scaleTutor", timePeriod);
                    dateAndTime = handler.getTimeAndScores("scaleTutor", timePeriod);
                    break;
                case "Scale Recognition Tutor (Basic)":
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() == false) {

                        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().recentPracticeTutorRecordMap.get(tutor) != null) {
                            correctIncorrectRecent = new Pair(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsCorrect").intValue(), env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsIncorrect").intValue());
                        }
                    } else {
                        correctIncorrectRecent = handler.getRecentTutorTotals("basicScaleTutor");
                    }
                    correctIncorrectOverall = handler.getTutorTotals("basicScaleTutor", timePeriod);
                    dateAndTime = handler.getTimeAndScores("basicScaleTutor", timePeriod);
                    break;

                case "Musical Terms Tutor":
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() == false) {

                        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().recentPracticeTutorRecordMap.get(tutor) != null) {
                            correctIncorrectRecent = new Pair(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsCorrect").intValue(), env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsIncorrect").intValue());
                        }
                    } else {
                        correctIncorrectRecent = handler.getRecentTutorTotals("musicalTermTutor");
                    }
                    correctIncorrectOverall = handler.getTutorTotals("musicalTermTutor", timePeriod);
                    dateAndTime = handler.getTimeAndScores("musicalTermTutor", timePeriod);
                    break;
                case "Chord Recognition Tutor":
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() == false) {

                        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().recentPracticeTutorRecordMap.get(tutor) != null) {
                            correctIncorrectRecent = new Pair(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsCorrect").intValue(), env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsIncorrect").intValue());
                        }
                    } else {
                        correctIncorrectRecent = handler.getRecentTutorTotals("chordTutor");
                    }
                    correctIncorrectOverall = handler.getTutorTotals("chordTutor", timePeriod);
                    dateAndTime = handler.getTimeAndScores("chordTutor", timePeriod);
                    break;
                case "Chord Recognition Tutor (Basic)":
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() == false) {

                        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().recentPracticeTutorRecordMap.get(tutor) != null) {
                            correctIncorrectRecent = new Pair(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsCorrect").intValue(), env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsIncorrect").intValue());
                        }
                    } else {
                        correctIncorrectRecent = handler.getRecentTutorTotals("basicChordTutor");
                    }
                    correctIncorrectOverall = handler.getTutorTotals("basicChordTutor", timePeriod);
                    dateAndTime = handler.getTimeAndScores("basicChordTutor", timePeriod);
                    break;
                case "Chord Spelling Tutor":
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() == false) {

                        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().recentPracticeTutorRecordMap.get(tutor) != null) {
                            correctIncorrectRecent = new Pair(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsCorrect").intValue(), env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsIncorrect").intValue());
                        }
                    } else {
                        correctIncorrectRecent = handler.getRecentTutorTotals("chordSpellingTutor");
                    }
                    correctIncorrectOverall = handler.getTutorTotals("chordSpellingTutor", timePeriod);
                    dateAndTime = handler.getTimeAndScores("chordSpellingTutor", timePeriod);
                    break;
                case "Key Signature Tutor":
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() == false) {

                        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().recentPracticeTutorRecordMap.get(tutor) != null) {
                            correctIncorrectRecent = new Pair(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsCorrect").intValue(), env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsIncorrect").intValue());
                        }
                    } else {
                        correctIncorrectRecent = handler.getRecentTutorTotals("keySignatureTutor");
                    }
                    correctIncorrectOverall = handler.getTutorTotals("keySignatureTutor", timePeriod);
                    dateAndTime = handler.getTimeAndScores("keySignatureTutor", timePeriod);
                    break;
                case "Diatonic Chord Tutor":
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() == false) {

                        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().recentPracticeTutorRecordMap.get(tutor) != null) {
                            correctIncorrectRecent = new Pair(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsCorrect").intValue(), env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsIncorrect").intValue());
                        }
                    } else {
                        correctIncorrectRecent = handler.getRecentTutorTotals("diatonicChordTutor");
                    }
                    correctIncorrectOverall = handler.getTutorTotals("diatonicChordTutor", timePeriod);
                    dateAndTime = handler.getTimeAndScores("diatonicChordTutor", timePeriod);
                    break;
                case "Scale Modes Tutor":
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() == false) {

                        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().recentPracticeTutorRecordMap.get(tutor) != null) {
                            correctIncorrectRecent = new Pair(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsCorrect").intValue(), env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsIncorrect").intValue());
                        }
                    } else {
                        correctIncorrectRecent = handler.getRecentTutorTotals("scaleModesTutor");
                    }
                    correctIncorrectOverall = handler.getTutorTotals("scaleModesTutor", timePeriod);
                    dateAndTime = handler.getTimeAndScores("scaleModesTutor", timePeriod);
                    break;
                case "Scale Spelling Tutor":
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() == false) {

                        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().recentPracticeTutorRecordMap.get(tutor) != null) {
                            correctIncorrectRecent = new Pair(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsCorrect").intValue(), env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsIncorrect").intValue());
                        }
                    } else {
                        correctIncorrectRecent = handler.getRecentTutorTotals("scaleSpellingTutor");
                    }
                    correctIncorrectOverall = handler.getTutorTotals("scaleSpellingTutor", timePeriod);
                    dateAndTime = handler.getTimeAndScores("scaleSpellingTutor", timePeriod);
                    break;
                case "Microphone Input Tutor":
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() == false) {

                        if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().recentPracticeTutorRecordMap.get(tutor) != null) {
                            correctIncorrectRecent = new Pair(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsCorrect").intValue(), env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getRecentPracticeTutorRecordMap().get(tutor).getStats().get("questionsIncorrect").intValue());
                        }
                    } else {
                        correctIncorrectRecent = handler.getRecentTutorTotals("microphoneInputTutor");
                    }
                    correctIncorrectOverall = handler.getTutorTotals("microphoneInputTutor", timePeriod);
                    dateAndTime = handler.getTimeAndScores("microphoneInputTutor", timePeriod);
                    break;

            }

            latestAttempt.setVisible(true);
            overallStats.setVisible(true);

            // Set up most recent graph and labels.

            double total = correctIncorrectRecent.getKey() + correctIncorrectRecent.getValue();
            double widthCorrect = 500 * (correctIncorrectRecent.getKey() / total);
            Timeline correctAnim = new Timeline(
                    new KeyFrame(Duration.millis(800), new KeyValue(correct.widthProperty(), widthCorrect, Interpolator.EASE_OUT)));
            correctAnim.play();
            correct.setWidth(widthCorrect);
            correct.setFill(Color.web("00b004"));
            double widthIncorrect;
            if (correctIncorrectOverall.getValue() != 0) {
                widthIncorrect = 500 * (correctIncorrectRecent.getValue() / total);
            } else {
                widthIncorrect = 500;
            }
            Timeline incorrectAnim = new Timeline(
                    new KeyFrame(Duration.millis(800), new KeyValue(incorrect.widthProperty(), widthIncorrect, Interpolator.EASE_OUT)));
            incorrectAnim.play();
            incorrect.setWidth(widthIncorrect);
            incorrect.setFill(Color.GRAY);
            recentCorrectLabel.setText(correctIncorrectRecent.getKey() + " \ncorrect");
            recentIncorrectLabel.setText(correctIncorrectRecent.getValue() + " \nincorrect");

            // Set up Overall graph and labels.

            double overallTotal = correctIncorrectOverall.getKey() + correctIncorrectOverall.getValue();
            double overallWidthCorrect = 500 * (correctIncorrectOverall.getKey() / overallTotal);
            Timeline overallCorrectAnim = new Timeline(
                    new KeyFrame(Duration.millis(800), new KeyValue(overallCorrect.widthProperty(), overallWidthCorrect, Interpolator.EASE_OUT)));
            overallCorrectAnim.play();
            overallCorrect.setWidth(overallWidthCorrect);
            overallCorrect.setFill(Color.web("00b004"));
            double overallWidthIncorrect;
            if (correctIncorrectOverall.getValue() != 0) {
                overallWidthIncorrect = 500 * (correctIncorrectOverall.getValue() / overallTotal);
            } else {
                overallWidthIncorrect = 500;
            }

            Timeline overallIncorrectAnim = new Timeline(
                    new KeyFrame(Duration.millis(800), new KeyValue(overallIncorrect.widthProperty(), overallWidthIncorrect, Interpolator.EASE_OUT)));
            overallIncorrectAnim.play();
            overallIncorrect.setWidth(overallWidthIncorrect);
            overallIncorrect.setFill(Color.GRAY);
            overallCorrectLabel.setText(correctIncorrectOverall.getKey() + " \ncorrect");
            overallIncorrectLabel.setText(correctIncorrectOverall.getValue() + " \nincorrect");

            // Figure out class average
            makeLineGraph(dateAndTime, timePeriod);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("There are no records for the " + tutor);
        }


    }

    /**
     * Calculates the average score for the class for a certain tutor and time period and displays
     * the line in the correct place on the graph.
     *
     * @param handler           The current tutorHandler.
     * @param tutorNameNoSpaces The current tutorName.
     * @param timePeriod        the time period of data to display.
     */
    private void displayClassAverage(TutorHandler handler, String tutorNameNoSpaces, String timePeriod) {
        DataSnapshot classroomData = env.getFirebase().getClassroomsSnapshot().child(env.getUserHandler().getClassRoom() + "/users");
        ArrayList<Pair<Integer, Integer>> classTotals = new ArrayList<>();

        classroomData.getChildren().forEach(user -> {
            DataSnapshot projects = user.child("projects");
            projects.getChildren().forEach(project -> {
                ArrayList<TutorRecord> records = handler.getTutorDataFromProject(project, tutorNameNoSpaces);
                Pair<Integer, Integer> correctIncorrect = handler.getTutorTotals(records, timePeriod);
                classTotals.add(correctIncorrect);
            });

        });
        Integer classTotalIncorrect = 0;
        Integer classTotalCorrect = 0;
        for (Pair<Integer, Integer> score : classTotals) {
            classTotalCorrect += score.getKey();
            classTotalIncorrect += score.getValue();
        }

        float averageClassScore = 0;
        if (classTotalCorrect + classTotalIncorrect != 0) {
            averageClassScore = classTotalCorrect.floatValue() / (classTotalCorrect + classTotalIncorrect);
        }
        StackPane.setMargin(classAverage, new Insets(0, 0, 0, 500 * averageClassScore - 30));
        HBox.setMargin(classAverageLabel, new Insets(0, 0, 0, 500 * averageClassScore - 50));
        classAverageNumber.setText(String.format("%.2f", averageClassScore));
    }


    /**
     * Draws a line graph showing the scores over time.
     */
    private void makeLineGraph(List<Pair<Date, Float>> dateAndTimeList, String timePeriod) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM H:mm:ss");
        NumberAxis numberAxis = (NumberAxis) lineChart.getYAxis();

        // This makes the hover labels visible for 0 and 100 by adding -20 and 120 onto the
        // the axis but then label formatting them to be invisible.
        numberAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                if (object.equals(-20.0)) {
                    return "";
                } else if (object.equals(0.0)) {
                    return "0";
                } else if (object.equals(20.0)) {
                    return "20";
                } else if (object.equals(40.0)) {
                    return "40";
                } else if (object.equals(60.0)) {
                    return "60";
                } else if (object.equals(80.0)) {
                    return "80";
                } else if (object.equals(100.0)) {
                    return "100";
                } else if (object.equals(120.0)) {
                    return " ";
                }
                return "";
            }

            @Override
            public Number fromString(String string) {
                if (string.equals("")) {
                    return -20.0;
                } else if (string.equals("0")) {
                    return 0.0;
                } else if (string.equals("20")) {
                    return 20.0;
                } else if (string.equals("40")) {
                    return 40.0;
                } else if (string.equals("60")) {
                    return 60.0;
                } else if (string.equals("80")) {
                    return 80.0;
                } else if (string.equals("100")) {
                    return 100.0;
                } else if (string.equals(" ")) {
                    return 120.0;
                }
                return 0;
            }
        });


        XYChart.Series<String, Float> lineSeries = new XYChart.Series<>();
        for (Pair<Date, Float> dateTime : dateAndTimeList) {
            Date date = dateTime.getKey();
            String milli = formatter.format(date);
            XYChart.Data data = new XYChart.Data<>(milli, dateTime.getValue());
            data.setNode(new hoverPane(date, dateTime.getValue()));
            lineSeries.getData().add(data);
        }
        lineChart.getData().clear();
        lineChart.getData().add(lineSeries);

    }


    class hoverPane extends VBox {
        hoverPane(Date date, float value) {
            setPrefSize(10, 10);
            final Label label = createDataLabel(date, value);
            this.setAlignment(Pos.CENTER);
            setOnMouseEntered(e -> {
                this.toFront();
                getChildren().setAll(label);
                setCursor(Cursor.NONE);
            });
            setOnMouseExited(e -> {
                getChildren().clear();
                setCursor(Cursor.CROSSHAIR);
            });

        }

        private Label createDataLabel(Date date, float value) {
            String score = String.format("%.0f", value);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YY H:mm");
            String dateformat = formatter.format(date);
            final Label label = new Label(score + "% - " + dateformat);
            label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
            label.setStyle("-fx-font-size: 8; -fx-font-weight: normal;");
            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setAlignment(Pos.CENTER);
            return label;
        }
    }

    /**
     * Used to create the badgeGrid and display the badges in stackpanes with the correct effect
     */
    public void updateBadgesDisplay() {
        ArrayList<Badge> tutorBadges = new ArrayList();

        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(-1.0);
        this.blackout = blackout;

        HashMap tutorBadgeMap = BadgeManager.getTutorBadges();

        for (Object tutor : tutorBadgeMap.keySet()) {
            if (tutor.toString().equals(tutorName.getText())) {
                for (Object b : (ArrayList) tutorBadgeMap.get(tutor)) {
                    tutorBadges.add((Badge) b);
                }
            }
        }

        badgeGrid.getChildren().removeAll();
        Collections.sort(tutorBadges, new badgeComparator());
        tutorBadges.forEach(this::addTutorBadgeToGrid);
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

    /**
     * Used to add a tutor badge to the badgeGrid
     *
     * @param b the Badge to be added
     */
    public void addTutorBadgeToGrid(Badge b) {
        String ribbonPath = "/images/ribbonAward.png";
        String lockImagePath = "/images/lock.png";
        String badgeImagePath = "/images/" + b.imageName + ".png";
        String platBadgePath = "/images/plat-badge.png";


        ImageView rView = new ImageView(UserSummaryController.imageCache.retrieve(ribbonPath, 70));
        rView.fitHeightProperty().setValue(70);
        rView.fitWidthProperty().setValue(70);

        ImageView bView = new ImageView(UserSummaryController.imageCache.retrieve(badgeImagePath, 26));
        bView.fitHeightProperty().setValue(26);
        bView.fitWidthProperty().setValue(26);

        ImageView lockView = new ImageView(UserSummaryController.imageCache.retrieve(lockImagePath, 40));
        lockView.fitHeightProperty().setValue(40);
        lockView.fitWidthProperty().setValue(40);

        ImageView pView = new ImageView(UserSummaryController.imageCache.retrieve(platBadgePath, 70));
        pView.fitHeightProperty().setValue(70);
        pView.fitWidthProperty().setValue(70);

        ColorAdjust badgeEffect = new ColorAdjust();
        if (b.currentBadgeType == 0) {
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
        } else if (b.currentBadgeType == 4) {
            rView = pView;
        }
        rView.setEffect(badgeEffect);

        VBox badgeBox = new VBox();
        Label badgeName = new Label(b.name);
        badgeName.setFont(javafx.scene.text.Font.font(16));
        Label tutorName = getTutorName(this.tutorName.getText());
        Label description = new Label(b.description);
        Label progressDesc = new Label();
        ProgressBar progressBar = new ProgressBar();


        StackPane badgeStack = new StackPane(rView, bView);
        if (b.currentBadgeType == 4) {
            badgeStack.getChildren().get(1).setTranslateY(-1);
            progressDesc.setText("Complete");
            progressBar.setProgress(1);
        } else {
            badgeStack.getChildren().get(1).setTranslateY(-13);
            progressDesc.setText((int) b.badgeProgress + " out of " + b.badgeLevels.get(b.currentBadgeType));
            progressBar.setProgress(b.badgeProgress / b.badgeLevels.get(b.currentBadgeType));
        }
        badgeStack.getChildren().get(1).setTranslateX(-0.6);

        badgeBox.getChildren().addAll(badgeStack, tutorName, badgeName, progressBar, description, progressDesc);
        badgeBox.setAlignment(Pos.CENTER);
        badgeBox.setSpacing(4);
        badgeGrid.add(badgeBox, gridCount, 0);
        gridCount++;
    }


    @FXML
    void loadTutor() {
        loadTutor(this.currentTutor);
    }


    /**
     * Fires the appropriate action for opening a tutor in the user pane, given the name of the
     * tutor.
     *
     * @param tutorName tutor name.
     */
    public void loadTutor(String tutorName) {

        env.getRootController().getTutorFactory().openTutor(tutorName);

    }

    /**
     * Helper function to get the tutor name label
     * @param tutorName the name of the tutor the badge belongs to
     * @return tutorNameLabel the badge label text
     */
    private Label getTutorName(String tutorName) {
        Label tutorNameLabel = new Label();
        if (tutorName.equals("Scale Recognition Tutor") || tutorName.equals("Chord Recognition Tutor")) {
            tutorNameLabel.setText(tutorName + " (Advanced)");
        } else {
            tutorNameLabel.setText(tutorName);
        }
        return tutorNameLabel;
    }


}
