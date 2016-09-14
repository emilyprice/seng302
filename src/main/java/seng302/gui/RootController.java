package seng302.gui;


import com.jfoenix.controls.JFXBadge;

import org.json.simple.JSONArray;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng302.Environment;
import seng302.managers.TranscriptManager;
import seng302.utility.FileHandler;
import seng302.utility.OutputTuple;


public class RootController implements Initializable {
    Environment env;
    TranscriptManager tm;
    Stage stage;

    String path;
    File fileDir;

    TutorFactory tutorFactory;

    @FXML
    Label txtHeader;


    @FXML
    HBox userBar;

    @FXML
    AnchorPane paneMain;

    @FXML
    SplitPane splitPane;

    @FXML
    SplitPane transcriptSplitPane;

    @FXML
    private BaseSettingsController settingsController;

    @FXML
    private KeyboardPaneController keyboardPaneController;

    @FXML
    private TranscriptPaneController transcriptPaneController;

    @FXML
    private MenuItem menuQuit;

    @FXML
    private Circle circleDP;

    @FXML
    private HBox hbUser;

    @FXML
    private JFXBadge levelBadge;

    @FXML
    private MenuItem menuOpen;

    @FXML
    private MenuItem menuSave;

    @FXML
    private MenuItem menuSaveCommands;

    @FXML
    private AnchorPane centerPane;

    @FXML
    private Menu menuOpenProjects;

    @FXML
    private Menu helpMenu;

    @FXML
    private HBox userDPBox;

    @FXML
    private ImageView imageDP;

    @FXML
    private MenuItem dslReferenceMenuItem;

    @FXML
    private MenuButton userDropDown;

    @FXML
    public void onTranscriptTab() {
        Platform.runLater(() -> transcriptPaneController.txtCommand.requestFocus());
    }

    @FXML
    public void showDslRef() {
        dslRefControl.getPopover().show(paneMain);
    }

    private DslReferenceController dslRefControl;

    public void initialize(URL location, ResourceBundle resources) {
        dslRefControl = new DslReferenceController(transcriptPaneController);

        String cssBordering = "-fx-border-color:dimgray ; \n" //#090a0c
                + "-fx-border-insets:3;\n"
                + "-fx-border-radius:1;\n"
                + "-fx-border-width:2.0";


        userDropDown.setEllipsisString("User");
        userDropDown.setText("User");

    }

    /**
     * Loads a new user image into a circular shape
     */
    public void updateImage() {
        updateLevelBadge();
        final Circle clip = new Circle(imageDP.getFitWidth() - 25.0, imageDP.getFitHeight() - 25.0, 50.0);
        imageDP.setImage(env.getUserHandler().getCurrentUser().getUserPicture());
        clip.setRadius(25.0);
        imageDP.setClip(clip);

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = imageDP.snapshot(parameters, null);

        imageDP.setClip(null);
        imageDP.setEffect(new DropShadow(5, Color.BLACK));

        imageDP.setImage(image);
        imageDP.setOnMouseClicked(event -> {

            try {
                showUserPage();
            } catch (Exception e) {

            }
        });
    }

    /**
     * Updates the level indicator badge to display the level of the user's current project
     */
    public void updateLevelBadge() {
        levelBadge.refreshBadge();
        levelBadge.setText(Integer.toString(env.getUserHandler().getCurrentUser().getUserLevel()));
    }


    /**
     * Display or hide the main GUI window.
     *
     * @param show Boolean indicating whether to show or hide the main window.
     */
    public void showWindow(Boolean show) {
        if (show) {

            applyTheme();
            stage.show();
            resizeSplitPane(1.0);
            updateImage();
            try {
                showUserPage();
            } catch (IOException e) {
                e.printStackTrace();
            }


        } else stage.hide();

    }

    /**
     * Apply the current user's theme to the main window.
     */
    private void applyTheme() {
        //Apply user theme
        env.getThemeHandler().setBaseNode(paneMain);
        String[] themeColours = env.getUserHandler().getCurrentUser().getThemeColours();
        env.getThemeHandler().setTheme(themeColours[0], themeColours[1]);
    }


