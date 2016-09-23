package seng302.gui;

import com.jfoenix.controls.JFXListView;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import seng302.Environment;

import java.io.IOException;
import java.util.ArrayList;

public class TeacherPageController {

    @FXML
    private VBox profilepic;

    @FXML
    private JFXListView listView;

//    @FXML
//    private JFXButton btnSettings;

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


    private Environment env;


    @FXML
    void openSettings(ActionEvent event) {

    }

    public void setEnvironment(Environment env) {
        this.env = env;
        this.env.setTeacherPageController(this);
    }

    public void load() {
        imageDP2.setImage(env.getUserHandler().getCurrentUser().getUserPicture());
        populateUserOptions();
    }


    public void populateUserOptions() {

        ArrayList<String> options = new ArrayList<>();
        options.add("Summary");
        options.add("Test User");

        listView.getItems().addAll(FXCollections.observableArrayList(options));

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            showPage((String) newValue);
        });


        listView.setMaxWidth(200);
        listView.setMinWidth(200);
        listView.setDepthProperty(1);


//        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            showPage((String) newValue);
//        });

        // Set after the listener so it loads user summary correctly
        listView.getSelectionModel().selectFirst();


    }

    public void showPage(String pageName) {

//        setupTimeSlider();
        if (pageName.equals("Summary")) {
            showSummaryPage();
        } else {
            showUserPage(pageName);
        }


    }

    public void showSummaryPage() {
        env.getRootController().setHeader("Summary");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/ClassSummary.fxml"));

        try {
            VBox summary = loader.load();
            currentPage.setContent(summary);
            AnchorPane.setLeftAnchor(summary, 0.0);
            AnchorPane.setTopAnchor(summary, 0.0);
            AnchorPane.setBottomAnchor(summary, 0.0);
            AnchorPane.setRightAnchor(summary, 0.0);
            ClassSummaryController classSummaryController = loader.getController();
            //statsController = tutorStatsLoader.getController();

            classSummaryController.create(env);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void showUserPage(String userName) {
        env.getRootController().setHeader("Student - " + userName);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/UserSummary.fxml"));

        try {
            VBox summary = loader.load();
            currentPage.setContent(summary);
            AnchorPane.setLeftAnchor(summary, 0.0);
            AnchorPane.setTopAnchor(summary, 0.0);
            AnchorPane.setBottomAnchor(summary, 0.0);
            AnchorPane.setRightAnchor(summary, 0.0);
            UserSummaryController userSummaryController = loader.getController();
            //statsController = tutorStatsLoader.getController();

            //change to be the user that was clicked on
            userSummaryController.create(env, env.getUserHandler().getCurrentUser());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
