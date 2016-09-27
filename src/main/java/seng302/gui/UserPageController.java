package seng302.gui;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;

import java.awt.*;
import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;


import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import seng302.Environment;
import seng302.data.Badge;
import seng302.managers.BadgeManager;

/**
 * Handles and Creates Users.
 */
public class UserPageController {


    @FXML
    AnchorPane contentPane;

    @FXML
    VBox summaryPage;

    @FXML
    AnchorPane scrollPaneAnchorPage;

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

    private UserSummaryController summaryController;

    private TutorStatsController basicStatsController;

    @FXML
    VBox tutors;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    AnchorPane summary;

    private Environment env;


    public void setEnvironment(Environment env) {
        this.env = env;
        this.env.setUserPageController(this);

    }


    /**
     * Pretty much a constructor - loads userPage relevant data.
     */
    protected void load() {
        populateUserOptions();


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

            if(newValue != null) {
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
                        if (!tutor.equals("Summary") && env.stageMapController.unlockStatus.get(env.stageMapController.converted.get(tutor)) == false ) {

                            if(tutor.equals("Scale Recognition Tutor") || tutor.equals("Chord Recognition Tutor") ){
                                if(env.stageMapController.unlockStatus.get(env.stageMapController.converted.get(tutor + " (Basic)")) == true){

                                }else {
                                    setGraphic(new ImageView(lockImg));
                                    setTextFill(Color.GRAY);
                                    setText(tutor);
                                    setDisable(true);
                                }
                            }else {

                                setGraphic(new ImageView(lockImg));
                                setTextFill(Color.GRAY);
                                setText(tutor);
                                setDisable(true);
                            }
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
                updateGraphs();
            }
        }));

        timeSlider.setOnMouseReleased(e -> {
            updateGraphs();
        });
    }

    /**
     * Updates the data in the summary stats graphs
     */
    public void updateGraphs() {
        statsController.displayGraphs((String) listView.getSelectionModel().getSelectedItem(), convert.toString(timeSlider.getValue()));
        basicStatsController.displayGraphs(listView.getSelectionModel().getSelectedItem() + " (Basic)", convert.toString(timeSlider.getValue()));
    }
    private void updateGraphs(String timePeriod) {
        statsController.displayGraphs((String) listView.getSelectionModel().getSelectedItem(), timePeriod);
        basicStatsController.displayGraphs(listView.getSelectionModel().getSelectedItem() + " (Basic)", timePeriod);
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
     * Displays the page containing summary information about the user's current project
     */
    public void showSummaryPage() {
        env.getRootController().setHeader("Summary");
        listView.getSelectionModel().selectFirst();

        FXMLLoader summaryLoader = new FXMLLoader(getClass().getResource("/Views/UserSummary.fxml"));

        try {
            FlowPane summaryPage = summaryLoader.load();
            //currentPage.setContent(summaryPage);

            scrollPaneAnchorPage.getChildren().setAll(summaryPage);
            AnchorPane.setLeftAnchor(summaryPage, 0.0);
            AnchorPane.setTopAnchor(summaryPage, 0.0);
            AnchorPane.setBottomAnchor(summaryPage, 0.0);
            AnchorPane.setRightAnchor(summaryPage, 0.0);
            //summaryPage.setMinWidth(currentPage.getWidth());

            summaryController = summaryLoader.getController();
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
        VBox all = new VBox();

        try {
            VBox stats = tutorStatsLoader.load();
            //currentPage.setContent(stats);
            all.getChildren().setAll(stats);
            scrollPaneAnchorPage.getChildren().clear();
            //scrollPaneAnchorPage.getChildren().setAll(all);
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

        if(tutor.equals("Scale Recognition Tutor") || tutor.equals("Chord Recognition Tutor") ){
            FXMLLoader tutorbasicStatsLoader = new FXMLLoader(getClass().getResource("/Views/TutorStats.fxml"));
            try {
                VBox stats = tutorbasicStatsLoader.load();
                all.getChildren().add(stats);
                //scrollPaneAnchorPage.getChildren().setAll(all);
                AnchorPane.setLeftAnchor(stats, 0.0);
                AnchorPane.setTopAnchor(stats, 0.0);
                AnchorPane.setBottomAnchor(stats, 0.0);
                AnchorPane.setRightAnchor(stats, 0.0);
                basicStatsController = tutorbasicStatsLoader.getController();

                basicStatsController.create(env);
                basicStatsController.displayGraphs( tutor + " (Basic)", convert.toString(timeSlider.getValue()));


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        scrollPaneAnchorPage.getChildren().setAll(all);

        listView.getSelectionModel().select(tutor);

    }

    /**
     * Converts the selected time period on the slider to textual form
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
