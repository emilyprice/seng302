package seng302.gui;

import com.google.firebase.database.DataSnapshot;
import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import seng302.Environment;

import java.io.IOException;

/**
 * Created by jmw280 on 21/07/16.
 */
public class UserRegisterController {


    @FXML
    private JFXTextField txtUsername;

    @FXML
    private JFXTextField txtClassRoomName;

    @FXML
    private JFXPasswordField txtPasswordConfirm;

    @FXML
    private JFXPasswordField txtPassword;

    @FXML
    private JFXButton btnReturn;

    @FXML
    private JFXButton btnRegister;

    @FXML
    private JFXTextField txtfname;

    @FXML
    private JFXTextField txtlname;

    @FXML
    private Label lblValidator;

    @FXML
    private ToggleGroup accountType;

    @FXML
    private HBox hbClassroom;

    @FXML
    private JFXComboBox cbClassroom;

    @FXML
    public RadioButton studentRadioBtn;


    private Environment env;
    private String classroom;

    public UserRegisterController() {

    }

    public void create(Environment env) {
        this.env = env;
        txtClassRoomName.setVisible(false);
        studentRadioBtn.setSelected(true);

    }


    @FXML
    public void initialize() {


        lblValidator.setVisible(false);

        txtUsername.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (checkUserNameExists()) {

                lblValidator.setText("User already exists!");
                lblValidator.setVisible(true);


            } else {
                lblValidator.setVisible(false);
            }

        });

        btnReturn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/back_32dp.png"))));


        accountType.selectedToggleProperty().addListener((ov, oldVal, newVal) -> {
            if (accountType.getSelectedToggle() != null) {

                String radioButtonText = ((RadioButton)accountType.getSelectedToggle()).getText();
                if(radioButtonText.equals(" Student")){
                    txtClassRoomName.setVisible(false);
                    hbClassroom.setVisible(true);
                    cbClassroom.getItems().clear();
                    for (DataSnapshot classroom : env.getFirebase().getClassroomsSnapshot().getChildren()) {
                        cbClassroom.getItems().add(classroom.getKey());
                    }

                } else {
                    hbClassroom.setVisible(false);
                    txtClassRoomName.setVisible(true);
                }
                hbClassroom.managedProperty().bind(hbClassroom.visibleProperty());
            }
        });


    }

    /**
     * cbClassroom onAction function.
     */
    @FXML
    void classroomSelected() {

        if (cbClassroom.getValue() != null) {
            this.classroom = cbClassroom.getValue().toString();
            env.getUserHandler().setClassRoom(this.classroom);
            env.getUserHandler().populateUsers();
        }

    }


    /**
     * Checks if the user for the given text input already exists
     *
     * @return True if the inputted username already exists, else false.
     */
    private Boolean checkUserNameExists() {


        if (env.getUserHandler().getUserNames().contains(txtUsername.getText())) {

            txtUsername.setFocusColor(javafx.scene.paint.Color.RED);
            txtUsername.requestFocus();

            return true;
        }
        txtUsername.setFocusColor(Paint.valueOf("#4059a9"));
        return false;

    }


    /**
     * Validates credentials (input lengths + validity.
     *
     * @return True if username/passwords are valid, false otherwise
     */
    private Boolean validCredentials(DataSnapshot dss) {

        Boolean valid = true;

        //Validating username
        if (txtUsername.getText().length() > 0) {
            if (dss.child("classrooms/" + this.classroom + "/users/" + txtUsername.getText()).exists()) {
                //If the User already exists!
                lblValidator.setText("User already exists!");

            }
        } else { //username needs to be atleast 1 character.

            txtUsername.setFocusColor(javafx.scene.paint.Color.RED);
            lblValidator.setText("Username must contain at least 1 character.");
            valid = false;
        }

        //Validating password

        if (txtPassword.getText().length() > 0 && txtPasswordConfirm.getText().length() > 0) {
            if (!txtPassword.getText().equals(txtPasswordConfirm.getText())) {
                //Passwords didn't match.
                txtPassword.clear();
                txtPasswordConfirm.clear();

                lblValidator.setText("Entered passwords did not match.");
                valid = false;
            }
        } else {

            txtPassword.clear();
            txtPasswordConfirm.clear();
            txtPassword.requestFocus();
            lblValidator.setText("Password must contain at least 1 character.");
            valid = false;


        }


        return valid;


    }

    @FXML
    public void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            register();
        }
    }

    /**
     * Registers a user and then logs that user in and opens the main window.
     */
    @FXML
    protected void register() {

        String selectedType = ((RadioButton) accountType.getSelectedToggle()).getText();
        hbClassroom.setStyle("-fx-border-color: none;");
        if (selectedType != null) {
            if (selectedType.equals(" Student")) {
                if (cbClassroom.getValue() != null) {
                    env.getUserHandler().setClassRoom(this.classroom);
                    registerUser(env.getFirebase().getClassroomsSnapshot().child(this.classroom + "/users/" + txtUsername.getText()));
                } else {
                    hbClassroom.setStyle("-fx-border-color: red;");

                }
            } else if (selectedType.equals(" Teacher")) {
                String name = txtClassRoomName.getText();
                if (!classRoomExists(name) && !ClassroomCreatorController.containsInvalidCharacters(name) && !ClassroomCreatorController.isEmpty(name)) {
                    env.getUserHandler().createTeacher(txtUsername.getText(), txtPassword.getText());
                    env.getUserHandler().setCurrentTeacher(txtUsername.getText(), txtClassRoomName.getText(), txtPassword.getText());

                    env.getUserHandler().getCurrentTeacher().setUserFirstName(txtfname.getText());
                    env.getUserHandler().getCurrentTeacher().setUserLastName(txtlname.getText());

                    env.getFirebase().getFirebase().child("teachers/" + txtUsername.getText() + "/classrooms").push().setValue(txtClassRoomName.getText());

                    env.getFirebase().getFirebase().child("classrooms/" + txtClassRoomName.getText() + "/users/").setValue("none");

                    Stage stage = (Stage) btnRegister.getScene().getWindow();
                    stage.close();
                    env.getRootController().showWindow(true);
                } else {
                    txtClassRoomName.setStyle("-fx-border-color: red;");
                }


            }
        }


    }

    /**
     * Registers a user, given a firebase snapshot containing the user's classroom and username.
     *
     * @param dataSnapShot snapshot with the address of the user and their classroom.
     */
    private void registerUser(DataSnapshot dataSnapShot) {
        if (validCredentials(dataSnapShot)) {
            env.getThemeHandler().setDefaultTheme();
            env.getUserHandler().createUser(txtUsername.getText(), txtPassword.getText());
            env.getUserHandler().removeCurrentTeacher();

            //Log in user.
            if (env.getUserHandler().userPassExists(txtUsername.getText(), txtPassword.getText())) {

                env.getUserHandler().getCurrentUser().setUserFirstName(txtfname.getText());
                env.getUserHandler().getCurrentUser().setUserLastName(txtlname.getText());
                env.getUserHandler().getCurrentUser().saveProperties();

                ((Stage) btnRegister.getScene().getWindow()).close();
                env.getRootController().showWindow(true);

            }

        } else {
            //Show validation label.
            lblValidator.setVisible(true);

        }
    }


    public boolean classRoomExists(String className) {
        DataSnapshot classroom = env.getFirebase().getClassroomsSnapshot().child(className);
        if (classroom.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Replaces the registration window with a log in window.
     */
    @FXML
    protected void Return() {
        Stage loginStage = (Stage) btnRegister.getScene().getWindow();
        try {
            env.getRootController().showLoginWindow(loginStage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
