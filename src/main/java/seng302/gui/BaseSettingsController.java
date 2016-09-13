package seng302.gui;

import com.jfoenix.controls.JFXButton;

import java.io.IOException;
import java.util.ArrayList;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import seng302.Environment;


/**
 * Settings Page shell controller. In charge of opening and all settings pages.
 */
public class BaseSettingsController {
    Environment env;

    @FXML
    private UserSettingsController userSettingsC;

    @FXML
    private UISkinnerController themeC;

    private FXMLLoader userSettingsLoader, themeLoader;


    @FXML
    private AnchorPane settingsPane;

    @FXML
    private JFXButton btnUserSettings;
    @FXML
    private JFXButton btnThemeSettings;

    @FXML
    JFXListView settingsOptions;


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
     * Used sort of as a constructor to pass through all necessary data to be used by the baseSettings.
     * @param env Program environment.
     */
    public void create(Environment env) {
        this.env = env;
        populateListView();
        openUserSettings();



    }

    private void populateListView(){

        ArrayList<String> options = new ArrayList<>();
        options.add("User Settings");
        options.add("Theme Settings");

        settingsOptions.getItems().addAll(FXCollections.observableArrayList(options));

        settingsOptions.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.launchSettings((String) newValue);

        });
        settingsOptions.getSelectionModel().selectFirst();
        settingsOptions.setDepthProperty(1);


        settingsOptions.setMaxWidth(200);
        settingsOptions.setMinWidth(200);

    }

    private void launchSettings(String settings){

        switch(settings){
            case "User Settings":
                openUserSettings();
                break;

            case "Theme Settings":
                onThemeSettings();
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

    /**
     *  Loads the project settings view and opens it inside the settings pane.
     *
     */
//    @FXML
//    void openProjectSettings() {
//
//
//        btnProjectSettings.setStyle(String.format("-fx-background-color: %s", env.getThemeHandler().getPrimaryColour()));
//        // btnProjectSettings.setStyle("-fx-background-color: #223768");
//        btnThemeSettings.setStyle("");
//        btnUserSettings.setStyle("");
//    }


}
