package seng302.gui;


import org.json.simple.JSONArray;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.PopOver;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import javafx.stage.WindowEvent;
import seng302.Environment;
import seng302.Users.Student;
import seng302.gui.MicrophoneInputPopoverController;
import seng302.managers.TranscriptManager;
import seng302.utility.OutputTuple;


public class RootController implements Initializable {
    Environment env;
    TranscriptManager tm;
    Stage stage;
    public Stage settingsStage;

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
    private Menu menuOpenClassroom;

    @FXML
    private Menu helpMenu;

    @FXML
    private HBox userDPBox;

    @FXML
    private ImageView imageDP;

    @FXML
    private MenuItem dslReferenceMenuItem;

    @FXML
    private RadioMenuItem menuTranscript;

    @FXML
    public void onTranscriptTab() {
        Platform.runLater(() -> transcriptPaneController.txtCommand.requestFocus());
    }

    @FXML
    public void showDslRef() {
        dslRefControl.getPopover().show(paneMain);
    }

    @FXML
    private Menu viewMenu;

    @FXML
    private Menu editMenu;

    @FXML
    private Menu fileMenu;

    @FXML
    private Menu teacherMenu;

    @FXML
    private MenuItem menuMicrophone;

    private DslReferenceController dslRefControl;

    private PopOver microphonePopover;

    public void initialize(URL location, ResourceBundle resources) {
        dslRefControl = new DslReferenceController(transcriptPaneController);

        String cssBordering = "-fx-border-color:dimgray ; \n" //#090a0c
                + "-fx-border-insets:3;\n"
                + "-fx-border-radius:1;\n"
                + "-fx-border-width:2.0";


        if (transcriptPaneController.getIsExpanded()) {
            transcriptPaneController.showTranscript();
            menuTranscript.setSelected(true);
        } else {
            transcriptPaneController.hideTranscript();
            menuTranscript.setSelected(false);
        }

    }


