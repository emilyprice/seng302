package seng302.gui;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;

import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;
import seng302.Environment;

/**
 * Handles and Creates Users.
 */
public class UserPageController {


    @FXML
    AnchorPane contentPane;

    @FXML
    VBox summaryPage;

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

    private Slider timeSlider;

    StringConverter convert;

    private TutorStatsController statsController;

    @FXML
    VBox tutors;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private JFXButton timeSliderButton;

    @FXML
    AnchorPane summary;

    private PopOver timePopover;

    private Environment env;

    private UserSummaryController summaryController;


    public void setEnvironment(Environment env) {
        this.env = env;
        this.env.setUserPageController(this);
        //setupTimeSlider();
    }


    /**
     * Pretty much a constructor - loads userPage relevant data.
     */
    protected void load() {
        populateUserOptions();

        Circle imageClip = new Circle(50, 50, 50);
        imageDP2.setClip(imageClip);
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

        Image lockImg = new Image(getClass().getResourceAsStream("/images/locked-padlock.png"), 20, 20, false, false);

        listView.getItems().addAll(FXCollections.observableArrayList(options));


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
                    if (env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getIsCompetitiveMode() && !tutor.equals("Summary") && !env.stageMapController.unlockStatus.get(env.stageMapController.converted.get(tutor))) {
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
                    } else {
                        setGraphic(null);
                        setText(tutor);
                        setPadding(new Insets(0, 0, 0, 40));
                        setAlignment(Pos.CENTER_LEFT);
                        setDisable(false);
                    }
                        }
                    }
                }

        );

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
    private void updateGraphs(String timePeriod) {
        if (env.getRootController().getHeader().equals("Summary")) {
            summaryController.updateGraphs();
        } else {
            statsController.displayGraphs((String) listView.getSelectionModel().getSelectedItem(), timePeriod);
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
    void openSettings() {
        env.getRootController().launchSettings();
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

            summaryController = summaryLoader.getController();
            summaryController.create(env);
            summaryController.loadStageMap();


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
     *
     * @return A string containing the currently selected time slider value
     */
    public String getTimePeriod() {
        return convert.toString(timeSlider.getValue());
    }

}
