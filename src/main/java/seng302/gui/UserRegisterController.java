package seng302.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import seng302.Environment;
import seng302.Users.User;

import java.awt.*;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by jmw280 on 21/07/16.
 */
public class UserRegisterController {

    @FXML
    HBox recentUsersHbox;

    @FXML
    TextField usernameInput;


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





    Environment env;

    public UserRegisterController() {

    }

    public void setEnv(Environment env) {
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


    }

    /**
     * Checks if the user for the given text input already exists
     *
     * @return
     */
    private Boolean checkUserNameExists() {
        if (env.getUserHandler().getUserNames().contains(txtUsername.getText())) {
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
    private Boolean validCredentials() {

        Boolean valid = true;
        //Validating username
        if (txtUsername.getText().length() > 0) {
            if (env.getUserHandler().getUserNames().contains(txtUsername.getText())) {
                //If the User already exists!
                lblValidator.setText("User already exists!");
                valid = !checkUserNameExists();
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

        if (validCredentials()) {
            env.getUserHandler().createUser(txtUsername.getText(), txtPassword.getText());

            //Log in user.
            if (env.getUserHandler().userPassExists(txtUsername.getText(), txtPassword.getText())) {


                env.getUserHandler().setCurrentUser(txtUsername.getText());

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

        try {
            env.getRootController().showLoginWindow(loginStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
