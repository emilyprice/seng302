package seng302.gui;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;

import java.awt.*;
import java.awt.TextField;
import java.io.IOException;
import java.awt.*;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import javafx.scene.layout.HBox;
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
        VBox metronomePopOver = new VBox();

        //Hbox to contain label stating current BPM
        HBox tempoLabelBox = new HBox();


        //HBox to contain metronome
        HBox metronome = new HBox();

        //Hbox to contain text box that allows user to change tempo
        HBox changeTempo = new HBox();
        JFXButton setTempo = new JFXButton("Set tempo");

        tempoInput.setPrefColumnCount(4); //setting col size (user can input 4 characters)
        changeTempo.getChildren().add(tempoInput);
        changeTempo.getChildren().add(setTempo);
        changeTempo.setSpacing(10);
        changeTempo.setPadding(new Insets(10));


        //HBox to contain buttons (Start, Pause, Resume, Stop)
        HBox metronomeButtons = new HBox();


        //Buttons
        JFXButton startBtn = new JFXButton("Start");
        startBtn.getStyleClass().add("primary");
        JFXButton pauseBtn = new JFXButton("Pause");
        pauseBtn.getStyleClass().add("primary");
        JFXButton resumeBtn = new JFXButton("Resume");
        resumeBtn.getStyleClass().add("primary");
        JFXButton stopBtn = new JFXButton("Stop");
        stopBtn.getStyleClass().add("primary");

        metronomeButtons.getChildren().add(startBtn);
        metronomeButtons.getChildren().add(pauseBtn);
        metronomeButtons.getChildren().add(resumeBtn);
        metronomeButtons.getChildren().add(stopBtn);
        metronomeButtons.setSpacing(10);
        metronomeButtons.setPadding(new Insets(10));

        Integer currentTempo = env.getPlayer().getTempo();
        tempoLabel.setText("The current tempo is set to " + currentTempo + " BPM");
        tempoInput.setText(currentTempo.toString());
        tempoLabelBox.getChildren().add(tempoLabel);

        metronomePopOver.getChildren().add(tempoLabelBox);
        metronomePopOver.getChildren().add(metronome);
        metronomePopOver.getChildren().add(changeTempo);
        metronomePopOver.getChildren().add(metronomeButtons);



        // used the spacing etc from settings to see if it will come out nicely. Subject to change
        metronomePopOver.setSpacing(10);
        metronomePopOver.setPadding(new Insets(10));

        //Declaring the popover
        metronomePop = new PopOver(metronomePopOver);
        metronomePop.setTitle("Metronome");
    }

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
