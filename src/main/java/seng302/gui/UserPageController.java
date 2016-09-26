package seng302.gui;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;

import java.awt.*;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.control.*;

import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ContextMenu;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.util.Pair;
import org.controlsfx.control.PopOver;
import seng302.Environment;
import seng302.MusicPlayer;
import seng302.data.Badge;
import seng302.managers.BadgeManager;
import seng302.MusicPlayer;

import javax.swing.*;

/**
 * Handles and Creates Users.
 */
public class UserPageController {


    @FXML
    AnchorPane contentPane;

    @FXML
    VBox summaryPage;

    /*
    metronome popover
     */

    private PopOver metronomePop;

    @FXML
    private Button metronomeBtn;


    @FXML
    SplitPane userView;

    @FXML
    JFXListView listView;

    @FXML
    private JFXButton btnSettings;

    @FXML
    Label txtFullName;

    @FXML
    ImageView imageDP2;

    @FXML
    JFXBadge levelBadge;

    @FXML
    Label latestAttempt;

    @FXML
    StackPane stageMap;

    @FXML
    ScrollPane currentPage;

    @FXML
    private Slider timeSlider;

    StringConverter convert;

    private TutorStatsController statsController;

    @FXML
    VBox tutors;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    AnchorPane summary;

    private Environment env;

    public Label tempoLabel = new Label();

    private TranslateTransition anim;

    public javafx.scene.control.TextField tempoInput = new javafx.scene.control.TextField();


    public void setEnvironment(Environment env) {
        this.env = env;
        this.env.setUserPageController(this);

    }


    /**
     * Pretty much a constructor - loads userPage relevant data.
     */
    protected void load() {
        populateUserOptions();
        launchMetronomePopOver();
        //tempoLabel = new Label();



        imageDP2.setImage(env.getUserHandler().getCurrentUser().getUserPicture());
        updateLevelBadge();

        try {

            txtFullName.setText(env.getUserHandler().getCurrentUser().getUserFirstName() + " "
                    + env.getUserHandler().getCurrentUser().getUserLastName());
        } catch (NullPointerException e) {
            //txtFullName not initialized yet.
        }

    }

    /**
     * Refreshes the display of the level indicator badge so that it matches the level of the user's
     * current project
     */
    public void updateLevelBadge() {
        levelBadge.refreshBadge();
        levelBadge.setText(Integer.toString(env.getUserHandler().getCurrentUser().getUserLevel()));
    }


    /**
     * Adds all the existing tutors to the list view. Enables functionality for displaying the tutor
     * stat pages.
     */
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
        options.add("Scale Modes Tutor");
        options.add("Scale Spelling Tutor");

        Image lockImg = new Image(getClass().getResourceAsStream("/images/lock.png"), 20, 20, false, false);

        listView.getItems().addAll(FXCollections.observableArrayList(options));


