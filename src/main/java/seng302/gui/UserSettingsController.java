package seng302.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXTextField;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
    private JFXButton btnUploadImage;


    @FXML
    private JFXTextField txtFName;

    @FXML
    private JFXTextField txtLName;
    @FXML
    private JFXButton btnEditFName;

    @FXML
    private JFXButton btnEditLName;

    @FXML
    private JFXButton btnDeleteUser;


    private Environment env;

    private UserHandler userHandler;


    public void create(Environment env) {
        this.env = env;
        this.imageDP.setImage(env.getUserHandler().getCurrentUser().getUserPicture());
        env.getRootController().setHeader("User Settings");
        userHandler = env.getUserHandler();
        try {
            txtFName.setText(userHandler.getCurrentUser().getUserFirstName());
            txtLName.setText(userHandler.getCurrentUser().getUserLastName());
        } catch (Exception e) {
            txtFName.clear();
            txtFName.clear();
        }
    }

    @FXML
    public void initialize() {
        String css = this.getClass().getResource("/css/user_settings.css").toExternalForm();


        ImageView imgUpload = new ImageView(new Image(getClass().getResourceAsStream("/images/file_upload_white_36dp.png"), 25, 25, false, false));


        ImageView imgEdit = new ImageView(new Image(getClass().getResourceAsStream("/images/edit_mode_black_18dp.png"), 18, 18, false, false));


        btnEditFName.setGraphic(imgEdit);
        btnEditLName.setGraphic(new ImageView(imgEdit.getImage()));
        btnUploadImage.setGraphic(imgUpload);

        txtFName.setDisable(true);
        txtFName.setEditable(false);

        txtLName.setDisable(true);
        txtLName.setEditable(false);

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
        Path filePath = Paths.get(userPath.toString() + "/profilePicture");

        try {
            FileHandler.copyFolder(file, filePath.toFile());
            userHandler.getCurrentUser().setUserPicture(filePath);
            imageDP.setImage(userHandler.getCurrentUser().getUserPicture());
            env.getRootController().updateImage();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Action listener for the first name edit/save button.
     */
    @FXML
    private void editFirstName() {
        if (btnEditFName.getText().equals("Edit")) {
            txtFName.setDisable(false);
            txtFName.setEditable(true);
            txtFName.requestFocus();
            btnEditFName.setText("Save");
        } else {
            // Save changes
            txtFName.setDisable(true);
            userHandler.getCurrentUser().setUserFirstName(txtFName.getText());
            userHandler.getCurrentUser().updateProperties();
            userHandler.getCurrentUser().saveProperties();
            txtFName.setEditable(false);
            btnEditFName.setText("Edit");
        }
    }

    /**
     * On click action for the last name edit/save button.
     */
    @FXML
    private void editLastName() {
        if (btnEditLName.getText().equals("Edit")) {
            txtLName.setDisable(false);
            txtLName.setEditable(true);
            txtLName.requestFocus();
            btnEditLName.setText("Save");
        } else {
            // Save changes
            txtLName.setDisable(true);
            userHandler.getCurrentUser().setUserLastName(txtLName.getText());
            userHandler.getCurrentUser().updateProperties();
            userHandler.getCurrentUser().saveProperties();
            txtLName.setEditable(false);
            btnEditLName.setText("Edit");
        }
    }

    /**
     * Shows a delete user confimation dialog, and deletes the current user if suitable.
     */
    @FXML
    private void deleteUser() {

        FXMLLoader popupLoader = new FXMLLoader(getClass().getResource("/Views/PopUpModal.fxml"));
        try {
            BorderPane modal = (BorderPane) popupLoader.load();
            JFXPopup popup = new JFXPopup();
            popup.setContent(modal);

            popup.setPopupContainer(env.getRootController().paneMain);
            popup.setSource(btnDeleteUser);
            popup.show(JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);
            Label header = (Label) modal.lookup("#lblHeader");

            JFXButton btnCancel = (JFXButton) modal.lookup("#btnCancel");
            btnCancel.setOnAction((e) -> popup.close());

            ((JFXButton) modal.lookup("#btnDelete")).
                    setOnAction((event) -> {
                        env.getUserHandler().deleteUser(env.getUserHandler().getCurrentUser().getUserName());
                        popup.close();
                    });


            header.setText("Are you sure you wish to delete user: " + userHandler.getCurrentUser().getUserName());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

