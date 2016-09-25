package seng302.gui;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jfoenix.controls.*;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import seng302.Environment;

/**
 * Created by jmw280 on 21/07/16.
 */
public class UserRegisterController {


    @FXML
    private JFXTextField txtUsername;

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





    private Environment env;
    private String classroom;

    public UserRegisterController() {

    }

    public void create(Environment env) {
        this.env = env;



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

        hbClassroom.setVisible(false);
        hbClassroom.managedProperty().bind(hbClassroom.visibleProperty());

        accountType.selectedToggleProperty().addListener((ov, oldVal, newVal) -> {
            if(accountType.getSelectedToggle() != null){
                System.out.println(((JFXRadioButton)accountType.getSelectedToggle()).textProperty());
                String radioButtonText = ((JFXRadioButton)accountType.getSelectedToggle()).getText();
                if(radioButtonText.equals("Student")){
                    hbClassroom.setVisible(true);
                    cbClassroom.getItems().clear();
                    for(DataSnapshot classroom : env.getFirebase().getClassroomsSnapshot().getChildren()){
                        cbClassroom.getItems().add(classroom.getKey());
                    }

                }
                else{
                    hbClassroom.setVisible(false);
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

        if(cbClassroom.getValue() != null){
            this.classroom = cbClassroom.getValue().toString();
            env.getUserHandler().setClassRoom(this.classroom);
            env.getUserHandler().populateUsers();
        }

    }



    /**
     * Checks if the user for the given text input already exists
     *
     * @return
     */
    private Boolean checkUserNameExists() {


        if (env.getUserHandler().getUserNames().contains(txtUsername.getText())) {

            System.out.println("user handle contains txtUsername!!" );

            //If the User already exists!

            txtUsername.setFocusColor(javafx.scene.paint.Color.RED);
            txtUsername.requestFocus();

            return true;
        }
        txtUsername.setFocusColor(Paint.valueOf("#4059a9"));
        return false;

    }


    /**
     * Validates credentials (input lengths + validity.
     * @return True if username/passwords are valid, false otherwise
     */
    private Boolean validCredentials(DataSnapshot dss) {

        Boolean valid = true;

        //Validating username
        if (txtUsername.getText().length() > 0) {
            if (dss.child("classrooms/" + this.classroom  + "/users/" + txtUsername.getText()).exists()) {
                //If the User already exists!
                lblValidator.setText("User already exists!");
                //valid = !checkUserNameExists();
            }
        } else { //username needs to be atleast 1 character.

            // txtUsername.validate();
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

                //txtPassword.validate();
                lblValidator.setText("Entered passwords did not match.");
                valid = false;
            }
        } else {

            txtPassword.clear();
            txtPasswordConfirm.clear();
            //txtPassword.validate();
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

        //DatabaseReference users = env.getFirebase().getFirebase().child("classrooms/"+ this.classroom+ "/users/"+txtUsername.getText());

        String selectedType = ((JFXRadioButton)accountType.getSelectedToggle()).getText();
        hbClassroom.setStyle("-fx-border-color: none;");
        if(selectedType != null){
            if(selectedType.equals("Student")){
                if(cbClassroom.getValue() != null){
                    env.getUserHandler().setClassRoom(this.classroom);
                    validateCredentials(env.getFirebase().getClassroomsSnapshot().child(this.classroom + "/users/"+txtUsername.getText()));
                }
                else{
                    //TODO: Classroom not selected.
                    hbClassroom.setStyle("-fx-border-color: red;");

                }
            }
            else if(selectedType.equals("Teacher")){
                //env.getFirebase().getFirebase().child("teachers/" + txtUsername.getText()).setValue("test");
                env.getUserHandler().createTeacher(txtUsername.getText(), txtPassword.getText());
                //TODO: add action for registering and logging in as a teacher.
            }
        }






    }

    private void validateCredentials(DataSnapshot dataSnapShot){
        if (validCredentials(dataSnapShot)) {
            env.getThemeHandler().setDefaultTheme();
            env.getUserHandler().createUser(txtUsername.getText(), txtPassword.getText());


            //Log in user.
            if (env.getUserHandler().userPassExists(txtUsername.getText(), txtPassword.getText())) {
                //env.getUserHandler().setCurrentUser(txtUsername.getText());


                env.getUserHandler().getCurrentUser().setUserFirstName(txtfname.getText());
                env.getUserHandler().getCurrentUser().setUserLastName(txtlname.getText());

                ((Stage) btnRegister.getScene().getWindow()).close();
                env.getRootController().showWindow(true);

            }

        } else {
            //Show validation label.
            lblValidator.setVisible(true);

        }
    }


    /**
     * Replaces the registration window with a log in window.
     */
    @FXML
    protected void Return() {

        Stage loginStage = (Stage) btnRegister.getScene().getWindow();
        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("/Views/userLogin.fxml"));

        Parent loginRoot = null;
        try {
            loginRoot = loginLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
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





//        try {
//            env.getRootController().showLoginWindow(loginStage);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }





}
