package seng302.gui;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import seng302.Environment;
import seng302.Users.UserHandler;
import seng302.utility.FileHandler;


public class UserSettingsController {

    @FXML
    private Button editFirstNameButton;

    @FXML
    private Button editLastNameButton;

    @FXML
    private ImageView imageDP;

    @FXML
    private AnchorPane chordSpellingAnchor;

    @FXML
    private Button uploadPhotoButton;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField firstNameField;

    @FXML
    private JFXButton btnDeleteUser;


    private Environment env;

    private UserHandler userHandler;


    public void create(Environment env) {
        this.env = env;
        this.imageDP.setImage(env.getUserHandler().getCurrentUser().getUserPicture());
        userHandler = env.getUserHandler();
        try {
            firstNameField.setText(userHandler.getCurrentUser().getUserFirstName());
            lastNameField.setText(userHandler.getCurrentUser().getUserLastName());
        } catch (Exception e) {
            firstNameField.clear();
            lastNameField.clear();
        }
    }

    /**
     * Opens a photo chooser.
     */
    @FXML
    private void launchPhotoChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("All Images", "*.*");
        fileChooser.getExtensionFilters().add(imageFilter);
        Stage stage = new Stage();
        File file = fileChooser.showOpenDialog(stage);
        Path userPath = userHandler.getCurrentUserPath();
        Path filePath = Paths.get(userPath.toString()+"/profilePicture");

        try {
            FileHandler.copyFolder(file, filePath.toFile());
            userHandler.getCurrentUser().setUserPicture(new Image(filePath.toUri().toString()));
            imageDP.setImage(userHandler.getCurrentUser().getUserPicture());
            env.getRootController().updateImage();
        }
        catch (Exception e){
            e.printStackTrace();

        }
    }

    @FXML
    private void editFirstName() {
        if (editFirstNameButton.getText().equals("Edit")) {
            firstNameField.setDisable(false);
            firstNameField.setEditable(true);
            firstNameField.requestFocus();
            editFirstNameButton.setText("Save");
        } else {
            // Save changes
            firstNameField.setDisable(true);
            userHandler.getCurrentUser().setUserFirstName(firstNameField.getText());
            userHandler.getCurrentUser().updateProperties();
            userHandler.getCurrentUser().saveProperties();
            firstNameField.setEditable(false);
            editFirstNameButton.setText("Edit");
        }
    }

    @FXML
    private void editLastName() {
        if (editLastNameButton.getText().equals("Edit")) {
            lastNameField.setDisable(false);
            lastNameField.setEditable(true);
            lastNameField.requestFocus();
            editLastNameButton.setText("Save");
        } else {
            // Save changes
            lastNameField.setDisable(true);
            userHandler.getCurrentUser().setUserLastName(lastNameField.getText());
            userHandler.getCurrentUser().updateProperties();
            userHandler.getCurrentUser().saveProperties();
            lastNameField.setEditable(false);
            editLastNameButton.setText("Edit");
        }
    }

    @FXML
    private void deleteUser() {

        env.getUserHandler().deleteUser(env.getUserHandler().getCurrentUser().getUserName());

    }
}