        listView.setMaxWidth(200);
        listView.setMinWidth(200);
        listView.setDepthProperty(1);


        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showPage((String) newValue);
        });

        // Set after the listener so it loads user summary correctly
        listView.getSelectionModel().selectFirst();


        // This allows images to be displayed in the listview. Still trying to
        // make the text centered and the height and width the same as the others.
        listView.setCellFactory(listView -> new JFXListCell<String>() {

            @Override
            public void updateItem(String tutor, boolean empty) {

                super.updateItem(tutor, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);

                } else {
                    //if in competitive mode, lock the relevant tabs
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode()) {
                        if (!tutor.equals("Summary") && env.stageMapController.unlockStatus.get(env.stageMapController.converted.get(tutor)) == false) {
                            setGraphic(new ImageView(lockImg));
                            setTextFill(Color.GRAY);
                            setText(tutor);
                            setDisable(true);
                        }


                    } else {
                        setDisable(false);
                    }
                }
            }
        });

    }


    /**
     * Creates the GUI time slider. This allows the user to select a specific time period to view
     * tutor results from.
     */
    private void setupTimeSlider() {
        timeSlider.setMaxWidth(200);

        convert = new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                if (object == 0) {
                    return "Last 24 Hours";
                } else if (object == 1) {
                    return "Last Week";
                } else if (object == 2) {
                    return "Last Month";
                } else if (object == 3) {
                    return "Last Six Months";
                } else if (object == 4) {
                    return "Last Year";
                } else if (object == 5) {
                    return "All Time";
                }
                return null;

            }

            @Override
            public Double fromString(String string) {
                if (string.equals("Last 24 Hours")) {
                    return 0d;
                } else if (string.equals("Last Week")) {
                    return 1d;
                } else if (string.equals("Last Month")) {
                    return 2d;
                } else if (string.equals("Last Six Months")) {
                    return 3d;
                } else if (string.equals("Last Year")) {
                    return 4d;
                } else if (string.equals("All Time")) {
                    return 5d;
                }
                return null;
            }
        };

        timeSlider.setLabelFormatter(convert);
        timeSlider.valueProperty().addListener(((observable1, oldValue1, newValue1) -> {
            String result = convert.toString(timeSlider.getValue());
            if (result != null) {
                updateGraphs(result);
            }
        }));

        timeSlider.setOnMouseReleased(e -> {
            updateGraphs(convert.toString(timeSlider.getValue()));
        });
    }

    /**
     * Updates the data in the summary stats graphs
     *
     * @param timePeriod The time period to display data from in the summary stats graphs
     */
    private void updateGraphs(String timePeriod) {
        statsController.displayGraphs((String) listView.getSelectionModel().getSelectedItem(), timePeriod);
    }

    /**
     * Displays either a statistics or summary page
     *
     * @param pageName The name of the page to display - either "summary" or the name of a tutor.
     */
    public void showPage(String pageName) {

        setupTimeSlider();
        if (pageName.equals("Summary")) {
            showSummaryPage();
        } else {
            showTutorStats(pageName);
        }


    }

    /**
     *  OnClick action for the UserPage settings button.
     *  Opens a context menu with settings/logout options
     */
    @FXML
    void openSettings(MouseEvent e) {

        MenuItem menuItemSettings = new MenuItem("Settings");

        MenuItem menuItemLogout = new MenuItem("Logout");

        menuItemLogout.setOnAction(k -> env.getRootController().showCloseWindow("logout"));

        menuItemSettings.setOnAction(e2 -> env.getRootController().launchSettings());
        ContextMenu settingsDropDown = new ContextMenu();
        settingsDropDown.getItems().addAll(menuItemSettings,menuItemLogout);

        settingsDropDown.setId("flatDropDown");

        btnSettings.setContextMenu(settingsDropDown);

        settingsDropDown.show(btnSettings, e.getScreenX(), e.getScreenY());


    }

    /**
     * OnClick action for the UserPage metronome button.
     * Opens a popover that contains the metronome
     *
     */
    @FXML
    public void launchMetronomePopOver() {

        //Goes inside metronome popover
        VBox metronomeVBox = new VBox();

        //Hbox to contain label stating current BPM
        HBox tempoLabelBox = new HBox();


        //HBox to contain metronome
        HBox metronome = new HBox();

        //Hbox to contain text box that allows user to change tempo
        HBox changeTempo = new HBox();
        JFXButton setTempo = new JFXButton("Set tempo");
        setTempo.getStyleClass().add("primary");

        tempoInput.setPrefColumnCount(4); //setting col size (user can input 4 characters)
        changeTempo.getChildren().add(tempoInput);
        changeTempo.getChildren().add(setTempo);
        changeTempo.setSpacing(10);
        changeTempo.setPadding(new Insets(10));






        Integer currentTempo = env.getPlayer().getTempo();
        tempoLabel.setText("The current tempo is set to " + currentTempo + " BPM");
        tempoInput.setText(((Integer)currentTempo).toString());
        tempoLabelBox.getChildren().add(tempoLabel);


        metronome.getChildren().add(metronomeAnimation());


        metronomeVBox.getChildren().add(tempoLabelBox);
        metronomeVBox.getChildren().add(metronome);
        metronomeVBox.getChildren().add(changeTempo);

        setTempo.setOnAction(event->{
            if (Integer.valueOf(tempoInput.getText()) >= 20 && Integer.valueOf(tempoInput.getText()) <= 300) {
                env.getPlayer().setTempo(Integer.valueOf(tempoInput.getText()));
                tempoLabel.setText("The current tempo is set to " + tempoInput.getText() + " BPM");
                anim.setDuration(Duration.millis((60/Float.valueOf(tempoInput.getText()))*1000));
                anim.playFromStart();
            }

                });


        // used the spacing etc from settings to see if it will come out nicely. Subject to change
        metronomeVBox.setSpacing(10);
        metronomeVBox.setPadding(new Insets(10));

        //Declaring the popover
        metronomePop = new PopOver(metronomeVBox);
        metronomePop.setTitle("Metronome");
    }



    private AnchorPane metronomeAnimation() {
        AnchorPane animationPane = new AnchorPane(); //pane to contain animation
        animationPane.setPrefSize(200,50);
        animationPane.setMinSize(100, 50);
        Circle ball = new Circle(); //bouncing ball for metronome
        ball.getStyleClass().add("primary"); //make the ball match the theme

        ball.setCenterX(20);
        ball.setCenterY(25);
        ball.setRadius(4);

        anim = new TranslateTransition(Duration.millis((60/Float.valueOf(tempoInput.getText()))*1000), ball);
//        anim.setDuration(Duration.INDEFINITE);
//        anim.setNode(ball);
        anim.setFromX(20);
        anim.setToX(200);
        anim.setInterpolator(Interpolator.LINEAR);
        anim.setAutoReverse(true);
        anim.setCycleCount(Timeline.INDEFINITE);

        anim.play();
        animationPane.getChildren().add(ball);

        return animationPane;
    }

