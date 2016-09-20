package seng302.gui;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import seng302.Environment;
import seng302.Users.User;

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
    Label labelError;

    @FXML
    JFXTextField txtClassroom;

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
    }

    public void setEnv(Environment env) {
        this.env = env;
    }


    protected void deselectUsers() {
        for (RecentUserController recentUser : recentUsers) {
            recentUser.deselect();
        }
    }

    protected void onRecentSelect(String username) {
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
        String name;

        for (User user : env.getUserHandler().getRecentUsers()) {
            name = user.getUserName();

            Image image = user.getUserPicture();
            recentUsersHbox.getChildren().add(generateRecentUser(name, image));
        }

    }


    /**
     * Creates a register scene and opens it.
     */
    @FXML
    protected void register() {

        FXMLLoader loader1 = new FXMLLoader();
        loader1.setLocation(getClass().getResource("/Views/UserRegistration.fxml"));

        Parent root1 = null;
        try {
            root1 = loader1.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene1 = new Scene(root1);
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
        userRegisterController.setEnv(env);


    }

    @FXML
    public void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            logIn();
        }
    }


    DataSnapshot ss;

    @FXML
    protected void logIn() {

        authenticate(env.getFirebase().getClassroomsSnapshot().child("group5"));

    }

    private void authenticate(DataSnapshot fbClass) {
        if (fbClass.exists()) {
            DataSnapshot userfb = fbClass.child("/users/" + usernameInput.getText());

            if (userfb.exists()) {
                //User exists
                env.getUserHandler().setClassRoom(txtClassroom.getText());
                String pass = userfb.child("password").getValue().toString();
                System.out.println("oinside authenticate");
                if (pass.equals(passwordInput.getText())) {
                    System.out.println("CORRECT PASSWORD");
                    env.getUserHandler().setCurrentUser(usernameInput.getText(), txtClassroom.getText(), passwordInput.getText());
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
            txtClassroom.clear();
            txtClassroom.validate();
            txtClassroom.requestFocus();
        }



    }



}
