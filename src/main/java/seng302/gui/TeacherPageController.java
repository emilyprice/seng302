package seng302.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class TeacherPageController {

    @FXML
    private VBox profilepic;

    @FXML
    private JFXListView<?> listView;

    @FXML
    private JFXButton btnSettings;

    @FXML
    private Label txtFullName;

    @FXML
    private ImageView imageDP2;

    @FXML
    private AnchorPane teacherPage;

    @FXML
    private ScrollPane currentPage;

    @FXML
    private SplitPane userView;

    @FXML
    void openSettings(ActionEvent event) {

    }

}
