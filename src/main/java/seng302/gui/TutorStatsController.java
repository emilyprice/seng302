package seng302.gui;

import com.google.firebase.database.DataSnapshot;

import com.jfoenix.controls.JFXButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
import seng302.utility.TutorRecord;

/**
 * Controller for the tutor stats pane,  used in the user page for all tutors.
 */
public class TutorStatsController {

    private Environment env;

    @FXML
    private VBox stats;

    @FXML
    private StackedBarChart stackedBar;


    @FXML
    private LineChart lineChart;

    @FXML
    private StackedBarChart levelBar;

    @FXML
    private ImageView imageDP;

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
    private Rectangle progressBar;

    @FXML
    private BorderPane tutorHeader;

    @FXML
    private Label tutorName;

    @FXML
    private JFXButton btnLoadTutor;

    @FXML
    private Label badgesLabel;

    @FXML
    private AnchorPane badgesAnchor;

    String currentTutor;

    public void create(Environment env) {
        this.env = env;


    }


    /**
     * creates the most recent tutor record graph and the overall tutor record graph
     *
     * @param tutor the specific tutor that the graphs will getting data from
     */
    public void displayGraphs(String tutor, String timePeriod) {
        currentTutor = tutor;
        String tutorNameNoSpaces = tutor.replaceAll("\\s", "");


        tutorName.setText(tutor);
        Pair<Integer, Integer> correctIncorrectRecent;
        Pair<Integer, Integer> correctIncorrectOverall;
        List<Pair<Date, Float>> dateAndTime = new ArrayList<>();
        dateAndTime.add(new Pair<>(new Date(0), 0f));

        TutorHandler handler = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler;

        correctIncorrectRecent = handler.getRecentTutorTotals(tutorNameNoSpaces);
        correctIncorrectOverall = handler.getTutorTotals(handler.getTutorData(tutorNameNoSpaces), timePeriod);
        dateAndTime = handler.getTimeAndScores(tutorNameNoSpaces, timePeriod);


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
        double widthIncorrect = 500 * (correctIncorrectRecent.getValue() / total);
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
        double overallWidthIncorrect = 500 * (correctIncorrectOverall.getValue() / overallTotal);
        Timeline overallIncorrectAnim = new Timeline(
                new KeyFrame(Duration.millis(800), new KeyValue(overallIncorrect.widthProperty(), overallWidthIncorrect, Interpolator.EASE_OUT)));
        overallIncorrectAnim.play();
        overallIncorrect.setWidth(overallWidthIncorrect);
        overallIncorrect.setFill(Color.GRAY);
        overallCorrectLabel.setText(correctIncorrectOverall.getKey() + " \ncorrect");
        overallIncorrectLabel.setText(correctIncorrectOverall.getValue() + " \nincorrect");

        // Figure out class average
        DataSnapshot classroomData = env.getFirebase().getClassroomsSnapshot().child(env.getUserHandler().getClassRoom() + "/users");
        ArrayList<Pair<Integer, Integer>> classTotals = new ArrayList<>();

        classroomData.getChildren().forEach(user -> {
            DataSnapshot projects = user.child("projects");
            projects.getChildren().forEach(project -> {
                ArrayList<TutorRecord> records = handler.getTutorDataFromProject(project, tutorNameNoSpaces);
                Pair<Integer, Integer> correctIncorrect = handler.getTutorTotals(records, timePeriod);
                if (correctIncorrect.getKey() != 0 || correctIncorrect.getValue() != 0) {
                    classTotals.add(correctIncorrect);
                }


            });

        });
        System.out.println(classTotals);
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
        System.out.println(averageClassScore);

        StackPane.setMargin(classAverage, new Insets(0, 0, 0, 500 * averageClassScore - 30));

        makeLineGraph(dateAndTime, timePeriod);


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


}