    /**
     * Closes the application, if There are unsaved changes then it prompts the user to save the
     * file.
     */
    @FXML
    private void closeApplication() {
        if (env.getUserHandler().getCurrentUser() != null && !env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().isSaved()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Unsaved changes");

            ButtonType btnSaveProject = new ButtonType("Save project");

            ButtonType btnQuit = new ButtonType("Quit");
            ButtonType btnCancel = new ButtonType("Cancel");

            alert.getButtonTypes().setAll(btnSaveProject, btnQuit, btnCancel);

            String contentString = "Unsaved Project properties\n\nAre you sure that you would like to quit?";

            alert.setContentText(contentString);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == btnSaveProject) {
                env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().saveCurrentProject();
                System.exit(0);
            } else if (result.get() == btnQuit) {
                System.exit(0);
            }


        } else if (env.getTranscriptManager().unsavedChanges) {
            env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().saveCurrentProject();
            System.exit(0);
        } else {

            System.exit(0);
        }


    }

    @FXML
    private void showHideKeyboard() {
        keyboardPaneController.toggleHideKeyboard();
    }


    @FXML
    private void toggleShowKeyboardNotesAlways() {
        keyboardPaneController.toggleShowKeyboardNotesAlways();
    }

    @FXML
    private void toggleShowKeyboardNotesAction() {
        keyboardPaneController.toggleShowKeyboardNotesAction();
    }

    @FXML
    private void stopShowingNotesOnKeyboard() {
        keyboardPaneController.stopShowingNotesOnKeyboard();
    }


    /**
     * Updates the user menu button text to display the current user's name.
     */
    public void updateUserInfo(String name) {
        userDropDown.setEllipsisString(name);
        userDropDown.setText(name);
    }


    /**
     * Toggles the visibility of the top User HBox and user image.
     *
     * @param show true to show, false to hide.
     */
    public void showUserBar(Boolean show) {
        userBar.setVisible(show);
        userDPBox.setVisible(show);
        userBar.setManaged(show);
    }

    /**
     * Opens the user page.
     */
    public void showUserPage() throws IOException {
        showUserBar(false);
        setHeader("Summary");

        FXMLLoader userPageLoader = new FXMLLoader();
        userPageLoader.setLocation(getClass().getResource("/Views/UserPage.fxml"));


        AnchorPane userPage = userPageLoader.load();

        centerPane.getChildren().add(userPage);

        AnchorPane.setRightAnchor(userPage, 0.0);
        AnchorPane.setLeftAnchor(userPage, 0.0);
        AnchorPane.setBottomAnchor(userPage, 0.0);
        AnchorPane.setTopAnchor(userPage, 0.0);

        UserPageController userPageController = userPageLoader.getController();
        userPageController.setEnvironment(env);
        userPageController.load();

    }


    public void setHeader(String text) {
        txtHeader.setText(text);
    }


    /**
     * Opens the login in a new stage.
     */
    private void showLoginWindow() {
        try {
            showLoginWindow(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a login page in a specified stage (window)
     */
    public void showLoginWindow(Stage loginStage) throws IOException {
        //Close current window.
        if (stage.isShowing()) stage.close();

        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("/Views/userLogin.fxml"));

        Parent loginRoot = loginLoader.load();
        Scene loginScene = new Scene(loginRoot);


        loginStage.setTitle("Allegro");
        loginStage.setScene(loginScene);


        loginStage.setOnCloseRequest(event -> {
            System.exit(0);
            event.consume();
        });

        loginStage.setMinWidth(600);
        Double initialHeight = loginStage.getHeight();
        loginStage.setMinHeight(initialHeight);

        loginStage.show();
        UserLoginController userLoginController = loginLoader.getController();
        userLoginController.setEnv(env);
        userLoginController.displayRecentUsers();

    }

    @FXML
    public void logOutUser(){
        try {
            stage.close();
            showLoginWindow();
        }catch(Exception e){

        }


    }




    /**
     * Displays a dialog to ask the user whether or not they want to save project changes.
     *
     * @return a boolean - true for save, false for cancel
     */
    public Boolean saveChangesDialog() {
        if (!env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().isSaved()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Unsaved project changes");

            ButtonType btnSaveProject = new ButtonType("Save");

            ButtonType btnNo = new ButtonType("Don't Save");
            ButtonType btnCancel = new ButtonType("Cancel");

            alert.getButtonTypes().setAll(btnSaveProject, btnNo, btnCancel);

            String contentString = "Unsaved project properties\n\nSave changes to current project?";


            alert.setContentText(contentString);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == btnSaveProject) {
                checkProjectDirectory();
                env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().saveCurrentProject();

            } else if (result.get() == btnCancel) {
                return false;
            }


        } else if (env.getTranscriptManager().unsavedChanges) {

            env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().saveCurrentProject();
        }
        return true;
    }

    /**
     * Removes all content from the transcript
     */
    @FXML
    public void clearTranscript() {
        env.getTranscriptManager().setBackupTranscript(env.getTranscriptManager().getTranscriptTuples());
        env.getEditManager().addToHistory("3", new ArrayList<String>());
        env.getTranscriptManager().setTranscriptContent(new ArrayList<OutputTuple>());

        transcriptPaneController.setTranscriptPane(env.getTranscriptManager().convertToText());

        env.getTranscriptManager().unsavedChanges = true;
    }


    /**
     * Used to save the transcript to a destination determined by the user, using a filechooser.
     */
    @FXML
    private void saveTranscript() {

        File file = generateSaveFileChooser();

        if (file != null) {
            fileDir = file.getParentFile();
            path = file.getAbsolutePath();
            env.getTranscriptManager().save(path);
        }
    }

    /**
     * Used to save only the commands to a destination determined by user
     */
    @FXML
    private void saveCommands() {
        File file = generateSaveFileChooser();

        if (file != null) {
            fileDir = file.getParentFile();
            fileDir = Paths.get(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getCurrentProjectPath()).toFile();
            path = file.getAbsolutePath();
            env.getTranscriptManager().saveCommandsOnly(path);
        }

    }

    /**
     * Will be used to undo commands in the transcript
     */
    @FXML
    private void undo() {
        transcriptPaneController.executeAndPrintToTranscript("undo");
    }

    /**
     * Will be used to redo commands in the transcript
     */
    @FXML
    private void redo() {
        transcriptPaneController.executeAndPrintToTranscript("redo");
    }

    /**
     * Creates and displays a "save file" file chooser
     *
     * @return The file which the user selects
     */
    private File generateSaveFileChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter textFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(textFilter);

        checkProjectDirectory();
        fileDir = Paths.get(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getCurrentProjectPath()).toFile();

        fileChooser.setInitialDirectory(fileDir);
        File file = fileChooser.showSaveDialog(stage);
        return file;
    }

    public Stage getStage() {
        return this.stage;
    }

    /**
     * Creates and displays an "open file" file chooser
     *
     * @return The file the user selects
     */
    private File generateOpenFileChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter textFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(textFilter);
        checkProjectDirectory();
        fileDir = Paths.get(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().getCurrentProjectPath()).toFile();

        fileChooser.setInitialDirectory(fileDir);

        File file = fileChooser.showOpenDialog(stage);
        return file;
    }

    /**
     * Opens a transcript that has been previously saved.
     */
    @FXML
    private void importTranscript() {
        File file = generateOpenFileChooser();

        if (file != null) {
            fileDir = file.getParentFile();
            path = file.getAbsolutePath();
            try {
                env.getTranscriptManager().open(path);
                transcriptPaneController.setTranscriptPane(env.getTranscriptManager().convertToText());
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("This file is not valid");
                alert.showAndWait();
                System.err.println("Not a valid file");
            }

        }
    }

    /**
     * Opens a file of commands only.
     */
    @FXML
    public void importCommands() {
        File file = generateOpenFileChooser();

        if (file != null) {
            fileDir = file.getParentFile();
            path = file.getAbsolutePath();
            try {
                ArrayList<String> commands = env.getTranscriptManager().loadCommands(path);
                transcriptPaneController.beginPlaybackMode(commands);
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("This file is not valid");
                alert.showAndWait();
                System.err.println("Not a valid file");
            }
        }
    }


    public void setTranscriptPaneText(String text) {
        transcriptPaneController.setTranscriptPane(text);
    }


    public void checkProjectDirectory() {
        Path path = Paths.get("UserData/Projects/");
        if (!Files.isDirectory(path)) {
            try {
                Files.createDirectories(path);

            } catch (IOException e) {
                //Failed to create the directory.
                e.printStackTrace();
            }


        }

    }

    /**
     * Displays a dialog for the user to create a new project
     */
    @FXML
    public void newProject() {
        env.resetProjectEnvironment();
        env.getUserHandler().getCurrentUser().getProjectHandler().createNewProject();
    }

    /**
     * Saves project information
     */
    @FXML
    private void saveProject() {
        env.getUserHandler().getCurrentUser().saveProperties();
        env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().saveCurrentProject();
    }

    @FXML
    private void bindOpenObjects() {

        JSONArray projects = env.getUserHandler().getCurrentUser().getProjectHandler().getProjectList();
        menuOpenProjects.getItems().clear();
        MenuItem selectItem = new MenuItem("Select Project");
        selectItem.setOnAction(event -> {
            if (saveChangesDialog()) selectProjectDirectory();
        });
        SeparatorMenuItem divider = new SeparatorMenuItem();
        divider.setText("Recent Projects..");
        selectItem.acceleratorProperty().setValue(KeyCombination.keyCombination("Shortcut+O"));
        menuOpenProjects.getItems().add(selectItem);
        menuOpenProjects.getItems().add(divider);

        for (int i = projects.size() - 1; i >= 0; i--) {
            final String projectName = projects.get(i).toString();

            MenuItem projectItem = new MenuItem(projectName);
            projectItem.setOnAction(event -> {
                if (saveChangesDialog())
                    env.getUserHandler().getCurrentUser().getProjectHandler().setCurrentProject(projectName);
            });

            menuOpenProjects.getItems().add(projectItem); //Add to Open projects menu
            if ((projects.size() - 1) - i >= 5) break; //Only show the 5 latest projects.

        }

    }


    /**
     * Displays an error message
     *
     * @param errorMessage The message to be displayed
     */
    public void errorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(errorMessage);
        alert.showAndWait();

    }


    /**
     * Open Project browser.
     */
    private void selectProjectDirectory() {
        DirectoryChooser dirChooser = new DirectoryChooser();

        dirChooser.setTitle("Select a project directory");
        Path path = Paths.get(env.getUserHandler().getCurrentUserPath().toString() + "/Projects/");
        checkProjectDirectory();
        dirChooser.setInitialDirectory(path.toFile());


        File folder = dirChooser.showDialog(stage);

        if (folder != null) {
            if (folder.isDirectory() && folder.getParent().equals(env.getUserHandler().getCurrentUser().getUserName())) {
                for (File f : folder.listFiles()) {

                    if (f.getName().endsWith(".json") && f.getName().substring(0, f.getName().length() - 5).equals(folder.getName())) {

                        if (!new File("userData/Projects/" + folder.getName()).isDirectory()) {

                            try {

                                //Copy all files from inside the projects folder.
                                FileHandler.copyFolder(folder, Paths.get(path.toString() + "/" + folder.getName()).toFile());
                            } catch (Exception ce) {
                                ce.printStackTrace();
                                errorAlert("Could not Import the project! Maybe it already exists in the Projects folder?");
                            }
                            //project with said name does not exist in the projects directory.. import it.
                            env.getUserHandler().getCurrentUser().getProjectHandler().setCurrentProject(folder.getName());

                        }

                        env.getUserHandler().getCurrentUser().getProjectHandler().setCurrentProject(folder.getName());
                        return;
                    }
                }
                errorAlert("Not a valid Project folder - Try again!");
                selectProjectDirectory();
                return;
            } else {

                errorAlert("Project must be contained in the current user's Projects folder.");
            }
        }
    }


    /**
     * Sets the stage for root
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setOnCloseRequest(event -> {
            closeApplication();
            event.consume();
        });
    }

    /**
     * Connects the GUI components with the logic environment
     */
    public void setEnvironment(Environment env) {
        this.env = env;
        this.env.setRootController(this);
        tm = env.getTranscriptManager();
        tutorFactory = new TutorFactory(env, centerPane);

        transcriptPaneController.setEnv(this.env);
        keyboardPaneController.create(this.env);


    }

    /**
     * sets the title of the application to the text input
     */
    public void setWindowTitle(String text) {
        this.stage.setTitle("Allegro - " + text);
    }


    public Environment getEnv() {
        return env;
    }


    public TranscriptPaneController getTranscriptController() {
        return transcriptPaneController;
    }


    @FXML
    protected void launchSettings() {
        showUserBar(true);


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/BaseSettings.fxml"));

        try {
            AnchorPane settingsPage = loader.load();
            centerPane.getChildren().setAll(settingsPage);
            AnchorPane.setRightAnchor(settingsPage, 0.0);
            AnchorPane.setLeftAnchor(settingsPage, 0.0);
            AnchorPane.setBottomAnchor(settingsPage, 0.0);
            AnchorPane.setTopAnchor(settingsPage, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        settingsController = loader.getController();
        settingsController.create(env);

    }

    @FXML
    public void toggleTranscript() {
        transcriptSplitPane.setDividerPositions(1.0);
        transcriptPaneController.showTranscript();
    }

    public TutorFactory getTutorFactory() {
        return tutorFactory;
    }

    public BaseSettingsController getBaseSettingsController() {
        return settingsController;
    }

    /**
     * Sets the divider of the main window/transcript split pane.
     *
     * @param position Value from 0-1, which dictates where in the window the splitpane divider will
     *                 be
     */
    public void resizeSplitPane(double position) {
        transcriptSplitPane.setDividerPositions(position);
    }

    public String getHeader() {
        return txtHeader.getText();
    }
}