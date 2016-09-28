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

        if (isEmpty(newClassroom)) {
            errorMessage.setText("Classroom name cannot be empty");
            errorMessage.setVisible(true);
        }

        if (containsInvalidCharacters(newClassroom)) {
            errorMessage.setText("Cannot contain '.', '#', '$', '[', or ']'");
            errorMessage.setVisible(true);
        }

        if (alreadyExists(newClassroom)) {
            errorMessage.setText("A classroom by this name already exists.");
            errorMessage.setVisible(true);
        }

        if (!errorMessage.isVisible()) {
            env.getUserHandler().setClassRoom(newClassroom);
            env.getFirebase().getFirebase().child("classrooms/" + newClassroom + "/users/").setValue("none");
            env.getFirebase().getUserRef().child("classrooms").push().setValue(newClassroom);
            env.getTeacherPageController().updateDisplay();
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
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

    public static boolean isEmpty(String potentialName) {
        if (potentialName.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean containsInvalidCharacters(String potentialName) {
        CharSequence[] invalidChars = {".", "#", "$", "[", "]"};

        for (CharSequence invalidChar : invalidChars) {
            if (potentialName.contains(invalidChar)) {
                return true;
            }
        }

        return false;
    }

    private boolean alreadyExists(String potentialName) {
        final boolean[] alreadyExists = {false};
        env.getFirebase().getClassroomsSnapshot().getChildren().forEach(classroom -> {
            if (classroom.getKey().equalsIgnoreCase(potentialName)) {
                alreadyExists[0] = true;
            }
        });

        return alreadyExists[0];
    }

}
