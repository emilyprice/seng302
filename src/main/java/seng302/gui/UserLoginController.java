package seng302.gui;

import com.google.firebase.database.DataSnapshot;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import seng302.Environment;
import seng302.utility.UserImporter;

/**
 * Controller for the user login screen.
 * Displays recent users and contains functionality for signing in and registering.
 */
public class UserLoginController {

    @FXML
    HBox recentUsersHbox;

    @FXML
    JFXTextField usernameInput;

    @FXML
    JFXPasswordField passwordInput;

    @FXML
    JFXButton btnRegister;

    @FXML
    HBox hbClassroom;


    @FXML
    private JFXComboBox ddClassroom;



    @FXML
    JFXButton btnLogin;

    Environment env;
    RequiredFieldValidator passwordValidator;

    ArrayList<RecentUserController> recentUsers = new ArrayList<>();


    public UserLoginController() {

    }

    @FXML
    public void initialize() {

        passwordValidator = new RequiredFieldValidator();

        passwordInput.getValidators().add(passwordValidator);
        passwordInput.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                passwordValidator.setMessage("Password Required");
                passwordInput.validate();
            }

        });

        recentUsersHbox.setVisible(false);
        recentUsersHbox.managedProperty().bind(recentUsersHbox.visibleProperty());

    }

    public void setEnv(Environment env) {
        this.env = env;

        for(DataSnapshot classroom : env.getFirebase().getClassroomsSnapshot().getChildren()){
            ddClassroom.getItems().add(classroom.getKey());
        }

    }


    protected void deselectUsers() {
        for (RecentUserController recentUser : recentUsers) {
            recentUser.deselect();
        }
    }

    protected void onRecentSelect(String username) {
        usernameInput.setPromptText("");
        usernameInput.setText(username);
        passwordInput.clear();
        passwordInput.requestFocus();
    }


    /**
     * Loads all recent users and collects information about them (dp, password etc)
     */
    private Node generateRecentUser(String username, Image image) {

        Node recentUser;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Views/recentUser.fxml"));
        try {
            recentUser = loader.load();
            RecentUserController recentUserController = loader.getController();
            recentUserController.setParentController(this);

            recentUserController.setUsername(username);
            recentUserController.setUserPic(image);

            recentUsers.add(recentUserController);
            return recentUser;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Displays imageBoxs of recent users.
     */
    public void displayRecentUsers() {
        recentUsersHbox.getChildren().clear();
        for (String user : env.getUserHandler().getRecentUserNames()) {
            //name = user.getUserName();
            //Image image = user.getUserPicture();
            try {
                String dpUrl = env.getFirebase().getClassroomsSnapshot().child(env.getUserHandler().getClassRoom() + "/users/" + user + "/properties/profilePicUrl").getValue().toString();
                Image img = new Image(dpUrl);
                recentUsersHbox.getChildren().add(generateRecentUser(user, img));
            } catch (NullPointerException e) {
                System.err.println("The user " + user + " has no profile picture. They may have been deleted from firebase.");
            }


        }



    }


    /**
     * Creates a register scene and opens it.
     */
    @FXML
    protected void register() {

        //if(classroomSelected()){
        FXMLLoader loader1 = new FXMLLoader();
        loader1.setLocation(getClass().getResource("/Views/UserRegistration.fxml"));

        Parent root1 = null;
        try {
            root1 = loader1.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene1 = new Scene(root1);
        scene1.getStylesheets().addAll(this.getClass().getResource("/css/style.css").toExternalForm());

        Stage registerStage = (Stage) btnLogin.getScene().getWindow();

        registerStage.setTitle("Register new user");
        registerStage.setScene(scene1);

        registerStage.setOnCloseRequest(event -> {
            System.exit(0);
            event.consume();
        });

        registerStage.setMinWidth(600);
        Double initialHeight = registerStage.getHeight();
        registerStage.setMinHeight(initialHeight);

        registerStage.show();
        UserRegisterController userRegisterController = loader1.getController();
        userRegisterController.create(env);
       // }


    }
    @FXML
    void onClassroomChange() {
        classroomSelected();
        System.out.println("in onClassroomChange" + ddClassroom.getValue().toString());
        env.getUserHandler().loadRecentUsers();
        displayRecentUsers();

    }



    private Boolean classroomSelected(){
        if(ddClassroom.getValue() != null){


            hbClassroom.setStyle("-fx-border-style: none;-fx-background-color: rgba(255, 255, 255, 1)");

            env.getUserHandler().setClassRoom(ddClassroom.getValue().toString());
            env.getUserHandler().populateUsers();
            recentUsersHbox.setVisible(true);
            recentUsersHbox.managedProperty().bind(recentUsersHbox.visibleProperty());
            return true;
        }
        recentUsersHbox.setVisible(false);
        hbClassroom.setStyle("-fx-border-style: solid;-fx-border-color: red;-fx-background-color: white;");
        recentUsersHbox.managedProperty().bind(recentUsersHbox.visibleProperty());
        return false;
    }



    @FXML
    public void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            logIn();
        }
    }



    @FXML
    protected void logIn() {

        if(classroomSelected()){
            //Classroom dropdown value selected.
            authenticate(env.getFirebase().getClassroomsSnapshot().child(ddClassroom.getValue().toString()));
        }
        else{
            //TODO: Handle having not have selected a classroom.
        }


    }

    @FXML
    void importUser() {
        UserImporter.importUser(this.env, btnLogin.getScene().getWindow());
    }


    private void authenticate(DataSnapshot fbClass) {
        if (fbClass.exists()) {
            DataSnapshot userfb = fbClass.child("/users/" + usernameInput.getText());

            if (userfb.exists()) {
                //User exists
                env.getUserHandler().setClassRoom(ddClassroom.getValue().toString());
                String pass = userfb.child("/properties/password").getValue().toString();

                if (pass.equals(passwordInput.getText())) {
                    env.getUserHandler().setCurrentUser(usernameInput.getText(), ddClassroom.getValue().toString(), passwordInput.getText());

                    Stage stage = (Stage) btnLogin.getScene().getWindow();
                    stage.close();
                    env.getRootController().showWindow(true);
                } else {
                    passwordValidator.setMessage("Invalid password.");
                    passwordInput.clear();
                    passwordInput.validate();
                    passwordInput.requestFocus();
                }
            } else {
                //User doesn't exist
                passwordValidator.setMessage("Invalid username.");
                passwordInput.clear();
                passwordInput.validate();
                usernameInput.requestFocus();
            }

        }
        else{
            System.out.println("classroom doesn't exist");
            //Handle classroom doesn't exist
            passwordValidator.setMessage("Classroom doesn't exist.");
//            txtClassroom.clear();
//            txtClassroom.validate();
//            txtClassroom.requestFocus();
        }



    }



}
