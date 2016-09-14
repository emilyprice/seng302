package seng302.gui;

import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;

import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.util.Pair;
import seng302.Environment;
import seng302.data.Badge;
import seng302.managers.BadgeManager;

/**
 * Created by jmw280 on 22/08/16.
 */
public class UserPageController {


    @FXML
    AnchorPane contentPane;

    @FXML
    VBox stats;

    @FXML
    VBox badgesVBox;

    @FXML
    StackedBarChart stackedBar;

    @FXML
    StackedBarChart recentBar;

    @FXML
    AnchorPane topPane;

    @FXML
    JFXListView listView;

    @FXML
    ImageView lockView;

    @FXML
    ColorAdjust blackout;

    @FXML
    Label chartTitle;

    @FXML
    GridPane badgeGrid;

    @FXML
    LineChart lineChart;

    @FXML
    ImageView imageDP;

    @FXML
    Label latestAttempt;

    @FXML
    Label overallStats;

    private Environment env;

    private int gridCount;

    public UserPageController() {
    }


    public void setEnvironment(Environment env) {
        this.env = env;
    }


    public void populateUserOptions() {

        ArrayList<String> options = new ArrayList<>();
        options.add("Summary");
        options.add("Musical Terms Tutor");
        options.add("Pitch Comparison Tutor");
        options.add("Scale Recognition Tutor");
        options.add("Chord Recognition Tutor");
        options.add("Interval Recognition Tutor");
        options.add("Chord Spelling Tutor");
        options.add("Key Signature Tutor");
        options.add("Diatonic Chord Tutor");
        //options.add("Modes Tutor");

        Image lockImg = new Image(getClass().getResourceAsStream("/images/lock.png"), 20, 20, false, false);

        listView.getItems().addAll(FXCollections.observableArrayList(options));

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            displayGraphs((String) newValue);
        });
        listView.getSelectionModel().selectFirst();
        listView.setMaxWidth(200);
        listView.setMinWidth(200);
        // This allows images to be displayed in the listview. Still trying to
        // make the text centered and the height and width the same as the others.
        listView.setCellFactory(listView -> new JFXListCell<String>() {

            @Override
            public void updateItem(String tutor, boolean empty) {
                super.updateItem(tutor, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else if (tutor.equals("Scale Recognition Tutor")) {
//                    imageView.setImage(lockImg);
//                    setText(tutor);
//                    setGraphic(imageView);
                    setDisable(true);
                    setMouseTransparent(true);

                }
            }
        });


    }

    /**
     * Makes a line graph showing the scores over time. Still figuring out the scale.
     */
    private void makeLineGraph(List<Pair<Date, Float>> dateAndTimeList) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM H:mm:ss");


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
                getChildren().setAll(label);
                setCursor(Cursor.NONE);
                toFront();
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
            final Label label = new Label(score + "%\n" + dateformat);
            label.getStyleClass().addAll("default-color0", "chart-line-symbol", "chart-series-line");
            label.setStyle("-fx-font-size: 8; -fx-font-weight: normal;");
            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setAlignment(Pos.CENTER);
            return label;
        }
    }


    /**
     * creates the most recent tutor record graph and the overall tutor record graph
     *
     * @param tutor the specific tutor that the graphs will getting data from
     */
    private void displayGraphs(String tutor) {
        Pair<Integer, Integer> correctIncorrectRecent = new Pair<>(0, 0);
        Pair<Integer, Integer> correctIncorrectOverall = new Pair<>(0, 0);
        List<Pair<Date, Float>> dateAndTime = new ArrayList<>();
        dateAndTime.add(new Pair<>(new Date(0), 0f));

        XYChart.Series<Number, String> recentSeries1 = new XYChart.Series<>();
        XYChart.Series<Number, String> recentSeries2 = new XYChart.Series<>();

        XYChart.Series<Number, String> overallSeries1 = new XYChart.Series<>();
        XYChart.Series<Number, String> overallSeries2 = new XYChart.Series<>();


        switch (tutor) {
            case "Pitch Comparison Tutor":
                correctIncorrectRecent = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getRecentTutorTotals("pitchTutor");
                correctIncorrectOverall = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getTutorTotals("pitchTutor");
                dateAndTime = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getTimeAndScores("pitchTutor");
                break;
            case "Interval Recognition Tutor":
                correctIncorrectOverall = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getRecentTutorTotals("intervalTutor");
                correctIncorrectRecent = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getTutorTotals("intervalTutor");
                dateAndTime = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getTimeAndScores("intervalTutor");
                break;
            case "Scale Recognition Tutor":
                correctIncorrectRecent = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getRecentTutorTotals("scaleTutor");
                correctIncorrectOverall = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getTutorTotals("scaleTutor");
                break;
            case "Musical Terms Tutor":
                correctIncorrectRecent = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getRecentTutorTotals("musicalTermTutor");
                correctIncorrectOverall = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getTutorTotals("musicalTermTutor");
                break;
            case "Chord Recognition Tutor":
                correctIncorrectRecent = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getRecentTutorTotals("chordTutor");
                correctIncorrectOverall = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getTutorTotals("chordTutor");
                break;
            case "Chord Spelling Tutor":
                correctIncorrectRecent = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getRecentTutorTotals("chordSpellingTutor");
                correctIncorrectOverall = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getTutorTotals("chordSpellingTutor");
                break;
            case "Key Signature Tutor":
                correctIncorrectRecent = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getRecentTutorTotals("keySignatureTutor");
                correctIncorrectOverall = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getTutorTotals("keySignatureTutor");
                break;
            case "Diatonic Chord Tutor":
                correctIncorrectRecent = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getRecentTutorTotals("diatonicChordTutor");
                correctIncorrectOverall = env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().tutorHandler.getTutorTotals("diatonicChordTutor");
                break;
        }

        if (tutor.equals("Summary")) {
            recentBar.setVisible(false);
            overallStats.setVisible(false);
            latestAttempt.setVisible(false);


        } else {

            recentSeries1.getData().add(new XYChart.Data<>(correctIncorrectRecent.getKey(), ""));
            recentSeries2.getData().add(new XYChart.Data<>(correctIncorrectRecent.getValue(), ""));
            recentBar.getData().clear();
            recentBar.setVisible(true);
            latestAttempt.setVisible(true);
            overallStats.setVisible(true);
            recentBar.getData().addAll(recentSeries1, recentSeries2);

            overallSeries1.getData().add(new XYChart.Data<>(correctIncorrectOverall.getKey(), ""));
            overallSeries2.getData().add(new XYChart.Data<>(correctIncorrectOverall.getValue(), ""));
            stackedBar.getData().clear();
            stackedBar.getData().addAll(overallSeries1, overallSeries2);


            makeLineGraph(dateAndTime);
            updateBadgesDisplay();
        }
    }

    private void updateBadgesDisplay() {

        ArrayList levels = new ArrayList<Integer>();
        Badge grad = new Badge("Graduation", "Yadda yadda", levels, 0.25, 1, "gradHat");
        Badge pitch = new Badge("Pitch Tutor", "Some ribbon thing", levels, 0.75, 2, "tuning-fork");
        Badge terms = new Badge("Terms Tutor", "Some ribbon thing", levels, 0.15, 0, "open-book");
        ArrayList<Badge> generalBadges = new ArrayList();
        ArrayList<Badge> tutorBadges = new ArrayList();

//        HashMap tutorBadges = BadgeManager.getTutorBadges();
//        ArrayList<Badge> tutorBadges = BadgeManager.getOverallBadges();
//        HashMap tutorBadges = BadgeManager.getTutorBadges();
//        System.out.println("Badges pls: " + tutorBadges);

        ColorAdjust blackout = new ColorAdjust();
        blackout.setBrightness(-1.0);
        this.blackout = blackout;
        Image lockImg = new Image("/images/lock.png");
        ImageView lockView = new ImageView(lockImg);
        lockView.fitHeightProperty().setValue(45);
        lockView.fitWidthProperty().setValue(45);
        this.lockView = lockView;

        generalBadges.add(grad);
        tutorBadges.add(pitch);
        tutorBadges.add(terms);
        tutorBadges.forEach(this::addTutorBadgeToGrid);
        generalBadges.forEach(this::addBadgeToGrid);
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
        badgeGrid.add(badgeBox, gridCount, 0);
        gridCount++;
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
        badgeGrid.add(badgeBox, gridCount, 0);
        gridCount++;
    }


    public void updateImage() {
        final Circle clip = new Circle(imageDP.getFitWidth() - 50.0, imageDP.getFitHeight() - 50.0, 100.0);

        imageDP.setImage(env.getUserHandler().getCurrentUser().getUserPicture());


        clip.setRadius(50);

        imageDP.setClip(clip);

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = imageDP.snapshot(parameters, null);

        imageDP.setClip(null);
        imageDP.setEffect(new DropShadow(5, Color.BLACK));

        imageDP.setImage(image);


    }


}
