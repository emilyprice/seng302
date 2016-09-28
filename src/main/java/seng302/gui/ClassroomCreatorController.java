package seng302.gui;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import seng302.Environment;

public class ClassroomCreatorController {

    @FXML
    private JFXTextField nameInput;

    @FXML
    private Label errorMessage;

    @FXML
    private AnchorPane dialogBox;

    private Environment env;

    @FXML
    void submitClassroom(ActionEvent event) {

        String newClassroom = nameInput.getText();

        if (isValidClassName(newClassroom)) {
            env.getUserHandler().setClassRoom(newClassroom);
            env.getFirebase().getFirebase().child("classrooms/" + newClassroom + "/users/").setValue("none");
            env.getFirebase().getUserRef().child("classrooms").push().setValue(newClassroom);
            env.getTeacherPageController().updateDisplay();
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } else {
            errorMessage.setVisible(true);
        }

    }

    public void create(Environment env) {
        this.env = env;
        errorMessage.setVisible(false);
        applyTheme();
    }

    private void applyTheme() {
        //Apply user theme
        env.getThemeHandler().setBaseNode(dialogBox);
        String[] themeColours = env.getUserHandler().getCurrentTeacher().getThemeColours();
        env.getThemeHandler().setTheme(themeColours[0], themeColours[1]);
    }

    private boolean isValidClassName(String potentialName) {
        CharSequence[] invalidChars = {".", "#", "$", "[", "]"};

        for (CharSequence invalidChar : invalidChars) {
            if (potentialName.contains(invalidChar)) {
                return false;
            }
        }

        if (potentialName.length() == 0) {
            return false;
        }

        return true;
    }

}
