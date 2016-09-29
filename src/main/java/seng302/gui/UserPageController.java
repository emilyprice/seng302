package seng302.gui;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import javafx.scene.control.Dialog;
import javafx.scene.control.Alert;


import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import javafx.animation.Interpolator;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.action.Action;
import seng302.Environment;

import java.io.IOException;
import java.util.ArrayList;

import static javafx.scene.paint.Color.RED;

/**
 * Handles and Creates Users.
 */
public class UserPageController {

    private PopOver metronomePop;

    @FXML
    private Button metroButton;


    @FXML
    AnchorPane scrollPaneAnchorPage;

    @FXML
    SplitPane userView;

    @FXML
    public JFXListView listView;


    @FXML
    Label txtFullName;

    @FXML
    ImageView imageDP2;

    @FXML
    JFXBadge levelBadge;


    @FXML
    ScrollPane currentPage;


    private Circle ball; //bouncing ball for metronome


    @FXML
    private Slider timeSlider;

    StringConverter convert;

    private TutorStatsController statsController;

    private TutorStatsController basicStatsController;

    @FXML
    VBox tutors;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private JFXButton timeSliderButton;


    @FXML
    JFXButton logoutButton;

    private PopOver timePopover;

    private Environment env;

    public Label tempoLabel = new Label();

    private TranslateTransition anim;

    private Boolean mute;

    private TextField tempoInput = new TextField();

    private UserSummaryController summaryController;


    public void setEnvironment(Environment env) {
        this.env = env;
        this.env.setUserPageController(this);
    }


    /**
     * Pretty much a constructor - loads userPage relevant data.
     */
    protected void load() {
        populateUserOptions();
        setupMetronomePopOver();
        updateProfilePicDisplay();
        updateLevelBadge();
        updateNameDisplay();
    }

    /**
     * Sets profile pic to user picture (in a circle)
     */
    public void updateProfilePicDisplay() {
        Circle imageClip = new Circle(50, 50, 50);
        imageDP2.setClip(imageClip);
        imageDP2.setImage(env.getUserHandler().getCurrentUser().getUserPicture());
    }


    /**
     * Sets name under profile pic to display the user's name or username if they have no name.
     */
    public void updateNameDisplay() {
        try {

            txtFullName.setText(env.getUserHandler().getCurrentUser().getUserFirstName() + " "
                    + env.getUserHandler().getCurrentUser().getUserLastName());
        } catch (NullPointerException e) {
            txtFullName.setText(env.getUserHandler().getCurrentUser().getUserName());
            //txtFullName not initialized yet.
        }
    }

