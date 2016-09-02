package seng302.gui;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import seng302.Environment;

import java.io.IOException;

/**
 * Created by Jonty on 01-Sep-16.
 */
public class BaseSettingsController {
    Environment env;

    @FXML
    UserSettingsController userSettingsC;

    @FXML
    UISkinnerController themeC;

    FXMLLoader userSettingsLoader, themeLoader;


    @FXML
    private VBox sidePane;

    @FXML
    private AnchorPane settingsPane;

    @FXML
    private JFXButton btnProjectSettings;

    @FXML
    private JFXButton btnUserSettings;
    @FXML
    private JFXButton btnThemeSettings;


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

    public void create(Environment env) {
        this.env = env;
        //Moved the below logic from the initialize function so that env is instantiated when calling
        //The inner 'create' functions.


    }

    @FXML
    void openUserSettings(ActionEvent event) {

        try {

            userSettingsLoader = new FXMLLoader();
            userSettingsLoader.setLocation(getClass().getResource("/Views/UserSettings.fxml"));

            Node loadedPane = (Node) userSettingsLoader.load();
            settingsPane.getChildren().setAll(loadedPane);
            this.setAnchors(loadedPane);

            userSettingsC = userSettingsLoader.getController();
            userSettingsC.create(env);

            btnUserSettings.setStyle(String.format("-fx-background-color: %s", env.getThemeHandler().getPrimaryColour()));
            btnThemeSettings.setStyle("");
            btnProjectSettings.setStyle("");

        } catch (IOException e) {
            e.printStackTrace();

        }

        //UserSettingsTabController = loader.getController();
        //UserSettingsTabController.create(env);

    }

    private void setAnchors(Node loadedPane) {
        settingsPane.setRightAnchor(loadedPane, 0.0);
        settingsPane.setLeftAnchor(loadedPane, 0.0);
        settingsPane.setBottomAnchor(loadedPane, 0.0);
        settingsPane.setTopAnchor(loadedPane, 0.0);
    }

    @FXML
    void onThemeSettings(ActionEvent event) {
        try {

            //settingsPane.setContent((Node) loader.load());
//            themeLoader.setRoot(null);
//            themeLoader.setController(themeC);
//            settingsPane.getChildren().setAll((Node) themeLoader.load());

            themeLoader = new FXMLLoader();
            themeLoader.setLocation(getClass().getResource("/Views/UISkinner.fxml"));
            Node loadedPane = (Node) themeLoader.load();

            settingsPane.getChildren().setAll(loadedPane);

            this.setAnchors(loadedPane);

            themeC = themeLoader.getController();

            themeC.create(env, env.getRootController().paneMain);
            btnThemeSettings.setStyle(String.format("-fx-background-color: %s", env.getThemeHandler().getPrimaryColour()));
            btnProjectSettings.setStyle("");
            btnUserSettings.setStyle("");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openProjectSettings(ActionEvent event) {


        btnProjectSettings.setStyle(String.format("-fx-background-color: %s", env.getThemeHandler().getPrimaryColour()));
        // btnProjectSettings.setStyle("-fx-background-color: #223768");
        btnThemeSettings.setStyle("");
        btnUserSettings.setStyle("");
    }


}