    /**
     * Display or hide the main GUI window.
     *
     * @param show Boolean indicating whether to show or hide the main window.
     */
    public void showWindow(Boolean show) {
        if (show) {

            boolean isTeacher = env.getUserHandler().getCurrentTeacher() != null;

            if (isTeacher) {
                applyTeacherTheme();
                stage.show();
                resizeSplitPane(1.0);
                menuTranscript.setSelected(false);
                toggleTranscript();
                keyboardPaneController.hideWholeKeyboard();
                splitPane.setDividerPosition(0, 1.0);
                try {
                    showTeacherPage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                applyTheme();
                stage.show();
                resizeSplitPane(1.0);
                menuTranscript.setSelected(false);
                toggleTranscript();
                keyboardPaneController.showWholeKeyboard();
                try {
                    showUserPage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
     * Apply the logged in teacher's theme to the main window
     */
    private void applyTeacherTheme() {
        env.getThemeHandler().setBaseNode(paneMain);
        String[] themeColours = env.getUserHandler().getCurrentTeacher().getThemeColours();
        env.getThemeHandler().setTheme(themeColours[0], themeColours[1]);
    }


    /**
     * Closes the application, if There are unsaved changes then it prompts the user to save the
     * file.
     */
    @FXML
    private void closeApplication() {

        showCloseWindow("close");

    }


    /**
     * handles checking whether the project is saved, and shows a dialogue if necessary.
     *
     * @param option close option either 'close' or 'logout'.
     */
    protected void showCloseWindow(String option) {
        if (env.getUserHandler().getCurrentUser() != null && !((Student) env.getUserHandler().getCurrentUser()).getProjectHandler().getCurrentProject().isSaved()) {

            String closeText = option.equals("close") ? "Quit" : "Logout";

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Unsaved changes");

            ButtonType btnSaveProject = new ButtonType("Save project");


            ButtonType btnQuit = new ButtonType(closeText);
            ButtonType btnCancel = new ButtonType("Cancel");

            alert.getButtonTypes().setAll(btnSaveProject, btnQuit, btnCancel);

            String contentString = "Unsaved Project properties\n\nAre you sure that you would like to quit?";

            alert.setContentText(contentString);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.get() == btnSaveProject) {
                ((Student) env.getUserHandler().getCurrentUser()).getProjectHandler().getCurrentProject().saveCurrentProject();
                if (option.equals("close")) {
                    System.exit(0);
                } else if (option.equals("logout")) {
                    logOutUser();
                }

            } else if (result.get() == btnQuit) {
                if (option.equals("close")) {
                    System.exit(0);
                } else if (option.equals("logout")) {
                    logOutUser();
                }
            }


        } else if (env.getTranscriptManager().unsavedChanges) {
            ( env.getUserHandler().getCurrentUser()).getProjectHandler().getCurrentProject().saveCurrentProject();

            if (option.equals("close")) System.exit(0);
            else if (option.equals("logout")) logOutUser();

        } else {

            if (option.equals("close")) System.exit(0);
            else if (option.equals("logout")) logOutUser();
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

        viewMenu.setVisible(true);
        editMenu.setVisible(true);
        fileMenu.setVisible(true);
        teacherMenu.setVisible(false);


    }

    public void showTeacherPage() throws IOException {
        showUserBar(false);
        setHeader("Summary");

        FXMLLoader teacherLoader = new FXMLLoader();
        teacherLoader.setLocation(getClass().getResource("/Views/TeacherPage.fxml"));


        AnchorPane teacherPage = teacherLoader.load();

        centerPane.getChildren().add(teacherPage);

        AnchorPane.setRightAnchor(teacherPage, 0.0);
        AnchorPane.setLeftAnchor(teacherPage, 0.0);
        AnchorPane.setBottomAnchor(teacherPage, 0.0);
        AnchorPane.setTopAnchor(teacherPage, 0.0);

        TeacherPageController teacherPageController = teacherLoader.getController();
        teacherPageController.setEnvironment(env);
        teacherPageController.load();

        viewMenu.setVisible(false);
        editMenu.setVisible(false);
        fileMenu.setVisible(false);
        teacherMenu.setVisible(true);

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


        loginStage.show();
        UserLoginController userLoginController = loginLoader.getController();
        userLoginController.setEnv(env);

    }

    /**
     * User dropdown logout option.
     */
    @FXML
    private void logoutButtonClick() {
        showCloseWindow("logout");
    }

    /**
     * Handles logging out the user showing the login screen.
     */
    public void logOutUser() {
        try {
            env.resetEnvironment();
            stage.close();
            showLoginWindow();
            settingsController.closeWindow();


        } catch (Exception e) {

        }

    }


    /**
     * Displays a dialog to ask the user whether or not they want to save project changes.
     *
     * @return a boolean - true for save, false for cancel
     */
    public Boolean saveChangesDialog() {
        if (!((Student) env.getUserHandler().getCurrentUser()).getProjectHandler().getCurrentProject().isSaved()) {
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
                ((Student) env.getUserHandler().getCurrentUser()).getProjectHandler().getCurrentProject().saveCurrentProject();

            } else if (result.get() == btnCancel) {
                return false;
            }


        } else if (env.getTranscriptManager().unsavedChanges) {

            ((Student) env.getUserHandler().getCurrentUser()).getProjectHandler().getCurrentProject().saveCurrentProject();
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

    public KeyboardPaneController getKeyboardPaneController() {
        return this.keyboardPaneController;
    }


    /**
     * Displays a dialog for the user to create a new project
     */
    @FXML
    public void newProject() {
        env.resetProjectEnvironment();
        env.getUserHandler().getCurrentUser().getProjectHandler().createNewProject();
        try {
            showUserPage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches a classroom creator window, that allows the teacher to name and create a classroom.
     */
    @FXML
    public void newClassroom() {
        //launch the clasroom creator
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/ClassroomCreator.fxml"));

        try {
            AnchorPane classCreator = loader.load();

            Stage classroomCreatorStage = new Stage();
            classroomCreatorStage.setTitle("Create New Classroom");
            classroomCreatorStage.setScene(new Scene(classCreator, 400, 200));
            classroomCreatorStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClassroomCreatorController classroomCreatorController = loader.getController();
        classroomCreatorController.create(env);
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

        for (int i = projects.size() - 1; i >= 0; i--) {
            final String projectName = projects.get(i).toString();

            CheckMenuItem projectItem = new CheckMenuItem(projectName);
            projectItem.setOnAction(event -> {
                projectItem.setSelected(true);
                if (saveChangesDialog())
                    env.getUserHandler().getCurrentUser().getProjectHandler().setCurrentProject(projectName);
                try {
                    showUserPage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            if (projectName.equals(env.getUserHandler().getCurrentUser().getProjectHandler().getCurrentProject().projectName)) {
                projectItem.setSelected(true);
            } else {
                projectItem.setSelected(false);
            }

            menuOpenProjects.getItems().add(projectItem); //Add to Open projects menu
        }

    }

    /**
     * Generates a list of menu items, where each menu item is a classroom belonging to the current teacher
     *
     * @param classes A list of classrooms belonging to the current teacher
     */
    public void updateClassroomsList(List<String> classes) {
        menuOpenClassroom.getItems().clear();
        for (String className : classes) {
            MenuItem classMenuItem = new MenuItem(className);
            classMenuItem.setOnAction(event -> {
                env.getUserHandler().setClassRoom(classMenuItem.getText());
                setWindowTitle("Allegro - " + env.getUserHandler().getClassRoom());
                env.getTeacherPageController().updateDisplay();
            });

            menuOpenClassroom.getItems().add(classMenuItem);

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
        this.stage.setTitle(text);
    }

    public String getWindowTitle() {
        return this.stage.getTitle();
    }

    public void addUnsavedChangesIndicator() {
        if (!this.stage.getTitle().contains("*")) {
            this.stage.setTitle(this.stage.getTitle() + "*");
        }
    }

    public void removeUnsavedChangesIndicator() {
        this.stage.setTitle(this.stage.getTitle().replace("*", ""));
    }


    public Environment getEnv() {
        return env;
    }


    public TranscriptPaneController getTranscriptController() {
        return transcriptPaneController;
    }


    @FXML
    protected void launchSettings() {


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/BaseSettings.fxml"));

        try {
            AnchorPane settingsPage = loader.load();

            settingsStage = new Stage();
            settingsStage.setTitle("Settings");
            settingsStage.setScene(new Scene(settingsPage, 1000, 700));
            settingsStage.setMinWidth(605);
            settingsStage.setMinHeight(505);
            settingsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        settingsController = loader.getController();
        settingsController.create(env);

    }

    /**
     * Launches a new window containing all user settings for a teacher.
     */
    @FXML
    public void launchTeacherSettings() {
        showUserBar(true);


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/TeacherSettings.fxml"));

        try {
            AnchorPane settingsPage = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setScene(new Scene(settingsPage, 1000, 700));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        TeacherSettingsController teacherSettingsController = loader.getController();
        teacherSettingsController.create(env);

    }

    @FXML
    public void toggleTranscript() {
        transcriptSplitPane.setDividerPositions(1.0);

        if (menuTranscript.isSelected()) {
            transcriptPaneController.showTranscript();
        } else {
            transcriptPaneController.hideTranscript();
        }
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

    public void disallowTranscript() {
        menuTranscript.setDisable(true);
        menuTranscript.setSelected(false);
    }

    public void disallowMicrophonePopover() {
        try {
            menuMicrophone.setDisable(true);
            microphonePopover.hide();
        } catch (Exception e) {
            menuMicrophone.setDisable(true);
        }
    }

    public void allowTranscript() {
        menuTranscript.setDisable(false);
    }

    public void allowMicrophonePopover() {
        menuMicrophone.setDisable(false);
    }


    /**
     * Adds a single classroom to the menu of available teacher classrooms
     *
     * @param newClassroom The name of the classroom to be added
     */
    public void addClassroomToMenu(String newClassroom) {
        MenuItem classMenuItem = new MenuItem(newClassroom);
        classMenuItem.setOnAction(clickEvent -> {
            env.getUserHandler().setClassRoom(classMenuItem.getText());
            env.getRootController().setWindowTitle("Allegro - " + env.getUserHandler().getClassRoom());
            env.getTeacherPageController().updateDisplay();
        });

        menuOpenClassroom.getItems().add(classMenuItem);

    }

    /**
     * Opens the microphone input popover
     */
    @FXML
    private void openMicrophonePopover() {
        if (microphonePopover == null) {
            microphonePopover = new PopOver();
        }
        microphonePopover.setDetachable(true);
        microphonePopover.setDetached(true);
        microphonePopover.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);

        try {
            FXMLLoader micInputPopLoader = new FXMLLoader();
            micInputPopLoader.setLocation(getClass().getResource("/Views/MicrophoneInputPopover.fxml"));
            Node loadedPane = (Node) micInputPopLoader.load();
            MicrophoneInputPopoverController micInputPopController = micInputPopLoader.getController();
            micInputPopController.create(env);
            microphonePopover.setContentNode(loadedPane);
            microphonePopover.show(paneMain);
            microphonePopover.setTitle("Microphone Input");
            microphonePopover.setOnCloseRequest(event -> {
                env.getMicrophoneInput().addPopover(null);
                try {
                    env.getMicrophoneInput().stopRecording();
                } catch (Exception e) {
                    // mic input was not recording.
                    System.err.println("failed");
                }
            });
            microphonePopover.setOnHidden(event -> {
                try {
                    env.getMicrophoneInput().stopRecording();
                } catch (Exception e) {
                    // mic input was not recording.
                    System.err.println("failed");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

}