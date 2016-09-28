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

    /**
     * Button click function that creates a new classroom if the text entered is a valid classroom name
     *
     * @param event The click event
     */
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
            env.getRootController().addClassroomToMenu(newClassroom);

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }

    }

    public void create(Environment env) {
        this.env = env;
        errorMessage.setVisible(false);
        applyTheme();
    }

    /**
     * Applies the teacher's theme to this GUI element
     */
    private void applyTheme() {
        //Apply user theme
        env.getThemeHandler().setBaseNode(dialogBox);
        String[] themeColours = env.getUserHandler().getCurrentTeacher().getThemeColours();
        env.getThemeHandler().setTheme(themeColours[0], themeColours[1]);
    }

    /**
     * Checks whether or not a potential classroom name is empty
     * @param potentialName The name of a classroom a teacher is attempting to create
     * @return true if the string is empty, false otherwise
     */
    public static boolean isEmpty(String potentialName) {
        if (potentialName.length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks whether or not a potential classroom name contains characters that are illegal in Firebase
     * @param potentialName The name of a classroom a teacher is attempting to create
     * @return true if any of the illegal characters are present in potentialName, false otherwise
     */
    public static boolean containsInvalidCharacters(String potentialName) {
        CharSequence[] invalidChars = {".", "#", "$", "[", "]"};

        for (CharSequence invalidChar : invalidChars) {
            if (potentialName.contains(invalidChar)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks whether or not a potential classroom name already belongs to an existing classroom
     * @param potentialName The name of a classroom a teacher is attempting to create
     * @return true if the given name already belongs to a classroom, false otherwise
     */
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
