package seng302.gui;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;

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

import javafx.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.util.Pair;
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
    SplitPane userView;

    @FXML
    JFXListView listView;
    
    @FXML
    VBox badgesVBox;

    @FXML
    StackedBarChart stackedBar;

    @FXML
    private JFXButton btnSettings;


    @FXML
    Label txtFullName;

    @FXML
    ImageView imageDP2;
    
    @FXML
    ImageView lockView;

    @FXML
    ColorAdjust blackout;

    @FXML
    Label chartTitle;

    @FXML
    JFXBadge levelBadge;
    
    @FXML
    GridPane badgeGrid;

    @FXML
    LineChart lineChart;

    @FXML
    Label latestAttempt;

    @FXML
    ScrollPane currentPage;

    @FXML
    private Slider timeSlider;

    StringConverter convert;

    private TutorStatsController statsController;
    private int gridCount;

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

        menuItemLogout.setOnAction(k -> env.getRootController().logOutUser());

        menuItemSettings.setOnAction(e2 -> env.getRootController().launchSettings());
        ContextMenu settingsDropDown = new ContextMenu();
        settingsDropDown.getItems().addAll(menuItemSettings,menuItemLogout);

        settingsDropDown.setStyle("-fx-background-radius: 0 6 6 6, 0 5 5 5, 0 4 4 4;");

        btnSettings.setContextMenu(settingsDropDown);

        settingsDropDown.show(btnSettings, e.getScreenX(), e.getScreenY());


    }

//
//            makeLineGraph(dateAndTime);
//            updateBadgesDisplay();
//        }
//    }

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