    @FXML
    public void onLogoutClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out Confirmation");
        alert.setContentText("Are you sure you want to log out?");
        Optional<ButtonType> result = alert.showAndWait();
        if ((result.isPresent()) && (result.get() == ButtonType.OK)) {
            env.getRootController().logOutUser();
        } else {
            alert.close();
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
        options.add("Microphone Input Tutor");
        options.add("Pitch Comparison Tutor");
        options.add("Scale Recognition Tutor");
        options.add("Chord Recognition Tutor");
        options.add("Interval Recognition Tutor");
        options.add("Chord Spelling Tutor");
        options.add("Scale Spelling Tutor");
        options.add("Key Signature Tutor");
        options.add("Diatonic Chord Tutor");
        options.add("Scale Modes Tutor");

        Image lockImg = new Image("/images/lock.png", 20, 20, true, true);
        listView.getItems().clear();
        listView.getItems().addAll(FXCollections.observableArrayList(options));


        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (((String) newValue).equals("Scale Recognition Tutor (Basic)")) {
                    showPage("Scale Recognition Tutor");
                } else if (((String) newValue).equals("Chord Recognition Tutor (Basic)")) {
                    showPage("Chord Recognition Tutor");
                } else {
                    showPage((String) newValue);
                }
            }
        });

        // Set after the listener so it loads user summary correctly
        listView.getSelectionModel().selectFirst();


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

                            if (tutor.equals("Scale Recognition Tutor") || tutor.equals("Chord Recognition Tutor")) {
                                if (env.stageMapController.unlockStatus.get(env.stageMapController.converted.get(tutor + " (Basic)")) == true) {

                                } else {
                                    ImageView lock = new ImageView(lockImg);
                                    StackPane image = new StackPane();
                                    image.setPadding(new Insets(0, 5, 0, 0));
                                    image.getChildren().add(lock);
                                    setGraphic(image);
                                    setTextFill(Color.GRAY);
                                    setText(tutor);
                                    setAlignment(Pos.CENTER_LEFT);
                                    setPadding(new Insets(0, 0, 0, 10));
                                    setDisable(true);
                                }
                            } else {
                                ImageView lock = new ImageView(lockImg);
                                StackPane image = new StackPane();
                                image.setPadding(new Insets(0, 5, 0, 0));
                                image.getChildren().add(lock);
                                setGraphic(image);
                                setTextFill(Color.GRAY);
                                setText(tutor);
                                setAlignment(Pos.CENTER_LEFT);
                                setPadding(new Insets(0, 0, 0, 10));
                                setDisable(true);

                            }
                        }


                    } else {
                        setGraphic(null);
                        setText(tutor);
                        setPadding(new Insets(0, 0, 0, 40));
                        setAlignment(Pos.CENTER_LEFT);
                        setDisable(false);
                    }
                }
            }
        });
    }

    @FXML
    private void showTimeSlider() {
        if (timePopover.isShowing()) {
            timePopover.hide();
        } else {
            timePopover.show(timeSliderButton);
        }
    }



    /**
     * Creates the GUI time slider. This allows the user to select a specific time period to view
     * tutor results from.
     */
    private void setupTimeSlider() {
        timePopover = new PopOver();
        timePopover.setTitle("Time Range");
        timeSlider = new Slider(0, 5, 5);
        timeSlider.setShowTickLabels(true);
        timeSlider.setMajorTickUnit(1.0);
        timeSlider.setShowTickMarks(true);
        timeSlider.minorTickCountProperty().setValue(0);
        timeSlider.snapToTicksProperty().setValue(true);
        timeSlider.blockIncrementProperty().setValue(1.0);
        timeSlider.setOrientation(Orientation.VERTICAL);
        VBox timeContent = new VBox();
        timeContent.setPadding(new Insets(20));
        timeContent.setSpacing(10);
        Label timeLabel = new Label("Adjust the time range displayed on the graphs:");
        timeContent.getChildren().add(timeLabel);
        timeContent.getChildren().add(timeSlider);
        timePopover.setContentNode(timeContent);

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
    public void updateGraphs(String timePeriod) {
        if (env.getRootController().getHeader().equals("Summary")) {
            summaryController.updateGraphs();
        } else {
            statsController.displayGraphs((String) listView.getSelectionModel().getSelectedItem(), timePeriod);
            basicStatsController.displayGraphs(listView.getSelectionModel().getSelectedItem() + " (Basic)", timePeriod);
        }
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
     * OnClick action for the UserPage settings button. Opens a context menu with settings/logout
     * options
     */
    @FXML
    public void openSettings() {
        try {
            if (!env.getRootController().settingsStage.isShowing()) {
                env.getRootController().launchSettings();
            } else {
                env.getRootController().settingsStage.toFront();
            }
        } catch (NullPointerException e) {
            env.getRootController().launchSettings();
        }
    }

    /**
     * Hides and shows the metronome popover when the metronome button is selected
     */
    @FXML
    public void openMetronome() {
        if (metronomePop.isShowing()) {
            metronomePop.hide();
        } else {
            metronomePop.show(metroButton);
        }
    }


    /**
     * OnClick action for the UserPage metronome button. Opens a popover that contains the
     * metronome
     */
    public void setupMetronomePopOver() {

        //Goes inside metronome popover
        VBox metronomeVBox = new VBox();
        metronomeVBox.setMinSize(270, 145);
        metronomeVBox.setMaxSize(270, 175);

        //Hbox to contain label stating current BPM
        HBox tempoLabelBox = new HBox();


        //HBox to contain metronome
        HBox metronome = new HBox();

        //Hbox to contain text box that allows user to change tempo
        HBox changeTempo = new HBox();
        JFXButton setTempo = new JFXButton("Set tempo");
        setTempo.getStyleClass().add("primary");
        JFXButton muteMetronome = new JFXButton("Mute");
        muteMetronome.getStyleClass().add("primary");
        muteMetronome.setMinSize(80, 30);
        setTempo.setMinSize(80, 30);

        //HBox that will contain error message for if the tempo range is exceeded
        HBox errorLabelBox = new HBox();
        Label errorLabel = new Label("Tempo is outside of appropriate range");
        errorLabel.setTextFill(RED);
        errorLabelBox.getChildren().add(errorLabel);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        muteMetronome.setOnAction(e -> {
            if (muteMetronome.getText().equals("Mute")) {
                mute = true;
                muteMetronome.setText("Unmute");
            } else {
                mute = false;
                muteMetronome.setText("Mute");
            }
        });

        tempoInput.setPrefColumnCount(3); //setting col size (user can input 3 characters)
        changeTempo.getChildren().add(tempoInput);
        changeTempo.getChildren().add(setTempo);
        changeTempo.getChildren().add(muteMetronome);
        changeTempo.setSpacing(10);
        changeTempo.setPadding(new Insets(5));

        Integer currentTempo = env.getPlayer().getTempo();
        tempoLabel.setText("The current tempo is set to " + currentTempo + " BPM");
        tempoInput.setText(((Integer) currentTempo).toString());
        tempoLabelBox.getChildren().add(tempoLabel);

        metronome.getChildren().add(metronomeAnimation()); //adds AnchorPane with animation to HBox metronome
        metronomeVBox.getChildren().add(tempoLabelBox);
        metronomeVBox.getChildren().add(metronome);
        metronomeVBox.getChildren().add(changeTempo);
        metronomeVBox.getChildren().add(errorLabelBox);


        setTempo.setOnAction(event -> {
            if (Integer.valueOf(tempoInput.getText()) >= 20 && Integer.valueOf(tempoInput.getText()) <= 300) {
                env.getPlayer().setTempo(Integer.valueOf(tempoInput.getText()));
                tempoLabel.setText("The current tempo is set to " + tempoInput.getText() + " BPM");
                anim.setDuration(Duration.millis((60 / Float.valueOf(tempoInput.getText())) * 1000));
                anim.playFromStart();
                tempoInput.setStyle("-fx-border-color: lightgray;");
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);

                //else if the user tries to input a tempo value outside of appropriate range
            } else {
                tempoInput.setStyle("-fx-border-color: red;"); //text border will set red
                errorLabel.setVisible(true); //label will display
                errorLabel.setManaged(true);
                errorLabel.setStyle("-fx-text-color: red;");
            }
        });


        // used the spacing etc from settings to see if it will come out nicely. Subject to change
        metronomeVBox.setSpacing(10);
        metronomeVBox.setPadding(new Insets(10));

        //Declaring the popover
        metronomePop = new PopOver(metronomeVBox);
        metronomePop.headerAlwaysVisibleProperty().setValue(true);
        metronomePop.setTitle("Metronome");

        //ensures the metronome stops playing when the popout is not showing
        metronomePop.showingProperty().addListener((o, old, newValue) -> {
            if (newValue) {
                anim.playFromStart();

            } else {
                anim.pause();

            }
        });
    }


    /**
     * Creates the metronome animation of a bouncing ball in an AnchorPane, and plays back the sound
     * when the animation makes contact with "start point"
     *
     * @return AnchorPane containing animation
     */
    private AnchorPane metronomeAnimation() {
        ball = new Circle();
        AnchorPane animationPane = new AnchorPane(); //pane to contain animation
        animationPane.setPrefSize(250, 50);
        animationPane.setMinSize(250, 50);
        ball.getStyleClass().add("primary"); //make the ball match the theme

        ball.setCenterX(20);
        ball.setCenterY(25);
        ball.setRadius(4);

        anim = new TranslateTransition(Duration.millis((60 / Float.valueOf(tempoInput.getText())) * 1000), ball);
        anim.setFromX(10);
        anim.setToX(200);
        anim.setInterpolator(Interpolator.LINEAR);
        anim.setAutoReverse(true);
        anim.setCycleCount(Timeline.INDEFINITE);
        animationPane.getChildren().add(ball);

        mute = false;
        initializeTickSound();

        return animationPane;
    }

    /**
     * Toggles the ticking sound of the metronome
     */
    private void initializeTickSound() {

        final AudioClip tickSound = new AudioClip("http://www.denhaku.com/r_box/sr16/sr16perc/losticks.wav"); //metronome tick sound
        ChangeListener<Number> tick = (observable, oldValue, newValue) -> {

            //if not on mute, play tick sound
            if (!mute) {
                if (newValue.equals(10.0) || newValue.equals(200.0)) {
                    tickSound.play();
                }
            }
        };
        ball.translateXProperty().addListener(tick);
    }

    /**
     * Updates the tempo to the current set tempo
     */
    public void updateCurrentTempo(Number newValue) {
        tempoLabel.setText("The current tempo is set to " + String.valueOf(newValue.intValue()) + " BPM");
        tempoInput.setText(String.valueOf(newValue.intValue()));
        updateMetronome();
    }

    /**
     * Updates the metronome animation to be in sync with the current tempo
     */
    public void updateMetronome() {
        anim.setDuration(Duration.millis((60 / Float.valueOf(tempoInput.getText())) * 1000));
        anim.playFromStart();

    }


    /**
     * Displays the page containing summary information about the user's current project
     */
    public void showSummaryPage() {
        env.getRootController().setHeader("Summary");
        listView.getSelectionModel().selectFirst();

        FXMLLoader summaryLoader = new FXMLLoader(getClass().getResource("/Views/UserSummary.fxml"));

        try {
            GridPane summaryPage = summaryLoader.load();

            currentPage.setContent(summaryPage);
            AnchorPane.setLeftAnchor(summaryPage, 0.0);
            AnchorPane.setTopAnchor(summaryPage, 0.0);
            AnchorPane.setBottomAnchor(summaryPage, 0.0);
            AnchorPane.setRightAnchor(summaryPage, 0.0);

            summaryController = summaryLoader.getController();
            summaryController.create(env);
            summaryController.loadStageMap();
            if (!env.getFirebase().getUserSnapshot().child("projects/" + env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().projectName + "/unlockMap").exists()) {
                env.getUserHandler().getCurrentUser().saveAll();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Shows a page showing summary stats of the user's current project
     *
     * @param tutor The name of the tutor whose stats are to be displayed
     */
    private void showTutorStats(String tutor) {

        env.getRootController().setHeader(tutor);
        FXMLLoader tutorStatsLoader = new FXMLLoader(getClass().getResource("/Views/TutorStats.fxml"));
        VBox all = new VBox();

        if (tutor.equals("Scale Recognition Tutor") || tutor.equals("Chord Recognition Tutor")) {
            FXMLLoader tutorbasicStatsLoader = new FXMLLoader(getClass().getResource("/Views/TutorStats.fxml"));
            try {
                VBox stats = tutorbasicStatsLoader.load();
                all.getChildren().add(stats);
                AnchorPane.setLeftAnchor(stats, 0.0);
                AnchorPane.setTopAnchor(stats, 0.0);
                AnchorPane.setBottomAnchor(stats, 0.0);
                AnchorPane.setRightAnchor(stats, 0.0);
                basicStatsController = tutorbasicStatsLoader.getController();

                basicStatsController.create(env);
                basicStatsController.displayGraphs(tutor + " (Basic)", convert.toString(timeSlider.getValue()));
                basicStatsController.updateBadgesDisplay();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if ((Boolean) (env.getStageMapController().getUnlockStatus().get(env.getStageMapController().converted.get(tutor)))) {
            try {
                VBox stats = tutorStatsLoader.load();
                all.getChildren().add(stats);
                currentPage.setContent(null);
                AnchorPane.setLeftAnchor(stats, 0.0);
                AnchorPane.setTopAnchor(stats, 0.0);
                AnchorPane.setBottomAnchor(stats, 0.0);
                AnchorPane.setRightAnchor(stats, 0.0);
                statsController = tutorStatsLoader.getController();

                statsController.create(env);
                statsController.displayGraphs(tutor, convert.toString(timeSlider.getValue()));
                statsController.updateBadgesDisplay();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        currentPage.setContent(all);

        listView.getSelectionModel().select(tutor);

    }

    /**
     * Converts the selected time period on the slider to textual form
     *
     * @return A string containing the currently selected time slider value
     */
    public String getTimePeriod() {
        return convert.toString(timeSlider.getValue());
    }

    public TutorStatsController getStatsController() {
        return statsController;
    }

    public UserSummaryController getSummaryController() {
        return summaryController;
    }

}
