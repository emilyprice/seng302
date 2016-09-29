package seng302.gui;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import seng302.Environment;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Settings Page shell controller. In charge of opening and all settings pages.
 */
public class BaseSettingsController {
    Environment env;

    @FXML
    private UserSettingsController userSettingsC;

    @FXML
    private UISkinnerController themeC;

    private ProjectSettingsController projectSettingsController;

    private TermsSettingsController termsSettingsController;

    private FXMLLoader userSettingsLoader, themeLoader, projectSettingsLoader;

    private MicInputSettingsController micInputSettingsController;


    @FXML
    private AnchorPane settingsPane;

    @FXML
    JFXListView settingsOptions;


    @FXML
    AnchorPane settingsWindow;


    @FXML
    private void initialize() {
        //Load user settings controller.
        userSettingsLoader = new FXMLLoader();
        userSettingsLoader.setLocation(getClass().getResource("/Views/UserSettings.fxml"));
        userSettingsC = userSettingsLoader.getController();

        //Load theme controller
        themeLoader = new FXMLLoader();
        themeLoader.setLocation(getClass().getResource("/Views/UISkinner.fxml"));
        themeC = themeLoader.getController();

    }

    /**
     * Used sort of as a constructor to pass through all necessary data to be used by the
     * baseSettings.
     *
     * @param env Program environment.
     */
    public void create(Environment env) {
        this.env = env;
        populateListView();
        openUserSettings();
        applyTheme();


    }

    public void closeWindow() {
        if(settingsWindow.getScene().getWindow().isShowing()){
            ((Stage) settingsWindow.getScene().getWindow()).close();
        }

    }

    /**
     * Applies the theme to the settings window.
     */
    private void applyTheme() {
        //Apply user theme
        env.getThemeHandler().setBaseNode(settingsPane);
        String[] themeColours = env.getUserHandler().getCurrentUser().getThemeColours();
        env.getThemeHandler().setTheme(themeColours[0], themeColours[1]);
    }

    /**
     * Populates the side menu with settings options.
     */
    private void populateListView() {

        ArrayList<String> options = new ArrayList<>();
        options.add("User Settings");
        options.add("Theme Settings");
        options.add("Project Settings");
        options.add("Microphone Input");
        options.add("Musical Terms");

        settingsOptions.getItems().addAll(FXCollections.observableArrayList(options));

        settingsOptions.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.launchSettings((String) newValue);

        });
        settingsOptions.getSelectionModel().selectFirst();
        settingsOptions.setDepthProperty(1);


        settingsOptions.setMaxWidth(200);
        settingsOptions.setMinWidth(200);

    }

    /**
     * Launches the settings page
     *
     * @param settings settings identifier (User Settings/Theme Settings)
     */
    private void launchSettings(String settings) {

        switch (settings) {
            case "User Settings":
                openUserSettings();
                break;

            case "Theme Settings":
                onThemeSettings();
                break;


            case "Project Settings":
                openProjectSettings();
                break;

            case "Microphone Input":
                openMicInput();
                break;

            case "Musical Terms":
                openTermsSettings();
                break;
        }

    }

    /**
     * Displays the user settings pane.
     */
    @FXML
    void openUserSettings() {

        try {

            userSettingsLoader = new FXMLLoader();
            userSettingsLoader.setLocation(getClass().getResource("/Views/UserSettings.fxml"));
            Node loadedPane = (Node) userSettingsLoader.load();
            settingsPane.getChildren().setAll(loadedPane);
            this.setAnchors(loadedPane);
            userSettingsC = userSettingsLoader.getController();
            userSettingsC.create(env);


        } catch (IOException e) {
            e.printStackTrace();

        }


    }

    /**
     * Displays the project settings view where the user will be able to change their instrument,
     * tempo, rhythm, and mode.
     */
    private void openProjectSettings() {
        try {
            projectSettingsLoader = new FXMLLoader();
            projectSettingsLoader.setLocation(getClass().getResource("/Views/ProjectSettings.fxml"));
            Node loadedPane = (Node) projectSettingsLoader.load();
            settingsPane.getChildren().setAll(loadedPane);
            this.setAnchors(loadedPane);
            projectSettingsController = projectSettingsLoader.getController();
            projectSettingsController.create(env);


        } catch (IOException e) {
            e.printStackTrace();

        }

    }


    /**
     * Displays the project settings view where the user will be able to change their instrument,
     * tempo, rhythm, and mode.
     */
    private void openMicInput() {
        try {
            FXMLLoader micSettingsLoader = new FXMLLoader();
            micSettingsLoader.setLocation(getClass().getResource("/Views/MicInputSettings.fxml"));
            Node loadedPane = (Node) micSettingsLoader.load();
            settingsPane.getChildren().setAll(loadedPane);
            this.setAnchors(loadedPane);
            micInputSettingsController = micSettingsLoader.getController();
            micInputSettingsController.create(env);

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    /**
     * Displays the page containing musical terms and information about them
     */
    private void openTermsSettings() {
        try {
            FXMLLoader termsLoader = new FXMLLoader();
            termsLoader.setLocation(getClass().getResource("/Views/TermsSettings.fxml"));
            Node loadedPane = (Node) termsLoader.load();
            settingsPane.getChildren().setAll(loadedPane);
            this.setAnchors(loadedPane);
            termsSettingsController = termsLoader.getController();
            termsSettingsController.create(env);

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    /**
     * Makes it so the given node is resized to completely fill the settings AnchorPane.
     *
     * @param loadedPane subject node to which is resized to fill the settingsPnae.
     */
    private void setAnchors(Node loadedPane) {
        settingsPane.setRightAnchor(loadedPane, 0.0);
        settingsPane.setLeftAnchor(loadedPane, 0.0);
        settingsPane.setBottomAnchor(loadedPane, 0.0);
        settingsPane.setTopAnchor(loadedPane, 0.0);
    }

    /**
     * Loads the themeSettings view and opens it inside the settings pane.
     */
    @FXML
    void onThemeSettings() {
        try {
            themeLoader = new FXMLLoader();
            themeLoader.setLocation(getClass().getResource("/Views/UISkinner.fxml"));
            Node loadedPane = (Node) themeLoader.load();

            settingsPane.getChildren().setAll(loadedPane);
            this.setAnchors(loadedPane);
            themeC = themeLoader.getController();
            themeC.create(env, env.getRootController().paneMain);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