//    private AnchorPane buildMetronomeAnimation(AnchorPane metronomeBox) {
//        //setting up the animated metronome
//        Line line;
//        DoubleProperty startXVal = new SimpleDoubleProperty(100.0);
//        Timeline anim = TimelineBuilder.create()
//                .autoReverse(true)
//                .keyFrames(
//                        new KeyFrame(
//                                new Duration(0.0),
//                                new KeyValue(startXVal, 100.0)
//                        ),
//                        new KeyFrame(
//                                new Duration(50),
//                                new KeyValue(startXVal, 300.0, Interpolator.LINEAR)
//                        )
//                )
//                .cycleCount(Timeline.INDEFINITE)
//                .build();
//
//        AnchorPane scene  = new AnchorPane()
//                .width(400)
//                .height(500)
//                .root(
//                        GroupBuilder.create()
//                                .children(
//                                        line = LineBuilder.create()
//                                                .startY(50)
//                                                .endX(200)
//                                                .endY(400)
//                                                .strokeWidth(4)
//                                                .stroke(Color.GREEN)
//                                                .build()
//                                )
//                                .build()
//                )
//                .build();
//
//
//        line.startXProperty().bind(startXVal);
//
//        metronomeBox.set
//        metronomeBox.setTitle("Metronome 1");
//        metronomeBox.show();
//
//        return metronomeBox;
//
//    }

    /**
     * Hides and shows the metronome popover when the metronome button is selected
     */
    @FXML
    private void toggleMetronomePopOver() {
        if (metronomePop.isShowing()) {
            metronomePop.hide();

        } else {
            metronomePop.show(metronomeBtn);
        }
    }



    /**
     * Displays the page containing summary information about the user's current project
     */
    public void showSummaryPage() {
        env.getRootController().setHeader("Summary");
        listView.getSelectionModel().selectFirst();

        FXMLLoader summaryLoader = new FXMLLoader(getClass().getResource("/Views/UserSummary.fxml"));

        try {
            VBox summaryPage = summaryLoader.load();
            currentPage.setContent(summaryPage);

            AnchorPane.setLeftAnchor(summaryPage, 0.0);
            AnchorPane.setTopAnchor(summaryPage, 0.0);
            AnchorPane.setBottomAnchor(summaryPage, 0.0);
            AnchorPane.setRightAnchor(summaryPage, 0.0);

            UserSummaryController summaryController = summaryLoader.getController();
            summaryController.create(env);
            summaryController.loadStageMap();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Shows a page showing summary stats of the user's current project
     * @param tutor The name of the tutor whose stats are to be displayed
     */
    private void showTutorStats(String tutor) {

        env.getRootController().setHeader(tutor);
        FXMLLoader tutorStatsLoader = new FXMLLoader(getClass().getResource("/Views/TutorStats.fxml"));

        try {
            VBox stats = tutorStatsLoader.load();
            currentPage.setContent(stats);
            AnchorPane.setLeftAnchor(stats, 0.0);
            AnchorPane.setTopAnchor(stats, 0.0);
            AnchorPane.setBottomAnchor(stats, 0.0);
            AnchorPane.setRightAnchor(stats, 0.0);
            statsController = tutorStatsLoader.getController();

            statsController.create(env);
            statsController.displayGraphs(tutor, convert.toString(timeSlider.getValue()));
            statsController.updateBadgesDisplay();
            listView.getSelectionModel().select(tutor);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Converts the selected time period on the slider to textual form
     * @return A string containing the currently selected time slider value
     */
    public String getTimePeriod() {
        return convert.toString(timeSlider.getValue());
    }

}

///** ticks according to a tempo in beats per minute controlled by the associated pulsar. */
//class Metronome {
//    private final AudioClip tick = new AudioClip("http://www.denhaku.com/r_box/sr16/sr16perc/losticks.wav");
//    private final Pulsar pulsar;
//
//    public Metronome(final double initialTempo) {
//        // the first time the audioclip is played, there is a delay before you hear it,
//        // so play with zero volume now as to make sure it is ready to play when straight away when needed.
//        tick.play(0);
//
//        pulsar = new Pulsar(initialTempo, new EventHandler<ActionEvent>() {
//            @Override public void handle(ActionEvent actionEvent) {
//                tick.play();
//            }
//        });
//    }
//
//    public Pulsar getPulsar() {
//        return pulsar;
//    }
//}
//
///** handles events according to a tempo in beats per minute. */
//class Pulsar {
//    private final DoubleProperty tempo    = new SimpleDoubleProperty(100);
 //       private final Timeline timeline = new Timeline();
//
//    public Pulsar(final double initialTempo, final EventHandler<ActionEvent> pulseHandler) {
//        timeline.setCycleCount(Animation.INDEFINITE);
//        timeline.getKeyFrames().setAll(
//                new KeyFrame(Duration.ZERO,       pulseHandler),
//                new KeyFrame(Duration.minutes(1), null)
//        );
//        timeline.rateProperty().bind(tempo);
//
//
//    public void start() {
//        timeline.play();
//    }
//
//    public void stop() {
//        timeline.stop();
//    }
//}
